package io.stealingdapenta.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.stealingdapenta.RainbowReloadCommand;
import io.stealingdapenta.config.ConfigKey;
import java.lang.reflect.Proxy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

class CommandSafetyTest {

    @Test
    void rainbowCommandHandlesConsoleSender() {
        Command command = new Command("rainbow") {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };

        assertTrue(new RainbowCommand().onCommand(newConsoleSender(), command, "rainbow", new String[0]));
    }

    @Test
    void rainbowReloadCommandHandlesConsoleSender() {
        Command command = new Command("rainbowreload") {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };

        assertTrue(new RainbowReloadCommand().onCommand(newConsoleSender(), command, "rainbowreload", new String[0]));
    }

    @Test
    void armorRemovedMessageUsesCorrectConfigKey() {
        assertEquals("armor-removed-message", ConfigKey.ARMOR_REMOVED_MESSAGE.getKey());
    }

    private static CommandSender newConsoleSender() {
        return (CommandSender) Proxy.newProxyInstance(
                CommandSender.class.getClassLoader(),
                new Class[]{CommandSender.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("sendMessage")) {
                        return null;
                    }
                    if (method.getName().equals("hasPermission")) {
                        return false;
                    }
                    if (method.getName().equals("isPermissionSet")) {
                        return false;
                    }
                    if (method.getName().equals("getName")) {
                        return "console";
                    }
                    if (method.getName().equals("isOp")) {
                        return false;
                    }
                    if (method.getName().equals("setOp")) {
                        return null;
                    }
                    if (method.getName().equals("getServer")) {
                        return null;
                    }
                    return null;
                }
        );
    }
}
