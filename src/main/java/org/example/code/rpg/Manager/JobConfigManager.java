package org.example.code.rpg.Manager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.code.rpg.RPG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobConfigManager {
    private RPG plugin;

    public JobConfigManager(RPG plugin) {
        this.plugin = plugin;
    }

    public void jobCreate(Player player, String job, String level) {
        FileConfiguration config = plugin.getConfig();
        config.set("users." + player.getUniqueId().toString() + ".job", job);
        config.set("users." + player.getUniqueId().toString() + ".level", level);
        plugin.saveConfig();
    }

    public String getPlayerJob(Player player) {
        FileConfiguration config = plugin.getConfig();
        String job = config.getString("users." + player.getUniqueId().toString() + ".job", "Miner");
        String level = config.getString("users." + player.getUniqueId().toString() + ".level", "1st Class");
        return job + "," + level;
    }

    // Job promotion book name check function (replaced switch case due to length)
    public boolean jobBookNameCheck(String jobBookName) {
        List<String> minerBooks = Arrays.asList("Miner 1st Class", "Miner 2nd Class", "Miner 3rd Class", "Miner 4th Class");
        for (String minerBook : minerBooks) {
            if (jobBookName.contains(minerBook)) {
                return true;
            }
        }
        return false;
    }

    public void createCustomItem(Player player, String command, String job) {
        ItemStack customItem = new ItemStack(Material.ENCHANTED_BOOK); // Create a custom item as an enchanted book
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
        customItemExplain.add(ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "Change job to " + command + " " + job + ".");
        customItemData.setLore(customItemExplain); // Set custom item lore in custom item data (not yet saved to custom item).
        customItem.setItemMeta(customItemData); // Set the values stored in custom item data to the custom item.
        player.getInventory().addItem(customItem); // Give the custom item to the player's inventory
    }

    public void giveCustomItemToPlayer(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() == -1) {
            // If the inventory is full
            player.getWorld().dropItem(player.getLocation(), item);
            player.sendMessage(ChatColor.RED + "Your inventory is full, so the item was dropped on the ground!");
        } else {
            // If there is space in the inventory
            player.getInventory().addItem(item);
        }
    }
}
