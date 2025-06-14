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
 * Handles gem storage, loading and saving for players.
 */
public class GemManager {
    private final GemTrackerPlugin plugin;
    private final File dataFolder;
    private final Map<UUID, Integer> gems = new HashMap<>();
    private final Map<UUID, Long> lastActive = new HashMap<>();

    public GemManager(GemTrackerPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    /** Load gems for a player from disk. */
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

    /** Save gems for a player to disk. */
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

    /** Save all loaded player data. */
    public void saveAll() {
        for (UUID uuid : gems.keySet()) {
            saveGems(uuid);
        }
    }

    /** Get gems for a player (loads from disk if not loaded). */
    public int getGems(UUID uuid) {
        if (!gems.containsKey(uuid)) {
            loadGems(uuid);
        }
        return gems.getOrDefault(uuid, 0);
    }

    /** Set gems for a player. */
    public void setGems(UUID uuid, int amount) {
        gems.put(uuid, amount);
    }

    /** Add gems to a player. */
    public void addGems(UUID uuid, int amount) {
        gems.put(uuid, getGems(uuid) + amount);
    }

    /** Record activity timestamp for a player. */
    public void markActive(UUID uuid) {
        lastActive.put(uuid, System.currentTimeMillis());
    }

    /** Start scheduled tasks for awarding gems and showing action bar. */
    public void startTasks() {
        // Award gems every minute
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    long last = lastActive.getOrDefault(uuid, now);
                    if (now - last <= 60000) {
                        addGems(uuid, 2);
                    }
                }
            }
        }.runTaskTimer(plugin, 1200L, 1200L); // every 60s

        // Send action bar every 5 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int count = getGems(player.getUniqueId());
                    player.sendActionBar("Gems: " + count);
                }
            }
        }.runTaskTimer(plugin, 100L, 100L); // every 5s
    }
}
