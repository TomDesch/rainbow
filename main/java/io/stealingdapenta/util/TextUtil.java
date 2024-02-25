package io.stealingdapenta.util;

import java.util.Map;
import java.util.regex.Pattern;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;

public class TextUtil {

    /**
     * All possible formatting options, exhausted list: b s u i o matching bold strikethrough underline italic obfuscated
     */
    private static final Map<String, Style> FORMAT_CODE_STYLES = Map.of("b", createStyle(TextDecoration.BOLD), "s", createStyle(TextDecoration.STRIKETHROUGH),
                                                                        "u", createStyle(TextDecoration.UNDERLINED), "i", createStyle(TextDecoration.ITALIC),
                                                                        "o", createStyle(TextDecoration.OBFUSCATED));


    /**
     * This regex is designed to match color codes in the format &(r,g,b) or the style codes &b, &u, &o, &s, or &i.
     * The backslashes are used to escape parentheses, and capturing groups allow extracting the individual color components.
     * Examples: &(12,7,222), &(255,255,255), &b, &u, &o, &s, &i
     * (.*) allows all the text after.
     */
    private static final Pattern TEXT_PATTERN = Pattern.compile("&(?:\\((\\d+),(\\d+),(\\d+)\\)|[buosi])(.*)");



    /**
     * Applies formatting styles based on the provided format code.
     *
     * @param formatCode The format code indicating the desired formatting (e.g., "b" for bold).
     * @return A Style object with the specified formatting applied.
     */
    static Style applyFormatCode(String formatCode) {
        return FORMAT_CODE_STYLES.getOrDefault(formatCode, Style.empty());
    }

    static Style createStyle(TextDecoration decoration) {
        return Style.style()
                    .decoration(decoration, State.TRUE)
                    .build();
    }
}
