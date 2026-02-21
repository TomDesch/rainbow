# 🌈 Rainbow Armor Plugin

A stylish Spigot plugin for Minecraft 1.21+ that gives players **color-cycling leather armor** — because why wear armor when you can wear art?

## ✨ Features

- 🎨 Smoothly animated rainbow colors on leather armor.
- ⚔️ Player-bound mode: `/rainbow [cycleSpeed]` command toggles animation on/off per player.
- 📦 Item-bound mode: `/rainbowitem [cycleSpeed]` gives tagged armor items that retain their animation wherever they go.
- 🐎 Horse item mode: `/rainbowhorseitem [cycleSpeed]` gives tagged horse armor items that retain their animation wherever they go.
- 🔄 Fully configurable animation speed and behavior.
- ⚙️ Lightweight and configurable: toggle exactly where animations should occur
- 🧪 Built-in reload command: `/rainbowreload`

## 📦 Installation

1. Drop `RainbowArmor.jar` into your `plugins/` folder.
2. Start or reload your server.
3. Customize the plugin in `config.yml` as desired.

## 📜 Commands

| Command                          | Description                                                                   |
|----------------------------------|-------------------------------------------------------------------------------|
| `/rainbow [cycleSpeed]`          | Toggles rainbow armor for yourself. Requires `rainbow.use`.                   |
| `/rainbowitem [cycleSpeed]`      | Gives you an item-bound set of rainbow armor. Requires `rainbow.item`.        |
| `/rainbowhorseitem [cycleSpeed]` | Gives you an item-bound set of rainbow horse armor. Requires `rainbow.horse`. |
| `/rainbowreload`                 | Reloads the config without restarting. Requires `rainbow.reload`.             |

## 🔑 Permissions

| Permission       | Description                      |
|------------------|----------------------------------|
| `rainbow.use`    | Allow use of `/rainbow`          |
| `rainbow.reload` | Allow use of `/rainbowreload`    |
| `rainbow.item`   | Allow use of `/rainbowitem`      |
| `rainbow.horse`  | Allow use of `/rainbowhorseitem` |

## 🛡️ Armor Interaction Restrictions

While wearing rainbow armor, certain inventory interactions are intentionally restricted to prevent item duplication or destruction of the animated pieces:

| Action                                                     | Allowed? | Reason                                                                            |
|------------------------------------------------------------|----------|-----------------------------------------------------------------------------------|
| Move non-armor items freely (bag ↔ bag, bag ↔ chest, etc.) | ✅ Yes    | Safe — unrelated to rainbow slots                                                 |
| Pick up / place armor pieces in bag or chest by clicking   | ✅ Yes    | Safe — armor slots are not involved                                               |
| Drag armor pieces onto bag or chest slots                  | ✅ Yes    | Safe — armor slots are not involved                                               |
| Click directly on an armor slot                            | ❌ No     | Would pick up the rainbow piece, causing duplication via the animator             |
| Drag anything onto an armor slot                           | ❌ No     | Would overwrite and destroy the rainbow piece in that slot                        |
| Shift-click any armor item                                 | ❌ No     | Minecraft auto-routes it to the nearest armor slot, overwriting the rainbow piece |
| Right-click-equip armor from hand                          | ❌ No     | Auto-equips directly into the armor slot, overwriting the rainbow piece           |
| Hotbar number-key swap involving armor                     | ❌ No     | Would swap an armor piece into or out of the rainbow slot                         |

## 🛠 Configuration

The config allows you to fine-tune performance vs. visual fidelity. Here's a summary:

- `cycle-speed`: How fast the colors animate.
- `armor-item-feature`: Whether `/rainbowitem` is enabled.
- `check-block-inventories`, etc.: Toggle which inventories/entities are to animate items in.

## 👤 Author

Plugin created by **StealingDaPenta**

Have suggestions, feedback, or feature requests? Join the Discord server: [Penta’s Plugin Garage](https://stealingdapenta.be)

---

