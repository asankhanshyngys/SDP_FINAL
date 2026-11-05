package restaurant;

import java.util.List;
import java.util.Objects;

/** Facade: coordinates the builder and order service behind simple UI operations. */
public final class OrderFacade {
    public record Quote(int subtotal, int discount) {
        public int total() { return subtotal - discount; }
    }
    private final OrderService service;
    public OrderFacade(OrderService service) { this.service = Objects.requireNonNull(service); }

    public Quote quote(List<? extends Meal> meals, DiscountStrategy strategy) {
        OrderBuilder.Totals totals = assemble(meals, strategy).quote();
        return new Quote(totals.subtotal(), totals.discount());
    }
    private OrderBuilder assemble(List<? extends Meal> meals, DiscountStrategy strategy) {
        Objects.requireNonNull(meals, "Order items are required");
        OrderBuilder builder = new OrderBuilder().withDiscount(strategy);
        for (Meal meal : meals) builder.addMeal(meal);
        return builder;
    }
    public Order placeOrder(List<? extends Meal> meals, DiscountStrategy strategy) {
        return service.submit(assemble(meals, strategy));
    }
}
