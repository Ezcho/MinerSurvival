package org.example.code.rpg.Event;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.example.code.rpg.Manager.JobConfigManager;
import org.example.code.rpg.RPG;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeaconOfferingListener implements Listener {

    private final Plugin plugin;
    private final JobConfigManager jobConfigManager; // Added JobConfigManager instance
    private final HashMap<Block, Set<Material>> beaconOfferings = new HashMap<>();
    private final Set<Material> requiredOfferings = Set.of(
            Material.COAL, Material.COPPER_INGOT, Material.IRON_INGOT, Material.GOLD_INGOT,
            Material.REDSTONE, Material.LAPIS_LAZULI, Material.EMERALD, Material.DIAMOND,
            Material.AMETHYST_SHARD, Material.QUARTZ, Material.NETHERITE_INGOT
    );

    private final Map<Material, String> materialNames = new HashMap<>();

    public BeaconOfferingListener(Plugin plugin, JobConfigManager jobConfigManager) {
        this.plugin = plugin;
        this.jobConfigManager = jobConfigManager; // Initialize JobConfigManager
        initializeMaterialNames();
    }

    private void initializeMaterialNames() {
        materialNames.put(Material.COAL, "Coal");
        materialNames.put(Material.COPPER_INGOT, "Copper Ingot");
        materialNames.put(Material.IRON_INGOT, "Iron Ingot");
        materialNames.put(Material.GOLD_INGOT, "Gold Ingot");
        materialNames.put(Material.REDSTONE, "Redstone");
        materialNames.put(Material.LAPIS_LAZULI, "Lapis Lazuli");
        materialNames.put(Material.EMERALD, "Emerald");
        materialNames.put(Material.DIAMOND, "Diamond");
        materialNames.put(Material.AMETHYST_SHARD, "Amethyst Shard");
        materialNames.put(Material.QUARTZ, "Quartz");
        materialNames.put(Material.NETHERITE_INGOT, "Netherite Ingot");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && clickedBlock.getType() == Material.BEACON) {
                handleBeaconInteraction(event.getPlayer(), clickedBlock);
            }
        }
    }

    public void handleBeaconFound(Block beaconBlock) {
        for (Player player : beaconBlock.getWorld().getPlayers()) {
            if (player.getLocation().distance(beaconBlock.getLocation()) < 50) {
                player.sendMessage("The core has been activated! Prepare the offering materials.");
                handleBeaconInteraction(player, beaconBlock);
            }
        }
    }

    private void handleBeaconInteraction(Player player, Block beaconBlock) {
        // Check the player's job and level
        String jobInfo = jobConfigManager.getPlayerJob(player);
        String[] jobDetails = jobInfo.split(",");
        String job = jobDetails[0];
        String level = jobDetails[1];
        if (!job.equals("§7§lMiner") || !level.equals("4th Stage")) {
            player.sendMessage(ChatColor.RED + "You must reach the 4th stage of Miner to start the offering!");
            return;
        }

        // Instance to get clue states from the RPG class
        RPG pluginInstance = (RPG) this.plugin;

        // Check the states of Clue1, Clue2, and Clue3
        boolean clue1 = pluginInstance.loadClueState(player, "Clue1");
        boolean clue2 = pluginInstance.loadClueState(player, "Clue2");
        boolean clue3 = pluginInstance.loadClueState(player, "Clue3");

        if (!(clue1 && clue2 && clue3)) {
            player.sendMessage(ChatColor.RED + "You must find all clues to start the offering!");
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        Material offeringMaterial = itemInHand.getType();

        Set<Material> offerings = beaconOfferings.getOrDefault(beaconBlock, new HashSet<>());
        if (offerings.contains(offeringMaterial)) {
            player.sendMessage(ChatColor.GREEN + getMaterialName(offeringMaterial) + ChatColor.YELLOW + " has already been offered. Use another material.");
            return;
        }

        if (isValidOfferingItem(offeringMaterial) && itemInHand.getAmount() >= 10) {
            itemInHand.setAmount(itemInHand.getAmount() - 10);
            player.sendMessage(ChatColor.GREEN + getMaterialName(offeringMaterial) + " 10 units" + ChatColor.AQUA + " successfully offered!");

            offerings.add(offeringMaterial);
            beaconOfferings.put(beaconBlock, offerings);

            // Check if all materials have been offered
            if (checkAllOfferingsComplete()) {
                triggerCompletionSequence(player);
            }
        } else {
            player.sendMessage("You do not have enough materials to offer.");
        }
    }

    private boolean isValidOfferingItem(Material material) {
        return requiredOfferings.contains(material);
    }

    private boolean checkAllOfferingsComplete() {
        for (Set<Material> offerings : beaconOfferings.values()) {
            if (!offerings.containsAll(requiredOfferings)) {
                return false;
            }
        }
        return true;
    }

    private void triggerCompletionSequence(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.sendTitle("§aAll anomalies", "", 10, 60, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendTitle("§aHave been restored.", "", 10, 60, 20);
            }
        }.runTaskLater(plugin, 40L);  // 40L = 2 seconds later (20 ticks = 1 second)

        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendTitle("§aThank you for", "", 10, 60, 20);
            }
        }.runTaskLater(plugin, 120L);  // 120L = 6 seconds later (20 ticks = 1 second)

        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendTitle("§aPlaying the 'Surviving as a Miner' plugin", "", 10, 60, 20);
            }
        }.runTaskLater(plugin, 160L);  // 160L = 8 seconds later (20 ticks = 1 second)

        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendTitle("§aThank you for playing.", "§ePlugin Creator: Ike", 10, 100, 20);
            }
        }.runTaskLater(plugin, 200L);  // 200L = 10 seconds later (20 ticks = 1 second)
    }

    private String getMaterialName(Material material) {
        return materialNames.getOrDefault(material, material.name());
    }
}
