package io.stealingdapenta.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;

public class TextUtil {

    private static TextUtil instance;

    public static TextUtil getInstance() {
        if (Objects.isNull(instance)) {
            instance = new TextUtil();
        }

        return instance;
    }

    /**
     * All possible formatting options, exhausted list: b s u i o matching bold strikethrough underline italic obfuscated
     */
    private static final Map<String, TextDecoration> FORMAT_CODE_STYLES = Map.of("b", TextDecoration.BOLD, "s", TextDecoration.STRIKETHROUGH, "u",
                                                                                 TextDecoration.UNDERLINED, "i", TextDecoration.ITALIC, "o",
                                                                                 TextDecoration.OBFUSCATED);


    private static final Pattern RGB_PATTERN = Pattern.compile("&(\\(\\d{1,3},\\d{1,3},\\d{1,3}\\))");
    private static final Pattern DECORATOR_PATTERN = Pattern.compile("&([buosi])");

    /**
     * This regex is designed to match color codes in the format &(r,g,b) or the style codes &b, &u, &o, &s, or &i. The backslashes are used to escape
     * parentheses, and capturing groups allow extracting the individual color components. Examples: &(12,7,222), &(255,255,255), &b, &u, &o, &s, &i (.*) allows
     * all the text after.
     */
    private static final Pattern TEXT_PATTERN = Pattern.compile(RGB_PATTERN + "|" + DECORATOR_PATTERN);

    Style createStyle(TextDecoration decoration) {
        return Style.style()
                    .decoration(decoration, State.TRUE)
                    .build();
    }


    public TextComponent parseFormattedString(String input) {
        String[] segments = TEXT_PATTERN.splitWithDelimiters(input, 0);

        if (segments.length <= 1) {
            return Component.text(segments[0]);
        }

        List<TextComponent> formattedSegments = new ArrayList<>();

        TextComponent.Builder textSegment = Component.text();

        int index = 0;

        while (index < segments.length) {
            if (Objects.isNull(textSegment)) {
                textSegment = Component.text();
            }
            String currentElement = segments[index];

            if (isRgbPattern(currentElement)) {
                textSegment.color(parseRGB(currentElement));
            } else if (isDecoratorPattern(currentElement)) {
                textSegment.decorationIfAbsent(FORMAT_CODE_STYLES.get(currentElement.substring(1)), State.TRUE);
            } else if (!currentElement.isBlank()) {
                if (textSegment.content()
                               .isEmpty()) {
                    textSegment.content(currentElement);
                    formattedSegments.add(textSegment.build());
                    textSegment = null;
                }
            }

            index++;
        }

        return combineTextComponents(formattedSegments);
    }

    private TextComponent combineTextComponents(List<TextComponent> textComponents) {
        if (textComponents.isEmpty()) {
            throw new IllegalArgumentException("Input list of TextComponents is empty");
        }

        TextComponent combinedComponent = textComponents.get(textComponents.size() - 1);

        for (int i = textComponents.size() - 2; i >= 0; i--) {
            combinedComponent = textComponents.get(i)
                                              .append(combinedComponent);
        }

        return combinedComponent;
    }

    private boolean isRgbPattern(String element) {
        return RGB_PATTERN.matcher(element)
                          .matches();
    }

    private boolean isDecoratorPattern(String element) {
        return DECORATOR_PATTERN.matcher(element)
                                .matches();
    }


    TextColor parseRGB(String input) {
        Matcher matcher = RGB_PATTERN.matcher(input.replace(" ", ""));

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Problem formatting RGB from input.");
        }

        String rgbString = matcher.group(1)
                                  .replaceAll("[()]", ""); // Remove parentheses
        String[] rgbComponents = rgbString.split(",");

        int red = parseRGBComponent(rgbComponents[0]);
        int green = parseRGBComponent(rgbComponents[1]);
        int blue = parseRGBComponent(rgbComponents[2]);

        return TextColor.color(toValidRGB(red), toValidRGB(green), toValidRGB(blue));
    }

    private int parseRGBComponent(String component) {
        try {
            return Integer.parseInt(component);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid RGB component: " + component, e);
        }
    }

    private int toValidRGB(int value) {
        return value % 256;
    }
}
