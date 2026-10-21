# Sprint Planning Guide

Sprints are ordered, undated delivery increments. Assign dates and capacity when known; the current plan does not
invent either. One- or two-week timeboxes can be used when scheduling begins. See [Backlog](Backlog.md) and the
[sprint template](templates/SprintTemplate.md).

## Inputs and readiness

Each sprint has an epic, release, user-facing outcome, governing designs/specifications, existing evidence, concrete
remaining work, dependencies, acceptance cases, demonstration commands, risks, and explicit non-goals.
Agents use [Process](../auto/Process.md), including both exploratory design discussion and work-item selection.
Human contributors need not follow a strict discussion sequence. Finalize behavioral decisions and implementation
acceptance before feature coding; do not treat a draft execution plan as a finalized specification.

## Release outcomes and design quality

V1 is a representative complete Night City workflow, V2 broader standard usage, and V3 custom-event extensibility.
Use [Roadmap](Roadmap.md) and [rebaseline](ReleaseRebaseline.md) for capability boundaries. Robustness and performance
are priorities throughout; release/hardening overhead does not justify postponing required quality.

Every selected capability design explicitly addresses correctness, actionable errors, failure recovery, resource
hygiene, and performance with decisions, constraints, tradeoffs, and acceptance/evidence or justified inapplicability.
Track design readiness separately from passing implementation evidence. Allow independent investigation/design for
later sprints while respecting actual implementation dependencies and ordered delivery.

## Small deliveries and spikes

Prefer tasks such as a specified lifecycle case, a passing specification test, a documented failure/recovery scenario,
or a measured dispatch improvement. Avoid broad tasks such as "implement evaluation" without an observable result.
Reuse existing behavior and strengthen evidence rather than assuming every unmarked claim needs reimplementation.

A spike answers a concrete question through the smallest executable proof. Record the decision or unresolved risk,
retained fixture/test, and follow-up backlog link. A spike is not silent feature implementation or release evidence.

## Demos and evidence

V1-E04 gives Night City its own Maven POM. Its independent consumer build depends on the installed library artifact;
root tests can still compile the shared example sources. A successful root test run is not consumer evidence, and
remote Maven publication is not required for local consumption.


Use Night City-backed specifications where they fit, with exact Maven selectors, input scenarios, and expected
assertions. Unit-test-only topics may use focused unit scenarios. Run `mvn clean install` before closing a coding task,
and inspect actual reports rather than relying on the command's exit code alone.

Record runtime/tool versions, actual passed/skipped totals, missing evidence, and relevant artifact checks. A planned
sprint has no completion claims or invented review history. Use the [review template](templates/SprintReview.md).

## Performance

Grow topic-specific `*Benchmark` classes in `src/test/java/com/taitl/existential/benchmarks/` as features are
implemented.
Use the existing `benchmarks` profile; store run metadata, samples, medians/ranges, and conclusions under
[performance](../performance/Readme.md). Link records from sprints and plans instead of duplicating measurements.
Benchmark changed hot paths and release baselines; document why measurement is inapplicable for other changes.
Retain early attention to algorithmic complexity without promoting speculative optimizations into release scope.

## Hardening and closeout

Every release ends with a dedicated hardening sprint, planned after all its feature sprints. Admit no new features.
Close release blockers and verify the public behavior, retained regression suite, diagnostics, resource hygiene,
examples, artifact contents, applicable consumer/runtime compatibility, coverage collection, and performance baselines.

Keep evidence lightweight in the release charter and hardening plan's Outcomes & Retrospective. Record exact commands,
versions, totals/skips, benchmark links, blockers, and approved deferrals. `mvn clean install` is necessary but does not
replace missing acceptance tests, actual coverage data, independent consumer evidence, or claimed remote publication.
