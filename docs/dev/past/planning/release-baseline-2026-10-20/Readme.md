# Existential Project Documentation

Superseded before implementation on 2026-10-20; this is not completed release work. See [rebaseline][wrap-01].

Start with [Polaris][wrap-02] for the library's purpose, [Assessment][wrap-03] for current evidence and gaps,
and [Roadmap](Roadmap.md) for release scope. This directory describes a Java library, not Structure's compiler.

## Planning and delivery

- [V1 release][link-01] and [V2 release][link-02]: outcomes, ordered sprints, dependencies, exit criteria, and release
  review.
- [Backlog](Backlog.md): canonical epics, planning progress, and suggestion/TODO triage.
- [Milestones](Milestones.md): evidenced milestones and remaining release gates.
- [Traceability](TraceabilityMatrix.md): every roadmap item, specifications, evidence/gaps, epics, and sprints.
- [Sprint charters][wrap-04], [planning guide][wrap-05], and [Definition of Done][wrap-06].

## Designs, specifications, and evidence

[Specification index][wrap-07] routes to normative topic claims and paired designs. Agent delivery follows
[Process][wrap-08]; human contributors can use these documents without following a strict interaction order.
Execution plans live under `docs/dev/planning/` while active and [past/planning](..) when completed.
[Performance records][link-03] accumulate by topic. [Night City][link-04]
is the shared example/test fixture, excluded from production artifacts.

Sprints are ordered and undated. Current V1/V2 feature assignments are preserved. Existing implementation is retained;
remaining work includes specification decisions, missing tests, and release verification. A document's presence or an
old completion mark does not by itself establish passing evidence.

[link-01]: V1.md
[link-02]: V2.md
[link-03]: ../../../performance/Readme.md
[link-04]: ../../../../../examples/night_city/Readme.md

[wrap-01]: ../../../project/ReleaseRebaseline.md
[wrap-02]: ../../../project/Polaris.md
[wrap-03]: Assessment.md
[wrap-04]: ../../../project/sprints
[wrap-05]: ../../../project/SprintPlanningGuide.md
[wrap-06]: ../../../project/DefinitionOfDone.md
[wrap-07]: ../../../Specification.md
[wrap-08]: ../../../auto/Process.md
