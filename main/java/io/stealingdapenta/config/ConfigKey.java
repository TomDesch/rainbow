package io.stealingdapenta.config;

public enum ConfigKey {
    CYCLE_SPEED("cycle-speed", "defaultValue"),
    PLUGIN_RELOADED_MESSAGE("plugin-reloaded-message", "Rainbow armor plugin reloaded."),
    NO_PERMISSION_MESSAGE("no-permission-message", "You don't have permission to use this command.");

    private final String key;
    private final String defaultValue;

    ConfigKey(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }


    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return "ConfigKey{" +
                "key='" + key + '\'' +
                '}';
    }
}

