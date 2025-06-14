package com.gemtracker;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

/**
 * Handles the `/gems` command. Players can check their gem balance and
 * administrators can adjust gem counts.
 */
public class GemCommand implements CommandExecutor, TabCompleter {
    /** Manager used for retrieving and modifying gem data. */
    private final GemManager gemManager;

    public GemCommand(GemManager gemManager) {
        this.gemManager = gemManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // If no arguments were supplied show the sender's own balance
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                // Console must specify a player name
                sender.sendMessage("Usage: /gems <player>");
                return true;
            }
            Player player = (Player) sender;
            int gems = gemManager.getGems(player.getUniqueId());
            sender.sendMessage("You have " + gems + " gems.");
            return true;
        }

        // When one argument is given we treat it as a player lookup
        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            int gems = gemManager.getGems(target.getUniqueId());
            sender.sendMessage(target.getName() + " has " + gems + " gems.");
            return true;
        }

        // Three or more arguments means we're using an admin subcommand
        if (args.length >= 3) {
            if (!sender.hasPermission("gemtracker.admin")) {
                sender.sendMessage("You do not have permission.");
                return true;
            }
            String sub = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            UUID uuid = target.getUniqueId();
            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage("Amount must be a number");
                return true;
            }

            if (sub.equalsIgnoreCase("set")) {
                gemManager.setGems(uuid, amount);
                sender.sendMessage("Set " + target.getName() + "'s gems to " + amount);
            } else if (sub.equalsIgnoreCase("add")) {
                gemManager.addGems(uuid, amount);
                sender.sendMessage("Added " + amount + " gems to " + target.getName());
            } else {
                sender.sendMessage("Unknown subcommand");
            }
            return true;
        }

        // Fallback usage message for anything that didn't match above
        sender.sendMessage("Usage: /gems [player] | set <player> <amount> | add <player> <amount>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Offer tab completion for the admin subcommands
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("set", "add"), Arrays.asList());
        }
        return Arrays.asList();
    }
}
