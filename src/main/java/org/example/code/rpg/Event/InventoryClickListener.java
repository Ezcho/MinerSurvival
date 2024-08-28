package org.example.code.rpg.Event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.code.rpg.Manager.GuiManager;
import org.example.code.rpg.Manager.JobConfigManager;
import org.example.code.rpg.Manager.MoneyManager;
import org.example.code.rpg.Manager.PlayerScoreboardManager;
import org.example.code.rpg.RPG;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class InventoryClickListener implements Listener {
    private final RPG plugin;
    private final GuiManager guiManager;
    private final MoneyManager moneyManager;
    private final PlayerScoreboardManager scoreboardManager;
    private final JobConfigManager jobConfigManager;

    // Material name mappings in English
    private final Map<Material, String> materialNames;

    // Clue unlock conditions storage
    private Material clue1Material;
    private Material clue2Material;
    private Material clue3Material;
    private int clue1RequiredSales;
    private int clue2RequiredSales;
    private int clue3RequiredSales;

    private final Random random = new Random();

    public InventoryClickListener(RPG plugin, GuiManager guiManager, MoneyManager moneyManager, PlayerScoreboardManager scoreboardManager, JobConfigManager jobConfigManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.moneyManager = moneyManager;
        this.scoreboardManager = scoreboardManager;
        this.jobConfigManager = jobConfigManager;

        // Initialize material name mappings
        this.materialNames = new HashMap<>();
        this.materialNames.put(Material.COAL, "Coal");
        this.materialNames.put(Material.COPPER_INGOT, "Copper Ingot");
        this.materialNames.put(Material.IRON_INGOT, "Iron Ingot");
        this.materialNames.put(Material.GOLD_INGOT, "Gold Ingot");
        this.materialNames.put(Material.REDSTONE, "Redstone");
        this.materialNames.put(Material.LAPIS_LAZULI, "Lapis Lazuli");
        this.materialNames.put(Material.EMERALD, "Emerald");
        this.materialNames.put(Material.DIAMOND, "Diamond");
        this.materialNames.put(Material.AMETHYST_SHARD, "Amethyst Shard");
        this.materialNames.put(Material.QUARTZ, "Quartz");
        this.materialNames.put(Material.NETHERITE_INGOT, "Netherite Ingot");

        // Randomly initialize clue unlock conditions
        initializeClueRequirements();
    }

    // Method to randomly set clue unlock conditions
    private void initializeClueRequirements() {
        Set<Material> materialSet = materialNames.keySet();
        Material[] materials = materialSet.toArray(new Material[0]);

        clue1Material = materials[random.nextInt(materials.length)];
        clue2Material = materials[random.nextInt(materials.length)];
        clue3Material = materials[random.nextInt(materials.length)];

        clue1RequiredSales = random.nextInt(100) + 1;
        clue2RequiredSales = random.nextInt(100) + 1;
        clue3RequiredSales = random.nextInt(100) + 1;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String inventoryTitle = event.getView().getTitle();
        if (!inventoryTitle.equals("Menu") && !inventoryTitle.equals("Job Change Shop") && !inventoryTitle.equals("Mineral Shop") && !inventoryTitle.equals("Clue Resolution")) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (event.getClickedInventory() == player.getInventory()) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
                player.sendMessage("Shift + Left Click is not allowed in this GUI.");
            } else if (event.getClick() == ClickType.DOUBLE_CLICK) {
                event.setCancelled(true);
                player.sendMessage("Double-clicking is not allowed in this GUI.");
            }
            return;
        }

        event.setCancelled(true);

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        if (clickedItem.getType() == Material.ENCHANTED_BOOK && clickedItem.hasItemMeta()) {
            String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            // Get the player's current job and level
            String[] jobInfo = jobConfigManager.getPlayerJob(player).split(",");
            String job = jobInfo[0];
            String level = jobInfo[1];

            if (displayName.equals("Job Change")) {
                guiManager.jobShop(player);
            } else if (displayName.equals("[Job Change] Miner 1st Stage")) {
                if (job.equals("No Job") && level.equals(" ")) {
                    processJobPurchase(player, 5000, "Miner 1st Stage", ChatColor.DARK_PURPLE + "You are now a Miner 1st Stage.");
                } else {
                    player.sendMessage(ChatColor.RED + "You already have a job.");
                }
            } else if (displayName.equals("[Job Change] Miner 2nd Stage")) {
                if (job.equals("§7§lMiner") && level.equals("1st Stage")) {
                    processJobPurchase(player, 30000, "Miner 2nd Stage", ChatColor.DARK_PURPLE + "You are now a Miner 2nd Stage.");
                } else {
                    player.sendMessage(ChatColor.RED + "You must be a 'Miner 1st Stage' to purchase this job book.");
                }
            } else if (displayName.equals("[Job Change] Miner 3rd Stage")) {
                if (job.equals("§7§lMiner") && level.equals("2nd Stage")) {
                    processJobPurchase(player, 65000, "Miner 3rd Stage", ChatColor.DARK_PURPLE + "You are now a Miner 3rd Stage.");
                } else {
                    player.sendMessage(ChatColor.RED + "You must be a 'Miner 2nd Stage' to purchase this job book.");
                }
            } else if (displayName.equals("[Job Change] Miner 4th Stage")) {
                if (job.equals("§7§lMiner") && level.equals("3rd Stage")) {
                    processJobPurchase(player, 100000, "Miner 4th Stage", ChatColor.DARK_PURPLE + "You are now a Miner 4th Stage.");
                } else {
                    player.sendMessage(ChatColor.RED + "You must be a 'Miner 3rd Stage' to purchase this job book.");
                }
            }
        } else if (clickedItem.getType() == Material.NETHERITE_PICKAXE && clickedItem.hasItemMeta()) {
            guiManager.mineralShop(player);
        } else if (clickedItem.getType() == Material.PAPER && clickedItem.hasItemMeta()) {
            boolean clue1Unlocked = plugin.loadClueState(player, "Clue1");
            boolean clue2Unlocked = plugin.loadClueState(player, "Clue2");
            boolean clue3Unlocked = plugin.loadClueState(player, "Clue3");
            guiManager.clues(player, clue1Unlocked, clue2Unlocked, clue3Unlocked);
        } else {
            handleMineralSale(player, clickedItem, event.getClick());
        }
    }

    private void processJobPurchase(Player player, int cost, String jobName, String lore) {
        if (moneyManager.getBalance(player) >= cost) {
            moneyManager.subtractBalance(player, cost);
            player.sendMessage(ChatColor.GREEN + "Purchase completed successfully!");

            ItemStack customItem = createCustomItem(jobName, lore);
            addCustomItemToPlayer(player, customItem);
            scoreboardManager.setPlayerScoreboard(player);
        } else {
            player.sendMessage(ChatColor.RED + "Insufficient balance.");
        }
    }

    private ItemStack createCustomItem(String name, String lore) {
        ItemStack customItem = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = customItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "[Job Change] " + ChatColor.GRAY + "" + ChatColor.BOLD + name);
            meta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', lore)));
            customItem.setItemMeta(meta);
        }
        return customItem;
    }

    private void addCustomItemToPlayer(Player player, ItemStack customItem) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(customItem);
        } else {
            player.getWorld().dropItem(player.getLocation(), customItem);
            player.sendMessage(ChatColor.RED + "Your inventory is full, the item has dropped on the ground!");
        }
    }

    private void handleMineralSale(Player player, ItemStack clickedItem, ClickType clickType) {
        Material material = clickedItem.getType();
        int leftClickCost = 0, middleClickCost = 0, rightClickCost = 0;

        switch (material) {
            case COAL:
                leftClickCost = 30;
                middleClickCost = 960;
                rightClickCost = 1920;
                break;
            case COPPER_INGOT:
                leftClickCost = 40;
                middleClickCost = 1280;
                rightClickCost = 2560;
                break;
            case IRON_INGOT:
                leftClickCost = 50;
                middleClickCost = 1600;
                rightClickCost = 3200;
                break;
            case GOLD_INGOT:
                leftClickCost = 60;
                middleClickCost = 1920;
                rightClickCost = 3840;
                break;
            case REDSTONE:
                leftClickCost = 20;
                middleClickCost = 640;
                rightClickCost = 1280;
                break;
            case LAPIS_LAZULI:
                leftClickCost = 80;
                middleClickCost = 2560;
                rightClickCost = 5120;
                break;
            case EMERALD:
                leftClickCost = 90;
                middleClickCost = 2880;
                rightClickCost = 5760;
                break;
            case DIAMOND:
                leftClickCost = 100;
                middleClickCost = 3200;
                rightClickCost = 6400;
                break;
            case AMETHYST_SHARD:
                leftClickCost = 120;
                middleClickCost = 3840;
                rightClickCost = 7680;
                break;
            case QUARTZ:
                leftClickCost = 150;
                middleClickCost = 4800;
                rightClickCost = 9600;
                break;
            case NETHERITE_INGOT:
                leftClickCost = 250;
                middleClickCost = 8000;
                rightClickCost = 16000;
                break;
            default:
                return;
        }

        int amountToSell = 0;
        int salePrice = 0;

        switch (clickType) {
            case LEFT:
                amountToSell = 1;
                salePrice = leftClickCost;
                break;
            case MIDDLE:
                amountToSell = 32;
                salePrice = middleClickCost;
                break;
            case RIGHT:
                amountToSell = 64;
                salePrice = rightClickCost;
                break;
            default:
                return;
        }

        String materialName = materialNames.getOrDefault(material, material.name());

        if (player.getInventory().containsAtLeast(new ItemStack(material), amountToSell)) {
            moneyManager.addBalance(player, salePrice);
            player.sendMessage(ChatColor.GREEN + "You sold " + amountToSell + " units for " + salePrice + " coins.");
            player.getInventory().removeItem(new ItemStack(material, amountToSell));
            updateSalesCount(player, material, amountToSell);
            scoreboardManager.setPlayerScoreboard(player);
        } else {
            player.sendMessage(ChatColor.RED + "You do not have enough " + materialName + " to sell.");
        }
    }

    private void updateSalesCount(Player player, Material material, int amountSold) {
        checkAndUpdateClue(player, material, amountSold, clue1Material, clue1RequiredSales, "Clue1");
        checkAndUpdateClue(player, material, amountSold, clue2Material, clue2RequiredSales, "Clue2");
        checkAndUpdateClue(player, material, amountSold, clue3Material, clue3RequiredSales, "Clue3");
    }

    private void checkAndUpdateClue(Player player, Material material, int amountSold, Material clueMaterial, int requiredSales, String clueName) {
        if (material == clueMaterial) {
            int currentSales = plugin.getSalesCount(player, clueName) + amountSold; // Store and accumulate sales per player
            plugin.setSalesCount(player, clueName, currentSales);

            if (currentSales >= requiredSales) {
                player.sendMessage(ChatColor.GREEN + clueName + " has been unlocked!");
                plugin.saveClueState(player, clueName, true);
            }
        }
    }
}
