package use_case.control_tank;

import entity.Bullet;
import entity.Match;
import entity.Maze;
import entity.Tank;

import java.util.ArrayList;
import java.util.List;

/**
 * Control Tank (UC3): runs one game tick. The rules live in the entities; this class only
 * runs the steps of a tick in order:
 * <ol>
 *   <li>each tank turns, drives, cools down and fires if asked;</li>
 *   <li>each bullet moves {@code getStepsPerTick()} steps, checking for hits after every step,
 *       then ages by one tick;</li>
 *   <li>expired bullets are removed;</li>
 *   <li>the presenter gets the frame, or the winner once fewer than two tanks are alive.</li>
 * </ol>
 * Scoring the round is End Round's job (UC5), so this class never calls {@code Match.endRound}.
 */
public class ControlTankInteractor implements ControlTankInputBoundary {
    private final ControlTankDataAccessInterface dataAccess;
    private final ControlTankOutputBoundary presenter;

    public ControlTankInteractor(ControlTankDataAccessInterface dataAccess,
                                 ControlTankOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(ControlTankInputData inputData) {
        final Match match = dataAccess.getCurrentMatch();
        if (match == null || match.isRoundOver()) {
            return;
        }
        final List<Tank> tanks = List.of(match.getTank1(), match.getTank2());

        controlTank(match.getTank1(), inputData.getPlayer1(), match);
        controlTank(match.getTank2(), inputData.getPlayer2(), match);
        for (Bullet bullet : match.getBullets()) {
            moveBullet(bullet, tanks, match.getMaze());
        }
        match.getBullets().removeIf(Bullet::isExpired);

        final ControlTankOutputData outputData = toOutputData(match, tanks);
        if (outputData.isRoundOver()) {
            presenter.prepareRoundOverView(outputData);
        } else {
            presenter.prepareTickView(outputData);
        }
    }

    private void controlTank(Tank tank, TankCommand command, Match match) {
        tank.rotate(command.getTurn());
        tank.drive(command.getDrive(), match.getMaze());
        tank.coolDown();
        if (command.isFire() && tank.canFire(activeBullets(match, tank.getId()))) {
            match.getBullets().addAll(tank.fire());
        }
    }

    private static int activeBullets(Match match, int ownerId) {
        int count = 0;
        for (Bullet bullet : match.getBullets()) {
            if (bullet.getOwnerId() == ownerId && !bullet.isExpired()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Moves a bullet step by step so a fast bullet can't skip over a tank. The step count is
     * read again every step: a laser beam that stops on a tank asks for no more steps.
     */
    private static void moveBullet(Bullet bullet, List<Tank> tanks, Maze maze) {
        for (int step = 0; step < bullet.getStepsPerTick() && !bullet.isExpired(); step++) {
            bullet.update(maze);
            for (Tank tank : tanks) {
                if (bullet.hits(tank)) {
                    tank.takeHit();
                    bullet.onHitTank();
                }
            }
        }
        bullet.age();
    }

    private static int winner(Tank tank1, Tank tank2) {
        if (tank1.isAlive() && tank2.isAlive()) {
            return Match.NO_WINNER;
        }
        if (tank1.isAlive()) {
            return Match.PLAYER_1;
        }
        if (tank2.isAlive()) {
            return Match.PLAYER_2;
        }
        return Match.DRAW;
    }

    private static ControlTankOutputData toOutputData(Match match, List<Tank> tanks) {
        final List<ControlTankOutputData.TankData> tankData = new ArrayList<>();
        for (Tank tank : tanks) {
            tankData.add(new ControlTankOutputData.TankData(tank.getId(), tank.getX(), tank.getY(),
                    tank.getAngle(), tank.getRadius(), tank.isAlive(), tank.isShielded()));
        }
        final List<ControlTankOutputData.BulletData> bulletData = new ArrayList<>();
        for (Bullet bullet : match.getBullets()) {
            bulletData.add(new ControlTankOutputData.BulletData(
                    bullet.getOwnerId(), bullet.getRadius(), bullet.getPath()));
        }
        return new ControlTankOutputData(tankData, bulletData, winner(match.getTank1(), match.getTank2()));
    }
}
