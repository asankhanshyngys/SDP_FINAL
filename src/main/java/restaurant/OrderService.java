package restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** In-memory order repository and Observer publisher. Used on Swing's event thread. */
public final class OrderService {
    private final List<Order> orders = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();
    private int nextId = 1;

    public void subscribe(OrderObserver observer) { observers.add(Objects.requireNonNull(observer)); }
    public void unsubscribe(OrderObserver observer) { observers.remove(observer); }
    public List<Order> orders() { return List.copyOf(orders); }
    Order submit(OrderBuilder builder) {
        Order order = builder.build(nextId);
        nextId++;
        orders.add(order);
        publish(order);
        return order;
    }
    public void advance(int id) {
        Order order = orders.stream().filter(o -> o.id() == id).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        order.advance();
        publish(order);
    }
    private void publish(Order order) {
        for (OrderObserver observer : List.copyOf(observers)) observer.orderChanged(order);
    }
}
