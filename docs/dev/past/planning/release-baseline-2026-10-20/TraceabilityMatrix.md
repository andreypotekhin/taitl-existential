# Release Traceability

Superseded before implementation on 2026-10-20; this is not completed release work. See [rebaseline][wrap-01].

Scope row numbers preserve the order and release assignment in [Roadmap.md](Roadmap.md). Topic specifications contain
stable requirement IDs; each sprint must add requirement-level acceptance links as its work is completed.
This assessment was made on 2026-10-20 against revision `a1c9abd` plus the documented infrastructure changes.

## Status and evidence

`verified` means the listed scope has matching passing specification evidence. `partial` means adjacent behavior
passes but requirements or cases remain. `unverified` means the full claim lacks direct evidence. `planned` means
the required evidence/interface is not established. `blocked` is used when a named prerequisite prevents progress.
Inherited `+` marks are preserved in topic specifications and are not automatic status promotions.
Evidence links identify existing tests, not promises that each listed file covers the whole scope row.

## V1

| Row | Roadmap scope | Spec | Epic | Sprint | Status | Evidence |
| --- | --- | --- | --- | --- | --- | --- |
| V1-01 | Library | [LC] | V1-E01 | [S01] | partial | [TL] |
| V1-02 | Library configuration | [LC] | V1-E01 | [S01] | verified | [TC] |
| V1-03 | Library usage | [TE] | V1-E04 | [S04] | partial | [TT] |
| V1-04 | Lifecycle phases | [TE] | V1-E03 | [S03] | partial | [TS] |
| V1-05 | Configure rules | [LC] | V1-E02 | [S02] | partial | [TX] |
| V1-06 | Configuration workflows | [LC] | V1-E02 | [S02] | partial | [TX] |
| V1-07 | Wildcard contexts | [LC] | V1-E02 | [S02] | partial | [TX] |
| V1-08 | Configure transactions | [TE] | V1-E02 | [S02] | partial | [TX] |
| V1-09 | Configure constraints | [LC] | V1-E02 | [S02] | partial | [TX] |
| V1-10 | Configure effects | [IE] | V1-E02 | [S02] | partial | [TI] |
| V1-11 | Configure intents | [IE] | V1-E03 | [S03] | partial | [TI] |
| V1-12 | Execution stages | [TE] | V1-E03 | [S03] | partial | [TS] |
| V1-13 | Evaluation workflow | [TE] | V1-E03 | [S03] | partial | [TS] |
| V1-14 | Stage-specific evaluations | [TE] | V1-E03 | [S03] | partial | [TS] |
| V1-15 | Intents | [IE] | V1-E03 | [S03] | partial | [TI] |
| V1-16 | Indexes | [QI] | V1-E03 | [S03] | partial | [TIDX] |
| V1-17 | Internal indexing | [QI] | V1-E03 | [S03] | partial | [TR] |
| V1-18 | Transaction lifecycle | [TE] | V1-E04 | [S04] | partial | [TT] |
| V1-19 | Public classes | [LC] | V1-E01 | [S01] | partial | [TL] |

## V2

| Row | Roadmap scope | Spec | Epic | Sprint | Status | Evidence |
| --- | --- | --- | --- | --- | --- | --- |
| V2-01 | Events | [EC] | V2-E01 | [S06] | partial | [TEV] |
| V2-02 | Single-entity constraints | [EC] | V2-E01 | [S06] | partial | [TQ] |
| V2-03 | Multi-entity constraints | [EC] | V2-E02 | [S07] | partial | [TQ] |
| V2-04 | Constraints on entity evolution | [EC] | V2-E01 | [S06] | partial | [TM] |
| V2-05 | Multi-entity evolution | [EC] | V2-E01 | [S06] | unverified | [TEV] |
| V2-06 | Constraints on subclasses | [EC] | V2-E01 | [S06] | unverified | [TEV] |
| V2-07 | Constraint violation reporting | [EC] | V2-E04 | [S09] | partial | [TD] |
| V2-08 | Universal Quantifier | [QI] | V2-E02 | [S07] | partial | [TQ] |
| V2-09 | Existence Quantifier | [QI] | V2-E02 | [S07] | partial | [TQ] |
| V2-10 | Effects | [IE] | V2-E03 | [S08] | partial | [TI] |
| V2-11 | Housekeeping | [HK] | V2-E04 | [S09] | partial | [TH] |
| V2-12 | Transaction Cleanup | [HK] | V2-E04 | [S09] | partial | [TH] |
| V2-13 | Global Cleanup | [HK] | V2-E04 | [S09] | unverified | [TH] |
| V2-14 | Constraint enforcement | [EC] | V2-E01 | [S06] | partial | [TQ] |
| V2-15 | Side effect execution | [IE] | V2-E03 | [S08] | partial | [TI] |
| V2-16 | Custom Event Types | [EC] | V2-E03 | [S08] | planned | Missing |
| V2-17 | Maven dependency | [DIST] | V2-E05 | [S10] | planned | Missing |

## Prerequisites and verification gaps

V1 already uses events, basic constraints/effects, exception handling, and transaction disposal. Retain and test those
prerequisites in V1; their comprehensive V2 scope remains assigned to V2. V1 index support precedes V2 composed
quantifier/relationship completion. A failing prerequisite blocks the affected V1 claim and cannot be hidden by its
V2 feature assignment.

Sprint 03 owns the three disabled validation-report cases; Sprint 05 verifies their closure and actual coverage
collection. Cross-wildcard ordering, deduplication identity, failure recovery, and concurrency require design
decisions before related implementation. Maven Central availability and global leak reporting remain unverified.

[LC]: ../../../specifications/LibraryAndConfiguration.spec.md
[TE]: ../../../specifications/TransactionsAndEvaluation.spec.md
[EC]: ../../../specifications/EventsAndConstraints.spec.md
[QI]: ../../../specifications/QuantifiersAndIndexes.spec.md
[IE]: ../../../specifications/IntentsAndEffects.spec.md
[HK]: ../../../specifications/Housekeeping.spec.md
[DIST]: ../../../specifications/Distribution.spec.md
[S01]: sprints/Sprint01_V1LibraryAccessAndConfiguration.md
[S02]: sprints/Sprint02_V1ContextsAndRuleConfiguration.md
[S03]: sprints/Sprint03_V1EvaluationIntentsAndIndexes.md
[S04]: sprints/Sprint04_V1TransactionLifecycle.md
[S05]: sprints/Sprint05_V1HardeningAndCloseout.md
[S06]: sprints/Sprint06_V2EventsAndEntityConstraints.md
[S07]: sprints/Sprint07_V2QuantifiersAndRelationships.md
[S08]: sprints/Sprint08_V2EffectsAndCustomEvents.md
[S09]: sprints/Sprint09_V2ReportingAndHousekeeping.md
[S10]: sprints/Sprint10_V2MavenConsumptionAndDistribution.md
[S11]: sprints/Sprint11_V2HardeningAndCloseout.md
[TL]: ../../../../../src/test/java/com/taitl/specs/library/UserCanAccessLibrary.java
[TC]: ../../../../../src/test/java/com/taitl/specs/library_configuration/UserCanConfigureLibrary.java
[TT]: ../../../../../src/test/java/com/taitl/specs/library_usage/UserCanInitiateTransactions.java
[TX]: ../../../../../src/test/java/com/taitl/specs/configuration_workflow
[TS]: ../../../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureEntities.java
[TI]: ../../../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureIntents.java
[TQ]: ../../../../../src/test/java/com/taitl/existential/quantifiers
[TR]: ../../../../../src/test/java/com/taitl/ex/core/transactions/TransactionIndexesTest.java
[TEV]: ../../../../../src/test/java/com/taitl/specs/library_usage/UserCanSendEntityEvents.java
[TM]: ../../../../../src/test/java/com/taitl/specs/library_usage/UserCanUseMemoForBiEvents.java
[TD]: ../../../../../src/test/java/com/taitl/existential/exceptions
[TH]: ../../../../../src/test/java/com/taitl/ex/logic/transactions/data/TrRegistryTest.java

[TIDX]: ../../../../../src/test/java/com/taitl/exlogic/unused/indexes

[wrap-01]: ../../../project/ReleaseRebaseline.md
