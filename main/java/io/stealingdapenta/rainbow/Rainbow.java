package io.stealingdapenta.rainbow;

import static io.stealingdapenta.ArmorListener.AIR_ARMOR;
import static io.stealingdapenta.ArmorListener.playersWearingRainbowArmor;

import io.stealingdapenta.ArmorListener;
import io.stealingdapenta.RainbowReloadCommand;
import io.stealingdapenta.command.RainbowCommand;
import io.stealingdapenta.config.ConfigKey;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Rainbow extends JavaPlugin {

    public static Logger logger;
    private static Rainbow instance = null;
    private final FileConfiguration config = getConfig();
    private final ArmorListener armorListener = new ArmorListener();
    private final RainbowCommand rainbowCommand = new RainbowCommand();
    private final RainbowReloadCommand reloadCommand = new RainbowReloadCommand();
    private static final String PLAYER_NOT_FOUND = "Player %s not found but should exist!";
    private static final String PLUGIN_ENABLED = "Rainbow armor plugin enabled.";
    private static final String PLUGIN_DISABLED = "Rainbow armor plugin disabled.";
    private static final String RAINBOW_COMMAND = "rainbow";
    private static final String RELOAD_COMMAND = "rainbowreload";


    public void onEnable() {
        instance = this;
        logger = getLogger();

        initializeConfiguration();

        getServer().getPluginManager()
                   .registerEvents(armorListener, instance);
        Objects.requireNonNull(getCommand(RAINBOW_COMMAND))
               .setExecutor(rainbowCommand);
        Objects.requireNonNull(getCommand(RELOAD_COMMAND))
               .setExecutor(reloadCommand);

        logger.info(PLUGIN_ENABLED);
    }

    private void initializeConfiguration() {
        initializeDefaultValues();
        config.options()
              .copyDefaults(true);
        saveConfig();
    }

    private void initializeDefaultValues() {
        Arrays.stream(ConfigKey.values()).forEach(this::addDefaultValue);
    }

    private void addDefaultValue(ConfigKey configKey) {
        config.addDefault(configKey.getKey(), configKey.getDefaultValue());
    }

    public void onDisable() {
        playersWearingRainbowArmor.forEach(playerName -> {
            Player player = Bukkit.getPlayer(playerName);
            if (Objects.isNull(player)) {
                logger.warning(PLAYER_NOT_FOUND.formatted(playerName));
            } else {
                player.getInventory()
                      .setArmorContents(AIR_ARMOR);
            }
        });

        logger.info(PLUGIN_DISABLED);
    }

    public static Rainbow getInstance() {
        return instance;
    }
}