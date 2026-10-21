# Existential Project Documentation

Start with [Polaris](Polaris.md), [Roadmap](Roadmap.md), and the [release rebaseline](ReleaseRebaseline.md). V1 delivers
the representative Night City workflow; V2 broadens standard usage; V3 completes custom-event extensibility. Robustness
and performance guide every release, with a dedicated hardening sprint at each boundary.

## Planning and evidence

- [V1](V1.md), [V2](V2.md), and [V3](V3.md): outcomes, admitted scope, ordered undated sprints, and exit criteria.
- [Backlog](Backlog.md): canonical epics and intake approval states.
- [Traceability](TraceabilityMatrix.md): capability requirements, design readiness, verification, and remaining work.
- [Milestones](Milestones.md), [sprints](sprints/), and [Assessment](Assessment.md).
- [Planning guide](SprintPlanningGuide.md) and [Definition of Done](DefinitionOfDone.md).

## Designs, specifications, and examples

[Specification index](../Specification.md) routes to normative claims and paired designs. Agents follow
[Process](../auto/Process.md); human contributors may use it without following a strict conversation sequence. Designs
must address correctness, actionable errors, failure recovery, resource hygiene, and performance. Record capability
readiness separately from verified implementation; existing completion marks are not fresh evidence.

[Night City](../../../examples/night_city/Readme.md) currently supplies shared test sources excluded from library JARs.
Night City Artifact Consumption and Performance will give it an independent Maven POM consuming the installed artifact;
that consumer build is planned, not already runnable. The root's shared fixtures remain useful and do not substitute for
artifact-consumer evidence. [Performance records](../performance/Readme.md) accumulate by topic. Active plans live under
`docs/dev/planning/` and completed plans under [past/planning](../past/planning/).

The [initial release baseline](../past/planning/release-baseline-2026-10-20/Readme.md) is superseded planning history,
not completed sprint work. Existing implementation and regression tests are retained under the new release split.
