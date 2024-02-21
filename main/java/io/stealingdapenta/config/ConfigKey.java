package io.stealingdapenta.config;

public enum ConfigKey {
    CYCLE_SPEED("cycle-speed"),
    PLUGIN_RELOADED_MESSAGE("plugin-reloaded-message"),
    NO_PERMISSION_MESSAGE("no-permission-message");

    private final String key;

    ConfigKey(String key) {
        this.key = key;
    }


    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "ConfigKey{" +
                "key='" + key + '\'' +
                '}';
    }
}

