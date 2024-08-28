package org.example.code.rpg.Command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.code.rpg.Manager.JobConfigManager;
import org.example.code.rpg.RPG;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class JobCommand implements CommandExecutor {
    private RPG plugin;
    public JobCommand(RPG plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is available only to players.");
            return true;
        }
        if (args == null) {
            sender.sendMessage(ChatColor.RED + "Please enter the job and level you wish to change to.");
        }

        Player player = (Player) sender;

        // 플레이어가 OP인지 확인
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        String command2 = command.getName();
        String job = args[0];
        JobConfigManager jobConfigManager = new JobConfigManager(plugin);
        jobConfigManager.createCustomItem(player, command2, job);
        return true;
    }
}