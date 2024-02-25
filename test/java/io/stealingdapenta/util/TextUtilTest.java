package io.stealingdapenta.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.junit.jupiter.api.Test;

class TextUtilTest {

    @Test
    void applyFormatCode_Bold_ReturnsCorrectStyle() {
        String validCode = "b";

        Style result = TextUtil.applyFormatCode(validCode);

        assertNotNull(result);
        assertTrue(result.hasDecoration(TextDecoration.BOLD));
        assertEquals(State.TRUE, result.decoration(TextDecoration.BOLD));
    }

    @Test
    void applyFormatCode_Strikethrough_ReturnsCorrectStyle() {
        String validCode = "s";

        Style result = TextUtil.applyFormatCode(validCode);

        assertNotNull(result);
        assertTrue(result.hasDecoration(TextDecoration.STRIKETHROUGH));
        assertEquals(State.TRUE, result.decoration(TextDecoration.STRIKETHROUGH));
    }

    @Test
    void applyFormatCode_Underlined_ReturnsCorrectStyle() {
        String validCode = "u";

        Style result = TextUtil.applyFormatCode(validCode);

        assertNotNull(result);
        assertTrue(result.hasDecoration(TextDecoration.UNDERLINED));
        assertEquals(State.TRUE, result.decoration(TextDecoration.UNDERLINED));
    }

    @Test
    void applyFormatCode_Italic_ReturnsCorrectStyle() {
        String validCode = "i";

        Style result = TextUtil.applyFormatCode(validCode);

        assertNotNull(result);
        assertTrue(result.hasDecoration(TextDecoration.ITALIC));
        assertEquals(State.TRUE, result.decoration(TextDecoration.ITALIC));
    }

    @Test
    void applyFormatCode_Obfuscated_ReturnsCorrectStyle() {
        String validCode = "o";

        Style result = TextUtil.applyFormatCode(validCode);

        assertNotNull(result);
        assertTrue(result.hasDecoration(TextDecoration.OBFUSCATED));
        assertEquals(State.TRUE, result.decoration(TextDecoration.OBFUSCATED));
    }

    @Test
    void applyFormatCode_InvalidCode_ReturnsEmptyStyle() {
        String invalidCode = "xyz";

        Style result = TextUtil.applyFormatCode(invalidCode);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createStyle_ValidDecoration_ReturnsCorrectStyle() {
        TextDecoration validDecoration = TextDecoration.STRIKETHROUGH;

        Style result = TextUtil.createStyle(validDecoration);

        assertNotNull(result);
        assertTrue(result.hasDecoration(validDecoration));
        assertEquals(State.TRUE, result.decoration(validDecoration));
    }
}