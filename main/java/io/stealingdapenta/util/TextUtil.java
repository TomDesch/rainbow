package io.stealingdapenta.util;

import java.util.Map;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;

public class TextUtil {

    /**
     * All possible formatting options, exhausted list: b s u i o matching bold strikethrough underline italic obfuscated
     */
    private static final Map<String, Style> FORMAT_CODE_STYLES = Map.of(
            "b", createStyle(TextDecoration.BOLD),
            "s", createStyle(TextDecoration.STRIKETHROUGH),
            "u", createStyle(TextDecoration.UNDERLINED),
            "i", createStyle(TextDecoration.ITALIC),
            "o", createStyle(TextDecoration.OBFUSCATED)
    );

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
