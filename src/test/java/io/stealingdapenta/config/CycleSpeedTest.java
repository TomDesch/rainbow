package io.stealingdapenta.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

class CycleSpeedTest {

    @Test
    void parseAcceptsPositiveIntegers() {
        assertEquals(OptionalInt.of(1), CycleSpeed.parse("1"));
        assertEquals(OptionalInt.of(Integer.MAX_VALUE), CycleSpeed.parse(Integer.toString(Integer.MAX_VALUE)));
    }

    @Test
    void parseRejectsNonpositiveAndOutOfRangeValues() {
        assertTrue(CycleSpeed.parse("0").isEmpty());
        assertTrue(CycleSpeed.parse("-1").isEmpty());
        assertTrue(CycleSpeed.parse("2147483648").isEmpty());
        assertTrue(CycleSpeed.parse("not-a-number").isEmpty());
    }

    @Test
    void parseConfiguredValueRequiresPositiveIntegralNumber() {
        assertEquals(OptionalInt.of(5), CycleSpeed.parseConfiguredValue(5));
        assertTrue(CycleSpeed.parseConfiguredValue(0).isEmpty());
        assertTrue(CycleSpeed.parseConfiguredValue(-1).isEmpty());
        assertTrue(CycleSpeed.parseConfiguredValue(1.5).isEmpty());
        assertTrue(CycleSpeed.parseConfiguredValue("5").isEmpty());
        assertTrue(CycleSpeed.parseConfiguredValue(Long.MAX_VALUE).isEmpty());
    }

    @Test
    void fromConfigurationUsesDefaultForInvalidValues() {
        assertEquals(5, CycleSpeed.fromConfiguration(0, 5));
        assertEquals(5, CycleSpeed.fromConfiguration("invalid", 5));
        assertEquals(7, CycleSpeed.fromConfiguration(7, 5));
    }
}
