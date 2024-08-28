package org.example.code.rpg;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.code.rpg.Command.*;
import org.example.code.rpg.Event.*;
import org.example.code.rpg.Manager.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;

public final class RPG extends JavaPlugin {
    private GuiManager guiManager;
    private JobConfigManager jobConfigManager;
    private MoneyManager moneyManager;
    private PlayerScoreboardManager scoreboardManager;
    private NameChangeManager nameChangeManager;
    private HashMap<UUID, BossBar> playerBossBars = new HashMap<>();
    private Map<UUID, Double> playerO2 = new HashMap<>();
    private final Map<Player, Map<String, Integer>> playerSalesCount = new HashMap<>(); // Newly added part

    @Override
    public void onEnable() {
        getLogger().info("MinerSurvival Plugin has been enabled.");
        this.saveDefaultConfig();
        this.getCommand("miner").setExecutor(new JobCommand(this));
        this.getCommand("money").setExecutor(new MoneyCommand(this));
        this.getCommand("mhelp").setExecutor(new PluginHelpCommand(this));
        this.getCommand("menu").setExecutor(new GuiCommand(this));
        this.getCommand("namechange").setExecutor(new NameChangeTokenCommand(this));
        guiManager = new GuiManager(this);
        jobConfigManager = new JobConfigManager(this);
        moneyManager = new MoneyManager(this);
        scoreboardManager = new PlayerScoreboardManager(this);
        nameChangeManager = new NameChangeManager(this);
        BeaconOfferingListener beaconOfferingListener = new BeaconOfferingListener(this, jobConfigManager);
        WorldInitListener worldInitListener = new WorldInitListener(this, beaconOfferingListener);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, playerBossBars, playerO2), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this, playerBossBars, playerO2), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this, playerO2), this);
        getServer().getPluginManager().registerEvents(new PlayerAttackedListener(playerO2), this);
        getServer().getPluginManager().registerEvents(new MonsterDamageListener(), this);
        getServer().getPluginManager().registerEvents(new RightClickListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this, guiManager, moneyManager, scoreboardManager, jobConfigManager), this);
        getServer().getPluginManager().registerEvents(new RenameAnvilListener(), this);
        getServer().getPluginManager().registerEvents(new NameChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new UnableInstallBedListener(), this);
        getServer().getPluginManager().registerEvents(worldInitListener, this);
        getServer().getPluginManager().registerEvents(beaconOfferingListener, this);
        getServer().getPluginManager().registerEvents(nameChangeManager, this);

        // Load custom structure data pack
        boolean dataPackCopied = false;

        try {
            File dataPackFolder = new File(Bukkit.getWorldContainer(), "world/datapacks/altar");
            if (!dataPackFolder.exists()) {
                dataPackFolder.mkdirs();
                copyResource("data/altar/worldgen/structure/ancient_altar.json", new File(dataPackFolder, "worldgen/structure/ancient_altar.json"));
                copyResource("data/altar/worldgen/structure_set/ancient_altar.json", new File(dataPackFolder, "worldgen/structure_set/ancient_altar.json"));
                copyResource("data/altar/worldgen/template_pool/ancient_altar/altar_centers.json", new File(dataPackFolder, "worldgen/template_pool/ancient_altar/altar_centers.json"));
                dataPackCopied = true;
            }
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to copy data pack files.", e);
        }

        if (dataPackCopied) {
            getLogger().info("Data pack files have been copied. To apply the changes, run/reload the server or restart it.");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("MinerSurvival Plugin has been disabled.");
        this.saveDefaultConfig();
    }

    private void copyResource(String resourcePath, File dest) throws IOException {
        Files.copy(Objects.requireNonNull(getResource(resourcePath)), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public JobConfigManager getJobConfig() {
        return jobConfigManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public MoneyManager getMoneyManager() {
        return moneyManager;
    }

    public PlayerScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public NameChangeManager getNameChangeManager() {
        return nameChangeManager;
    }

    // Save the clue state
    public void saveClueState(Player player, String clue, boolean unlocked) {
        getConfig().set("users." + player.getUniqueId().toString() + "." + clue, unlocked);
        saveConfig();
    }

    // Load the clue state
    public boolean loadClueState(Player player, String clue) {
        return getConfig().getBoolean("users." + player.getUniqueId().toString() + "." + clue, false);
    }

    // Method to set clue sales count per player
    public void setSalesCount(Player player, String clueName, int count) {
        Map<String, Integer> salesCount = playerSalesCount.computeIfAbsent(player, k -> new HashMap<>());
        salesCount.put(clueName, count);
    }

    // Method to get clue sales count per player
    public int getSalesCount(Player player, String clueName) {
        Map<String, Integer> salesCount = playerSalesCount.get(player);
        return salesCount != null ? salesCount.getOrDefault(clueName, 0) : 0;
    }
}
