package io.stealingdapenta;

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
    private static final int MAX_COUNT = 255;
    private int count = 0;

    public Armor(Player player) {
        this.player = player;
    }

    public void run() {
        PlayerInventory playerInventory = player.getInventory();

        if (!ArmorListener.playersWearingRainbowArmor.contains(player.getName())) {
            playerInventory.setArmorContents(ArmorListener.AIR_ARMOR);
            cancel();
            return;
        }

        double normalizedCount = (Math.sin(Math.toRadians(count)) + 1) / 2.0; // Normalize to [0, 1]

        int red = (int) (normalizedCount * 255);
        int green = (int) ((normalizedCount + 1.0 / 3.0) * 255) % 255;
        int blue = (int) ((normalizedCount + 2.0 / 3.0) * 255) % 255;

        setArmor(playerInventory, red, green, blue);
        count++;

        if (count > MAX_COUNT) {
            count = 0;
        }
    }

    private void setArmor(PlayerInventory playerInventory, int red, int green, int blue) {
        setArmor(playerInventory::setHelmet, Material.LEATHER_HELMET, red, green, blue);
        setArmor(playerInventory::setChestplate, Material.LEATHER_CHESTPLATE, red, green, blue);
        setArmor(playerInventory::setLeggings, Material.LEATHER_LEGGINGS, red, green, blue);
        setArmor(playerInventory::setBoots, Material.LEATHER_BOOTS, red, green, blue);
    }

    private void setArmor(Consumer<ItemStack> setter, Material material, int red, int green, int blue) {
        setter.accept(createColoredArmor(material, red, green, blue));
    }

    private ItemStack createColoredArmor(Material material, int r, int g, int b) {

        ItemStack item = new ItemStack(material, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(r, g, b));
        item.setItemMeta(meta);
        return item;
    }
}
