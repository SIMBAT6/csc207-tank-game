package app;

import data_access.FileUserDataAccessObject;
import data_access.InMemoryCurrentPlayerDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import use_case.login.LoginInteractor;
import use_case.session.CurrentPlayerDataAccessInterface;
import view.GameView;
import view.LoginView;
import view.MainMenuView;
import view.ViewManager;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.nio.file.Path;

/**
 * Builds the application: creates each view and wires it to its use case.
 * Each use case lead adds an addXView() / addXUseCase() method here.
 */
public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();

    private final CurrentPlayerDataAccessInterface currentPlayers =
            new InMemoryCurrentPlayerDataAccessObject();

    private MainMenuView mainMenuView;
    private GameView gameView;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
        new ViewManager(cardPanel, cardLayout, viewManagerModel);
    }

    public AppBuilder addMainMenuView() {
        mainMenuView = new MainMenuView();
        cardPanel.add(mainMenuView, MainMenuView.VIEW_NAME);
        return this;
    }

    public AppBuilder addGameView() {
        gameView = new GameView();
        cardPanel.add(gameView, GameView.VIEW_NAME);
        return this;
    }

    private void wireLogin() {
        Path accountsFile = Path.of(System.getProperty("user.home"),
                ".ricochet-tanks", "accounts.properties");
        LoginViewModel loginModel = new LoginViewModel();
        LoginInteractor loginInteractor = new LoginInteractor(
                new FileUserDataAccessObject(accountsFile), currentPlayers,
                new LoginPresenter(loginModel));
        LoginView loginView = new LoginView(new LoginController(loginInteractor),
                loginModel, () -> switchTo(MainMenuView.VIEW_NAME));
        cardPanel.add(loginView, loginModel.getViewName());
        mainMenuView.setLoginAction(() -> switchTo(loginModel.getViewName()));
        loginModel.addPropertyChangeListener(event -> mainMenuView.showPlayers(
                loginModel.getState().getPlayer1Name(), loginModel.getState().getPlayer2Name()));
    }

    public JFrame build() {
        wireLogin();
        // Placeholder navigation until the Start Match use case (UC2) exists.
        mainMenuView.setStartAction(() -> {
            switchTo(GameView.VIEW_NAME);
            gameView.start();
        });
        gameView.setBackAction(() -> {
            gameView.stop();
            switchTo(MainMenuView.VIEW_NAME);
        });

        JFrame application = new JFrame("Ricochet Tanks");
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.setResizable(false);
        application.add(cardPanel);

        switchTo(MainMenuView.VIEW_NAME);
        return application;
    }

    private void switchTo(String viewName) {
        viewManagerModel.setState(viewName);
        viewManagerModel.firePropertyChanged();
    }
}
