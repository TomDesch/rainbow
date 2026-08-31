package io.stealingdapenta.command;

import static io.stealingdapenta.config.ConfigKey.ARMOR_ADDED_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.INVALID_CYCLE_SPEED_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.NOT_ENOUGH_EMPTY_SPACES_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.NO_PERMISSION_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.PLAYERS_ONLY_MESSAGE;
import static io.stealingdapenta.config.PermissionNode.RAINBOW_HORSE_ARMOR;

import io.stealingdapenta.ArmorPieceFactory;
import io.stealingdapenta.config.ConfigKey;
import java.util.HashMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Command that gives the player a rainbow horse armor set. Usage: /rainbowhorseitem [cycleSpeed]
 */
public class RainbowHorseItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (!ConfigKey.CHECK_HORSES.asBoolean()) {
            sender.sendMessage(ConfigKey.FEATURE_DISABLED_MESSAGE.getFormattedMessage());
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(PLAYERS_ONLY_MESSAGE.getFormattedMessage());
            return true;
        }

        if (!player.hasPermission(RAINBOW_HORSE_ARMOR.getNode())) {
            player.sendMessage(NO_PERMISSION_MESSAGE.getFormattedMessage());
            return true;
        }

        int cycleSpeed = ConfigKey.CYCLE_SPEED.asInt(); // Default
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

        ItemStack armorSet = ArmorPieceFactory.ARMOR_PIECE_FACTORY.createHorseArmor(cycleSpeed);
        HashMap<Integer, ItemStack> leftover = player.getInventory()
                                                     .addItem(armorSet);

        if (!leftover.isEmpty()) { // This works here because we're only adding one item
            player.sendMessage(NOT_ENOUGH_EMPTY_SPACES_MESSAGE.getFormattedMessage());
            return true;
        }

        player.sendMessage(ARMOR_ADDED_MESSAGE.getFormattedMessage());
        return true;
    }
}
