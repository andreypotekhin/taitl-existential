# Existential Project Documentation

Start with [Polaris](Polaris.md) for the library's purpose, [Assessment](Assessment.md) for current evidence and gaps,
and [Roadmap](Roadmap.md) for release scope. This directory describes a Java library, not Structure's compiler.

## Planning and delivery

- [V1 release][link-01] and [V2 release][link-02]: outcomes, ordered sprints, dependencies, exit criteria, and release
  review.
- [Backlog](Backlog.md): canonical epics, planning progress, and suggestion/TODO triage.
- [Milestones](Milestones.md): evidenced milestones and remaining release gates.
- [Traceability](TraceabilityMatrix.md): every roadmap item, specifications, evidence/gaps, epics, and sprints.
- [Sprint charters](sprints/), [planning guide](SprintPlanningGuide.md), and [Definition of Done](DefinitionOfDone.md).

## Designs, specifications, and evidence

[Specification index](../Specification.md) routes to normative topic claims and paired designs. Agent delivery follows
[Process](../auto/Process.md); human contributors can use these documents without following a strict interaction order.
Execution plans live under `docs/dev/planning/` while active and [past/planning](../past/planning/) when completed.
[Performance records][link-03] accumulate by topic. [Night City][link-04]
is the shared example/test fixture, excluded from production artifacts.

Sprints are ordered and undated. Current V1/V2 feature assignments are preserved. Existing implementation is retained;
remaining work includes specification decisions, missing tests, and release verification. A document's presence or an
old completion mark does not by itself establish passing evidence.

[link-01]: V1.md
[link-02]: V2.md
[link-03]: ../performance/Readme.md
[link-04]: ../../../examples/night_city/Readme.md
