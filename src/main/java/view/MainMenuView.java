import interface_adapter.start_match.StartMatchController;
import use_case.start_match.MatchFormat;
import use_case.start_match.PresetMaps;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.util.List;
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
 * The main menu and current player names.
 */
public class MainMenuView extends JPanel {
    public static final String VIEW_NAME = "main menu";

    private final JLabel players = new JLabel();
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
        add(Box.createVerticalStrut(24));
        players.setAlignmentX(Component.CENTER_ALIGNMENT);
        showPlayers("Guest 1", "Guest 2");
        add(players);
        add(Box.createVerticalStrut(16));

        for (JButton button : new JButton[] {startButton, logInButton, leaderboardButton}) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(button);
            add(Box.createVerticalStrut(12));
        }

    }

    /** Runs when the Leaderboard button is clicked (View Leaderboard, UC5). */
    public void setLeaderboardAction(Runnable action) {
        leaderboardButton.addActionListener(e -> action.run());
    }

    public void showPlayers(String player1, String player2) {
        players.setText("Player 1: " + player1 + "     Player 2: " + player2);
    }

    public void setLoginAction(Runnable action) {
        logInButton.addActionListener(event -> action.run());
    }

    /** TODO (UC2 lead): replace with a call to the StartMatchController. */
    public void setStartAction(Runnable action) {
        startButton.addActionListener(e -> action.run());
    }
    public void setStartMatchController(StartMatchController controller) {
        List<String> mapIds = PresetMaps.getMapIds();
        String[] choices = new String[mapIds.size() + 1];

        for (int index = 0; index < mapIds.size(); index++) {
            choices[index] = displayName(mapIds.get(index));
        }
        choices[mapIds.size()] = "Random";

        JComboBox<String> maps = new JComboBox<>(choices);
        JComboBox<MatchFormat> formats = new JComboBox<>(MatchFormat.values());

        JPanel selections = new JPanel();

        JLabel mapLabel = new JLabel("Map");
        mapLabel.setLabelFor(maps);
        selections.add(mapLabel);
        selections.add(maps);

        JLabel formatLabel = new JLabel("Format");
        formatLabel.setLabelFor(formats);
        selections.add(formatLabel);
        selections.add(formats);

        selections.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(selections, getComponentZOrder(startButton));

        startButton.addActionListener(event -> {
            int index = maps.getSelectedIndex();
            String mapId = null;

            if (index == mapIds.size()) {
                mapId = PresetMaps.RANDOM;
            } else if (index >= 0) {
                mapId = mapIds.get(index);
            }

            controller.startMatch(mapId, (MatchFormat) formats.getSelectedItem());
        });
    }

    private static String displayName(String mapId) {
        StringBuilder name = new StringBuilder();
        for (String word : mapId.split("-")) {
            if (name.length() > 0) {
                name.append(' ');
            }
            name.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }
        return name.toString();
    }

    public void showStartError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Cannot start match", JOptionPane.ERROR_MESSAGE);
    }
}
