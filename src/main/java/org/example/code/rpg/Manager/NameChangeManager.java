package org.example.code.rpg.Manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.code.rpg.RPG;

import java.util.HashSet;
import java.util.Set;

public class NameChangeManager implements Listener {
    private RPG plugin;
    private Set<Player> nameChangeList = new HashSet<>();

    public NameChangeManager(RPG plugin) {
        this.plugin = plugin;
    }

    public void addPlayerToNameChangeList(Player player) {
        nameChangeList.add(player);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (nameChangeList.contains(player)) {
            event.setCancelled(true);
            String newName = event.getMessage().trim();

            Bukkit.getScheduler().runTask(plugin, () -> {
                FileConfiguration config = plugin.getConfig();
                config.set("users." + player.getUniqueId().toString() + ".name", newName);
                plugin.saveConfig();

                player.sendMessage(ChatColor.GREEN + "Your name has been successfully changed to " + ChatColor.YELLOW + newName + ChatColor.GREEN + "!");

                // Update scoreboard
                plugin.getScoreboardManager().setPlayerScoreboard(player);

                // Change name (Minecraft unique nickname above the player, name in the player list when pressing Tab)
                player.setDisplayName(newName);
                player.setPlayerListName(newName);

                // Update the name displayed above the player's head
                player.setCustomName(newName);
                player.setCustomNameVisible(true);

                // Remove the item from the inventory after use
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                if (itemInHand != null && itemInHand.getType() == Material.PAPER && itemInHand.hasItemMeta()) {
                    ItemMeta meta = itemInHand.getItemMeta();
                    if (meta != null && ChatColor.stripColor(meta.getDisplayName()).equals("Name Change Token")) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    }
                }

                // Remove player from the list
                nameChangeList.remove(player);
            });
        }
    }
}
