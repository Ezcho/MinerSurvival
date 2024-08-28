package org.example.code.rpg.Manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.example.code.rpg.RPG;

public class PlayerScoreboardManager {
    private RPG plugin;
    private Scoreboard scoreboard;
    private Objective objective;

    public PlayerScoreboardManager(RPG plugin) {
        this.plugin = plugin;
    }

    public void setPlayerScoreboard(Player player) {
        FileConfiguration config = plugin.getConfig();
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("1", "dummy", ChatColor.BOLD + "Player Info");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        String playerName = config.getString("users." + player.getUniqueId().toString() + ".name", player.getName());

        Score nameScore = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&lName: " + ChatColor.YELLOW + "&l" + playerName));
        nameScore.setScore(3);

        String job = config.getString("users." + player.getUniqueId().toString() + ".job", "Miner");
        String level = config.getString("users." + player.getUniqueId().toString() + ".level", "1st Class");
        Score jobScore = objective.getScore(ChatColor.BOLD + "Job: " + job + " " + level);
        jobScore.setScore(2);

        double balance = config.getDouble("users." + player.getUniqueId().toString() + ".economy", 0.0);
        int roundedBalance = (int) balance;
        Score moneyScore = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&lMoney: " + ChatColor.YELLOW + roundedBalance + "&l coins"));
        moneyScore.setScore(1);

        player.setScoreboard(scoreboard);
    }
}
