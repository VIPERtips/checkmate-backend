package co.zw.blexta.checkmate.notification;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import co.zw.blexta.checkmate.common.exception.EmailDeliveryException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final String FROM_EMAIL = "mytipstadiwa@gmail.com";
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
                .message("Your account has been created. Below are your credentials.")
                .credentials("Username: " + recipientEmail, "Temporary Password: " + tempPassword)
                .cta("Go To Dashboard", BASE_URL + "/login")
                .footerMessage("Checkmate • Blame With Proof");

        dispatchEmail(builder);
    }

    @Async("notificationExecutor")
    public void sendPasswordChangedNotification(String recipientEmail, String name) {
        EmailBuilder builder = new EmailBuilder()
                .to(recipientEmail)
                .subject("Your Checkmate Password Has Been Updated")
                .greeting("Hi " + name + ",")
                .message("Your password was changed. If it wasn't you, contact support.")
                .cta("Go To Dashboard", BASE_URL)
                .footerMessage("Checkmate • Blame With Proof");

        dispatchEmail(builder);
    }

    @Async("notificationExecutor")
    public void sendReminder(String recipientEmail, String name, String item, String due) {
        EmailBuilder builder = new EmailBuilder()
                .to(recipientEmail)
                .subject("Action Required: Please Return " + item)
                .greeting("Hello " + name + ",")
                .message(
                        "This is a friendly reminder that the device \"" + item +
                                "\" assigned to you is due for return by " + due +
                                ". Kindly ensure it is returned on time to avoid any disruption or follow-up notifications."
                )
                .cta("View Device Details", BASE_URL )
                .footerMessage("Checkmate • Accountability Made Simple");

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

    private String getTemplate() {
        if (cachedTemplate == null) {
            try {
                var stream = getClass().getClassLoader()
                        .getResourceAsStream("templates/checkmate-email-template.html");

                if (stream == null)
                    throw new EmailDeliveryException("Email template not found", null);

                cachedTemplate = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

            } catch (IOException e) {
                throw new EmailDeliveryException("Failed to load email template", e);
            }
        }
        return cachedTemplate;
    }

    private String fillTemplate(String template, EmailBuilder builder) {

        String credentialBlock = builder.credentialBlock != null ? builder.credentialBlock : "";

        return template
                .replace("{{EMAIL_TITLE}}", n(builder.subject))
                .replace("{{GREETING}}", n(builder.greeting))
                .replace("{{MESSAGE_CONTENT}}", n(builder.message))
                .replace("{{CREDENTIAL_BLOCK}}", credentialBlock)
                .replace("{{CTA_TEXT}}", n(builder.ctaText))
                .replace("{{CTA_URL}}", n(builder.ctaUrl))
                .replace("{{FOOTER_MESSAGE}}", n(builder.footerMessage))
                .replace("{{ICON_URL}}", "https://checkmate.blexta.co.zw/favicon.ico");
    }

    private String n(String value) {
        return value != null ? value : "";
    }


    public static class EmailBuilder {
        private String toEmail;
        private String subject;
        private String greeting;
        private String message;
        private String ctaText;
        private String ctaUrl;
        private String footerMessage;
        private String credentialBlock;

        public EmailBuilder to(String email) { this.toEmail = email; return this; }
        public EmailBuilder subject(String subject) { this.subject = subject; return this; }
        public EmailBuilder greeting(String greeting) { this.greeting = greeting; return this; }
        public EmailBuilder message(String msg) { this.message = msg; return this; }
        public EmailBuilder cta(String text, String url) { this.ctaText = text; this.ctaUrl = url; return this; }
        public EmailBuilder footerMessage(String footer) { this.footerMessage = footer; return this; }

        public EmailBuilder credentials(String username, String tempPassword) {
            this.credentialBlock =
                    "<div class=\"credentials\">" +
                            "<div>" + username + "</div>" +
                            "<div>" + tempPassword + "</div>" +
                    "</div>";
            return this;
        }
    }
}
