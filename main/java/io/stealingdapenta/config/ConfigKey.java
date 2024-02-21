package io.stealingdapenta.config;

import io.stealingdapenta.rainbow.Rainbow;

public enum ConfigKey {
    CYCLE_SPEED("cycle-speed", "5"),
    PLUGIN_RELOADED_MESSAGE("plugin-reloaded-message", "Rainbow armor plugin reloaded."),
    NO_PERMISSION_MESSAGE("no-permission-message", "You don't have permission to use this command."),
    ARMOR_ENABLED_MESSAGE("armor-enabled-message", "Rainbow armor enabled."),
    ARMOR_DISABLED_MESSAGE("armor-disabled-message", "Rainbow armor disabled."),
    NO_EMPTY_SPACES_MESSAGE("no-empty-spaces-message", "You must have empty armor slots in order to use rainbow armor."),
    ARMOR_REMOVED_MESSAGE("no-empty-spaces-message", "Your rainbow armor was removed!");


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

    public String getStringValue() {
        return Rainbow.getInstance()
                      .getConfig()
                      .getString(this.getKey());
    }

    public int getIntValue() {
        return Rainbow.getInstance()
                      .getConfig()
                      .getInt(this.getKey());
    }

    @Override
    public String toString() {
        return "ConfigKey{" +
                "key='" + key + '\'' +
                '}';
    }
}

