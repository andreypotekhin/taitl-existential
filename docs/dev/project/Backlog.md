# Backlog

The [roadmap](Roadmap.md) now plans V1 representative core, V2 broader standard usage, and V3 extensibility.
Robustness and performance are priorities in every release. The [rebaseline](ReleaseRebaseline.md) maps the original
scope/epics to the current plan; archived charters were superseded before implementation, not completed.
[Traceability](TraceabilityMatrix.md) records capability scope, design readiness, and verification independently.

## Completed planning foundation

+ PM-00: initial documentation-first foundation established; see the [assessment](Assessment.md).
+ PM-01: three-release workflow planning established; feature-delivery sprints remain planned.

## V1

<a id="v1-e01"></a>

### V1-E01: Night City Core Workflow

Status: planned. Delivery: [Sprint 01](sprints/Sprint01_V1NightCityCoreWorkflow.md).

Follow a documented Night City operation from rule configuration through events to successful or rejected commit.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

<a id="v1-e02"></a>

### V1-E02: Relationships and Evolution

Status: planned. Delivery: [Sprint 02](sprints/Sprint02_V1RelationshipsAndEvolution.md).

Validate a representative relationship with basic All/Exists and reject an invalid before/after entity change.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

<a id="v1-e03"></a>

### V1-E03: Failure Recovery and Resource Hygiene

Status: planned. Delivery: [Sprint 03](sprints/Sprint03_V1FailureRecoveryAndResourceHygiene.md).

Recover predictably from rejected or failed operations and release the representative workflow resources.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

<a id="v1-e04"></a>

### V1-E04: Night City Artifact Consumption and Performance

Status: planned. Delivery: [Sprint 04](sprints/Sprint04_V1NightCityArtifactAndPerformance.md).

Build and exercise Night City as a Maven project consuming the installed Existential artifact.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

<a id="v1-e05"></a>

### V1-E05: Hardening and Closeout

Status: planned. Delivery: [Sprint 05](sprints/Sprint05_V1HardeningAndCloseout.md).

Close V1 with a robust, documented, independently consumed representative workflow and performance evidence.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

## V2

<a id="v2-e01"></a>

### V2-E01: Instance Isolation and Configuration

Status: planned. Delivery: [Sprint 06](sprints/Sprint06_V2InstanceIsolationAndConfiguration.md).

Use independent instances and the broader documented configuration options without unintended shared state.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

<a id="v2-e02"></a>

### V2-E02: Contexts and Type Matching

Status: planned. Delivery: [Sprint 07](sprints/Sprint07_V2ContextsAndTypeMatching.md).

Apply parent/wildcard rules and broader generic/subclass matching with deterministic documented semantics.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

<a id="v2-e03"></a>

### V2-E03: Stages, Intents, and Transaction Customization

Status: planned. Delivery: [Sprint 08](sprints/Sprint08_V2StagesIntentsAndCustomTransactions.md).

Use built-in execution stages, intents/effects, and customized transactions with predictable timing and failures.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

<a id="v2-e04"></a>

### V2-E04: Indexes and Operational Coverage

Status: planned. Delivery: [Sprint 09](sprints/Sprint09_V2IndexesAndOperationalCoverage.md).

Use maintained relationships and broader lifecycle/reporting/resource behavior with measured scaling.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

<a id="v2-e05"></a>

### V2-E05: Hardening and Closeout

Status: planned. Delivery: [Sprint 10](sprints/Sprint10_V2HardeningAndCloseout.md).

Close broader standard usage while retaining the V1 workflow, consumption, and quality evidence.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

## V3

<a id="v3-e01"></a>

### V3-E01: Custom Event Contracts and Registration

Status: planned. Delivery: [Sprint 11](sprints/Sprint11_V3CustomEventContractsAndRegistration.md).

Declare and register custom events with clear identity, matching, ownership, and invalid-registration behavior.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

<a id="v3-e02"></a>

### V3-E02: Custom Event Evaluation and Extensions

Status: planned. Delivery: [Sprint 12](sprints/Sprint12_V3CustomEventEvaluationAndExtensions.md).

Run admitted custom events through transaction evaluation, splitting, intents, and resource management.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

<a id="v3-e03"></a>

### V3-E03: Hardening and Closeout

Status: planned. Delivery: [Sprint 13](sprints/Sprint13_V3HardeningAndCloseout.md).

Close extensibility with robust custom-event behavior, bounded costs, and retained standard-library evidence.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

## Existing suggestion and TODO intake

The following records retain their existing approval state. Epic assignment is triage, not approval of a proposed
API or algorithm. Related proposals are grouped by epic; resolve duplicate/contradictory proposals in design first.
Several action IDs collide, so links use full filenames. Do not renumber historical records.

- [S02182601 Log redaction][intake-01]: proposal; V1-E03; design/evidence review required.
- [S02182602 Validation Report + Trace Surface][intake-02]: proposal; V2-E04; design/evidence review required.
- [-S02182603 Context Matching Strategy Registry][intake-03]: proposal; V2-E02; design/evidence review required.
- [-S02182604 Fail-Fast Stubs for Unimplemented Actions][intake-04]: proposal; V1-E05; design/evidence review required.
- [S02182605 Transaction Membership Index][intake-05]: proposal; V1-E04; design/evidence review required.
- [S02182607 Event Queue Deque Backing][intake-06]: proposal; V1-E04; design/evidence review required.
- [S02182610 Immutable Rule Snapshot + Hot Reload][intake-07]: proposal; No release assignment (optional hot reload);
  design/evidence review required.
- [+S02192604 Publish License and Source Availability][intake-08]: proposal; V1-E04; review current evidence before
  archive.
- [S02202601 Encapsulate Handler State][intake-09]: proposal; V2-E03; design/evidence review required.
- [S02202602 Stable Rule Identity + Digest][intake-10]: proposal; V2-E04; design/evidence review required.
- [S02202603 Add Security Static Analysis][intake-11]: proposal; V1-E05; design/evidence review required.
- [S02202604 Prefer HashMap for TransactionIndexes][intake-12]: proposal; V1-E04; design/evidence review required.
- [S02202605 Dependency Vulnerability Scans][intake-13]: proposal; V1-E05; design/evidence review required.
- [S02212601 Public Lifecycle Status Dashboard][intake-14]: proposal; No release assignment (optional dashboard);
  design/evidence review required.
- [S02212602 Enforce Secure Config File Permissions][intake-15]: proposal; V2-E01; design/evidence review required.
- [S02212603 Context Path Index (Trie) for Matching][intake-16]: proposal; V2-E02; design/evidence review required.
- [S02222602 Specify context matching precedence and event identity][intake-17]: proposal; V1-E01 / V2-E02;
  design/evidence
  review required.
- [S02222603 Document thread-safety and locking model][intake-18]: proposal; V1-E01 / V2-E01; design/evidence review
  required.
- [S02222612 Introduce immutable configuration snapshots][intake-19]: proposal; V2-E01; design/evidence review required.
- [S02222613 Define event identity and de-dup semantics in specification][intake-20]: proposal; V1-E01; design/evidence
  review required.
- [S02222615 Document concurrency guarantees for configuration and injection][intake-21]: proposal; V2-E01;
  design/evidence review required.
- [S02222616 LimitedInputStream boundary validation][intake-22]: proposal; V2-E01; design/evidence review required.
- [S02222617 Cross-platform secure config permission checks][intake-23]: proposal; V2-E01; design/evidence review
  required.
- [S02242601 Bi-handler constraint consistency][intake-24]: proposal; V1-E02; design/evidence review required.
- [S02242601 Troubleshooting links helper][intake-25]: proposal; V1-E03; design/evidence review required.
- [S02242601 Unify port handler logic][intake-26]: proposal; V1-E02; design/evidence review required.
- [S02242602 Specify intent semantics and access decision flow][intake-27]: proposal; V2-E03; design/evidence review
  required.
- [S02252601 Configuration integrity verification][intake-28]: proposal; V2-E01; design/evidence review required.
- [S02272601 Event Input Validation Troubleshooting Links][intake-29]: proposal; V1-E03; design/evidence review
  required.
- [S02282601 Context identity index in ConfigBuilder][intake-30]: proposal; V2-E02; design/evidence review required.
- [S02282601 Event handler hierarchy matching][intake-31]: proposal; V2-E02; design/evidence review required.
- [S02282601 Getting Started Guide][intake-32]: proposal; V1-E01; design/evidence review required.
- [S02282602 Specify custom event registration and semantics][intake-33]: proposal; V3-E01; design/evidence review
  required.
- [S03012601 Avoid merge-conflict artifacts][intake-34]: proposal; V1-E05; design/evidence review required.
- [S03012601 Split-event validation consistency][intake-35]: proposal; V1-E02; design/evidence review required.
- [S03012601 Transaction helper for safe commit/rollback][intake-36]: proposal; V1-E03; design/evidence review required.
- [S03102601 Centralize argument validation messages][intake-37]: proposal; V1-E03; design/evidence review required.
- [S03102601 Context resolution and ordering pipeline][intake-38]: proposal; V2-E02; design/evidence review required.
- [S03102601 Transaction lifecycle state][intake-39]: proposal; V1-E03; design/evidence review required.
- [S03102602 Specify context matching and rule ordering semantics][intake-40]: proposal; V2-E02; design/evidence review
  required.
- [S02222601 Error metadata for public exceptions][intake-41]: approved; V2-E04; design/evidence review required.
- [S02222605 Key Naming Strategy Injection][intake-42]: approved; V2-E02; design/evidence review required.
- [S02222614 Child context builder support][intake-43]: approved; V2-E02; design/evidence review required.
- [S02242601 Argument wording consistency][intake-44]: approved; V1-E03; design/evidence review required.
- [S03121601 Ex.memo][intake-45]: approved; V2-E03; review current evidence before archive.
- [S02242601 Allow creating constraints with type key][intake-46]: planned record; V2-E02; review current evidence
  before archive.
- [T02182601 Separate Handler Execution Flow in On][intake-47]: TODO intake; V2-E03; design/evidence review required.
- [T02202602 Define Non-Null Handler Action Defaults][intake-48]: TODO intake; V1-E01; design/evidence review required.

Completed plans and historical suggestions remain in [past](../past/); do not treat their old timings or claims
as current release evidence. New optional enhancements require their usual suggestion review.

[intake-01]: ../suggestions/S02182601.Log-redaction.md
[intake-02]: ../suggestions/S02182602.Validation-Report-Trace-Surface.md
[intake-03]: ../suggestions/S02182603.Context-Matching-Strategy-Registry.md
[intake-04]: ../suggestions/S02182604.Fail-Fast-Stubs-for-Unimplemented-Actions.md
[intake-05]: ../suggestions/S02182605.Transaction-Membership-Index.md
[intake-06]: ../suggestions/S02182607.Event-Queue-Deque-Backing.md
[intake-07]: ../suggestions/S02182610.Immutable-Rule-Snapshot-Hot-Reload.md
[intake-08]: ../suggestions/S02192604.Publish-License-and-Source-Availability.md
[intake-09]: ../suggestions/S02202601.Encapsulate-Handler-State.md
[intake-10]: ../suggestions/S02202602.Stable-Rule-Identity-Digest.md
[intake-11]: ../suggestions/S02202603.Add-Security-Static-Analysis.md
[intake-12]: ../suggestions/S02202604.Prefer-HashMap-for-TransactionIndexes.md
[intake-13]: ../suggestions/S02202605.Dependency-Vulnerability-Scans.md
[intake-14]: ../suggestions/S02212601.Public-Lifecycle-Status-Dashboard.md
[intake-15]: ../suggestions/S02212602.Enforce-Secure-Config-File-Permissions.md
[intake-16]: ../suggestions/S02212603.Context-Path-Index-Trie-for-Matching.md
[intake-17]: ../suggestions/S02222602.md
[intake-18]: ../suggestions/S02222603.md
[intake-19]: ../suggestions/S02222612.Config-snapshot.md
[intake-20]: ../suggestions/S02222613.md
[intake-21]: ../suggestions/S02222615.md
[intake-22]: ../suggestions/S02222616.md
[intake-23]: ../suggestions/S02222617.md
[intake-24]: ../suggestions/S02242601.Bi-handler-consistency.md
[intake-25]: ../suggestions/S02242601.Troubleshooting-links-helper.md
[intake-26]: ../suggestions/S02242601.Unify-Transit-Handler-Logic.md
[intake-27]: ../suggestions/S02242602.md
[intake-28]: ../suggestions/S02252601.Config-integrity-verification.md
[intake-29]: ../suggestions/S02272601.Event-Input-Validation-Troubleshooting.md
[intake-30]: ../suggestions/S02282601.Context-identity-index.md
[intake-31]: ../suggestions/S02282601.EventHandlerHierarchyMatching.md
[intake-32]: ../suggestions/S02282601.Getting-Started-Guide.md
[intake-33]: ../suggestions/S02282602.Custom-event-spec.md
[intake-34]: ../suggestions/S03012601.Avoid-Merge-Conflict-Artifacts.md
[intake-35]: ../suggestions/S03012601.Split-event-validation-consistency.md
[intake-36]: ../suggestions/S03012601.Transaction-helpers-for-commit-rollback.md
[intake-37]: ../suggestions/S03102601.Centralize-argument-validation-messages.md
[intake-38]: ../suggestions/S03102601.Context-resolution-ordering.md
[intake-39]: ../suggestions/S03102601.Transaction-Lifecycle-State.md
[intake-40]: ../suggestions/S03102602.Context-ordering-spec.md
[intake-41]: ../suggestions/approved/S02222601.error-metadata.md
[intake-42]: ../suggestions/approved/S02222605.md
[intake-43]: ../suggestions/approved/S02222614.md
[intake-44]: ../suggestions/approved/S02242601.argument-wording-consistency.md
[intake-45]: ../suggestions/approved/S03121601.Ex.memo.md
[intake-46]: ../suggestions/planned/S02242601.allow-creating-constraints-with-type-key.md
[intake-47]: ../todo/T02182601.Separate-Handler-Execution-Flow-in-On.md
[intake-48]: ../todo/T02202602.Define-Non-Null-Handler-Action-Defaults.md

- [Restore coverage evidence][wrap-01]: proposal; V1-E01, rechecked at V1-E05.

[wrap-01]: ../suggestions/S10202601.Restore-coverage-evidence.md
