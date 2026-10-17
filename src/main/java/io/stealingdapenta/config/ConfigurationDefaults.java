package io.stealingdapenta.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.bukkit.configuration.ConfigurationSection;

public final class ConfigurationDefaults {

    private ConfigurationDefaults() {
    }

    public static void saveDefaultIfMissing(Path configFile, InputStream defaultConfig) throws IOException {
        if (Files.exists(configFile)) {
            if (defaultConfig != null) {
                defaultConfig.close();
            }
            return;
        }
        if (defaultConfig == null) {
            throw new IllegalStateException("Default config.yml resource is missing.");
        }

        Path parent = configFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (InputStream source = defaultConfig) {
            Files.copy(source, configFile);
        }
    }

    public static void addDefaults(ConfigurationSection config, ConfigKey... keys) {
        for (ConfigKey key : keys) {
            config.addDefault(key.getKey(), key.getDefaultValue());
        }
    }
}
