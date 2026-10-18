package io.stealingdapenta.animator;

import static io.stealingdapenta.ArmorListener.playersWearingRainbowArmor;

import io.stealingdapenta.ArmorListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public final class BoundArmorTasks {

    private final Map<UUID, Animation> animations = new HashMap<>();
    private final Function<BoundArmorAnimator, BukkitTask> scheduler;

    public BoundArmorTasks(Function<BoundArmorAnimator, BukkitTask> scheduler) {
        this.scheduler = scheduler;
    }

    public void start(Player player, int cycleSpeed) {
        stop(player);
        BoundArmorAnimator animator = new BoundArmorAnimator(player, cycleSpeed, this);
        BukkitTask task = scheduler.apply(animator);
        animations.put(player.getUniqueId(), new Animation(player, animator, task));
        playersWearingRainbowArmor.add(player.getName());
    }

    public void stop(Player player) {
        Animation animation = animations.remove(player.getUniqueId());
        boolean wasWearing = playersWearingRainbowArmor.remove(player.getName());
        if (animation != null) {
            animation.task().cancel();
        }
        if (animation != null || wasWearing) {
            player.getInventory().setArmorContents(ArmorListener.emptyArmorContents());
        }
    }

    public void stopAll() {
        for (Animation animation : List.copyOf(animations.values())) {
            stop(animation.player());
        }
        playersWearingRainbowArmor.clear();
    }

    boolean isCurrent(Player player, BoundArmorAnimator animator) {
        Animation animation = animations.get(player.getUniqueId());
        return animation != null && animation.animator() == animator;
    }

    private record Animation(Player player, BoundArmorAnimator animator, BukkitTask task) {
    }
}
