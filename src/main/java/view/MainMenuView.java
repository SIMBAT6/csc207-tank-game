package view;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Font;

/**
 * The main menu. Placeholder until the UC1, UC2 and UC5 leads wire in their controllers.
 */
public class MainMenuView extends JPanel {
    public static final String VIEW_NAME = "main menu";

    private final JButton startButton = new JButton("Start");
    private final JButton logInButton = new JButton("Log In");
    private final JButton leaderboardButton = new JButton("Leaderboard");

    public MainMenuView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(80, 0, 80, 0));

        JLabel title = new JLabel("Ricochet Tanks");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 36f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(40));

        for (JButton button : new JButton[] {startButton, logInButton, leaderboardButton}) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(button);
            add(Box.createVerticalStrut(12));
        }

        // TODO (UC1 lead): open the login view.
        logInButton.setEnabled(false);
        // TODO (UC5 lead): open the leaderboard view.
        leaderboardButton.setEnabled(false);
    }

    /** TODO (UC2 lead): replace with a call to the StartMatchController. */
    public void setStartAction(Runnable action) {
        startButton.addActionListener(e -> action.run());
    }
}
