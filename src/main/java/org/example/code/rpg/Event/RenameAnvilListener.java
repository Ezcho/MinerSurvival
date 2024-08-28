package org.example.code.rpg.Event;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class RenameAnvilListener implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        ItemStack resultItem = anvilInventory.getItem(2); // Anvil result item slot (slot 2)

        if (resultItem != null && resultItem.hasItemMeta() && resultItem.getItemMeta().hasDisplayName()) {
            String newName = resultItem.getItemMeta().getDisplayName();

            // Prevent renaming to names containing specific words
            if (newName.contains("Miner 1st Class") || newName.contains("Miner 2nd Class") || newName.contains("Miner 3rd Class") || newName.contains("Miner 4th Class")) {
                event.setResult(null);
                // Send a message to the player
                event.getView().getPlayer().sendMessage(ChatColor.RED + "You cannot rename this item to include 'Miner'.");
            }
        }
    }
}
