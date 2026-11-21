package view;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public final class LoginView extends JPanel {
    private final LoginController controller;
    private final JComboBox<String> side = new JComboBox<>(new String[] {"Player 1", "Player 2"});
    private final JTextField username = new JTextField(18);
    private final JPasswordField password = new JPasswordField(18);
    private final JButton login = new JButton("Log In");
    private final JButton signup = new JButton("Sign Up");
    private final JButton guest = new JButton("Continue as Guest");
    private final JButton back = new JButton("Back");
    private final JLabel status = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel players = new JLabel(" ", SwingConstants.CENTER);

    public LoginView(LoginController controller, LoginViewModel model, Runnable returnToMenu) {
        super(new BorderLayout(0, 20));
        this.controller = controller;
        setBorder(BorderFactory.createEmptyBorder(36, 20, 36, 20));

        add(players, BorderLayout.NORTH);
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(row("Side", side));
        form.add(row("Username", username));
        form.add(row("Password", password));

        JPanel actions = new JPanel(new FlowLayout());
        actions.add(login);
        actions.add(signup);
        actions.add(guest);
        actions.add(back);
        form.add(actions);
        add(form, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        login.addActionListener(event -> submit(false));
        signup.addActionListener(event -> submit(true));
        password.addActionListener(event -> submit(false));
        guest.addActionListener(event -> {
            password.setText("");
            controller.continueAsGuest(side.getSelectedIndex() + 1);
        });
        back.addActionListener(event -> {
            username.setText("");
            password.setText("");
            status.setText(" ");
            returnToMenu.run();
        });
        model.addPropertyChangeListener(event -> render(model.getState()));
        render(model.getState());
    }

    private JPanel row(String labelText, javax.swing.JComponent field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel label = new JLabel(labelText);
        label.setLabelFor(field);
        label.setPreferredSize(new Dimension(76, label.getPreferredSize().height));
        row.add(label);
        row.add(field);
        return row;
    }

    private void render(LoginState state) {
        players.setText("Player 1: " + state.getPlayer1Name() + "     Player 2: " + state.getPlayer2Name());
        status.setText(state.getMessage());
    }

    private void submit(boolean createAccount) {
        String enteredName = username.getText();
        char[] enteredPassword = password.getPassword();
        int selectedSide = side.getSelectedIndex() + 1;
        password.setText("");
        setFormEnabled(false);
        status.setText(createAccount ? "Creating account…" : "Logging in…");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    if (createAccount) {
                        controller.signUp(enteredName, enteredPassword, selectedSide);
                    } else {
                        controller.login(enteredName, enteredPassword, selectedSide);
                    }
                } finally {
                    Arrays.fill(enteredPassword, '\0');
                }
                return null;
            }

            @Override
            protected void done() {
                setFormEnabled(true);
                try {
                    get();
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    status.setText("The account operation was interrupted.");
                } catch (ExecutionException error) {
                    status.setText("The account operation failed.");
                }
            }
        }.execute();
    }

    private void setFormEnabled(boolean enabled) {
        for (javax.swing.JComponent field
                : new javax.swing.JComponent[] {side, username, password, login, signup, guest, back}) {
            field.setEnabled(enabled);
        }
    }
}
