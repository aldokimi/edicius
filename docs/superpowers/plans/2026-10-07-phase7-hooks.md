# Phase 7 hooks (deferred full features)

Shipped as interfaces / stubs so v1 can grow without rewrite:

- `com.edicius.net.LeaderboardClient` + `LocalLeaderboardClient` — swap for HTTP later
- `com.edicius.net.CoopHooks` — documents `MAX_PLAYERS` / disabled flag; sim already uses `InputCommand`
- Android / iOS modules — not added; LibGDX can add `android` module when needed

Online boards, real co-op, and mobile packaging remain post-v1 work.
