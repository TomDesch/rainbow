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
 * Singleton factory for creating and identifying special rainbow armor pieces. Uses PersistentDataContainer tags to track and verify custom items.
 */
public enum ArmorPieceFactory {
    ARMOR_PIECE_FACTORY;

    public static final Material HELMET_MATERIAL = Material.LEATHER_HELMET;
    public static final Material CHESTPLATE_MATERIAL = Material.LEATHER_CHESTPLATE;
    public static final Material LEGGINGS_MATERIAL = Material.LEATHER_LEGGINGS;
    public static final Material BOOTS_MATERIAL = Material.LEATHER_BOOTS;
    public static final String ARMOR_NSK = "Rainbow-Armor";

    /**
     * Creates a full set of tagged rainbow armor pieces (helmet, chestplate, leggings, boots).
     *
     * @return An array of 4 leather armor items marked as rainbow armor.
     */
    public ItemStack[] createArmorSet() {
        return new ItemStack[]{createHelmet(), createChestplate(), createLeggings(), createBoots()};
    }

    /**
     * Checks if a given ItemStack is one of the special rainbow armor pieces by checking for the PersistentDataContainer tag.
     *
     * @param itemStack The item to check.
     * @return True if the item is a rainbow armor piece, false otherwise.
     */
    public boolean isRainbowArmorPiece(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return false;
        }

        if (!(itemStack.getItemMeta() instanceof LeatherArmorMeta meta)) {
            return false;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(getRainbowArmorNSK(), PersistentDataType.BYTE);
    }

    /**
     * @return The NamespacedKey used to tag rainbow armor.
     */
    private NamespacedKey getRainbowArmorNSK() {
        return new NamespacedKey(Rainbow.getInstance(), ARMOR_NSK);
    }

    /**
     * @return A tagged leather helmet item.
     */
    private ItemStack createHelmet() {
        return createArmorPiece(HELMET_MATERIAL);
    }

    /**
     * @return A tagged leather chestplate item.
     */
    private ItemStack createChestplate() {
        return createArmorPiece(CHESTPLATE_MATERIAL);
    }

    /**
     * @return A tagged leather leggings item.
     */
    private ItemStack createLeggings() {
        return createArmorPiece(LEGGINGS_MATERIAL);
    }

    /**
     * @return A tagged leather boots item.
     */
    private ItemStack createBoots() {
        return createArmorPiece(BOOTS_MATERIAL);
    }

    /**
     * Creates a tagged leather armor item with a placeholder color and PersistentData flag.
     *
     * @param material The leather armor material to use (helmet, chestplate, etc).
     * @return A tagged ItemStack that is recognized as rainbow armor.
     */
    private ItemStack createArmorPiece(Material material) {
        ItemStack armorPiece = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) armorPiece.getItemMeta();

        if (meta == null) {
            Rainbow.getInstance()
                   .getLogger()
                   .warning("Failed to create armor piece: " + material);
            return null;
        }

        meta.setColor(Color.GREEN); // Placeholder; gets animated later
        meta.getPersistentDataContainer()
            .set(getRainbowArmorNSK(), PersistentDataType.BYTE, (byte) 1);

        armorPiece.setItemMeta(meta);
        return armorPiece;
    }
}
