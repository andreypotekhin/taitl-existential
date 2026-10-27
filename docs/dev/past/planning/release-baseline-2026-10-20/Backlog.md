# Backlog

Superseded before implementation on 2026-10-20; this is not completed release work. See [rebaseline][wrap-01].

Release scope is set by the [roadmap](Roadmap.md). The [traceability matrix](TraceabilityMatrix.md) maps every scope
item to its specification, evidence/gap, epic, and sprint. Sprints are ordered and undated; their charters describe
remaining work, including verification of existing implementation. No new library feature is approved by this inventory.

## Completed planning foundation

+ PM-00: assess current state, establish topic designs/specifications, agent process, release plans, and sprint
charters.
This completes the former "Assess Current State" planning item, not any release feature or hardening criterion.
See [Assessment][link-01] and the [completed task plan][link-02].

## V1

<a id="v1-e01"></a>

### V1-E01: Library Access and Configuration

Status: planned. Delivery: [Sprint 01](sprints/Sprint01_V1LibraryAccessAndConfiguration.md).

Configure independent library instances and use predictable configuration sources without cross-instance state.

Governing topic: [LibraryAndConfiguration](../../../specifications/LibraryAndConfiguration.spec.md).
The sprint defines concrete remaining work, dependencies, and acceptance. Preserve existing passing behavior while
closing the documented gaps; do not reimplement a feature merely because its claim lacks a completion mark.

<a id="v1-e02"></a>

### V1-E02: Contexts and Rule Configuration

Status: planned. Delivery: [Sprint 02](sprints/Sprint02_V1ContextsAndRuleConfiguration.md).

Configure parent, wildcard, generic, and custom-transaction rules with deterministic resolution and ordering.

Governing topic: [LibraryAndConfiguration](../../../specifications/LibraryAndConfiguration.spec.md).
The sprint defines concrete remaining work, dependencies, and acceptance. Preserve existing passing behavior while
closing the documented gaps; do not reimplement a feature merely because its claim lacks a completion mark.

<a id="v1-e03"></a>

### V1-E03: Evaluation, Intents, and Indexes

Status: planned. Delivery: [Sprint 03](sprints/Sprint03_V1EvaluationIntentsAndIndexes.md).

Evaluate applicable rules at documented stages while maintaining predictable intent checks and event indexes.

Governing topic: [TransactionsAndEvaluation](../../../specifications/TransactionsAndEvaluation.spec.md).
The sprint defines concrete remaining work, dependencies, and acceptance. Preserve existing passing behavior while
closing the documented gaps; do not reimplement a feature merely because its claim lacks a completion mark.

<a id="v1-e04"></a>

### V1-E04: Transaction Lifecycle

Status: planned. Delivery: [Sprint 04](sprints/Sprint04_V1TransactionLifecycle.md).

Begin, checkpoint, commit, and roll back independent transactions with defined invalid-operation behavior.

Governing topic: [TransactionsAndEvaluation](../../../specifications/TransactionsAndEvaluation.spec.md).
The sprint defines concrete remaining work, dependencies, and acceptance. Preserve existing passing behavior while
closing the documented gaps; do not reimplement a feature merely because its claim lacks a completion mark.

<a id="v1-e05"></a>

### V1-E05: Hardening and Closeout

Status: planned. Delivery: [Sprint 05](sprints/Sprint05_V1HardeningAndCloseout.md).

Make every V1 release claim reproducible and reconcile documentation with actual release evidence.

Governing topic: [TransactionsAndEvaluation](../../../specifications/TransactionsAndEvaluation.spec.md).
The sprint defines concrete remaining work, dependencies, and acceptance. Preserve existing passing behavior while
closing the documented gaps; do not reimplement a feature merely because its claim lacks a completion mark.

## V2

<a id="v2-e01"></a>

### V2-E01: Events and Entity Constraints

Status: planned. Delivery: [Sprint 06](sprints/Sprint06_V2EventsAndEntityConstraints.md).

Express and enforce entity, evolution, cross-entity, and subclass constraints through documented events.

Governing topic: [EventsAndConstraints](../../../specifications/EventsAndConstraints.spec.md).
The sprint defines concrete remaining work, dependencies, and acceptance. Preserve existing passing behavior while
closing the documented gaps; do not reimplement a feature merely because its claim lacks a completion mark.

<a id="v2-e02"></a>

### V2-E02: Quantifiers and Relationships

Status: planned. Delivery: [Sprint 07](sprints/Sprint07_V2QuantifiersAndRelationships.md).

Use All/Exists, collections, and maintained indexes to express relationships between in-memory entities.

Governing topic: [QuantifiersAndIndexes](../../../specifications/QuantifiersAndIndexes.spec.md).
The sprint defines concrete remaining work, dependencies, and acceptance. Preserve existing passing behavior while
closing the documented gaps; do not reimplement a feature merely because its claim lacks a completion mark.

<a id="v2-e03"></a>

### V2-E03: Effects and Custom Events

Status: planned. Delivery: [Sprint 08](sprints/Sprint08_V2EffectsAndCustomEvents.md).

Attach effects and custom events with explicit timing, ordering, intent interactions, and failure behavior.

Governing topic: [IntentsAndEffects](../../../specifications/IntentsAndEffects.spec.md).
The sprint defines concrete remaining work, dependencies, and acceptance. Preserve existing passing behavior while
closing the documented gaps; do not reimplement a feature merely because its claim lacks a completion mark.

<a id="v2-e04"></a>

### V2-E04: Reporting and Housekeeping

Status: planned. Delivery: [Sprint 09](sprints/Sprint09_V2ReportingAndHousekeeping.md).

Receive useful violation details and release transaction/global resources under documented ownership rules.

Governing topic: [Housekeeping](../../../specifications/Housekeeping.spec.md).
The sprint defines concrete remaining work, dependencies, and acceptance. Preserve existing passing behavior while
closing the documented gaps; do not reimplement a feature merely because its claim lacks a completion mark.

<a id="v2-e05"></a>

### V2-E05: Maven Consumption and Distribution

Status: planned. Delivery: [Sprint 10](sprints/Sprint10_V2MavenConsumptionAndDistribution.md).

Consume the correct artifact from a separate Maven project using documented coordinates and prerequisites.

Governing topic: [Distribution](../../../specifications/Distribution.spec.md).
The sprint defines concrete remaining work, dependencies, and acceptance. Preserve existing passing behavior while
closing the documented gaps; do not reimplement a feature merely because its claim lacks a completion mark.

<a id="v2-e06"></a>

### V2-E06: Hardening and Closeout

Status: planned. Delivery: [Sprint 11](sprints/Sprint11_V2HardeningAndCloseout.md).

Close V2 with reproducible feature, retained-V1, resource, performance, and independent-consumer evidence.

Governing topic: [Distribution](../../../specifications/Distribution.spec.md).
The sprint defines concrete remaining work, dependencies, and acceptance. Preserve existing passing behavior while
closing the documented gaps; do not reimplement a feature merely because its claim lacks a completion mark.

## Existing suggestion and TODO intake

The following records retain their existing approval state. Epic assignment is triage, not approval of a proposed
API or algorithm. Related proposals are grouped by epic; resolve duplicate/contradictory proposals in design first.
Several action IDs collide, so links use full filenames. Do not renumber historical records.

- [S02182601 Log redaction][intake-01]: proposal; V2-E04; design/evidence review required.
- [S02182602 Validation Report + Trace Surface][intake-02]: proposal; V2-E04; design/evidence review required.
- [-S02182603 Context Matching Strategy Registry][intake-03]: proposal; V1-E02; design/evidence review required.
- [-S02182604 Fail-Fast Stubs for Unimplemented Actions][intake-04]: proposal; V1-E05; design/evidence review required.
- [S02182605 Transaction Membership Index][intake-05]: proposal; V1-E03; design/evidence review required.
- [S02182607 Event Queue Deque Backing][intake-06]: proposal; V1-E03; design/evidence review required.
- [S02182610 Immutable Rule Snapshot + Hot Reload][intake-07]: proposal; V1-E05; design/evidence review required.
- [+S02192604 Publish License and Source Availability][intake-08]: proposal; V2-E05; review current evidence before
  archive.
- [S02202601 Encapsulate Handler State][intake-09]: proposal; V1-E03; design/evidence review required.
- [S02202602 Stable Rule Identity + Digest][intake-10]: proposal; V1-E03; design/evidence review required.
- [S02202603 Add Security Static Analysis][intake-11]: proposal; V2-E05; design/evidence review required.
- [S02202604 Prefer HashMap for TransactionIndexes][intake-12]: proposal; V1-E03; design/evidence review required.
- [S02202605 Dependency Vulnerability Scans][intake-13]: proposal; V2-E05; design/evidence review required.
- [S02212601 Public Lifecycle Status Dashboard][intake-14]: proposal; V1-E04; design/evidence review required.
- [S02212602 Enforce Secure Config File Permissions][intake-15]: proposal; V1-E01; design/evidence review required.
- [S02212603 Context Path Index (Trie) for Matching][intake-16]: proposal; V1-E02; design/evidence review required.
- [S02222602 Specify context matching precedence and event identity][intake-17]: proposal; V1-E05; design/evidence
  review required.
- [S02222603 Document thread-safety and locking model][intake-18]: proposal; V1-E05; design/evidence review required.
- [S02222612 Introduce immutable configuration snapshots][intake-19]: proposal; V1-E03; design/evidence review required.
- [S02222613 Define event identity and de-dup semantics in specification][intake-20]: proposal; V1-E05; design/evidence
  review required.
- [S02222615 Document concurrency guarantees for configuration and injection][intake-21]: proposal; V1-E05;
  design/evidence review required.
- [S02222616 LimitedInputStream boundary validation][intake-22]: proposal; V1-E05; design/evidence review required.
- [S02222617 Cross-platform secure config permission checks][intake-23]: proposal; V1-E05; design/evidence review
  required.
- [S02242601 Bi-handler constraint consistency][intake-24]: proposal; V2-E01; design/evidence review required.
- [S02242601 Troubleshooting links helper][intake-25]: proposal; V2-E04; design/evidence review required.
- [S02242601 Unify port handler logic][intake-26]: proposal; V2-E03; design/evidence review required.
- [S02242602 Specify intent semantics and access decision flow][intake-27]: proposal; V1-E05; design/evidence review
  required.
- [S02252601 Configuration integrity verification][intake-28]: proposal; V1-E01; design/evidence review required.
- [S02272601 Event Input Validation Troubleshooting Links][intake-29]: proposal; V2-E04; design/evidence review
  required.
- [S02282601 Context identity index in ConfigBuilder][intake-30]: proposal; V1-E02; design/evidence review required.
- [S02282601 Event handler hierarchy matching][intake-31]: proposal; V2-E01; design/evidence review required.
- [S02282601 Getting Started Guide][intake-32]: proposal; V1-E05; design/evidence review required.
- [S02282602 Specify custom event registration and semantics][intake-33]: proposal; V2-E03; design/evidence review
  required.
- [S03012601 Avoid merge-conflict artifacts][intake-34]: proposal; V1-E05; design/evidence review required.
- [S03012601 Split-event validation consistency][intake-35]: proposal; V2-E01; design/evidence review required.
- [S03012601 Transaction helper for safe commit/rollback][intake-36]: proposal; V1-E04; design/evidence review required.
- [S03102601 Centralize argument validation messages][intake-37]: proposal; V2-E04; design/evidence review required.
- [S03102601 Context resolution and ordering pipeline][intake-38]: proposal; V1-E02; design/evidence review required.
- [S03102601 Transaction lifecycle state][intake-39]: proposal; V1-E04; design/evidence review required.
- [S03102602 Specify context matching and rule ordering semantics][intake-40]: proposal; V1-E02; design/evidence review
  required.
- [S02222601 Error metadata for public exceptions][intake-41]: approved; V2-E04; design/evidence review required.
- [S02222605 Key Naming Strategy Injection][intake-42]: approved; V1-E05; design/evidence review required.
- [S02222614 Child context builder support][intake-43]: approved; V1-E05; design/evidence review required.
- [S02242601 Argument wording consistency][intake-44]: approved; V2-E04; design/evidence review required.
- [S03121601 Ex.memo][intake-45]: approved; V1-E04; review current evidence before archive.
- [S02242601 Allow creating constraints with type key][intake-46]: planned record; V1-E02; review current evidence
  before archive.
- [T02182601 Separate Handler Execution Flow in On][intake-47]: TODO intake; V1-E03; design/evidence review required.
- [T02202602 Define Non-Null Handler Action Defaults][intake-48]: TODO intake; V1-E03; design/evidence review required.

Completed plans and historical suggestions remain in [past](../..); do not treat their old timings or claims
as current release evidence. New optional enhancements require their usual suggestion review.

[intake-01]: ../../../suggestions/S02182601.Log-redaction.md
[intake-02]: ../../../suggestions/S02182602.Validation-Report-Trace-Surface.md
[intake-03]: ../../../suggestions/S02182603.Context-Matching-Strategy-Registry.md
[intake-04]: ../../../suggestions/S02182604.Fail-Fast-Stubs-for-Unimplemented-Actions.md
[intake-05]: ../../../suggestions/S02182605.Transaction-Membership-Index.md
[intake-06]: ../../../suggestions/S02182607.Event-Queue-Deque-Backing.md
[intake-07]: ../../../suggestions/S02182610.Immutable-Rule-Snapshot-Hot-Reload.md
[intake-08]: ../../../suggestions/S02192604.Publish-License-and-Source-Availability.md
[intake-09]: ../../../suggestions/S02202601.Encapsulate-Handler-State.md
[intake-10]: ../../../suggestions/S02202602.Stable-Rule-Identity-Digest.md
[intake-11]: ../../../suggestions/S02202603.Add-Security-Static-Analysis.md
[intake-12]: ../../../suggestions/S02202604.Prefer-HashMap-for-TransactionIndexes.md
[intake-13]: ../../../suggestions/S02202605.Dependency-Vulnerability-Scans.md
[intake-14]: ../../../suggestions/S02212601.Public-Lifecycle-Status-Dashboard.md
[intake-15]: ../../../suggestions/S02212602.Enforce-Secure-Config-File-Permissions.md
[intake-16]: ../../../suggestions/S02212603.Context-Path-Index-Trie-for-Matching.md
[intake-17]: ../../../suggestions/S02222602.md
[intake-18]: ../../../suggestions/S02222603.md
[intake-19]: ../../../suggestions/S02222612.Config-snapshot.md
[intake-20]: ../../../suggestions/S02222613.md
[intake-21]: ../../../suggestions/S02222615.md
[intake-22]: ../../../suggestions/S02222616.md
[intake-23]: ../../../suggestions/S02222617.md
[intake-24]: ../../../suggestions/S02242601.Bi-handler-consistency.md
[intake-25]: ../../../suggestions/S02242601.Troubleshooting-links-helper.md
[intake-26]: ../../../suggestions/S02242601.Unify-Transit-Handler-Logic.md
[intake-27]: ../../../suggestions/S02242602.md
[intake-28]: ../../../suggestions/S02252601.Config-integrity-verification.md
[intake-29]: ../../../suggestions/S02272601.Event-Input-Validation-Troubleshooting.md
[intake-30]: ../../../suggestions/S02282601.Context-identity-index.md
[intake-31]: ../../../suggestions/S02282601.EventHandlerHierarchyMatching.md
[intake-32]: ../../../suggestions/S02282601.Getting-Started-Guide.md
[intake-33]: ../../../suggestions/S02282602.Custom-event-spec.md
[intake-34]: ../../../suggestions/S03012601.Avoid-Merge-Conflict-Artifacts.md
[intake-35]: ../../../suggestions/S03012601.Split-event-validation-consistency.md
[intake-36]: ../../../suggestions/S03012601.Transaction-helpers-for-commit-rollback.md
[intake-37]: ../../../suggestions/S03102601.Centralize-argument-validation-messages.md
[intake-38]: ../../../suggestions/S03102601.Context-resolution-ordering.md
[intake-39]: ../../../suggestions/S03102601.Transaction-Lifecycle-State.md
[intake-40]: ../../../suggestions/S03102602.Context-ordering-spec.md
[intake-41]: ../../suggestions/S02222601.error-metadata.md
[intake-42]: ../../../suggestions/approved/S02222605.md
[intake-43]: ../../../suggestions/approved/S02222614.md
[intake-44]: ../../../suggestions/approved/S02242601.argument-wording-consistency.md
[intake-45]: ../../../suggestions/approved/S03121601.Ex.memo.md
[intake-46]: ../../../suggestions/planned/S02242601.allow-creating-constraints-with-type-key.md
[intake-47]: ../../../todo/T02182601.Separate-Handler-Execution-Flow-in-On.md
[intake-48]: ../../../todo/T02202602.Define-Non-Null-Handler-Action-Defaults.md

- [Restore coverage evidence](../../../suggestions/S10202601.Restore-coverage-evidence.md): proposal; V1-E05.

[link-01]: Assessment.md
[link-02]: ../P10202601.Documentation-first-development.plan.md

[wrap-01]: ../../../project/ReleaseRebaseline.md
