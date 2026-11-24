package org.owasp.benchmark.demo.bizlogic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory order logic used to demonstrate business-logic findings (no injection sinks involved).
 *
 * <p>Each flaw below is intentional:
 *
 * <ol>
 *   <li>Unit price is supplied by the client and trusted (price tampering).
 *   <li>Quantity is not validated, so negative values produce negative totals.
 *   <li>Orders can be read by id without checking ownership (IDOR).
 *   <li>A coupon can be applied repeatedly, stacking the discount.
 *   <li>Refunds are not capped at the amount paid and can be issued more than once.
 *   <li>Orders can be shipped without having been paid (workflow step skipping).
 * </ol>
 */
public class OrderService {

    public enum Status {
        CREATED,
        PAID,
        SHIPPED,
        REFUNDED
    }

    public static class Order {
        public final long id;
        public final String owner;
        public long totalCents;
        public long paidCents;
        public Status status = Status.CREATED;

        Order(long id, String owner, long totalCents) {
            this.id = id;
            this.owner = owner;
            this.totalCents = totalCents;
        }
    }

    private static final Map<Long, Order> ORDERS = new ConcurrentHashMap<>();
    private static final AtomicLong IDS = new AtomicLong(1000);

    public static Order create(String owner, long unitPriceCents, int quantity) {
        long id = IDS.incrementAndGet();
        Order order = new Order(id, owner, unitPriceCents * quantity);
        ORDERS.put(id, order);
        return order;
    }

    /** Returns any order by id, regardless of who is asking. */
    public static Order find(long id) {
        return ORDERS.get(id);
    }

    public static void applyCoupon(Order order, String code) {
        if ("SAVE20".equals(code)) {
            order.totalCents -= order.totalCents / 5;
        }
    }

    public static void pay(Order order, long amountCents) {
        order.paidCents += amountCents;
        order.status = Status.PAID;
    }

    public static void ship(Order order) {
        order.status = Status.SHIPPED;
    }

    public static long refund(Order order, long amountCents) {
        order.paidCents -= amountCents;
        order.status = Status.REFUNDED;
        return amountCents;
    }
}
