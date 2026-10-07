# Swing prototype archive plan

The legacy module `app/` (Java Swing / Canvas) remains in the repo as a **behavior reference** for movement, A*, and combat feel.

- Do not add new features to `app/`
- Ship target is LibGDX (`core` + `lwjgl3`)
- When Act 1 is considered feature-complete on LibGDX, move `app/` to `archive/swing-prototype/` in a dedicated cleanup PR
