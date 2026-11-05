package restaurant;

import java.util.Objects;

/** Decorator: extras can wrap either a base meal or another extra. */
public abstract class Extra implements Meal {
    protected final Meal wrapped;
    protected Extra(Meal wrapped) { this.wrapped = Objects.requireNonNull(wrapped); }

    public static final class Cheese extends Extra {
        public Cheese(Meal meal) { super(meal); }
        public String description() { return wrapped.description() + " + cheese"; }
        public int price() { return wrapped.price() + 300; }
    }
    public static final class Sauce extends Extra {
        public Sauce(Meal meal) { super(meal); }
        public String description() { return wrapped.description() + " + sauce"; }
        public int price() { return wrapped.price() + 150; }
    }
}
