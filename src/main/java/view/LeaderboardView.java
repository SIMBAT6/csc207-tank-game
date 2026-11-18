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
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The leaderboard screen: every account ranked by wins.
 */
public class LeaderboardView extends JPanel implements PropertyChangeListener {
    public static final String VIEW_NAME = LeaderboardViewModel.VIEW_NAME;
    private static final String[] COLUMNS = {"Rank", "Player", "Wins", "Losses"};

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
        messageLabel.setForeground(new Color(180, 30, 30));
        bottom.add(messageLabel, BorderLayout.NORTH);
        final JPanel buttons = new JPanel();
        buttons.add(refreshButton);
        buttons.add(backButton);
        bottom.add(buttons, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> {
            if (controller != null) {
                controller.execute();
            }
        });
        backButton.addActionListener(e -> backAction.run());
    }

    public void setController(ViewLeaderboardController controller) {
        this.controller = controller;
    }

    public void setBackAction(Runnable backAction) {
        this.backAction = backAction;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final LeaderboardState state = (LeaderboardState) evt.getNewValue();
        tableModel.setRowCount(0);
        for (String[] row : state.getRows()) {
            tableModel.addRow(row);
        }
        if (state.getErrorMessage() != null) {
            messageLabel.setText(state.getErrorMessage());
        }
        else if (state.getRows().isEmpty()) {
            messageLabel.setText("No results recorded yet.");
        }
        else {
            messageLabel.setText(" ");
        }
    }
}
