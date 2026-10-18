package io.stealingdapenta.util;

import org.bukkit.Color;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class ArmorColor {

    private ArmorColor() {
    }

    public static boolean setColor(ItemMeta meta, Color color) {
        if (meta instanceof ColorableArmorMeta colorableArmorMeta) {
            colorableArmorMeta.setColor(color);
            return true;
        }
        if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(color);
            return true;
        }
        return false;
    }
}
