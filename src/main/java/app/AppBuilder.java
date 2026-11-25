package app;

import data_access.DBPlayerStatsDataAccessObject;
import data_access.FileUserDataAccessObject;
import data_access.InMemoryCurrentPlayerDataAccessObject;
import entity.PowerUpFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.collect_power_up.CollectPowerUpController;
import interface_adapter.collect_power_up.CollectPowerUpPresenter;
import interface_adapter.collect_power_up.PowerUpViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.view_leaderboard.LeaderboardViewModel;
import interface_adapter.view_leaderboard.ViewLeaderboardController;
import interface_adapter.view_leaderboard.ViewLeaderboardPresenter;
import use_case.collect_power_up.CollectPowerUpInteractor;
import use_case.login.LoginInteractor;
import use_case.session.CurrentPlayerDataAccessInterface;
import use_case.view_leaderboard.ViewLeaderboardInteractor;
import view.GameLayer;
import view.GameView;
import view.LeaderboardView;
import view.LoginView;
import view.MainMenuView;
import view.PowerUpPainter;
import view.ViewManager;
import entity.Match;
import interface_adapter.start_match.StartMatchController;
import interface_adapter.start_match.StartMatchPresenter;
import use_case.start_match.StartMatchInteractor;
import view.MazePainter;
import data_access.InMemoryMatchDataAccessObject;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.nio.file.Path;
import java.util.Random;

/**
 * Builds the application: creates each view and wires it to its use case.
 * Each use case lead adds an addXView() / addXUseCase() method here.
 *
 * <p>A use case that runs on the game screen plugs in like this (call after addGameView()):</p>
 * <pre>
 * public AppBuilder addCollectPowerUpUseCase() {
 *     PowerUpViewModel viewModel = new PowerUpViewModel();
 *     CollectPowerUpController controller = new CollectPowerUpController(
 *             new CollectPowerUpInteractor(matchDataAccessObject, new CollectPowerUpPresenter(viewModel), ...));
 *     gameView.addTickAction(() -> controller.execute(gameView.getTickCount()));
 *     gameView.addPainter(GameLayer.ITEMS, g -> powerUpPainter.paint(g, viewModel.getState()));
 *     return this;
 * }
 * </pre>
 * Tick actions run in the order the addXUseCase() methods are called in Main, so Control Tank (UC3)
 * must be added before Collect Power-Up (UC4).
 */
public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    private final DBPlayerStatsDataAccessObject playerStatsDataAccessObject = new DBPlayerStatsDataAccessObject();

    private final CurrentPlayerDataAccessInterface currentPlayers =
            new InMemoryCurrentPlayerDataAccessObject();
    /** The one current match, shared by Start Match (UC2), Collect Power-Up (UC4) and End Round (UC5). */
    private final InMemoryMatchDataAccessObject matchDataAccessObject =
            new InMemoryMatchDataAccessObject();

    private MainMenuView mainMenuView;
    private GameView gameView;
    private LeaderboardViewModel leaderboardViewModel;
    private LeaderboardView leaderboardView;

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

    public AppBuilder addLeaderboardView() {
        leaderboardViewModel = new LeaderboardViewModel();
        leaderboardView = new LeaderboardView(leaderboardViewModel);
        cardPanel.add(leaderboardView, LeaderboardView.VIEW_NAME);
        return this;
    }

    /** View Leaderboard (UC5): main menu button, Refresh and Back on the leaderboard screen. */
    public AppBuilder addViewLeaderboardUseCase() {
        final ViewLeaderboardPresenter presenter = new ViewLeaderboardPresenter(leaderboardViewModel);
        final ViewLeaderboardController controller = new ViewLeaderboardController(
                new ViewLeaderboardInteractor(playerStatsDataAccessObject, presenter));
        leaderboardView.setController(controller);
        leaderboardView.setBackAction(() -> switchTo(MainMenuView.VIEW_NAME));
        mainMenuView.setLeaderboardAction(() -> {
            switchTo(LeaderboardView.VIEW_NAME);
            leaderboardView.load();
        });
        return this;
    }

    /** Collect Power-Up (UC4): runs every tick after Control Tank (UC3); call after addGameView(). */
    public AppBuilder addCollectPowerUpUseCase() {
        final Random random = new Random();
        final PowerUpViewModel viewModel = new PowerUpViewModel();
        final CollectPowerUpController controller = new CollectPowerUpController(
                new CollectPowerUpInteractor(matchDataAccessObject,
                        new CollectPowerUpPresenter(viewModel), new PowerUpFactory(random), random));
        final PowerUpPainter painter = new PowerUpPainter(viewModel);
        gameView.addTickAction(() -> controller.execute(gameView.getTickCount()));
        gameView.addPainter(GameLayer.ITEMS, painter::paintItems);
        gameView.addPainter(GameLayer.OVERLAY, painter::paintStatus);
        return this;
    }

    // TODO (UC5 lead, after UC2 and UC3 merge): addEndRoundUseCase() — build EndRoundInteractor with the
    //  match data access object and playerStatsDataAccessObject, EndRoundPresenter + EndRoundViewModel,
    //  a RoundResultOverlay registered on the game screen with
    //  gameView.addPainter(GameLayer.OVERLAY, g -> roundResultOverlay.paint(g, GameView.WIDTH, GameView.HEIGHT)),
    //  and call EndRoundController.execute(winner) when the round ends.

    public JFrame build() {
        wireLogin();
        // Placeholder navigation until the Start Match use case (UC2) exists.
        wireStartMatch();
        gameView.setBackAction(() -> {
            gameView.stop();
            matchDataAccessObject.setCurrentMatch(null);
            switchTo(MainMenuView.VIEW_NAME);
        });
        gameView.addPainter(GameLayer.OVERLAY,
                g -> g.drawString("Esc: back to menu", 10, GameView.HEIGHT - 10));

        JFrame application = new JFrame("Ricochet Tanks");
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.setResizable(false);
        application.add(cardPanel);

        switchTo(MainMenuView.VIEW_NAME);
        return application;
    }

    private void wireStartMatch() {
        StartMatchPresenter presenter = new StartMatchPresenter(match -> {
            matchDataAccessObject.setCurrentMatch(match);
            switchTo(GameView.VIEW_NAME);
            gameView.start();
        }, mainMenuView::showStartError);

        StartMatchInteractor interactor =
                new StartMatchInteractor(currentPlayers, presenter);
        mainMenuView.setStartMatchController(new StartMatchController(interactor));

        MazePainter painter = new MazePainter();
        gameView.addPainter(GameLayer.MAZE, graphics -> {
            Match match = matchDataAccessObject.getCurrentMatch();
            if (match != null) {
                painter.paint(graphics, match.getMaze());
            }
        });
    }

    private void switchTo(String viewName) {
        viewManagerModel.setState(viewName);
        viewManagerModel.firePropertyChanged();
    }
}
