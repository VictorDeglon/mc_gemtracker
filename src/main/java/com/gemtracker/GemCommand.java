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
 * Implements the <code>/gems</code> command and tab completion.
 *
 * <p>This command allows players to check their own gem balance, view another
 * player's balance or (for administrators) modify a player's gem count. The
 * command structure is as follows:</p>
 *
 * <ul>
 *   <li><code>/gems</code> – display your own gems</li>
 *   <li><code>/gems &lt;player&gt;</code> – display another player's gems</li>
 *   <li><code>/gems set &lt;player&gt; &lt;amount&gt;</code> – set the player's gems</li>
 *   <li><code>/gems add &lt;player&gt; &lt;amount&gt;</code> – add gems to the player</li>
 * </ul>
 */
public class GemCommand implements CommandExecutor, TabCompleter {
    /** Gem management API used to query and modify stored values. */
    private final GemManager gemManager;

    /**
     * Constructs the command executor.
     *
     * @param gemManager manager responsible for gem persistence
     */
    public GemCommand(GemManager gemManager) {
        this.gemManager = gemManager;
    }

    /**
     * Handles the execution of the command.
     *
     * @return true always to indicate the command was processed
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // No arguments: player is asking for their own gem count
        if (args.length == 0) {
            // Console cannot have gems, so require a player
            if (!(sender instanceof Player)) {
                sender.sendMessage("Usage: /gems <player>");
                return true;
            }

            Player player = (Player) sender;
            int gems = gemManager.getGems(player.getUniqueId());
            sender.sendMessage("You have " + gems + " gems.");
            return true;
        }

        // Single argument: show gems for another player
        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            int gems = gemManager.getGems(target.getUniqueId());
            sender.sendMessage(target.getName() + " has " + gems + " gems.");
            return true;
        }

        // Subcommands with at least three args: set or add gems
        if (args.length >= 3) {
            // Only players with the admin permission may modify gem counts
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

        // Fallback usage message for invalid arguments
        sender.sendMessage("Usage: /gems [player] | set <player> <amount> | add <player> <amount>");
        return true;
    }

    /**
     * Provides tab completion for the first argument of the command.
     *
     * <p>Only "set" and "add" are suggested for players with the admin
     * permission; no suggestions are provided otherwise.</p>
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("set", "add"), Arrays.asList());
        }
        return Arrays.asList();
    }
}
