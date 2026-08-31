package io.stealingdapenta.animator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TaggedArmorAnimatorTest {

    @Test
    void advancesColorByElapsedTicksBetweenScans() {
        assertEquals(5L, TaggedArmorAnimator.TICK_PERIOD);
        assertEquals(125, TaggedArmorAnimator.advanceColorCount(100, 5));
    }

    @Test
    void resetsColorCounterBeforeOverflow() {
        assertEquals(0, TaggedArmorAnimator.advanceColorCount(Integer.MAX_VALUE - 5001, 5));
    }
}
