package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.response.AddressResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.OrderItemResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.OrderResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.ShipmentResponse;
import E_commerce.com.SecureEcommerceApplication.entity.enums.ShipmentStatus;
import com.resend.*;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Service
@Slf4j
public class EmailService {

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.from}")
    private String fromEmail;

    @Value("${app.reset-password.url}")
    private String resetPasswordUrl;

    @Value("${app.url}")
    private String appUrl;

    private Resend resend;

    // ✅ Cache both templates at startup
    private String resetTemplate;
    private String orderTemplate;
    private String shipmentTemplate;
    private String registerTemplate;
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy");


    @PostConstruct
    public void init() {
        this.resend           = new Resend(apiKey);
        this.resetTemplate    = loadTemplate("templates/reset-password-email.html");
        this.orderTemplate    = loadTemplate("templates/order-confirmation-email.html");
        this.shipmentTemplate = loadTemplate("templates/shipment-update-email.html");
        this.registerTemplate = loadTemplate("templates/register-email.html"); // ✅ جديد
        log.info("Email templates loaded successfully");
    }

    // ─────────────────────────────────────────
    // Reset Password
    // ─────────────────────────────────────────
    public void sendResetEmail(String toEmail, String userName, String rawToken) {
        try {
            String resetLink = resetPasswordUrl + "?token=" + rawToken;
            String html = buildResetEmailTemplate(userName, resetLink);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(List.of(toEmail))
                    .subject("Reset your password")
                    .html(html)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Reset email sent. id={} to={}", response.getId(), toEmail);

        } catch (ResendException e) {
            log.error("Failed to send reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send reset email");
        }
    }

    // ─────────────────────────────────────────
    // Order Confirmation
    // ─────────────────────────────────────────
    public void sendOrderConfirmationEmail(String toEmail, String userName, OrderResponse order) {
        try {
            String html = buildOrderConfirmationTemplate(userName, order);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(toEmail)
                    .subject("Your order #" + order.getId() + " is confirmed!")
                    .html(html)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Order confirmation email sent. id={} to={}", response.getId(), toEmail);

        } catch (ResendException e) {
            log.error("Failed to send order confirmation to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send order confirmation email");
        }
    }

    // ─────────────────────────────────────────
    // Builders
    // ─────────────────────────────────────────
    private String buildResetEmailTemplate(String userName, String resetLink) {
        return resetTemplate
                .replace("{{userName}}",  escapeHtml(userName))
                .replace("{{resetLink}}", escapeHtml(resetLink));
    }
    public void sendShipmentUpdateEmail(String toEmail, String userName, ShipmentResponse shipment, Long orderId) {
        try {
            String html = buildShipmentUpdateTemplate(userName, shipment, orderId);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(List.of(toEmail))
                    .subject(resolveShipmentSubject(shipment.getStatus()))
                    .html(html)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Shipment update email sent. id={} to={}", response.getId(), toEmail);

        } catch (ResendException e) {
            log.error("Failed to send shipment update email: message={}", e.getMessage());
            throw new RuntimeException("Failed to send shipment update email: " + e.getMessage(), e);
        }
    }
    public void sendRegisterEmail(String toEmail, String userName) {
        try {
            String html = buildRegisterTemplate(userName);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(List.of(toEmail))
                    .subject("Welcome to YourApp!")
                    .html(html)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Register email sent. id={} to={}", response.getId(), toEmail);

        } catch (ResendException e) {
            log.error("Failed to send register email: {}", e.getMessage());
            throw new RuntimeException("Failed to send register email: " + e.getMessage(), e);
        }
    }

    private String buildRegisterTemplate(String userName) {
        return registerTemplate
                .replace("{{userName}}",  escapeHtml((userName)))
                .replace("{{loginLink}}", appUrl + "/login");
    }
    private String resolveShipmentSubject(ShipmentStatus status) {
        return switch (status) {
            case SHIPPED    -> "Your order is on the way!";
            case DELIVERED  -> "Your order has been delivered!";
            case PREPARING  -> "Your order has been preparing!";
            case RETURNED  -> "Your order has been returned!";
            default         -> "Your shipment status has been updated";
        };
    }

    private String buildShipmentUpdateTemplate(String userName, ShipmentResponse shipment, Long orderId) {
        return shipmentTemplate
                .replace("{{userName}}",          escapeHtml((userName)))
                .replace("{{orderId}}",            orderId.toString())
                .replace("{{status}}",             shipment.getStatus().name())
                .replace("{{carrier}}",            (shipment.getCarrier()))
                .replace("{{trackingNumber}}",     (shipment.getTrackingNumber()))
                .replace("{{estimatedDelivery}}", shipment.getEstimatedDelivery() != null
                        ? shipment.getEstimatedDelivery().format(DATE_FMT) : "TBD")
                .replace("{{shippedAt}}",         shipment.getShippedAt() != null
                        ? shipment.getShippedAt().format(DATE_FMT) : "")
                .replace("{{orderLink}}",         appUrl + "/orders/" + orderId);
    }
    private String buildOrderConfirmationTemplate(String userName, OrderResponse order) {
        AddressResponse addr = order.getAddress();
        String addressLine = addr.getFullName() + "\n" +
                addr.getPhone()    + "\n" +
                addr.getStreet()   + "\n" +
                addr.getCity() + ", " + addr.getZipCode() + "\n" +
                addr.getCountry();

        boolean hasCoupon = order.getCouponCode() != null
                && !order.getCouponCode().isBlank();

        return orderTemplate
                .replace("{{userName}}",       escapeHtml(userName))
                .replace("{{orderId}}",         order.getId().toString())
                .replace("{{status}}",          order.getStatus().name())
                .replace("{{originalPrice}}",   "$" + order.getOriginalPrice())
                .replace("{{discountAmount}}",  "$" + order.getDiscountAmount())
                .replace("{{totalPrice}}",      "$" + order.getTotalPrice())
                .replace("{{createdAt}}",       order.getCreatedAt().format(DATE_FMT))
                .replace("{{addressLine}}",     escapeHtml(addressLine))
                .replace("{{couponCode}}",      hasCoupon ? escapeHtml(order.getCouponCode()) : "")
                .replace("{{#hasCoupon}}",      hasCoupon ? "" : "<!--")
                .replace("{{/hasCoupon}}",      hasCoupon ? "" : "-->")
                .replace("{{orderLink}}",       appUrl + "/orders/" + order.getId());
    }

    // ─────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────
    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Could not load template: " + path, e);
        }
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return HtmlUtils.htmlEscape(input);
    }

}