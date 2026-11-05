package restaurant;

/** Observer: subscribers receive both submission and state-change events. */
@FunctionalInterface
public interface OrderObserver {
    void orderChanged(Order order);
}
