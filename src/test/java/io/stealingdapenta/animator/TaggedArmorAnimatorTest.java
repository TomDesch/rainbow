package io.stealingdapenta.animator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.stealingdapenta.ArmorPieceFactory;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.junit.jupiter.api.Test;

class TaggedArmorAnimatorTest {

    @Test
    void advancesColorBySpeedEachTick() {
        assertEquals(101, TaggedArmorAnimator.advanceColorCount(100, 1));
        assertEquals(120, TaggedArmorAnimator.advanceColorCount(100, 20));
        assertEquals(200, TaggedArmorAnimator.advanceColorCount(100, 100));
    }

    @Test
    void resetsColorCounterWhenNextCountCrossesOverflowThreshold() {
        assertEquals(0, TaggedArmorAnimator.advanceColorCount(Integer.MAX_VALUE - 5004, 5));
    }

    @Test
    void animatePlayerColorsBothMetadataFamiliesAndSkipsUnsupportedMetadata() {
        for (Class<? extends ItemMeta> family : List.of(ColorableArmorMeta.class, LeatherArmorMeta.class, ItemMeta.class)) {
            TaggedItem item = new TaggedItem(family);

            new TaggedArmorAnimator().animatePlayer(player(new ItemStack[4], new ItemStack[]{item}, null));

            boolean supported = family != ItemMeta.class;
            assertEquals(supported ? 5 : 0, item.values.get(ArmorPieceFactory.getColorCountKey()));
            assertEquals(supported ? AnimatorUtil.convertCountToRGB(0) : null, item.color);
        }
    }

    private static Player player(ItemStack[] armor, ItemStack[] storage, ItemStack offhand) {
        PlayerInventory inventory = (PlayerInventory) Proxy.newProxyInstance(PlayerInventory.class.getClassLoader(),
                new Class<?>[]{PlayerInventory.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getArmorContents" -> armor;
                    case "getStorageContents" -> storage;
                    case "getItemInOffHand" -> offhand;
                    case "getContents" -> {
                        ItemStack[] contents = new ItemStack[storage.length + armor.length + 1];
                        System.arraycopy(storage, 0, contents, 0, storage.length);
                        System.arraycopy(armor, 0, contents, storage.length, armor.length);
                        contents[contents.length - 1] = offhand;
                        yield contents;
                    }
                    default -> throw new AssertionError("Unexpected inventory call: " + method);
                });
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getInventory" -> inventory;
                    case "getItemOnCursor" -> null;
                    default -> throw new AssertionError("Unexpected player call: " + method);
                });
    }

    private static final class TaggedItem extends ItemStack {
        private final Map<NamespacedKey, Object> values = new HashMap<>();
        private final ItemMeta meta;
        private Color color;

        private TaggedItem(Class<? extends ItemMeta> family) {
            super(Material.LEATHER_HORSE_ARMOR);
            values.put(ArmorPieceFactory.getArmorTagKey(), (byte) 1);
            values.put(ArmorPieceFactory.getCycleSpeedKey(), 5);
            values.put(ArmorPieceFactory.getColorCountKey(), 0);
            PersistentDataContainer container = (PersistentDataContainer) Proxy.newProxyInstance(
                    PersistentDataContainer.class.getClassLoader(), new Class<?>[]{PersistentDataContainer.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "has" -> values.containsKey(args[0]);
                        case "getOrDefault" -> values.getOrDefault(args[0], args[2]);
                        case "set" -> { values.put((NamespacedKey) args[0], args[2]); yield null; }
                        default -> throw new AssertionError("Unexpected PDC call: " + method);
                    });
            meta = (ItemMeta) Proxy.newProxyInstance(family.getClassLoader(), new Class<?>[]{family},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "getPersistentDataContainer" -> container;
                        case "setColor" -> { color = (Color) args[0]; yield null; }
                        default -> throw new AssertionError("Unexpected metadata call: " + method);
                    });
        }

        @Override
        public ItemMeta getItemMeta() {
            return meta;
        }

        @Override
        public boolean setItemMeta(ItemMeta itemMeta) {
            return itemMeta == meta;
        }
    }
}
