package use_case.login;

public interface LoginInputBoundary {
    void execute(LoginInputData input);
    void signUp(LoginInputData input);
    void continueAsGuest(int side);
}
