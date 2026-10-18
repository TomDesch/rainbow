package io.stealingdapenta.animator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TaggedArmorAnimatorTest {

    @Test
    void advancesColorBySpeedEachTick() {
        assertEquals(101, TaggedArmorAnimator.advanceColorCount(100, 1));
        assertEquals(120, TaggedArmorAnimator.advanceColorCount(100, 20));
        assertEquals(200, TaggedArmorAnimator.advanceColorCount(100, 100));
    }

    @Test
    void resetsColorCounterWhenNextCountCrossesOverflowThreshold() {
        assertEquals(0, TaggedArmorAnimator.advanceColorCount(Integer.MAX_VALUE - 5004, 5));
    }
}
