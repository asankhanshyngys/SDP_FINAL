package restaurant;

/** Strategy: return the discount amount, rounded down to whole tenge. */
public interface DiscountStrategy {
    String name();
    int discount(int subtotal);

    final class None implements DiscountStrategy {
        public String name() { return "No discount"; }
        public int discount(int subtotal) { return 0; }
        public String toString() { return name(); }
    }
    final class Student implements DiscountStrategy {
        public String name() { return "Student (10%)"; }
        public int discount(int subtotal) { return (int) ((long) subtotal * 10 / 100); }
        public String toString() { return name(); }
    }
    final class Promotion implements DiscountStrategy {
        public String name() { return "Promotion (15%)"; }
        public int discount(int subtotal) { return (int) ((long) subtotal * 15 / 100); }
        public String toString() { return name(); }
    }
}
