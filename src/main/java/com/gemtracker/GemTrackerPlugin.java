package com.gemtracker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for GemTracker.
 */
public class GemTrackerPlugin extends JavaPlugin {
    private GemManager gemManager;

    @Override
    public void onEnable() {
        // Initialize manager and register listeners/commands
        this.gemManager = new GemManager(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(gemManager), this);
        GemCommand command = new GemCommand(gemManager);
        getCommand("gems").setExecutor(command);
        getCommand("gems").setTabCompleter(command);

        // Start recurring tasks
        gemManager.startTasks();
        getLogger().info("GemTracker enabled");
    }

    @Override
    public void onDisable() {
        // Save all player data
        gemManager.saveAll();
        getLogger().info("GemTracker disabled");
    }
}
