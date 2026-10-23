# Runtime Execution Plans Specification

These requirements describe the selected compiled-execution behavior. They are not implementation evidence and carry
no completion marks until direct acceptance tests pass. Rationale and internal concepts are in
[ExecutionPlans.design.md](../design/ExecutionPlans.design.md).

## Execution plan selection

### Effective rule inventory

The library derives required execution work from all applicable configured context rules and actual factory-created
or caller-supplied transaction rules. A context-only validation profile cannot suppress report-time rules supplied by a
transaction. Separate instances and separate transaction bindings do not share mutable rule selections.

### Automatic specialization

Applications use the existing transaction/event APIs. The library selects the validation-only reporting path
automatically when the effective inventory has no report-time handlers or intents. Reporting during incomplete
transaction setup uses the conservative path. Supported changes to local rules cannot leave a stale selection active.

### Failed compilation and resolution

Failed configuration-plan compilation leaves configuration unfinalized and retryable. Failed first-use selection
resolution does not cache an empty successful result or a partial selection. Existing begin, checkpoint, commit,
rollback, and shutdown failure/cleanup contracts continue to apply.

## Validation-only transactions

### Reporting before validation

For validation-only rules, reporting well-formed events records them without executing predicates, effects, or intent
checks. It does not resolve validation handler selections or expand validation event/type combinations before the
boundary that needs them. Transaction state and input validation remain immediate, including invalid Transit/Port
endpoints. Generic-resolution warnings are emitted when resolution is needed, subject to the configured warning policy.

### Event identity and folding

The specialization preserves existing event/type/entity-identity folding. Repeated reports of the same event/type
on the same entity fold as specified; distinct equal entities remain distinct. Different event kinds and explicit types
retain their established distinctions. The optimization adds no guessed net-change coalescing; retained records keep
their needed endpoint payloads under the existing folding contract.

### Boundary evaluation and order

Checkpoint and commit perform validation at their established lifecycle positions, preserving operation/context/rule
order and entity encounter order. Applicable checks execute for each recorded entity/event as required. Sharing a
selection does not share predicate outcomes or omit checks on distinct entities.

### Repeat validation

A later checkpoint/commit reuses applicable rule selections, but evaluates the relevant current entity/source values
again. A previous successful check does not certify later mutations, and an earlier failure does not contaminate a new
validation report. The specialization adds no new snapshot or automatic compensation guarantees.

### Explicit report-time behavior

Begin/immediate handlers and intents retain their existing timing/frequency whenever applicable. Adding an immediate
transaction rule to validation-only context configuration selects a path that executes that rule while reporting.
Lifecycle callbacks continue to run even when the corresponding stage has no entity-driven rules.

## Reusable matching

### Shared prepared selections

Distinct entities with the same dispatch signature can share an immutable selection in the library's `EventField`.
A dispatch signature includes all type, event-selection, operation/stage, and rule-source distinctions that can affect
applicability. Runtime descriptor-cache keys preserve actual class identity across class loaders. Public TypeKey and
EventKey rule-registration equality remains string-based, so same-named classes from different loaders are not
promised distinct registrations. Entity values and memo snapshots do not become shared selection contents.

### Rule declaration identity

One declaration selected by several context/type/event paths is invoked once for the applicable event evaluation.
Two separately registered declarations remain two rules, even if they reference the same callback or rule object.
This requirement does not replace event folding or change immediate-stage invocation frequency.

### Empty selections

A proven empty selection can be reused. Missing required metadata or failed resolution is not proof of an empty
selection. Unknown generic information follows the
[generic matching](EventsAndConstraints.spec.md#generic-signature-recovery) and
[warning](LibraryAndConfiguration.spec.md#unresolved-generic-warnings) requirements.

## Resources and concurrency

### Bounded retained selections

Runtime matching-cache growth is bounded independently of the number of entity reports. Cache eviction affects
recomputation cost, not rule applicability, order, or validation results. Transaction data can grow with distinct
recorded events and is released under the existing disposal contract.

### Cleanup and isolation

Transaction completion/disposal releases local events, memo state, and local rule overlays as required by the existing
lifecycle contract. Successful library shutdown releases instance-owned matching caches. Shared class metadata cannot
retain instance rules, reported objects, or transaction closures. Caller-owned relationship sources are not cleared.

### Concurrent first use

Supported concurrent transactions can encounter a new descriptor/signature simultaneously. They observe equivalent
complete selections, without losing or multiplying declarations or exposing partially compiled state. Rule callbacks
and factories are not invoked while holding a shared matching-cache lock.

## Acceptance scenarios

Direct specifications demonstrate validation-only reporting followed by checkpoint/commit, unchanged event folding and
order, fresh values at repeated checkpoints, custom-transaction immediate fallback, empty selection reuse, unknown
generic diagnostics and opt-out, concurrent first use, and cleanup after rejected commit/rollback/shutdown.

+ A Night City workload acceptance case reports 64 distinct Cat instances under five validation declarations, observes
  zero declaration callbacks while reporting, then observes exactly 320 callbacks at commit.

+ Configuration compilation and post-preparation transaction binding select begin/immediate report-time stages.
  Validation-only reporting keeps both inactive; context and supplied-transaction immediate rules keep effects active
  during reporting. `UserCanValidateLargeTransactions` covers all three bindings.

+ The shared Night City fixture reports 400 entities across 200 distinct real generic descriptors. Declaration
  callbacks remain deferred until commit and run exactly once per entity/rule. Root and installed-consumer
  specifications verify the same fixture.

+ Runtime generic descriptor expansion is cached by type-key class, descriptor, actual runtime `Class` identity, and
  full-name mode. The 4,096-entry cache is bounded; concurrent first use resolves one complete immutable selection,
  and same-named classes from distinct class loaders remain separate keys. `SplitTypeKeyTest` verifies reuse, eviction,
  concurrent first use, and class-loader identity.

Night City supplies large datasets of 10,000–100,000 entity reports, 20–200 concrete class/generic descriptors, and
100–500 configured declarations. Tests assert actual callback counts and unique signature counts, not only successful
termination. Performance records separate reporting, commit, complete workflows, and per-thread allocation where
measured; cold/warm resolution timings and byte-level retained-heap measurements remain future profiling work. No
numerical throughput promise is established by this specification.
