package org.example.code.rpg.Manager;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.code.rpg.RPG;

import java.util.*;

public class GuiManager {
    private final RPG plugin;

    public GuiManager(RPG plugin) {
        this.plugin = plugin;
    }

    public void openGui(Player player) {
        Inventory basicsInventory = Bukkit.createInventory(null, 27, "Menu");

        ItemStack itemStack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemStack itemStack1 = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemStack itemStack2 = new ItemStack(Material.NETHERITE_PICKAXE, 1);
        ItemStack itemStack3 = new ItemStack(Material.PAPER, 1);
        ItemStack itemStack4 = new ItemStack(Material.WRITABLE_BOOK, 1);

        setDisplayName(itemStack1, ChatColor.YELLOW + "" + ChatColor.BOLD + "Job Change");
        setDisplayName(itemStack2, ChatColor.GOLD + "" + ChatColor.BOLD + "Minerals");
        setDisplayName(itemStack3, ChatColor.GRAY + "" + ChatColor.BOLD + "Clue");
        setDisplayName(itemStack4, ChatColor.GREEN + "" + ChatColor.BOLD + "/Help");

        for (int i = 0; i < 10; i++) {
            basicsInventory.setItem(i, itemStack);
        }
        basicsInventory.setItem(10, itemStack1);
        basicsInventory.setItem(12, itemStack2);
        basicsInventory.setItem(14, itemStack3);
        basicsInventory.setItem(16, itemStack4);
        basicsInventory.setItem(17, itemStack);

        for (int i = 18; i < 27; i++) {
            basicsInventory.setItem(i, itemStack);
        }

        player.openInventory(basicsInventory);
    }

    private void setDisplayName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.addEnchant(Enchantment.DURABILITY, 1, true); // Add enchantment
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); // Hide enchantment
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
    }

    public void jobShop(Player player) {
        Inventory jobShopInventory = Bukkit.createInventory(null, 45, "Job Change Shop");

        ItemStack itemStack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);

        JobConfigManager jobConfigManager = plugin.getJobConfig();
        ItemStack customItem1 = createCustomItemForGUI(player, "Miner", "1st Class", 5000, "coins");
        ItemStack customItem2 = createCustomItemForGUI(player, "Miner", "2nd Class", 30000, "coins");
        ItemStack customItem3 = createCustomItemForGUI(player, "Miner", "3rd Class", 65000, "coins");
        ItemStack customItem4 = createCustomItemForGUI(player, "Miner", "4th Class", 100000, "coins");

        for (int i = 0; i < 10; i++) {
            jobShopInventory.setItem(i, itemStack);
        }
        jobShopInventory.setItem(17, itemStack);
        jobShopInventory.setItem(18, itemStack);

        jobShopInventory.setItem(19, customItem1);
        jobShopInventory.setItem(21, customItem2);
        jobShopInventory.setItem(23, customItem3);
        jobShopInventory.setItem(25, customItem4);

        jobShopInventory.setItem(26, itemStack);
        jobShopInventory.setItem(27, itemStack);
        jobShopInventory.setItem(35, itemStack);
        for (int i = 36; i < 45; i++) {
            jobShopInventory.setItem(i, itemStack);
        }
        player.openInventory(jobShopInventory);
    }

    private ItemStack createCustomItemForGUI(Player player, String command, String job, int cost, String unit) {
        ItemStack customItem = new ItemStack(Material.ENCHANTED_BOOK); // Create custom item as enchanted book
        ItemMeta customItemData = customItem.getItemMeta(); // Load the item's data into custom item data.
        String jobColor = "";

        // Set item name with setDisplayName
        if (command.equals("Miner")) {
            jobColor = "&7&l";
        }
        customItemData.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&l[Job Change] " + "&r" + jobColor + command + " " + job));

        // Add a list to store the lore of the custom item, overwriting existing lore.
        // The new lore of the custom item is stored in customItemExplain.
        List<String> customItemExplain = new ArrayList<>();
        customItemExplain.add(ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "Change job to " + command + " " + job + "."); // First line of job change book description
        customItemExplain.add(ChatColor.RESET + "" + ChatColor.YELLOW + "" + ChatColor.BOLD + "Price: " + ChatColor.RESET + "" + ChatColor.YELLOW + cost + " " + unit); // Second line of job change book description
        customItemData.setLore(customItemExplain); // Set custom item lore in custom item data (not yet saved to custom item).
        customItem.setItemMeta(customItemData); // Set the values stored in custom item data to the custom item.
        return customItem; // Return the custom item
    }

    public void mineralShop(Player player) {
        Inventory mineralShopInventory = Bukkit.createInventory(null, 45, "Mineral Shop");

        ItemStack itemStack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);

        ItemStack itemStack1 = new ItemStack(Material.COAL, 1);
        ItemStack itemStack2 = new ItemStack(Material.COPPER_INGOT, 1);
        ItemStack itemStack3 = new ItemStack(Material.IRON_INGOT, 1);
        ItemStack itemStack4 = new ItemStack(Material.GOLD_INGOT, 1);
        ItemStack itemStack5 = new ItemStack(Material.REDSTONE, 1);
        ItemStack itemStack6 = new ItemStack(Material.LAPIS_LAZULI, 1);
        ItemStack itemStack7 = new ItemStack(Material.EMERALD, 1);
        ItemStack itemStack8 = new ItemStack(Material.DIAMOND, 1);
        ItemStack itemStack9 = new ItemStack(Material.AMETHYST_SHARD, 1);
        ItemStack itemStack10 = new ItemStack(Material.QUARTZ, 1);
        ItemStack itemStack11 = new ItemStack(Material.NETHERITE_INGOT, 1);

        setItemMeta(itemStack1, "Coal", 30, 960, 1920);
        setItemMeta(itemStack2, "Copper Ingot", 40, 1280, 2560);
        setItemMeta(itemStack3, "Iron Ingot", 50, 1600, 3200);
        setItemMeta(itemStack4, "Gold Ingot", 60, 1920, 3840);
        setItemMeta(itemStack5, "Redstone Dust", 20, 640, 1280);
        setItemMeta(itemStack6, "Lapis Lazuli", 80, 2560, 5120);
        setItemMeta(itemStack7, "Emerald", 90, 2880, 5760);
        setItemMeta(itemStack8, "Diamond", 100, 3200, 6400);
        setItemMeta(itemStack9, "Amethyst Shard", 120, 3840, 7680);
        setItemMeta(itemStack10, "Nether Quartz", 150, 4800, 9600);
        setItemMeta(itemStack11, "Netherite Ingot", 250, 8000, 16000);

        for (int i = 0; i < 10; i++) {
            mineralShopInventory.setItem(i, itemStack);
        }
        mineralShopInventory.setItem(10, itemStack1);
        mineralShopInventory.setItem(12, itemStack2);
        mineralShopInventory.setItem(14, itemStack3);
        mineralShopInventory.setItem(16, itemStack4);
        mineralShopInventory.setItem(17, itemStack);
        mineralShopInventory.setItem(18, itemStack);
        mineralShopInventory.setItem(20, itemStack5);
        mineralShopInventory.setItem(22, itemStack6);
        mineralShopInventory.setItem(24, itemStack7);
        mineralShopInventory.setItem(26, itemStack);
        mineralShopInventory.setItem(27, itemStack);
        mineralShopInventory.setItem(28, itemStack8);
        mineralShopInventory.setItem(30, itemStack9);
        mineralShopInventory.setItem(32, itemStack10);
        mineralShopInventory.setItem(34, itemStack11);
        mineralShopInventory.setItem(35, itemStack);
        for (int i = 36; i < 45; i++) {
            mineralShopInventory.setItem(i, itemStack);
        }
        player.openInventory(mineralShopInventory);
    }

    private void setItemMeta(ItemStack itemStack, String displayName, int leftClickCost, int middleClickCost, int rightClickCost) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + displayName);
            meta.addEnchant(Enchantment.DURABILITY, 1, true); // Add enchantment
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); // Hide enchantment
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            List<String> lore = createLoreList(leftClickCost, middleClickCost, rightClickCost);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
    }

    private List<String> createLoreList(int leftClickCost, int middleClickCost, int rightClickCost) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + "" + ChatColor.YELLOW + "" + ChatColor.BOLD + " [Left Click] " + ChatColor.RESET + "" + ChatColor.YELLOW + "Sell 1: " + leftClickCost + " coins ");
        lore.add(ChatColor.RESET + "" + ChatColor.YELLOW + "" + ChatColor.BOLD + " [Middle Click] " + ChatColor.RESET + "" + ChatColor.YELLOW + "Sell 32: " + middleClickCost + " coins ");
        lore.add(ChatColor.RESET + "" + ChatColor.YELLOW + "" + ChatColor.BOLD + " [Right Click] " + ChatColor.RESET + "" + ChatColor.YELLOW + "Sell 64: " + rightClickCost + " coins ");
        return lore;
    }

    public void clues(Player player, boolean clue1Unlocked, boolean clue2Unlocked, boolean clue3Unlocked) {
        Inventory cluesInventory = Bukkit.createInventory(null, 27, "Clue Resolution");

        ItemStack itemStack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemStack itemStack1 = new ItemStack(Material.PAPER, 1);
        ItemStack itemStack2 = new ItemStack(Material.PAPER, 1);
        ItemStack itemStack3 = new ItemStack(Material.PAPER, 1);
        ItemStack itemStack4 = new ItemStack(Material.NETHER_STAR, 1);

        ItemMeta meta1 = itemStack1.getItemMeta();
        ItemMeta meta2 = itemStack2.getItemMeta();
        ItemMeta meta3 = itemStack3.getItemMeta();
        ItemMeta meta4 = itemStack4.getItemMeta();

        if (clue1Unlocked && meta1 != null) {
            meta1.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Clue 1");
            List<String> lore1 = new ArrayList<>();
            lore1.add(ChatColor.DARK_PURPLE + "The altar is within ±3000 coordinates of the world spawn.");
            lore1.add(ChatColor.RED + "You must unlock all clues to offer at the altar.");
            meta1.setLore(lore1);
            itemStack1.setItemMeta(meta1);
        }

        if (clue2Unlocked && meta2 != null) {
            meta2.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Clue 2");
            int xCoordinate = plugin.getConfig().getInt("structures.ancient_altar.nearest.x", 0);
            List<String> lore2 = new ArrayList<>();
            lore2.add(ChatColor.DARK_PURPLE + "The x-coordinate of the nearest altar is " + xCoordinate + ".");
            lore2.add(ChatColor.RED + "You must unlock all clues to offer at the altar.");
            meta2.setLore(lore2);
            itemStack2.setItemMeta(meta2);
        }

        if (clue3Unlocked && meta3 != null) {
            meta3.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Clue 3");
            int zCoordinate = plugin.getConfig().getInt("structures.ancient_altar.nearest.z", 0);
            List<String> lore3 = new ArrayList<>();
            lore3.add(ChatColor.DARK_PURPLE + "The z-coordinate of the nearest altar is " + zCoordinate + ".");
            lore3.add(ChatColor.RED + "You must unlock all clues to offer at the altar.");
            meta3.setLore(lore3);
            itemStack3.setItemMeta(meta3);
        }

        if (meta4 != null) {
            meta4.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Locked");
            meta4.addEnchant(Enchantment.DURABILITY, 1, true); // Add enchantment
            meta4.addItemFlags(ItemFlag.HIDE_ENCHANTS); // Hide enchantment
            meta4.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta4.setLore(Collections.singletonList(ChatColor.DARK_PURPLE + "Complete specific sales conditions to unlock the clue."));
            itemStack4.setItemMeta(meta4);
        }

        for (int i = 0; i < 10; i++) {
            cluesInventory.setItem(i, itemStack);
        }
        cluesInventory.setItem(11, clue1Unlocked ? itemStack1 : itemStack4);
        cluesInventory.setItem(13, clue2Unlocked ? itemStack2 : itemStack4);
        cluesInventory.setItem(15, clue3Unlocked ? itemStack3 : itemStack4);
        cluesInventory.setItem(17, itemStack);

        for (int i = 18; i < 27; i++) {
            cluesInventory.setItem(i, itemStack);
        }

        player.openInventory(cluesInventory);
    }
}
