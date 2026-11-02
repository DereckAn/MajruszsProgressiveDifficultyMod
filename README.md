# Majrusz's Progressive Difficulty — unofficial Minecraft 26.3 port (Fabric)

This is an **unofficial** port of [Majrusz's Progressive Difficulty](https://github.com/Majrusz/MajruszsProgressiveDifficultyMod) by **Majrusz** to Minecraft **26.3** on **Fabric**. The original mod and its library stopped at 1.20.1. All credit for the mod's design, code, art and sounds goes to Majrusz; this port only updates it to run on 26.3.

Please report problems with this port [here](https://github.com/DereckAn/MajruszsProgressiveDifficultyMod/issues), not to Majrusz.

## What it does
The game gets harder over time: game stages (Normal, Expert, Master), bleeding, the Undead Army, blood moons, treasure bags, Enderium gear, new potions and six new mobs (Tank, Giant, Cerberus, Cursed Armor, Creeperling, Illusioner). See the original project pages ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/majruszs-progressive-difficulty), [Modrinth](https://modrinth.com/mod/majruszs-progressive-difficulty)) for details.

## Install
- Minecraft 26.3, [Fabric Loader](https://fabricmc.net) 0.20.1+ and [Fabric API](https://modrinth.com/mod/fabric-api).
- Download the jar from [Releases](https://github.com/DereckAn/MajruszsProgressiveDifficultyMod/releases). The required Majrusz Library is **bundled inside** the jar; you don't need to install it separately.

## Differences from the 1.20.1 version
- Fabric only (no Forge/NeoForge/Quilt builds).
- Worlds from 1.20.1 do not carry over the mod's saved data (game stages, treasure bag progress).
- Spawn eggs have new textures (26.3 removed the two-color egg template).
- The library's update checker was removed (it pointed at the 1.20.1 release list).

## Building
The trimmed library port lives in [DereckAn/MajruszLibrary](https://github.com/DereckAn/MajruszLibrary) (branch `26.3`). Build it first, then this mod (both need a Java 25+ runtime for Gradle):

```bash
cd MajruszLibrary && ./gradlew publishToMavenLocal
cd MajruszsProgressiveDifficultyMod && ./gradlew build
```

## License
MIT, like the original. See [LICENSE](LICENSE).
