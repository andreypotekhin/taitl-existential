# Sprint 07: V2 Contexts and Type Matching

Status: planned. Dates/capacity: unassigned. Epic:
[Contexts and Type Matching](../Backlog.md#contexts-and-type-matching). Release: [V2](../V2.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

## Goal and user outcome

Apply parent/wildcard rules and broader generic/subclass matching with deterministic documented semantics.

## Dependencies and readiness

Review [Sprint 06](Sprint06_V2InstanceIsolationAndConfiguration.md) for actual dependencies and evidence. Allow
independent investigation and design ahead of later sprints. Implementation waits only for its actual behavioral and
technical dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [LibraryAndConfiguration.spec.md] and [LibraryAndConfiguration.design.md].
- [EventsAndConstraints.spec.md] and [EventsAndConstraints.design.md].

Link the exact requirement sections and acceptance evidence for the selected slice in the existing plan/traceability
entry.

## Remaining work

- Resolve parent/wildcard/exact ordering and tie-breaking before changing handlers or matching indexes.
- Verify custom contexts/factories and public builder combinations under those decisions.
- Complete selected generic TypeKey, subclass/interface, and event input combinations.
- Cover configuration errors, ordering conflicts, resource ownership, and matching-cost scaling.

## Acceptance

- Overlapping rules execute in one specified order reproduced by tests.
- Supported type/hierarchy combinations and unsupported forms have clear positive/negative cases.
- Retained V1 results, diagnostic context, and matching performance remain sound.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Measure context count, wildcard overlap, and hierarchy lookup; justify any cache/index tradeoffs. Use
[performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

Existing tests are starting evidence, not proof of all new acceptance cases. From the repository root:

    mvn -Dtest=UserCanConfigureContexts,MultiIndexConfigTypeKeyTest test

Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs.

## Non-goals

Custom event registration, speculative trie/strategy APIs, and theorem-solving behavior.

## Plan and review

No feature-delivery execution plan is finalized yet. Follow [Plans](../../auto/Plans.md) when the work is selected.
Review pending: record completed requirements, actual commands/versions, passed/skipped totals, five quality
obligations, consumer/benchmark evidence where relevant, blockers, and deferrals. Use the [review template].

[LibraryAndConfiguration.spec.md]: ../../specifications/LibraryAndConfiguration.spec.md
[LibraryAndConfiguration.design.md]: ../../design/LibraryAndConfiguration.design.md
[EventsAndConstraints.spec.md]: ../../specifications/EventsAndConstraints.spec.md
[EventsAndConstraints.design.md]: ../../design/EventsAndConstraints.design.md

[review template]: ../templates/SprintReview.md
