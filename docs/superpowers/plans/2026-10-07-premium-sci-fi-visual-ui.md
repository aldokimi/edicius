# Premium Sci-Fi Visual + UI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans (single-agent inline). Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace default Arial + keyboard-only menus with FreeType sci-fi fonts, Scene2D clickable UI, richer particles, and bloom — keeping LibGDX sim unchanged.

**Architecture:** Shared `UiTheme` (fonts + procedural neon `Skin`) drives Scene2D stages on all menu screens and run overlays. `ParticleBus` + framebuffer bloom wrap world rendering. Commercial pack drop-in paths stay under `assets/runtime/`; until purchased packs arrive, use OFL fonts + generated UI chrome + existing runtime art upgraded with VFX.

**Tech Stack:** LibGDX 1.13.1, Scene2D + FreeType, Java 21, Gradle

## Global Constraints

- Logical resolution `1280×720` FitViewport
- Keyboard shortcuts remain alongside mouse
- Combat rules unchanged; `:core:test` must stay green
- No engine migration

---

### Task 1: FreeType fonts + UiTheme skin

**Files:**
- Create: `assets/fonts/Orbitron-Bold.ttf`, `assets/fonts/Exo2-Regular.ttf` (OFL)
- Create: `core/.../ui/UiTheme.java`
- Modify: `core/build.gradle`, `lwjgl3/build.gradle` (gdx-freetype)
- Modify: `EdiciusGame.java` — hold `UiTheme`, dispose default font path

- [ ] **Step 1:** Add `gdx-freetype` + natives to Gradle
- [ ] **Step 2:** Download OFL fonts into `assets/fonts/`
- [ ] **Step 3:** Implement `UiTheme` with title/body BitmapFonts + programmatic neon `Skin` (TextButton, Label, ScrollPane)
- [ ] **Step 4:** Wire into `EdiciusGame.create/dispose`
- [ ] **Step 5:** Commit

### Task 2: Scene2D menu screens (click + keyboard)

**Files:**
- Create: `core/.../ui/MenuStageScreen.java` (base: stage, viewport, bg draw, input processor)
- Modify: `MainMenuScreen`, `ChapterSelectScreen`, `CharacterSelectScreen`, `MetaScreen`, `CodexScreen`, `BriefScreen`, `UpgradeScreen`, `ResultsScreen`, `BootScreen`

- [ ] **Step 1:** Base `MenuStageScreen` with stage lifecycle
- [ ] **Step 2:** Convert MainMenu + Chapter + Brief first
- [ ] **Step 3:** Convert remaining menus
- [ ] **Step 4:** Manual sanity — compile `:lwjgl3:classes`
- [ ] **Step 5:** Commit

### Task 3: Run overlays clickable

**Files:**
- Modify: `RunScreen.java`, `HudRenderer.java`

- [ ] **Step 1:** Overlay stage for tutorial / pause / room-clear buttons
- [ ] **Step 2:** InputMultiplexer: overlay when visible, else GameInput only for combat
- [ ] **Step 3:** Commit

### Task 4: ParticleBus + bloom

**Files:**
- Create: `core/.../render/ParticleBus.java`, `core/.../render/BloomEffect.java`
- Modify: `WorldRenderer.java`, `RunScreen.java`, `World.java` (event hooks if needed)

- [ ] **Step 1:** ParticleBus pool for muzzle/hit/death/dash/clear
- [ ] **Step 2:** Bloom FBO pass around world draw
- [ ] **Step 3:** Hook player/enemy events
- [ ] **Step 4:** Commit

### Task 5: Credits + verify

- [ ] **Step 1:** Update `CREDITS.md` / `assets/licenses/`
- [ ] **Step 2:** Run `:core:test`
- [ ] **Step 3:** Final commit

---

**Execution note:** User requested single-agent inline execution immediately after plan save.
