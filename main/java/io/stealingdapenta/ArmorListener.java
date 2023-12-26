package io.stealingdapenta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

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
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory()
                                     .getItemInMainHand();

        if (playersWearingRainbowArmor.contains(player.getName()) && isRainbowArmor(itemInHand) && event.getAction() == Action.RIGHT_CLICK_AIR) {
            event.setCancelled(true);
        }
    }

    private boolean isRainbowArmor(ItemStack item) {
        return item.getType()
                   .name()
                   .startsWith("LEATHER_") && item.hasItemMeta() && item.getItemMeta() instanceof LeatherArmorMeta;
    }


    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (playersWearingRainbowArmor.contains(player.getName()) && Objects.nonNull(clickedInventory) && clickedInventory.getType() == InventoryType.PLAYER
                && isArmorSlot(event.getRawSlot()) && isInvalidClickType(event.getClick())) {
            event.setCancelled(true);
        }
    }

    private boolean isInvalidClickType(ClickType clickType) {
        return clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT || clickType == ClickType.DOUBLE_CLICK
                || clickType == ClickType.NUMBER_KEY;
    }


    private boolean isArmorSlot(int slot) {
        return slot >= 5 && slot <= 8;
    }
}
