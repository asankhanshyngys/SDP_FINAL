package restaurant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Two views subscribe independently to the same order events. */
public final class RestaurantPanel extends JPanel {
    private final OrderService service;
    private final OrderFacade facade;
    private final List<Meal> cart = new ArrayList<>();
    private final JComboBox<MealCreator> menu = new JComboBox<>(new MealCreator[] {
        new MealCreator.PizzaCreator(), new MealCreator.BurgerCreator(), new MealCreator.DrinkCreator()
    });
    private final JCheckBox cheese = new JCheckBox("Cheese +300 KZT");
    private final JCheckBox sauce = new JCheckBox("Sauce +150 KZT");
    private final JComboBox<DiscountStrategy> discounts = new JComboBox<>(new DiscountStrategy[] {
        new DiscountStrategy.None(), new DiscountStrategy.Student(), new DiscountStrategy.Promotion()
    });
    private final DefaultListModel<String> cartModel = new DefaultListModel<>();
    private final JList<String> cartList = new JList<>(cartModel);
    private final JLabel preview = new JLabel();
    private final JLabel totals = new JLabel();
    private final JLabel notice = new JLabel("Build an order to get started.");
    private final DefaultListModel<Order> customerModel = new DefaultListModel<>();
    private final DefaultListModel<Order> kitchenModel = new DefaultListModel<>();
    private final JList<Order> customerList = new JList<>(customerModel);
    private final JList<Order> kitchenList = new JList<>(kitchenModel);
    private final JTextArea customerDetails = detailsArea();
    private final JTextArea kitchenDetails = detailsArea();
    private final JButton advance = new JButton("Advance status");
    private final JButton submit = new JButton("Submit order");
    private final JTabbedPane tabs = new JTabbedPane();

    public RestaurantPanel(OrderService service) {
        super(new BorderLayout(0, 16));
        this.service = service;
        facade = new OrderFacade(service);
        setBorder(new EmptyBorder(20, 24, 20, 24));
        JLabel heading = new JLabel("Table");
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.add(heading, BorderLayout.WEST);
        JPanel introduction = new JPanel(new GridLayout(2, 1, 0, 3));
        introduction.add(new JLabel("Restaurant ordering"));
        introduction.add(new JLabel("Choose your meal, add your extras, and follow your order."));
        header.add(introduction, BorderLayout.CENTER);
        header.setBorder(new EmptyBorder(0, 0, 6, 0));
        add(header, BorderLayout.NORTH);
        tabs.addTab("Place Order", customerTab());
        tabs.addTab("Kitchen", kitchenTab());
        add(tabs, BorderLayout.CENTER);
        add(notice, BorderLayout.SOUTH);
        UiTheme.apply(this);
        heading.setFont(UiTheme.BODY.deriveFont(Font.BOLD, 36f));
        notice.setForeground(UiTheme.MUTED);
        notice.setBorder(new EmptyBorder(6, 0, 0, 0));
        notice.setToolTipText("Orders are stored for this session only and clear when the app closes.");
        preview.setFont(UiTheme.BODY.deriveFont(Font.BOLD));
        totals.setFont(UiTheme.BODY.deriveFont(Font.BOLD));
        UiTheme.primary(submit);
        UiTheme.primary(advance);
        service.subscribe(this::refreshCustomer);
        service.subscribe(this::refreshKitchen);
        menu.addActionListener(e -> {
            boolean food = !(menu.getSelectedItem() instanceof MealCreator.DrinkCreator);
            cheese.setEnabled(food);
            sauce.setEnabled(food);
            if (!food) { cheese.setSelected(false); sauce.setSelected(false); }
            refreshPreview();
        });
        cheese.addActionListener(e -> refreshPreview());
        sauce.addActionListener(e -> refreshPreview());
        discounts.addActionListener(e -> refreshCart());
        customerList.addListSelectionListener(e -> showDetails(customerList, customerDetails));
        kitchenList.addListSelectionListener(e -> {
            showDetails(kitchenList, kitchenDetails);
            Order selected = kitchenList.getSelectedValue();
            advance.setEnabled(selected != null && selected.canAdvance());
        });
        refreshPreview();
        refreshCart();
        for (Order order : service.orders()) { refreshCustomer(order); refreshKitchen(order); }
    }

    private JPanel customerTab() {
        JPanel left = new JPanel(new BorderLayout(0, 12));
        JPanel configuration = new JPanel(new GridLayout(0, 1, 0, 6));
        UiTheme.section(configuration, "1. Choose your meal");
        configuration.add(menu);
        JPanel extras = new JPanel(new GridLayout(1, 2));
        extras.add(cheese);
        extras.add(sauce);
        configuration.add(extras);
        configuration.add(preview);
        JButton add = new JButton("Add item to order");
        add.addActionListener(e -> { cart.add(configuredMeal()); refreshCart(); notice.setText("Item added. Add another or submit your order."); });
        configuration.add(add);
        left.add(configuration, BorderLayout.NORTH);
        JPanel basket = new JPanel(new BorderLayout(0, 8));
        UiTheme.section(basket, "2. Review your order");
        cartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        basket.add(new JScrollPane(cartList), BorderLayout.CENTER);
        JPanel actions = new JPanel(new GridLayout(0, 1, 0, 8));
        JButton remove = new JButton("Remove selected item");
        remove.addActionListener(e -> {
            int index = cartList.getSelectedIndex();
            if (index >= 0) { cart.remove(index); refreshCart(); }
        });
        actions.add(remove);
        actions.add(discounts);
        actions.add(totals);
        actions.add(submit);
        submit.addActionListener(e -> submitCart());
        basket.add(actions, BorderLayout.SOUTH);
        left.add(basket, BorderLayout.CENTER);
        JPanel right = new JPanel(new BorderLayout(0, 12));
        UiTheme.section(right, "3. Track submitted orders");
        customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane history = new JScrollPane(customerList);
        history.setPreferredSize(new Dimension(0, 155));
        right.add(history, BorderLayout.NORTH);
        right.add(new JScrollPane(customerDetails), BorderLayout.CENTER);
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));
        panel.add(left);
        panel.add(right);
        return panel;
    }

    private JPanel kitchenTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));
        panel.add(new JLabel("Pending  >  Preparing  >  Ready  >  Completed"), BorderLayout.NORTH);
        JPanel body = new JPanel(new GridLayout(1, 2, 20, 0));
        kitchenList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        body.add(new JScrollPane(kitchenList));
        body.add(new JScrollPane(kitchenDetails));
        panel.add(body, BorderLayout.CENTER);
        advance.setEnabled(false);
        advance.addActionListener(e -> {
            Order order = kitchenList.getSelectedValue();
            if (order != null) {
                try { service.advance(order.id()); notice.setText("Order #" + order.id() + " is now " + order.status() + "."); }
                catch (IllegalArgumentException | IllegalStateException ex) { showError(ex.getMessage()); }
            }
        });
        panel.add(advance, BorderLayout.SOUTH);
        return panel;
    }

    private Meal configuredMeal() {
        Meal meal = ((MealCreator) menu.getSelectedItem()).prepare();
        if (cheese.isSelected()) meal = new Extra.Cheese(meal);
        if (sauce.isSelected()) meal = new Extra.Sauce(meal);
        return meal;
    }
    private DiscountStrategy selectedDiscount() { return (DiscountStrategy) discounts.getSelectedItem(); }
    private void refreshPreview() {
        Meal meal = configuredMeal();
        preview.setText("Item price: " + money(meal.price()));
    }
    private void refreshCart() {
        cartModel.clear();
        for (Meal meal : cart) cartModel.addElement(meal.description() + " | " + money(meal.price()));
        OrderFacade.Quote quote = facade.quote(cart, selectedDiscount());
        totals.setText("Subtotal " + money(quote.subtotal()) + " - " + money(quote.discount()) + " = " + money(quote.total()));
        submit.setEnabled(!cart.isEmpty());
    }
    private void submitCart() {
        try {
            Order order = facade.placeOrder(cart, selectedDiscount());
            cart.clear();
            refreshCart();
            customerList.setSelectedValue(order, true);
            notice.setText("Order #" + order.id() + " submitted. Open Kitchen to begin preparation.");
        } catch (IllegalArgumentException ex) { showError(ex.getMessage()); }
    }
    private void refreshCustomer(Order changed) {
        refreshOrders(customerModel, customerList, changed);
        showDetails(customerList, customerDetails);
    }
    private void refreshKitchen(Order changed) {
        refreshOrders(kitchenModel, kitchenList, changed);
        showDetails(kitchenList, kitchenDetails);
        Order selected = kitchenList.getSelectedValue();
        advance.setEnabled(selected != null && selected.canAdvance());
    }
    private void refreshOrders(DefaultListModel<Order> model, JList<Order> list, Order changed) {
        Order selected = list.getSelectedValue();
        model.clear();
        for (Order order : service.orders()) model.addElement(order);
        list.setSelectedValue(selected == null ? changed : selected, true);
    }
    private void showDetails(JList<Order> list, JTextArea area) {
        Order order = list.getSelectedValue();
        if (order == null) { area.setText("No order selected."); return; }
        StringBuilder text = new StringBuilder("ORDER #" + order.id() + "\nStatus: " + order.status() + "\n\n");
        for (Order.Item item : order.items()) text.append(item.description()).append("\n  ").append(money(item.price())).append("\n");
        text.append("\nSubtotal: ").append(money(order.subtotal()))
            .append("\nDiscount: ").append(order.discountName()).append("\nSaved: ").append(money(order.discount()))
            .append("\nTOTAL: ").append(money(order.total()));
        area.setText(text.toString());
        area.setCaretPosition(0);
    }
    private void showError(String message) { JOptionPane.showMessageDialog(this, message, "Order issue", JOptionPane.WARNING_MESSAGE); }
    private static String money(int value) { return String.format(java.util.Locale.US, "%,d KZT", value); }
    private static JTextArea detailsArea() {
        JTextArea area = new JTextArea("No order selected.");
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(14, 14, 14, 14));
        area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        return area;
    }
}
