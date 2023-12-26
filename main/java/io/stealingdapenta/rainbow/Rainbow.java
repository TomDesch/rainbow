package io.stealingdapenta.rainbow;

import static io.stealingdapenta.ArmorListener.AIR_ARMOR;
import static io.stealingdapenta.ArmorListener.playersWearingRainbowArmor;

import io.stealingdapenta.ArmorListener;
import io.stealingdapenta.RainbowCommand;
import java.util.Objects;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Rainbow extends JavaPlugin {

    public static Logger logger;
    private static Rainbow instance = null;

    private final ArmorListener armorListener = new ArmorListener();
    private final RainbowCommand rainbowCommand = new RainbowCommand();
    private static final String PLAYER_NOT_FOUND = "Player %s not found but should exist!";
    private static final String PLUGIN_ENABLED = "Rainbow armor plugin enabled.";
    private static final String PLUGIN_DISABLED = "Rainbow armor plugin disabled.";
    private static final String RAINBOW_COMMAND = "rainbow";


    public void onEnable() {
        instance = this;
        logger = getLogger();

        getServer().getPluginManager()
                   .registerEvents(armorListener, instance);
        Objects.requireNonNull(getCommand(RAINBOW_COMMAND))
               .setExecutor(rainbowCommand);

        logger.info(PLUGIN_ENABLED);
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