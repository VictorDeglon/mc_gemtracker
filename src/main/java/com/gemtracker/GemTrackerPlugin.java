package com.gemtracker;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main entry point for the GemTracker plugin. Bukkit will create an
 * instance of this class when the plugin is loaded and invoke the lifecycle
 * methods defined by {@link JavaPlugin}.
 */
public class GemTrackerPlugin extends JavaPlugin {
    /** Manager responsible for storing gem data and running background tasks. */
    private GemManager gemManager;

    @Override
    public void onEnable() {
        /*
         * Called by the server when the plugin is enabled. Here we set up all of
         * our components: the GemManager, event listeners and command handlers.
         */
        this.gemManager = new GemManager(this);

        // Register event listeners so we can detect player activity
        getServer().getPluginManager().registerEvents(new PlayerListener(gemManager), this);

        // Configure the /gems command executor and tab completer
        GemCommand command = new GemCommand(gemManager);
        getCommand("gems").setExecutor(command);
        getCommand("gems").setTabCompleter(command);

        // Start repeating tasks for awarding gems and updating the action bar
        gemManager.startTasks();

        getLogger().info("GemTracker enabled");
    }

    @Override
    public void onDisable() {
        // Persist any in-memory gem data before the plugin is unloaded
        gemManager.saveAll();
        getLogger().info("GemTracker disabled");
    }
}
