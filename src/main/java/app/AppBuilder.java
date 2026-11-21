package app;

import data_access.DBPlayerStatsDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.view_leaderboard.LeaderboardViewModel;
import interface_adapter.view_leaderboard.ViewLeaderboardController;
import interface_adapter.view_leaderboard.ViewLeaderboardPresenter;
import use_case.view_leaderboard.ViewLeaderboardInteractor;
import view.GameLayer;
import view.GameView;
import view.LeaderboardView;
import view.MainMenuView;
import view.ViewManager;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

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

    // TODO (UC5 lead, after UC2 and UC3 merge): addEndRoundUseCase() — build EndRoundInteractor with the
    //  match data access object and playerStatsDataAccessObject, EndRoundPresenter + EndRoundViewModel,
    //  a RoundResultOverlay registered on the game screen with
    //  gameView.addPainter(GameLayer.OVERLAY, g -> roundResultOverlay.paint(g, GameView.WIDTH, GameView.HEIGHT)),
    //  and call EndRoundController.execute(winner) when the round ends.

    public JFrame build() {
        // Placeholder navigation until the Start Match use case (UC2) exists.
        mainMenuView.setStartAction(() -> {
            switchTo(GameView.VIEW_NAME);
            gameView.start();
        });
        gameView.setBackAction(() -> {
            gameView.stop();
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

    private void switchTo(String viewName) {
        viewManagerModel.setState(viewName);
        viewManagerModel.firePropertyChanged();
    }
}
