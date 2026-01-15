# WelcomeMessagePlugin

A simple Hytale plugin that sends custom welcome messages when players join your server.

## Check us out
[Snipr](https://hytale.snipr.me/)

## What It Does

- Sends a personalized welcome message to players
- Optional server-wide join announcements
- Fully configurable via JSON

## Installation

1. Download the JAR from [Releases](../../releases)
2. Place it in your server's `mods/` folder
3. Restart your server
4. Done!

## Configuration

The config generates at: `mods/Snipr_WelcomeMsg/WelcomeMessage.json`

```json
{
  "MessagePrefix": "[Server]",
  "WelcomeMessage": "Welcome to the server, {player}! Enjoy your stay!",
  "BroadcastJoin": true,
  "DelaySeconds": 0
}

```

**Options:**
- `WelcomeMessage` - Your custom message (use `{player}` for player name)
- `BroadcastJoin` - `true` = everyone sees joins, `false` = private welcome only
- `DelaySeconds` - Not yet implemented

## Building It Yourself

**Requirements:** Java 21+, HytaleServer.jar

```bash
# Place HytaleServer.jar in libs/ folder first
gradlew shadowJar
```

Output: `build/libs/WelcomeMessagePlugin-1.0.0.jar`

## Source Code

All plugin code is in the `/src` folder. Copy it into your own Hytale plugin project if you want to customize it.

**Package:** `com.snipr.welcomemessage`

## License

MIT - Use it however you want!

---

Made by hate.bio



## Looking for quality hosting?
[![Kinetic Hosting - Hytale Server Hosting](https://i.ibb.co/5XFkWtyy/KH-Curse-Forge-Final-Wide-Banner-Hytale-Small.png)](https://billing.kinetichosting.com/aff.php?aff=1251)

