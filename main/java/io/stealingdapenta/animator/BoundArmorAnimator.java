package io.stealingdapenta.animator;

import static io.stealingdapenta.ArmorListener.AIR_ARMOR;
import static io.stealingdapenta.ArmorListener.playersWearingRainbowArmor;
import static io.stealingdapenta.animator.AnimatorUtil.convertCountToRGB;
import static io.stealingdapenta.config.ConfigKey.CYCLE_SPEED;

import java.util.function.Consumer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class BoundArmorAnimator extends BukkitRunnable {

    private final Player player;
    private int count = 0;
    private static final int THRESHOLD = 5000;
    private final int cycleSpeed;


    public BoundArmorAnimator(Player player) {
        this.cycleSpeed = CYCLE_SPEED.asInt();
        this.player = player;
    }

    public BoundArmorAnimator(Player player, int cycleSpeed) {
        this.player = player;
        this.cycleSpeed = cycleSpeed;
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
}