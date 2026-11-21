package use_case.login;

public interface LoginOutputBoundary {
    void prepareSuccess(String username, int side);
    void prepareFailure(String message);
}
