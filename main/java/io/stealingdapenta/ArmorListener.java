package io.stealingdapenta;

import static io.stealingdapenta.config.ConfigKey.ARMOR_REMOVED_MESSAGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArmorListener implements Listener {

    public static final ArrayList<String> playersWearingRainbowArmor = new ArrayList<>();

    public static final ItemStack AIR_ITEM = new ItemStack(Material.AIR);
    public static final ItemStack[] AIR_ARMOR = {AIR_ITEM, AIR_ITEM, AIR_ITEM, AIR_ITEM};

    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (playersWearingRainbowArmor.remove(player.getName())) {
            PlayerInventory playerInventory = player.getInventory();
            playerInventory.setArmorContents(AIR_ARMOR);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (playersWearingRainbowArmor.remove(player.getName())) {
            player.sendMessage(ARMOR_REMOVED_MESSAGE.getFormattedMessage());

            PlayerInventory playerInventory = player.getInventory();
            event.getDrops()
                 .removeAll(Arrays.asList(playerInventory.getLeggings(), playerInventory.getBoots(), playerInventory.getChestplate(),
                                          playerInventory.getHelmet()));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory()
                                     .getItemInMainHand();

        if (playersWearingRainbowArmor.contains(player.getName()) && isArmorItem(itemInHand)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (Objects.isNull(clickedInventory) || !playersWearingRainbowArmor.contains(player.getName())) {
            return;
        }

        // Cancel event if the player is wearing rainbow armor and clicks an armor slot
        if (event.getSlotType()
                 .equals(SlotType.ARMOR)) {
            event.setCancelled(true);
        }

        // Additional check for shift-clicking armor in any slot
        if (isPlayerInventory(clickedInventory)
                && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                && isArmorItem(event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }

    private boolean isPlayerInventory(Inventory inventory) {
        return inventory.getType()
                        .equals(InventoryType.PLAYER);
    }

    private boolean isArmorItem(ItemStack item) {
        return Objects.nonNull(item)
                && item.getType()
                       .name()
                       .endsWith("_HELMET")
                || item.getType()
                       .name()
                       .endsWith("_CHESTPLATE")
                || item.getType()
                       .name()
                       .endsWith("_LEGGINGS")
                || item.getType()
                       .name()
                       .endsWith("_BOOTS");
    }

}
