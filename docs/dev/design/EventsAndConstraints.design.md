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

## Access reports across stages

Read and RL describe loaded working objects; RL additionally selects read handlers because a locked read is a read.
The library does not acquire the application lock. Write describes a save without inventing create/update endpoints;
Access is a general explicit report rather than a wildcard expansion into all storage accesses. General On and entity
expressions supply common rules. Selection includes the common Event route used by On, merges declarations in existing
order, and removes duplicate
discovery. Intents match concrete and general families, then use the cached shared selection and local declaration
order rather than event-family discovery order.
Elementary-to-compound opt-out does not turn off compound-to-elementary RL selection.

Use existing handle/ID event entry points rather than add shortcuts. Retain begin history across checkpoints, immediate
per-trigger authorization, and identity-folded boundary evaluation. Parent/child/local intent restrictions all apply;
report-time rejection precedes event recording and automatic enrollment. Managed read opt-out affects Read/RL/Access,
while Write still performs mutation maintenance. Failed boundaries keep the established rollback-only lifecycle.

Each handle owns its report history and managed contents. Tests must close it even when assertions fail, inspect
released collections/views, and never imply that explicit rollback undoes application reads, locks, or writes.
General On dispatch reuses EventField selection ownership rather than adding a new cache. Matching several intent
families reuses a merged EventField selection and orders remaining local declarations. This adds report-time work when
intents participate; measure complete workflows against typed-rule controls as well as new general-rule scenarios.
Acceptance exercises both naming/expansion modes and all built-in event roots without changing their endpoint meaning.

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

## Structured violation diagnostics

Diagnostics borrow Rust's separation of a primary message, error code, context, notes, and corrective guidance.
For this runtime library, the useful location is the operation and rule declaration rather than a compiler source
span. Diagnostic data is available programmatically and drives the readable presentation; callers do not parse text
to identify a rule or failure category. This extends the existing exception/report flow, not the transaction API.

A library diagnostic code identifies the failure category; the existing description identifies the consumer's rule.
Consumers choose prose or a short ID-like description without additional metadata arguments, ruleId methods, or
separate caller rule identifiers. Preserve description text in the structured fields; rendering escapes it only for
presentation. Codes have no `EX-` prefix; the ExistentialException type supplies library attribution. Neither codes
nor descriptions have application-model semantics. Descriptions need not be unique and never become selection or
deduplication keys. RuleInventory's compact integer positions remain internal declaration identities, not external
diagnostic codes. Optional/missing descriptions retain the existing description policy; do not synthesize a rule ID.

### Presentation and provenance

Library-generated structured failures use detailed multiline `getMessage()` output by default. Fields appear in
this order when available: operation, rule, context, event, evaluation, then notes and actions. The primary line
contains the library code and human-readable failure category; the rule field supplies the consumer's description
without repeating it in the primary line. For example:

```text
error[RULE-REJECTED]: Rule rejected
  operation: /records/save
  rule: Amount must be positive
  context: /records/
  event: update<Record>
  evaluation: validation during checkpoint

  note: transaction is rollback-only
```

`operation` is the invocation's operation; `context` is the rule's original declaration context, including a parent
or literal wildcard context. Never substitute the matched operation for that provenance. Transaction-local rules
identify their transaction-local origin rather than inventing a context declaration. Missing information is omitted
without guessing. Retain the reported event/type and selected rule event/type separately when expansion or general
matching makes them differ. Stage and triggering boundary are separate data: validation can run during either
checkpoint or commit. Source file/line spans are not necessary to this runtime context model.

Notes explain consequences or limitations, such as rollback-only state or evaluation stopped before later work.
An optional `action` gives a specific correction the library can justify. Omit obvious stock recovery instructions;
do not infer a remedy from an arbitrary business predicate. Relevant troubleshooting links remain available.
An aggregate presents shared transaction information once and keeps individual violations distinct. A sibling
violation is not the underlying cause of another violation, even though existing Throwable cause/suppressed wiring
is retained for compatibility. Rendering escapes embedded line breaks/control characters without altering raw fields.

### Evaluation and recovery

Declaration provenance travels with each rule occurrence through shared compiled selection and transaction-local
overlays. Reusing one handler in two contexts must retain both declarations' identities. Intent rejection must keep
the actual rejected rule and its failure details instead of reducing the result to a boolean with a generic message.
Missing authorization, a rejected predicate, and an unexpected callback failure remain distinguishable; an absent
matching intent has no offending declaration to invent.

Keep stock library exception types, underlying causes, aggregate membership, ordering, and the existing effect-aware
evaluation policy. Checks-only evaluation collects ordinary violations. When participating effects require stopping
after a violation, diagnostics state that later work was not evaluated rather than implying an exhaustive report.
Unexpected callback failures and authorization denials still stop immediately. Diagnostic construction must not invoke
callbacks again, change handle state, execute rollback callbacks, or hide the original failure.

Unexpected RuntimeException from a non-intent callback is normalized into the existing EventHandlerException, retaining
the original as cause; unexpected intent callback failure retains IntentViolation. This gives callback crashes a
structured library surface and keeps them distinct from ordinary rejected predicates. Error propagates unchanged.
Do not mutate callback-supplied exceptions to attach invocation context: retain them as causes of fresh stock failures.
Stock library-origin failures retain their existing underlying causes/suppressed failures when contextualized.

### Ownership and cost

Prepare/share immutable declaration metadata with its configured owner. Invocation diagnostics copy descriptive
values on failure; they retain no transaction, reported entity, mutable index/view, or runtime callback merely to
render later. A user-supplied Throwable cause can itself retain arbitrary user data; this design does not promise
to sanitize its object graph. Detached diagnostics remain readable after disposal and do not require a failed-handle
registry. Concurrent transactions do not share mutable diagnostic state.

Keep successful reporting, folding, and cached selection free of per-entity diagnostic allocation and stack walks.
Allocate failure details only when needed and avoid repeated whole-report rendering while collecting violations.
Compare complete success, rejection/rollback, recovery, and many-violation workflows, with setup separate. Detailed
messages trade greater failure-path output for immediate usefulness; measure rendering separately from evaluation.

### Public surface and rendering ownership

ExistentialException.diagnostic() returns a read-only Diagnostic snapshot or null when no structured data is attached.
Existing constructors retain plain message/cause behavior for undecorated exceptions. Diagnostic is a serializable
interface under com.taitl.existential.diagnostics; its internal immutable implementation contains descriptive values,
ordered immutable notes/actions/children, and no Throwable. Causes stay on the exception. No public declaration metadata
builder is introduced. Code, context, reported/selected event and type, stage/boundary, local origin, and known
evaluation stopping are inspectable independently of presentation. Diagnostic.message() is the concise primary message;
getMessage() is detailed by default. Null denotes an unknown optional scalar; absent lists are empty.

DiagnosticCode separates RULE_REJECTED, INTENT_REJECTED, INTENT_MISSING, CALLBACK_FAILED, and EVALUATION_FAILED.
Its id() returns the corresponding unprefixed hyphenated display code, such as RULE-REJECTED. Missing intent has no
invented rule but can suggest a matching declaration or reviewing gating. Each code has a stable troubleshooting
heading. Selection positions and caller descriptions do not replace these library failure categories.

DiagnosticLogic delegates snapshot construction, exception contextualization, rendering, and escaping to small actions.
Pass evaluation context explicitly, without thread-local ambient state. Classify ordinary failures before decoration
using types/original text for legacy compatibility, then codes; never depend on formatted output. Capture rollback-only
notes after the actual boundary state transition. Preserve report siblings and Throwable cause/suppressed relationships
without confusing them in the diagnostic tree. Shared transaction data is displayed once, but child snapshots retain
their complete invocation context for independent inspection.

Render lazily and memoize the detailed string per exception. Rendering uses no entity/Throwable toString, callback
formatters, stack inspection, or live owner. A non-fatal formatting failure returns the original fallback message;
diagnostic-construction failure leaves the original failure propagating. Neither path retries callbacks or masks a
business failure. VM errors are not caught for decoration. These guarantees apply to library-generated snapshots,
not arbitrary user implementations of the public interface.

### Implementation evidence

The selected implementation carries immutable RuleOrigin values alongside RuleDeclaration occurrences, including
legacy EventField selections and handle-owned local intent overlays. Internal library-failure markers distinguish
runtime-created exceptions from callback-supplied stock/custom exceptions without stack inspection. FailureSnapshot
keeps fallback text detached so adding rollback-only state never forces message rendering. DiagnosticLogic delegates
construction, copying, rendering, and escaping; successful dispatch creates no diagnostic snapshots.

[Acceptance and
verification](../project/sprints/Sprint09_V2IndexesAndOperationalCoverage.md#structured-diagnostics-verification)
and [performance evidence](../performance/topics/Diagnostics.md) cover this selected slice. The broader approved
public-exception suggestion remains open.

## Performance

One report creates an event/type lookup whose expansion depends on the declared generic type. Repeating an equal event
on the same instance folds the validation work, while repeated reporting still costs event creation/indexing and
immediate-stage lookup. Source cardinality and matched-source recovery cost are independent of this event-folding cost.

[derived events]:
  ../../../src/main/java/com/taitl/ex/logic/evaluation/events/split_events/event_splitter/EventSplitter.java
[relationships]: ../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java
