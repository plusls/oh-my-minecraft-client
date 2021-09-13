# Oh My Minecraft Client

Make Minecraft Client Great Again!

## Description

Oh My Minecraft Client is a client-side mod, which helps you manage your hands. The mod is written for the Fabric
Loader.

## NOTE: My Minecraft Client also requires the MaLiLib library mod by masady:

https://www.curseforge.com/minecraft/mc-mods/malilib

The default hotkey to open the in-game config GUI is O + C

## Generic

### Don't clear chat history.

Minecraft client will not to clear chat history and input history.

### Highlight waypoint

Add command `/highlightWaypoint` to highlight waypoint.

Auto parse waypoint in chat with voxelmap's format (like `[x:1, y:2, z:3]`，`(x:1, y:2, z:3)`, `[1, 2, 3]`, `(1, 2, 3)`).

It looks like:

![highlightWaypoint](./docs/img/highlightWaypoint.png)

Can delete it with hotkey.

### Force Parse Waypoint From Chat

Force parse waypoint from chat (such it will override the clickevent of rtext).

### Send Looking At Pos

A hotkey to send looking at pos.

### Sort Inventory

Press hotkey to sort inventory.

### Sort Inventory Support Empty ShulkerBox Stack

Support empty shulker box stack when sort inventory.

## Features

### Auto Switch Elytra

Auto Switch elytra and chestplate.

It looks like:

![autoSwitchElytra](./docs/img/autoSwitchElytra.gif)

### Disable Break Block

You can't break blocks in **breakBlockBlackList**.

### Disable Break Scaffolding

You can break the scaffolding with the items in **breakScaffoldingWhiteList**.

### Disable Move Down In Scaffolding

You can move down in scaffolding when the item of **moveDownInScaffoldingWhiteList** in your hand.

### Disable Piston Push Entity

Prevent piston push entities (except the player) to reduce piston lag (such as the carpet duper), it will cause entities pos error when entity push by piston.

### Forced Break Cooling

Players will have a 5GT cooldown after breaking the block instantaneously, which will prevent you from accidentally
destroying components with the Haste 2 effect.

### Highlight Lava Sources

Any lava source will be highlighted with a special texture.

We have also provided some optional resource packs for this purpose.
+ [ommc-highlightLavaSource\[32x\](static)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-highlightLavaSource[32x](static).zip) by [Hendrix-Shen](https://github.com/Hendrix-Shen).
+ [ommc-highlightLavaSource\[32x\](dynamic)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-highlightLavaSource[32x](dynamic).zip) by [Hendrix-Shen](https://github.com/Hendrix-Shen).
+ [ommc-xk(32x)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-xk(32x).zip) by [SunnySlopes](https://github.com/SunnySlopes).
+ [ommc-faithful(static)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-faithful(static).zip) by [SunnySlopes](https://github.com/SunnySlopes).
+ [ommc-faithful(dynamic)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-faithful(dynamic).zip) by [SunnySlopes](https://github.com/SunnySlopes).

It looks like:

![highlightLavaSourceOff](./docs/img/highlightLavaSourceOff.png)

![highlightLavaSourceOn](./docs/img/highlightLavaSourceOn.png)

### Wandering Trader Glow

The Wandering Trader will have the glowing affect, so it in Invisibility.

![highlightLavaSourceOn](./docs/img/highlightWanderingTrader.png)

### Prevent Intentional Game Design

Prevent Intentional Game Design (Bed and Respawn Anchor).

### High Light Persistent Mob

Highlight persistent mobs (Mob have item in hand or mob have custom name).

### Flat Digger

Which allowed you mine a flat road while digging stone by preventing digging of blocks under your feet while standing，sneak to dig blocks under you.

### Real Sneaking

Players cannot ascend or descend when sneaking.

### Remove Breaking Cooldown

Remove cooldown after break block (default is 5gt), it will not work when enable **forceBreakingCooldown§r**.


### World Eater And Mine Helper

When the blocks in **worldEaterMineHelperWhitelist** are exposed to the air, the game will render their own mirror image
above them, which is convenient for world eater maintenance and mining.

![worldEaterMineHelper](./docs/img/worldEaterMineHelper.png)

## Lists

### Fallback Language List

Fallback language list.

## Advanced Integrated Server

Now allow user to change some integrated server setting.

### Online Mode

Integrated server use online mode.

### pvp

Integrated server enable pvp.

### flight

Integrated server enable flight.

### port

Integrated server lan port, 0 to use default port.

## Acknowledgements

+ Thanks to [XeKr](https://space.bilibili.com/5930630) for the lava eye protection texture.
+ Thanks to [NextPage](https://github.com/Next-Page-Vi) for providing the English translation, lava source texture
  modification and testing work.
+ Thanks to [水星嗷](https://space.bilibili.com/18525909) for
  providing [the idea of highlighting ores and resourcepack](https://www.bilibili.com/video/BV1w64y1D7wP).
+ Thanks to [voxelmap](https://www.curseforge.com/minecraft/mc-mods/voxelmap) for providing the code of highlight waypoint.
+ Thanks to [Hendrix-Shen](https://github.com/Hendrix-Shen) for providing highlightLavaSource x32 resource pack.
+ Thanks to [SunnySlopes](https://github.com/SunnySlopes) for providing highlightLavaSource x32 resource pack.
