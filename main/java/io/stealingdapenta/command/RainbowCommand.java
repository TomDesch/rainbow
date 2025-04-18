package io.stealingdapenta.command;

import static io.stealingdapenta.ArmorListener.AIR_ARMOR;
import static io.stealingdapenta.ArmorListener.playersWearingRainbowArmor;
import static io.stealingdapenta.config.ConfigKey.ARMOR_DISABLED_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.ARMOR_ENABLED_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.NO_EMPTY_SPACES_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.NO_PERMISSION_MESSAGE;
import static io.stealingdapenta.config.PermissionNode.RAINBOW_USE;

import io.stealingdapenta.animator.BoundArmorAnimator;
import io.stealingdapenta.rainbow.Rainbow;
import java.util.Arrays;
import java.util.Objects;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class RainbowCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        Player player = (Player) sender;
        PlayerInventory playerInventory = player.getInventory();

        if (!player.hasPermission(RAINBOW_USE.getNode())) {
            player.sendMessage(NO_PERMISSION_MESSAGE.getFormattedMessage());
            return true;
        }

        if (playersWearingRainbowArmor.contains(player.getName())) {
            playersWearingRainbowArmor.remove(player.getName());
            playerInventory.setArmorContents(AIR_ARMOR);
            player.sendMessage(ARMOR_DISABLED_MESSAGE.getFormattedMessage());
            return true;
        }

        if (hasOccupiedArmorSlot(playerInventory)) {
            player.sendMessage(NO_EMPTY_SPACES_MESSAGE.getFormattedMessage());
            return true;
        }

        playersWearingRainbowArmor.add(player.getName());

        BoundArmorAnimator boundArmorAnimator;

        if (args.length > 0) {
            try {
                boundArmorAnimator = new BoundArmorAnimator(player, Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                playersWearingRainbowArmor.remove(player.getName());
                return false;
            }
        } else {
            boundArmorAnimator = new BoundArmorAnimator(player);
        }
        boundArmorAnimator.runTaskTimer(Rainbow.getInstance(), 0L, 1L);
        player.sendMessage(ARMOR_ENABLED_MESSAGE.getFormattedMessage());
        return true;
    }

    private boolean hasOccupiedArmorSlot(PlayerInventory inventory) {
        return Arrays.stream(inventory.getArmorContents())
                     .anyMatch(Objects::nonNull);
    }
}
