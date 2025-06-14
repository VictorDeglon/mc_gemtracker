package com.gemtracker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Bukkit event listener used to track player activity and data loading.
 */
public class PlayerListener implements Listener {
    /** Reference to the gem manager for persistence and activity updates. */
    private final GemManager gemManager;

    /**
     * Create a new listener bound to the given manager.
     *
     * @param gemManager manager responsible for gem persistence
     */
    public PlayerListener(GemManager gemManager) {
        this.gemManager = gemManager;
    }

    /**
     * Load a player's gem data when they join the server.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        gemManager.loadGems(event.getPlayer().getUniqueId());
    }

    /**
     * Save a player's gem data when they leave the server.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        gemManager.saveGems(event.getPlayer().getUniqueId());
    }

    /**
     * Mark a player as active when they move significantly.
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // Only consider the player active if they actually changed position
        if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
            gemManager.markActive(event.getPlayer().getUniqueId());
        }
    }

    /**
     * Any interaction also counts as activity for gem rewards.
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        gemManager.markActive(event.getPlayer().getUniqueId());
    }
}
