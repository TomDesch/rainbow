package io.stealingdapenta.animator;

import static io.stealingdapenta.animator.AnimatorUtil.convertCountToRGB;
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
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
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
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Periodically scans for all instances of tagged rainbow armor and updates their color based on the animation cycle speed. Controlled via config to balance performance and visuals.
 */
public class TaggedArmorAnimator extends BukkitRunnable {

    private static final int THRESHOLD = 5000;
    private final int cycleSpeed;
    private final NamespacedKey tagKey = ArmorPieceFactory.getArmorTagKey();
    private int count = 0;

    public TaggedArmorAnimator(int cycleSpeed) {
        this.cycleSpeed = cycleSpeed;
    }

    @Override
    public void run() {
        Color color = convertCountToRGB(count);

        // Animate player-related items
        for (Player player : Bukkit.getOnlinePlayers()) {
            animatePlayer(player, color);
        }

        // Animate world-based items
        for (World world : Bukkit.getWorlds()) {
            animateWorldEntities(world, color);
            animateBlockInventories(world, color);
        }

        count += cycleSpeed;
        if (count >= Integer.MAX_VALUE - THRESHOLD) {
            count = 0;
        }
    }

    /**
     * Updates all rainbow armor related to a specific player, based on enabled config options.
     */
    public void animatePlayer(Player player, Color color) {
        // Equipped armor (always animated)
        for (ItemStack armorPiece : player.getInventory()
                                          .getArmorContents()) {
            applyColorIfRainbowArmor(armorPiece, color);
        }

        if (CHECK_CURSOR_ITEMS.asBoolean()) {
            applyColorIfRainbowArmor(player.getItemOnCursor(), color);
        }

        if (CHECK_PLAYER_INVENTORY.asBoolean()) {
            animateInventory(player.getInventory(), color);
        }

        if (CHECK_OPEN_INVENTORIES.asBoolean()) {
            Inventory openInventory = player.getOpenInventory()
                                            .getTopInventory();
            animateInventory(openInventory, color);
        }
    }

    /**
     * Updates rainbow armor found on entities and ground items in the given world.
     */
    public void animateWorldEntities(World world, Color color) {
        if (CHECK_GROUND_ITEMS.asBoolean()) {
            for (Item itemEntity : world.getEntitiesByClass(Item.class)) {
                applyColorIfRainbowArmor(itemEntity.getItemStack(), color);
            }
        }

        if (CHECK_ARMOR_STANDS.asBoolean()) {
            for (ArmorStand stand : world.getEntitiesByClass(ArmorStand.class)) {
                for (ItemStack armorPiece : stand.getEquipment()
                                                 .getArmorContents()) {
                    applyColorIfRainbowArmor(armorPiece, color);
                }
            }
        }

        if (CHECK_ITEM_FRAMES.asBoolean()) {
            for (ItemFrame frame : world.getEntitiesByClass(ItemFrame.class)) {
                applyColorIfRainbowArmor(frame.getItem(), color);
            }
        }

        if (CHECK_MOB_ARMOR.asBoolean()) {
            for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class)) {
                if (entity instanceof Player || entity instanceof ArmorStand) {
                    continue;
                }
                EntityEquipment equipment = entity.getEquipment();
                if (equipment != null) {
                    for (ItemStack armorPiece : equipment.getArmorContents()) {
                        applyColorIfRainbowArmor(armorPiece, color);
                    }
                }
            }
        }
    }

    /**
     * Scans container blocks like chests, shulkers, barrels, etc., in all loaded chunks of a world.
     */
    public void animateBlockInventories(World world, Color color) {
        if (!CHECK_BLOCK_INVENTORIES.asBoolean()) {
            return;
        }

        for (Chunk chunk : world.getLoadedChunks()) {
            for (BlockState blockState : chunk.getTileEntities()) {
                if (blockState instanceof Container container) {
                    animateInventory(container.getInventory(), color);
                }
            }
        }
    }

    /**
     * Applies the animation color to all rainbow armor pieces in a given inventory.
     */
    private void animateInventory(Inventory inventory, Color color) {
        for (ItemStack item : inventory.getContents()) {
            applyColorIfRainbowArmor(item, color);
        }
    }

    /**
     * Applies the animated color to the given item if it's recognized as rainbow armor.
     */
    private void applyColorIfRainbowArmor(ItemStack item, Color color) {
        if (item == null || !(item.getItemMeta() instanceof LeatherArmorMeta meta)) {
            return;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(tagKey)) {
            meta.setColor(color);
            item.setItemMeta(meta);
        }
    }
}