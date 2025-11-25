package co.zw.blexta.checkmate.notification;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import co.zw.blexta.checkmate.common.exception.EmailDeliveryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final String FROM_EMAIL = "info@checkmate.blexta.co.zw";
    private static final String FROM_NAME = "Checkmate Support";
    private static final String PLATFORM_NAME = "Checkmate";
    private static final String BASE_URL = "https://checkmate.blexta.co.zw";

    @Autowired
    private JavaMailSender mailSender;

    private String cachedTemplate;

    @Async("notificationExecutor")
    public void sendAccountOnboardingEmail(String recipientEmail, String name, String tempPassword) {
        EmailBuilder builder = new EmailBuilder()
                .to(recipientEmail)
                .subject("Welcome to " + PLATFORM_NAME)
                .greeting("Hi " + name + ",")
                .message(
                        "Your account has been successfully created on " + PLATFORM_NAME + ". " +
                                "Use the credentials below to log in. For security, change your password after your first login."
                )
                .credentials("Username: " + recipientEmail, "Temporary Password: " + tempPassword)
                .cta("Go to Dashboard", BASE_URL + "/login")
                .footerMessage("Checkmate • Blame With Proof");
        dispatchEmail(builder);
    }

    @Async("notificationExecutor")
    public void sendPasswordChangedNotification(String recipientEmail, String name) {
        EmailBuilder builder = new EmailBuilder()
                .to(recipientEmail)
                .subject("Your Checkmate Password Has Been Updated")
                .greeting("Hi " + name + ",")
                .message(
                        "We wanted to let you know that your password has been successfully changed. " +
                                "If you did not make this change, please contact Checkmate Support immediately."
                )
                .cta("Go to Dashboard", BASE_URL + "/login")
                .footerMessage("Checkmate • Blame With Proof");
        dispatchEmail(builder);
    }


    private void dispatchEmail(EmailBuilder builder) {
        try {
            String htmlTemplate = getTemplate();
            String processed = fillTemplate(htmlTemplate, builder);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            helper.setTo(builder.toEmail);
            helper.setSubject(builder.subject);
            helper.setText(processed, true);

            mailSender.send(message);
            System.out.println("Checkmate email sent to " + builder.toEmail);
        } catch (Exception ex) {
            System.err.println("Failed to send Checkmate email to " + builder.toEmail + ": " + ex.getMessage());
            throw new EmailDeliveryException("Email delivery failed", ex);
        }
    }

    private String getTemplate() throws IOException {
        if (cachedTemplate == null) {
            Path path = new ClassPathResource("templates/checkmate-email-template.html").getFile().toPath();
            cachedTemplate = Files.readString(path, StandardCharsets.UTF_8);
        }
        return cachedTemplate;
    }

    private String fillTemplate(String template, EmailBuilder builder) {
        return template
                .replace("{{EMAIL_TITLE}}", builder.subject)
                .replace("{{GREETING}}", builder.greeting)
                .replace("{{MESSAGE_CONTENT}}", builder.message)
                .replace("{{USERNAME}}", builder.username)
                .replace("{{TEMP_PASSWORD}}", builder.tempPassword)
                .replace("{{CTA_TEXT}}", builder.ctaText != null ? builder.ctaText : "")
                .replace("{{CTA_URL}}", builder.ctaUrl != null ? builder.ctaUrl : "#")
                .replace("{{FOOTER_MESSAGE}}", builder.footerMessage)
                .replace("{{PRIMARY_COLOR}}", "#4FC3F7") // Blexta light blue
                .replace("{{SECONDARY_COLOR}}", "#F1F3F6") // Blexta soft grey
                .replace("{{ICON_URL}}", "https://blexta.co.zw/checkmate-icon.png"); // replace with your official icon
    }

    private static class EmailBuilder {
        private String toEmail;
        private String subject;
        private String greeting;
        private String message;
        private String username;
        private String tempPassword;
        private String ctaText;
        private String ctaUrl;
        private String footerMessage = "Powered by Checkmate.";

        public EmailBuilder to(String email) { this.toEmail = email; return this; }
        public EmailBuilder subject(String subject) { this.subject = subject; return this; }
        public EmailBuilder greeting(String greeting) { this.greeting = greeting; return this; }
        public EmailBuilder message(String message) { this.message = message; return this; }
        public EmailBuilder credentials(String username, String tempPassword) { this.username = username; this.tempPassword = tempPassword; return this; }
        public EmailBuilder cta(String text, String url) { this.ctaText = text; this.ctaUrl = url; return this; }
        public EmailBuilder footerMessage(String footer) { this.footerMessage = footer; return this; }
    }
}
