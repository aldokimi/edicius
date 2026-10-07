# LibGDX Vertical Slice (Phase 0–1) Implementation Plan

> **For agentic workers:** Execute inline in one session. Do **not** dispatch subagents. Do **not** commit unless the user explicitly asks. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Scaffold LibGDX (`core` + `lwjgl3`) beside the legacy Swing `app` module and deliver a playable vertical slice: move, aim, shoot, dash, one map, grunt + shooter, real projectiles, HUD, pause.

**Architecture:** Fixed-timestep simulation in `core` with Screen flow (Boot → Menu → Run). Presentation uses `OrthographicCamera` + `SpriteBatch`. Legacy `app/` remains untouched as behavior reference until the slice replaces it.

**Tech Stack:** Java 21, Gradle 8.12.1, LibGDX 1.13.1, LWJGL3, JUnit 5 (core unit tests).

## Global Constraints

- Desktop-only for this phase (no Android/iOS modules)
- Package root: `com.edicius`
- No git commits unless the user explicitly requests them
- Single-agent execution only (no parallel subagents)
- Do not delete or “fix” the Swing `app/` module in this plan
- Assets live in repo-root `assets/` and are shared by `lwjgl3`
- Prefer relative classpath assets (never hardcoded absolute music paths)

---

## File structure (locked)

| Path | Responsibility |
|---|---|
| `settings.gradle` | Include `app`, `core`, `lwjgl3` |
| `build.gradle` | Root repos + shared Java toolchain |
| `gradle.properties` | `gdxVersion=1.13.1` and related versions |
| `core/build.gradle` | LibGDX core + JUnit |
| `lwjgl3/build.gradle` | LWJGL3 backend + `run` task |
| `assets/` | Maps, placeholders, audio |
| `core/.../EdiciusGame.java` | `Game` subclass, screen switching |
| `core/.../screen/*` | Boot, MainMenu, Run screens |
| `core/.../sim/*` | World, entities, combat (no draw calls) |
| `core/.../render/*` | WorldRenderer, HudRenderer |
| `core/.../input/GameInput.java` | Keyboard/mouse → commands |
| `lwjgl3/.../Lwjgl3Launcher.java` | Desktop entry |

---

### Task 1: LibGDX Gradle scaffold

**Files:**
- Create: `build.gradle`, `gradle.properties`, `core/build.gradle`, `lwjgl3/build.gradle`
- Create: `core/src/main/java/com/edicius/EdiciusGame.java`
- Create: `lwjgl3/src/main/java/com/edicius/lwjgl3/Lwjgl3Launcher.java`
- Create: `assets/.gitkeep`
- Modify: `settings.gradle`

**Interfaces:**
- Produces: `EdiciusGame` extends `com.badlogic.gdx.Game`; launcher calls `new Lwjgl3Application(new EdiciusGame(), config)`
- Consumes: none

- [ ] **Step 1: Update settings.gradle**

```gradle
rootProject.name = 'Edicius'
include('app')
include('core')
include('lwjgl3')
```

- [ ] **Step 2: Add gradle.properties**

```properties
org.gradle.daemon=true
org.gradle.jvmargs=-Xms512M -Xmx1G
gdxVersion=1.13.1
```

- [ ] **Step 3: Add root build.gradle**

```gradle
allprojects {
    apply plugin: 'eclipse'
    version = '1.0.0'
    ext {
        appName = 'Edicius'
        gdxVersion = gdxVersion
    }
    repositories {
        mavenCentral()
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
    }
}

subprojects {
    apply plugin: 'java-library'
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
}
```

Note: `app` already applies `application`/`java` — keep `app/build.gradle` as-is; if root `subprojects` conflicts, scope Java toolchain only to `core` and `lwjgl3` instead of all subprojects.

- [ ] **Step 4: Add core/build.gradle**

```gradle
[compileJava, compileTestJava]*.options*.encoding = 'UTF-8'

dependencies {
    api "com.badlogicgames.gdx:gdx:$gdxVersion"
    testImplementation 'org.junit.jupiter:junit-jupiter:5.11.4'
}

test {
    useJUnitPlatform()
}
```

- [ ] **Step 5: Add lwjgl3/build.gradle**

```gradle
apply plugin: 'application'

dependencies {
    implementation project(':core')
    implementation "com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion"
    implementation "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop"
}

application {
    mainClass = 'com.edicius.lwjgl3.Lwjgl3Launcher'
}

run {
    workingDir = rootProject.file('assets').exists() ? rootProject.file('assets') : rootProject.file('.')
    ignoreExitValue = true
}

eclipse.project.name = appName + '-lwjgl3'
```

- [ ] **Step 6: Minimal EdiciusGame + launcher**

`EdiciusGame` clears to dark neon and implements empty `create`/`render`. Launcher window 1280x720, title `Edicius — Neon Protocol`.

- [ ] **Step 7: Verify compile**

Run: `.\gradlew.bat :lwjgl3:compileJava :core:test --console=plain`  
Expected: BUILD SUCCESSFUL

- [ ] **Step 8: Commit** — SKIPPED (user request)

---

### Task 2: Screens + fixed-timestep Run loop shell

**Files:**
- Create: `core/.../screen/BootScreen.java`, `MainMenuScreen.java`, `RunScreen.java`
- Create: `core/.../sim/SimClock.java`
- Modify: `EdiciusGame.java`

**Interfaces:**
- Produces: `SimClock` with `void accumulate(float deltaSeconds)`, `boolean consumeTick()` at `1/60f`
- Produces: `EdiciusGame.setScreen(...)` transitions Boot → Menu → Run on key/click

- [ ] **Step 1: Implement SimClock (TDD)**

Test: after accumulating `1/30f`, exactly two `consumeTick()` return true, third false.

- [ ] **Step 2: Screens**

Boot loads nothing yet and switches to MainMenu. MainMenu shows placeholder text via `SpriteBatch`+bitmap font (default) and starts Run on Enter / click. RunScreen owns camera + batch and calls sim tick then render.

- [ ] **Step 3: Verify**

Run: `.\gradlew.bat :core:test :lwjgl3:compileJava`  
Expected: tests pass

---

### Task 3: Tile map + collision grid

**Files:**
- Create: `assets/maps/map1.txt` (copy from `app/src/main/resources/maps/map1.txt`)
- Create: `core/.../sim/TileMap.java`, `core/.../sim/GridCollision.java`
- Test: `core/src/test/java/com/edicius/sim/TileMapTest.java`

**Interfaces:**
- `TileMap.load(FileHandle)` → `int[][]` walls where `1` = solid, `0` = walkable; size 20x20
- `GridCollision.blocks(float worldX, float worldY, float halfW, float halfH)` using 64px tiles (`TILE_SIZE = 64`)

- [ ] **Step 1: Failing tests for wall at (0,0) and open tile**
- [ ] **Step 2: Implement TileMap + GridCollision**
- [ ] **Step 3: Draw map in RunScreen with solid-color rects (floor dark, wall brighter) until sprites ported**
- [ ] **Step 4: Verify tests**

---

### Task 4: Player movement, camera, dash

**Files:**
- Create: `core/.../sim/Player.java`, `core/.../input/GameInput.java`
- Modify: `RunScreen.java`

**Interfaces:**
- `GameInput.poll()` → `InputCommand(dx, dy, aimX, aimY, fire, dash, pause)`
- `Player.update(cmd, GridCollision, float dt)` — speed 240 px/s; dash 220px over 0.12s with i-frames

- [ ] **Step 1: Test dash grants i-frames for dash duration**
- [ ] **Step 2: Implement player + WASD + camera follow**
- [ ] **Step 3: Manual run check** (`.\gradlew.bat :lwjgl3:run`) — move without leaving map bounds through walls

---

### Task 5: Projectiles (real type) + shooting

**Files:**
- Create: `core/.../sim/Projectile.java`, `core/.../sim/World.java`
- Modify: `Player` / `RunScreen`

**Interfaces:**
- `World.spawnPlayerShot(x,y,vx,vy)` creates `Projectile` with owner `PLAYER`
- Projectiles are **not** enemies
- Fire on mouse button; cooldown 0.2s; speed 600 px/s toward aim

- [ ] **Step 1: Test projectile removed on wall hit**
- [ ] **Step 2: Implement World entity list + fire**
- [ ] **Step 3: Render shots as small bright rects**

---

### Task 6: Enemies — grunt + shooter

**Files:**
- Create: `core/.../sim/Enemy.java`, `core/.../sim/EnemyRole.java`, `core/.../sim/ai/ChaseBrain.java`
- Port A* idea into `core/.../sim/ai/Pathfinder.java` (simplified grid A*)
- Test: pathfinder finds path around a wall block

**Interfaces:**
- `EnemyRole.GRUNT` melee contact damage; `EnemyRole.SHOOTER` fires at player every 1.0s if distance < 400
- Enemy HP: grunt 30, shooter 40; player shot damage 20

- [ ] **Step 1: Pathfinder unit test**
- [ ] **Step 2: Spawn 3 grunts + 2 shooters on open tiles**
- [ ] **Step 3: Kill removes enemy; room clear when none left**

---

### Task 7: HUD, pause, player HP, game over stub

**Files:**
- Create: `core/.../render/HudRenderer.java`
- Modify: `RunScreen`, `World`

**Interfaces:**
- HUD: HP bar, kill count, “PAUSED”
- Pause toggles sim freeze (still renders)
- Player HP 100; shooter bullets and grunt contact reduce HP; at 0 → return to MainMenu with kill count message (simple)

- [ ] **Step 1: Wire pause from Esc / P**
- [ ] **Step 2: Damage + death flow**
- [ ] **Step 3: Manual playtest checklist** (move, dash, shoot, kill, die, pause)

---

### Task 8: Phase-1 verification gate

- [ ] **Step 1: Run** `.\gradlew.bat :core:test :lwjgl3:compileJava`
- [ ] **Step 2: Confirm playable slice matches exit criteria:** better than prototype feel for move/shoot/dash on one map with two enemy roles and real projectiles
- [ ] **Step 3: Commit** — SKIPPED

---

## Spec coverage (Phase 0–1 only)

| Spec item | Task |
|---|---|
| LibGDX core+desktop scaffold | Task 1 |
| Screens / fixed timestep | Task 2 |
| Map + collision cleanup | Task 3 |
| Twin-stick + dash | Task 4 |
| Real `Projectile` type | Task 5 |
| Grunt + shooter roles | Task 6 |
| HUD + pause | Task 7 |
| Later acts/meta/bosses/MP | Out of scope (future plans) |

## Self-review notes

- No TBDs in task steps
- Types consistent: `InputCommand`, `TileMap`, `World`, `Projectile`, `EnemyRole`
- Full Neon Protocol content is intentionally deferred past this plan
