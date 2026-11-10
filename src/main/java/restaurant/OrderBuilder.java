package restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Builder: assemble item snapshots and a pricing policy before creating an Order. */
public final class OrderBuilder {
    public record Totals(int subtotal, int discount) {
        public int total() { return subtotal - discount; }
    }

    private final List<Order.Item> items = new ArrayList<>();
    private DiscountStrategy strategy = new DiscountStrategy.None();

    public OrderBuilder addMeal(Meal meal) {
        Objects.requireNonNull(meal, "Meal is required");
        items.add(new Order.Item(meal.description(), meal.price()));
        return this;
    }

    public OrderBuilder withDiscount(DiscountStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "Discount is required");
        return this;
    }

    public Totals quote() {
        int subtotal = 0;
        for (Order.Item item : items) subtotal = Math.addExact(subtotal, item.price());
        int discount = strategy.discount(subtotal);
        if (discount < 0 || discount > subtotal) throw new IllegalArgumentException("Invalid discount");
        return new Totals(subtotal, discount);
    }

    public Order build(int id) {
        if (id < 1) throw new IllegalArgumentException("Order ID must be positive");
        if (items.isEmpty()) throw new IllegalArgumentException("Add at least one item before submitting.");
        Totals totals = quote();
        return new Order(id, items, strategy.name(), totals.subtotal(), totals.discount());
    }
}
