package interface_adapter.control_tank;

import org.junit.jupiter.api.Test;
import use_case.control_tank.ControlTankInputData;
import use_case.control_tank.TankCommand;

import java.awt.event.KeyEvent;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyBindingsTest {

    @Test
    void player1DrivesWithWasdAndFiresWithQ() {
        TankCommand command = KeyBindings.PLAYER_1.toCommand(Set.of(KeyEvent.VK_W, KeyEvent.VK_D, KeyEvent.VK_Q));

        assertEquals(1, command.getDrive());
        assertEquals(1, command.getTurn());
        assertTrue(command.isFire());
    }

    @Test
    void player2DrivesWithArrowsAndFiresWithM() {
        TankCommand command = KeyBindings.PLAYER_2.toCommand(
                Set.of(KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_M));

        assertEquals(-1, command.getDrive());
        assertEquals(-1, command.getTurn());
        assertTrue(command.isFire());
    }

    @Test
    void oppositeKeysCancelOut() {
        TankCommand command = KeyBindings.PLAYER_1.toCommand(
                Set.of(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D));

        assertEquals(0, command.getDrive());
        assertEquals(0, command.getTurn());
    }

    @Test
    void theOtherPlayersKeysAreIgnored() {
        TankCommand command = KeyBindings.PLAYER_1.toCommand(Set.of(KeyEvent.VK_UP, KeyEvent.VK_M));

        assertEquals(0, command.getDrive());
        assertFalse(command.isFire());
    }

    @Test
    void controllerSendsBothPlayersCommandsFromTheSameKeys() {
        ControlTankInputData[] sent = new ControlTankInputData[1];
        ControlTankController controller = new ControlTankController(
                input -> sent[0] = input, KeyBindings.PLAYER_1, KeyBindings.PLAYER_2);

        controller.execute(Set.of(KeyEvent.VK_W, KeyEvent.VK_UP, KeyEvent.VK_M));

        assertEquals(1, sent[0].getPlayer1().getDrive());
        assertFalse(sent[0].getPlayer1().isFire());
        assertEquals(1, sent[0].getPlayer2().getDrive());
        assertTrue(sent[0].getPlayer2().isFire());
    }
}
