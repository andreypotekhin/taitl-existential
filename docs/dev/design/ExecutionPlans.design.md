# Runtime Execution Plans Design

## Purpose

An application may report 10,000–100,000 entities in one transaction, using 20–200 distinct class/generic type
descriptors and 100–500 configured rules. Reporting validation-only events should primarily record and fold events;
type matching and validation belong at the transaction boundary. Repeated entities share matching work without
sharing predicate outcomes.

An `ExecutionPlan` is an internal compiled description of applicable rule sources and required execution work. It is
distinct from a delivery execution plan in `planning/`. Observable requirements are in
[ExecutionPlans.spec.md](../specifications/ExecutionPlans.spec.md). Context ordering belongs to
[LibraryAndConfiguration.spec.md](../specifications/LibraryAndConfiguration.spec.md#deterministic-context-resolution),
and type compatibility belongs to
[EventsAndConstraints.spec.md](../specifications/EventsAndConstraints.spec.md#entity-type-matching).

## Concepts and responsibilities

`ExecutionPlan` is an immutable stage-activity profile. Operation/stage indexes also own a compiled `RuleInventory`:
ordered declaration references, compact IDs, and raw-subject-type pattern buckets. `RulePattern` groups declarations
with the same event/type pattern without merging their identities. Plans and inventories contain configuration
references, never reported entities, transaction memo values, or validation results. Registering a callback twice
creates two declaration IDs.

`ExecutionPlanLogic`, `CompileExecutionPlan`, and `BindExecutionPlan` select report-time work after transaction
preparation. `CompileRuleInventory` compiles indexed declarations. `ResolveSelectionInput` resolves event shape and
per-report prerequisites on a miss; `ResolveReportedTypes` recovers structural descriptors. `ResolveRuleSelection`
retrieves relevant pattern buckets, delegates compatibility to `TypeMatchingLogic`/`MatchTypePattern`, and uses
`MergeRuleMembership` to produce the ordered selection. `EventField` owns publication and caching. These are internal
Creator/Logic delegates; there is no public strategy or execution-mode API.

`ConfigurationLogic` retains its existing role. `ContextResolutionLogic`, `WildcardContextLogic`, and
`RuleOrderingLogic` share parent/declaration ordering. `TypeHierarchyLogic` resolves superclass/interface signatures
and generic substitutions through ClassValue metadata, without rules or instance closures. `TypePattern` holds parsed
structure and optional known reflection Type metadata. Direct wildcard bounds are tested independently against that
metadata, including parameterized bounds, rather than generating wildcard products. Plain nested arguments remain
invariant. Symbolic strings alone do not supply subtype evidence.

`RequiresReportTimeEvaluation` and `RequiresStageTraversal` express which work is necessary. `ToMemo` retains memo
requirements and per-entity payload conversion; another memo-policy class would duplicate that responsibility.
Actions orchestrate these decisions, and data holders remain focused on structure, indexes, and ordered references.

## Compilation and binding

Compile known contexts, stage inventories, type patterns, generic bounds, and declaration identities during
configuration finalization. Pre-index patterns by their raw subject types so first-use matching does not scan every
rule or enumerate every possible generic wildcard combination. Publish the base plan only after successful
configuration construction and indexing. A failed attempt leaves configuration retryable.

Resolve operation-specific context order once per encountered operation. Wildcard operations need not be enumerated
upfront. A base plan belongs to the library instance; transaction-local bindings belong to the transaction. Materialize
transaction factories and attach supplied transactions before establishing the effective event-stage profile.
Lifecycle setup must retain its existing position: event reporting from a begin callback uses a conservative path
until transaction preparation and binding are complete. Supported local rule changes must invalidate/rebind the
selection or retain the conservative path; optimization cannot silently omit newly applicable work.

The plan's configured stage activity is separate from the existing begin/immediate lifecycle-window state. Reporting
requires both an active window and applicable configured event work. A begin lifecycle callback is not itself a
begin-stage entity rule. Commit/rollback lifecycle callbacks do not by themselves require per-report evaluation.
The selected [stage lifetime](TransactionsAndEvaluation.design.md#stage-timing) keeps reporting active after a
successful checkpoint and preserves begin encounter history; a failed checkpoint makes the handle rollback-only.
Compiled activity cannot override those lifecycle restrictions. Declaration presence also determines the
[effect-aware failure policy](IntentsAndEffects.design.md#evaluation-failure-policy) for an evaluation, including
transaction-local and conditional effects. This describes required participation, not cached predicate outcomes.

## Validation-only reporting

An effective plan with no begin/immediate entity handlers or intents can collect reports without entering those
evaluation paths. The stricter validation-only case has entity rules only in `StageName.VALIDATION`. The same reporting
principle also applies to other boundary-only entity stages when their dependencies permit it.

Input checks, transaction state checks, synchronization/admission accounting, event identity, explicit endpoints,
type information, and caller-supplied memo semantics remain intact. Record a report once and preserve existing
identity/type/event folding. Avoid the temporary runtime key currently constructed for empty report-time evaluation,
then reconstructed for indexing. Allocate begin encounter tracking, unused stage overlays, and memo storage only when
needed; an explicit memo call is sufficient to require memo storage.

A proven empty validation selection does not by itself authorize dropping the reported event: other stages,
transaction indexes, or callbacks may still observe it. This design preserves the transaction's event-record contract.

Defer validation-only type resolution, event-family expansion, rule selection, and missing-generic diagnostics until
validation needs them. Skip report-time intent checks, empty validation reports, empty-stage splitting, and handler
lookup. This optimization does not defer malformed endpoint errors or change explicitly immediate rule timing.

At checkpoint/commit, reuse selections across reported entities while traversing their existing encounter order.
Do not regroup callback execution by type. At each validation attempt, invoke applicable rules against the relevant
current values, subject to the effect-aware failure policy; neither successful predicate results nor earlier failures
are cached as entity validity. Failed checkpoints cannot be retried on the same handle. Empty
boundary stages can skip entity traversal while lifecycle callbacks and disposal still execute as required.

## EventField and first-use matching

Managed relationship indexes extend this compilation model under
[QuantifiersAndIndexes.design.md](QuantifiersAndIndexes.design.md#construction-access-and-folded-maintenance).
Their definition metadata can be shared, but their contents and pending maintenance belong to Tr. Empty index objects
may be created at transaction start. When index consumers are proven boundary-only, defer population/maintenance and
leverage folded events; an early query instead flushes folded changes before returning. Unknown callback dependencies
must not be classified as boundary-only by inspecting or guessing lambda behavior. Preserve the existing event/rule
folding contract and keep library maintenance separate from user Effect declarations.

Retain `EventField` as the owner of prepared, ordered handler/expression selections. Extend its existing lazy cache
rather than introducing a competing handler cache. Preserve transaction overlays through `EventFieldOverlay`, with
transaction-owned references released on transaction disposal. Existing entry points can adapt to a more compact
internal selection key.

A selection key identifies the owning operation/stage and the event-selection shape, reported structural type,
relevant runtime class identity, and applicable rule-source generation. Event-selection shape includes distinctions
needed for elementary/combined events and before/after forms. Per-event memo availability and payload conversion must
still be checked where required; sharing a selection cannot reuse another entity's before-state. Distinguish generic
instantiations and class loaders. Human-readable type-key strings alone are insufficient for metadata-aware identity.

The existing missing-memo admission policy remains scoped to the declared subject. Available payloads can select
inherited bi-event rules; explicit endpoints continue to carry their own values. This optimization does not widen
the stage or memo policy.

On a cache miss, resolve known generic hierarchy information, retrieve relevant raw-type pattern buckets, test the
candidate patterns, and collect declaration identities. Use declaration IDs established by the resolved context order
and cache a compact immutable sequence, including an empty sequence for a proven empty selection. Repeated use should
reach that sequence
without reconstructing an expanded `MultiKey`, repeating reflection, or sorting handlers again.

For N recorded events, D distinct selection signatures, and A actual applicable callback invocations, the intended
work is event recording/traversal proportional to N, cold matching proportional to D and relevant candidate patterns,
and execution proportional to A. Deferring checks does not remove checks on distinct entities. Event repetitions can
fold according to the established runtime identity contract.

## Membership representation

Configured patterns own pre-sized BitSet membership over compact declaration IDs. These rows are read-only after
publication. ResolveRuleSelection deduplicates matching pattern identity, avoiding repeated hierarchy discoveries.
MergeRuleMembership combines several rows with a private inventory-sized accumulator. Enumeration follows ascending
declaration IDs, preserving execution order and independent registrations without invoking callback equality.

Each pattern also owns its compiled immutable ordered handler selection. A single applicable pattern reuses that
selection directly when memo admission permits; no union or per-report handler reconstruction is needed. Empty
matches remain cacheable. Multiple patterns combine bitmap rows and materialize the final handler array directly,
without an intermediate identifier array. Missing-memo checks remain per entity and do not share before-state values.

The initial sparse/broad/clustered Night City fixtures selected zero or one row, whereas the earlier synthetic
prototype combined twenty. Genuine dwelling configurations select up to 32 distinct rows across generic subjects
and Create/CU/CUD/Expression families. Their prepared native comparisons favor BitSet for many-row combination and
materialization at 100–500 declarations. The single-row compiled shortcut avoids the measured cost of re-enumerating
a bitmap when no combination is necessary. Arrays and test-scoped Roaring remain comparators; BitSet adds no external
runtime dependency. Two-row array materialization can be faster, but the complete workflow evidence does not
justify storing a second membership representation and introducing an adaptive threshold. Precompiled single-row
handlers add storage alongside bitmap rows, slightly increasing measured full multirow holder graphs. Cold-selection
allocation falls; complete transaction ranges overlap because most entities reuse cached handlers. The internal
representation choice therefore targets preparation cost rather than claiming universal workflow acceleration.

Warm EventField dispatch reuses immutable ordered handlers regardless of membership representation. It performs no
union, expanded MultiKey construction, or sorting. Membership never changes declaration order, callback frequency,
or payload prerequisites. The [performance topic](../performance/topics/ExecutionPlans.md) links append-only
measurements and their limits; no numerical performance gate or application throughput promise follows.

Avoid full matrices over loaded JVM types, exhaustive generic wildcard combinations, and preallocation for every
operation/stage/type combination. Compile configured patterns once, then populate observed selections lazily.

Native membership can materialize its final ordered handlers directly. A bitmap need not allocate an intermediate
integer array, and both alternatives can reuse a single prepared row without union allocation. Prepared bitmap rows
must never be mutated by concurrent dispatch; accumulators are local to one resolution. Declaration IDs still define
execution order. A transferred handler array is owned exclusively by the immutable selection; caller collections
continue to be copied.

Representation evidence must include actual distinct matching-row counts independently of total rules and entities.
Sparse/broad/clustered labels alone do not identify combination work. Concrete/inherited and generic patterns across
applicable event families produce genuine many-row dispatch. Synthetic overlapping sets test a separate workload from
RulePattern partitions. Preserve the simple cases and compare construction, native combination, final selection,
cache misses/hits, complete transactions, allocation, and shared row graphs without assuming a winner.

## Ownership, concurrency, and diagnostics

Publish immutable plans and selections atomically. Concurrent first use may compute equivalent temporary metadata,
but readers must observe a complete selection and no partially populated row. Do not hold library-wide locks while
calling predicates, effects, lifecycle callbacks, or user factories. One transaction retains its existing restriction
against concurrent mutation.

Keep configuration-specific IDs, rows, selections, warning suppression, and closures instance-owned. Shared class
metadata, if used, contains no library rules or transaction state and must permit class-loader reclamation. Bound
runtime descriptor/operation cache growth independently of reported entity count; eviction may require recomputation
but cannot change applicability or ordering. Each EventField retains at most 1,024 combined compact/legacy selections,
including empty results; transaction overlays retain at most 1,024 merged selections. Operation inventories and stage
profiles each retain at most 128 operations. FIFO bounds allow hits to avoid the insertion lock. Capacity covers the
primary
200-descriptor workload and the measured 500-signature mixed-event workload; churn tests cross both limits.

Unresolved-warning suppression retains 64 signatures for the whole evaluation instance and clears at shutdown. The
legacy descriptor cache retains at most 4,096 entries; its key holds weak class references so capacity alone does not
pin a class loader. Shared hierarchy ClassValue entries contain structural metadata only. Object-graph measurements
exclude JVM/class metadata and weak referents; entry bounds are not byte-level heap limits.

Transaction disposal clears event records, memo values, overlays, and local selections on success/failure according to
the existing lifecycle contract. Event-index holders replace cleared backing sets so retained terminal handles do not
keep large empty tables. Memo storage, stage data, and begin encounter tracking are allocated only when needed.
Bi-events use the available endpoint as entity identity, including deletion ports whose after endpoint is null.
Successful library shutdown releases instance-owned caches after active work drains.
Caller-owned relationship sources and object contents remain caller-owned.

Unresolvable generic information is handled under the
[warning policy](../specifications/LibraryAndConfiguration.spec.md#unresolved-generic-warnings). A failed resolution
must not publish a successful empty selection. Diagnostics identify the operation and offending context/type/rule as
available, explain supplying a suitable `TypeKey`, and use the existing logging/troubleshooting facilities.

## Workload and evidence

Night City supplies deterministic large-workload fixtures, varied entities, inherited interfaces/classes, and concrete
generic instantiations. Its shared `model/`, `configure/`, `data/`, and `app/` code supports specifications and the
independent consumer. The library distribution remains independent of these fixtures.

Compare validation-only and immediate-stage configurations using the same object/type/rule populations. Measure
construction/finalization, reporting, first validation, warmed transactions, actual rule work, and retained transaction
data separately. Include repeated reports, distinct equal entities, upper-bound patterns, diamond interfaces,
unmatched signatures, concurrent first use, cache churn, failures, and repeated checkpoints. Throughput alone does
not establish correctness or resource hygiene. Measurement conventions are in the
[performance guide](../performance/Readme.md).

## Documentation discipline

Public API JavaDocs explain supported matching, evaluation timing, warnings, and caller responsibilities where those
behaviors are exposed. Non-trivial internal classes explain their purpose, collaborators, ownership,
publication, and compatibility constraints in class-level JavaDocs, with additional method rationale where needed. Avoid
routine parameter/return boilerplate in implementation packages.
Design describes the solution; schedules, milestones, unresolved delivery choices, and evidence status belong in the
execution plan and project records.

[roaring]: https://github.com/RoaringBitmap/RoaringBitmap#how-does-roaring-compare-with-the-alternatives
