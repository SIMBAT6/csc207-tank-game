package app;

import interface_adapter.ViewManagerModel;
import view.GameLayer;
import view.GameView;
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
