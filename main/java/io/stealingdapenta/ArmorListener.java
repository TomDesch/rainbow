package io.stealingdapenta;

import static io.stealingdapenta.config.ConfigKey.ARMOR_REMOVED_MESSAGE;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArmorListener implements Listener {

    public static ArrayList<String> playersWearingRainbowArmor = new ArrayList<>();
    public static ArrayList<String> playersWearingItemBoundRainbowArmor = new ArrayList<>();

    public static final ItemStack AIR_ITEM = new ItemStack(Material.AIR);
    public static final ItemStack[] AIR_ARMOR = {AIR_ITEM, AIR_ITEM, AIR_ITEM, AIR_ITEM};

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
     * Listener for when a player interacts (e.g., right-clicks). If the player is wearing rainbow armor, prevent them from equipping new armor via right-click from either hand or cursor.
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

        if (isLeatherArmorItem(handItem) || isLeatherArmorItem(cursorItem)) {
            event.setCancelled(true);
        }
    }


    /**
     * Listener for when a player clicks inside any inventory.
     * <p>
     * If the player is wearing rainbow armor, this prevents all possible ways to remove or replace armor, including:
     * <ul>
     *   <li>Clicking or picking up items from armor slots</li>
     *   <li>Shift-clicking armor to auto-equip</li>
     *   <li>Placing armor from the cursor</li>
     *   <li>Dragging and dropping armor</li>
     *   <li>Using number keys to swap hot bar items into armor slots</li>
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

        // Block all direct interaction with armor slots (pickup, place, drag, etc.)
        if (event.getSlotType() == SlotType.ARMOR) {
            event.setCancelled(true);
            return;
        }

        // Block placing armor manually from the cursor into any inventory slot
        ItemStack cursor = event.getCursor();
        if (isLeatherArmorItem(cursor)) {
            event.setCancelled(true);
            return;
        }

        // Block shift-clicking armor into the player inventory (where it could equip)
        ItemStack clickedItem = event.getCurrentItem();
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && isLeatherArmorItem(clickedItem)) {
            event.setCancelled(true);
            return;
        }

        // Prevent placing armor with drag-drop interaction
        if (event.getAction() == InventoryAction.PLACE_ALL && isLeatherArmorItem(cursor)) {
            event.setCancelled(true);
        }

        // Block number key hot bar swaps
        if (event.getClick()
                 .isKeyboardClick()) {
            event.setCancelled(true);
        }
    }


    /**
     * Checks whether the given item is a piece of leather armor.
     *
     * @param item The item to check.
     * @return True if the item is a leather helmet, chestplate, leggings, or boots; false otherwise.
     */
    private boolean isLeatherArmorItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        String type = item.getType()
                          .name();
        return type.endsWith("LEATHER_HELMET") || type.endsWith("LEATHER_CHESTPLATE") || type.endsWith("LEATHER_LEGGINGS") || type.endsWith("LEATHER_BOOTS");
    }
}
