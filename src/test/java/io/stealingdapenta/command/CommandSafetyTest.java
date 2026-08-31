package io.stealingdapenta.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.stealingdapenta.RainbowReloadCommand;
import io.stealingdapenta.config.ConfigKey;
import io.stealingdapenta.rainbow.Rainbow;
import java.lang.reflect.Field;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class CommandSafetyTest {

    @Test
    void rainbowCommandHandlesConsoleSender() throws Exception {
        installPluginInstance();

        Command command = new Command("rainbow") {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };

        assertTrue(new RainbowCommand().onCommand(mock(CommandSender.class), command, "rainbow", new String[0]));
    }

    @Test
    void rainbowReloadCommandHandlesConsoleSender() throws Exception {
        installPluginInstance();

        Command command = new Command("rainbowreload") {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };

        assertTrue(new RainbowReloadCommand().onCommand(mock(CommandSender.class), command, "rainbowreload", new String[0]));
    }

    @Test
    void armorRemovedMessageUsesCorrectConfigKey() {
        assertEquals("armor-removed-message", ConfigKey.ARMOR_REMOVED_MESSAGE.getKey());
    }

    private static void installPluginInstance() throws Exception {
        Rainbow plugin = mock(Rainbow.class);
        YamlConfiguration config = new YamlConfiguration();
        for (ConfigKey key : ConfigKey.values()) {
            config.set(key.getKey(), key.getDefaultValue());
        }
        when(plugin.getConfig()).thenReturn(config);

        Field field = Rainbow.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, plugin);
    }
}
