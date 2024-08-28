package org.example.code.rpg.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.code.rpg.RPG;

public class PluginHelpCommand implements CommandExecutor {
    private RPG plugin;

    // Inject the plugin via constructor
    public PluginHelpCommand(RPG plugin) {
        this.plugin = plugin;
    }

    // Define the help message as a constant
    private static final String HELP_MESSAGE = ChatColor.translateAlternateColorCodes('&',
            "The MinerSurvival plugin is a 'Survive as a Miner' plugin.\n" +
                    "Here are the commands available in this plugin:\n" +
                    " \n" +
                    "- List of available general commands -\n\n" +
                    " \n" +
                    "&e/money\n&f: Check your current balance.\n" +
                    " \n" +
                    "&e/money send (player name) (amount)\n&f: Send money to the specified player.\n" +
                    " \n" +
                    "&e/menu\n&f: Open the menu with various functions.\n" +
                    " \n" +
                    " \n" +
                    "- List of available OP commands -\n\n" +
                    " \n" +
                    "&e/money deposit (player name) (amount)\n&f: Deposit money into the player's account from an infinite money account. (OP command)\n" +
                    " \n" +
                    "&e/money withdraw (player name) (amount)\n&f: Withdraw money from the player's balance. (OP command)\n" +
                    " \n" +
                    "&e/miner (1st~4th class)\n&f: Give the miner job change book. (OP command)\n" +
                    " \n" +
                    "&e/namechange\n&f: Give a name change token. (OP command)\n" +
                    " \n" +
                    " \n" +
                    "&eFor bugs, please contact on Discord: 이케#9461."
    );

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Check if the command was called by a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        // Check if the command is 'help' and has no arguments
        if (command.getName().equalsIgnoreCase("mhelp") && args.length == 0) {
            Player player = (Player) sender;
            player.sendMessage(HELP_MESSAGE);  // Send the message defined as a constant
        }
        return true;
    }
}
