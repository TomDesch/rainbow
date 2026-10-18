package io.stealingdapenta.animator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.stealingdapenta.ArmorPieceFactory;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.IdentityHashMap;
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
import org.bukkit.persistence.PersistentDataType;
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

    @Test
    void animatePlayerAdvancesWornStorageAndOffhandItemsExactlyOnce() {
        TaggedItem worn = new TaggedItem(ColorableArmorMeta.class);
        TaggedItem stored = new TaggedItem(ColorableArmorMeta.class);
        TaggedItem offhand = new TaggedItem(LeatherArmorMeta.class);

        new TaggedArmorAnimator().animatePlayer(player(new ItemStack[]{worn}, new ItemStack[]{stored}, offhand));

        for (TaggedItem item : List.of(worn, stored, offhand)) {
            assertEquals(5, item.values.get(ArmorPieceFactory.getColorCountKey()));
        }
    }

    @Test
    void animatePlayerReadsOneMetadataSnapshotAndPersistsItsColorAndCounter() {
        TaggedItem item = new TaggedItem(ColorableArmorMeta.class);
        item.values.put(ArmorPieceFactory.getArmorTagKey(), (byte) 0);
        item.values.put(ArmorPieceFactory.getCycleSpeedKey(), 17);
        item.values.put(ArmorPieceFactory.getColorCountKey(), 123);
        NamespacedKey unrelatedKey = new NamespacedKey("otherplugin", "sentinel");
        item.values.put(unrelatedKey, "preserve-me");

        new TaggedArmorAnimator().animatePlayer(player(new ItemStack[]{item}, new ItemStack[0], null));

        assertEquals(1, item.metadataReads);
        assertEquals(1, item.metadataWrites);
        assertEquals(140, item.values.get(ArmorPieceFactory.getColorCountKey()));
        assertEquals(AnimatorUtil.convertCountToRGB(123), item.color);
        assertEquals((byte) 0, item.values.get(ArmorPieceFactory.getArmorTagKey()));
        assertEquals(17, item.values.get(ArmorPieceFactory.getCycleSpeedKey()));
        assertEquals("preserve-me", item.values.get(unrelatedKey));
    }

    @Test
    void animatePlayerRejectsWrongTagTypeAndMissingOrNullMetadata() {
        TaggedItem wrongType = new TaggedItem(ColorableArmorMeta.class);
        wrongType.values.put(ArmorPieceFactory.getArmorTagKey(), 1);
        TaggedItem untagged = new TaggedItem(ColorableArmorMeta.class);
        untagged.values.remove(ArmorPieceFactory.getArmorTagKey());
        TaggedItem missingMeta = new TaggedItem(null);
        TaggedItem air = new TaggedItem(ColorableArmorMeta.class, Material.AIR);
        TaggedItem unsupported = new TaggedItem(ItemMeta.class);

        new TaggedArmorAnimator().animatePlayer(player(
                new ItemStack[]{wrongType, untagged, missingMeta, air, unsupported, null}, new ItemStack[0], null));

        for (TaggedItem item : List.of(wrongType, untagged, missingMeta, air, unsupported)) {
            assertEquals(0, item.metadataWrites);
            assertEquals(0, item.values.get(ArmorPieceFactory.getColorCountKey()));
            assertNull(item.color);
        }
        assertEquals(0, air.metadataReads);
    }

    @Test
    void animatePlayerPreservesCursorProtectionAndRequiresByteCursorTag() {
        for (Object cursorTag : List.of((byte) 1, 1)) {
            TaggedItem worn = new TaggedItem(ColorableArmorMeta.class);
            TaggedItem offhand = new TaggedItem(LeatherArmorMeta.class);
            TaggedItem cursor = new TaggedItem(ColorableArmorMeta.class);
            cursor.values.put(ArmorPieceFactory.getArmorTagKey(), cursorTag);

            new TaggedArmorAnimator().animatePlayer(player(
                    new ItemStack[]{worn}, new ItemStack[0], offhand, cursor));

            for (TaggedItem item : List.of(worn, offhand)) {
                assertEquals(cursorTag instanceof Byte ? 0 : 5, item.values.get(ArmorPieceFactory.getColorCountKey()));
                assertEquals(cursorTag instanceof Byte ? 0 : 1, item.metadataWrites);
            }
            assertEquals(0, cursor.metadataWrites);
            assertEquals(0, cursor.values.get(ArmorPieceFactory.getColorCountKey()));
        }
    }

    @Test
    void animatePlayerAcceptsCustomTaggedItemsWithSupportedMetadataWithoutMaterialFiltering() {
        TaggedItem custom = new TaggedItem(ColorableArmorMeta.class, Material.PAPER);
        custom.values.remove(ArmorPieceFactory.getCycleSpeedKey());
        custom.values.remove(ArmorPieceFactory.getColorCountKey());

        new TaggedArmorAnimator().animatePlayer(player(new ItemStack[]{custom}, new ItemStack[0], null));

        assertEquals(5, custom.values.get(ArmorPieceFactory.getColorCountKey()));
        assertEquals(AnimatorUtil.convertCountToRGB(0), custom.color);
        assertEquals(1, custom.metadataReads);
    }

    private static Player player(ItemStack[] armor, ItemStack[] storage, ItemStack offhand) {
        return player(armor, storage, offhand, null);
    }

    private static Player player(ItemStack[] armor, ItemStack[] storage, ItemStack offhand, ItemStack cursor) {
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
                    case "getItemOnCursor" -> cursor;
                    default -> throw new AssertionError("Unexpected player call: " + method);
                });
    }

    private static final class TaggedItem extends ItemStack {
        private final Map<NamespacedKey, Object> values = new HashMap<>();
        private final Map<ItemMeta, MetadataState> snapshots = new IdentityHashMap<>();
        private final Class<? extends ItemMeta> family;
        private Color color;
        private int metadataReads;
        private int metadataWrites;

        private TaggedItem(Class<? extends ItemMeta> family) {
            this(family, Material.LEATHER_HORSE_ARMOR);
        }

        private TaggedItem(Class<? extends ItemMeta> family, Material material) {
            super(material);
            this.family = family;
            values.put(ArmorPieceFactory.getArmorTagKey(), (byte) 1);
            values.put(ArmorPieceFactory.getCycleSpeedKey(), 5);
            values.put(ArmorPieceFactory.getColorCountKey(), 0);
        }

        @Override
        public ItemMeta getItemMeta() {
            metadataReads++;
            if (family == null) {
                return null;
            }
            MetadataState snapshot = new MetadataState(new HashMap<>(values), color);
            PersistentDataContainer container = (PersistentDataContainer) Proxy.newProxyInstance(
                    PersistentDataContainer.class.getClassLoader(), new Class<?>[]{PersistentDataContainer.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "has" -> snapshot.values.containsKey(args[0]) && (args.length == 1
                                || ((PersistentDataType<?, ?>) args[1]).getPrimitiveType().isInstance(snapshot.values.get(args[0])));
                        case "getOrDefault" -> snapshot.values.getOrDefault(args[0], args[2]);
                        case "set" -> { snapshot.values.put((NamespacedKey) args[0], args[2]); yield null; }
                        default -> throw new AssertionError("Unexpected PDC call: " + method);
                    });
            ItemMeta meta = (ItemMeta) Proxy.newProxyInstance(family.getClassLoader(), new Class<?>[]{family},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "getPersistentDataContainer" -> container;
                        case "setColor" -> { snapshot.color = (Color) args[0]; yield null; }
                        case "getColor" -> snapshot.color;
                        default -> throw new AssertionError("Unexpected metadata call: " + method);
                    });
            snapshots.put(meta, snapshot);
            return meta;
        }

        @Override
        public boolean setItemMeta(ItemMeta itemMeta) {
            metadataWrites++;
            MetadataState snapshot = snapshots.get(itemMeta);
            values.clear();
            values.putAll(snapshot.values);
            color = snapshot.color;
            return true;
        }
    }

    private static final class MetadataState {
        private final Map<NamespacedKey, Object> values;
        private Color color;

        private MetadataState(Map<NamespacedKey, Object> values, Color color) {
            this.values = values;
            this.color = color;
        }
    }
}
