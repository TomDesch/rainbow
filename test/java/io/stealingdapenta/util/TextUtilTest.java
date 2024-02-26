package io.stealingdapenta.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.junit.jupiter.api.Test;

class TextUtilTest {

    private final TextUtil textUtil = TextUtil.getInstance();

    @Test
    void createStyle_ValidDecoration_ReturnsCorrectStyle() {
        TextDecoration validDecoration = TextDecoration.STRIKETHROUGH;

        Style result = textUtil.createStyle(validDecoration);

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
    void parseRGB_Valid_Success() {
        String input = "&(100, 150, 200)";
        TextColor result = textUtil.parseRGB(input);
        assertNotNull(result);
        assertEquals(100, result.red());
        assertEquals(150, result.green());
        assertEquals(200, result.blue());
    }

    @Test
    void parseRGB_LeadingSpaces_Success() {
        String input = "  &(50, 100, 150)";
        TextColor result = textUtil.parseRGB(input);
        assertNotNull(result);
        assertEquals(50, result.red());
        assertEquals(100, result.green());
        assertEquals(150, result.blue());
    }

    @Test
    void parseRGB_TrailingSpaces_Success() {
        String input = "&(25, 75, 125)  ";
        TextColor result = textUtil.parseRGB(input);
        assertNotNull(result);
        assertEquals(25, result.red());
        assertEquals(75, result.green());
        assertEquals(125, result.blue());
    }

    @Test
    void parseRGB_Invalid_IllegalArgumentException() {
        String input = "&(aaa, 555, 777)";
        assertThrows(IllegalArgumentException.class, () -> textUtil.parseRGB(input));
    }

    @Test
    void parseRGB_TooHighValues_Success() {
        String input = "&(256, 512, 768)";
        TextColor result = textUtil.parseRGB(input);
        assertNotNull(result);
        assertEquals(0, result.red());
        assertEquals(0, result.green());
        assertEquals(0, result.blue());
    }

    @Test
    void TEXT_PATTERN_InvalidColorCode_noMatch() {
        String input = "&(abc,def,ghi)";
        Matcher matcher = TEXT_PATTERN.matcher(input);

        assertFalse(matcher.matches());
    }

    @Test
    void parseFormattedString_NoFormat_PlainText() {
        String input = "Hello, World!";

        TextComponent result = textUtil.parseFormattedString(input);

        TextComponent expected = Component.text(input);
        assertEquals(expected, result);
    }

    @Test
    void parseFormattedString_ColorCodes_Formatted() {
        String input = "&(255,0,0)Red Text";
        TextColor color = TextColor.color(255, 0, 0);

        TextComponent result = textUtil.parseFormattedString(input);

        TextComponent expected = Component.text("Red Text", color);
        assertEquals(expected, result);
    }

    @Test
    void parseFormattedString_DecorationCodes_Formatted() {
        String input = "&bBold Text";
        TextComponent result = textUtil.parseFormattedString(input);

        TextComponent expected = Component.text("Bold Text", Style.style(TextDecoration.BOLD));
        assertEquals(expected, result);
    }

    @Test
    void parseFormattedString_ColorAndDecorationCodes_Formatted() {
        String input = "&(0,0,0)&bFormatted Text";
        TextColor color = TextColor.color(0, 0, 0);

        TextComponent result = textUtil.parseFormattedString(input);
        TextComponent expected = Component.text("Formatted Text", color, TextDecoration.BOLD);

        assertEquals(expected, result);
    }

    @Test
    void parseFormattedString_ColorAndDecorationCodesNotAsFirstPattern_Formatted() {
        String input = "Some unformatted and some &(0,0,0)&bFormatted Text";
        TextColor color = TextColor.color(0, 0, 0);

        TextComponent result = textUtil.parseFormattedString(input);
        TextComponent expected = Component.text("Some unformatted and some ")
                                          .append(Component.text("Formatted Text", color, TextDecoration.BOLD));

        assertEquals(expected, result);
    }

    @Test
    void parseFormattedString_InvalidFormatCodes_PlainText() {
        String input = "&InvalidCode&Text";

        TextComponent result = textUtil.parseFormattedString(input);

        TextComponent expected = Component.text(input);
        assertEquals(expected, result);
    }
}