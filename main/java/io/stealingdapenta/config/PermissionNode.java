package io.stealingdapenta.config;

public enum PermissionNode {
    RAINBOW_RELOAD("rainbow.reload");

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
