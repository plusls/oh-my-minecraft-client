# Oh My Minecraft Client

[![Issues](https://img.shields.io/github/issues/plusls/oh-my-minecraft-client.svg)](https://github.com/plusls/oh-my-minecraft-client/issues)
[![MC Versions](http://cf.way2muchnoise.eu/versions/For%20MC_454900_all.svg)](https://www.curseforge.com/minecraft/mc-mods/oh-my-minecraft-client)
[![CurseForge](http://cf.way2muchnoise.eu/full_454900_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/oh-my-minecraft-client)

[>>> English <<<](./README_EN.md)

Make Minecraft Client Great Again!

默认使用 O+C 打开设置界面

## 依赖

+ [malilib](https://www.curseforge.com/minecraft/mc-mods/malilib)
+ [fabric-api](https://www.curseforge.com/minecraft/mc-mods/fabric-api)

## Generic

### 不清空聊天历史记录

不清空聊天历史记录，不清空输入历史记录

### 整理仓库

按下快捷键后可以自动整理仓库（R 键整理的替代品）

### 整理仓库时支持空潜影盒堆叠

在自动整理时会将空盒视作可堆叠的

## Feature

### 禁止破坏脚手架

玩家只有在手持 **破坏脚手架白名单** 中的物品时才能破坏脚手架

### 禁止在脚手架中下降

玩家只有在手持 **在脚手架中下降白名单** 中的物品时才能在脚手架下降

### 强制添加破坏冷却

玩家在秒破方块后会有 5gt 的破坏冷却时间

### 高亮岩浆源

岩浆源将会使用特殊的贴图高亮

效果如下：

![highlightLavaSourceOff](./docs/img/highlightLavaSourceOff.png)

![highlightLavaSourceOn](./docs/img/highlightLavaSourceOn.png)

### 高亮流浪商人

流浪商人会像被射了光灵箭一样高亮，隐身时同样也会高亮

![highlightLavaSourceOn](./docs/img/highlightWanderingTrader.png)

### 防止床爆炸

阻止玩家在下界或者末地使用床

### 真潜行

玩家在潜行时无法上升或者下降，比如走上或者离开地毯，半砖，台阶这类方块

### 世吞挖矿助手

当 **世吞挖矿助手白名单** 中的方块暴露在空气中时，客户端会在它们上方渲染出自己的镜像，方便世吞运维以及挖矿

![worldEaterMineHelper](./docs/img/worldEaterMineHelper.png)

## 致谢

+ 感谢 [XeKr](https://space.bilibili.com/5930630) 的岩浆护眼材质
+ 感谢 [NextPage](https://github.com/Next-Page-Vi) 提供的英文翻译，岩浆材质修改以及测试工作
+ 感谢 [水星嗷](https://space.bilibili.com/18525909) 提供的[矿物高亮的灵感以及样例](https://www.bilibili.com/video/BV1w64y1D7wP)

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
