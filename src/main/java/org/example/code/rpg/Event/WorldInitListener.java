package org.example.code.rpg.Event;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldInitListener implements Listener {

    private final Plugin plugin;
    private final BeaconOfferingListener beaconOfferingListener;
    private final List<Location> structureLocations = new ArrayList<>();

    public WorldInitListener(Plugin plugin, BeaconOfferingListener beaconOfferingListener) {
        this.plugin = plugin;
        this.beaconOfferingListener = beaconOfferingListener;
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        World world = event.getWorld();
        // 오버월드에서만 구조물 생성 및 위치 저장 작업 실행
        if (world.getEnvironment() == World.Environment.NORMAL) {
            // 20틱 (1초) 후에 구조물 생성 및 위치 저장 작업 실행
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                locateAndSaveStructureLocation(world);
                saveNearestStructureToConfig(world);
            }, 20L);
        }
    }

    private void locateAndSaveStructureLocation(World world) {
        if (world == null) {
            plugin.getLogger().warning("World not found.");
            return;
        }

        // 랜덤한 좌표 생성
        int x = getRandomCoordinate(-3000, 3000);
        int z = getRandomCoordinate(-3000, 3000);
        int y = getRandomYCoordinate(world, x, z);

        // 주변 청크를 로드 및 비콘 블록 확인
        int chunkX = x / 16;
        int chunkZ = z / 16;
        for (int i = chunkX - 1; i <= chunkX + 1; i++) {
            for (int j = chunkZ - 1; j <= chunkZ + 1; j++) {
                Chunk chunk = world.getChunkAt(i, j);
                chunk.load(true);

                // 청크 내 모든 블록 확인
                for (int bx = 0; bx < 16; bx++) {
                    for (int bz = 0; bz < 16; bz++) {
                        for (int by = 0; by < world.getMaxHeight(); by++) {
                            Block block = chunk.getBlock(bx, by, bz);
                            if (block.getType() == Material.BEACON) {
                                // 신호기 블록 발견 시 BeaconOfferingListener와 연동
                                beaconOfferingListener.handleBeaconFound(block);
                            }
                        }
                    }
                }
            }
        }

        // 구조물 생성 및 좌표 저장
        ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
        Bukkit.dispatchCommand(consoleSender, "place structure altar:ancient_altar " + x + " " + y + " " + z);

        // 구조물 위치를 리스트에 저장
        structureLocations.add(new Location(world, x, y, z));
    }

    // 가장 가까운 구조물을 찾고 config에 저장하는 메서드
    private void saveNearestStructureToConfig(World world) {
        Location spawnLocation = world.getSpawnLocation();
        Location nearestLocation = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Location loc : structureLocations) {
            double distance = loc.distance(spawnLocation);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestLocation = loc;
            }
        }

        if (nearestLocation != null) {
            saveCoordinatesToConfig(nearestLocation.getBlockX(), nearestLocation.getBlockY(), nearestLocation.getBlockZ());
        }
    }

    // 랜덤한 x, z 좌표 생성
    private int getRandomCoordinate(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    // 랜덤한 y 좌표 생성
    private int getRandomYCoordinate(World world, int x, int z) {
        return world.getHighestBlockYAt(x, z);
    }

    // 구조물 좌표를 config.yml에 저장하는 메서드
    private void saveCoordinatesToConfig(int x, int y, int z) {
        FileConfiguration config = plugin.getConfig();
        config.set("structures.ancient_altar.nearest.x", x);
        config.set("structures.ancient_altar.nearest.y", y);
        config.set("structures.ancient_altar.nearest.z", z);
        plugin.saveConfig();
        plugin.getLogger().info("Nearest structure coordinates saved to config.yml");
    }
}