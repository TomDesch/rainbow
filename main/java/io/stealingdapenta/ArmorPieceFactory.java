package io.stealingdapenta;

import io.stealingdapenta.rainbow.Rainbow;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
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
    private static final String ARMOR_TAG_KEY = "Rainbow-Armor";
    private static final String SPEED_TAG_KEY = "Cycle-Speed";
    private static final String COLOR_COUNT_KEY = "Color-Count";

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
     * Checks if the given item is a rainbow armor piece by checking its persistent tags.
     *
     * @param itemStack The item to check.
     * @return True if the item is a rainbow armor piece, false otherwise.
     */
    public boolean isRainbowArmorPiece(ItemStack itemStack) {
        if (itemStack == null || !(itemStack.getItemMeta() instanceof LeatherArmorMeta meta)) {
            return false;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(getArmorTagKey(), PersistentDataType.BYTE);
    }

    /**
     * Gets the cycle speed value embedded into a rainbow armor piece, or -1 if not found.
     *
     * @param itemStack The tagged rainbow armor item.
     * @return The configured cycle speed, or -1 if missing or invalid.
     */
    public int getCycleSpeed(ItemStack itemStack) {
        if (itemStack == null || !(itemStack.getItemMeta() instanceof LeatherArmorMeta meta)) {
            return -1;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(getCycleSpeedKey(), PersistentDataType.INTEGER, -1);
    }

    /**
     * @return The NamespacedKey used to store the counter of the current color of rainbow armor.
     */
    public static NamespacedKey getColorCountKey() {
        return new NamespacedKey(Rainbow.getInstance(), COLOR_COUNT_KEY);
    }

    /**
     * @return The NamespacedKey used to identify rainbow armor items.
     */
    public static NamespacedKey getArmorTagKey() {
        return new NamespacedKey(Rainbow.getInstance(), ARMOR_TAG_KEY);
    }

    /**
     * @return The NamespacedKey used to store the cycle speed of rainbow armor.
     */
    public static NamespacedKey getCycleSpeedKey() {
        return new NamespacedKey(Rainbow.getInstance(), SPEED_TAG_KEY);
    }

    /**
     * Creates a single rainbow armor piece of the given type, tagged with animation settings.
     *
     * @param material   The leather armor material (helmet, chestplate, etc).
     * @param cycleSpeed The animation cycle speed to embed in the item.
     * @return A fully tagged rainbow armor ItemStack.
     */
    private ItemStack createArmorPiece(Material material, int cycleSpeed) {
        ItemStack armorPiece = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) armorPiece.getItemMeta();

        if (meta == null) {
            Rainbow.getInstance()
                   .getLogger()
                   .warning("Failed to create armor piece: " + material);
            return null;
        }

        meta.setColor(Color.GREEN); // Placeholder; will be updated by animation
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(getArmorTagKey(), PersistentDataType.BYTE, (byte) 1);
        container.set(getCycleSpeedKey(), PersistentDataType.INTEGER, cycleSpeed);
        container.set(getColorCountKey(), PersistentDataType.INTEGER, 0);

        armorPiece.setItemMeta(meta);
        return armorPiece;
    }
}
