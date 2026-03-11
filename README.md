# Tyrs Guard Clan Plugin

A RuneLite plugin built exclusively for members of the **Tyr's Guard** OSRS clan. This plugin connects to the clan's private Discord bot to provide seamless integration between the game and the clan's Discord server.

> ⚠️ **This plugin requires access to Tyr's Guard's private clan bot to function. It is intended only for Tyr's Guard clan members. Without the correct configuration it will do nothing.**

---

## Features

### 📸 Screenshot Submissions
Submit screenshots directly from RuneLite to the clan's Discord submissions channel. Screenshots are automatically stamped with your local date and time, along with CST and GMT conversions, so staff can verify submissions across time zones at a glance.

### ⚔️ XP & Rank Tracking
View your current clan XP, rank, and progress toward your next rank directly in the plugin panel. The clan uses a custom rank system based on participation and activity.

### 💬 Clan Chat Bridge *(Opt-in)*
Optionally bridge your in-game clan chat to the clan's Discord channel and vice versa. Messages sent in the Discord clan chat channel will appear in your in-game clan chat, and your in-game clan chat messages will appear in Discord. This feature is **completely optional** and can be toggled off at any time in the plugin settings.

---

## Privacy & Data

- **All features are opt-in.** Nothing is sent anywhere until you configure the plugin with your own Discord ID and the clan bot's URL.
- **No data is collected by this plugin beyond what you explicitly submit.** Screenshots are only sent when you press the submit button.
- **The chat bridge only activates if you enable it in settings.** It can be turned off at any time.
- **Your Discord ID is stored locally in your RuneLite config** and is only used to identify your submissions and look up your XP on the clan bot.
- The plugin communicates exclusively with Tyr's Guard's private clan bot server. No data is sent to any third party.

---

## Setup Instructions

### Step 1 — Get your configuration details from clan staff
You will need the following from a Tyr's Guard staff member:
- The clan bot's API URL
- The plugin API secret key

### Step 2 — Find your Discord ID
1. Open Discord
2. Go to Settings → Advanced → Enable **Developer Mode**
3. Right-click your name anywhere in the Tyr's Guard Discord server
4. Click **Copy User ID**

### Step 3 — Configure the plugin in RuneLite
1. Install the plugin from the RuneLite Plugin Hub
2. Open the plugin settings (wrench icon next to the plugin)
3. Fill in the following fields:
   - **Bot API URL** — provided by clan staff
   - **Plugin API Secret** — provided by clan staff
   - **Discord ID** — your Discord user ID from Step 2
4. Optionally enable or disable the **Chat Bridge** toggle depending on whether you want clan chat bridged to Discord

### Step 4 — You're all set
Open the plugin panel by clicking the Tyr's Guard icon in the RuneLite sidebar. You can now submit screenshots, view your XP and rank, and use the clan chat bridge if enabled.

---

## Configuration Options

| Setting | Description | Required |
|---|---|---|
| Bot API URL | The URL of the Tyr's Guard clan bot | Yes |
| Plugin API Secret | Secret key to authenticate with the bot | Yes |
| Discord ID | Your Discord user ID | Yes |
| Enable Chat Bridge | Toggle clan chat ↔ Discord bridging on or off | Optional |

---

## Support

If you have issues with the plugin, reach out to Tyr's Guard staff in the clan Discord. This plugin is maintained by the clan and is not officially supported by RuneLite.
