# Grappling Hook Mod for Minecraft

A mod which adds grappling hooks. The aim of this mod is to provide a fun way to get around large builds like cities.

This branch is for Minecraft 1.21.1 and builds for both Fabric and NeoForge from a shared codebase.

The Fabric version requires Fabric API and Forge Config API Port: https://www.curseforge.com/minecraft/mc-mods/forge-config-api-port-fabric

## Mod Description & Downloads

[https://www.curseforge.com/minecraft/mc-mods/grappling-hook-mod](https://www.curseforge.com/minecraft/mc-mods/grappling-hook-mod)

## Setup for Developing

1. Clone this repository and check out the 1.21.1 branch.
2. Make sure Java 21 is installed (Gradle will download a toolchain if one isn't found).
3. Import the root folder into IntelliJ IDEA as a Gradle project, or run `./gradlew build` from the command line.
4. Use the generated run configurations (or `./gradlew :fabric:runClient` / `./gradlew :neoforge:runClient`) to start the game.

Built jars end up in `fabric/build/libs` and `neoforge/build/libs`.

## Project Structure

Older versions of this mod are on their own branches (1.12, 1.16.5, 1.18, 1.19, 1.20.x, etc).

The project is split into three Gradle subprojects:

- common: All of the mod's code and resources. It is compiled against vanilla Minecraft and can't reference loader specific code.
- fabric: Fabric entrypoints, Fabric networking/event registration and Fabric only mixins.
- neoforge: NeoForge entrypoints and NeoForge networking/event registration.

### Code Structure Overview

- common/src/main/java/com/yyon/grapplemod/client: Client-side code. Initialization in ClientSetup.java and event handlers in ClientEventHandlers.java. All non-client-side code must call ClientProxy.java code through ClientProxyInterface.java.
- common/src/main/java/com/yyon/grapplemod/common: Event handlers that run on both client-side and server-side (CommonEventHandlers.java).
- common/src/main/java/com/yyon/grapplemod/init: Registration of blocks, items, entities and data components.
- common/src/main/java/com/yyon/grapplemod/server: Server-side code.
- common/src/main/java/com/yyon/grapplemod/blocks: All Minecraft blocks added by this mod.
- common/src/main/java/com/yyon/grapplemod/items: All Minecraft items added by this mod.
- common/src/main/java/com/yyon/grapplemod/entities: All Minecraft entities added by this mod.
- common/src/main/java/com/yyon/grapplemod/enchantments: Enchantment keys and helpers. The enchantments themselves are data driven (data/grapplemod/enchantment).
- common/src/main/java/com/yyon/grapplemod/controllers: Code for physics / controlling player movement while on a grappling hook, etc.
- common/src/main/java/com/yyon/grapplemod/network: Custom network packets which are sent between client and server
- common/src/main/java/com/yyon/grapplemod/config: Configuration parameters provided by this mod. The config files are generated with NeoForge's config system (Forge Config API Port on Fabric) and can be edited in game through the mod's config screen.
- common/src/main/java/com/yyon/grapplemod/mixin: Mixins used in place of loader events
- common/src/main/java/com/yyon/grapplemod/utils: Miscellaneous utilities

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
