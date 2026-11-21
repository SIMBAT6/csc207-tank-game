package interface_adapter.login;

import use_case.login.LoginOutputBoundary;

import javax.swing.SwingUtilities;

public final class LoginPresenter implements LoginOutputBoundary {
    private final LoginViewModel viewModel;

    public LoginPresenter(LoginViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccess(String username, int side) {
        publish(() -> {
            viewModel.getState().setPlayerName(side, username);
            viewModel.getState().setMessage("Player " + side + " is now " + username + ".");
        });
    }

    @Override
    public void prepareFailure(String message) {
        publish(() -> viewModel.getState().setMessage(message));
    }

    private void publish(Runnable update) {
        Runnable publish = () -> {
            update.run();
            viewModel.firePropertyChanged();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            publish.run();
        } else {
            SwingUtilities.invokeLater(publish);
        }
    }
}
