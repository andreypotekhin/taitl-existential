# Events and Constraints Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
Legacy `+` marks are preserved. Add `+` to a requirement only when its current acceptance evidence passes; see the
traceability matrix.
Reference requirements by their descriptive headings. The claims describe library behavior, not legal commitments.

See the paired [design](../design/EventsAndConstraints.design.md) and [specification index](../Specification.md).

## Configuring Custom Events

The original specification left this section empty. Its missing decisions are tracked in the paired design.

## Events

### Reporting class events

User can emit (record) a class event, such as accessing or modifying entity.

### Access event selection

+ Read selects read handlers; RL selects read-and-lock and read handlers. Write selects write handlers without inferring
creation or update. Explicit Access reports general access without inferring read, lock, or write. General On handlers
and entity expressions can observe all four. General On declarations also apply to other built-in event reports;
bi-events supply the after-value, or the before-value for deletion. Selected declarations run once in declaration order,
even when a report
selects several event forms. Disabling elementary-to-compound expansion does not disable RL-to-read selection.
These are reports of application actions; no storage access or lock is acquired by the library.

### Access event stages and recovery

+ Access reports retain begin-stage once-per-event/type history across successful checkpoints. Immediate processing
runs for every accepted trigger; boundary validation folds repeated same-event/type/object reports, retains distinct
object identities, and reevaluates retained events at subsequent boundaries. A successful checkpoint permits more
reports. A failed checkpoint or commit detaches ID lookup and permits only explicit rollback or close. Rollback
callbacks remain explicit-only. A fresh transaction can perform a corrected operation.

### Access authorization and managed enrollment

+ Applicable parent, child, and transaction-local intents compose conjunctively for access reports, in resolved
context/declaration order across read, lock, and general event families. A denied candidate
is not recorded or enrolled in managed indexes. Default read enrollment covers Read/RL/Access; disabling it does not
disable Write maintenance. Fully manual indexes remain unchanged by reports. Handle and string-ID event entry points
retain equivalent selection/authorization semantics. Explicit Access/RL use the existing typed event API.

Acceptance: [access stages, policies, and
recovery](../../../src/test/java/com/taitl/specs/library_usage/UserCanValidateAccessEvents.java)
and [general rules and intent
ordering](../../../src/test/java/com/taitl/specs/library_usage/UserCanApplyGeneralEventRules.java).

### Equal event deduplication

Multiple equal events (e.g. same entity + event type) (successive or not) are considered to be (have same
effect) as a single such event.

On the representative entity path, event equality uses the same event/type key and the same entity instance.
Repeated reports on that instance fold; distinct equal snapshots remain distinct reported entities. Source membership
uses its collection equality/comparator contract instead of runtime entity identity.

### Events require library configuration

User can't send any events before the library is configured.

### Events require a started transaction

User can't send any events before the transaction has started.

### Events after transaction completion

User can't send any events after transaction has been committed/rolled back.

### Configuration after the first event

User can't modify configurations (e.g. create invariants) after sending first event.
- Except for specifying custom Transaction object for transaction run

### Port event type inference

+ When emitting a port event without an explicit type key, library infers the type from the non-null entity
value (preferring t1 when present, otherwise t0). Both t0 and t1 can't be null.

### Transit event endpoints

+ When emitting a transit event without an explicit type key, both t0 and t1 must be non-null.

## Constraints

(Below, the terms 'constraints' and 'invariants' are used interchangeably)

## Constraint on a single entity

### Internal field constraints

User can create a constraint on a field within an entity's class code.

### External field constraints

User can create a constraint on a field outside of entity's class code.

### Third-party field constraints

User can create a constraint on a field of a third party class (e.g. a class which code is unavailable).

### Two-field constraints

User can create a constraint on two fields of a class.

### Multiple-field constraints

User can create a constraint on multiple fields of a class.

### Collection size constraints

User can create a constraint on a collection field size.

### Collection contents constraints

User can create a constraint on a collection field contents.

## Constraints on multiple entities

### Same-class entity relationships

Create a constraint on two entities of same class within entity class code.

### Cross-class entity relationships

Create a constraint on two entities of different classes, outside entity class code.

### Same-class existence constraints

Require another entity to exist when entity exists, (same class, within entity class code).

### Cross-class existence constraints

Require another entity to exist when entity exists, (different classes, outside entity class code).

## Constraints on entity evolution

### Entity transition invariants

+ User can declare an invariant on entity transition.

### Entity porting invariants

User can declare an invariant on entity porting.

### Explicit evolution and endpoint validation

+ The representative cat evolution rule preserves color and requires a dwelling at the selected destination.
Transit reports explicit non-null before/after values. Port permits one null endpoint for creation or deletion;
both endpoints null are rejected immediately. An invalid event does not enter validation and leaves the transaction
available for a corrected event. Failed commit identifies the operation and violated rule descriptions, detaches
active transaction lookup, and permits explicit rollback. Source collections and caller-owned snapshots are not undone.

Acceptance: [relationships and endpoint
validation](../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java)
and [cat relocation](../../../src/test/java/com/taitl/specs/library_usage/UserCanCreateAndRelocateCats.java).

## Constraints on evolution of multiple entities

### Additional event values

+ User can report a typed application envelope containing an entity and additional values (for example, actor and
destination) through standard create/read/write/transit events. Rules receive that exact envelope; explicit generic
TypeKeys retain its payload type. Distinct envelope identities remain distinct, while repeated reports of the same
identity follow existing folding. Nested values are not independently enrolled or deep-copied. Checkpoint continuation,
explicit rollback/fresh recovery and transaction disposal apply normally. Custom event registration/splitting remains
V3.

## Constraints on subclasses

### Inherited class constraints

Constraints on parent classes apply to subclasses under [entity hierarchy matching](#entity-hierarchy-matching).

## Entity type matching

### Entity hierarchy matching

+ Event dispatch traverses generic superclass signatures with type-variable substitution and directly or indirectly
  implemented interfaces. A declaration reached through an interface diamond executes once. `UserCanApplyInheritedRules`
  covers a multi-edge generic superclass and a diamond interface.

Rules declared for the reported entity class, its superclass chain, and its directly or indirectly implemented
interfaces participate when compatible. Diamond inheritance does not multiply one declaration's execution. Applicable
rules retain declaration order inside their resolved context, rather than executing by class/interface specificity.
An explicit generic key supplements available type evidence; it cannot erase known runtime subclass/interface rules.

### Generic argument matching

An explicitly reported concrete generic type selects compatible exact, raw, and unbounded-wildcard rules. Using an
illustrative `Box<T>`, `Box<Cat>` can select `Box<Cat>`, raw `Box`, and `Box<?>`, but does not select `Box<Animal>`
merely because Cat extends Animal. The same restriction applies to non-collection generic classes: a callback typed
as `Box<Animal>` could otherwise store a different Animal subtype in a Box reserved for cats.

Concrete generic arguments are invariant. Nested wildcard positions follow the same compatibility discipline; a
wildcard inside a concrete invariant argument does not make its enclosing argument covariant. Preserve symbolic
exact matching for string-only TypeKeys. Inheritance/bound matching requires reliable metadata, not guessing which
class an ambiguous simple name denotes.

+ Type-key expansion treats only the entity type's direct generic arguments as wildcard positions. For example,
  reporting `Outer<Box<Cat>>` does not select `Outer<Box<?>>`; only `Outer<?>` and raw `Outer` are compatible.
  `UserCanApplyInheritedRules` verifies that the nested callback is not invoked.

### Generic signature recovery

Use explicit TypeKey metadata and retained superclass/interface signatures to recover actual arguments where possible,
including substitution across multiple inheritance edges. For example, `CatBox extends Box<Cat>` provides a known
Box argument without an explicit key at each report. The library does not infer erased arguments from object contents
or substitute a declared upper bound as though it were the concrete argument.

Keep unresolved information distinct from a known type. Apply rules whose compatibility is provable, including general
raw/unbounded rules; unresolved concrete compatibility follows the
[warning policy](LibraryAndConfiguration.spec.md#unresolved-generic-warnings). Metadata-aware matching preserves
actual class identity in reflected hierarchy and descriptor-cache keys and does not change existing TypeKey
printed-key/equality contracts. The bounded dispatch descriptor cache evicts the oldest inserted key after its
4,096-entry limit; eviction changes recomputation cost only.

### Upper-bounded generic matching

A rule for `Box<? extends Animal>` can select an entity reported as `Box<Cat>` when the Cat-to-Animal relationship is
known. Apply superclass/interface projection and generic substitution before testing the bound. Multiple/nested generic
arguments obey the same compatible-bound and concrete-invariance rules; matching does not change callbacks' Java type
contracts. Lower-bounded rule patterns using `? super` are outside this selected contract and fail with a corrective
unsupported-pattern diagnostic during configuration rather than silently leaving an intended rule unapplied.

+ A rule using a direct upper bound selects a reported generic subclass when its reflected argument signature proves
  the subtype relationship. `UserCanApplyInheritedRules` verifies `Box<? extends Being>` receives `CatBox` through
  the resolved `Box<Cat>` signature and a raw `Box` event carrying an explicit anonymous TypeKey.

+ `IndexEvs` rejects a configured lower-bound pattern before publishing its indexes and links to the
  [lower-bound troubleshooting guidance](../../../Troubleshooting.md#unsupported-lower-bounded-type-rules).
  `UserCanApplyInheritedRules` verifies the failure occurs during configuration finalization.

+ `UserCanApplyInheritedRules.appliesIndependentBoundsAndParameterizedBoundsWithoutVariantProducts` verifies
  simultaneous upper bounds on both Dwelling arguments and a parameterized Being bound. Native TypeKey/hierarchy
  metadata proves the positive matches; unrelated Muridae arguments do not match the Felidae bound.

### Shared type matching

Matching results may be shared across distinct entities with the same relevant type/event signature, but actual rule
checks still apply to their respective entities. Resource, ordering, concurrent-publication, and validation-only
requirements are defined in [ExecutionPlans.spec.md](ExecutionPlans.spec.md).

## Constraint violation reporting

### Constraint violation exceptions

User should receive an exception in case of constraint violation, along with the details.
Existential exceptions are unchecked, so application methods can propagate constraint failures without declaring
them in `throws` clauses; callers may catch the common base or a specific subtype when they can recover.

+ `UserCanPropagateExistentialExceptions.useTransactionApiWithoutThrowsDeclaration` verifies application code can
  call `begin`, `update`, and `commit` without a `throws` declaration.

### Constraint descriptions

+ User can pass a human-readable description when creating a constraint, to help with reporting.

Acceptance:
[policy tests](../../../src/test/java/com/taitl/specs/library_configuration/UserCanRequireRuleDescriptions.java)
checks ordinary, universal, existence, and managed-source validation failures. Structured diagnostics remain a separate
requirement.

The existing description identifies the rule in structured diagnostics. Callers choose prose, concise text, or an
ID-like description through existing declaration methods; no separate caller rule ID or metadata argument is required.
Descriptions are preserved as raw field values and need not be unique. Equal descriptions do not merge declarations
or change execution order. Missing descriptions follow the existing optional/required policy, without a generated ID.

### Violation details

Constraint violation exception contains details about the violation, such as human description of violated
rule.

### Structured violation diagnostics

+ Library-generated structured failures expose diagnostic fields programmatically and render a detailed multiline
message through `getMessage()` by default. The primary line identifies the library diagnostic code and failure
category; the rule field supplies the existing consumer description without repeating it in the primary line. Library
codes have no `EX-` prefix and remain separate from descriptions. Callers can inspect fields without parsing text.

+ Available context fields render in this order: operation, rule, context, event, evaluation, followed by optional notes
and actions. `context` names the original declaration context, including a parent or wildcard, while `operation`
names the invocation. Transaction-local rules identify that origin without claiming a context declaration. Missing
values are omitted rather than inferred. The diagnostic distinguishes the reported event/type from the selected
rule event/type where they differ, and the evaluation stage from its triggering boundary, such as checkpoint or
commit. Stage names in prose are lowercase.

+ Notes explain actual consequences or incomplete evaluation. Actions provide specific, justified corrections and
are omitted when no useful correction is known. Arbitrary predicate failures do not automatically receive generic
rollback/retry advice or inferred business remedies. Relevant troubleshooting references remain accessible. Rendered
caller text cannot inject additional diagnostic lines through embedded control characters; raw fields stay intact.

+ Reports retain separate violations and present common transaction information once. Ordinary checks-only failures
are collected under the existing evaluation policy. When effects participate and a violation stops later work, the
report identifies that limitation. Authorization denial and unexpected callback failure remain immediate failures;
their diagnostics distinguish missing intent, rejected intent, and callback failure, retaining the offending rule
when one exists and the original cause when present. No synthetic rule is reported for a missing intent.

+ Existing exception subtypes, aggregate membership, cause/suppressed relationships, and explicit-only rollback
callbacks remain available. Rendering never reruns rules or changes cleanup behavior. Diagnostic fields remain
usable after disposal without retaining handles, entities, indexes, or callback closures for presentation. This
does not constrain references held by a caller-provided Throwable cause.

Required acceptance covers parent/wildcard/local provenance, general and expanded event matching, checkpoint versus
commit, prose/ID-like/duplicate/missing descriptions, intent rejection and missing intent, callback causes, multiple
violations, effect-induced stopping, rendering order and escaping, concurrent isolation, and post-disposal access.
Direct evidence: UserCanDiagnoseRuleViolations supplies 29 cases; DiagnosticLogicTest supplies eight focused
compatibility, ownership, classification, and fallback cases. See [performance
evidence](../performance/topics/Diagnostics.md).

### Diagnostic access and compatibility

+ ExistentialException.diagnostic() exposes a read-only com.taitl.existential.diagnostics.Diagnostic or null when
absent.
Library-generated snapshots are immutable and serializable, with nullable optional scalar values and non-null immutable
ordered lists. Diagnostic.message() provides the concise primary message; exception getMessage() renders the detailed
diagnostic lazily and memoizes the result. Caller-created exceptions using existing constructors retain their current
plain behavior when not contextualized by library evaluation. No rule-ID/metadata declaration API is added.

+ Diagnostic exposes code/message, operation, rule description, original context/local origin, reported event/type,
selected rule event/type, configured StageName, triggering boundary, known evaluation stopping, notes/actions,
troubleshooting reference, and aggregate children. Boundary values are report, begin, checkpoint, commit, or rollback
where known. Context is null for transaction-local declarations, whose local-origin flag is true. Children retain
full fields and correspond to the aggregate's multiple() order, even when common fields are shown once in rendering.

+ Rule-evaluation DiagnosticCode constants and their id() display values are RULE_REJECTED (RULE-REJECTED),
  INTENT_REJECTED
(INTENT-REJECTED), INTENT_MISSING (INTENT-MISSING), CALLBACK_FAILED (CALLBACK-FAILED), and EVALUATION_FAILED
(EVALUATION-FAILED). Each has a stable diagnostic-specific troubleshooting heading. Codes classify failures, not
consumer rules. Classification does not depend on the rendered message. Additional state/admission codes belong to
[transaction state diagnostics](TransactionsAndEvaluation.spec.md#transaction-state-diagnostics).

+ Existing stock library exception types, aggregate stage()/multiple(), defensive copying, and original underlying
cause/suppressed relationships remain supported. Unexpected non-intent RuntimeException from invoked callbacks is
wrapped in EventHandlerException; unexpected intent callback failure uses IntentViolation. Both expose CALLBACK_FAILED,
retain the original as cause, and stop immediately. Callback-supplied/custom exception objects are not mutated to add
invocation data; their exact instance remains available as cause under a fresh stock failure. Error propagates
unchanged.

+ Rollback-only notes reflect the state after a failed checkpoint/commit boundary. A report-time rejection does not
imply that state. Known early stopping is inspectable; absence of the stopping flag does not promise that every
possible violation was found. Required-description timing and transaction cleanup behavior remain unchanged.
+ Rendering performs no callback, entity/Throwable toString, stack scan, or live-handle access. If formatting fails with
a RuntimeException, getMessage() returns the original fallback message; diagnostic construction failure does not
replace the original business failure. These fallbacks do not catch VM errors or retry evaluation.

### Required constraint descriptions

Optionally, user can configure the library to require a description for each constraint.

+ For fluent context and transaction builders, BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS rejects missing/empty descriptions
  when
a rule is added, covering event checks, intents, effects, All, and Exists including managed sources. Each declaration
uses the current owning-instance flag even through a previously obtained builder. Failed declarations add no rule and
can be corrected on that builder. Enabling the flag does not invalidate earlier optional declarations; disabling it
permits subsequent optional declarations. Configuration freezes at the established first successful begin boundary.

Acceptance: [required-description policy and
quantifiers](../../../src/test/java/com/taitl/specs/library_configuration/UserCanRequireRuleDescriptions.java)
covers 33 cases. Standalone rule objects/runtime definitions have their explicit policy; no retroactive validation or
implicit instance binding is promised by this selected contract. Explicit standalone constructor argument contracts
remain unchanged; use the corresponding overload to omit optional text.

## Custom Event Types

### Defining custom event types

User can define a custom event type.

### Custom event handlers

User can define event handler for custom event type.

### Custom event context constraints

User can create a constraint based on custom event type (Context).

### Custom event transaction constraints

User can create a constraint based on custom event type (Transaction).

### Custom event splitting

User can customize event splitter to emit events of custom event type.

### Reporting custom events

User can send/record custom event from application code.
The system invokes custom event handler upon encountering the custom event.

### Supplied rule binding

+ Supplied Invariant/Intent/Effect definitions remain caller-owned. Configure them fully before first begin and keep
them read-only after binding. Read-only reuse across contexts/instances preserves independent declaration origins and
registration order; callbacks must synchronize any shared mutable application state. Post-binding mutation and deep
snapshots of arbitrary callbacks/quantifier sources are unsupported. An explicit intent TypeKey applies to its binding
without rewriting the supplied intent; a differing type captures its current leaf list while an equal type reuses the
read-only definition. Required-description policy at
fluent attachment validates all existing leaves prospectively without changing the supplied set's own option. Rejected
attachment adds nothing, so correction and retry are supported. Earlier optional registrations are not rechecked.


Acceptance for supplied binding: [supplied-rule
reuse](../../../src/test/java/com/taitl/specs/library_usage/UserCanReuseSuppliedRules.java).
Acceptance for additional values: [typed
envelopes](../../../src/test/java/com/taitl/specs/library_usage/UserCanPassAdditionalValues.java).
Direct low-level handler/quantifier failures without indexed declaration provenance retain available explanatory text
without inventing operation, type, stage or context. Indexed evaluation replaces those provisional facts with its
actual declaration snapshot and preserves ordinary rejection versus callback-failure classification.
