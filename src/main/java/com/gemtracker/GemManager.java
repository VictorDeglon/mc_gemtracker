package com.gemtracker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Central class responsible for managing players' gem totals.
 *
 * <p>All gem values are kept in memory while players are online and flushed to
 * disk when necessary. Gem files are stored under the plugin's
 * <code>playerdata/</code> folder using each player's UUID as the filename.</p>
 */
public class GemManager {
    /** Reference to the main plugin instance. */
    private final GemTrackerPlugin plugin;
    /** Directory where player data files are stored. */
    private final File dataFolder;
    /** In-memory cache of gem counts keyed by player UUID. */
    private final Map<UUID, Integer> gems = new HashMap<>();
    /** Tracks the last time each player performed an action or moved. */
    private final Map<UUID, Long> lastActive = new HashMap<>();

    /**
     * Creates the manager and ensures the data directory exists.
     *
     * @param plugin the owning plugin instance
     */
    public GemManager(GemTrackerPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    /** Load a player's gems from disk, creating an entry if none exists. */
    public void loadGems(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        if (file.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            gems.put(uuid, cfg.getInt("gems"));
        } else {
            gems.put(uuid, 0);
        }
        lastActive.put(uuid, System.currentTimeMillis());
    }

    /** Persist a player's gem count to their YAML file. */
    public void saveGems(UUID uuid) {
        Integer amount = gems.get(uuid);
        if (amount == null) return;
        File file = new File(dataFolder, uuid.toString() + ".yml");
        FileConfiguration cfg = new YamlConfiguration();
        cfg.set("gems", amount);
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save data for " + uuid + ": " + e.getMessage());
        }
    }

    /** Write all loaded player data to disk. */
    public void saveAll() {
        for (UUID uuid : gems.keySet()) {
            saveGems(uuid);
        }
    }

    /**
     * Retrieve a player's current gem balance.
     *
     * <p>If the player has never joined before, their data will be loaded and
     * defaulted to 0.</p>
     */
    public int getGems(UUID uuid) {
        if (!gems.containsKey(uuid)) {
            loadGems(uuid);
        }
        return gems.getOrDefault(uuid, 0);
    }

    /** Overwrite a player's gem total. */
    public void setGems(UUID uuid, int amount) {
        gems.put(uuid, amount);
    }

    /** Increase a player's gems by the given amount. */
    public void addGems(UUID uuid, int amount) {
        gems.put(uuid, getGems(uuid) + amount);
    }

    /** Update the last active timestamp for a player to now. */
    public void markActive(UUID uuid) {
        lastActive.put(uuid, System.currentTimeMillis());
    }

    /**
     * Start the repeating tasks used by the plugin.
     *
     * <p>One task awards gems every minute based on activity and the other
     * updates the action bar every five seconds.</p>
     */
    public void startTasks() {
        // Award gems every minute based on recorded activity
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    long last = lastActive.getOrDefault(uuid, now);
                    // Only award gems if the player moved or interacted within the last minute
                    if (now - last <= 60000) {
                        addGems(uuid, 2);
                    }
                }
            }
        }.runTaskTimer(plugin, 1200L, 1200L); // schedule every 60 seconds

        // Periodically update the action bar so players can see their gems
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int count = getGems(player.getUniqueId());
                    player.sendActionBar("Gems: " + count);
                }
            }
        }.runTaskTimer(plugin, 100L, 100L); // schedule every 5 seconds
    }
}
