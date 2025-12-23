package co.zw.blexta.checkmate.payment.intent;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.zw.blexta.checkmate.common.dto.PaymentIntentRequestDto;
import co.zw.blexta.checkmate.common.dto.PaymentIntentResponseDto;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.company.Company;
import co.zw.blexta.checkmate.company.CompanyRepository;
import co.zw.blexta.checkmate.company.PlanType;
import co.zw.blexta.checkmate.payment.Payment;
import co.zw.blexta.checkmate.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentIntentService {

    private final CompanyRepository companyRepository;
    private final PaymentIntentRepository paymentIntentRepository;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PaymentIntentResponseDto createPaymentIntent(PaymentIntentRequestDto request) throws Exception {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        String clientReference = UUID.randomUUID().toString();

        PaymentIntent intent = PaymentIntent.builder()
                .amount((long) request.getAmount())
                .currency(request.getCurrency())
                .planId(request.getPlanId())
                .status(PaymentIntentStatus.INITIATED)
                .createdBy(company)
                .clientReference(clientReference)
                .build();
        paymentIntentRepository.save(intent);

        String paymeUrl = callClicknPayAPI(intent);

        intent.setPayMeUrl(paymeUrl);
        paymentIntentRepository.save(intent);

        return PaymentIntentResponseDto.builder()
                .clientReference(clientReference)
                .currency(request.getCurrency())
                .paymeURL(paymeUrl)
                .build();
    }

  private String callClicknPayAPI(PaymentIntent intent) throws Exception {
    String url = "https://backendservices.clicknpay.africa:2081/payme/orders";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String body = "{"
            + "\"channel\":\"AUTOMATED\","
            + "\"clientReference\":\"" + intent.getClientReference() + "\","
            + "\"currency\":\"" + intent.getCurrency() + "\","
            + "\"customerCharged\":false,"
            + "\"customerPhoneNumber\":\"0789773911\","
            + "\"description\":\"Payment for Checkmate Subscription\","
            + "\"multiplePayments\":true,"
            + "\"orderYpe\":\"DYNAMIC\","
            + "\"productsList\":[{"
                + "\"description\":\".\","
                + "\"id\":0,"
                + "\"price\":" + intent.getAmount() + ","
                + "\"productName\":\"Checkmate\","
                + "\"quantity\":1"
            + "}],"
            + "\"publicUniqueId\":\"bmyOwbgbxMpmLKCqz\","
            + "\"returnUrl\":\"https://checkmate.blexta.co.zw/payment-response?clientReference=" + intent.getClientReference() + "\""
            + "}";


    HttpEntity<String> request = new HttpEntity<>(body, headers);
    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
    System.out.println(response);

    JsonNode jsonNode = objectMapper.readTree(response.getBody());
    return jsonNode.get("paymeURL").asText();
}

  
    @Override
public PaymentIntentStatus checkPaymentStatus(String clientReference) throws Exception {
    PaymentIntent intent = paymentIntentRepository.findByClientReference(clientReference)
            .orElseThrow(() -> new ResourceNotFoundException("PaymentIntent not found"));

    String url = "https://backendservices.clicknpay.africa:2081/payme/orders/top-paid/" + clientReference;
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    JsonNode jsonNode = objectMapper.readTree(response.getBody());
    String status = jsonNode.get("status").asText();

    if ("SUCCESS".equalsIgnoreCase(status)) {
        intent.setStatus(PaymentIntentStatus.COMPLETED);
        paymentIntentRepository.save(intent);

        Payment payment = Payment.builder()
                .company(intent.getCreatedBy())
                .amount(intent.getAmount())
                .paymentIntent(intent)
                .paymentMethod(jsonNode.path("paymentChannel").asText(null))
                .paymentDate(parseOrderDate(jsonNode.path("orderDate").asText()))
                .build();

        paymentRepository.save(payment);
        upgradeCompanyPlan(intent.getCreatedBy(), intent.getPlanId());

    } else if ("FAILED".equalsIgnoreCase(status)) {
        intent.setStatus(PaymentIntentStatus.FAILED);
        paymentIntentRepository.save(intent);
    }

    return intent.getStatus(); 
}

    private LocalDateTime parseOrderDate(String orderDate) {
        return LocalDateTime.parse(orderDate);
    }
    
    
    private void upgradeCompanyPlan(Company company, String planId) {
        PlanType newPlan;
        switch (planId.toLowerCase()) {
            case "starter":
                newPlan = PlanType.BASIC;
                break;
            case "professional":
                newPlan = PlanType.PRO;
                break;
            case "enterprise":
                newPlan = PlanType.ENTERPRISE; 
                break;
            default:
                newPlan = PlanType.FREE;
        }
        company.setPlan(newPlan);
        companyRepository.save(company);
    }


}
