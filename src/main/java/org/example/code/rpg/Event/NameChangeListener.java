package org.example.code.rpg.Event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.code.rpg.RPG;

public class NameChangeListener implements Listener {
    private RPG plugin;

    public NameChangeListener(RPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.PAPER && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && ChatColor.stripColor(meta.getDisplayName()).equals("Name Change Ticket")) {
                player.sendMessage(ChatColor.GREEN + "Please enter the new name in the chat!");
                plugin.getNameChangeManager().addPlayerToNameChangeList(player);
                event.setCancelled(true);
            }
        }
    }
}
