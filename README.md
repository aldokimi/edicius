# Edicius — Neon Protocol

Desktop top-down twin-stick raids. You are a banned combat protocol fighting **THE GRID** across neon sectors.

Built with **LibGDX** (`core` + `lwjgl3`). The legacy Swing module `app/` is a behavior reference only.

## Requirements

- JDK 21+
- Windows / macOS / Linux

## Run

```powershell
# Windows (set JAVA_HOME to your JDK 21)
.\gradlew.bat :lwjgl3:run
```

```bash
./gradlew :lwjgl3:run
```

Working directory is `assets/` (maps, sprites, audio).

## Package

```powershell
.\gradlew.bat :lwjgl3:dist
```

Output: `lwjgl3/build/dist/` (jar + `assets/`). Run the jar with the `assets` folder beside it / cwd set correctly.

## Controls

| Input | Action |
|---|---|
| WASD | Move |
| Mouse | Aim |
| LMB | Shoot |
| Space | Dash (i-frames) |
| 1 / 2 / 3 | Weapons (Bolt / Scatter / Beam when unlocked) |
| Esc / P | Pause |
| M | Mute |
| E | Boost hack channeling |
| Enter | Confirm / dismiss tips / advance |

## Modes

1. **Campaign** — Boot Sector → Mirror District → Root Spire  
2. **Daily Protocol** — seeded run + mutator  
3. **Endless** — unlock after Act 3  
4. **Meta tree** — spend protocol points  
5. **Codex** — lore unlocks  
6. **Protocol shells / chroma** — pick from 6 character shells + 6 color presets (cosmetics)

Art credits: see `assets/CREDITS.md` (Kenney CC0 runtime cast; pipeline ready for CHROME DISTRICT drop-in).  


## Develop

```powershell
.\gradlew.bat :core:test :lwjgl3:compileJava
```

Design docs: `docs/superpowers/specs/`

## Credits

[Aldokimi](https://github.com/Aldokimi) & [talalquleh](https://github.com/talalquleh)
