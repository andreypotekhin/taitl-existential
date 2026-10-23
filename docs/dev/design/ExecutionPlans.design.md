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

`ExecutionPlan` is immutable data: the resolved operation, ordered declaration references, stage activity, event
selection information, and dependencies such as before-state memo requirements. It contains configuration references,
never reported entities, transaction memo values, or validation results. Each rule registration has its own declaration
identity; using the same callback in two registrations does not make them one declaration.

`ExecutionPlanLogic` coordinates compilation and transaction binding. `CompileExecutionPlan` creates a base plan from
finalized configuration. `BindExecutionPlan` combines that plan with factory-produced and caller-supplied transaction
rules. `ResolveRuleSelection` performs a previously unseen dispatch lookup and publishes its ordered result through
`EventField`. These are internal delegates/actions, constructed through the existing `Creator` and `@Logic` patterns.
Their concrete signatures and data holders remain internal implementation details.

The existing `ConfigurationLogic` delegates context resolution to `ContextResolutionLogic`. The latter uses
`WildcardContextLogic` for matching and `RuleOrderingLogic` for parent-level/declaration ordering. `TypeHierarchyLogic`
resolves class/interface signatures and generic substitutions. `TypeMatchingLogic` tests reported descriptors against
configured patterns. `ExecutionPlanLogic` composes these results; it does not duplicate their policies.

Business rules such as `RequiresReportTimeEvaluation`, `RequiresMemo`, and `RequiresStageTraversal` encode decisions
about required work. They operate on the compiled rule inventory, not on entity values. Actions orchestrate these rules;
data objects do not grow into general-purpose dispatchers. Keep classes focused and use smaller action/rule delegates
when a responsibility becomes substantial.

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

## Validation-only reporting

An effective plan with no begin/immediate entity handlers or intents can collect reports without entering those
evaluation paths. The stricter validation-only case has entity rules only in VALIDATION. The same reporting principle
also applies to other boundary-only entity stages when their dependencies permit it.

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
current values; neither successful predicate results nor earlier failures are cached as entity validity. Empty
boundary stages can skip entity traversal while lifecycle callbacks and disposal still execute as required.

## EventField and first-use matching

Retain `EventField` as the owner of prepared, ordered handler/expression selections. Extend its existing lazy cache
rather than introducing a competing handler cache. Preserve transaction overlays through `EventFieldOverlay`, with
transaction-owned references released on transaction disposal. Existing entry points can adapt to a more compact
internal selection key.

A selection key identifies the owning operation/stage and the event-selection shape, reported structural type,
relevant runtime class identity, and applicable rule-source generation. Event-selection shape includes distinctions
needed for elementary/combined events and before/after forms. Per-event memo availability and payload conversion must
still be checked where required; sharing a selection cannot reuse another entity's before-state. Distinguish generic
instantiations and class loaders. Human-readable type-key strings alone are insufficient for metadata-aware identity.

On a cache miss, resolve known generic hierarchy information, retrieve relevant raw-type pattern buckets, test the
candidate patterns, and collect declaration identities. Order the result using `RuleOrderingLogic` and cache a compact
immutable sequence, including an empty sequence for a proven empty selection. Repeated use should reach that sequence
without reconstructing an expanded `MultiKey`, repeating reflection, or sorting handlers again.

For N recorded events, D distinct selection signatures, and A actual applicable callback invocations, the intended
work is event recording/traversal proportional to N, cold matching proportional to D and relevant candidate patterns,
and execution proportional to A. Deferring checks does not remove checks on distinct entities. Event repetitions can
fold according to the established runtime identity contract.

## Membership representation

Use compact instance-owned IDs for configured type patterns and declarations. A cached row describes one observed
type descriptor's matches; it is not allocated per entity. With 200 descriptors and 256 rule slots, a dense row table
has 6.25 KiB of bit payload before metadata, object overhead, and operation/stage selections.

Compare short sorted integer arrays, JDK `BitSet` rows, and Java `RoaringBitmap` in the initial prototype. Sparse arrays
suit small matching sets; bitsets can make unions and intersections cheap at hundreds of rule slots. Roaring adapts
among array, bitmap, and run containers within integer-ID chunks. Its overhead and benefits must be measured on both
the primary compact-ID workload and larger sparse/dense or clustered sets, rather than inferred from entity count.
The [Roaring project documentation][roaring] describes those representations and notes that small universes can favor
uncompressed bitsets.

Include construction, union/intersection, enumeration into the final selection, allocation, and retained heap in the
comparison. Serialized bitmap size alone is not retained-heap evidence. Keep Roaring test-scoped during the prototype;
production adoption requires a measured advantage that justifies its dependency and distribution costs. The selected
row representation stays internal, and warm EventField selections use the same immutable ordered sequence for all
candidates. Membership does not determine execution order. Do not substitute callback-object equality for declaration
identity.

Avoid a full matrix over all loaded JVM types, exhaustive generic-variant products, and preallocation for every
operation/stage/type combination. Compile known metadata once and populate observed selections lazily. Representation
and cache-capacity choices must be justified by comparable time/allocation/retention measurements; they are internal
choices that do not alter matching semantics.

## Ownership, concurrency, and diagnostics

Publish immutable plans and selections atomically. Concurrent first use may compute equivalent temporary metadata,
but readers must observe a complete selection and no partially populated row. Do not hold library-wide locks while
calling predicates, effects, lifecycle callbacks, or user factories. One transaction retains its existing restriction
against concurrent mutation.

Keep configuration-specific IDs, rows, selections, warning suppression, and closures instance-owned. Shared class
metadata, if used, contains no library rules or transaction state and must permit class-loader reclamation. Bound
runtime descriptor/operation cache growth independently of reported entity count; eviction may require recomputation
but cannot change applicability or ordering. Choose and record concrete bounds using the workload evidence. Retaining
every historical warning signature in a second unbounded registry is not acceptable.

Transaction disposal clears event records, memo values, overlays, and local selections on success/failure according to
the existing lifecycle contract. Successful library shutdown releases instance-owned caches after active work drains.
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
behaviors are exposed. Internal classes receive only necessary rationale for non-obvious ordering, ownership,
publication, and compatibility constraints. Avoid routine parameter/return boilerplate in implementation packages.
Design describes the solution; schedules, milestones, unresolved delivery choices, and evidence status belong in the
execution plan and project records.

[roaring]: https://github.com/RoaringBitmap/RoaringBitmap#how-does-roaring-compare-with-the-alternatives
