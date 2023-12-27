package io.stealingdapenta;

import static io.stealingdapenta.ArmorListener.AIR_ARMOR;
import static io.stealingdapenta.ArmorListener.playersWearingRainbowArmor;
import static io.stealingdapenta.rainbow.Rainbow.CYCLE_SPEED;

import io.stealingdapenta.rainbow.Rainbow;
import java.util.function.Consumer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Armor extends BukkitRunnable {

    private final Player player;
    private int count = 0;
    private static final int THRESHOLD = 5000;
    private final int cycleSpeed = Rainbow.getInstance().getConfig().getInt(CYCLE_SPEED);


    public Armor(Player player) {
        this.player = player;
    }

    public void run() {
        PlayerInventory playerInventory = player.getInventory();

        if (!playersWearingRainbowArmor.contains(player.getName())) {
            playerInventory.setArmorContents(AIR_ARMOR);
            cancel();
            return;
        }

        Color armorColor = convertCountToRGB(count);
        setArmor(playerInventory, armorColor);
        count += cycleSpeed;

        // Reset count to keep it within a reasonable range
        if (count >= Integer.MAX_VALUE - THRESHOLD) {
            count = 0;
        }
    }

    private void setArmor(PlayerInventory playerInventory, Color armorColor) {
        setArmor(playerInventory::setHelmet, Material.LEATHER_HELMET, armorColor);
        setArmor(playerInventory::setChestplate, Material.LEATHER_CHESTPLATE, armorColor);
        setArmor(playerInventory::setLeggings, Material.LEATHER_LEGGINGS, armorColor);
        setArmor(playerInventory::setBoots, Material.LEATHER_BOOTS, armorColor);
    }

    private void setArmor(Consumer<ItemStack> setter, Material material, Color armorColor) {
        setter.accept(createColoredArmor(material, armorColor));
    }

    private ItemStack createColoredArmor(Material material, Color armorColor) {
        ItemStack item = new ItemStack(material, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(armorColor);
        item.setItemMeta(meta);
        return item;
    }

    private Color convertCountToRGB(int count) {
        int red = (int) (Math.sin(count * 0.01) * 127 + 128);
        int green = (int) (Math.sin(count * 0.01 + 2) * 127 + 128);
        int blue = (int) (Math.sin(count * 0.01 + 4) * 127 + 128);
        return Color.fromRGB(red, green, blue);
    }
}