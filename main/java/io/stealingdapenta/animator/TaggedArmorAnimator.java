package io.stealingdapenta.animator;

import static io.stealingdapenta.ArmorPieceFactory.getColorCountKey;
import static io.stealingdapenta.ArmorPieceFactory.getCycleSpeedKey;
import static io.stealingdapenta.config.ConfigKey.CHECK_ARMOR_STANDS;
import static io.stealingdapenta.config.ConfigKey.CHECK_BLOCK_INVENTORIES;
import static io.stealingdapenta.config.ConfigKey.CHECK_CURSOR_ITEMS;
import static io.stealingdapenta.config.ConfigKey.CHECK_GROUND_ITEMS;
import static io.stealingdapenta.config.ConfigKey.CHECK_ITEM_FRAMES;
import static io.stealingdapenta.config.ConfigKey.CHECK_MOB_ARMOR;
import static io.stealingdapenta.config.ConfigKey.CHECK_OPEN_INVENTORIES;
import static io.stealingdapenta.config.ConfigKey.CHECK_PLAYER_INVENTORY;

import io.stealingdapenta.ArmorPieceFactory;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Scans all armor pieces across the server and animates those tagged as rainbow armor. Each piece stores its own animation state via PersistentDataContainer. Config options control where the scanner looks (cursor, mobs, item frames, etc).
 */
public class TaggedArmorAnimator extends BukkitRunnable {

    private static final int THRESHOLD = 5000;

    @Override
    public void run() {
        // Player-scoped items
        for (Player player : Bukkit.getOnlinePlayers()) {
            animatePlayer(player);
        }

        // World-scoped items
        for (World world : Bukkit.getWorlds()) {
            animateWorldEntities(world);
            animateBlockInventories(world);
        }
    }

    /**
     * Updates all rainbow armor related to a specific player, based on enabled config options.
     */
    public void animatePlayer(Player player) {
        for (ItemStack armorPiece : player.getInventory()
                                          .getArmorContents()) {
            applyColorIfRainbowArmor(armorPiece);
        }

        if (CHECK_CURSOR_ITEMS.asBoolean()) {
            applyColorIfRainbowArmor(player.getItemOnCursor());
        }

        if (CHECK_PLAYER_INVENTORY.asBoolean()) {
            animateInventory(player.getInventory());
        }

        if (CHECK_OPEN_INVENTORIES.asBoolean()) {
            Inventory openInventory = player.getOpenInventory()
                                            .getTopInventory();
            animateInventory(openInventory);
        }
    }

    /**
     * Updates rainbow armor found in entities, ground items, armor stands, etc., in the given world.
     */
    public void animateWorldEntities(World world) {
        if (CHECK_GROUND_ITEMS.asBoolean()) {
            for (Item itemEntity : world.getEntitiesByClass(Item.class)) {
                applyColorIfRainbowArmor(itemEntity.getItemStack());
            }
        }

        if (CHECK_ARMOR_STANDS.asBoolean()) {
            for (ArmorStand stand : world.getEntitiesByClass(ArmorStand.class)) {
                for (ItemStack armorPiece : stand.getEquipment()
                                                 .getArmorContents()) {
                    applyColorIfRainbowArmor(armorPiece);
                }
            }
        }

        if (CHECK_ITEM_FRAMES.asBoolean()) {
            for (ItemFrame frame : world.getEntitiesByClass(ItemFrame.class)) {
                applyColorIfRainbowArmor(frame.getItem());
            }
        }

        if (CHECK_MOB_ARMOR.asBoolean()) {
            for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class)) {
                if (entity instanceof Player || entity instanceof ArmorStand) {
                    continue;
                }
                EntityEquipment equipment = entity.getEquipment();
                if (equipment == null) {
                    continue;
                }
                for (ItemStack armorPiece : equipment.getArmorContents()) {
                    applyColorIfRainbowArmor(armorPiece);
                }
            }
        }
    }

    /**
     * Scans container blocks like chests, shulkers, barrels, etc., in all loaded chunks of a world.
     */
    public void animateBlockInventories(World world) {
        if (!CHECK_BLOCK_INVENTORIES.asBoolean()) {
            return;
        }

        for (Chunk chunk : world.getLoadedChunks()) {
            for (BlockState blockState : chunk.getTileEntities()) {
                if (blockState instanceof Container container) {
                    animateInventory(container.getInventory());
                }
            }
        }
    }

    /**
     * Applies the animation color to all rainbow armor pieces in a given inventory.
     */
    private void animateInventory(Inventory inventory) {
        if (inventory == null) {
            return;
        }
        for (ItemStack item : inventory.getContents()) {
            applyColorIfRainbowArmor(item);
        }
    }

    /**
     * Animates a single item if it is recognized as tagged rainbow armor.
     */
    private void applyColorIfRainbowArmor(ItemStack item) {
        if (item == null || !(item.getItemMeta() instanceof LeatherArmorMeta meta)) {
            return;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(ArmorPieceFactory.getArmorTagKey())) {
            return;
        }

        int count = container.getOrDefault(getColorCountKey(), PersistentDataType.INTEGER, 0);
        int speed = container.getOrDefault(getCycleSpeedKey(), PersistentDataType.INTEGER, 5);

        meta.setColor(AnimatorUtil.convertCountToRGB(count));
        item.setItemMeta(meta);

        // Save updated state
        count += speed;
        if (count >= Integer.MAX_VALUE - THRESHOLD) {
            count = 0;
        }

        container.set(getColorCountKey(), PersistentDataType.INTEGER, count);
    }
}
