package ru.ali.ecommerce.order;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ali.ecommerce.customer.CustomerClient;
import ru.ali.ecommerce.exception.BusinessException;
import ru.ali.ecommerce.kafka.OrderConfirmation;
import ru.ali.ecommerce.kafka.OrderProducer;
import ru.ali.ecommerce.orderline.OrderLineRequest;
import ru.ali.ecommerce.orderline.OrderLineService;
import ru.ali.ecommerce.payment.PaymentClient;
import ru.ali.ecommerce.payment.PaymentRequest;
import ru.ali.ecommerce.product.ProductClient;
import ru.ali.ecommerce.product.PurchaseRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(OrderRequest request) {
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(
                        () -> new BusinessException(
                                "Cannot create order:: No Customer exists with the provided ID"
                                        + request.customerId())
                );

        var purchasedProducts = this.productClient.purchaseProducts(request.products());

        var order = this.orderRepository.save(mapper.toOrder(request));

        for(PurchaseRequest purchaseRequest: request.products())
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity())
            );

        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(mapper::fromOrder).toList();
    }

    public OrderResponse findOrderById(Integer orderId) {
        return orderRepository.findById(orderId).map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Order with ID %d wasn't found", orderId)));
    }
}
