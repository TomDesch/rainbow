package io.stealingdapenta.command;

import static io.stealingdapenta.config.ConfigKey.ARMOR_ENABLED_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.NOT_ENOUGH_EMPTY_SPACES_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.NO_PERMISSION_MESSAGE;
import static io.stealingdapenta.config.PermissionNode.RAINBOW_ITEM_USE;

import io.stealingdapenta.ArmorPieceFactory;
import io.stealingdapenta.BoundArmorAnimator;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class RainbowItemCommand implements CommandExecutor { // todo register me

    private static final int MINIMUM_FREE_SLOTS = 4;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        Player player = (Player) sender;
        PlayerInventory playerInventory = player.getInventory();

        if (!player.hasPermission(RAINBOW_ITEM_USE.getNode())) {
            player.sendMessage(NO_PERMISSION_MESSAGE.getFormattedMessage());
            return true;
        }

        if (!hasFreeSlots(playerInventory)) {
            player.sendMessage(NOT_ENOUGH_EMPTY_SPACES_MESSAGE.getFormattedMessage());
            return true;
        }

        BoundArmorAnimator boundArmorAnimator;
        if (args.length > 0) {
            try {
                int cycleSpeed = Integer.parseInt(args[0]);
                ItemStack[] armorSet = ArmorPieceFactory.ARMOR_PIECE_FACTORY.createArmorSet();

            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            boundArmorAnimator = new BoundArmorAnimator(player);
        }
        //        boundArmorAnimator.runTaskTimer(Rainbow.getInstance(), 0L, 1L); fixme
        player.sendMessage(ARMOR_ENABLED_MESSAGE.getFormattedMessage());
        return true;
    }


    /**
     * @param inventory the inventory to check for enough free slots
     * @return true if there are at least MINIMUM_FREE_SLOTS free slots in the inventory, false otherwise
     */
    private boolean hasFreeSlots(PlayerInventory inventory) {
        int freeSlots = 0;

        ItemStack[] contents = inventory.getStorageContents(); // excludes armor + offhand
        for (ItemStack item : contents) {
            if (item == null || item.getType() == Material.AIR) {
                freeSlots++;
            }
        }
        return freeSlots >= MINIMUM_FREE_SLOTS;
    }

}
