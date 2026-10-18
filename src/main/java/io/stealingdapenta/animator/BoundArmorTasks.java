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

    /**
     * Replaces the player's current animation, canceling its task and clearing its temporary armor before tracking the new task.
     */
    public void start(Player player, int cycleSpeed) {
        stop(player);
        BoundArmorAnimator animator = new BoundArmorAnimator(player, cycleSpeed, this);
        BukkitTask task = scheduler.apply(animator);
        animations.put(player.getUniqueId(), new Animation(player, animator, task));
        playersWearingRainbowArmor.add(player.getName());
    }

    /**
     * Cancels this player's tracked task and removes its wearer marker; armor is cleared only if either was present.
     */
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

    /**
     * Stops every tracked animation, clears its temporary armor, and empties the wearer-marker set.
     */
    public void stopAll() {
        for (Animation animation : List.copyOf(animations.values())) {
            stop(animation.player());
        }
        playersWearingRainbowArmor.clear();
    }

    /**
     * Returns whether this exact animator is still owned by the player, so callbacks from replaced tasks can be ignored.
     */
    boolean isCurrent(Player player, BoundArmorAnimator animator) {
        Animation animation = animations.get(player.getUniqueId());
        return animation != null && animation.animator() == animator;
    }

    private record Animation(Player player, BoundArmorAnimator animator, BukkitTask task) {
    }
}
