package io.stealingdapenta.animator;

import static io.stealingdapenta.animator.AnimatorUtil.convertCountToRGB;

import io.stealingdapenta.ArmorPieceFactory;
import io.stealingdapenta.rainbow.Rainbow;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class TaggedArmorAnimator extends BukkitRunnable {

    private static final int THRESHOLD = 5000;
    private final int cycleSpeed;
    private final NamespacedKey tagKey = new NamespacedKey(Rainbow.getInstance(), ArmorPieceFactory.ARMOR_NSK);
    private int count = 0;

    public TaggedArmorAnimator(int cycleSpeed) {
        this.cycleSpeed = cycleSpeed;
    }

    @Override
    public void run() {
        Color color = convertCountToRGB(count);

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack armorPiece : player.getInventory()
                                              .getArmorContents()) {
                if (isTaggedRainbowArmor(armorPiece)) {
                    updateArmorColor(armorPiece, color);
                }
            }
        }

        count += cycleSpeed;
        if (count >= Integer.MAX_VALUE - THRESHOLD) {
            count = 0;
        }
    }

    private boolean isTaggedRainbowArmor(ItemStack item) {
        if (item == null || !(item.getItemMeta() instanceof LeatherArmorMeta meta)) {
            return false;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(tagKey, PersistentDataType.BYTE);
    }

    private void updateArmorColor(ItemStack item, Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        if (meta == null) {
            return;
        }

        meta.setColor(color);
        item.setItemMeta(meta);
    }
}
