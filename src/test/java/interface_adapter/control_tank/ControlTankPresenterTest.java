package interface_adapter.control_tank;

import entity.Match;
import org.junit.jupiter.api.Test;
import use_case.control_tank.ControlTankOutputData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControlTankPresenterTest {
    private final ControlTankViewModel viewModel = new ControlTankViewModel();
    private final ControlTankPresenter presenter = new ControlTankPresenter(viewModel);
    private final List<String> events = new ArrayList<>();

    ControlTankPresenterTest() {
        viewModel.addPropertyChangeListener(event -> events.add(event.getPropertyName()));
    }

    private static ControlTankOutputData output(boolean tank2Alive, int winner) {
        return new ControlTankOutputData(
                List.of(new ControlTankOutputData.TankData(1, 100, 120, 0.5, 14, true, true),
                        new ControlTankOutputData.TankData(2, 300, 120, Math.PI, 8, tank2Alive, false)),
                List.of(new ControlTankOutputData.BulletData(1, 3, List.of(new double[] {150, 125}))),
                winner);
    }

    @Test
    void aTickBecomesSprites() {
        presenter.prepareTickView(output(true, Match.NO_WINNER));

        ControlTankState state = viewModel.getState();
        assertFalse(state.isRoundOver());
        ControlTankState.TankSprite tank1 = state.getTanks().get(0);
        assertEquals(1, tank1.getId());
        assertEquals(100, tank1.getX(), 1e-9);
        assertEquals(0.5, tank1.getAngle(), 1e-9);
        assertTrue(tank1.isShielded());
        assertEquals(8, state.getTanks().get(1).getRadius(), 1e-9);
        assertEquals(150, state.getBullets().get(0).getPath().get(0)[0], 1e-9);
        assertEquals(List.of("state"), events);
    }

    @Test
    void roundOverIsAnnouncedOnceWhileTheInteractorKeepsReportingIt() {
        presenter.prepareTickView(output(true, Match.NO_WINNER));
        presenter.prepareRoundOverView(output(false, Match.PLAYER_1));
        presenter.prepareRoundOverView(output(false, Match.PLAYER_1));

        assertTrue(viewModel.getState().isRoundOver());
        assertEquals(Match.PLAYER_1, viewModel.getState().getWinner());
        assertEquals(List.of("state", "state", ControlTankViewModel.ROUND_OVER, "state"), events);
    }

    @Test
    void theNextRoundCanBeAnnouncedAgain() {
        presenter.prepareRoundOverView(output(false, Match.PLAYER_1));
        presenter.prepareTickView(output(true, Match.NO_WINNER));
        presenter.prepareRoundOverView(output(false, Match.DRAW));

        assertEquals(2, events.stream().filter(ControlTankViewModel.ROUND_OVER::equals).count());
        assertEquals(Match.DRAW, viewModel.getState().getWinner());
    }
}
