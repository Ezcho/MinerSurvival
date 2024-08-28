package org.example.code.rpg.Event;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.example.code.rpg.RPG;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PlayerMoveListener implements Listener {

    private HashMap<UUID, BossBar> playerBossBars;
    private Map<UUID, Double> playerO2;
    private Map<UUID, BukkitRunnable> activeTimers;
    private final double initialTime = 600.0;
    private final double maxTime = 1800.0; // Maximum time
    private RPG plugin;

    public PlayerMoveListener(RPG plugin, HashMap<UUID, BossBar> playerBossBars, Map<UUID, Double> playerO2) {
        this.plugin = plugin;
        this.playerBossBars = playerBossBars;
        this.playerO2 = playerO2;
        this.activeTimers = new HashMap<>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        BossBar bossBar = playerBossBars.get(playerId);
        double remainingTime = playerO2.getOrDefault(playerId, initialTime);

        // Limit maximum time that can be extended with ores
        if (remainingTime > maxTime) {
            player.sendMessage("The maximum time you can extend with ores is 30 minutes.");
            remainingTime = maxTime;
            playerO2.put(playerId, remainingTime);
        }

        double y = player.getLocation().getY();
        if (y < 60) {
            // When underground
            if (bossBar != null) {
                bossBar.setVisible(true);
                bossBar.setTitle("Time until oxygen depletion: " + formatTime(remainingTime));
                if (remainingTime <= 0) {
                    remainingTime = 0;
                }
                bossBar.setProgress(clamp(remainingTime / initialTime, 0.0, 1.0));
            }

            if (!activeTimers.containsKey(playerId)) {
                BukkitRunnable timer = new BukkitRunnable() {
                    @Override
                    public void run() {
                        BossBar currentBossBar = playerBossBars.get(playerId);
                        if (player.isOnline() && player.getLocation().getY() < 60) {
                            Double timeLeftObj = playerO2.get(playerId);
                            if (timeLeftObj == null) {
                                timeLeftObj = initialTime;
                            }
                            double timeLeft = timeLeftObj;
                            if (timeLeft <= 0) {
                                player.damage(5);
                                if (player.getGameMode() == GameMode.CREATIVE) {
                                    player.setHealth(0);
                                }
                                if (currentBossBar != null) {
                                    currentBossBar.setVisible(false);
                                }
                                this.cancel();
                                activeTimers.remove(playerId);
                            } else {
                                timeLeft -= 1.0;

                                if (timeLeft > maxTime) {
                                    player.sendMessage("The maximum time you can extend with ores is 30 minutes.");
                                    timeLeft = maxTime;
                                }

                                playerO2.put(playerId, timeLeft);
                                if (currentBossBar != null) {
                                    currentBossBar.setProgress(clamp(timeLeft / initialTime, 0.0, 1.0));
                                    currentBossBar.setTitle("Time until oxygen depletion: " + formatTime(timeLeft));
                                }
                                if (timeLeft % 30 == 0) {
                                    createLava(player);
                                }
                            }
                        } else {
                            if (currentBossBar != null) {
                                currentBossBar.setVisible(false);
                            }
                            this.cancel(); // Stop the timer when above ground but don't reset it
                            activeTimers.remove(playerId);
                        }
                    }
                };
                timer.runTaskTimer(plugin, 20, 20); // Run every 1 second (20 ticks)
                activeTimers.put(playerId, timer);
            }
        } else {
            // When above ground
            if (bossBar != null) {
                bossBar.setVisible(false);
            }

            if (activeTimers.containsKey(playerId)) {
                activeTimers.get(playerId).cancel(); // Just stop the timer, don't reset it
                activeTimers.remove(playerId);
            }
        }
    }

    private void createLava(Player player) {
        Location playerLocation = player.getLocation();
        Random random = new Random();

        int randomX = random.nextInt(3) - 1; // -1, 0, 1
        int randomY = random.nextInt(3) - 1; // -1, 0, 1

        Location lavaLocation = playerLocation.clone().add(randomX, randomY, 0);
        Block block = lavaLocation.getBlock();
        if (block.getType() == Material.STONE) {
            block.setType(Material.LAVA);
        }
    }

    private String formatTime(double seconds) {
        int minutes = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%02d minutes %02d seconds", minutes, secs);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
