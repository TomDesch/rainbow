package io.stealingdapenta.config;

public enum PermissionNode {
    RAINBOW_RELOAD("rainbow.reload"),
    RAINBOW_USE("rainbow.use");

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
