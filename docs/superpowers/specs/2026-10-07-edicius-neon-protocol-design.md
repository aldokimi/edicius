# Edicius — Neon Protocol Design Spec

**Date:** 2026-10-07  
**Status:** Approved in brainstorming; pending user file review  
**Product approach:** Neon Protocol (hybrid campaign-as-runs, stylish arcade sci-fi)  
**Tech direction:** Rebuild on LibGDX (desktop-first); current Swing app is prototype reference only  

---

## 1. Decisions locked

| Topic | Choice |
|---|---|
| Ambition | Long-horizon IP + content systems (“famous game” ceiling) |
| Engine | Migrate to LibGDX; do not expand Swing as the ship target |
| Tone | Stylish arcade sci-fi (neon ruins, rogue AI, swagger) |
| Loop | Hybrid: story chapters that play as short runs + meta-progression |
| Platforms (v1) | Desktop only (Windows / macOS / Linux); mobile later |
| Multiplayer | Single-player v1; architecture must not block future MP |
| Combat feel | Top-down twin-stick, deepened (dash, weapons, telegraphs); light positioning pressure |

---

## 2. Current game capture (prototype)

### 2.1 Fantasy today

Survive waves in a top-down arena. Kill all enemies to advance levels. Unlimited fire shots. Death shows game-over with kill count. No narrative win condition.

### 2.2 Stack

- Java + Gradle
- Swing / `Canvas` / `BufferStrategy`
- ~60 updates-per-second `Timer`-driven loop
- Desktop only

### 2.3 Implemented systems

- **Player:** controller-driven movement, 8-direction sprites (`dave`), mouse-aim fireballs
- **Enemies:** `matt` sprites; brain FSM (`stand` ↔ `wonder`); A* chase (`Node.findPath`); shoot when in range
- **World:** tile maps from text files (`map1.txt`–`map3.txt` loaded; gameplay hardcodes `map1`)
- **Collision:** wall + entity collision boxes; camera follows player
- **UI:** HP bar, level, kills; pause and music buttons; main menu and game-over screens
- **Progression (numeric):** kill quota intended as `level * 2`; spawn currently forces a fixed count (5) in places

### 2.4 Architecture problems (do not copy into LibGDX)

- Projectiles are `Enemy` instances with an `isShot` flag (no real projectile type)
- Duplicate / inconsistent health fields on `Player`; combat and kill logic mixed into `Renderer`
- Hardcoded absolute music path; `map2` / `map3` unused in gameplay
- Dead or commented level-progression paths; static mutable globals (`State.gameOver`, etc.)
- Menu and game use separate Swing frames
- Little automated gameplay testing

**Authors (existing README):** Aldokimi, talalquleh.

---

## 3. Gaps and improvements

### 3.1 Gameplay gaps

- No story, brand fantasy, or satisfying campaign win state
- One active map; no biome variety, bosses, or set pieces
- One enemy archetype; AI is chase + shoot only (no telegraphs, roles, elites)
- Unlimited ammo; no weapons/builds; no dash / i-frames — low skill ceiling
- Level progression is a counter only; spawn count inconsistent with kill quota
- No pickups, hazards, or arena events

### 3.2 Technical / product gaps

- Swing limits polish, packaging, and future multiplayer
- Entity model conflates units and bullets; simulation mixed with rendering
- Hardcoded paths, magic numbers, static game state
- No save/meta, settings persistence, or accessibility options
- Docs are mostly javadoc; no content pipeline or design source of truth
- Weak tests for combat, pathfinding, and level flow

### 3.3 Rewrite priorities

1. Clean domain model: `Player`, `Enemy`, `Projectile`, `Pickup`, `Arena` — combat in simulation, not draw code
2. Data-driven content: maps, waves, enemy defs, weapons as assets / JSON
3. Readable combat: telegraphs, dash, knockback, distinct hit feedback
4. Hybrid loop: chapter → run (rooms/waves) → reward → story beat → meta unlock
5. Desktop packaging (LWJGL3), relative assets, proper audio
6. Future-MP hooks: command-style input, no hidden global mutable world, seeded RNG for dailies
7. Debug tools + automated tests for pathfinding, collision, wave clear

---

## 4. Story and IP — Neon Protocol

### 4.1 Elevator pitch

In the neon ruins of a dead megacity, you are an **Edicius** — a banned combat protocol wearing a human shell. A rogue network called **THE GRID** is rewriting survivors into puppets. Each chapter is a raid into a GRID sector: short, stylish runs that steal memory shards and corrupt the AI’s control. Clear the sector → story beat → unlock new tools → next raid.

### 4.2 Tone

Hotline swagger + synthwave dread. Short VO/logs and handler lines — not cutscene novels. Humor is dry tech-noir, not slapstick.

### 4.3 Cast

| Character | Role |
|---|---|
| **You (Edicius)** | Silent operator; personality via loadout and kill-feed style quips |
| **Nyx** | Handler in your ear — briefs, sarcasm, lore crumbs |
| **THE GRID** | Antagonist; evolves per act (new enemy behaviors as “patches”) |
| **Act bosses** | Named GRID avatars with a one-line motive each |

### 4.4 Acts (v1)

1. **Boot Sector** — learn the loop; GRID is local; first real boss  
2. **Mirror District** — builds matter; enemy roles diversify; Nyx signal glitches (betrayal beat)  
3. **Root Spire** — climax raid; final GRID form; ending seeds DLC/MP (“other Edicii still online”)

### 4.5 Player fantasy

“I’m the stylish outlaw AI killing the bigger AI — every run looks and sounds like a music-video firefight.”

---

## 5. Levels and chapters

### 5.1 Run shape

Every chapter mission:

1. Hub brief (Nyx)
2. **4–7 arenas** on a short authored path
3. Optional elite room
4. **Boss or sector core**
5. Extract with rewards

### 5.2 Arena types

| Type | Goal |
|---|---|
| **Clear** | Kill all (classic Edicius) |
| **Hold** | Survive a timed GRID surge |
| **Hunt** | Mark + kill a priority target among trash mobs |
| **Hack** | Channel at a node while defending |
| **Gauntlet** | Corridor pressure, denser bullets |

### 5.3 Act content

**Act 1 — Boot Sector**  
- Biomes: warehouse alleys, server yards (evolve current maze DNA)  
- Enemies: Grunts, Shooters  
- Boss: **Warden-01** (telegraphed slam + bullet arcs)  
- Unlocks after: second weapon slot, first meta branch (dash is available from the vertical slice as core combat)  

**Act 2 — Mirror District**  
- Biomes: neon malls, reflective floors, billboard hazards  
- Enemies: + Shielders, Drones, Mimic (lightly copies last player shot archetype)  
- Boss: **Echo Broker** (phase mirrors + arena split)  
- Unlocks: mutators, rare weapon pool; Nyx glitch story beat  

**Act 3 — Root Spire**  
- Biomes: vertical hub rings, collapsing-platform *presentation* (still 2D)  
- Enemies: prior elites + **Patchers** (buff nearby GRID units)  
- Boss: **GRID Root** (multi-phase)  
- Post-game: **Endless Protocol** (infinite sector with daily seed)

### 5.4 Volume target (v1)

- ~18–24 handcrafted arenas
- 3 bosses
- Endless playlist assembled from those pieces
- Procedural *arrangement* of rooms inside a chapter is allowed; room layouts stay authored

---

## 6. Systems and fame features

### 6.1 Core combat (must feel perfect)

- Twin-stick move + aim; **dash with brief i-frames**; readable hitboxes
- Weapon kits: hitscan / projectile / beam archetypes with distinct audio-visual IDs
- Enemy telegraphs before dangerous attacks; roles: grunt, shooter, shield, drone, elite
- Juice: capped screen shake, kill sparks, combo multiplier, style rank per room

### 6.2 Run systems

- In-run upgrades (3-choose-1 after rooms); limited heal economy
- Mid-act mutators (e.g. “low gravity bullets”, “GRID overclock”) for shareable chaos
- Death: keep meta XP / unlock progress; lose run inventory

### 6.3 Meta (between chapters)

- Protocol tree: mobility, offense, defense, utility
- Cosmetics that are not pay-to-win (trails, crosshairs, Nyx skins) — identity for clips
- Codex: enemy/boss logs unlocked by kills

### 6.4 Fame hooks (desktop v1)

- Clip-friendly spectacle + style ranks
- **Daily Protocol** seed + local leaderboard (online board later)
- Strong title/menu identity, synth soundtrack, custom cursor language from prototype
- Seeded endless mode post-credits
- Future co-op readiness: separate input/control from simulation; no singleton “god” player world state

### 6.5 Out of scope for v1

- Online multiplayer
- Microtransactions
- User-generated maps
- iOS / Android

---

## 7. LibGDX architecture and migration

### 7.1 Strategy

Treat the Swing project as a **behavior reference**, not a line-by-line port. Create a LibGDX multi-module Gradle app (`core` + `desktop`). Keep legacy `app/` until the vertical slice replaces it, then archive.

### 7.2 Module layout

- `core` — game logic, screens, content loaders (no LWJGL)
- `desktop` — `Lwjgl3Application`, packaging
- Shared `assets/` — maps, atlases, audio, JSON defs

### 7.3 Runtime shape

- Screens: Boot → MainMenu → ChapterSelect → Run (arena) → Results → Meta
- Fixed-timestep simulation (~60 Hz); render may interpolate
- Layers: Input → Simulation (entities, combat, waves) → Presentation (sprites, VFX, HUD, audio)
- Content as data: `enemies.json`, `weapons.json`, `chapters/*.json`, maps via Tiled or evolved text maps (start simple; prefer Tiled once editors matter)

### 7.4 Prototype → target mapping

| Prototype | LibGDX target |
|---|---|
| A* `Node` | Keep algorithm; feed from a tile-grid service |
| Enemy brain FSM | State machine + role behaviors |
| Collision boxes | Spatial queries (uniform grid first) |
| Camera follow | `OrthographicCamera` |
| Pause / music / menus | Scene2D for menus; run HUD in world/screen space |

### 7.5 Multiplayer-ready rules (SP-only v1)

- No static mutable world state as source of truth
- Player actions as input commands applied to sim
- Deterministic-friendly RNG seeds for dailies / endless

### 7.6 Risks

- Art and audio pipeline capacity
- Overbuilding ECS too early — start with a simple entity list and light components
- Content bottleneck vs engineering speed

---

## 8. Phased execution plan

| Phase | Goal | Exit criteria |
|---|---|---|
| **0 — Spec & scaffold** | Design locked; LibGDX `core`+`desktop`; CI; Swing archive plan | Project runs empty LibGDX window; tests in CI |
| **1 — Vertical slice** | Move/aim/shoot/dash; one map; grunt+shooter; real projectiles; HUD; pause; relative assets | Feels better than prototype in ~5 minutes |
| **2 — Run loop** | Room chain; ≥2 arena types; 3-choose-1; death→results; Nyx brief text | One complete Boot Sector mission (gameplay) |
| **3 — Combat depth & juice** | 3 weapon kits; telegraphs; style rank; VFX/SFX; stable A* + roles | Clip-worthy Act 1 fights |
| **4 — Act 1 shippable** | Full Boot Sector; Warden-01; meta tree v1; cosmetics stub; save/load | Friends can finish Act 1 |
| **5 — Act 2 + mutators** | Mirror District; Echo Broker; daily seed + local leaderboard | Builds feel meaningfully different |
| **6 — Act 3 + endless** | Root Spire; GRID Root; Endless Protocol; codex; polish; desktop packages | v1 release candidate |
| **7 — Post-v1** | Online boards, co-op MP, Android, more acts/DLC | Not blocking launch |

### 8.1 Success metrics

- Players finish Act 1 without tutorial rage-quits
- Typical run length ~15–25 minutes
- At least one “I want to clip that” moment per run
- Daily / endless provides a return hook after credits

---

## 9. Testing and verification (design-level)

- Unit tests: pathfinding, collision resolution, wave-clear / room completion rules
- Data validation: JSON defs load and reference existing assets
- Playtests at end of Phases 1, 4, and 6 with written feedback notes
- Performance budget: stable 60 FPS on a mid-range desktop laptop in Act 2 density

---

## 10. Open items (resolved defaults)

These were decided during brainstorming when the user chose “do whatever is best”:

- Combat: top-down twin-stick + dash/weapons/telegraphs (not cover-tactics or isometric pivot)
- Product shape: Neon Protocol hybrid, not pure score-chase or slow tactics

No unresolved TBDs remain for starting the implementation plan.
