package io.stealingdapenta.animator;

import static io.stealingdapenta.ArmorPieceFactory.ARMOR_PIECE_FACTORY;
import static io.stealingdapenta.ArmorPieceFactory.getColorCountKey;
import static io.stealingdapenta.ArmorPieceFactory.getCycleSpeedKey;
import static io.stealingdapenta.config.ConfigKey.CHECK_BLOCK_INVENTORIES;
import static io.stealingdapenta.config.ConfigKey.CHECK_PLAYER_INVENTORY;

import io.stealingdapenta.ArmorPieceFactory;
import io.stealingdapenta.config.ConfigKey;
import io.stealingdapenta.rainbow.Rainbow;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Scans all armor pieces across the server and animates those tagged as rainbow armor. Each piece stores its own animation state via PersistentDataContainer. Config options control where the scanner looks (cursor, mobs, item frames, etc.).
 */
public class TaggedArmorAnimator extends BukkitRunnable {

    private static final int THRESHOLD = 5000;

    @Override
    public void run() {
        if (!ConfigKey.ARMOR_ITEM_FEATURE.asBoolean()) {
            Rainbow.logger.warning("Armor item feature not enabled, yet the animator is running!");
            this.cancel();
            return;
        }

        // Player-scoped items
        for (Player player : Bukkit.getOnlinePlayers()) {
            animatePlayer(player);
        }

        // World-scoped items
        if (!CHECK_BLOCK_INVENTORIES.asBoolean()) {
            return;
        }
        for (World world : Bukkit.getWorlds()) {
            animateBlockInventories(world);
        }
    }

    /**
     * Updates all rainbow armor related to a specific player, based on enabled config options.
     */
    public void animatePlayer(@NotNull Player player) {
        // Animate the armor being worn regardless what the pLayer is doing
        for (ItemStack armorPiece : player.getInventory()
                                          .getArmorContents()) {
            applyColorIfRainbowArmor(armorPiece, player);
        }

        if (CHECK_PLAYER_INVENTORY.asBoolean()) {
            animateInventoryFor(player);
        }
    }

    /**
     * Scans container blocks like chests, shulkers, barrels, etc., in all loaded chunks of a world.
     */
    public void animateBlockInventories(World world) {
        for (Chunk chunk : world.getLoadedChunks()) {
            for (BlockState blockState : chunk.getTileEntities()) {
                if (blockState instanceof Container container) {
                    animateBlockInventory(container.getInventory());
                }
            }
        }
    }

    /**
     * Applies the animation color to all rainbow armor pieces in a given inventory.
     */
    private void animateBlockInventory(@NotNull Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            applyColorIfRainbowArmor(item, null);
        }
    }

    /**
     * Applies the animation color to all rainbow armor pieces in a given inventory.
     */
    private void animateInventoryFor(@NotNull Player player) {
        for (ItemStack item : player.getInventory()
                                    .getContents()) {
            applyColorIfRainbowArmor(item, player);
        }
    }

    /**
     * Animates a single item if it is recognized as tagged rainbow armor. Optionally skips animation if the item is currently on the cursor of a given player (to avoid duplication glitches).
     *
     * @param item           The item to potentially animate.
     * @param excludedPlayer Players whose items potentially should not be updated (e.g., during open inventory interactions).
     */
    private void applyColorIfRainbowArmor(ItemStack item, Player excludedPlayer) {
        if (!ARMOR_PIECE_FACTORY.isRainbowArmorPiece(item) || !(item.getItemMeta() instanceof LeatherArmorMeta meta)) {
            return;
        }

        // Prevent updating items currently held on the cursor (avoids visual/duplication glitches)
        if (ARMOR_PIECE_FACTORY.isRainbowArmorPiece(excludedPlayer.getItemOnCursor())) {
            return; // Skip the item if it's held on the cursor to prevent any further animation updates
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(ArmorPieceFactory.getArmorTagKey())) {
            return;
        }

        int count = container.getOrDefault(getColorCountKey(), PersistentDataType.INTEGER, 0);
        int speed = container.getOrDefault(getCycleSpeedKey(), PersistentDataType.INTEGER, 5);

        meta.setColor(AnimatorUtil.convertCountToRGB(count));

        // Save updated state BEFORE setting item meta
        count += speed;
        if (count >= Integer.MAX_VALUE - THRESHOLD) {
            count = 0;
        }

        container.set(getColorCountKey(), PersistentDataType.INTEGER, count);

        // Now apply the changes
        item.setItemMeta(meta);
    }
}
