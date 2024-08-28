package org.example.code.rpg.Event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.code.rpg.Command.NameChangeTokenCommand;
import org.example.code.rpg.Manager.MoneyManager;
import org.example.code.rpg.Manager.PlayerScoreboardManager;
import org.example.code.rpg.RPG;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.createPlayerProfile;
import static org.bukkit.Bukkit.getServer;

public class PlayerJoinListener implements Listener {
    private PlayerScoreboardManager playerScoreboardManager;
    private MoneyManager moneyManager;
    private NameChangeTokenCommand nameChangeTokenCommand;
    private HashMap<UUID, BossBar> playerBossBars;
    private Map<UUID, Double> playerO2;
    private final double initialTime = 600.0;
    private RPG plugin;

    public PlayerJoinListener(RPG plugin, HashMap<UUID, BossBar> playerBossBars, Map<UUID, Double> playerO2) {
        this.plugin = plugin;
        this.playerScoreboardManager = new PlayerScoreboardManager(plugin);
        this.moneyManager = new MoneyManager(plugin);
        this.nameChangeTokenCommand = new NameChangeTokenCommand(plugin);
        this.playerBossBars = playerBossBars;
        this.playerO2 = playerO2;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Retrieve the player's custom name from the config
        FileConfiguration config = plugin.getConfig();
        String playerName = config.getString("users." + player.getUniqueId().toString() + ".name", player.getName());

        // Check if a custom name is set
        if (playerName == null || playerName.isEmpty()) {
            playerName = player.getName(); // Minecraft's default nickname
        }

        // Change the name (both the display name above the player's head and the name in the player list when pressing Tab)
        player.setDisplayName(playerName);
        player.setPlayerListName(playerName);

        event.setJoinMessage(ChatColor.GREEN + "[+] " + ChatColor.WHITE + playerName);

        // Check if the player has joined before -> hasPlayedBefore() method
        if (!player.hasPlayedBefore()) {
            player.sendMessage("Welcome to the server! Use '/help' to check available commands!");
            moneyManager.setBalance(event.getPlayer(), 1000); // Give 1000 coins on first join
            plugin.getJobConfig().jobCreate(player, "No Job", " "); // Set default job and job level
            giveNameChangeTicket(player);
        }

        BossBar bossBar = getServer().createBossBar("Time until oxygen depletion: 10 minutes 00 seconds", BarColor.GREEN, BarStyle.SOLID);
        bossBar.addPlayer(player);
        bossBar.setProgress(1.0); // Set progress to 100%
        bossBar.setVisible(false);
        playerBossBars.put(player.getUniqueId(), bossBar);
        playerO2.put(player.getUniqueId(), initialTime);
        playerScoreboardManager.setPlayerScoreboard(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BossBar bossBar = playerBossBars.remove(event.getPlayer().getUniqueId());
        if (bossBar != null) {
            bossBar.removeAll();
        }
        // Retrieve the player's custom name from the config
        FileConfiguration config = plugin.getConfig();
        String playerName = config.getString("users." + player.getUniqueId().toString() + ".name", player.getName());

        // Change the name (both the display name above the player's head and the name in the player list when pressing Tab)
        player.setDisplayName(playerName);
        player.setPlayerListName(playerName);
        event.setQuitMessage(ChatColor.RED + "[-] " + ChatColor.WHITE + playerName);
    }

    private void giveNameChangeTicket(Player player) {
        player.getInventory().addItem(nameChangeTokenCommand.createNameChangeToken());
    }
}
