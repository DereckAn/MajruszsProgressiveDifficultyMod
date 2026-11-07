# Port plan: Majrusz's Progressive Difficulty → Minecraft 26.3 (Fabric)

This is the progress file for a multi-session port. **Any Claude Code session working on this repo reads this file first and continues from the current step.** Update the Status section after every step.

## Status

- [x] Step 0: Survey (2026-11-02, results in "Survey results" below)
- [x] Step 1: Build setup (2026-11-02)
- [x] Step 2: Library port (2026-11-02)
- [x] Step 3: Gameplay (first playable version) (2026-11-02, tested in-game by the user)
- [x] Step 4: Custom mobs (2026-11-02, mobs and item visuals tested in-game by the user)
- [x] Step 5: Blood moon visuals (2026-11-02, tested in-game by the user)
- [x] Step 6: Polish and publish (2026-11-02: release v26.3-1.9.10, library fork DereckAn/MajruszLibrary)
- [ ] Step 7: Merge with Improved Mobs into one mod (plan in "Step 7 plan"). 7a one jar, 7b milestones, 7c tiers: done and tested in-game by the user (uncommitted, user commits only on request)

**Current step:** Step 7e (config screen) awaiting in-game test. 7d tested OK. Next: plan the Mutant More port (separate private project). See "Step 7 plan".

### Decisions
- 2026-11-02: user chose to do Step 0 before Step 1.
- 2026-11-02: Step 7 ideas recorded (see "Step 7 ideas"); the user will decide the details when planning.
- 2026-11-02 (Step 7): **full code merge** of Improved Mobs into this repo (user's choice, aware it's ARR: keep the merged jar private until Flemmli97 answers the issue). Undead Army: difficulty milestones **replace** the kill-count trigger. Blood moon: milestones **plus** the random nightly chance. With Improved Mobs active, this mod's `mobs_spawn_stronger` is off. A config screen lets the user switch these (difficulty source: Improved Mobs / Majrusz / both; army and blood moon triggers).
- 2026-11-02: user will play this mod **alone** (without Improved Mobs) for now, so keep all config defaults as they are. Merging both mods' logic into one mod is a new Step 7, after Step 6.

### Known problems
- Jar-in-jar `include` resolves (library is in mavenLocal) but is only proven once the mod builds in Step 3.

### Library port notes (Step 2) — read before Step 3
Library branch `26.3` in `~/Documents/GIT/MajruszLibrary`: compiles, 136 classes, **24 mixins** (from 71), all checked against the 26.3 jar (method descriptors and every `@At` target). Run the skill's `checkmixins.py` on a copy with `"name (desc)"` spaces removed: it can't parse Majrusz's spacing or `@Mixin( value = X.class, priority = N )` (false "NO METHOD" alarms).
- **Fabric API instead of mixins:** server start/stop/save/tick, client tick/start, commands, `/reload`, player join, loot drops (`LootTableEvents.MODIFY_DROPS`), tooltips (`ItemTooltipCallback`), HUD (`HudElementRegistry`), use item/block/entity callbacks.
- **Deferred to Step 4** (`deferred/` in the library, not compiled): `animations/`, `ResourceLoader`/`Resource` (custom model + animation JSON), `MixinHumanoidModel`, `MixinItemInHandRenderer`, `MixinItemColors`, `MixinLivingEntityRenderer` and their events. `ModHelper.load/loadClient` are gone until then, and `EntityHelper.EntityAnimation` + entity animation ticking are stubbed (`TODO(step 4)`).
- **API changes the mod must follow in Step 3:**
  - Registration: `GROUP.create( id, key->new X( new Item.Properties().setId( key ) ) )` for items/blocks/entity types (26.x needs the key before construction). Spawn eggs: `ItemHelper.createEgg( TYPE )` (colors gone; each egg needs a texture + `assets/<ns>/items/*.json`).
  - `AttributeHandler( name, ()->Attributes.X, op )`: attributes are `Holder`s; operations are `ADD_VALUE` / `ADD_MULTIPLIED_BASE` / `ADD_MULTIPLIED_TOTAL`.
  - `EnchantmentHelper` takes `ResourceKey< Enchantment >` (e.g. `Enchantments.SILK_TOUCH`); `Registries.ENCHANTMENTS` and `Reader.enchantment()` are gone (CurseRandomly needs the registry from the loot context).
  - `Custom.Advancements.register( id, trigger )`; `Custom.PotionRecipe` and `Custom.ItemProperties` are gone (brewing recipes are JSON in 26.x; the shard locator needs an item model `range_dispatch` property).
  - `OnGuiOverlaysRegistered.Renderer` gets a `GuiGraphicsExtractor`; `OnRaidDefeated` has a `level`; `CustomParticle` takes a `TextureAtlasSprite` (pass `spriteSet.first()`).
  - `OnLootingLevelGet` exists but nothing fires it (looting is an enchantment now): the Enderium set's +1 looting needs a new hook.
- **Behaviour changes in the library:** `VersionChecker` removed (it pointed at the abandoned 1.20.1 update feed); world data moved to `data/majruszsdifficulty/world_data.dat` (1.20.1 worlds not migrated); a fully shield-blocked hit now still fires `OnEntityPreDamaged`.

### Gameplay port notes (Step 3)
- Java compiles; data converted with a one-shot script (folder renames, ingredient/result/icon formats, `looting_enchant` → `enchanted_count_increase`, `random_chance_with_looting` → `random_chance_with_enchanted_bonus`, `match_tool` enchantment predicate, `loot_table` entry `name` → `value`, `scute` → `turtle_scute`). `forge:` item tags became vanilla `minecraft:` tool/armor tags (they also drive enchanting); `c:ingots`/`c:nuggets` kept.
- New data: repair tags, `undead` + `burn_in_daylight` entity tags (Cerberus, Giant, Tank), 18 wither brewing recipes (`recipe/brewing/`), `assets/<ns>/items/*.json` for all 51 items (armor uses the vanilla `trim_material` select, resin trims added), equipment assets for Enderium/Tattered armor, new 16×16 spawn egg textures drawn by `eggs.py` (original pixel art in the old colors).
- **Custom mobs:** Cerberus, Creeperling, Cursed Armor, Giant, Tank are registered but never spawn until Step 4 (`MajruszsDifficulty.isWithoutRenderer`: biome spawns commented out, removed from Undead Army waves, any other spawn cancelled). The Illusioner works now (vanilla renderer).
- The library's `animations/` + `ResourceLoader` came back (mob attack timing runs on animation callbacks).
- **Still TODO (Step 4):** shard locator distance item property, Soul Jar soul tint colors and shield overlay, scroll casting arm pose, custom mob models.
- In-game fixes after the first launches: ResourceLoader created before NetworkHandler; ClassFinder now scans Fabric mod root paths (jar-in-jar); worldgen `configured_feature` → `worldgen/feature` and flattened JSON; advancements `recipe` → `recipes`, background only on roots; **loot tables use `condition` (one object, `all_of` for several), `modifier`, and `type` as the id key — the old `conditions`/`functions`/`function` keys are silently ignored by 26.x.**
- Test profile: Improved Mobs and Forge Config API Port moved to `~/Documents/curseforge/minecraft/Instances/test/mods-disabled/` (user plays this mod alone for now).

### Custom mobs and item visuals (Step 4)
- Each mob renderer extracts its own render state that carries the entity's `Animations`; models read only that state (`walkAnimationPos/Speed`, `yRot/xRot`, `ageInTicks`). Giant uses vanilla's zombie model/armor layers ×5; Cursed Armor bakes vanilla's zombie armor meshes into `CursedArmorModel` (its model JSON now has `hat` under `head`, which 26.x HumanoidModel requires); Illusioner subclasses vanilla's renderer for the mod texture.
- Item visuals as 26.x item-model types registered in `client/ItemModelExtensions`: `majruszsdifficulty:shard_distance` (range_dispatch) and `majruszsdifficulty:soul_color` (tint). The shard locator's scan cache now lives in client memory instead of the item's data.
- Mod client mixins: `MixinShieldSpecialRenderer` (Soul Jar shield overlay), `MixinHumanoidModel` + `MixinFirstPersonHandsAndItemsRenderer` (scroll casting pose). The library's parked rendering hooks were removed.

### Blood moon visuals (Step 5)
- `MixinClientLevel` adds time-based environment attribute layers that tint `FOG_COLOR`, `SKY_COLOR` and `SKY_LIGHT_COLOR` (same ratios as 1.20.1). `MixinSkyRenderer` swaps the moon vertex buffer for red phase sprites (`textures/environment/celestial/blood_moon/*.png`, split from the old sheet; the vanilla celestials atlas picks them up) and tints the stars.

### Publishing (Step 6)
- Both repos got README, changelog entry and an MIT `LICENSE` (Majrusz + port). The `custom.update` feed was removed and contact links point at the forks.
- Mod: `26.3` pushed to `origin` (DereckAn fork). Library: fork as remote `fork` (`origin` stays Majrusz upstream), push `26.3` to `fork`. Never PR the trimmed library upstream.
- Release: tag `v26.3-1.9.10`, pre-release, jar `build/libs/majruszs-difficulty-fabric-26.3-1.9.10.jar` (published by the user).
- Library fork created by the user: `DereckAn/MajruszLibrary`, remote `fork`, branch `26.3` pushed. Note: `gh repo fork` needs to run inside the library clone (or pass `Majrusz/MajruszLibrary`).

### Build notes (Step 1)
- Both repos are now **single-module Fabric Loom projects** (`src/` = old `common/src` + `fabric/src`); Forge, NeoForge and the CurseForge/Modrinth upload tasks are gone. Branch `26.3` in both (the library branch is local only, no fork yet).
- Versions: Loom 1.19.3, Gradle 9.8.2, Fabric Loader 0.20.1, Fabric API 0.165.0+26.3, Java 25.
- The old build-time filter that turned `@OnlyIn(Dist.CLIENT)` into Fabric's `@Environment(EnvType.CLIENT)` was applied to the sources once instead.
- Build order:
  ```bash
  cd ~/Documents/GIT/MajruszLibrary && JAVA_HOME=/usr/lib/jvm/java-27-openjdk ./gradlew publishToMavenLocal
  cd ~/Documents/GIT/MajruszsProgressiveDifficultyMod && JAVA_HOME=/usr/lib/jvm/java-27-openjdk ./gradlew build
  ```
- On this machine `grep` is aliased to ugrep; use `command grep` in scripts.

---

## Survey results (Step 0, 2026-11-02)

Library cloned to `~/Documents/GIT/MajruszLibrary` (branch `1.20.X`, upstream, not forked).
The closure script is in the session scratchpad; rerun it by following imports from `common/` + `fabric/` of the mod.

### Size
- **Mod:** 160 Java files in `common`, 2 in `fabric`. Resources: 32 advancements, 38 loot tables, 24 recipes, 16 tags, 10 worldgen, 1 damage type, 103 models, 82 textures, 12 langs, and `data/.../custom` + `assets/.../custom` (the library's JSON model/animation format for the custom mobs).
- **Library (common + fabric):** 190 non-mixin classes, 71 mixins (58 common, 13 fabric).
- **Library needed by the mod:** ~148 classes (events 49, data/config 27, modhelper 8, platform 7, registry 7, animations 6, rest small) and **~45 mixins**. The other ~42 classes and ~26 mixins (trades, breeding, taming, brewing, brushing, crafting, phantoms, noise, creative tab icons, item decorations…) can be dropped.

### Features and the library parts they use
| Feature | Mod code | Library parts |
|---|---|---|
| Game stages (Normal/Expert/Master, per player or global) | `gamestage/`, `data/WorldData` | Serializables/Reader (config + save data), Command, Events (OnEntityDied, OnLevelsLoaded, OnPlayerLoggedIn, OnDimensionChanged, OnClampedRegionalDifficultyGet), custom advancement trigger (IMixinCriteriaTriggers) |
| Undead Army | `undeadarmy/` (25 files) | Serializables, Command, ParticleEmitter, SoundEmitter, LevelHelper, LootHelper, TimeHelper, OnServerTicked, OnEntityDied, OnLevelsLoaded; boss bar |
| Blood moon (logic) | `bloodmoon/` | Command, OnServerTicked, OnClientTicked, OnMobSpawnLimitGet (MobCategory mixin), OnMobSpawnRateGet (NaturalSpawner mixin), network sync |
| Blood moon (visuals) | own mixins `MixinFogRenderer`, `MixinLevelRenderer`, `MixinLightTexture` | none (mod's own client mixins) |
| Effects: Bleeding, Bleeding Immunity, Glass Regeneration | `effects/` | EffectDef/EffectHelper, OnEntityDamaged/PreDamaged/Ticked/EffectCheck/Died, OnItemEaten, OnItemTooltip/AttributeTooltip, OnGuiOverlaysRegistered (HUD), ParticleEmitter |
| Items (bandages, scrolls, Soul Jar, Ender Pouch, Recall Potion, Wither Sword, Enderium tools/armor, Tattered armor, Battle Standard, Cerberus Fang, smithing template, spawn eggs) | `items/` (23), `itemsets/` (5) | ItemHelper, AttributeHandler, OnItemRendered (hand renderer mixin), OnEntityModelSetup (HumanoidModel mixin), OnItemRenderColorsGet, OnItemInventoryClicked, OnPlayerInteracted, OnLootGenerated, OnBreakSpeedGet, OnChorusFruitEaten, OnEnderManAngered, OnLootingLevelGet |
| Blocks (Enderium block/ore, fragile + infested end stone, infernal sponge) + ore worldgen | `blocks/`, `data/.../worldgen` | BlockHelper, OnBlockPlaced (ServerLevel mixin) |
| Treasure bags (7 kinds) | `treasurebag/`, `items/TreasureBag` | LootHelper, OnEntityDied/Damaged, OnItemFished, OnRaidDefeated, network (opening screen), Command |
| Custom mobs: Tank, Giant, Cerberus, Cursed Armor, Creeperling (+ a modded Illusioner using the vanilla model) | `entity/` (17) | **animations/** (JSON models + animations: ModelDef, ModelParts, Animations), OnEntitySpawned, OnExploded, OnEntityTicked |
| Mob tweaks (`features/`, 22 classes: stronger mobs, creeper changes, jockeys, mob groups, double loot, spawn blockers, killer bunny, player zombie, evoker totem, pillager fireworks…) | `features/` | AttributeHandler, OnEntitySpawned, OnExploded, OnLootGenerated, OnExpOrbPickedUp, OnMobSpawnLimitGet/RateGet |
| Loot (CurseRandomly function, loot pool injection) | `loot/`, mixins `IMixinLootPool*` | library loot mixins (`MixinLootTable`, `LootTable2`, `LootTableBuilder`, `LootDataType`) |
| Configs | `data/Config` + `static {}` blocks everywhere | `data/` (Serializables, Reader): one JSON config, synced to clients over the network |
| Commands | `*Command(s).java` | `command/` (Command, CommandData, IParameter) + `MixinCommands` |

### Riskiest areas
1. **Custom mob models/animations** (`animations/` + `assets/.../custom` JSON). The library drives `ModelPart`s directly from its own JSON format; 26.x renderers use render states, so this system has to be rewritten, not renamed. Step 4.
2. **Blood moon visuals**: `FogRenderer`, `LevelRenderer` and `LightTexture` were rewritten (fog environments, lightmap shader). The mixins can't just be retargeted; expect new hooks. Step 5.
3. **Item rendering hooks** (`OnItemRendered`, `OnEntityModelSetup`, item colors): item rendering moved to item models/`ItemStackRenderState` in 1.21.4. Affects Soul Jar, scrolls and Tattered/Enderium armor visuals.
4. **Loot mixins** (4 library + 2 mod): replace with Fabric `LootTableEvents` instead of porting.
5. **Items/armor/tools/potions/spawn eggs**: all data-driven since 1.21.2–1.21.5 (`setId`, `ToolMaterial`, `ArmorMaterial` → equipment assets, spawn egg components, `assets/<ns>/items/*.json` for every item).
6. **Networking** (`NetworkObject`) → `CustomPacketPayload` + `StreamCodec`.
7. **Datapack JSON**: 1.20.1 → 26.x folder renames and format changes on ~120 files (recipes, loot, advancements, tags, worldgen).

Low risk: the config/serialization core, events bus, math/text/time helpers (plain Java).

**Mixin shortcut:** about half of the ~45 needed library mixins can be replaced by Fabric API events (server/client ticks, server start, commands, player join, entity load, death, damage, tooltips, HUD, loot tables), which removes crash risk instead of porting it.

### Overlaps with Improved Mobs
| This mod | Improved Mobs | Effect when both run |
|---|---|---|
| `mobs_spawn_stronger` (+15%/+30% health, +10%/+20% damage at Expert/Master) | difficulty attribute scaling (`attributes.json`) | **stacks**: both multiply mob health/damage |
| `spawn_rate_increaser` + blood moon spawn rate/limit | `Night Monster Cap Multiplier` | **stacks**: many more night mobs |
| Game stages raise vanilla regional difficulty | own difficulty value | independent, both make mobs harder |
| Evoker totems, pillager fireworks, player zombies, Undead Army gear | `Equipment Chance` / enchanting | IM may give extra gear to these mobs; minor |
| Creeper changes (charged, chain reaction, explode behind walls) | block breaking AI, TNT destruction | minor, different behaviours |

---

## Assessment (2026-11-01)

It can be done, but it's the biggest of the user's mods so far.

| | Improved Mobs (done) | This mod |
|---|---|---|
| Starting version | 1.21.1 | **1.20.1** (bigger jump) |
| Mod code | 136 files | 166 files |
| Required library | TenshiLib: trimmed to ~80 files | **Majrusz Library: 297 files, 79 mixins**, which the mod uses heavily |
| Custom mobs with 3D models | none | **5**: Tank, Giant, Cerberus, Cursed Armor, Creeperling |
| Visual effects | small HUD | Blood moon sky and fog changes |
| Original author | active | **Abandoned** (both mod and library stopped at 1.20.1 in April 2024) |

**Hard parts:**
- **Rendering.** The custom mobs and the blood moon sky depend on rendering, which Minecraft rewrote in 1.21.2 (render states) and 1.21.5 (render pipelines). Unlike TenshiLib, it can't be skipped: the mobs are part of the mod.
- **The library.** Majrusz Library hooks deep into the game with 79 mixins; each one can crash the game at startup if it no longer matches.

**Good news:** both the mod and the library are **MIT licensed**, so the port can be published and shared. Keep the MIT license and credit Majrusz.

**Overlap with Improved Mobs:** both mods make mobs stronger over time, give mobs gear and raise their stats. Running both stacks the effects and may conflict. Step 0 must list the overlaps so the user can decide what to turn off in which config.

## Step 7 plan (2026-11-02)

Sub-steps; commit and test in-game after each.
- **7a. One jar.** Copy Improved Mobs (`common` + `fabric` sources, resources, generated resources, mixins, access widener) into this repo under its own package `io.github.flemmli97.improvedmobs`; keep its `improvedmobs` namespace, world data and TOML config. Our `fabric.mod.json` gets its entrypoints and mixins and `"provides": ["improvedmobs"]` (Forge Config API Port looks the mod id up). TenshiLib and Forge Config API Port (MPL-2.0) are bundled jar-in-jar. Test: both mods' features work from one jar, no other mods besides Fabric API.
- **7b. Milestones.** Read `DifficultyFetcher.getDifficulty`. Every 10 levels → Undead Army at a player (replaces the kill trigger); every 20 → blood moon on the next night (random chance stays). `mobs_spawn_stronger` follows the difficulty source setting.
- **7c. Tiers.** Base / Nether visited / End visited + Expert or Master / tier 3 + difficulty > 300: extra elite mobs per wave and in blood moons, more count, more health and damage.
- **7d. New elites.** "Giant skeleton" as a skeleton with the `minecraft:scale` attribute; Mutant Monsters mobs spawned by registry id only when that mod is installed (no code copied, it's AGPL).
- 7b–7d notes: milestones live in `*/listeners/DifficultyMilestoneTrigger.java`; tiers in `tiers/TierConfig.java` (config key `tiers.list`, tier = game stage + Improved Mobs difficulty); blood moon elites in `bloodmoon/listeners/EliteSpawner.java`. `MobDef` keeps its id and spawns nothing when the mod is missing (the vanilla registry would return a pig); `scale`/`health_multiplier` make giant skeletons. Test profile has Mutant Monsters 26.3.1 + Puzzles Lib 26.3.17 (Modrinth); Forge Config API Port comes from our jar-in-jar.
- 7e notes: `client/ConfigScreen.java`, opened from Mod Menu (optional, `compileOnly`; hook in `compat/modmenu/` outside the ClassFinder-scanned package). New setting `improved_mobs_merge.mob_stat_scaling` (IMPROVED_MOBS default / MAJRUSZ / BOTH) gates `MobsSpawnStronger` and Improved Mobs' attribute scaling in `EventCalls.applyAttributesAndItems`. Library change (uncommitted on its `26.3` branch): `Config.Builder.create()` returns the `Config` so the screen can `save()`.
- 7f extras (user requests 2026-11-07): army mobs always glow option (`undead_army.always_highlight`); Totem of Banishing (`items/BanishingTotem.java`: banishes the nearby army without rewards, else postpones difficulty armies by `totem_postpone_levels` 30; costs `totem_difficulty_cost` 5 Improved Mobs difficulty while its scaling is on; recipe gold blocks + bone blocks + totem of undying, 5% in the army treasure bag, 2% in dungeon/mansion/ancient city chests); enchantments Sharpness, Smite, Bane, Power, Impaling, Density and the four Protections go to max level X (`data/minecraft/enchantment/*.json` overrides), anvil "Too Expensive" removed (`MixinAnvilMenu`/`MixinAnvilScreen`), books VI-VIII in Ancient City / End City chests (`loot/ExtraChestLoot.java` + `majruszsdifficulty/extra_chest_loot.json`). Breach left at IV (above ~VI it would cancel more than 100% of armor).
- 7g difficulty slider (2026-11-07): `improved_mobs_merge.difficulty` = MAJRUSZ / GENTLE / STANDARD (default) / HARD / BRUTAL / INFERNO, easiest first. Aids: per-hit cap (`hit_cap` 40% of max HP after armor; MAJRUSZ..STANDARD), crowd dampening (`crowd_damage` 1/0.75/0.5/0.25 for mob hits within 1 s; all but INFERNO), Gentle halves Improved Mobs' damage bonus (`gentle_damage_factor`; patched in IM's `DifficultyAttributeProperty.apply` and `EventCalls.hurtEvent`). BRUTAL and INFERNO stack Majrusz scaling on Improved Mobs. Earned max health on every step (+1 heart per army defeated, +1/2 per blood moon survived, cap 30 hearts, per UUID in world data, never reset). Screen has no hit-cap, hearts or army on/off fields (user: army always on; only Peaceful stops it). Code: `survival/`, `mixin/MixinLivingEntity` (RETURN of `getDamageAfterMagicAbsorb`).
- **7e. Config screen.** In-game screen for the merged settings (check whether Mod Menu has a 26.3 build; otherwise a command or keybind opens it).

## Step 7 ideas (from the user, 2026-11-02, not planned or implemented yet)

**Difficulty source:** Improved Mobs' difficulty value drives everything (it increases over time).
- Every **10 difficulty levels**: an Undead Army starts (almost certainly).
- Every **20 difficulty levels**: a blood moon.

**Tiers that change what the Undead Army and blood moon spawn:**
1. **Base:** current behaviour.
2. **Player has been to the Nether:** more "elite" mobs in the hordes and blood moons, e.g. more Cerberus, giant skeletons, and maybe mutant mobs from [Mutant Monsters](https://github.com/Fuzss/mutant-monsters).
3. **Player has been to the End and reached Expert and Master game stages (hardest difficulty):** hordes spawn many more zombies, evokers, Cursed Armor, witches and Giants, all with more health and damage.
4. **Tier 3 plus Improved Mobs difficulty above 300:** huge numbers of enemies and elite mobs, from vanilla and from the mods.

**Notes to check when planning:**
- "Giant skeletons" don't exist in either mod (there's the Giant, a giant zombie); a new mob or a scaled-up skeleton would be needed.
- Mutant Monsters (by Fuzss) has Fabric builds for 26.3 (26.3.0, 26.3.1). It's **AGPL-3.0**, so it should stay an optional separate mod (spawned only if installed), not copied into this MIT mod.
- Improved Mobs is ARR; merging its code into one mod needs the same care as its port (author was asked via an issue).
- Improved Mobs mobs can already mine (see the answer below); decide whether horde mobs should dig to underground players.

**Improved Mobs block breaking (checked in its source):** about 30% of mobs (`Breaker Chance` 0.3, from difficulty 0) can break blocks and get a diamond pickaxe or iron axe. By default they only break glass, glass panes, doors and fence gates (`Block Break Whitelist`). Setting `Breaklist as Blacklist = true` turns the list into a blacklist, so they can dig through stone and dirt toward the player, including up and down. They only break what their tool can harvest, unless `Ignore Harvest Check` is on, and broken blocks can be restored after a delay (`Restore delay`).

## Repos

- **Improved Mobs (for Step 7):** the finished 26.3 port is in `~/Documents/GIT/improvedmods` (`ImprovedMobs/` + trimmed `TenshiLib/`); read its `CLAUDE.md`. In the test profile it's parked in `mods-disabled/`.

- **This repo** is the user's fork, `git@github.com:DereckAn/MajruszsProgressiveDifficultyMod.git`, branch `1.20.X`. Upstream is `Majrusz/MajruszsProgressiveDifficultyMod`.
- **Required library:** `https://github.com/Majrusz/MajruszLibrary`, branch `1.20.X`. Clone it to `~/Documents/GIT/MajruszLibrary`. The user has no fork yet; **ask before forking it** to their account.

## Ground rules

- Load the `minecraft-26-port` skill (migration tables, build setup, rename/remap scripts, mixin checker) and the `install-mod` skill (installs the jar into the CurseForge test profile).
- Read `~/Documents/GIT/improvedmods/CLAUDE.md`: a finished port with the same workflow, including how the user tests.
- Fabric only: remove Forge and NeoForge.
- Port only the parts of Majrusz Library the mod actually uses (like the trimmed TenshiLib in the improvedmods project). Bundle it into the mod jar with Fabric jar-in-jar so players install one mod.
- Run Gradle with `JAVA_HOME=/usr/lib/jvm/java-27-openjdk` (Loom 1.19.x and Gradle 9.8+ need a Java 25+ runtime; the target is Java 25).
- Before every in-game test, run the skill's `checkmixins.py` against `~/Documents/curseforge/minecraft/Install/versions/26.3/26.3.jar`.
- If a feature can't be kept the way it was, **ask the user** before changing or removing it.
- Work on a new `26.3` branch and commit at the end of each step. **Push only when the user says so.**
- Stop at the end of each step, tell the user in plain words what to test in-game, and wait for their result. Logs are in `~/Documents/curseforge/minecraft/Instances/test/logs/latest.log`.

## Plan

**Step 0 - Survey (no code changes).** Clone the library. List every feature of the mod and which Majrusz Library parts each one uses:
- game stages
- Undead Army
- blood moon
- effects such as bleeding
- items and blocks
- treasure bags
- the 5 custom mobs
- configs and commands

Then estimate which library parts are needed and which can be dropped, flag the riskiest areas, and list the overlaps with Improved Mobs. Write the results into this file and show the user a summary before continuing.

**Step 1 - Build setup.**
- Set up a Fabric-only 26.3 build: Loom 1.19.x, Gradle 9.8+, Java 25 target, Fabric API for 26.3.
- Set up the trimmed library as its own project, published to mavenLocal and included jar-in-jar.

**Step 2 - Library port.** Port the needed library code, leaving custom mob rendering and animation for step 4. Get it compiling, then check every library mixin against the 26.3 jar.

**Step 3 - Gameplay (first playable version).**
- Port everything except custom mob rendering and blood moon visuals: game stages, Undead Army, blood moon logic, effects, items, blocks, treasure bags, configs and commands.
- Check every datapack JSON (loot tables, recipes, tags, advancements) against the 26.3 format.
- If the 5 custom mobs' rendering isn't ready, leave them registered but temporarily unspawnable.
- Install with `/install-mod`; the user tests in a new world.

**Step 4 - Custom mobs.** Port the models, renderers and animations for Tank, Giant, Cerberus, Cursed Armor and Creeperling to the 26.x rendering system (render states, 1.21.5+ rendering). Re-enable their spawning. The user tests each mob in-game.

**Step 5 - Blood moon visuals.** Port the sky, fog and level rendering mixins. The user tests a blood moon night.

**Step 6 - Polish and publish (only when the user says so).**
- Clean up, and update the README and changelog.
- Push the `26.3` branch to the user's fork (and to the library fork).
- Create a GitHub pre-release crediting Majrusz.

Expect steps 2 and 4 to take the longest. Step 3 is the first point where the mod is playable.

## How to start or continue

Open Claude Code in this repo (`~/Documents/GIT/MajruszsProgressiveDifficultyMod`) and say:

```text
Read PORT_PLAN.md and start with the current step.
```

In later sessions, the same sentence continues from wherever the Status section says.
