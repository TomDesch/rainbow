package io.stealingdapenta.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.junit.jupiter.api.Test;

class ArmorColorTest {

    @Test
    void setColorSupportsPlayerAndHorseMetadataFamilies() {
        for (Class<? extends ItemMeta> family : List.of(ColorableArmorMeta.class, LeatherArmorMeta.class)) {
            AtomicReference<Color> appliedColor = new AtomicReference<>();
            ItemMeta meta = (ItemMeta) Proxy.newProxyInstance(family.getClassLoader(), new Class<?>[]{family},
                    (proxy, method, args) -> {
                        if (method.getName().equals("setColor")) {
                            appliedColor.set((Color) args[0]);
                        }
                        return null;
                    });

            assertTrue(ArmorColor.setColor(meta, Color.GREEN));
            assertEquals(Color.GREEN, appliedColor.get());
        }
    }

    @Test
    void setColorSkipsUnsupportedAndMissingMetadata() {
        ItemMeta meta = (ItemMeta) Proxy.newProxyInstance(ItemMeta.class.getClassLoader(), new Class<?>[]{ItemMeta.class},
                (proxy, method, args) -> {
                    throw new AssertionError("Unsupported metadata must not be mutated");
                });

        assertFalse(ArmorColor.setColor(meta, Color.GREEN));
        assertFalse(ArmorColor.setColor(null, Color.GREEN));
    }
}
