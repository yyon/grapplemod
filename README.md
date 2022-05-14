# Grappling Hook Mod for Minecraft

A mod which adds grappling hooks. The aim of this mod is to provide a fun way to get around large builds like cities.

This mod is for Forge only. No fabric version is planned at the moment.

1.16.5/1.18.2 versions requires Cloth Config API:  https://www.curseforge.com/minecraft/mc-mods/cloth-config-forge

## Mod Description & Downloads

[https://www.curseforge.com/minecraft/mc-mods/grappling-hook-mod](https://www.curseforge.com/minecraft/mc-mods/grappling-hook-mod)

## Setup for Developing

1. Download the latest Minecraft Forge Mdk for the correct version of Minecraft from [https://files.minecraftforge.net/net/minecraftforge/forge/](https://files.minecraftforge.net/net/minecraftforge/forge/)
2. Clone this repository into the src folder (e.g. `rm -r src; git clone git@github.com:yyon/grapplemod.git src`)
3. Copy or symlink build.gradle and gradle.properties into the root of the Mdk
4. Follow standard Forge Development setup (e.g. `./gradlew build`, see [https://mcforge.readthedocs.io/en/latest/gettingstarted/](https://mcforge.readthedocs.io/en/latest/gettingstarted/))

## Project Structure

Currently, the versions of this mod for Minecraft 1.12, 1.16, and 1.18 are on branches 1.12, 1.16.5, and 1.18 respectively.

### Code Structure Overview

- main/java/com/yyon/grapplinghook/client: Client-side code. Initialization in ClientSetup.java and event handlers in ClientEventHandlers.java. All non-client-side code must call ClientProxy.java code through ClientProxyInterface.java.
- main/java/com/yyon/grapplinghook/common: Code that runs on both client-side and server-side. Initializiation in CommonSetup.java and event handlers in CommonEventHandlers.java.
- main/java/com/yyon/grapplinghook/server: Server-side code. 
- main/java/com/yyon/grapplinghook/blocks: All Minecraft blocks added by this mod.
- main/java/com/yyon/grapplinghook/items: All Minecraft items added by this mod.
- main/java/com/yyon/grapplinghook/entities: All Minecraft entities added by this mod.
- main/java/com/yyon/grapplinghook/enchantments: All Minecraft enchantments added by this mod.
- main/java/com/yyon/grapplinghook/controllers: Code for physics / controlling player movement while on a grappling hook, etc.
- main/java/com/yyon/grapplinghook/network: Custom network packets which are sent between client and server
- main/java/com/yyon/grapplinghook/config: Configuration parameters provided by this mod that allows users to configure the parameters through a config file or cloth config
- main/java/com/yyon/grapplinghook/integrations: Integration of this mod with other mods
- main/java/com/yyon/grapplinghook/utils: Miscellaneous utilities

## Credits

1.18 update by Nyfaria

Textures by Mayesnake

Bug fixes:

- Random832 (Prevent tick from running when shootingEntity is null)

- LachimHeigrim (Fix for #37: removed forgotten debug prints)

Languages:

- Blueberryy (Russian)

- Neerwan (French)

- Eufranio (Brazillian Portugese)

Sound Effects:

- Iwan Gabovitch (Double jump sound effects (modified by me): https://opengameart.org/content/swish-bamboo-stick-weapon-swhoshes - Copyright Iwan Gabovitch 2009 Under the CC0 1.0 Universal license)

- Outroelison (Ender staff sound effect (modified by me): https://freesound.org/people/outroelison/sounds/150950/ - Copyright outroelison 2012 under the CC0 1.0 Universal License)

Bug finding:

- Shivaxi
