# EnderBan

Bukkit/Paper plugin that blocks configured items from being placed into Ender Chests.

## Features

- Configurable list of banned materials (`config.yml`)
- Messages in English, Russian and Spanish
- `/ebreload` command to reload config and language files without a restart

## Compatibility

Minecraft 26.1 - 26.3, on Spigot, Paper, Purpur and Folia.

## Layout

- `src/` - the Paper plugin (Maven)
- `fabric/` - Fabric mod for 1.21.11, built to be back-ported to earlier versions
- `forge/` - early, unfinished scaffolding for a Forge port
