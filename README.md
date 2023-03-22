# Oh My Minecraft Client

[![MC Versions](http://cf.way2muchnoise.eu/versions/For%20MC_454900_all.svg)](https://www.curseforge.com/minecraft/mc-mods/oh-my-minecraft-client)
[![CurseForge](http://cf.way2muchnoise.eu/full_454900_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/oh-my-minecraft-client)
[![Issues](https://img.shields.io/github/issues/plusls/oh-my-minecraft-client?style=flat-square)](https://github.com/plusls/oh-my-minecraft-client/issues)
[![Pull Requests](https://img.shields.io/github/issues-pr/plusls/oh-my-minecraft-client?style=flat-square)](https://github.com/plusls/oh-my-minecraft-client/pulls)
[![CI](https://img.shields.io/github/actions/workflow/status/plusls/oh-my-minecraft-client/build.yml?label=Build&style=flat-square)](https://github.com/plusls/oh-my-minecraft-client/actions/workflows/build.yml)
[![Publish Release](https://img.shields.io/github/actions/workflow/status/plusls/oh-my-minecraft-client/publish.yml?label=Publish%20Release&style=flat-square)](https://github.com/plusls/oh-my-minecraft-client/actions/workflows/publish.yml)
[![Release](https://img.shields.io/github/v/release/plusls/oh-my-minecraft-client?include_prereleases&style=flat-square)](https://github.com/plusls/oh-my-minecraft-client/releases)
[![Github Release Downloads](https://img.shields.io/github/downloads/plusls/oh-my-minecraft-client/total?label=Github%20Release%20Downloads&style=flat-square)](https://github.com/plusls/oh-my-minecraft-client/releases)

English | [中文](./README_ZH_CN.md)

Make Minecraft Client Great Again!

The default hotkey to open the in-game config GUI is **O + C**.

# Dependencies
| Dependency | Download                                                                                                                                                                           |
|------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Fabric API | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api) &#124; [GitHub](https://github.com/FabricMC/fabric) &#124; [Modrinth](https://modrinth.com/mod/fabric-api)   |
| MagicLib   | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/magiclib) &#124; [GitHub](https://github.com/Hendrix-Shen/MagicLib) &#124; [Modrinth](https://modrinth.com/mod/magiclib) |
| MaliLib    | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/malilib) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=malilib)                                       |

## Generic
## clearWaypoint
A hotkey to clear highlight waypoint.

- Category: `Generic`
- Type: `hotkey`
- Default value: `C`
## debug
Display debug message

- Category: `Generic`
- Type: `boolean`
- Default value: `false`
## dontClearChatHistory
Don't clear chat history and input history.

- Category: `Generic`
- Type: `boolean`
- Default value: `false`
## forceParseWaypointFromChat
Force parse waypoint from chat (such it will override the clickevent of rtext).

- Category: `Generic`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`

![highlightWaypoint](./docs/img/highlightWaypoint.png)
## openConfigGui
A hotkey to open the in-game Config GUI

- Category: `Generic`
- Type: `hotkey`
- Default value: `O,C`
## parseWaypointFromChat
Parse waypoint from chat.

- Category: `Generic`
- Type: `boolean with hotkey`
- Default value: `true`, `no hotkey`
## sendLookingAtPos
A hotkey to send looking at pos.

- Category: `Generic`
- Type: `hotkey`
- Default value: `O,P`
## sortInventory
A hotkey to sort inventory.

- Category: `Generic`
- Type: `hotkey`
- Default value: `R`
## sortInventoryShulkerBoxLast
Sort inventory shulker box last.

- Category: `Generic`
- Type: `enum`
- Default value: `AUTO`
- Option values: `AUTO`, `FALSE`, `TRUE`
## sortInventorySupportEmptyShulkerBoxStack
Support empty shulker box stack when sort inventory.

- Category: `Generic`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`

## Feature Toggles
## autoSwitchElytra
Auto switch elytra and chestplate.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`

![autoSwitchElytra](./docs/img/autoSwitchElytra.gif)
## betterSneaking
Player can move down 1 height when sneaking.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## disableBlocklistCheck
Workaround for MC-218167, prevent network request from blocking the Render Thread.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
- Dependencies:
    - And (All conditions need to be satisfied):
        - minecraft: >1.15.2

## disableBreakBlock
You can't break blocks in **breakBlockBlackList**.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## disableBreakScaffolding
You can only break scaffolding with the items in **breakScaffoldingWhiteList**.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## disableMoveDownInScaffolding
You can only move down scaffolding with the items inside **moveDownInScaffoldingWhiteList** in your hand.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## disablePistonPushEntity
Prevent piston push entities (except the player) to reduce piston lag (such as the carpet duper), it will cause entities pos error when entity push by piston.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## flatDigger
Which allowed you mine a flat road while digging stone by preventing digging of blocks under your feet while standing，sneak to dig blocks under you.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## forceBreakingCooldown
You will have 5gt cooldown after instant breaking.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## highlightLavaSource
Highlight lava sources.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`

![highlightLavaSourceOff](./docs/img/highlightLavaSourceOff.png)
![highlightLavaSourceOn](./docs/img/highlightLavaSourceOn.png)
We have also provided some optional resource packs for this purpose.
+ [ommc-highlightLavaSource\[32x\](static)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-highlightLavaSource[32x](static).zip) by [Hendrix-Shen](https://github.com/Hendrix-Shen).
+ [ommc-highlightLavaSource\[32x\](dynamic)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-highlightLavaSource[32x](dynamic).zip) by [Hendrix-Shen](https://github.com/Hendrix-Shen).
+ [ommc-xk(32x)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-xk(32x).zip) by [SunnySlopes](https://github.com/SunnySlopes).
+ [ommc-faithful(static)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-faithful(static).zip) by [SunnySlopes](https://github.com/SunnySlopes).
+ [ommc-faithful(dynamic)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-faithful(dynamic).zip) by [SunnySlopes](https://github.com/SunnySlopes).

## highlightPersistentMob
Highlight persistent mobs (Mob have item in hand or mob have custom name).

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## highlightPersistentMobClientMode
Use client data to check persistent mob, such as hand item, custom name. Tips: in local game should disable this option, in server also can use syncAllEntityData in MasaGadget to sync entity data to local.

- Category: `Feature Toggles`
- Type: `boolean`
- Default value: `false`
## preventIntentionalGameDesign
Prevent Intentional Game Design (Bed and Respawn Anchor).

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## preventWastageOfWater
Prevent water bucket from vanishing in nether.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## realSneaking
You cannot ascend or descend non-full blocks when sneaking, e.g. carpets.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## removeBreakingCooldown
Remove cooldown after break block (default is 5gt), it will not work when enable **forceBreakingCooldown**.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## worldEaterMineHelper
When the blocks in **worldEaterMineHelperWhitelist**r are exposed to the air, the game will render a mirror image above them, which is convenient for world eater maintenance and mining.

- Category: `Feature Toggles`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`

![worldEaterMineHelper](./docs/img/worldEaterMineHelper.png)

## List
## blockModelNoOffsetBlacklist
blockModelNoOffsetBlacklist

- Category: `Lists`
- Type: `string list`
- Default value: `[]`
## blockModelNoOffsetListType
blockModelNoOffsetListType

- Category: `Lists`
- Type: `enum`
- Default value: `WHITELIST`
- Option values: `WHITELIST`, `NONE`, `BLACKLIST`
## blockModelNoOffsetWhitelist
blockModelNoOffsetWhitelist

- Category: `Lists`
- Type: `string list`
- Default value: `[minecraft:wither_rose, minecraft:poppy, minecraft:dandelion]`
## breakBlockBlackList
If **disableBreakScaffolding** is enabled, you can't break blocks in **breakBlockBlackList**.

- Category: `Lists`
- Type: `string list`
- Default value: `[minecraft:budding_amethyst, _bud]`
## breakScaffoldingWhiteList
If **disableBreakScaffolding** is enabled, you can only break scaffolding with the items in **breakScaffoldingWhiteList**.

- Category: `Lists`
- Type: `string list`
- Default value: `[minecraft:air, minecraft:scaffolding]`
## highlightEntityBlackList
highlightEntityBlackList

- Category: `Lists`
- Type: `string list`
- Default value: `[]`
## highlightEntityListType
highlightEntityListType

- Category: `Lists`
- Type: `enum`
- Default value: `WHITELIST`
- Option values: `WHITELIST`, `NONE`, `BLACKLIST`
## highlightEntityWhiteList
highlightEntityWhiteList

- Category: `Lists`
- Type: `string list`
- Default value: `[minecraft:wandering_trader]`
## moveDownInScaffoldingWhiteList
If **disableMoveDownInScaffolding** is enabled, you can only move down scaffolding with the items inside **moveDownInScaffoldingWhiteList** in your hand.

- Category: `Lists`
- Type: `string list`
- Default value: `[minecraft:air, minecraft:scaffolding]`
## worldEaterMineHelperWhitelist
If **worldEaterMineHelper** is enabled, when the blocks in **worldEaterMineHelperWhitelist** are exposed to the air, the game will render a mirror image above them, which is convenient for world eater maintenance and mining.

- Category: `Lists`
- Type: `string list`
- Default value: `[_ore, minecraft:ancient_debris, minecraft:obsidian]`

## Advanced Integrated Server
## flight
Integrated server enable flight.

- Category: `Advanced Integrated Server`
- Type: `boolean`
- Default value: `true`
## onlineMode
Integrated server use online mode.

- Category: `Advanced Integrated Server`
- Type: `boolean with hotkey`
- Default value: `true`, `no hotkey`
## port
Integrated server lan port, 0 to use default port.

- Category: `Advanced Integrated Server`
- Type: `integer`
- Default value: `0`
- Min value: `0`
- Max value: `65535`
- Dependencies:
    - Not (Any condition needs to be excluded):
        - minecraft: <1.19.3

## pvp
Integrated server enable pvp.

- Category: `Advanced Integrated Server`
- Type: `boolean`
- Default value: `true`

## Development

### Support

Current main development for Minecraft version: 1.19.4

And use `preprocess` to be compatible with all versions.

**Note: We only accept the following versions of issues. Please note that this information is time-sensitive and any version of the issue not listed here will be closed**

- Minecraft 1.14.4
- Minecraft 1.15.2
- Minecraft 1.16.5
- Minecraft 1.17.1
- Minecraft 1.18.2
- Minecraft 1.19.2
- Minecraft 1.19.3
- Minecraft 1.19.4

### Mappings

We are using the **Mojang official** mappings to de-obfuscate Minecraft and insert patches.

### Document

The English doc and the Chinese doc are aligned line by line.

## License

This project is available under the LGPL-3.0 license. Feel free to learn from it and incorporate it in your own projects.

# Credit
+ Thanks to [XeKr](https://space.bilibili.com/5930630) for the lava eye protection texture.
+ Thanks to [NextPage](https://github.com/Next-Page-Vi) for providing the English translation, lava source texture modification and testing work.
+ Thanks to [水星嗷](https://space.bilibili.com/18525909) for providing [the idea of highlighting ores and resourcepack](https://www.bilibili.com/video/BV1w64y1D7wP).
+ Thanks to [voxelmap](https://www.curseforge.com/minecraft/mc-mods/voxelmap) for providing the code of highlight waypoint.
+ Thanks to [Hendrix-Shen](https://github.com/Hendrix-Shen) for providing highlightLavaSource x32 resource pack.
+ Thanks to [SunnySlopes](https://github.com/SunnySlopes) for providing highlightLavaSource x32 resource pack.
