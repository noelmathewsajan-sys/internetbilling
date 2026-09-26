package internetbilling;

import internetbilling.gui.WelcomeFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Application Entry Point for Internet Billing Management System.
 * Initializes Look & Feel and launches the WelcomeFrame.
 */
public class Main {

    public static void main(String[] args) {
        // Configure standard Java Swing Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // Launch the GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            WelcomeFrame welcomeFrame = new WelcomeFrame();
            welcomeFrame.setVisible(true);
        });
    }
}
