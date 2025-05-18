package io.stealingdapenta.config;

public enum PermissionNode {
    RAINBOW_RELOAD("rainbow.reload"),
    RAINBOW_USE("rainbow.use"),
    RAINBOW_ITEM_USE("rainbow.item"),
    RAINBOW_HORSE_ARMOR("rainbow.horse"),
    RAINBOW_WOLF_ARMOR("rainbow.wolf");

    private final String node;

    PermissionNode(String node) {
        this.node = node;
    }

    public String getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "PermissionNode{" +
                "node='" + node + '\'' +
                '}';
    }
}
