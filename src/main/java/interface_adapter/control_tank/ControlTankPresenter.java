package interface_adapter.control_tank;

import use_case.control_tank.ControlTankOutputBoundary;
import use_case.control_tank.ControlTankOutputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns each tick's output into sprites for the game screen. The interactor keeps reporting the
 * end of the round until End Round (UC5) scores it, so {@link ControlTankViewModel#ROUND_OVER}
 * is fired only on the first of those ticks.
 */
public final class ControlTankPresenter implements ControlTankOutputBoundary {
    private final ControlTankViewModel viewModel;

    public ControlTankPresenter(ControlTankViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareTickView(ControlTankOutputData outputData) {
        final ControlTankState state = show(outputData);
        state.setRoundOver(false, outputData.getWinner());
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareRoundOverView(ControlTankOutputData outputData) {
        final ControlTankState state = show(outputData);
        final boolean firstTick = !state.isRoundOver();
        state.setRoundOver(true, outputData.getWinner());
        viewModel.firePropertyChanged();
        if (firstTick) {
            viewModel.firePropertyChanged(ControlTankViewModel.ROUND_OVER);
        }
    }

    private ControlTankState show(ControlTankOutputData outputData) {
        final List<ControlTankState.TankSprite> tanks = new ArrayList<>();
        for (ControlTankOutputData.TankData tank : outputData.getTanks()) {
            tanks.add(new ControlTankState.TankSprite(tank.getId(), tank.getX(), tank.getY(),
                    tank.getAngle(), tank.getRadius(), tank.isAlive(), tank.isShielded()));
        }
        final List<ControlTankState.BulletSprite> bullets = new ArrayList<>();
        for (ControlTankOutputData.BulletData bullet : outputData.getBullets()) {
            bullets.add(new ControlTankState.BulletSprite(
                    bullet.getOwnerId(), bullet.getRadius(), bullet.getPath()));
        }
        final ControlTankState state = viewModel.getState();
        state.setTanks(tanks);
        state.setBullets(bullets);
        return state;
    }
}
