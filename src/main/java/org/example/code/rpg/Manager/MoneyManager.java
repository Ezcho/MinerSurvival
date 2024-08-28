package org.example.code.rpg.Manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.example.code.rpg.RPG;

public class MoneyManager {
    private RPG plugin;
    private FileConfiguration config;

    public MoneyManager(RPG plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    // Get player's balance
    public int getBalance(Player player) {
        return config.getInt("users." + player.getUniqueId().toString() + ".economy", 0); // Changed to int
    }

    // Set player's balance
    public void setBalance(Player player, int amount) {
        config.set("users." + player.getUniqueId().toString() + ".economy", amount);
        plugin.saveConfig();
    }

    // Add to player's balance
    public void addBalance(Player player, int amount) {
        setBalance(player, getBalance(player) + amount);
    }

    // Subtract from player's balance
    public void subtractBalance(Player player, int amount) {
        int currentBalance = getBalance(player);
        if (currentBalance >= amount) {
            setBalance(player, currentBalance - amount);
        } else {
            // When attempting to subtract more than the balance
            setBalance(player, 0); // Ensure the balance does not become negative
        }
    }
}
