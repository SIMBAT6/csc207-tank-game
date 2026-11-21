package interface_adapter.login;

import use_case.login.LoginInputBoundary;
import use_case.login.LoginInputData;

public final class LoginController {
    private final LoginInputBoundary interactor;

    public LoginController(LoginInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void login(String username, char[] password, int side) {
        interactor.execute(new LoginInputData(username, password, side));
    }

    public void signUp(String username, char[] password, int side) {
        interactor.signUp(new LoginInputData(username, password, side));
    }

    public void continueAsGuest(int side) {
        interactor.continueAsGuest(side);
    }
}
