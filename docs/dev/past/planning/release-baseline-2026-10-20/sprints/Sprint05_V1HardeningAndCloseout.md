# Sprint 05: V1 Hardening and Closeout

Superseded before implementation on 2026-10-20; this is not completed release work. See [rebaseline][wrap-01].

Status: planned. Dates and capacity: not assigned. Epic: [V1-E05](../Backlog.md#v1-e05).
Release: [V1](../V1.md). No implementation completion is implied by this charter.

## Sprint goal and user-facing outcome

Make every V1 release claim reproducible and reconcile documentation with actual release evidence.

## Entry conditions and dependencies

[Sprint 04](Sprint04_V1TransactionLifecycle.md) is complete with its evidence and unresolved blockers reviewed.
Use the [agent process](../../../../auto/Process.md) to resolve design/specification decisions before implementing them.

## Specification and design

Primary: [TransactionsAndEvaluation specification](../../../../specifications/TransactionsAndEvaluation.spec.md) and
[design](../../../../design/TransactionsAndEvaluation.design.md). The
[traceability matrix](../TraceabilityMatrix.md) supplies the other topic requirements assigned to this epic.
Record exact requirement identifiers in the delivery plan and tests as work is admitted.

## Remaining work

- Run the full clean build and V1 acceptance cases; resolve release-blocking defects without adding features.
- Verify all disabled tests have been repaired or retain a named release blocker; skipped cases are not proof.
- Restore and verify actual coverage collection/checking, rather than accepting a skipped JaCoCo execution.
- Review baseline-compatible performance, examples, diagnostics, artifact contents, and retained V2 prerequisites.
- Reconcile specifications, traceability, milestone/backlog progress, deferrals, and the V1 release review.

## Acceptance

- All V1 scope rows have sufficient current evidence and no unresolved release blockers.
- Coverage evidence actually exists and full Maven verification passes without silently skipped required checks.
- V1 release review records commands, JDK/Maven versions, totals/skips, artifact checks, and benchmark links.

## Demo and validation

From the repository root:

    mvn clean install

Before completion run `mvn clean install`; record actual executed/skipped totals and inspection of required checks.
Also run `mvn -Pbenchmarks test`, inspect both library JARs, and link the release-specific evidence.

## Performance

Repeat comparable transaction-path measurements and explain material changes rather than adding a noisy threshold.
Store comparable samples under [performance](../../../../performance/Readme.md), linking the relevant record here.

## Risks

A successful build can conceal disabled cases and skipped coverage; inspect report contents and plugin output.

## Non-goals

All new features, external publication, and optional optimizations.

## Execution plan and review

No feature-delivery execution plan is finalized yet. Create one under `docs/dev/planning/` when the work is selected;
use [Plans.md](../../../../auto/Plans.md). Update this charter with its link and any admitted scope changes.

Review pending. Record completed requirement IDs, exact commands, tool/runtime versions, executed/passed/skipped totals,
demo output, performance record links, remaining blockers, and explicit deferrals. Use the
[review template](../../../../project/templates/SprintReview.md). A planned charter is not a completed sprint.

[wrap-01]: ../../../../project/ReleaseRebaseline.md
