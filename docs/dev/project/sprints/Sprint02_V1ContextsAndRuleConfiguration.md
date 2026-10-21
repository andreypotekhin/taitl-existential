# Sprint 02: V1 Contexts and Rule Configuration

Status: planned. Dates and capacity: not assigned. Epic: [V1-E02](../Backlog.md#v1-e02).
Release: [V1](../V1.md). No implementation completion is implied by this charter.

## Sprint goal and user-facing outcome

Configure parent, wildcard, generic, and custom-transaction rules with deterministic resolution and ordering.

## Entry conditions and dependencies

[Sprint 01](Sprint01_V1LibraryAccessAndConfiguration.md) is complete with its evidence and unresolved blockers reviewed.
Use the [agent process](../../auto/Process.md) to resolve design/specification decisions before implementing them.

## Specification and design

Primary: [LibraryAndConfiguration specification](../../specifications/LibraryAndConfiguration.spec.md) and
[design](../../design/LibraryAndConfiguration.design.md). The
[traceability matrix](../TraceabilityMatrix.md) supplies the other topic requirements assigned to this epic.
Record exact requirement identifiers in the delivery plan and tests as work is admitted.

## Remaining work

- Specify overlapping wildcard/parent/exact context precedence, tie-breaking, and transaction-rule composition.
- Extend existing context/factory tests to conflicting and registration-order scenarios.
- Verify builders, TypeKey constraints/effects, inherited factories, and caller-supplied transaction configuration.
- Review child-context and TypeKey suggestion records against current tests before archiving any as complete.

## Acceptance

- An overlapping-context example has one specified invocation order, reproduced by tests.
- Configured and caller-supplied transactions preserve intended factory inheritance and rule ordering.
- Unsupported or empty context configurations fail with an actionable explanation.

## Demo and validation

From the repository root:

    mvn -Dtest=UserCanConfigureContexts,UserCanConfigureCustomTransactions test

Inspect assertion results and demonstrate the relevant Night City-backed scenario where applicable.
Before completion run `mvn clean install`; record actual executed/skipped totals and inspection of required checks.

## Performance

Capture configuration-resolution cost and rerun the custom-transaction benchmark if overlays or factories change.
Store comparable samples under [performance](../../performance/Readme.md), linking the relevant record here.

## Risks

Existing ordering suggestions disagree; resolve the intended behavior before implementation.

## Non-goals

Trie/strategy-registry optimizations, arbitrary configuration replacement, and full V2 effects semantics.

## Execution plan and review

No feature-delivery execution plan is finalized yet. Create one under `docs/dev/planning/` when the work is selected;
use [Plans.md](../../auto/Plans.md). Update this charter with its link and any admitted scope changes.

Review pending. Record completed requirement IDs, exact commands, tool/runtime versions, executed/passed/skipped totals,
demo output, performance record links, remaining blockers, and explicit deferrals. Use the
[review template](../templates/SprintReview.md). A planned charter is not a completed sprint.
