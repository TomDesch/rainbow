package io.stealingdapenta.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.stealingdapenta.ArmorListener;
import java.lang.reflect.Proxy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class CommandCycleSpeedTest {

    private static final String PLAYER_NAME = "cycle-speed-test";

    @AfterEach
    void tearDown() {
        ArmorListener.playersWearingRainbowArmor.remove(PLAYER_NAME);
    }

    @Test
    void rainbowCommandRejectsNonpositiveExplicitSpeeds() {
        assertRejects(new RainbowCommand(), "0", "-1");
    }

    @Test
    void rainbowItemCommandRejectsNonpositiveExplicitSpeeds() {
        assertRejects(new RainbowItemCommand(), "0", "-1");
    }

    @Test
    void rainbowHorseItemCommandRejectsNonpositiveExplicitSpeeds() {
        assertRejects(new RainbowHorseItemCommand(), "0", "-1");
    }

    private static void assertRejects(CommandExecutor executor, String... speeds) {
        Command command = new Command("rainbow") {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };

        for (String speed : speeds) {
            Player player = newPlayer();
            boolean result = executor.onCommand(player, command, command.getName(), new String[]{speed});

            assertTrue(result);
            assertFalse(ArmorListener.playersWearingRainbowArmor.contains(PLAYER_NAME));
        }
    }

    private static Player newPlayer() {
        PlayerInventory inventory = (PlayerInventory) Proxy.newProxyInstance(
                PlayerInventory.class.getClassLoader(),
                new Class<?>[]{PlayerInventory.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getArmorContents")) {
                        return new ItemStack[4];
                    }
                    if (method.getName().equals("getStorageContents")) {
                        return new ItemStack[36];
                    }
                    return null;
                }
        );

        return (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getName")) {
                        return PLAYER_NAME;
                    }
                    if (method.getName().equals("hasPermission")) {
                        return true;
                    }
                    if (method.getName().equals("getInventory")) {
                        return inventory;
                    }
                    return null;
                }
        );
    }
}
