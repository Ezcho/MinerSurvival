package org.example.code.rpg.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.code.rpg.Manager.GuiManager;
import org.example.code.rpg.RPG;

public class GuiCommand implements CommandExecutor {
    private RPG plugin;

    public GuiCommand(RPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is available only to players.");
            return true;
        }
        Player player = (Player) sender;
        GuiManager guiManager = new GuiManager(plugin);
        guiManager.openGui(player);
        return true;
    }

}
