# Topic: Measurement Session

Timestamp: UTC. Revision: full commit. Dirty state: actual value and relevant changes.
Harness/scenario version or source hashes: actual identity. Governing plan/sprint: links.

## Environment and workload

Record JDK vendor/version, Maven version, JVM options, OS/architecture, generic processor/core description,
concurrency, entities/events/rules per operation, state reset, scenario ordering, and warmup/measurement counts.
State what is inside the timed region and what is excluded. Mark unavailable metadata explicitly.

## Reproduction

Record exact commands from the repository root and sample count. Each baseline sample runs in a fresh test JVM.

## Samples and focused output

Keep actual per-scenario values with units for each independent sample and the focused benchmark output.
Record failures, skips, and unexpected conditions rather than silently dropping them.

## Summary and comparison

Report median and minimum/maximum across samples. Link a matching baseline or state that none exists.
Explain comparability, relative change where justified, limitations, and any follow-up. Do not infer percentile latency
or statistical significance from the current average-timing harness.
