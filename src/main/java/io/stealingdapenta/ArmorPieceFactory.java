package io.stealingdapenta;

import io.stealingdapenta.rainbow.Rainbow;
import io.stealingdapenta.util.ArmorColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Singleton factory for creating and identifying special rainbow armor pieces. Rainbow armor is tagged using the PersistentDataContainer with both a boolean identity and a configurable cycle speed value.
 */
public enum ArmorPieceFactory {
    ARMOR_PIECE_FACTORY;

    public static final Material HELMET_MATERIAL = Material.LEATHER_HELMET;
    public static final Material CHESTPLATE_MATERIAL = Material.LEATHER_CHESTPLATE;
    public static final Material LEGGINGS_MATERIAL = Material.LEATHER_LEGGINGS;
    public static final Material BOOTS_MATERIAL = Material.LEATHER_BOOTS;
    private static final NamespacedKey ARMOR_TAG_KEY = new NamespacedKey("rainbowarmor", "rainbow-armor");
    private static final NamespacedKey SPEED_TAG_KEY = new NamespacedKey("rainbowarmor", "cycle-speed");
    private static final NamespacedKey COLOR_COUNT_KEY = new NamespacedKey("rainbowarmor", "color-count");

    /**
     * Creates a full set of leather armor tagged as rainbow armor with a given cycle speed.
     *
     * @param cycleSpeed The animation cycle speed to tag into each piece.
     * @return An array containing the helmet, chestplate, leggings, and boots.
     */
    public ItemStack[] createArmorSet(int cycleSpeed) {
        return new ItemStack[]{createArmorPiece(HELMET_MATERIAL, cycleSpeed), createArmorPiece(CHESTPLATE_MATERIAL, cycleSpeed), createArmorPiece(LEGGINGS_MATERIAL, cycleSpeed), createArmorPiece(BOOTS_MATERIAL, cycleSpeed)};
    }

    /**
     * @param cycleSpeed The animation cycle speed to tag into the horse armor.
     * @return A leather horse armor piece tagged as rainbow armor.
     */
    public ItemStack createHorseArmor(int cycleSpeed) {
        return createArmorPiece(Material.LEATHER_HORSE_ARMOR, cycleSpeed);
    }

    /**
     * Checks if the given item is a rainbow armor piece by checking its persistent tags.
     *
     * @param itemStack The item to check.
     * @return True if the item is a rainbow armor piece, false otherwise.
     */
    public boolean isRainbowArmorPiece(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return false;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return false;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(getArmorTagKey(), PersistentDataType.BYTE);
    }

    /**
     * @return The NamespacedKey used to store the counter of the current color of rainbow armor.
     */
    public static NamespacedKey getColorCountKey() {
        return COLOR_COUNT_KEY;
    }

    /**
     * @return The NamespacedKey used to identify rainbow armor items.
     */
    public static NamespacedKey getArmorTagKey() {
        return ARMOR_TAG_KEY;
    }

    /**
     * @return The NamespacedKey used to store the cycle speed of rainbow armor.
     */
    public static NamespacedKey getCycleSpeedKey() {
        return SPEED_TAG_KEY;
    }

    /**
     * Creates a single rainbow armor piece of the given type, tagged with animation settings.
     *
     * @param material   The leather armor material (helmet, chestplate, etc.).
     * @param cycleSpeed The animation cycle speed to embed in the item.
     * @return A fully tagged rainbow armor ItemStack.
     */
    private ItemStack createArmorPiece(Material material, int cycleSpeed) {
        ItemStack armorPiece = new ItemStack(material);
        ItemMeta meta = armorPiece.getItemMeta();

        if (meta == null) {
            if (Rainbow.getInstance() != null) {
                Rainbow.getInstance()
                       .getLogger()
                       .warning("Failed to create armor piece: " + material);
            }
            return null;
        }

        ArmorColor.setColor(meta, Color.GREEN);

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(getArmorTagKey(), PersistentDataType.BYTE, (byte) 1);
        container.set(getCycleSpeedKey(), PersistentDataType.INTEGER, cycleSpeed);
        container.set(getColorCountKey(), PersistentDataType.INTEGER, 0);

        armorPiece.setItemMeta(meta);
        return armorPiece;
    }
}
