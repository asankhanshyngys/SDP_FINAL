package restaurant;

/** Factory Method: subclasses decide which product the shared operation creates. */
public abstract class MealCreator {
    protected abstract Meal createMeal();

    public final Meal prepare() {
        Meal meal = createMeal();
        if (meal.price() < 0) throw new IllegalStateException("Invalid menu price");
        return meal;
    }

    public static final class PizzaCreator extends MealCreator {
        protected Meal createMeal() { return new Pizza(); }
        public String toString() { return "Margherita pizza - 2,500 KZT"; }
    }
    public static final class BurgerCreator extends MealCreator {
        protected Meal createMeal() { return new Burger(); }
        public String toString() { return "Classic burger - 1,800 KZT"; }
    }
    public static final class DrinkCreator extends MealCreator {
        protected Meal createMeal() { return new Drink(); }
        public String toString() { return "Lemonade - 600 KZT"; }
    }
    private static final class Pizza implements Meal {
        public String description() { return "Margherita pizza"; }
        public int price() { return 2500; }
    }
    private static final class Burger implements Meal {
        public String description() { return "Classic burger"; }
        public int price() { return 1800; }
    }
    private static final class Drink implements Meal {
        public String description() { return "Lemonade"; }
        public int price() { return 600; }
    }
}
