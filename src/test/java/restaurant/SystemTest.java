package restaurant;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Dependency-free behavioral checks; failures always return a nonzero exit code. */
public final class SystemTest {
    private static int checks;
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    private static void rejects(Runnable operation, Class<? extends Exception> type) {
        try { operation.run(); }
        catch (Exception ex) { check(type.isInstance(ex), "Expected " + type + ", got " + ex); return; }
        throw new AssertionError("Expected rejection: " + type);
    }
    public static void main(String[] args) throws Exception {
        Meal pizza = new MealCreator.PizzaCreator().prepare();
        Meal burger = new MealCreator.BurgerCreator().prepare();
        Meal drink = new MealCreator.DrinkCreator().prepare();
        check(pizza.price() == 2500 && burger.price() == 1800 && drink.price() == 600, "Menu products");
        Meal extra = new Extra.Sauce(new Extra.Cheese(pizza));
        check(extra.price() == 2950 && extra.description().contains("cheese + sauce"), "Stacked decorators");
        OrderService service = new OrderService();
        OrderFacade facade = new OrderFacade(service);
        check(facade.quote(List.of(extra, drink), new DiscountStrategy.None()).total() == 3550, "No discount");
        check(facade.quote(List.of(extra, drink), new DiscountStrategy.Student()).total() == 3195, "Student discount");
        check(facade.quote(List.of(extra, drink), new DiscountStrategy.Promotion()).total() == 3018, "Promotion rounding");
        List<String> customerEvents = new ArrayList<>();
        List<String> kitchenEvents = new ArrayList<>();
        OrderObserver customer = o -> customerEvents.add(o.status());
        service.subscribe(customer);
        service.subscribe(o -> kitchenEvents.add(o.status()));
        rejects(() -> facade.placeOrder(List.of(), new DiscountStrategy.None()), IllegalArgumentException.class);
        check(service.orders().isEmpty() && customerEvents.isEmpty(), "Rejected order has no side effects");
        List<Meal> cart = new ArrayList<>(List.of(extra, drink));
        Order first = facade.placeOrder(cart, new DiscountStrategy.Student());
        cart.clear();
        check(first.items().size() == 2 && first.total() == 3195 && first.id() == 1, "Submitted snapshot");
        rejects(() -> first.items().clear(), UnsupportedOperationException.class);
        rejects(() -> service.orders().clear(), UnsupportedOperationException.class);
        Order second = facade.placeOrder(List.of(burger), new DiscountStrategy.None());
        service.advance(first.id());
        check(first.status().equals("Preparing") && second.status().equals("Pending"), "Independent order states");
        service.advance(first.id());
        check(first.status().equals("Ready"), "Ready state");
        service.advance(first.id());
        check(first.status().equals("Completed") && !first.canAdvance(), "Terminal state");
        check(customerEvents.equals(kitchenEvents) && customerEvents.size() == 5, "Two observers receive every event");
        rejects(() -> service.advance(first.id()), IllegalStateException.class);
        check(customerEvents.size() == 5, "Invalid transition sends no event");
        rejects(() -> service.advance(999), IllegalArgumentException.class);
        service.unsubscribe(customer);
        service.advance(second.id());
        check(customerEvents.size() == 5 && kitchenEvents.size() == 6, "Unsubscribe");
        Meal mutable = new Meal() {
            int reads;
            public String description() { return "External item"; }
            public int price() { return ++reads * 100; }
        };
        Order snapshot = facade.placeOrder(List.of(mutable), new DiscountStrategy.None());
        check(snapshot.total() == snapshot.items().get(0).price(), "Read price once during submission");
        rejects(() -> facade.placeOrder(List.of(drink), new DiscountStrategy() {
            public String name() { return "Invalid"; }
            public int discount(int subtotal) { return subtotal + 1; }
        }), IllegalArgumentException.class);
        SwingUtilities.invokeAndWait(() -> {
            try { uiFlow(); } catch (Exception ex) { throw new RuntimeException(ex); }
        });
        System.out.println("PASS: " + checks + " behavioral checks, including Swing workflow and rendered screenshots.");
    }
    private static void uiFlow() throws Exception {
        OrderService service = new OrderService();
        RestaurantPanel panel = new RestaurantPanel(service);
        panel.setSize(1080, 700);
        check(!button(panel, "Submit order").isEnabled(), "Empty UI order disabled");
        find(panel, JCheckBox.class).get(0).doClick();
        find(panel, JCheckBox.class).get(1).doClick();
        button(panel, "Add item to order").doClick();
        find(panel, JComboBox.class).get(1).setSelectedIndex(1);
        button(panel, "Submit order").doClick();
        check(service.orders().size() == 1 && service.orders().get(0).total() == 2655, "UI configured submission");
        check(!button(panel, "Submit order").isEnabled(), "Cart cleared after submission");
        List<JTextArea> details = find(panel, JTextArea.class);
        check(details.stream().allMatch(t -> t.getText().contains("Pending")), "Both views observe submission");
        render(panel, "docs/screenshots/place-order.png");
        find(panel, JTabbedPane.class).get(0).setSelectedIndex(1);
        button(panel, "Advance status").doClick();
        check(details.stream().allMatch(t -> t.getText().contains("Preparing")), "Both views observe state change");
        render(panel, "docs/screenshots/kitchen.png");
        button(panel, "Advance status").doClick();
        button(panel, "Advance status").doClick();
        check(!button(panel, "Advance status").isEnabled(), "Terminal UI action disabled");
        find(panel, JTabbedPane.class).get(0).setSelectedIndex(0);
        find(panel, JComboBox.class).get(0).setSelectedIndex(2);
        check(find(panel, JCheckBox.class).stream().noneMatch(AbstractButton::isSelected), "Drink resets extras");
        check(find(panel, JCheckBox.class).stream().noneMatch(Component::isEnabled), "Drink disables extras");
        button(panel, "Add item to order").doClick();
        find(panel, JList.class).get(0).setSelectedIndex(0);
        button(panel, "Remove selected item").doClick();
        check(!button(panel, "Submit order").isEnabled(), "Remove last cart item");
        button(panel, "Add item to order").doClick();
        button(panel, "Submit order").doClick();
        check(service.orders().size() == 2 && service.orders().get(0).status().equals("Completed")
            && service.orders().get(1).status().equals("Pending"), "Multiple orders through UI");
        panel.setSize(980, 660);
        render(panel, "docs/screenshots/place-order-minimum.png");
    }
    private static JButton button(Container root, String label) {
        return find(root, JButton.class).stream().filter(b -> b.getText().equals(label)).findFirst().orElseThrow();
    }
    private static <T> List<T> find(Container root, Class<T> type) {
        List<T> matches = new ArrayList<>();
        for (Component component : root.getComponents()) {
            if (type.isInstance(component)) matches.add(type.cast(component));
            if (component instanceof Container child) matches.addAll(find(child, type));
        }
        return matches;
    }
    private static void layout(Container root) {
        root.doLayout();
        for (Component component : root.getComponents()) if (component instanceof Container child) layout(child);
    }
    private static void render(JPanel panel, String path) throws Exception {
        layout(panel);
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        panel.printAll(graphics);
        graphics.dispose();
        File file = new File(path);
        file.getParentFile().mkdirs();
        ImageIO.write(image, "png", file);
    }
}
