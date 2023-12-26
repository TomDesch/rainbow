package io.stealingdapenta;

import static io.stealingdapenta.ArmorListener.AIR_ARMOR;
import static io.stealingdapenta.ArmorListener.playersWearingRainbowArmor;

import io.stealingdapenta.rainbow.Rainbow;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class RainbowCommand implements CommandExecutor {

    private static final String NO_PERMISSION = "You don't have permission to use this command.";
    private static final String PERMISSION_NODE = "rainbow.use";
    private static final String ARMOR_ENABLED = "Rainbow armor enabled.";
    private static final String ARMOR_DISABLED = "Rainbow armor disabled.";
    private static final String NO_EMPTY_SPACES = "You must have empty armor slots in order to use rainbow armor.";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        PlayerInventory playerInventory = player.getInventory();

        if (!player.hasPermission(PERMISSION_NODE)) {
            player.sendMessage(Component.text(NO_PERMISSION)
                                        .color(TextColor.color(255, 0, 0)));
            return true;
        }

        if (playersWearingRainbowArmor.contains(player.getName())) {
            playersWearingRainbowArmor.remove(player.getName());
            playerInventory.setArmorContents(AIR_ARMOR);
            player.sendMessage(Component.text(ARMOR_DISABLED)
                                        .color(TextColor.color(249, 255, 68)));
            return true;
        } else {

        }

        for (int i = 36; i <= 39; i++) {
            if (playerInventory.getItem(i) != null) {
                player.sendMessage(Component.text(NO_EMPTY_SPACES)
                                            .color(TextColor.color(255, 0, 0)));

                return true;
            }
        }
        playersWearingRainbowArmor.add(player.getName());
        player.sendMessage(Component.text(ARMOR_ENABLED)
                                    .color(TextColor.color(249, 255, 68)));

        Armor armor = new Armor(player);
        armor.runTaskTimer(Rainbow.getInstance(), 0L, 1L);

        return true;
    }
}
