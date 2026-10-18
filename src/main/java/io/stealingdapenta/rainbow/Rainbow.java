package io.stealingdapenta.rainbow;

import io.stealingdapenta.ArmorListener;
import io.stealingdapenta.RainbowReloadCommand;
import io.stealingdapenta.animator.BoundArmorTasks;
import io.stealingdapenta.animator.TaggedArmorAnimator;
import io.stealingdapenta.command.RainbowCommand;
import io.stealingdapenta.command.RainbowHorseItemCommand;
import io.stealingdapenta.command.RainbowItemCommand;
import io.stealingdapenta.config.ConfigKey;
import io.stealingdapenta.config.ConfigurationDefaults;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Main class for the RainbowArmor plugin. Handles lifecycle events, command and event registration, and configuration setup.
 */
public class Rainbow extends JavaPlugin {

    public static Logger logger;
    private static final String PLUGIN_ENABLED = "Rainbow armor plugin enabled.";
    private static final String PLUGIN_DISABLED = "Rainbow armor plugin disabled.";
    private static Rainbow instance;

    private final ArmorListener armorListener = new ArmorListener();
    private final RainbowCommand rainbowCommand = new RainbowCommand();
    private final RainbowReloadCommand reloadCommand = new RainbowReloadCommand();
    private final RainbowItemCommand rainbowItemCommand = new RainbowItemCommand();
    private final RainbowHorseItemCommand rainbowHorseItemCommand = new RainbowHorseItemCommand();

    private BukkitTask armorAnimatorTask;
    private final BoundArmorTasks boundArmorTasks = new BoundArmorTasks(animator -> animator.runTaskTimer(this, 0L, 1L));

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
        registerCommand("rainbowhorseitem", rainbowHorseItemCommand);

        startItemBoundArmorAnimator();

        logger.info(PLUGIN_ENABLED);
    }

    public void startItemBoundArmorAnimator() {
        if (ConfigKey.ARMOR_ITEM_FEATURE.asBoolean()) {
            if (armorAnimatorTask != null) {
                armorAnimatorTask.cancel();
            }
            logger.info("Rainbow item feature is enabled in the config. Starting the animator.");
            armorAnimatorTask = new TaggedArmorAnimator().runTaskTimer(this, 0, TaggedArmorAnimator.TICK_PERIOD);
        } else {
            logger.info("Rainbow item feature is disabled in the config. If you believe this is a mistake, please check the config.yml file.");
        }
    }

    @Override
    public void onDisable() {
        boundArmorTasks.stopAll();

        if (armorAnimatorTask != null) {
            armorAnimatorTask.cancel();
        }

        logger.info(PLUGIN_DISABLED);
    }

    private void initializeConfiguration() {
        try {
            ConfigurationDefaults.saveDefaultIfMissing(getDataFolder().toPath().resolve("config.yml"), getResource("config.yml"));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not save default config.yml.", exception);
        }
        ConfigurationDefaults.addDefaults(getConfig(), ConfigKey.values());
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

    public BoundArmorTasks getBoundArmorTasks() {
        return boundArmorTasks;
    }
}
