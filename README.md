# GemTracker

GemTracker is a Bukkit plugin for Minecraft 1.21.1 that rewards players with
in-game "gems" for being active on the server. Gems accumulate while a player
moves or interacts and are displayed on the action bar above the hotbar. If a
player is idle for more than 60 seconds they stop earning gems until they become
active again.

## Features

- **Automatic Gem Rewards** – players earn 2 gems every minute of activity.
- **Idle Detection** – no gems are granted when a player is inactive for 60
  seconds or longer.
- **Persistent Storage** – gem totals are stored in a YAML file per player and
  automatically loaded on join and saved on quit or server shutdown.
- **Action Bar Display** – each player's current gem count is shown on their
  action bar every five seconds.
- **Commands** – query and modify gems with the `/gems` command and its
  subcommands.

## Building

This project uses Gradle. A `Makefile` is provided for convenience. Run the
following command to compile the plugin:

```bash
make build
```

The build will produce a JAR in `build/libs/` which can be placed in your
server's `plugins/` directory.

## Installation

1. Build the plugin using the steps above.
2. Copy the resulting `GemTracker-1.0-SNAPSHOT.jar` to the `plugins/` folder of
your Spigot or Paper server.
3. Start the server. The plugin will create a `GemTracker` folder to store player
data.

## Commands

```
/gems                - show your current gem total
/gems <player>       - view another player's gems
/gems set <player> <amount> - set a player's gem count (requires gemtracker.admin)
/gems add <player> <amount> - add gems to a player (requires gemtracker.admin)
```

All players have permission to use `/gems` by default. Administrators require
the `gemtracker.admin` permission for the `set` and `add` subcommands.

## Data Files

Player data is stored in the plugin's `playerdata/` directory. Each player has a
file named `<uuid>.yml` containing a single `gems` value. These files are loaded
on join and written on quit or server shutdown.

## Development

The code is heavily commented to make it easy to follow. Contributions are
welcome; feel free to open pull requests or issues.

## License

This project is provided under the MIT License. See the [LICENSE](LICENSE) file
for details.
