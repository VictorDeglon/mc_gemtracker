package com.gemtracker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main entry point for the GemTracker plugin.
 *
 * <p>This class sets up the {@link GemManager}, registers listeners and commands,
 * and starts the repeating tasks that award gems and update the action bar.</p>
 */
public class GemTrackerPlugin extends JavaPlugin {
    /** Handles all gem storage and scheduled tasks. */
    private GemManager gemManager;

    /**
     * Called by Bukkit when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        // Initialize the gem manager and register all listeners and commands
        this.gemManager = new GemManager(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(gemManager), this);

        GemCommand command = new GemCommand(gemManager);
        getCommand("gems").setExecutor(command);
        getCommand("gems").setTabCompleter(command);

        // Kick off the periodic tasks for gem rewards and action bar updates
        gemManager.startTasks();
        getLogger().info("GemTracker enabled");
    }

    /**
     * Called by Bukkit when the plugin is disabled or the server shuts down.
     */
    @Override
    public void onDisable() {
        // Persist any loaded player data before shutdown
        gemManager.saveAll();
        getLogger().info("GemTracker disabled");
    }
}
