# Oh My Minecraft Client

[![MC Versions](http://cf.way2muchnoise.eu/versions/For%20MC_454900_all.svg)](https://www.curseforge.com/minecraft/mc-mods/oh-my-minecraft-client)
[![CurseForge](http://cf.way2muchnoise.eu/full_454900_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/oh-my-minecraft-client)
[![Issues](https://img.shields.io/github/issues/plusls/oh-my-minecraft-client?style=flat-square)](https://github.com/plusls/oh-my-minecraft-client/issues)
[![Pull Requests](https://img.shields.io/github/issues-pr/plusls/oh-my-minecraft-client?style=flat-square)](https://github.com/plusls/oh-my-minecraft-client/pulls)
[![CI](https://img.shields.io/github/actions/workflow/status/plusls/oh-my-minecraft-client/build.yml?label=Build&style=flat-square)](https://github.com/plusls/oh-my-minecraft-client/actions/workflows/build.yml)
[![Publish Release](https://img.shields.io/github/actions/workflow/status/plusls/oh-my-minecraft-client/publish.yml?label=Publish%20Release&style=flat-square)](https://github.com/plusls/oh-my-minecraft-client/actions/workflows/publish.yml)
[![Release](https://img.shields.io/github/v/release/plusls/oh-my-minecraft-client?include_prereleases&style=flat-square)](https://github.com/plusls/oh-my-minecraft-client/releases)
[![Github Release Downloads](https://img.shields.io/github/downloads/plusls/oh-my-minecraft-client/total?label=Github%20Release%20Downloads&style=flat-square)](https://github.com/plusls/oh-my-minecraft-client/releases)

[English](./README.md) | 中文

让 Minecraft 客户端再次好起来~~（我怎么就管不住这手呢）~~

默认使用 **O + C** 打开设置界面

# 依赖
| 依赖         | 下载                                                                                                                                                                                 |
|------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Fabric API | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api) &#124; [GitHub](https://github.com/FabricMC/fabric) &#124; [Modrinth](https://modrinth.com/mod/fabric-api)   |
| MagicLib   | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/magiclib) &#124; [GitHub](https://github.com/Hendrix-Shen/MagicLib) &#124; [Modrinth](https://modrinth.com/mod/magiclib) |
| MaliLib    | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/malilib) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=malilib)                                       |

## 通用
## 取消高亮坐标点 (clearWaypoint)
取消高亮坐标点的快捷键

- 分类: `通用`
- 类型: `快捷键`
- 默认值: `C`
## 调试模式 (debug)
开启后将会打印调试日志

- 分类: `通用`
- 类型: `开关`
- 默认值: `false`
## 不清空聊天历史记录 (dontClearChatHistory)
不清空聊天历史记录，以及输入的历史记录

- 分类: `通用`
- 类型: `开关`
- 默认值: `false`
## 强制从聊天中解析路径点 (forceParseWaypointFromChat)
强制从聊天中解析路径点，就算该信息中存在 click event 也会将其覆盖

- 分类: `通用`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`

![highlightWaypoint](./docs/img/highlightWaypoint.png)
## 打开设置界面 (openConfigGui)
打开设置界面的快捷键

- 分类: `通用`
- 类型: `快捷键`
- 默认值: `O,C`
## 从聊天中解析路径点 (parseWaypointFromChat)
从聊天中解析路径点

- 分类: `通用`
- 类型: `带快捷键开关`
- 默认值: `true`, `无快捷键`
## 发送当前注视的方块的坐标 (sendLookingAtPos)
发送当前注视的方块的坐标

- 分类: `通用`
- 类型: `快捷键`
- 默认值: `O,P`
## 整理仓库 (sortInventory)
整理仓库的快捷键

- 分类: `通用`
- 类型: `快捷键`
- 默认值: `R`
## 整理仓库时潜影盒放在最后 (sortInventoryShulkerBoxLast)
整理仓库时潜影盒放在最后

- 分类: `通用`
- 类型: `枚举`
- 默认值: `自动`
- 可选值: `自动`, `关闭`, `开启`
## 整理仓库时支持空潜影盒堆叠 (sortInventorySupportEmptyShulkerBoxStack)
整理仓库时支持空潜影盒堆叠（请配合 PCA 使用）

- 分类: `通用`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`

## 特性开关
## 自动切换鞘翅 (autoSwitchElytra)
自动切换鞘翅和胸甲

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`

![autoSwitchElytra](./docs/img/autoSwitchElytra.gif)
## 更好的潜行 (betterSneaking)
在潜行时玩家可以向下移动 1 格高

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
## 关闭玩家黑名单检查 (disableBlocklistCheck)
避免 MC-218167，避免网络请求阻塞主线程

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
- 依赖:
    - 与 (需要满足全部条件):
        - minecraft: >1.15.2

## 禁止破坏特定方块 (disableBreakBlock)
玩家无法破坏在 **破坏方块黑名单** 中的方块

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
## 禁止破坏脚手架 (disableBreakScaffolding)
玩家只有在手持 **破坏脚手架白名单**** 中的物品时才能破坏脚手架

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
## 禁止在脚手架中下降 (disableMoveDownInScaffolding)
玩家只有在手持 **在脚手架中下降白名单** 中的物品时才能在脚手架下降

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
## 禁止活塞推动实体 (disablePistonPushEntity)
通过阻止客户端活塞推动实体（玩家除外）来减少活塞卡顿（比如地毯复制机），会导致一些实体位置渲染错误

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
## 平坦挖掘 (flatDigger)
只有在潜行时才能挖掘比自己低的方块

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
## 强制添加破坏冷却 (forceBreakingCooldown)
玩家在秒破方块后会有 5gt 的破坏冷却时间

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
## 高亮岩浆源 (highlightLavaSource)
岩浆源将会使用特殊的贴图高亮

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`

![highlightLavaSourceOff](./docs/img/highlightLavaSourceOff.png)
![highlightLavaSourceOn](./docs/img/highlightLavaSourceOn.png)
We have also provided some optional resource packs for this purpose.
+ [ommc-highlightLavaSource\[32x\](static)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-highlightLavaSource[32x](static).zip) by [Hendrix-Shen](https://github.com/Hendrix-Shen).
+ [ommc-highlightLavaSource\[32x\](dynamic)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-highlightLavaSource[32x](dynamic).zip) by [Hendrix-Shen](https://github.com/Hendrix-Shen).
+ [ommc-xk(32x)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-xk(32x).zip) by [SunnySlopes](https://github.com/SunnySlopes).
+ [ommc-faithful(static)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-faithful(static).zip) by [SunnySlopes](https://github.com/SunnySlopes).
+ [ommc-faithful(dynamic)](https://github.com/plusls/oh-my-minecraft-client/raw/1.17/docs/file/ommc-faithful(dynamic).zip) by [SunnySlopes](https://github.com/SunnySlopes).

## 高亮不会消失的怪物 (highlightPersistentMob)
高亮不会消失的怪物（受到客户端的限制，现在只能高亮手里有特殊物品或者被命名的怪物）

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
## 高亮不会消失的怪物客户端模式 (highlightPersistentMobClientMode)
使用客户端的数据来检查怪物是否会消失，比如手上的物品以及是否有名字，这可能会导致误判。提示：在本地游戏中应该关闭这个选项，在服务器中可以使用 MasaGadget 中的 同步全部实体数据来同步实体信息到本地。

- 分类: `特性开关`
- 类型: `开关`
- 默认值: `false`
## 防止刻意的游戏设计 (preventIntentionalGameDesign)
防止刻意的游戏设计（床或者重生锚爆炸）

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
## 防止浪费水 (preventWastageOfWater)
防止在地狱使用水桶

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
## 真潜行 (realSneaking)
玩家在潜行时无法上升或者下降，比如走上或者离开地毯，半砖，台阶这类方块

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
## 移除挖掘冷却 (removeBreakingCooldown)
移除在非秒破方块后的挖掘冷却（默认是 5gt），该功能在开启 **强制添加破坏冷却** 时不会生效

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`
## 世吞挖矿助手 (worldEaterMineHelper)
当 **世吞挖矿助手白名单** 中的方块暴露在空气中时，客户端会在它们上方渲染出自己的镜像，方便世吞运维以及挖矿

- 分类: `特性开关`
- 类型: `带快捷键开关`
- 默认值: `false`, `无快捷键`

![worldEaterMineHelper](./docs/img/worldEaterMineHelper.png)

## 列表
## 方块模型没有偏移列表黑名单 (blockModelNoOffsetBlacklist)
方块模型没有偏移列表黑名单

- 分类: `列表`
- 类型: `字符串列表`
- 默认值: `[]`
## 方块模型没有偏移列表类型 (blockModelNoOffsetListType)
方块模型没有偏移列表类型

- 分类: `列表`
- 类型: `枚举`
- 默认值: `WHITELIST`
- 可选值: `WHITELIST`, `NONE`, `BLACKLIST`
## 方块模型没有偏移列表白名单 (blockModelNoOffsetWhitelist)
方块模型没有偏移列表白名单

- 分类: `列表`
- 类型: `字符串列表`
- 默认值: `[minecraft:wither_rose, minecraft:poppy, minecraft:dandelion]`
## 破坏方块黑名单 (breakBlockBlackList)
如果开启 **禁止破坏特定方块**，玩家无法破坏在 **破坏方块黑名单** 中的方块

- 分类: `列表`
- 类型: `字符串列表`
- 默认值: `[minecraft:budding_amethyst, _bud]`
## 破坏脚手架白名单 (breakScaffoldingWhiteList)
如果开启 **禁止破坏脚手架**，玩家只有在使用 **破坏脚手架白名单** 中的物品时才能破坏脚手架

- 分类: `列表`
- 类型: `字符串列表`
- 默认值: `[minecraft:air, minecraft:scaffolding]`
## 高亮实体列表黑名单 (highlightEntityBlackList)
高亮实体列表黑名单

- 分类: `列表`
- 类型: `字符串列表`
- 默认值: `[]`
## 高亮实体列表类型 (highlightEntityListType)
高亮实体列表类型

- 分类: `列表`
- 类型: `枚举`
- 默认值: `WHITELIST`
- 可选值: `WHITELIST`, `NONE`, `BLACKLIST`
## 高亮实体列表白名单 (highlightEntityWhiteList)
高亮实体列表白名单

- 分类: `列表`
- 类型: `字符串列表`
- 默认值: `[minecraft:wandering_trader]`
## 在脚手架中下降白名单 (moveDownInScaffoldingWhiteList)
如果开启 **禁止在脚手架中下降**，玩家手中拿着 **在脚手架中下降白名单** 中的物品时才能在脚手架下降

- 分类: `列表`
- 类型: `字符串列表`
- 默认值: `[minecraft:air, minecraft:scaffolding]`
## 世吞挖矿助手白名单 (worldEaterMineHelperWhitelist)
如果开启 **世吞挖矿助手白名单**，当 **世吞挖矿助手白名单** 中的方块暴露在空气中时，客户端会在它们上方渲染出自己的镜像，方便世吞运维以及挖矿

- 分类: `列表`
- 类型: `字符串列表`
- 默认值: `[_ore, minecraft:ancient_debris, minecraft:obsidian]`

## 本地服务器设置
## 允许飞行 (flight)
本地服务器允许飞行

- 分类: `本地服务器设置`
- 类型: `开关`
- 默认值: `true`
## 正版验证 (onlineMode)
本地服务器开启正版验证

- 分类: `本地服务器设置`
- 类型: `带快捷键开关`
- 默认值: `true`, `无快捷键`
## 局域网端口 (port)
本地服务器的局域网端口，0 表示使用随机端口

- 分类: `本地服务器设置`
- 类型: `整型`
- 默认值: `0`
- 最小值: `0`
- 最大值: `65535`
- 依赖:
    - 非 (需要排除任意条件):
        - minecraft: <1.19.3

## pvp (pvp)
本地服务器开启 PVP

- 分类: `本地服务器设置`
- 类型: `开关`
- 默认值: `true`

## 开发

### 支持

当前主开发版本：1.19.4

并且使用 `预处理` 来兼容各版本。

**注意: 我们仅接受以下版本的议题。请注意该信息的时效性，任何不在此列出的版本议题均会被关闭。**

- Minecraft 1.14.4
- Minecraft 1.15.2
- Minecraft 1.16.5
- Minecraft 1.17.1
- Minecraft 1.18.2
- Minecraft 1.19.2
- Minecraft 1.19.3
- Minecraft 1.19.4

### 混淆映射表

我们使用 **Mojang 官方** 混淆映射表来反混淆 Minecraft 并插入补丁程序。

### 文档

英文文档与中文文档是逐行对应的。

## 许可

此项目在 LGPL-3.0许可证 下可用。 从中学习，并将其融入到您自己的项目中。

# 致谢
+ 感谢 [XeKr](https://space.bilibili.com/5930630) 的岩浆护眼材质
+ 感谢 [NextPage](https://github.com/Next-Page-Vi) 提供的英文翻译，岩浆材质修改以及测试工作
+ 感谢 [水星嗷](https://space.bilibili.com/18525909) 提供的[矿物高亮的灵感以及样例](https://www.bilibili.com/video/BV1w64y1D7wP)
+ 感谢 [voxelmap](https://www.curseforge.com/minecraft/mc-mods/voxelmap) 提供的高亮坐标点的实现
+ 感谢 [Hendrix-Shen](https://github.com/Hendrix-Shen) 提供的 32x 岩浆高亮材质
+ 感谢 [SunnySlopes](https://github.com/SunnySlopes) 提供的 32x 岩浆高亮材质
