# GemTracker

GemTracker is a lightweight [Spigot](https://www.spigotmc.org/) plugin that rewards players with in-game **gems** for actively playing on your Minecraft server. The plugin demonstrates simple data storage using YAML files and basic command handling. It can serve as a starting point for more advanced reward systems or economy plugins.

## Features

- Tracks playtime activity and awards gems automatically.
- Displays the player's current gem balance in the action bar every few seconds.
- `/gems` command for players to check their own or another player's gem count.
- Administrative subcommands to set or add gems for any player.

## Building

This project uses **Gradle** for compilation and testing. The repository also provides a `Makefile` with convenience targets. You need a JDK (Java 17 or newer) and Gradle installed.

```bash
# Compile the plugin and create the JAR in build/libs
make build

# Run tests (none at the moment but included for completeness)
make test

# Remove build artifacts
make clean
```

The resulting JAR can be found in `build/libs/` and should be placed in your server's `plugins` directory.

## Usage

Once the plugin has been copied to your server and the server has been restarted, players will start accumulating gems automatically while they move or interact. Every minute the plugin checks for recently active players and awards them two gems. Their current gem total is shown in the action bar every five seconds.

### Commands

- `/gems` &ndash; Shows your gem count.
- `/gems <player>` &ndash; Shows the gem count of the specified player.
- `/gems set <player> <amount>` &ndash; (Requires `gemtracker.admin`) Sets a player's gem total.
- `/gems add <player> <amount>` &ndash; (Requires `gemtracker.admin`) Adds gems to a player.

### Permissions

- `gemtracker.use` &ndash; Allows usage of the `/gems` command (granted to everyone by default).
- `gemtracker.admin` &ndash; Allows use of the administrative subcommands.

## Data Storage

Player gem totals are stored in the plugin's `playerdata` folder using YAML files named after each player's UUID. Files are loaded when a player joins and saved when they leave or when the server stops.

## Contributing

See **AGENT.md** for contribution guidelines. In short: run `gradle test` before committing, keep the code heavily commented, and update this README when necessary.

Enjoy tracking gems on your server!
