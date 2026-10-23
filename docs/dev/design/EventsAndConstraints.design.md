# Events and Constraints Design

## User outcome

Report entity creation, modification, access, and evolution, then receive a useful failure when an invariant is false.

The [specification](../specifications/EventsAndConstraints.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

Entity and access events are explicitly reported. Port permits one missing endpoint; Transit requires both states. The
splitter derives elementary/combined events and memo state supplies before-values for bi-event handlers. Constraint
evaluation and handler invocation use the configured entity/type keys rather than scanning all rules.

- [public dispatch delegation](../../../src/main/java/com/taitl/ex/core/existential/ExistentialEvents.java)
- [derived events]
- [before-state memo storage](../../../src/main/java/com/taitl/ex/logic/transactions/data/MemoData.java)
- [public exception types](../../../src/main/java/com/taitl/existential/exceptions)

## Approach and tradeoffs

Keep event reporting explicit and limited to in-memory entities. Preserve fail-early input validation and keep
before-state ownership visible to callers; do not imply automatic observation of arbitrary object mutation.

## Explicit evolution

Transit takes non-null before/after snapshots. Port takes at least one non-null endpoint: null before denotes creation,
null after denotes deletion. Input errors fail before indexing and permit a corrected event on the same transaction.
Diagnostics explain the endpoint requirements and link to the appropriate remedy. A cat's evolution preserves color;
the relationship rule validates the after-value's selected dwelling, or the before-value for deletion.

Runtime event folding uses entity instance identity and event/type keys. Repeated reports of the same event on the same
instance fold; distinct equal snapshots remain separate reported entities. Source collection membership instead uses
its equality/comparator contract. Example value classes compare their actual class, color, and location consistently
with hashing. Callers must keep source keys stable and use explicit snapshots rather than mutating a stored hash key.

Validation reports preserve rule descriptions and identify the operation. Rejected commit detaches active lookup;
explicit rollback runs callbacks and clears transaction runtime event/type indexes, even when the caller retains the
closed handle or an index reference. This release does not undo caller-owned objects or relationship source contents.

## Type matching

`TypeHierarchyLogic` resolves the entity's concrete class, superclass chain, implemented interfaces, and generic
substitutions along inheritance edges. `TypeMatchingLogic` compares those descriptors with relevant registered
patterns. Interface diamonds may discover a declaration several ways; the compiled declaration identity keeps one
invocation without combining separately registered rules. Context order and within-context declaration order remain
independent of type specificity.

Retain original reflective Type/Class evidence alongside TypeKey's existing formatted representation. Preserve public
constructors/factories, string output, and equality compatibility. Type descriptors track actual class identity and
known arguments, distinguishing separate loaders and unresolved metadata. String-only keys retain symbolic exact
matching; a simple name does not justify choosing an arbitrary loaded class. Explicit generic evidence supplements
known runtime inheritance instead of disabling subclass/interface constraints.

Concrete generic arguments remain invariant because predicates/effects can contain arbitrary Java code, including
mutation. Even a non-collection `Box<Animal>` callback may store a Dog, so invoking it on `Box<Cat>` is unsafe.
Upper-bounded patterns such as `Box<? extends Animal>` express the supported subtype relationship. Resolve generic
superclasses/interfaces before testing bounds, and preserve invariance at enclosing concrete arguments when handling
nested patterns. Raw and unbounded patterns supply intentional broad matching. A class such as
`CatBox extends Box<Cat>` provides actual arguments that can be recovered without inspecting object contents.

Unknown arguments remain unknown; a type variable's upper bound is not its actual argument. Apply provable matches
and warn if missing information could prevent intended configured matches. The warning option is owned by
[library configuration](LibraryAndConfiguration.design.md#type-diagnostics-and-configuration-scope). Unsupported
lower-bounded rule patterns fail clearly rather than masquerading as a successful no-match.

Prepare raw-type buckets and parsed patterns once, then share structural matching results across entities through
the [ExecutionPlan/EventField design](ExecutionPlans.design.md). Do not enumerate all possible generic variants per
entity. Cache selections, including proven empty selections, without retaining reported objects or predicate results.
Per-event memo and before/after checks still use the actual event payload.

## Evidence and acceptance work

- [entity events](../../../src/test/java/com/taitl/specs/library_usage/UserCanSendEntityEvents.java)
- [memo behavior](../../../src/test/java/com/taitl/specs/library_usage/UserCanUseMemoForBiEvents.java)
- [event input/equality tests](../../../src/test/java/com/taitl/existential/events)

The [relationship acceptance][relationships]
backs the selected input/evolution/recovery contract. Hierarchy, generic-bound, diamond, loader identity, and
diagnostic cases need direct acceptance under the type-matching contract. Custom event registration remains a separate
capability. Passing adjacent unit tests does not establish the new type-matching behavior.

## Ownership and diagnostics

The caller supplies stable explicit snapshots and synchronizes mutable relationship sources. The library reports
endpoint mistakes before event indexing, validates the selected entity at the configured stage, and retains individual
rule exceptions under an operation-naming ValidationStageExceptions. A rejected input can be corrected on the current
transaction; a rejected commit requires a fresh transaction for retry. Explicit rollback releases runtime indexes but
does not compensate caller-side object/storage changes. Configuration source references remain owned by configuration.

## Performance

One report creates an event/type lookup whose expansion depends on the declared generic type. Repeating an equal event
on the same instance folds the validation work, while repeated reporting still costs event creation/indexing and
immediate-stage lookup. Source cardinality and matched-source recovery cost are independent of this event-folding cost.

[derived events]:
  ../../../src/main/java/com/taitl/ex/logic/evaluation/events/split_events/event_splitter/EventSplitter.java
[relationships]: ../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java
