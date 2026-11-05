package restaurant;

import java.util.List;

/** Submitted items and prices are snapshots; only lifecycle state changes. */
public final class Order {
    public record Item(String description, int price) {
        public Item {
            if (description == null || description.isBlank() || price < 0)
                throw new IllegalArgumentException("Invalid order item");
        }
    }
    private final int id;
    private final List<Item> items;
    private final String discountName;
    private final int subtotal;
    private final int discount;
    private OrderState state = new OrderState.Pending();

    Order(int id, List<Item> items, String discountName, int subtotal, int discount) {
        this.id = id;
        this.items = List.copyOf(items);
        this.discountName = discountName;
        this.subtotal = subtotal;
        this.discount = discount;
    }
    void advance() { state = state.next(); }
    public int id() { return id; }
    public List<Item> items() { return items; }
    public String discountName() { return discountName; }
    public int subtotal() { return subtotal; }
    public int discount() { return discount; }
    public int total() { return subtotal - discount; }
    public String status() { return state.label(); }
    public boolean canAdvance() { return state.canAdvance(); }
    public String toString() { return "#" + id + " | " + status() + " | " + total() + " KZT"; }
}
