package restaurant;

/** State: each concrete state defines its permitted successor. */
public interface OrderState {
    String label();
    OrderState next();
    default boolean canAdvance() { return true; }

    final class Pending implements OrderState {
        public String label() { return "Pending"; }
        public OrderState next() { return new Preparing(); }
    }
    final class Preparing implements OrderState {
        public String label() { return "Preparing"; }
        public OrderState next() { return new Ready(); }
    }
    final class Ready implements OrderState {
        public String label() { return "Ready"; }
        public OrderState next() { return new Completed(); }
    }
    final class Completed implements OrderState {
        public String label() { return "Completed"; }
        public boolean canAdvance() { return false; }
        public OrderState next() { throw new IllegalStateException("This order is already completed."); }
    }
}
