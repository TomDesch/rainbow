package io.stealingdapenta;

import static io.stealingdapenta.ArmorListener.AIR_ARMOR;
import static io.stealingdapenta.ArmorListener.playersWearingRainbowArmor;

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
    private static final int MAX_COUNT = 18;

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

        int[] colors = {0, 255, 0, 255, 0, 255}; // green, red, blue alternating

        int index = (count / 3) % 2;

        if (count < MAX_COUNT) {
            int blue = colors[index];
            int green = colors[index + 1];
            int red = colors[index + 2];

            setArmor(playerInventory, blue, green, red);
            count++;
        } else {
            count = 0;
        }
    }

    private void setArmor(PlayerInventory playerInventory, int blue, int green, int red) {
        setArmor(playerInventory::setHelmet, Material.LEATHER_HELMET, blue, green, red);
        setArmor(playerInventory::setChestplate, Material.LEATHER_CHESTPLATE, blue, green, red);
        setArmor(playerInventory::setLeggings, Material.LEATHER_LEGGINGS, blue, green, red);
        setArmor(playerInventory::setBoots, Material.LEATHER_BOOTS, blue, green, red);
    }

    private void setArmor(Consumer<ItemStack> setter, Material material, int blue, int green, int red) {
        setter.accept(createColoredArmor(material, blue, green, red));
    }

    private ItemStack createColoredArmor(Material material, int b, int g, int r) {
        ItemStack item = new ItemStack(material, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromBGR(b, g, r));
        item.setItemMeta(meta);
        return item;
    }

}
