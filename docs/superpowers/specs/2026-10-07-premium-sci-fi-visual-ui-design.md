# Premium Sci-Fi Visual + UI Overhaul

**Date:** 2026-10-07  
**Status:** Approved for planning  
**Approach:** Curated commercial packs + Scene2D (LibGDX stays)

## 1. Goals

Ship a **premium sci-fi** presentation for Neon Protocol:

- In-run and menus no longer look like sparse Kenney prototypes + default Arial
- **Every menu/option is mouse-clickable**; keyboard shortcuts remain
- Heavy VFX allowed (bloom, lights, particles); prefer fidelity on desktop
- No budget cap for coherent commercial asset packs this pass

### Explicitly out of scope

- New map formats / multi-chamber map redesign
- Line-of-sight / stealth combat rules
- Engine migration (Godot/Unity)
- Full custom commissioned art pipeline (Spine from scratch)

Combat simulation rules stay unchanged unless a visual system needs a thin hook (e.g. shoot event → muzzle particle).

## 2. Architecture

| Layer | Responsibility |
|--------|----------------|
| **Assets** | Commercial sci-fi packs under `assets/runtime/` (`characters/`, `enemies/`, `tiles/`, `vfx/`, `ui/`); fonts; license copies |
| **UI** | Shared `UiSkin` + Scene2D `Stage` on all non-run screens |
| **Run HUD / overlays** | Scene2D overlay for tutorial dismiss, room-clear continue, pause resume |
| **World render** | Animated sheets, richer tiles, `ParticleBus`, post-process bloom/glow |
| **Sim** | Existing `World` / `Player` / `Enemy` / projectiles — no gameplay redesign |

LibGDX `core` + `lwjgl3` remain the stack. `FitViewport(1280×720)` stays the logical UI/run resolution.

## 3. Assets & fonts

### Pack strategy

Buy for **coherence**, not random mix:

1. **World pack** — top-down sci-fi characters (idle/walk/shoot), enemies, floors/walls, props  
2. **VFX pack** — muzzle, hits, dash trail, explosions, soft light blobs  
3. **UI pack** — panels, buttons, frames, icons (neon / HUD)  
4. Audio refresh optional / later — not blocking

Prefer one vendor kit for characters + tiles so scale/perspective match. VFX/UI may be a second vendor.

Drop into `assets/runtime/` with a fixed folder contract so code can integrate before or after final pack files land (placeholders allowed temporarily). Keep chroma as color multiply on compatible sheets.

### Fonts

- **Display:** bold futuristic (titles, brand, room clear)
- **Body:** clean readable HUD/menu font
- Ship FreeType and/or pre-baked BitmapFont atlases at title / UI / HUD sizes
- Remove all `new BitmapFont()` default Arial usage from shipping screens

### Credits & licenses

- Update `CREDITS.md`
- Store license texts under `assets/licenses/` for every pack

### Acquisition workflow

Player/owner purchases (or approves purchase links). Integration lands files under the runtime contract and wires `GameAssets` / `UiSkin`.

## 4. Mouse UI (Scene2D)

### Shared `UiSkin`

- Buttons, labels, list rows, panels from UI pack + display/body fonts
- Hover / pressed / disabled states
- Locked options appear disabled and ignore clicks

### Screen coverage

| Screen | Mouse actions |
|--------|----------------|
| Main menu | Campaign, Daily, Endless, Meta, Codex, Shells |
| Chapter select | Unlocked chapters; Back |
| Character / chroma | Shell cards; chroma cycle; Confirm; Back |
| Meta tree | Spend branches; cosmetic cycle; Back |
| Codex | Back (entries informational; scroll if needed) |
| Brief | Drop in; Back |
| Upgrade | Click one of three upgrades |
| Results | Return to menu |
| Run overlay | Dismiss tutorial; Continue on clear; Resume on pause |

Keyboard shortcuts remain as duplicates of the same actions.

### Input model

- Each menu screen: `Stage` + `FitViewport(1280×720)`; `Gdx.input.setInputProcessor(stage)` on `show`, clear/restore on `hide`
- **Run:** combat keeps `GameInput` (aim + LMB fire). Overlay `Stage` is active only when tutorial / pause / room-clear UI is shown so buttons do not steal fire during normal combat
- Brand title uses display font; menu rows are real buttons with hitboxes (not bare `font.draw` lines)

## 5. World render & VFX

### Units

- Replace static stand/armed pairs with animation sheets where the pack provides idle / walk / shoot
- Drive `SheetAnimator` from `moving`, shoot events, facing / aim angle
- Bosses: distinct sheet + larger scale

### Tiles

- Multi-variant floors/walls from the pack; existing biome tints remain
- Optional non-colliding prop decoration where maps allow

### Particles & juice

- Central `ParticleBus`: muzzle, hit sparks, death burst, dash afterimage, room-clear flash
- Soft additive lights under player / boss (sprite blobs or point lights)
- Particle pool hard cap (~200–400 active) to hold ~60 FPS on a mid desktop

### Post-process

- Framebuffer + bloom/glow on bright VFX (tunable intensity)
- Optional mild chromatic fringe on damage — keep subtle for readability

### Cursor

- Custom reticle in-run; hide system cursor while playing

## 6. Rollout order

1. Fonts + `UiSkin` + Main Menu / Chapter / Brief clickable  
2. Remaining menus + Upgrade / Results / Run overlays  
3. Swap runtime packs + animators  
4. `ParticleBus` + bloom  
5. Polish (cursor, optional hover SFX, CREDITS)

Scaffolding may use folder-contract placeholders so UI/FX engineering is not blocked on pack delivery.

## 7. Verification & success criteria

### Verification

- Manual: every listed menu action works with **mouse only** and with **keyboard only**
- Manual: Boot Sector room shows animated units + bloom without unreadable HUD
- Automated: existing `:core:test` suite stays green (no combat rule changes)
- Resize: click hitboxes remain correct under `FitViewport`

### Done when

- No default LibGDX Arial on shipping screens
- No keyboard-only menus — every listed action has a click target
- In-run characters / enemies / tiles / VFX read as premium sci-fi, not sparse prototypes

## 8. Non-goals reminder

Maps/floors redesign, LOS stealth, and unique commissioned art identity are separate specs after this visual/UI bar is met.
