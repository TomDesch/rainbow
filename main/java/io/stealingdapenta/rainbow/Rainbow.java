package io.stealingdapenta.rainbow;

import static io.stealingdapenta.ArmorListener.AIR_ARMOR;
import static io.stealingdapenta.ArmorListener.playersWearingRainbowArmor;

import io.stealingdapenta.ArmorListener;
import io.stealingdapenta.RainbowReloadCommand;
import io.stealingdapenta.animator.TaggedArmorAnimator;
import io.stealingdapenta.command.RainbowCommand;
import io.stealingdapenta.command.RainbowItemCommand;
import io.stealingdapenta.config.ConfigKey;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Main class for the RainbowArmor plugin. Handles lifecycle events, command and event registration, and configuration setup.
 */
public class Rainbow extends JavaPlugin {

    public static Logger logger;
    private static final String PLAYER_NOT_FOUND = "Player %s not found but should exist!";
    private static final String PLUGIN_ENABLED = "Rainbow armor plugin enabled.";
    private static final String PLUGIN_DISABLED = "Rainbow armor plugin disabled.";
    private static Rainbow instance;

    private final ArmorListener armorListener = new ArmorListener();
    private final RainbowCommand rainbowCommand = new RainbowCommand();
    private final RainbowReloadCommand reloadCommand = new RainbowReloadCommand();
    private final RainbowItemCommand rainbowItemCommand = new RainbowItemCommand();

    private BukkitTask armorAnimatorTask;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        initializeConfiguration();

        getServer().getPluginManager()
                   .registerEvents(armorListener, this);

        registerCommand("rainbow", rainbowCommand);
        registerCommand("rainbowreload", reloadCommand);
        registerCommand("rainbowitem", rainbowItemCommand);

        startItemBoundArmorAnimator();

        logger.info(PLUGIN_ENABLED);
    }

    public void startItemBoundArmorAnimator() {
        if (ConfigKey.ARMOR_ITEM_FEATURE.asBoolean()) {
            if (armorAnimatorTask != null) {
                armorAnimatorTask.cancel();
            }
            logger.warning("Rainbow item feature is enabled in the config. Starting the animator.");
            armorAnimatorTask = new TaggedArmorAnimator().runTaskTimer(this, 0, 1L);
        }
    }

    @Override
    public void onDisable() {
        playersWearingRainbowArmor.forEach(playerName -> {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                player.getInventory()
                      .setArmorContents(AIR_ARMOR);
            } else {
                logger.warning(PLAYER_NOT_FOUND.formatted(playerName));
            }
        });

        if (armorAnimatorTask != null) {
            armorAnimatorTask.cancel();
        }

        logger.info(PLUGIN_DISABLED);
    }

    private void initializeConfiguration() {
        Arrays.stream(ConfigKey.values())
              .forEach(this::addDefaultValue);
        getConfig().options()
                   .copyDefaults(true);
        saveConfig();
    }

    private void addDefaultValue(ConfigKey configKey) {
        getConfig().addDefault(configKey.getKey(), configKey.getDefaultValue());
    }

    private void registerCommand(String commandName, Object executor) {
        if (getCommand(commandName) != null) {
            Objects.requireNonNull(getCommand(commandName))
                   .setExecutor((org.bukkit.command.CommandExecutor) executor);
        } else {
            logger.warning("Command not found in plugin.yml: " + commandName);
        }
    }

    public static Rainbow getInstance() {
        return instance;
    }
}
