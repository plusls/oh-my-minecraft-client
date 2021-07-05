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

### Sort Inventory

Press hotkey to sort inventory.

### Sort Inventory Support Empty ShulkerBox Stack

Support empty shulker box stack when sort inventory.

## Features

### Disable Break Block

You can't break blocks in **breakBlockBlackList**.

### Disable Break Scaffolding

You can break the scaffolding with the items in **breakScaffoldingWhiteList**.

### Disable Move Down In Scaffolding

You can move down in scaffolding when the item of **moveDownInScaffoldingWhiteList** in your hand.

### Forced Break Cooling

Players will have a 5GT cooldown after breaking the block instantaneously, which will prevent you from accidentally
destroying components with the Haste 2 effect.

### Highlight Lava Sources

Any lava source will be highlighted with a special texture.

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

### World Eater And Mine Helper

When the blocks in **worldEaterMineHelperWhitelist** are exposed to the air, the game will render their own mirror image
above them, which is convenient for world eater maintenance and mining.

![worldEaterMineHelper](./docs/img/worldEaterMineHelper.png)

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

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
