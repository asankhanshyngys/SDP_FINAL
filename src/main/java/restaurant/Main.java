package restaurant;

import javax.swing.*;
import java.awt.*;

public final class Main {
    private Main() { }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("Button.font", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            UIManager.put("Label.font", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            UIManager.put("List.font", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            JFrame frame = new JFrame("TABLE - Restaurant Ordering System");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new RestaurantPanel(new OrderService()));
            frame.setSize(1080, 760);
            frame.setMinimumSize(new Dimension(1000, 720));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
