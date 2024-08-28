package org.example.code.rpg.Command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.code.rpg.RPG;

import java.util.Collections;

public class NameChangeTokenCommand implements CommandExecutor {
    private RPG plugin;

    public NameChangeTokenCommand(RPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        // Check if the player is OP
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        ItemStack nameChangeToken = createNameChangeToken();
        player.getInventory().addItem(nameChangeToken);
        player.sendMessage(ChatColor.GREEN + "You have received a Name Change Token!");

        return true;
    }

    public ItemStack createNameChangeToken() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Name Change Token");
            meta.setLore(Collections.singletonList(ChatColor.RED + "The changed name cannot be changed again."));
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }
}
