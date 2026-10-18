package io.stealingdapenta.config;

import java.util.OptionalInt;

public final class CycleSpeed {

    private CycleSpeed() {
    }

    public static OptionalInt parse(String value) {
        try {
            int speed = Integer.parseInt(value);
            return speed > 0 ? OptionalInt.of(speed) : OptionalInt.empty();
        } catch (NumberFormatException exception) {
            return OptionalInt.empty();
        }
    }

    public static OptionalInt parseConfiguredValue(Object value) {
        if (!(value instanceof Number number)) {
            return OptionalInt.empty();
        }

        double numericValue = number.doubleValue();
        if (!Double.isFinite(numericValue) || numericValue <= 0 || numericValue > Integer.MAX_VALUE || numericValue != Math.rint(numericValue)) {
            return OptionalInt.empty();
        }

        return OptionalInt.of((int) numericValue);
    }

    public static int fromConfiguration(Object value, int defaultSpeed) {
        return parseConfiguredValue(value).orElse(defaultSpeed);
    }
}
