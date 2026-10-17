package io.stealingdapenta;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.stealingdapenta.rainbow.Rainbow;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArmorListenerTest {

    private static final String PLAYER_NAME = "armor-listener-test";

    private final ArmorListener listener = new ArmorListener();

    @BeforeEach
    void setUp() {
        Rainbow.logger = Logger.getAnonymousLogger();
        ArmorListener.playersWearingRainbowArmor.add(PLAYER_NAME);
    }

    @AfterEach
    void tearDown() {
        ArmorListener.playersWearingRainbowArmor.remove(PLAYER_NAME);
        Rainbow.logger = null;
    }

    @Test
    void inventoryClickAllowsContainerSlotFiveWithEmptyCursor() {
        InventoryClickEvent event = new InventoryClickEvent(
                newInventoryView(SlotType.CONTAINER),
                SlotType.CONTAINER,
                5,
                ClickType.LEFT,
                InventoryAction.NOTHING
        );

        listener.inventoryClick(event);

        assertFalse(event.isCancelled());
    }

    @Test
    void inventoryClickBlocksArmorSlot() {
        InventoryClickEvent event = new InventoryClickEvent(
                newInventoryView(SlotType.ARMOR),
                SlotType.ARMOR,
                5,
                ClickType.LEFT,
                InventoryAction.NOTHING
        );

        listener.inventoryClick(event);

        assertTrue(event.isCancelled());
    }

    @Test
    void inventoryDragAllowsContainerSlotFive() {
        Map<Integer, ItemStack> slots = new HashMap<>();
        slots.put(5, null);
        InventoryDragEvent event = new InventoryDragEvent(
                newInventoryView(SlotType.CONTAINER),
                null,
                null,
                true,
                slots
        );

        listener.inventoryDrag(event);

        assertFalse(event.isCancelled());
    }

    @Test
    void inventoryDragBlocksArmorSlot() {
        Map<Integer, ItemStack> slots = new HashMap<>();
        slots.put(5, null);
        InventoryDragEvent event = new InventoryDragEvent(
                newInventoryView(SlotType.ARMOR),
                null,
                null,
                true,
                slots
        );

        listener.inventoryDrag(event);

        assertTrue(event.isCancelled());
    }

    private static InventoryView newInventoryView(SlotType slotType) {
        Player player = newPlayer();
        return (InventoryView) Proxy.newProxyInstance(
                InventoryView.class.getClassLoader(),
                new Class<?>[]{InventoryView.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getPlayer")) {
                        return player;
                    }
                    if (method.getName().equals("getSlotType")) {
                        return slotType;
                    }
                    if (method.getName().equals("convertSlot")) {
                        return args[0];
                    }
                    return null;
                }
        );
    }

    private static Player newPlayer() {
        return (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getName")) {
                        return PLAYER_NAME;
                    }
                    if (method.getName().equals("updateInventory")) {
                        return false;
                    }
                    return null;
                }
        );
    }
}
