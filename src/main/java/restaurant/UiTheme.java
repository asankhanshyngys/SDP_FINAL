package restaurant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/** Small Swing-only visual theme; no business logic or external libraries. */
final class UiTheme {
    static final Color BACKGROUND = new Color(242, 246, 242);
    static final Color INK = new Color(35, 65, 49);
    static final Color MUTED = new Color(95, 111, 101);
    static final Color LINE = new Color(213, 223, 215);
    static final Color GREEN = new Color(38, 88, 62);
    static final Color SELECTED = new Color(224, 238, 226);
    static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);

    private UiTheme() { }

    static void section(JPanel panel, String title) {
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(LINE), title,
                TitledBorder.LEADING, TitledBorder.TOP, BODY.deriveFont(Font.BOLD), INK),
            new EmptyBorder(10, 12, 12, 12)));
    }

    static void apply(Container root) {
        UIManager.put("Button.gradient", null);
        UIManager.put("Button.disabledText", MUTED);
        style(root, BACKGROUND);
    }

    private static void style(Component component, Color background) {
        component.setFont(BODY);
        component.setForeground(INK);
        if (component instanceof JPanel panel) {
            // Sections are white; layout containers inherit their parent's surface.
            boolean section = panel.getBorder() instanceof javax.swing.border.CompoundBorder;
            background = section ? Color.WHITE : background;
            panel.setBackground(background);
        } else if (component instanceof JButton button) {
            button.setBackground(SELECTED);
            button.setFocusPainted(true);
            button.setFont(BODY.deriveFont(Font.BOLD));
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE), new EmptyBorder(5, 12, 5, 12)));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else if (component instanceof JCheckBox check) {
            check.setBackground(background);
        } else if (component instanceof JComboBox<?> combo) {
            combo.setBackground(Color.WHITE);
            combo.setBorder(BorderFactory.createLineBorder(LINE));
            return; // Leave the look-and-feel's arrow-button layout intact.
        } else if (component instanceof JList<?> list) {
            list.setBackground(Color.WHITE);
            list.setSelectionBackground(SELECTED);
            list.setSelectionForeground(INK);
            list.setFixedCellHeight(38);
            list.setCellRenderer(new OrderRenderer());
        } else if (component instanceof JTextArea area) {
            area.setBackground(Color.WHITE);
            area.setFont(BODY.deriveFont(15f));
            area.setMargin(new Insets(16, 18, 16, 18));
        } else if (component instanceof JScrollPane scroll) {
            scroll.setBorder(BorderFactory.createLineBorder(LINE));
            scroll.getViewport().setBackground(Color.WHITE);
            Component view = scroll.getViewport().getView();
            if (view != null) style(view, Color.WHITE);
            return;
        } else if (component instanceof JTabbedPane tabs) {
            tabs.setBackground(BACKGROUND);
            tabs.setFont(BODY.deriveFont(Font.BOLD));
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) style(child, background);
        }
    }

    static void primary(JButton button) {
        primaryColors(button);
        button.addPropertyChangeListener("enabled", event -> primaryColors(button));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GREEN), new EmptyBorder(5, 14, 5, 14)));
    }

    private static void primaryColors(JButton button) {
        button.setBackground(button.isEnabled() ? GREEN : SELECTED);
        button.setForeground(button.isEnabled() ? Color.WHITE : MUTED);
    }

    /** Status remains written out; color is an additional cue, never the only cue. */
    private static final class OrderRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                       boolean selected, boolean focused) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focused);
            label.setBorder(BorderFactory.createCompoundBorder(label.getBorder(), new EmptyBorder(6, 10, 6, 8)));
            if (value instanceof Order order) {
                label.setFont(BODY.deriveFont(Font.BOLD));
                if (!selected) {
                    label.setForeground(switch (order.status()) {
                        case "Pending" -> new Color(126, 87, 24);
                        case "Preparing" -> new Color(50, 96, 136);
                        case "Ready" -> GREEN;
                        default -> MUTED;
                    });
                }
            }
            return label;
        }
    }
}
