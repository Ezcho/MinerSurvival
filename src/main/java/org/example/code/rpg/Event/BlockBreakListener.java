package org.example.code.rpg.Event;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.example.code.rpg.Manager.JobConfigManager;
import org.example.code.rpg.RPG;

import java.util.*;

import static org.bukkit.Bukkit.getLogger;

public class BlockBreakListener implements Listener {
    private RPG plugin;
    private Map<UUID, Double> playerO2 = new HashMap<>();
    private final HashMap<UUID, Integer> playerBlockCount = new HashMap<>();
    private final List<Material> trackedBlocks = Arrays.asList(
            Material.STONE, Material.COBBLESTONE, Material.MOSSY_COBBLESTONE, Material.GRANITE,
            Material.DIORITE, Material.ANDESITE, Material.DEEPSLATE, Material.COBBLED_DEEPSLATE,
            Material.REINFORCED_DEEPSLATE, Material.TUFF, Material.COAL_ORE, Material.IRON_ORE,
            Material.COPPER_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE, Material.LAPIS_ORE,
            Material.EMERALD_ORE, Material.DIAMOND_ORE, Material.RAW_IRON_BLOCK, Material.RAW_GOLD_BLOCK,
            Material.RAW_COPPER_BLOCK, Material.NETHERRACK, Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE,
            Material.ANCIENT_DEBRIS, Material.AMETHYST_CLUSTER
    );
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    // Set to track the location of blocks placed by the player
    private final Set<Location> playerPlacedBlocks = new HashSet<>();

    // Create a map for Korean names
    private static final Map<Material, String> materialToKoreanNameMap = new HashMap<>();

    static {
        materialToKoreanNameMap.put(Material.COPPER_ORE, "Copper Ore");
        materialToKoreanNameMap.put(Material.DEEPSLATE_COPPER_ORE, "Deepslate Copper Ore");
        materialToKoreanNameMap.put(Material.IRON_ORE, "Iron Ore");
        materialToKoreanNameMap.put(Material.DEEPSLATE_IRON_ORE, "Deepslate Iron Ore");
        materialToKoreanNameMap.put(Material.GOLD_ORE, "Gold Ore");
        materialToKoreanNameMap.put(Material.DEEPSLATE_GOLD_ORE, "Deepslate Gold Ore");
        materialToKoreanNameMap.put(Material.NETHER_GOLD_ORE, "Nether Gold Ore");
        materialToKoreanNameMap.put(Material.ANCIENT_DEBRIS, "Ancient Debris");
        materialToKoreanNameMap.put(Material.COAL_ORE, "Coal Ore");
        materialToKoreanNameMap.put(Material.DEEPSLATE_COAL_ORE, "Deepslate Coal Ore");
        materialToKoreanNameMap.put(Material.REDSTONE_ORE, "Redstone Ore");
        materialToKoreanNameMap.put(Material.DEEPSLATE_REDSTONE_ORE, "Deepslate Redstone Ore");
        materialToKoreanNameMap.put(Material.LAPIS_ORE, "Lapis Lazuli Ore");
        materialToKoreanNameMap.put(Material.DEEPSLATE_LAPIS_ORE, "Deepslate Lapis Lazuli Ore");
        materialToKoreanNameMap.put(Material.EMERALD_ORE, "Emerald Ore");
        materialToKoreanNameMap.put(Material.DEEPSLATE_EMERALD_ORE, "Deepslate Emerald Ore");
        materialToKoreanNameMap.put(Material.DIAMOND_ORE, "Diamond Ore");
        materialToKoreanNameMap.put(Material.DEEPSLATE_DIAMOND_ORE, "Deepslate Diamond Ore");
        materialToKoreanNameMap.put(Material.AMETHYST_CLUSTER, "Amethyst Cluster");
        materialToKoreanNameMap.put(Material.NETHER_QUARTZ_ORE, "Nether Quartz Ore");
    }

    public BlockBreakListener(RPG plugin, Map<UUID, Double> playerO2) {
        this.plugin = plugin;
        this.playerO2 = playerO2;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material blockType = block.getType();
        Location blockLocation = block.getLocation();

        // If the block is included in trackedBlocks, check if the player placed the block
        if (trackedBlocks.contains(blockType) && playerPlacedBlocks.contains(blockLocation)) {
            // If the block was placed by the player, do not apply additional effects
            player.sendMessage(ChatColor.RED + "This block was player-placed and does not grant effects.");
            return;
        }

        JobConfigManager jobConfigManager = new JobConfigManager(plugin);
        String[] jobData = jobConfigManager.getPlayerJob(player).split(",");
        String job = jobData.length > 0 ? jobData[0] : "No job";
        String level = jobData.length > 1 ? jobData[1] : "";

        handleOreDrops(event, player, blockType);
        handleJobEffects(player, blockType, job, level);
        handleOxygenRecovery(player, blockType);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Track the location when a player places a block
        playerPlacedBlocks.add(event.getBlock().getLocation());
    }

    private void handleOreDrops(BlockBreakEvent event, Player player, Material blockType) {
        Map<Material, ItemStack> oreToIngotMap = new HashMap<>();
        oreToIngotMap.put(Material.COPPER_ORE, new ItemStack(Material.COPPER_INGOT));
        oreToIngotMap.put(Material.DEEPSLATE_COPPER_ORE, new ItemStack(Material.COPPER_INGOT));
        oreToIngotMap.put(Material.IRON_ORE, new ItemStack(Material.IRON_INGOT));
        oreToIngotMap.put(Material.DEEPSLATE_IRON_ORE, new ItemStack(Material.IRON_INGOT));
        oreToIngotMap.put(Material.GOLD_ORE, new ItemStack(Material.GOLD_INGOT));
        oreToIngotMap.put(Material.DEEPSLATE_GOLD_ORE, new ItemStack(Material.GOLD_INGOT));
        oreToIngotMap.put(Material.NETHER_GOLD_ORE, new ItemStack(Material.GOLD_INGOT));
        oreToIngotMap.put(Material.ANCIENT_DEBRIS, new ItemStack(Material.NETHERITE_INGOT));

        // Check the tool used to break the block
        ItemStack tool = player.getInventory().getItemInMainHand();

        // Specify the required pickaxe level for each block
        Map<Material, Integer> blockToRequiredPickaxeLevel = new HashMap<>();
        blockToRequiredPickaxeLevel.put(Material.GOLD_ORE, 2); // Requires Iron Pickaxe or higher
        blockToRequiredPickaxeLevel.put(Material.DEEPSLATE_GOLD_ORE, 2);
        blockToRequiredPickaxeLevel.put(Material.IRON_ORE, 1); // Requires Stone Pickaxe or higher
        blockToRequiredPickaxeLevel.put(Material.DEEPSLATE_IRON_ORE, 1);
        blockToRequiredPickaxeLevel.put(Material.COPPER_ORE, 1); // Requires Stone Pickaxe or higher
        blockToRequiredPickaxeLevel.put(Material.DEEPSLATE_COPPER_ORE, 1);
        blockToRequiredPickaxeLevel.put(Material.ANCIENT_DEBRIS, 3); // Requires Diamond Pickaxe or higher

        // Convert pickaxe levels to numbers
        Map<Material, Integer> pickaxeLevelMap = new HashMap<>();
        pickaxeLevelMap.put(Material.WOODEN_PICKAXE, 0);
        pickaxeLevelMap.put(Material.STONE_PICKAXE, 1);
        pickaxeLevelMap.put(Material.IRON_PICKAXE, 2);
        pickaxeLevelMap.put(Material.DIAMOND_PICKAXE, 3);
        pickaxeLevelMap.put(Material.NETHERITE_PICKAXE, 4);

        int pickaxeLevel = pickaxeLevelMap.getOrDefault(tool.getType(), -1);

        // Compare required pickaxe level with the level of the used pickaxe
        if (blockToRequiredPickaxeLevel.containsKey(blockType)) {
            int requiredLevel = blockToRequiredPickaxeLevel.get(blockType);
            if (pickaxeLevel < requiredLevel) {
                player.sendMessage(ChatColor.RED + "This ore cannot be mined with your current pickaxe.");
                return;
            }
        }

        // If blockType is in oreToIngotMap, drop the corresponding item
        if (oreToIngotMap.containsKey(blockType)) {
            event.setDropItems(false);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), oreToIngotMap.get(blockType));

            // Display message using Korean names
            String koreanName = materialToKoreanNameMap.getOrDefault(blockType, blockType.name());
            player.sendMessage(ChatColor.GREEN + koreanName + ChatColor.YELLOW + " has been mined and item obtained.");
        }
    }

    private void handleJobEffects(Player player, Material blockType, String job, String level) {
        if (!job.equals("§7§lMiner")) return;

        int bonusChance = 10;
        Map<Material, ItemStack> bonusItems = new HashMap<>();
        bonusItems.put(Material.COAL_ORE, new ItemStack(Material.COAL, 5));
        bonusItems.put(Material.DEEPSLATE_COAL_ORE, new ItemStack(Material.COAL, 5));
        bonusItems.put(Material.COPPER_ORE, new ItemStack(Material.COPPER_INGOT, 5));
        bonusItems.put(Material.DEEPSLATE_COPPER_ORE, new ItemStack(Material.COPPER_INGOT, 5));
        bonusItems.put(Material.IRON_ORE, new ItemStack(Material.IRON_INGOT, 5));
        bonusItems.put(Material.DEEPSLATE_IRON_ORE, new ItemStack(Material.IRON_INGOT, 5));
        bonusItems.put(Material.GOLD_ORE, new ItemStack(Material.GOLD_INGOT, 5));
        bonusItems.put(Material.DEEPSLATE_GOLD_ORE, new ItemStack(Material.GOLD_INGOT, 5));
        bonusItems.put(Material.NETHER_GOLD_ORE, new ItemStack(Material.GOLD_INGOT, 5));
        bonusItems.put(Material.REDSTONE_ORE, new ItemStack(Material.REDSTONE, 5));
        bonusItems.put(Material.DEEPSLATE_REDSTONE_ORE, new ItemStack(Material.REDSTONE, 5));
        bonusItems.put(Material.LAPIS_ORE, new ItemStack(Material.LAPIS_LAZULI, 5));
        bonusItems.put(Material.DEEPSLATE_LAPIS_ORE, new ItemStack(Material.LAPIS_LAZULI, 5));
        bonusItems.put(Material.EMERALD_ORE, new ItemStack(Material.EMERALD, 5));
        bonusItems.put(Material.DEEPSLATE_EMERALD_ORE, new ItemStack(Material.EMERALD, 5));
        bonusItems.put(Material.DIAMOND_ORE, new ItemStack(Material.DIAMOND, 5));
        bonusItems.put(Material.DEEPSLATE_DIAMOND_ORE, new ItemStack(Material.DIAMOND, 5));
        bonusItems.put(Material.AMETHYST_CLUSTER, new ItemStack(Material.AMETHYST_SHARD, 5));
        bonusItems.put(Material.NETHER_QUARTZ_ORE, new ItemStack(Material.QUARTZ, 5));
        bonusItems.put(Material.ANCIENT_DEBRIS, new ItemStack(Material.NETHERITE_INGOT, 5));

        if (bonusItems.containsKey(blockType) && Math.random() * 100 < bonusChance) {
            player.getInventory().addItem(bonusItems.get(blockType));
            String koreanName = materialToKoreanNameMap.getOrDefault(blockType, blockType.name());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e" + koreanName + "&a You have obtained 5 more!"));
        }

        if (trackedBlocks.contains(blockType)) {
            UUID playerId = player.getUniqueId();
            playerBlockCount.put(playerId, playerBlockCount.getOrDefault(playerId, 0) + 1);

            if (playerBlockCount.get(playerId) >= 50) {
                int hasteLevel = level.equals("Tier 3") || level.equals("Tier 4") ? 1 : 0;
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 30 * 20, hasteLevel));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have mined 50 stones or ores and received &eHaste " + (hasteLevel + 1) + " effect&a for &e30 seconds&a!"));
                playerBlockCount.put(playerId, 0);
            }

            if (level.equals("Tier 2") || level.equals("Tier 3") || level.equals("Tier 4")) {
                handleNightVisionEffect(player, playerId);
            }
        }

        if (level.equals("Tier 4")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        }
    }

    private void handleNightVisionEffect(Player player, UUID playerId) {
        long currentTime = System.currentTimeMillis();
        long effectCooldown = 30 * 1000;
        Long effectEndTime = cooldowns.get(playerId);

        if (effectEndTime == null || currentTime > effectEndTime) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 20, 0));
            cooldowns.put(playerId, currentTime + effectCooldown);
        } else {
            long remainingCooldown = (effectEndTime - currentTime) / 1000;
            player.sendMessage("Night Vision effect cooldown is " + remainingCooldown + " seconds remaining.");
        }
    }

    private void handleOxygenRecovery(Player player, Material blockType) {
        Map<Material, Double> oxygenRecoveryMap = new HashMap<>();
        oxygenRecoveryMap.put(Material.COAL_ORE, 10.0);
        oxygenRecoveryMap.put(Material.DEEPSLATE_COAL_ORE, 10.0);
        oxygenRecoveryMap.put(Material.COPPER_ORE, 20.0);
        oxygenRecoveryMap.put(Material.DEEPSLATE_COPPER_ORE, 20.0);
        oxygenRecoveryMap.put(Material.IRON_ORE, 30.0);
        oxygenRecoveryMap.put(Material.DEEPSLATE_IRON_ORE, 30.0);
        oxygenRecoveryMap.put(Material.GOLD_ORE, 40.0);
        oxygenRecoveryMap.put(Material.DEEPSLATE_GOLD_ORE, 40.0);
        oxygenRecoveryMap.put(Material.REDSTONE_ORE, 15.0);
        oxygenRecoveryMap.put(Material.DEEPSLATE_REDSTONE_ORE, 15.0);
        oxygenRecoveryMap.put(Material.LAPIS_ORE, 60.0);
        oxygenRecoveryMap.put(Material.DEEPSLATE_LAPIS_ORE, 60.0);
        oxygenRecoveryMap.put(Material.EMERALD_ORE, 240.0);
        oxygenRecoveryMap.put(Material.DEEPSLATE_EMERALD_ORE, 240.0);
        oxygenRecoveryMap.put(Material.DIAMOND_ORE, 120.0);
        oxygenRecoveryMap.put(Material.DEEPSLATE_DIAMOND_ORE, 120.0);
        oxygenRecoveryMap.put(Material.AMETHYST_CLUSTER, 50.0);
        oxygenRecoveryMap.put(Material.NETHER_GOLD_ORE, 45.0);
        oxygenRecoveryMap.put(Material.NETHER_QUARTZ_ORE, 55.0);
        oxygenRecoveryMap.put(Material.ANCIENT_DEBRIS, 300.0);

        if (oxygenRecoveryMap.containsKey(blockType)) {
            double newOxygenTime = playerO2.getOrDefault(player.getUniqueId(), 0.0) + oxygenRecoveryMap.get(blockType);
            playerO2.put(player.getUniqueId(), newOxygenTime);

            String koreanName = materialToKoreanNameMap.getOrDefault(blockType, blockType.name());
            String message = ChatColor.GREEN + koreanName + ChatColor.RESET + " was mined and your oxygen energy has increased by " + ChatColor.GREEN + oxygenRecoveryMap.get(blockType) + " seconds" + ChatColor.RESET + "!";
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }
}
