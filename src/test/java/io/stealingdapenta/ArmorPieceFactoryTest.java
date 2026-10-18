package io.stealingdapenta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ArmorPieceFactoryTest {

    @Test
    void armorKeysUseConsistentNamespaceAndNames() {
        assertEquals("rainbowarmor", ArmorPieceFactory.getArmorTagKey().getNamespace());
        assertEquals("rainbow-armor", ArmorPieceFactory.getArmorTagKey().getKey());
        assertEquals("rainbowarmor", ArmorPieceFactory.getCycleSpeedKey().getNamespace());
        assertEquals("cycle-speed", ArmorPieceFactory.getCycleSpeedKey().getKey());
        assertEquals("rainbowarmor", ArmorPieceFactory.getColorCountKey().getNamespace());
        assertEquals("color-count", ArmorPieceFactory.getColorCountKey().getKey());
    }
}
