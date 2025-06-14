package com.gemtracker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener class that hooks into various player events. Whenever a player joins
 * or leaves we load or save their gem data. We also listen for movement and
 * interaction events so we can mark players as active for reward purposes.
 */
public class PlayerListener implements Listener {
    /** Reference to the manager that handles gem persistence. */
    private final GemManager gemManager;

    public PlayerListener(GemManager gemManager) {
        this.gemManager = gemManager;
    }

    /**
     * Load a player's gems when they join the server so the information is
     * available immediately.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        gemManager.loadGems(event.getPlayer().getUniqueId());
    }

    /**
     * Save the player's gem data as soon as they disconnect to minimise the
     * chance of losing progress.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        gemManager.saveGems(event.getPlayer().getUniqueId());
    }

    /**
     * Every time the player moves we mark them as active. This helps the reward
     * task know that the player is actually playing rather than being AFK.
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
            gemManager.markActive(event.getPlayer().getUniqueId());
        }
    }

    /**
     * Any interaction (such as clicking or using items) also counts as activity.
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        gemManager.markActive(event.getPlayer().getUniqueId());
    }
}
