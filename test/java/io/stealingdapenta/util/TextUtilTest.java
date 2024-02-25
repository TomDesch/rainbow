package io.stealingdapenta.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private static final Pattern TEXT_PATTERN = Pattern.compile("&(?:\\((\\d+),(\\d+),(\\d+)\\)|[buosi])(.*)");

    @Test
    void TEXT_PATTERN_ColorCode_Matches() {
        String input = "&(127,255,0)thetext";
        Matcher matcher = TEXT_PATTERN.matcher(input);

        assertTrue(matcher.matches());
        assertEquals("127", matcher.group(1));
        assertEquals("255", matcher.group(2));
        assertEquals("0", matcher.group(3));
        assertEquals("thetext", matcher.group(4));
    }

    @Test
    void TEXT_PATTERN_DecoratorCode_Matches() {
        String input = "&b";
        Matcher matcher = TEXT_PATTERN.matcher(input);

        assertTrue(matcher.matches());
        assertNull(matcher.group(1));
        assertNull(matcher.group(2));
        assertNull(matcher.group(3));
    }

    @Test
    void TEXT_PATTERN_InvalidColorCode_noMatch() {
        String input = "&(abc,def,ghi)";
        Matcher matcher = TEXT_PATTERN.matcher(input);

        assertFalse(matcher.matches());
    }

//    @Test
//    void deserializeToFormattedText_MultipleFormatCodes_ReturnsCorrectTextComponent() {
//        TextColor color1 = TextColor.color(187, 0, 62);
//        TextColor color2 = TextColor.color(201, 255, 141);
//        TextColor color3 = TextColor.color(255, 29, 221);
//        String input = "&(187,0,62)color1 &(201, 255, 141)&scolor2 strikethrough &(255, 29, 221)&bcolor3 in bold!!";
//
//        TextComponent expected = Component.text()
//                                          .append(Component.text("color1 ", color1))
//                                          .append(Component.text("color2 strikethrough ", color2, TextDecoration.STRIKETHROUGH))
//                                          .append(Component.text("color3 in bold!!", color3, TextDecoration.BOLD))
//                                          .build();
//
//        TextComponent result = TextUtil.deserializeToFormattedText(input);
//
//        System.out.println(expected);
//        System.out.println(result);
//
//        assertNotNull(result);
//        assertEquals(expected, result);
//    }
}