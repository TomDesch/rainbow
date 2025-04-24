package io.stealingdapenta.config;

import io.stealingdapenta.rainbow.Rainbow;
import io.stealingdapenta.util.TextUtil;
import net.kyori.adventure.text.TextComponent;

public enum ConfigKey {
    CYCLE_SPEED("cycle-speed", 5),
    UNUSED_MESSAGE_EXAMPLE("formatted-example-message",
                           "To format your messages, use &(r,g,b) format, for example &(123,456,789) to set colors, " + "and mix it up with decorators like &b &o &u &i &s to make your text bold, obfuscated, underlined, italic or strikethrough :) "
                                   + "Use &r to reset decorations."),
    PLUGIN_RELOADED_MESSAGE("plugin-reloaded-message", "&(75,255,75)Rainbow armor plugin reloaded."),
    NO_PERMISSION_MESSAGE("no-permission-message", "&(255,0,0)You don't have permission to use this command."),
    PLAYERS_ONLY_MESSAGE("players-only-message", "&(255,0,0)This command can only be used by players."),
    INVALID_CYCLE_SPEED_MESSAGE("invalid-cycle-speed", "§cInvalid cycle speed. Please enter a positive number."),
    ARMOR_ENABLED_MESSAGE("armor-enabled-message", "&(75,255,75)Rainbow armor enabled."),
    ARMOR_DISABLED_MESSAGE("armor-disabled-message", "&(249,255,68)Rainbow armor disabled."),
    FEATURE_DISABLED_MESSAGE("feature-disabled-message", "&(249,255,68)This feature is disabled. Please contact the server administrator if this is an error."),
    NO_EMPTY_SPACES_MESSAGE("no-empty-spaces-message", "&(255,0,0)You must have empty armor slots in order to use rainbow armor."),
    NOT_ENOUGH_EMPTY_SPACES_MESSAGE("not-enough-empty-spaces-message", "&(255,0,0)You must have 4 empty inventory slots in order to use rainbow armor."),
    ARMOR_REMOVED_MESSAGE("no-empty-spaces-message", "Your rainbow armor was removed!"),
    ARMOR_ITEM_FEATURE("armor-item-feature", true),
    CHECK_PLAYER_INVENTORY("check-player-inventory", true),
    CHECK_BLOCK_INVENTORIES("check-block-inventories", false);


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

    public TextComponent getFormattedMessage() {
        return textUtil.parseFormattedString(asString());
    }

    private String asString() {
        return Rainbow.getInstance()
                      .getConfig()
                      .getString(getKey());
    }

    public int asInt() {
        return Rainbow.getInstance()
                      .getConfig()
                      .getInt(getKey());
    }

    public boolean asBoolean() {
        return Rainbow.getInstance()
                      .getConfig()
                      .getBoolean(getKey(), (Boolean) defaultValue);
    }

    @Override
    public String toString() {
        return "ConfigKey{" + "key='" + key + '\'' + '}';
    }
}

