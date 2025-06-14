package com.gemtracker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * <p>Central class responsible for persisting and manipulating gem counts for
 * all players on the server. This class handles reading and writing YAML files
 * that store a player's gem balance, as well as scheduling tasks that award
 * gems over time.</p>
 */
public class GemManager {
    /** Reference back to the main plugin instance. */
    private final GemTrackerPlugin plugin;
    /** Folder that holds individual YAML files for each player. */
    private final File dataFolder;
    /** Cached map of player UUIDs to their gem totals. */
    private final Map<UUID, Integer> gems = new HashMap<>();
    /** Map of when a player was last considered "active". */
    private final Map<UUID, Long> lastActive = new HashMap<>();

    /**
     * Create a new manager. The constructor also ensures the data folder
     * exists so that player files can be stored inside it.
     */
    public GemManager(GemTrackerPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        // Create the folder the first time the plugin runs
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    /**
     * Load gem data for the given player from disk. If no file exists we start
     * the player at zero gems. This method also marks the player as currently
     * active.
     */
    public void loadGems(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        if (file.exists()) {
            // Read the YAML configuration to obtain the stored gem amount
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            gems.put(uuid, cfg.getInt("gems"));
        } else {
            // First time seeing this player, initialise with zero gems
            gems.put(uuid, 0);
        }
        // Record the current timestamp so we know the player is active
        lastActive.put(uuid, System.currentTimeMillis());
    }

    /**
     * Save the given player's gems back to disk. If we have never loaded gems
     * for this player then nothing is written.
     */
    public void saveGems(UUID uuid) {
        Integer amount = gems.get(uuid);
        // Only write a file if we actually have data in memory
        if (amount == null) return;
        File file = new File(dataFolder, uuid.toString() + ".yml");
        FileConfiguration cfg = new YamlConfiguration();
        cfg.set("gems", amount);
        try {
            cfg.save(file);
        } catch (IOException e) {
            // Log any failure so administrators know something went wrong
            plugin.getLogger().warning("Could not save data for " + uuid + ": " + e.getMessage());
        }
    }

    /**
     * Save gem data for all players that are currently loaded into memory. This
     * is called when the server shuts down to ensure nothing is lost.
     */
    public void saveAll() {
        for (UUID uuid : gems.keySet()) {
            saveGems(uuid);
        }
    }

    /**
     * Get the gem count for a specific player. If the data hasn't been loaded
     * yet we attempt to load it from disk first.
     */
    public int getGems(UUID uuid) {
        if (!gems.containsKey(uuid)) {
            loadGems(uuid);
        }
        return gems.getOrDefault(uuid, 0);
    }

    /**
     * Replace the player's gem count with the specified value.
     */
    public void setGems(UUID uuid, int amount) {
        gems.put(uuid, amount);
    }

    /**
     * Add the given amount of gems to the player. The current value will be
     * loaded if necessary.
     */
    public void addGems(UUID uuid, int amount) {
        gems.put(uuid, getGems(uuid) + amount);
    }

    /**
     * Update the last activity timestamp for a player. This is called whenever
     * we detect player movement or interaction so that scheduled tasks know the
     * player is active.
     */
    public void markActive(UUID uuid) {
        lastActive.put(uuid, System.currentTimeMillis());
    }

    /**
     * Start two repeating tasks:
     * <ul>
     *   <li>Every minute players who were recently active receive two gems.</li>
     *   <li>Every five seconds players are shown their current gem total in the
     *       action bar.</li>
     * </ul>
     */
    public void startTasks() {
        // Task that awards gems once per minute
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    long last = lastActive.getOrDefault(uuid, now);
                    // If the player has been active in the last 60 seconds,
                    // reward them with some gems
                    if (now - last <= 60000) {
                        addGems(uuid, 2);
                    }
                }
            }
        }.runTaskTimer(plugin, 1200L, 1200L); // 1200 ticks = 60 seconds

        // Task that displays the gem count in the action bar
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int count = getGems(player.getUniqueId());
                    player.sendActionBar("Gems: " + count);
                }
            }
        }.runTaskTimer(plugin, 100L, 100L); // 100 ticks = 5 seconds
    }
}
