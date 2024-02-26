package io.stealingdapenta;

import static io.stealingdapenta.config.ConfigKey.NO_PERMISSION_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.PLUGIN_RELOADED_MESSAGE;
import static io.stealingdapenta.config.PermissionNode.RAINBOW_RELOAD;

import io.stealingdapenta.rainbow.Rainbow;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RainbowReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        if (!player.hasPermission(RAINBOW_RELOAD.getNode())) {
            player.sendMessage(NO_PERMISSION_MESSAGE.getFormattedMessage());
            return true;
        }

        Rainbow.getInstance()
               .reloadConfig();
        player.sendMessage(PLUGIN_RELOADED_MESSAGE.getFormattedMessage());
        return true;
    }

}
