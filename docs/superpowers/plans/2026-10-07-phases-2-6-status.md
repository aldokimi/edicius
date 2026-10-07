# Phases 2–6 implementation status

Implemented in the LibGDX `core` / `lwjgl3` modules (not committed unless requested).

| Phase | Status | Delivered |
|---|---|---|
| 2 Run loop | Done | Room chain, CLEAR/HOLD/HUNT/HACK/GAUNTLET, 3-choose-1 upgrades, Nyx brief, results |
| 3 Combat juice | Done | Bolt/Scatter/Beam kits, telegraphs, style ranks/combo, VFX bursts, audio hooks |
| 4 Act 1 | Done | Boot Sector rooms + Warden-01, meta tree, cosmetics trail, Preferences save/load |
| 5 Act 2 | Done | Mirror District + Echo Broker, mutators, daily seed + local leaderboard |
| 6 Act 3 + polish | Done | Root Spire + GRID Root, Endless Protocol, Codex, `lwjgl3:dist` package, CI on JDK 21 |
| 7 Post-v1 | Deferred | Online boards / co-op / Android intentionally out of scope |

## Run

```powershell
$env:JAVA_HOME = 'C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot'
.\gradlew.bat :lwjgl3:run
```

Desktop package: `.\gradlew.bat :lwjgl3:dist` → `lwjgl3/build/dist/`
