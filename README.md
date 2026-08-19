# Tyrs Guard Clan Plugin

A RuneLite plugin built exclusively for members of the **Tyr's Guard** OSRS clan. This plugin connects to the clan's private Discord bot to provide integration between the game and the clan's Discord server.

> ⚠️ **This plugin requires access to Tyr's Guard's private clan bot to function. It is intended only for Tyr's Guard clan members. Without the correct configuration it does nothing.**

---

## Features

### 📸 Screenshot Submissions

Submit screenshots directly from RuneLite to the clan's Discord submissions channel. Screenshots are stamped with your local date and time, along with CST and GMT conversions, so staff can verify submissions across time zones.

### ⚔️ XP & Rank Tracking

View your current clan XP, rank, and progress toward your next rank in the plugin panel. The clan uses a custom rank system based on participation and activity.

### 💬 Clan Chat Bridge

Bridges your in-game clan chat to the clan's Discord channel and back. Messages in the Discord clan chat channel appear in your in-game clan chat, and clan chat messages appear in Discord.

**This is enabled by default** and can be turned off at any time in the plugin settings. Only messages from the Tyr's Guard clan chats are bridged — private messages, friends chat and public chat are never sent.

### 🗺️ Clan Map — Location Sharing *(opt-in, OFF by default)*

Shows clan members on the RuneLite world map, with their rank icon and a coarse activity label.

**This feature is OFF by default and must be explicitly enabled.** See the Privacy & Data section below for exactly what is sent when you turn it on.

Your location is **never** shared while you are in the Wilderness or on a PvP world, regardless of your settings — this is enforced in code and cannot be overridden by configuration.

---

## Privacy & Data

This plugin communicates with a private server operated by Tyr's Guard clan leadership. Under RuneLite's definitions this is a **third-party server** — it is not run by RuneLite or Jagex. Below is exactly what is sent, and when.

### Nothing is sent until configured

With no Bot API URL and secret entered, the plugin makes no network requests at all.

### Screenshot submissions — manual only

Sent only when you press the submit button. Nothing is captured or uploaded automatically.

### XP & rank lookup

Sends your RuneScape name to the clan bot to look up your own rank and XP.

### Clan chat bridge — ON by default

When enabled, clan chat messages you can see are sent to the clan server so they can be mirrored into Discord. This includes:

- the sender's RuneScape name
- their message text
- their clan rank

Only Tyr's Guard clan chat is included. **Private messages, friends chat, and public chat are never sent.** Turn the bridge off in settings if you do not want clan chat relayed.

### Location sharing — OFF by default

When you explicitly enable **Share my location**, the plugin sends:

- your RuneScape name
- your in-game coordinates
- your world number
- your clan rank title
- a coarse activity label derived from your current animation (e.g. "Training Mining")

Nothing else is sent, and nothing is sent at all while the setting is off.

Locations are held in memory on the server, are visible only to other Tyr's Guard members, and expire automatically about 60 seconds after your last update. They are not written to a database or retained long-term.

**Location is never sent while you are in the Wilderness or on a PvP world.** This is enforced in code independently of your settings.

---

## Setup

You need two things from a Tyr's Guard staff member:

- the clan bot's **API URL**
- the **API secret**

Then:

1. Install the plugin from the RuneLite Plugin Hub
2. Open the plugin settings (wrench icon next to the plugin)
3. Fill in **Bot API URL** and **API Secret**
4. Optionally turn the **Chat Bridge** off, or turn **Share my location** on

That's it — your rank and XP are looked up from your RuneScape name automatically.

---

## Configuration Options

| Setting | Description | Required |
| --- | --- | --- |
| Bot API URL | URL of the Tyr's Guard clan bot | **Yes** |
| API Secret | Secret key to authenticate with the bot | **Yes** |
| Your Discord ID | Only needed to load your rank while logged out | No |
| Enable Chat Bridge | Bridge clan chat ↔ Discord (default: **on**) | No |
| Share my location | Show yourself on the clan map (default: **off**) | No |
| Clan Map — API URL / Secret | Leave blank; reuses the Bot API settings above | No |

---

## Third-Party Content

The clan rank icons are taken from [runelite-live-friend-locations-plugin](https://github.com/TiboDeMunck/runelite-live-friend-locations-plugin) (BSD-2-Clause). See `THIRD-PARTY-NOTICES.md` for the retained copyright and licence text.

Rank icons depict Old School RuneScape clan rank sprites, which are the intellectual property of Jagex Ltd. This plugin is not affiliated with or endorsed by Jagex Ltd.

---

## Support

For issues with the plugin, reach out to Tyr's Guard staff in the clan Discord. This plugin is maintained by the clan and is not officially supported by RuneLite.

## Licence

BSD-2-Clause. See `LICENSE`.
