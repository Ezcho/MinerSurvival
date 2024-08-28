package org.example.code.rpg.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.code.rpg.Manager.MoneyManager;
import org.example.code.rpg.Manager.PlayerScoreboardManager;
import org.example.code.rpg.RPG;

public class MoneyCommand implements CommandExecutor {
    private RPG plugin;

    public MoneyCommand(RPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MoneyManager moneyManager = new MoneyManager(this.plugin);
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is available only to players.");
            return true;
        }
        if (command.getName().equals("money") && args.length == 0) {
            Player player = ((Player) sender).getPlayer();
            player.sendMessage("Balance: " + moneyManager.getBalance(player));
        }

        Player player = (Player) sender;

        //   /money deposit (player name) (amount)
        // money = (this is just the command), deposit = args[0], player name = args[1], amount = args[2]

        // If the player is an op
        if (args.length == 3) {
            Player targetPlayer = plugin.getServer().getPlayer(args[1]);
            if (command.getName().equals("money") && args[0].equals("deposit")) {
                // If the specified player cannot be found among the players on the server
                if (targetPlayer == null) {
                    player.sendMessage(ChatColor.RED + "The player could not be found.");
                    return true;
                }
                try {
                    if (player.isOp()) {
                        int depositAmount = Integer.parseInt(args[2]);
                        // If the amount to be deposited is not positive
                        if (depositAmount < 0) {
                            player.sendMessage(ChatColor.RED + "Please enter the amount to be deposited as a positive number.");
                            return true;
                        }
                        moneyManager.addBalance(targetPlayer, depositAmount); // Add depositAmount to the target player's balance
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully deposited &e" + depositAmount + "&a to &e" + targetPlayer.getName() + "&a."));
                        targetPlayer.sendMessage(ChatColor.YELLOW + Integer.toString(depositAmount) + ChatColor.GREEN + " has been " + ChatColor.YELLOW + "deposited" + ChatColor.GREEN + ".");
                    } else {
                        player.sendMessage(ChatColor.RED + "This command can only be used by ops.");
                    }
                    // Update scoreboard
                    if (targetPlayer.isOnline()) {
                        PlayerScoreboardManager scoreboardManager = new PlayerScoreboardManager(plugin);
                        scoreboardManager.setPlayerScoreboard(targetPlayer);
                    }
                    // Invalid format (non-numeric strings like letters or special characters)
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "The amount format is incorrect.");
                }
            } else if (command.getName().equals("money") && args[0].equals("send")) {
                if (targetPlayer == null) {
                    player.sendMessage(ChatColor.RED + "The specified player could not be found.");
                    return true;
                }
                // Prevent sending money to oneself
                if (player.equals(targetPlayer)) {
                    player.sendMessage(ChatColor.RED + "You cannot send money to yourself.");
                    return true;
                }
                try {
                    int transferAmount = Integer.parseInt(args[2]);
                    if (transferAmount < 0) {
                        player.sendMessage(ChatColor.RED + "Please enter a positive amount to transfer.");
                        return true;
                    }
                    // Check the balance of the sender
                    if (moneyManager.getBalance(player) < transferAmount) {
                        player.sendMessage(ChatColor.RED + "Insufficient balance.");
                        return true;
                    }
                    // Deduct the transfer amount from the sender's balance
                    moneyManager.subtractBalance(player, transferAmount);
                    // Add the transfer amount to the target player's balance
                    moneyManager.addBalance(targetPlayer, transferAmount);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully transferred &e" + transferAmount + "&a to &e" + targetPlayer.getName() + "&a."));
                    targetPlayer.sendMessage(ChatColor.YELLOW + Integer.toString(transferAmount) + ChatColor.GREEN + " has been " + ChatColor.YELLOW + "transferred" + ChatColor.GREEN + " to your account.");

                    // Update scoreboard
                    if (targetPlayer.isOnline()) {
                        PlayerScoreboardManager scoreboardManager = new PlayerScoreboardManager(plugin);
                        scoreboardManager.setPlayerScoreboard(player); // Update sender's scoreboard
                        if (targetPlayer.isOnline()) {
                            scoreboardManager.setPlayerScoreboard(targetPlayer); // Update target player's scoreboard
                        }
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "The amount format is incorrect.");
                }
            } else if (command.getName().equals("money") && args[0].equals("withdraw")) {
                // If the specified player cannot be found among the players on the server
                if (targetPlayer == null) {
                    player.sendMessage(ChatColor.RED + "The specified player could not be found.");
                    return true;
                }
                try {
                    if (player.isOp()) {
                        int withdrawAmount = Integer.parseInt(args[2]);
                        if (withdrawAmount < 0) {
                            player.sendMessage(ChatColor.RED + "Please enter a positive amount to withdraw.");
                            return true;
                        }
                        if (moneyManager.getBalance(targetPlayer) < withdrawAmount) {
                            player.sendMessage(ChatColor.RED + "Insufficient balance.");
                            return true;
                        }
                        moneyManager.subtractBalance(targetPlayer, withdrawAmount);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully withdrew &e" + withdrawAmount + "&a from &e" + targetPlayer.getName() + "&a's balance."));
                        targetPlayer.sendMessage(ChatColor.RED + Integer.toString(withdrawAmount) + ChatColor.GREEN + " has been " + ChatColor.RED + "withdrawn" + ChatColor.GREEN + " from your account.");
                    } else {
                        player.sendMessage(ChatColor.RED + "This command can only be used by ops.");
                    }
                    // Update scoreboard
                    if (targetPlayer.isOnline()) {
                        PlayerScoreboardManager scoreboardManager = new PlayerScoreboardManager(plugin);
                        scoreboardManager.setPlayerScoreboard(targetPlayer);
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "The amount format is incorrect.");
                }
                return true;
            }
        }
        return true;
    }
}
