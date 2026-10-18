package io.stealingdapenta.animator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.stealingdapenta.ArmorListener;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class BoundArmorTasksTest {

    private final List<Scheduled> scheduled = new ArrayList<>();
    private final BoundArmorTasks tasks = new BoundArmorTasks(animator -> {
        Scheduled entry = new Scheduled(animator);
        scheduled.add(entry);
        return entry.task;
    });

    @AfterEach
    void tearDown() {
        tasks.stopAll();
    }

    @Test
    void rapidToggleCancelsOldTaskAndStaleCallbackCannotTouchNewSession() {
        Actor actor = new Actor();
        tasks.start(actor.player, 5);
        tasks.stop(actor.player);
        tasks.start(actor.player, 20);
        int clearsBeforeStaleCallback = actor.armorClears;

        scheduled.getFirst().animator.run();

        assertTrue(scheduled.getFirst().cancelled);
        assertFalse(scheduled.getLast().cancelled);
        assertFalse(tasks.isCurrent(actor.player, scheduled.getFirst().animator));
        assertTrue(tasks.isCurrent(actor.player, scheduled.getLast().animator));
        assertEquals(clearsBeforeStaleCallback, actor.armorClears);
        assertEquals(1, ArmorListener.playersWearingRainbowArmor.stream().filter(actor.name::equals).count());
    }

    @Test
    void replacingActiveSessionCancelsItsTaskWithoutWaitingForTick() {
        Actor actor = new Actor();
        tasks.start(actor.player, 5);
        tasks.start(actor.player, 20);

        assertTrue(scheduled.getFirst().cancelled);
        assertFalse(scheduled.getLast().cancelled);
        assertEquals(1, actor.armorClears);
    }

    @Test
    void stopAllCancelsEveryTaskClearsArmorAndReleasesOwnership() {
        Actor first = new Actor();
        Actor second = new Actor();
        tasks.start(first.player, 5);
        tasks.start(second.player, 20);

        tasks.stopAll();
        tasks.stopAll();

        assertTrue(scheduled.stream().allMatch(entry -> entry.cancelled));
        assertEquals(1, first.armorClears);
        assertEquals(1, second.armorClears);
        assertFalse(tasks.isCurrent(first.player, scheduled.getFirst().animator));
        assertFalse(tasks.isCurrent(second.player, scheduled.getLast().animator));
        assertFalse(ArmorListener.playersWearingRainbowArmor.contains(first.name));
        assertFalse(ArmorListener.playersWearingRainbowArmor.contains(second.name));
    }

    @Test
    void stoppingUntrackedPlayerLeavesOrdinaryArmorAlone() {
        Actor actor = new Actor();

        tasks.stop(actor.player);

        assertEquals(0, actor.armorClears);
    }

    private static final class Scheduled {
        private final BoundArmorAnimator animator;
        private boolean cancelled;
        private final BukkitTask task = (BukkitTask) Proxy.newProxyInstance(BukkitTask.class.getClassLoader(),
                new Class<?>[]{BukkitTask.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "cancel" -> { cancelled = true; yield null; }
                    case "isCancelled" -> cancelled;
                    default -> throw new AssertionError("Unexpected scheduler call: " + method);
                });

        private Scheduled(BoundArmorAnimator animator) {
            this.animator = animator;
        }
    }

    private static final class Actor {
        private final UUID id = UUID.randomUUID();
        private final String name = id.toString();
        private int armorClears;
        private final PlayerInventory inventory = (PlayerInventory) Proxy.newProxyInstance(PlayerInventory.class.getClassLoader(),
                new Class<?>[]{PlayerInventory.class}, (proxy, method, args) -> {
                    if (method.getName().equals("setArmorContents")) {
                        armorClears++;
                        return null;
                    }
                    throw new AssertionError("Unexpected inventory call: " + method);
                });
        private final Player player = (Player) Proxy.newProxyInstance(Player.class.getClassLoader(),
                new Class<?>[]{Player.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getUniqueId" -> id;
                    case "getName" -> name;
                    case "getInventory" -> inventory;
                    default -> throw new AssertionError("Unexpected player call: " + method);
                });
    }
}
