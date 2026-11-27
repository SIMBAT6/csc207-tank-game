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
 *   <li>each tank turns, drives, cools down and fires if asked; new bullets are moved along
 *       the barrel to its tip;</li>
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

        controlTank(match.getTank1(), inputData.getPlayer1(), match, tanks);
        controlTank(match.getTank2(), inputData.getPlayer2(), match, tanks);
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

    private void controlTank(Tank tank, TankCommand command, Match match, List<Tank> tanks) {
        tank.rotate(command.getTurn());
        tank.drive(command.getDrive(), match.getMaze());
        tank.coolDown();
        if (command.isFire() && tank.canFire(activeBullets(match, tank.getId()))) {
            for (Bullet bullet : tank.fire()) {
                launch(bullet, tank.getBarrelLength(), tanks, match.getMaze());
                match.getBullets().add(bullet);
            }
        }
    }

    /**
     * Weapons create bullets at the tank's centre. Moving them to the barrel tip with normal
     * steps means a barrel touching a wall makes the bullet bounce instead of starting inside
     * the wall, and a shot fired point-blank still hits the tank in front.
     */
    private static void launch(Bullet bullet, double barrelLength, List<Tank> tanks, Maze maze) {
        final double speed = Math.hypot(bullet.getVelocityX(), bullet.getVelocityY());
        if (speed == 0) {
            return;
        }
        final int steps = (int) Math.ceil(barrelLength / speed);
        for (int step = 0; step < steps && !bullet.isExpired() && bullet.getStepsPerTick() > 0; step++) {
            step(bullet, tanks, maze);
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
            step(bullet, tanks, maze);
        }
        bullet.age();
    }

    /** One step of a bullet, then a hit check against each tank. */
    private static void step(Bullet bullet, List<Tank> tanks, Maze maze) {
        bullet.update(maze);
        for (Tank tank : tanks) {
            if (bullet.hits(tank)) {
                tank.takeHit();
                bullet.onHitTank();
            }
        }
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
