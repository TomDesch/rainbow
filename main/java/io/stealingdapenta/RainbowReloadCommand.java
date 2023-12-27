package io.stealingdapenta;

import io.stealingdapenta.rainbow.Rainbow;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RainbowReloadCommand implements CommandExecutor {

    private static final String NO_PERMISSION = "You don't have permission to use this command.";
    private static final String PERMISSION_NODE = "rainbow.reload";
    private static final String PLUGIN_RELOADED = "Rainbow armor plugin reloaded.";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        if (!player.hasPermission(PERMISSION_NODE)) {
            player.sendMessage(Component.text(NO_PERMISSION)
                                        .color(TextColor.color(255, 0, 0)));
            return true;
        }

        Rainbow.getInstance()
               .reloadConfig();
        player.sendMessage(Component.text(PLUGIN_RELOADED)
                                    .color(TextColor.color(75, 255, 75)));
        return true;
    }

}
