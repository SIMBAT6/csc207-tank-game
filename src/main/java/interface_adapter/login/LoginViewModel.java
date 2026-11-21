package interface_adapter.login;

import interface_adapter.ViewModel;

public final class LoginViewModel extends ViewModel<LoginState> {
    public LoginViewModel() {
        super("login");
        setState(new LoginState());
    }
}
