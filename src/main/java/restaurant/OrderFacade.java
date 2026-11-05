package restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Facade: UI submits with one call; validation, snapshots and pricing stay here. */
public final class OrderFacade {
    public record Quote(int subtotal, int discount) {
        public int total() { return subtotal - discount; }
    }
    private final OrderService service;
    public OrderFacade(OrderService service) { this.service = Objects.requireNonNull(service); }

    public Quote quote(List<? extends Meal> meals, DiscountStrategy strategy) {
        return calculate(snapshot(meals), strategy);
    }
    private List<Order.Item> snapshot(List<? extends Meal> meals) {
        Objects.requireNonNull(meals, "Order items are required");
        List<Order.Item> items = new ArrayList<>();
        for (Meal meal : meals) {
            Objects.requireNonNull(meal, "Meal is required");
            items.add(new Order.Item(meal.description(), meal.price()));
        }
        return items;
    }
    private Quote calculate(List<Order.Item> items, DiscountStrategy strategy) {
        Objects.requireNonNull(strategy, "Discount is required");
        int subtotal = 0;
        for (Order.Item item : items) subtotal = Math.addExact(subtotal, item.price());
        int discount = strategy.discount(subtotal);
        if (discount < 0 || discount > subtotal) throw new IllegalArgumentException("Invalid discount");
        return new Quote(subtotal, discount);
    }
    public Order placeOrder(List<? extends Meal> meals, DiscountStrategy strategy) {
        List<Order.Item> items = snapshot(meals);
        if (items.isEmpty()) throw new IllegalArgumentException("Add at least one item before submitting.");
        Quote quote = calculate(items, strategy);
        return service.submit(items, strategy.name(), quote.subtotal(), quote.discount());
    }
}
