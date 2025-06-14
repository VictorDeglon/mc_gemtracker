package com.gemtracker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player join/quit and activity detection.
 */
public class PlayerListener implements Listener {
    private final GemManager gemManager;

    public PlayerListener(GemManager gemManager) {
        this.gemManager = gemManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        gemManager.loadGems(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        gemManager.saveGems(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
            gemManager.markActive(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        gemManager.markActive(event.getPlayer().getUniqueId());
    }
}
