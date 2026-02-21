package io.stealingdapenta;

import static io.stealingdapenta.config.ConfigKey.ARMOR_REMOVED_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.ARMOR_SHIFT_CLICK_BLOCKED_MESSAGE;
import static io.stealingdapenta.config.ConfigKey.CHECK_HORSES;
import static io.stealingdapenta.rainbow.Rainbow.logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class ArmorListener implements Listener {

    public static ArrayList<String> playersWearingRainbowArmor = new ArrayList<>();

    public static final ItemStack AIR_ITEM = new ItemStack(Material.AIR);
    public static final ItemStack[] AIR_ARMOR = {AIR_ITEM, AIR_ITEM, AIR_ITEM, AIR_ITEM};

    // Raw slot indices for armor slots in the player inventory crafting view.
    // In Paper 1.21 the player inventory raw slots are: 5=helmet, 6=chestplate, 7=leggings, 8=boots.
    // NOTE: raw slots 36-39 are the HOTBAR, not armor — do NOT include them here.
    private static final Set<Integer> ARMOR_RAW_SLOTS = Set.of(5, 6, 7, 8);

    private boolean isArmorSlot(InventoryClickEvent event) {
        return event.getSlotType() == SlotType.ARMOR || ARMOR_RAW_SLOTS.contains(event.getRawSlot());
    }

    /**
     * Listener for when a player leaves the server. If the player is wearing rainbow armor, it is removed to prevent it from persisting.
     *
     * @param event The quit event triggered when a player leaves the server.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (playersWearingRainbowArmor.remove(player.getName())) {
            player.getInventory()
                  .setArmorContents(AIR_ARMOR);
        }
    }

    /**
     * Listener for when a player dies. If the player is wearing rainbow armor, it is removed from the drop list and the player is notified that their rainbow armor has been removed.
     *
     * @param event The death event triggered when a player dies.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (playersWearingRainbowArmor.remove(player.getName())) {
            player.sendMessage(ARMOR_REMOVED_MESSAGE.getFormattedMessage());

            PlayerInventory inv = player.getInventory();
            event.getDrops()
                 .removeAll(Arrays.asList(inv.getHelmet(), inv.getChestplate(), inv.getLeggings(), inv.getBoots()));
        }
    }

    /**
     * Listener for when a player interacts (e.g., right-clicks).
     * If the player is wearing rainbow armor, prevent them from right-click-equipping <em>any</em>
     * armor piece from their hand or cursor.
     * Right-clicking an armor piece auto-equips it into the
     * armor slot, which would swap out (and destroy) the rainbow piece.
     * Non-armor items are unaffected.
     *
     * @param event The interaction event triggered when a player right- or left-clicks.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!playersWearingRainbowArmor.contains(player.getName())) {
            return;
        }

        ItemStack handItem = player.getInventory()
                                   .getItemInMainHand();
        ItemStack cursorItem = player.getItemOnCursor();

        // Block right-click equip for ANY armor piece — equipping any armor swaps out the rainbow piece
        if (isArmorItem(handItem) || isArmorItem(cursorItem)) {
            event.setCancelled(true);
            player.sendMessage(ARMOR_SHIFT_CLICK_BLOCKED_MESSAGE.getFormattedMessage());
        }
    }


    /**
     * Listener for when a player clicks inside any inventory.
     * <p>
     * While wearing rainbow armor, the following rules apply:
     * <ul>
     *   <li><b>Armor slots are fully locked</b> — nothing may be picked up from or placed into them,
     *       preventing both duplication and destruction of the rainbow pieces.</li>
     *   <li><b>Shift-clicking any armor item is blocked</b> — Minecraft always routes it to the
     *       nearest matching armor slot, which would overwrite the rainbow piece.</li>
     *   <li><b>Hot bar number-key swaps involving armor are blocked</b> — same overwrite risk.</li>
     *   <li><b>Everything else is allowed</b> — picking up, placing, or moving non-armor items
     *       between any slots (bag, chest, hot bar) is completely safe.</li>
     * </ul>
     *
     * @param event The inventory click event triggered by any click in an inventory.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!playersWearingRainbowArmor.contains(player.getName())) {
            return;
        }

        logger.fine("[DEBUG CLICK] player=" + player.getName() + " slotType=" + event.getSlotType() + " rawSlot=" + event.getRawSlot() + " slot=" + event.getSlot() + " action=" + event.getAction() + " click=" + event.getClick() + " item=" + (
                event.getCurrentItem() != null ? event.getCurrentItem()
                                                      .getType() : "null") + " cursor=" + event.getCursor()
                                                                                               .getType());

        // BLOCK: armor slots are fully locked — picking up or placing anything here would dupe or destroy the rainbow piece.
        if (isArmorSlot(event)) {
            event.setCancelled(true);
            player.updateInventory();
            return;
        }

        // BLOCK: shift-clicking any armor item always auto-equips it, which would overwrite a rainbow slot.
        // Explicitly restore the item so creative mode doesn't consume it on cancel.
        ItemStack clickedItem = event.getCurrentItem();
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && isArmorItem(clickedItem)) {
            event.setCancelled(true);
            if (event.getClickedInventory() != null) {
                event.getClickedInventory()
                     .setItem(event.getSlot(), clickedItem);
            }
            player.updateInventory();
            player.sendMessage(ARMOR_SHIFT_CLICK_BLOCKED_MESSAGE.getFormattedMessage());
            return;
        }

        // BLOCK: hot bar number-key swap where either side is armor — would equip or displace a rainbow slot.
        // ALLOW: hot bar swaps where neither side is armor — safe to move freely.
        if (event.getClick()
                 .isKeyboardClick()) {
            int hotbarButton = event.getHotbarButton();
            if (hotbarButton >= 0) {
                ItemStack hotbarItem = player.getInventory()
                                             .getItem(hotbarButton);
                if (isArmorItem(clickedItem) || isArmorItem(hotbarItem)) {
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
        }

        // ALLOW (implicit): all other clicks — picking up, placing, and moving non-armor items anywhere is safe.
    }

    /**
     * Listener for inventory drag events.
     * <p>
     * While wearing rainbow armor, dragging onto an armor slot is blocked — it would overwrite the rainbow piece. Dragging armor or non-armor items onto any other slot is freely allowed.
     *
     * @param event The inventory drag event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!playersWearingRainbowArmor.contains(player.getName())) {
            return;
        }

        logger.fine("[DEBUG DRAG] player=" + player.getName() + " rawSlots=" + event.getRawSlots() + " item=" + event.getOldCursor()
                                                                                                                     .getType());

        // BLOCK: dragging onto an armor slot would overwrite the rainbow piece in that slot.
        // ALLOW (implicit): dragging onto any non-armor slot is safe regardless of item type.
        for (int rawSlot : event.getRawSlots()) {
            if (event.getView()
                     .getSlotType(rawSlot) == SlotType.ARMOR || ARMOR_RAW_SLOTS.contains(rawSlot)) {
                event.setCancelled(true);
                player.updateInventory();
                return;
            }
        }
    }



    /**
     * Checks whether the given item is a piece of armor.
     *
     * @param item The item to check.
     * @return True if the item is a helmet, chestplate, leggings, boots, or leather horse armor; false otherwise.
     */
    private boolean isArmorItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        String type = item.getType()
                          .name();
        return type.endsWith("HELMET") || type.endsWith("CHESTPLATE") || type.endsWith("LEGGINGS") || type.endsWith("BOOTS") || isLeatherHorseArmor(item);
    }

    /**
     * Checks whether the given item is leather horse armor. Will default to false if the feature is disabled in the config.
     *
     * @param item the item to check
     * @return true if the item is leather horse armor, false otherwise or false if the feature is disabled
     */
    private boolean isLeatherHorseArmor(@NotNull ItemStack item) {
        if (!CHECK_HORSES.asBoolean()) {
            return false;
        }

        String type = item.getType()
                          .name();
        return type.contains("LEATHER_HORSE_ARMOR");
    }
}
