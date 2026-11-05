package restaurant;

/** A configured item. Prices use integer tenge, avoiding floating point money. */
public interface Meal {
    String description();
    int price();
}
