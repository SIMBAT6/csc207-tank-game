package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame application = new AppBuilder()
                    .addMainMenuView()
                    .addGameView()
                    .build();
            application.pack();
            application.setLocationRelativeTo(null);
            application.setVisible(true);
        });
    }
}
