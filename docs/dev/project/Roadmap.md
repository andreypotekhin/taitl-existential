# Roadmap

[Polaris](Polaris.md) describes the library's purpose. [Specification index](../Specification.md) routes to the intended
behavior; [traceability](TraceabilityMatrix.md) distinguishes design readiness from implementation verification. The
[rebaseline record](ReleaseRebaseline.md) explains the approved pivot and maps every former scope item.

## Release labels and priorities

V1, V2, and V3 identify project releases 0.0.1, 0.0.2, and 0.0.3, not semantic-version major versions. Sprints are
ordered and undated. Existing implementation/regression tests are retained even where broader verification moves to a
later release. No feature is declared complete by changing its release assignment.

Correctness, actionable errors, failure recovery, resource hygiene, and performance must be addressed in each capability
design and its acceptance evidence. Robustness and performance take priority over minimizing release boundaries or
hardening cycles. These obligations apply to all three releases, not a late quality/performance phase.

## V1: Representative core

Deliver a complete documented Night City workflow: configure ordinary rules in an exact operation context, report entity
changes, validate at commit, demonstrate success and useful rejection, recover and release resources, verify a basic
All/Exists relationship, and enforce an entity-evolution rule. Use explicit before/after values for the selected
evolution scenario; broader customization and combinations remain later work.

Night City becomes a separate Maven project consuming the locally installed library artifact. The consumer verifies the
documented public workflow without root production/test class directories. Maven Central is not required for local
consumption. Scope, epics, and hardening evidence: [V1](V1.md).

## V2: Broader standard usage

Complete independent-instance/configuration behavior, parent/wildcard contexts, generic/subclass matching, built-in
stages/intents/effects, transaction factories and caller-supplied transactions, maintained relationships/indexes,
checkpoint behavior, richer diagnostics, cache/global resource behavior, and their admitted combinations. Measure
broader workload scaling while retaining the V1 consumer and workflow. See [V2](V2.md).

## V3: Extensibility

Complete custom event registration, identity/matching, derived-event splitting, context/transaction binding,
intent/stage integration, and extension failure/resource behavior. Expose only mechanisms justified by those use cases.
Existing optional strategy, hot-reload, dashboard, and plugin suggestions are not automatically admitted. See [V3].

## Distribution and hardening

Local artifact consumption is V1 acceptance. Publication readiness is tracked separately in V2; remote availability is
claimed only after the destination and required artifacts are actually verified. External publication needs its own
authorized action. No functional release waits solely for an unclaimed distribution channel.

Every release ends with a dedicated feature-free hardening sprint. Delivery follows actual dependencies; independent
investigation/design for later capabilities may begin earlier. [Backlog](Backlog.md) contains current epics and
[Milestones](Milestones.md) records evidenced completion.

[V3]: V3.md
