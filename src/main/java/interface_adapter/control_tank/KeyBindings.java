package interface_adapter.control_tank;

import use_case.control_tank.TankCommand;

import java.awt.event.KeyEvent;
import java.util.Set;

/**
 * Which keys drive one player's tank. Turns the keys held this tick into a {@link TankCommand},
 * so the use case never sees key codes.
 */
public final class KeyBindings {
    /** Player 1: W A S D to drive, Q to fire. */
    public static final KeyBindings PLAYER_1 =
            new KeyBindings(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_Q);
    /** Player 2: arrow keys to drive, M to fire. */
    public static final KeyBindings PLAYER_2 =
            new KeyBindings(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_M);

    private final int forward;
    private final int backward;
    private final int left;
    private final int right;
    private final int fire;

    public KeyBindings(int forward, int backward, int left, int right, int fire) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.fire = fire;
    }

    /** Opposite keys held together cancel out. */
    public TankCommand toCommand(Set<Integer> pressedKeys) {
        int drive = held(pressedKeys, forward) - held(pressedKeys, backward);
        int turn = held(pressedKeys, right) - held(pressedKeys, left);
        return new TankCommand(drive, turn, pressedKeys.contains(fire));
    }

    private static int held(Set<Integer> pressedKeys, int key) {
        return pressedKeys.contains(key) ? 1 : 0;
    }
}
