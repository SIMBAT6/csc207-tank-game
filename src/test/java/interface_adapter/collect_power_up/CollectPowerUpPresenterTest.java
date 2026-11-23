package interface_adapter.collect_power_up;

import org.junit.jupiter.api.Test;
import use_case.collect_power_up.CollectPowerUpOutputData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectPowerUpPresenterTest {

    @Test
    void formatsIconsHudLabelsAndAimLinesAndNotifiesTheView() {
        PowerUpViewModel viewModel = new PowerUpViewModel();
        int[] changes = {0};
        viewModel.addPropertyChangeListener(event -> changes[0]++);
        CollectPowerUpPresenter presenter = new CollectPowerUpPresenter(viewModel);

        List<double[]> aim = List.of(new double[] {0, 0}, new double[] {50, 0});
        presenter.prepareSuccessView(new CollectPowerUpOutputData(
                List.of(new CollectPowerUpOutputData.MapItem("SHIELD", 75, 125)),
                List.of("SHOTGUN", ""),
                List.of(541, 0),
                List.of(aim, List.of()),
                0));

        PowerUpState state = viewModel.getState();
        assertEquals(1, state.getIcons().size());
        assertEquals("Shield", state.getIcons().get(0).getLabel());
        assertEquals(75, state.getIcons().get(0).getX());
        assertEquals("Shotgun (10s)", state.getHudLabels().get(0), "541 ticks rounds up to 10 s");
        assertEquals("", state.getHudLabels().get(1));
        assertEquals(2, state.getAimPaths().get(0).size());
        assertTrue(state.getAimPaths().get(1).isEmpty());
        assertEquals(1, changes[0]);
    }

    @Test
    void showsJustTheNameWhileAShrunkTankWaitsToGrowBack() {
        PowerUpViewModel viewModel = new PowerUpViewModel();
        new CollectPowerUpPresenter(viewModel).prepareSuccessView(new CollectPowerUpOutputData(
                List.of(), List.of("SHRINK", "LASER"), List.of(0, 60), List.of(List.of(), List.of()), -1));

        assertEquals("Shrink", viewModel.getState().getHudLabels().get(0));
        assertEquals("Laser (1s)", viewModel.getState().getHudLabels().get(1));
    }
}
