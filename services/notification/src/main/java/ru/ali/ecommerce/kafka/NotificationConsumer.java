package ru.ali.ecommerce.kafka;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.ali.ecommerce.email.EmailService;
import ru.ali.ecommerce.kafka.order.OrderConfirmation;
import ru.ali.ecommerce.kafka.payment.PaymentConfirmation;
import ru.ali.ecommerce.notification.Notification;
import ru.ali.ecommerce.notification.NotificationRepository;

import java.time.LocalDateTime;

import static ru.ali.ecommerce.notification.NotificationType.ORDER_CONFIRMATION;
import static ru.ali.ecommerce.notification.NotificationType.PAYMENT_CONFIRMATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotification(PaymentConfirmation confirmation) throws MessagingException {
        log.info(String.format("Consuming the message from payment-topic Topic:: %s", confirmation));
        notificationRepository.save(
                Notification.builder()
                        .type(PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(confirmation)
                        .build()
        );

        var customerName = confirmation.customerFirstname() + " " + confirmation.customerLastname();
        emailService.sendPaymentSuccessEmail(
                confirmation.customerEmail(),
                customerName,
                confirmation.amount(),
                confirmation.orderReference()
        );
    }

    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotification(OrderConfirmation confirmation) throws MessagingException {
        log.info(String.format("Consuming the message from order-topic Topic:: %s", confirmation));
        notificationRepository.save(
                Notification.builder()
                        .type(ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(confirmation)
                        .build()
        );

        var customerName = confirmation.customer().firstname() + " " + confirmation.customer().lastname();
        emailService.sendOrderConfirmationEmail(
                confirmation.customer().email(),
                customerName,
                confirmation.totalAmount(),
                confirmation.orderReference(),
                confirmation.products()
        );
    }
}
