package view;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SpritesTest {

    @Test
    void everyPowerUpIconIsBundledAndSquare() {
        for (String name : new String[] {"shotgun", "cannon", "laser", "shield", "shrink"}) {
            BufferedImage icon = Sprites.load("powerup-" + name + ".png");
            assertNotNull(icon, name);
            assertEquals(icon.getWidth(), icon.getHeight(), name);
        }
    }

    @Test
    void aMissingImageLoadsAsNull() {
        assertNull(Sprites.load("no-such-image.png"));
    }
}
