package io.stealingdapenta.config;

import io.stealingdapenta.rainbow.Rainbow;
import io.stealingdapenta.util.TextUtil;
import net.kyori.adventure.text.TextComponent;

public enum ConfigKey {
    CYCLE_SPEED("cycle-speed", 5),
    UNUSED_MESSAGE_EXAMPLE("formatted-example-message",
                           "To format your messages, use &(r,g,b) format, for example &(123,456,789) to set colors, "
                                   + "and mix it up with decorators like &b &o &u &i &s to make your text bold, obfuscated, underlined, italic or strikethrough :) "
                                   + "Use &r to reset decorations."),
    PLUGIN_RELOADED_MESSAGE("plugin-reloaded-message", "&(75,255,75)Rainbow armor plugin reloaded."),
    NO_PERMISSION_MESSAGE("no-permission-message", "&(255,0,0)You don't have permission to use this command."),
    ARMOR_ENABLED_MESSAGE("armor-enabled-message", "&(75,255,75)Rainbow armor enabled."),
    ARMOR_DISABLED_MESSAGE("armor-disabled-message", "&(249,255,68)Rainbow armor disabled."),
    NO_EMPTY_SPACES_MESSAGE("no-empty-spaces-message", "&(255,0,0)You must have empty armor slots in order to use rainbow armor."),
    NOT_ENOUGH_EMPTY_SPACES_MESSAGE("not-enough-empty-spaces-message", "&(255,0,0)You must have 4 empty inventory slots in order to use rainbow armor."),
    ARMOR_REMOVED_MESSAGE("no-empty-spaces-message", "Your rainbow armor was removed!");


    private final String key;
    private final Object defaultValue;

    private static final TextUtil textUtil = TextUtil.getInstance();

    ConfigKey(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }


    public String getKey() {
        return key;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Object getValue() {
        if (defaultValue instanceof String) {
            return getStringValue();
        } else if (defaultValue instanceof Integer) {
            return getIntValue();
        }
        return 0;
    }

    public TextComponent getFormattedMessage() {
        return textUtil.parseFormattedString(getStringValue());
    }

    private String getStringValue() {
        return Rainbow.getInstance()
                      .getConfig()
                      .getString(getKey());
    }

    private int getIntValue() {
        return Rainbow.getInstance()
                      .getConfig()
                      .getInt(getKey());
    }

    @Override
    public String toString() {
        return "ConfigKey{" +
                "key='" + key + '\'' +
                '}';
    }
}

