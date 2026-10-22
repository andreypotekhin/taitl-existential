# Backlog

The [roadmap](Roadmap.md) now plans V1 representative core, V2 broader standard usage, and V3 extensibility. Robustness
and performance are priorities in every release. The [rebaseline](ReleaseRebaseline.md) maps the original scope/epics to
the current plan; archived charters were superseded before implementation, not completed.
[Traceability](TraceabilityMatrix.md) records capability scope, design readiness, and verification independently.

## Completed planning foundation

+ Initial documentation-first foundation established; see the [assessment](Assessment.md).
+ Three-release workflow planning established; Sprint 01/02 evidence is recorded in their reviews.

## V1

### Night City Core Workflow

Status: complete for the selected Sprint 01 slice; V1 closeout is complete in Sprint 05. Delivery:
[Sprint 01](sprints/Sprint01_V1NightCityCoreWorkflow.md).

Follow a documented Night City operation from rule configuration through events to successful or rejected commit.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

### Relationships and Evolution

Status: complete for the selected V1 caller-owned relationship/evolution scope. Delivery:
[Sprint 02](sprints/Sprint02_V1RelationshipsAndEvolution.md). Broader maintained-index/type behavior remains in V2.

Validate a representative relationship with basic All/Exists and reject an invalid before/after entity change.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

### Failure Recovery and Resource Hygiene

Status: complete. Delivery: [Sprint 03](sprints/Sprint03_V1FailureRecoveryAndResourceHygiene.md).

Recover predictably from rejected or failed operations and release the representative workflow resources.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

### Night City Artifact Consumption and Performance

Status: complete. Delivery: [Sprint 04](sprints/Sprint04_V1NightCityArtifactAndPerformance.md).

Build and exercise Night City as a Maven project consuming the installed Existential artifact.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

### V1 Hardening and Closeout

Status: complete. Delivery: [Sprint 05](sprints/Sprint05_V1HardeningAndCloseout.md).

Close V1 with a robust, documented, independently consumed representative workflow and performance evidence.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

## V2

### Instance Isolation and Configuration

Status: planned. Delivery: [Sprint 06](sprints/Sprint06_V2InstanceIsolationAndConfiguration.md).

Use independent instances and the broader documented configuration options without unintended shared state.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

### Contexts and Type Matching

Status: planned. Delivery: [Sprint 07](sprints/Sprint07_V2ContextsAndTypeMatching.md).

Apply parent/wildcard rules and broader generic/subclass matching with deterministic documented semantics.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

### Stages, Intents, and Transaction Customization

Status: planned. Delivery: [Sprint 08](sprints/Sprint08_V2StagesIntentsAndCustomTransactions.md).

Use built-in execution stages, intents/effects, and customized transactions with predictable timing and failures.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

### Indexes and Operational Coverage

Status: planned. Delivery: [Sprint 09](sprints/Sprint09_V2IndexesAndOperationalCoverage.md).

Use maintained relationships and broader lifecycle/reporting/resource behavior with measured scaling.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

### V2 Hardening and Closeout

Status: planned. Delivery: [Sprint 10](sprints/Sprint10_V2HardeningAndCloseout.md).

Close broader standard usage while retaining the V1 workflow, consumption, and quality evidence.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

## V3

### Custom Event Contracts and Registration

Status: planned. Delivery: [Sprint 11](sprints/Sprint11_V3CustomEventContractsAndRegistration.md).

Declare and register custom events with clear identity, matching, ownership, and invalid-registration behavior.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

### Custom Event Evaluation and Extensions

Status: planned. Delivery: [Sprint 12](sprints/Sprint12_V3CustomEventEvaluationAndExtensions.md).

Run admitted custom events through transaction evaluation, splitting, intents, and resource management.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

### V3 Hardening and Closeout

Status: planned. Delivery: [Sprint 13](sprints/Sprint13_V3HardeningAndCloseout.md).

Close extensibility with robust custom-event behavior, bounded costs, and retained standard-library evidence.

The sprint defines requirements, dependencies, quality obligations, and acceptance. Retain existing functionality and
regressions; the new scope changes delivery/evidence priorities, not availability of code already present.

## Existing suggestion and TODO intake

The following records retain their existing approval state. Epic assignment is triage, not approval of a proposed API or
algorithm. Related proposals are grouped by epic; resolve duplicate/contradictory proposals in design first. Several
action IDs collide, so links use full filenames. Do not renumber historical records.

- [Log redaction]: proposal; Failure Recovery and Resource Hygiene; design/evidence review required.
- [Validation Report + Trace Surface]: proposal; Indexes and Operational Coverage; design/evidence review required.
- [Context Matching Strategy Registry]: proposal; Contexts and Type Matching; design/evidence review required.
- [Fail-Fast Stubs for Unimplemented Actions]: proposal; V1 Hardening and Closeout; design/evidence review required.
- [Transaction Membership Index]: proposal; Night City Artifact Consumption and Performance; design/evidence review
  required.
- [Event Queue Deque Backing]: proposal; Night City Artifact Consumption and Performance; design/evidence review
  required.
- [Immutable Rule Snapshot + Hot Reload]: proposal; No release assignment (optional hot reload); design/evidence review
  required.
- [Publish License and Source Availability]: proposal; Night City Artifact Consumption and Performance; review current
  evidence before archive.
- [Encapsulate Handler State]: proposal; Stages, Intents, and Transaction Customization; design/evidence review
  required.
- [Stable Rule Identity + Digest]: proposal; Indexes and Operational Coverage; design/evidence review required.
- [Add Security Static Analysis]: proposal; V1 Hardening and Closeout; design/evidence review required.
- [Prefer HashMap for TransactionIndexes]: proposal; Night City Artifact Consumption and Performance; design/evidence
  review required.
- [Dependency Vulnerability Scans]: proposal; V1 Hardening and Closeout; design/evidence review required.
- [Public Lifecycle Status Dashboard]: proposal; No release assignment (optional dashboard); design/evidence review
  required.
- [Enforce Secure Config File Permissions]: proposal; Instance Isolation and Configuration; design/evidence review
  required.
- [Context Path Index (Trie) for Matching]: proposal; Contexts and Type Matching; design/evidence review required.
- [Specify context matching precedence and event identity]: proposal; Night City Core Workflow / Contexts and Type
  Matching; design/evidence review required.
- [Document thread-safety and locking model]: proposal; Night City Core Workflow / Instance Isolation and Configuration;
  design/evidence review required.
- [Introduce immutable configuration snapshots]: proposal; Instance Isolation and Configuration; design/evidence review
  required.
- [Define event identity and de-dup semantics in specification]: proposal; Night City Core Workflow; design/evidence
  review required.
- [Document concurrency guarantees for configuration and injection]: proposal; Instance Isolation and Configuration;
  design/evidence review required.
- [LimitedInputStream boundary validation]: proposal; Instance Isolation and Configuration; design/evidence review
  required.
- [Cross-platform secure config permission checks]: proposal; Instance Isolation and Configuration; design/evidence
  review required.
- [Bi-handler constraint consistency]: proposal; Relationships and Evolution; design/evidence review required.
- [Troubleshooting links helper]: proposal; Failure Recovery and Resource Hygiene; design/evidence review required.
- [Unify port handler logic]: proposal; Relationships and Evolution; design/evidence review required.
- [Specify intent semantics and access decision flow]: proposal; Stages, Intents, and Transaction Customization;
  design/evidence review required.
- [Configuration integrity verification]: proposal; Instance Isolation and Configuration; design/evidence review
  required.
- [Event Input Validation Troubleshooting Links]: proposal; Failure Recovery and Resource Hygiene; design/evidence
  review required.
- [Context identity index in ConfigBuilder]: proposal; Contexts and Type Matching; design/evidence review required.
- [Event handler hierarchy matching]: proposal; Contexts and Type Matching; design/evidence review required.
- [Getting Started Guide]: proposal; Night City Core Workflow; design/evidence review required.
- [Specify custom event registration and semantics]: proposal; Custom Event Contracts and Registration; design/evidence
  review required.
- [Avoid merge-conflict artifacts]: proposal; V1 Hardening and Closeout; design/evidence review required.
- [Split-event validation consistency]: proposal; Relationships and Evolution; design/evidence review required.
- [Transaction helper for safe commit/rollback]: proposal; Failure Recovery and Resource Hygiene; design/evidence review
  required.
- [Centralize argument validation messages]: proposal; Failure Recovery and Resource Hygiene; design/evidence review
  required.
- [Context resolution and ordering pipeline]: proposal; Contexts and Type Matching; design/evidence review required.
- [Transaction lifecycle state]: proposal; Failure Recovery and Resource Hygiene; design/evidence review required.
- [Specify context matching and rule ordering semantics]: proposal; Contexts and Type Matching; design/evidence review
  required.
- [Error metadata for public exceptions]: approved; Indexes and Operational Coverage; design/evidence review required.
- [Key Naming Strategy Injection]: approved; Contexts and Type Matching; design/evidence review required.
- [Child context builder support]: approved; Contexts and Type Matching; design/evidence review required.
- [Argument wording consistency]: approved; Failure Recovery and Resource Hygiene; design/evidence review required.
- [Ex.memo]: approved; Stages, Intents, and Transaction Customization; review current evidence before archive.
- [Allow creating constraints with type key]: planned record; Contexts and Type Matching; review current evidence before
  archive.
- [Separate Handler Execution Flow in On]: TODO intake; Stages, Intents, and Transaction Customization; design/evidence
  review required.
- [Define Non-Null Handler Action Defaults]: TODO intake; Night City Core Workflow; design/evidence review required.

Completed plans and historical suggestions remain in [past](../past/); do not treat their old timings or claims as
current release evidence. New optional enhancements require their usual suggestion review.

[Log redaction]: ../suggestions/S02182601.Log-redaction.md
[Validation Report + Trace Surface]: ../suggestions/S02182602.Validation-Report-Trace-Surface.md
[Context Matching Strategy Registry]: ../suggestions/S02182603.Context-Matching-Strategy-Registry.md
[Fail-Fast Stubs for Unimplemented Actions]: ../suggestions/S02182604.Fail-Fast-Stubs-for-Unimplemented-Actions.md
[Transaction Membership Index]: ../suggestions/S02182605.Transaction-Membership-Index.md
[Event Queue Deque Backing]: ../suggestions/S02182607.Event-Queue-Deque-Backing.md
[Immutable Rule Snapshot + Hot Reload]: ../suggestions/S02182610.Immutable-Rule-Snapshot-Hot-Reload.md
[Publish License and Source Availability]: ../suggestions/S02192604.Publish-License-and-Source-Availability.md
[Encapsulate Handler State]: ../suggestions/S02202601.Encapsulate-Handler-State.md
[Stable Rule Identity + Digest]: ../suggestions/S02202602.Stable-Rule-Identity-Digest.md
[Add Security Static Analysis]: ../suggestions/S02202603.Add-Security-Static-Analysis.md
[Prefer HashMap for TransactionIndexes]: ../suggestions/S02202604.Prefer-HashMap-for-TransactionIndexes.md
[Dependency Vulnerability Scans]: ../suggestions/S02202605.Dependency-Vulnerability-Scans.md
[Public Lifecycle Status Dashboard]: ../suggestions/S02212601.Public-Lifecycle-Status-Dashboard.md
[Enforce Secure Config File Permissions]: ../suggestions/S02212602.Enforce-Secure-Config-File-Permissions.md
[Context Path Index (Trie) for Matching]: ../suggestions/S02212603.Context-Path-Index-Trie-for-Matching.md
[Specify context matching precedence and event identity]: ../suggestions/S02222602.md
[Document thread-safety and locking model]: ../suggestions/S02222603.md
[Introduce immutable configuration snapshots]: ../suggestions/S02222612.Config-snapshot.md
[Define event identity and de-dup semantics in specification]: ../suggestions/S02222613.md
[Document concurrency guarantees for configuration and injection]: ../suggestions/S02222615.md
[LimitedInputStream boundary validation]: ../suggestions/S02222616.md
[Cross-platform secure config permission checks]: ../suggestions/S02222617.md
[Bi-handler constraint consistency]: ../suggestions/S02242601.Bi-handler-consistency.md
[Troubleshooting links helper]: ../suggestions/S02242601.Troubleshooting-links-helper.md
[Unify port handler logic]: ../suggestions/S02242601.Unify-Transit-Handler-Logic.md
[Specify intent semantics and access decision flow]: ../suggestions/S02242602.md
[Configuration integrity verification]: ../suggestions/S02252601.Config-integrity-verification.md
[Event Input Validation Troubleshooting Links]: ../suggestions/S02272601.Event-Input-Validation-Troubleshooting.md
[Context identity index in ConfigBuilder]: ../suggestions/S02282601.Context-identity-index.md
[Event handler hierarchy matching]: ../suggestions/S02282601.EventHandlerHierarchyMatching.md
[Getting Started Guide]: ../suggestions/S02282601.Getting-Started-Guide.md
[Specify custom event registration and semantics]: ../suggestions/S02282602.Custom-event-spec.md
[Avoid merge-conflict artifacts]: ../suggestions/S03012601.Avoid-Merge-Conflict-Artifacts.md
[Split-event validation consistency]: ../suggestions/S03012601.Split-event-validation-consistency.md
[Transaction helper for safe commit/rollback]: ../suggestions/S03012601.Transaction-helpers-for-commit-rollback.md
[Centralize argument validation messages]: ../suggestions/S03102601.Centralize-argument-validation-messages.md
[Context resolution and ordering pipeline]: ../suggestions/S03102601.Context-resolution-ordering.md
[Transaction lifecycle state]: ../suggestions/S03102601.Transaction-Lifecycle-State.md
[Specify context matching and rule ordering semantics]: ../suggestions/S03102602.Context-ordering-spec.md
[Error metadata for public exceptions]: ../suggestions/approved/S02222601.error-metadata.md
[Key Naming Strategy Injection]: ../suggestions/approved/S02222605.md
[Child context builder support]: ../suggestions/approved/S02222614.md
[Argument wording consistency]: ../suggestions/approved/S02242601.argument-wording-consistency.md
[Ex.memo]: ../suggestions/approved/S03121601.Ex.memo.md
[Allow creating constraints with type key]: ../suggestions/planned/S02242601.allow-creating-constraints-with-type-key.md
[Separate Handler Execution Flow in On]: ../todo/T02182601.Separate-Handler-Execution-Flow-in-On.md
[Define Non-Null Handler Action Defaults]: ../todo/T02202602.Define-Non-Null-Handler-Action-Defaults.md

- [Restore coverage evidence]: proposal; Night City Core Workflow, rechecked at V1 Hardening and Closeout.

[Restore coverage evidence]: ../suggestions/S10202601.Restore-coverage-evidence.md
