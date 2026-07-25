package com.domain.order;

import com.domain.Aggregation;
import com.domain.exceptions.DomainException;
import com.domain.validators.Error;
import com.domain.validators.ValidationHandlerDomain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Random;

public class Order extends Aggregation<OrderID> implements Cloneable {

    private BigDecimal orderValue;
    private String channel;
    private String paymentStatus;
    private String orderStatus;
    private String reason;
    private Instant created;
    private Instant updated;

    protected Order(OrderID orderID, BigDecimal orderValue, String channel, String paymentStatus, Instant created, Instant updated) {
        super(orderID);
        this.orderValue = orderValue;
        this.channel = channel;
        this.paymentStatus = paymentStatus;
        this.orderStatus = "PAYMENT_PENDING";
        this.reason = "NO REASON - OK";
        this.created = created;
        this.updated = updated;
    }

    public static Order newOrder(BigDecimal orderValue, String channel, String paymentStatus) {
        final var id = OrderID.uniqueID();
        final var now = Instant.now();
        return new Order(id, orderValue, channel, paymentStatus, now, now);
    }

    public static Order aggregate(OrderID id, BigDecimal orderValue, String channel, String paymentStatus, Instant created, Instant updated) {
        return new Order(id, orderValue, channel, paymentStatus, created, updated);
    }

    public Order update(String orderId, BigDecimal orderValue, String channel, String paymentStatus) {
        this.orderValue = orderValue;
        this.channel = channel;
        this.paymentStatus = paymentStatus;
        this.updated = Instant.now();
        return this;
    }

    public Order check(String orderId, BigDecimal orderValue, String channel, String paymentStatus) {
        this.orderValue = orderValue;
        this.channel = channel;
        this.paymentStatus = paymentStatus;
        this.orderStatus = "PAYMENT_PENDING";
        this.reason = "NO REASON - OK";
        this.updated = Instant.now();
        applyDecisionRulesForOrderPaymentStatus();
        return this;
    }


    public enum CancellationReason {
        SEPARATION_PROBLEM("SEPARATION PROBLEM"),
        RUPTURE("NO STOCK"),
        INSUFFICIENT_BALANCE("INSUFFICIENT BALANCE");

        private final String description;

        CancellationReason(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private void evaluateOrderCancelReason(Random random) {
        CancellationReason[] reasons = CancellationReason.values();
        if (random.nextInt(100) < 20) {
            CancellationReason reason = reasons[random.nextInt(reasons.length)];
            cancelOrder(reason);
        }
    }

    private void cancelOrder(CancellationReason reason) {
        this.orderStatus = "CANCELLED";
        this.reason = reason.getDescription();
        throw new DomainException("Order Cancelled", List.of(new Error("Order Cancelled due to: " + reason.getDescription())));
    }

    private void applyDecisionRulesForOrderPaymentStatus() {
        Random random = new Random();

        if ("SITE".equals(this.channel) || "APP".equals(this.channel)) {
            switch (this.paymentStatus.toUpperCase()) {
                case "FRAUD_CHECK":
                    if (random.nextBoolean()) {
                        this.orderStatus = "PAYMENT_PENDING";
                    } else {
                        if (random.nextInt(100) < 30) {
                            this.orderStatus = "CANCELLED";
                            cancelOrder(CancellationReason.RUPTURE);
                        }
                    }
                    break;
                case "AUTHORIZED":
                    int decision = random.nextInt(3);
                    if (decision == 0) {
                        this.orderStatus = "FRAUD_CHECK_MANUAL";
                        throw new DomainException("Order Check Manual Fraud ",
                                List.of(new Error("Order contains problems to be checked automatically")));
                    } else if (decision == 1) {
                        this.orderStatus = "PAID";
                    } else {
                        evaluateOrderCancelReason(random);
                    }
                    break;
                default:
                    this.orderStatus = "PAYMENT_PENDING";
            }

            if (this.orderValue.compareTo(new BigDecimal("10000")) > 0) {
                if (random.nextInt(100) < 70) {
                    this.orderStatus = "PAID";
                } else {
                    if (random.nextInt(100) < 20) {
                        cancelOrder(CancellationReason.INSUFFICIENT_BALANCE);
                    }
                }
            } else {
                if (random.nextInt(100) < 30) {
                    this.orderStatus = "PAID";
                } else {
                    if (random.nextInt(100) < 10) {
                        cancelOrder(CancellationReason.SEPARATION_PROBLEM);
                    }
                }
            }
        } else {
            this.orderStatus = "PAID";
        }
    }

    @Override
    public void validate(ValidationHandlerDomain handler) {
        new OrderValidator(this, handler).validate();
    }

    public String getChannel() {
        return channel;
    }

    public BigDecimal getOrderValue() {
        return orderValue;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public Instant getCreated() {
        return created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getReason() {
        return reason;
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return null;
    }
}
