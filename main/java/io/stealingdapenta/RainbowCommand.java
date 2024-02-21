package io.stealingdapenta;

import static io.stealingdapenta.ArmorListener.AIR_ARMOR;
import static io.stealingdapenta.ArmorListener.playersWearingRainbowArmor;
import static io.stealingdapenta.config.ConfigKey.ARMOR_DISABLED_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.ARMOR_ENABLED_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.NO_EMPTY_SPACES_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.NO_PERMISSION_MESSAGE;
import static io.stealingdapenta.config.PermissionNode.RAINBOW_USE;

import io.stealingdapenta.rainbow.Rainbow;
import java.util.Arrays;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class RainbowCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        PlayerInventory playerInventory = player.getInventory();

        if (!player.hasPermission(RAINBOW_USE.getNode())) {
            player.sendMessage(Component.text(NO_PERMISSION_MESSAGE.getValue().toString())
                                        .color(TextColor.color(255, 0, 0)));
            return true;
        }

        if (playersWearingRainbowArmor.contains(player.getName())) {
            playersWearingRainbowArmor.remove(player.getName());
            playerInventory.setArmorContents(AIR_ARMOR);
            player.sendMessage(Component.text(ARMOR_DISABLED_MESSAGE.getValue().toString())
                                        .color(TextColor.color(249, 255, 68)));
            return true;
        }

        if (hasOccupiedArmorSlot(playerInventory)) {
            player.sendMessage(Component.text(NO_EMPTY_SPACES_MESSAGE.getValue().toString())
                                        .color(TextColor.color(255, 0, 0)));
            return true;
        }

        playersWearingRainbowArmor.add(player.getName());

        Armor armor;

        if (args.length > 0) {
            try {
                armor = new Armor(player, Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                playersWearingRainbowArmor.remove(player.getName());
                return false;
            }
        } else {
            armor = new Armor(player);
        }
        armor.runTaskTimer(Rainbow.getInstance(), 0L, 1L);
        player.sendMessage(Component.text(ARMOR_ENABLED_MESSAGE.getValue().toString())
                                    .color(TextColor.color(75, 255, 75)));
        return true;
    }

    private boolean hasOccupiedArmorSlot(PlayerInventory inventory) {
        return Arrays.stream(inventory.getArmorContents())
                     .anyMatch(Objects::nonNull);
    }
}
