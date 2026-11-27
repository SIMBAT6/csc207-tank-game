package data_access;

import use_case.control_tank.ControlTankDataAccessInterface;
import use_case.end_round.EndRoundMatchDataAccessInterface;

public final class InMemoryMatchDataAccessObject
        extends InMemoryPowerUpDataAccessObject
        implements ControlTankDataAccessInterface, EndRoundMatchDataAccessInterface {
}
