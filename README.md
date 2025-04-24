# 🌈 Rainbow Armor Plugin

A stylish Spigot plugin for Minecraft 1.21+ that gives players **color-cycling leather armor** — because why just wear armor when you can wear art?

## ✨ Features

- 🎨 Smoothly animated rainbow colors on leather armor.
- ⚔️ Player-bound mode: `/rainbow [cycleSpeed]` command toggles animation on/off per player.
- 📦 Item-bound mode: `/rainbowitem [cycleSpeed]` gives tagged armor items that retain their animation wherever they go.
- 🔄 Fully configurable animation speed and behavior.
- ⚙️ Lightweight and configurable: toggle exactly where animations should occur
- 🧪 Built-in reload command: `/rainbowreload`

## 📦 Installation

1. Drop `RainbowArmor.jar` into your `plugins/` folder.
2. Start or reload your server.
3. Customize the plugin in `config.yml` as desired.

## 📜 Commands

| Command | Description |
|--------|-------------|
| `/rainbow [cycleSpeed]` | Toggles rainbow armor for yourself. Requires `rainbow.use`. |
| `/rainbowitem [cycleSpeed]` | Gives you an item-bound set of rainbow armor. Requires `rainbow.item`. |
| `/rainbowreload` | Reloads the config without restarting. Requires `rainbow.reload`. |

## 🔑 Permissions

| Permission | Description |
|------------|-------------|
| `rainbow.use` | Allow use of `/rainbow` |
| `rainbow.reload` | Allow use of `/rainbowreload` |
| `rainbow.item` | Allow use of `/rainbowitem` |

## 🛠 Configuration

The config allows you to fine-tune performance vs visual fidelity. Here's a summary:

- `cycle-speed`: How fast the colors animate.
- `armor-item-feature`: Whether `/rainbowitem` is enabled.
- `check-block-inventories`, etc.: Toggle which inventories/entities to animate items in.

## 👤 Author

Plugin created by **StealingDaPenta**

Have suggestions, feedback, or feature requests? Join the Discord server: [Penta’s Plugin Garage](https://discord.gg/4KtJcSqk)

---

