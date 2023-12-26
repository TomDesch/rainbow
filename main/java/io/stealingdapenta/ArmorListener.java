package io.stealingdapenta;

import java.util.ArrayList;
import java.util.Arrays;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArmorListener implements Listener {

    public static final ArrayList<String> playersWearingRainbowArmor = new ArrayList<>();
    private static final String ARMOR_REMOVED = "Your rainbow armor was removed!";

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
            player.sendMessage(Component.text(ARMOR_REMOVED));

            PlayerInventory playerInventory = player.getInventory();
            event.getDrops()
                 .removeAll(Arrays.asList(playerInventory.getLeggings(), playerInventory.getBoots(), playerInventory.getChestplate(),
                         playerInventory.getHelmet()));
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && clickedInventory.getType() != InventoryType.PLAYER && playersWearingRainbowArmor.contains(player.getName())
                && event.getRawSlot() >= 5 && event.getRawSlot() <= 8) {
            event.setCancelled(true);
        }
    }
}
