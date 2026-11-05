package restaurant;

import javax.swing.*;
import java.awt.*;

/** Brief native-window startup check; closes its own application window afterward. */
public final class DesktopSmokeTest {
    public static void main(String[] args) throws Exception {
        Main.main(new String[0]);
        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = null;
            for (Window window : Window.getWindows())
                if (window instanceof JFrame candidate && candidate.isShowing()) frame = candidate;
            if (frame == null) throw new AssertionError("Main did not show an application window");
            try {
                if (!(frame.getContentPane() instanceof RestaurantPanel))
                    throw new AssertionError("Unexpected application content");
                if (frame.getWidth() != 1080 || frame.getHeight() != 760)
                    throw new AssertionError("Unexpected initial dimensions");
                frame.setSize(frame.getMinimumSize());
                frame.validate();
                if (frame.getContentPane().getWidth() <= 0) throw new AssertionError("Missing layout");
                System.out.println("PASS: Main creates a visible native Swing window and lays out at minimum size.");
            } finally { frame.dispose(); }
        });
    }
}
