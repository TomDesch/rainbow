package io.stealingdapenta.command;

import static io.stealingdapenta.config.ConfigKey.ARMOR_ENABLED_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.INVALID_CYCLE_SPEED_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.NOT_ENOUGH_EMPTY_SPACES_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.NO_PERMISSION_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.PLAYERS_ONLY_MESSAGE;
import static io.stealingdapenta.config.PermissionNode.RAINBOW_ITEM_USE;

import io.stealingdapenta.ArmorPieceFactory;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

/**
 * Command that gives the executing player a set of tagged rainbow armor pieces. Usage: /rainbowitem [cycleSpeed]
 */
public class RainbowItemCommand implements CommandExecutor {

    private static final int MINIMUM_FREE_SLOTS = 4;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PLAYERS_ONLY_MESSAGE.getFormattedMessage());
            return true;
        }

        if (!player.hasPermission(RAINBOW_ITEM_USE.getNode())) {
            player.sendMessage(NO_PERMISSION_MESSAGE.getFormattedMessage());
            return true;
        }

        if (!hasFreeSlots(player.getInventory())) {
            player.sendMessage(NOT_ENOUGH_EMPTY_SPACES_MESSAGE.getFormattedMessage());
            return true;
        }

        int cycleSpeed = 5; // Default
        if (args.length > 0) {
            try {
                cycleSpeed = Integer.parseInt(args[0]);
                if (cycleSpeed <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                player.sendMessage(INVALID_CYCLE_SPEED_MESSAGE.getFormattedMessage());
                return true;
            }
        }

        ItemStack[] armorSet = ArmorPieceFactory.ARMOR_PIECE_FACTORY.createArmorSet(cycleSpeed);
        for (ItemStack piece : armorSet) {
            player.getInventory()
                  .addItem(piece);
        }

        player.sendMessage(ARMOR_ENABLED_MESSAGE.getFormattedMessage());
        return true;
    }

    /**
     * Checks if a player's inventory has at least MINIMUM_FREE_SLOTS empty spaces (ignoring armor slots).
     *
     * @param inventory the inventory to check
     * @return true if enough free slots exist, false otherwise
     */
    private boolean hasFreeSlots(PlayerInventory inventory) {
        int freeSlots = 0;
        for (ItemStack item : inventory.getStorageContents()) { // excludes armor + offhand
            if (item == null || item.getType() == Material.AIR) {
                freeSlots++;
            }
        }
        return freeSlots >= MINIMUM_FREE_SLOTS;
    }
}
