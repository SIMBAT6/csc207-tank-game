package view;

import interface_adapter.view_leaderboard.LeaderboardState;
import interface_adapter.view_leaderboard.LeaderboardViewModel;
import interface_adapter.view_leaderboard.ViewLeaderboardController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;

/**
 * The leaderboard screen: every account ranked by wins.
 * Loading calls the account API, so it runs on a background thread (SwingWorker) to keep the window responsive.
 */
public class LeaderboardView extends JPanel implements PropertyChangeListener {
    public static final String VIEW_NAME = LeaderboardViewModel.VIEW_NAME;
    private static final String[] COLUMNS = {"Rank", "Player", "Wins", "Losses"};
    private static final Color ERROR_COLOR = new Color(180, 30, 30);
    private static final Color INFO_COLOR = Color.GRAY;

    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton backButton = new JButton("Back");
    private ViewLeaderboardController controller;
    private Runnable backAction = () -> { };

    public LeaderboardView(LeaderboardViewModel viewModel) {
        viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        final JLabel title = new JLabel("Leaderboard", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        add(title, BorderLayout.NORTH);

        final JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        final JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.add(messageLabel, BorderLayout.NORTH);
        final JPanel buttons = new JPanel();
        buttons.add(refreshButton);
        buttons.add(backButton);
        bottom.add(buttons, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> load());
        backButton.addActionListener(e -> backAction.run());
    }

    public void setController(ViewLeaderboardController controller) {
        this.controller = controller;
    }

    public void setBackAction(Runnable backAction) {
        this.backAction = backAction;
    }

    /** Loads the leaderboard in the background; the table updates when the presenter fires. */
    public void load() {
        if (controller == null) {
            return;
        }
        refreshButton.setEnabled(false);
        showMessage("Loading...", INFO_COLOR);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.execute();
                return null;
            }

            @Override
            protected void done() {
                refreshButton.setEnabled(true);
                try {
                    get();
                }
                catch (InterruptedException | ExecutionException ex) {
                    showMessage("Could not load the leaderboard: " + ex.getCause(), ERROR_COLOR);
                }
            }
        }.execute();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final LeaderboardState state = (LeaderboardState) evt.getNewValue();
        // The presenter fires from the background thread; Swing must only be touched on the event thread.
        SwingUtilities.invokeLater(() -> show(state));
    }

    private void show(LeaderboardState state) {
        tableModel.setRowCount(0);
        for (String[] row : state.getRows()) {
            tableModel.addRow(row);
        }
        if (state.getErrorMessage() != null) {
            showMessage(state.getErrorMessage(), ERROR_COLOR);
        }
        else if (state.getRows().isEmpty()) {
            showMessage("No results recorded yet.", INFO_COLOR);
        }
        else {
            showMessage(" ", INFO_COLOR);
        }
    }

    private void showMessage(String text, Color color) {
        messageLabel.setForeground(color);
        messageLabel.setText(text);
    }
}
