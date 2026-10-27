# Existential

Existential is a constraint library for expressing and enforcing invariants between program entities.

It provides a small set of math-inspired notations for describing application logic. Existential
implements two logic quantifiers, ∀ ("for any") and ∃ ("exists"), allowing you to create logical
expressions about application entities (elements of the business domain) and to guarantee those
expressions hold. For instance, the library lets you define constraints on a single field, multiple fields,
multiple objects, and how an object may and may not change over time.
Performance comes from validating the constraints only at transaction boundaries, treating repeated changes
to an entity during a transaction as single change.

## Limitations

Existential is an in-memory library, so its checks do not span beyond what is loaded as part of
a business operation.

## What it is not

Existential does not attempt to derive new truths from what's already known,
nor is it aimed at theorem proving or equation solving. It allows the user to declare
certain truths about a program's entities, and guarantees a failure,
such as an exception, if those truths are violated.

The library does not aim to implement any complete set of mathematical logic notations.
It provides a limited set of notations to allow for declarative reasoning about program
entities, and focuses on performance and memory efficiency.

## Formalisms

∀ == "For Any" (a universal quantification)  
∃ == "Exists" (an existential quantification)  
∈ == "Element of" (member of a set)  
| = "Such as" (a set comprehension)   
P == logical predicate (a boolean-valued function)  
⊤ == "Truth", a logical predicate always rendering true

In the examples below:
- Math notation is on the left, and corresponding Java notation is on the right.

## Establishing Truth

For any object of type X, a predicate holds true:

    ∀ x ∈ X ⊤(x)      all(x -> predicate(x))

    Here, predicate() is any boolean function.

For any object of a subset of X, a predicate holds true:

    ∀ x ∈ X | condition(x) ⊤(x)      all(x -> condition(x), x -> predicate(x))

For any object of X that has been changed in the course of a business transaction, predicate holds true:

    ∀ x0, x1 ∈ X, ⊤(x0, x1)      transit((x0, x1) -> predicate(x0, x1))

    'Transit' describes a change of an object with before- and after- states x0, x1. 
    x0 is the object state at the start of business transaction,
    x1 is the object state state at the end of business transaction.
    Both states are non-null.

For any object of X that has been created, changed or deleted in the course of a business transaction,
predicate holds true:

    ∀ x0, x1 ∈ X, ⊤(x0, x1)      port((x0, x1) -> predicate(x0, x1))

    'Port' describes a change of an object with before- and after- states x0, x1,
    where one of before- or after- states may be null (but not both).
    If x0 is null, the object is considered being created during the transaction.
    If x1 is null, the object is considered being deleted during the transaction.

Similarly, for an object from a subset of X:

    ∀ x0, x1 ∈ X | condition(x0, x1) ⊤(x0, x1)      transit((x0, x1) -> condition(x0, x1), (x0, x1) -> predicate(x0, x1))
    ∀ x0, x1 ∈ X | condition(x0, x1) ⊤(x0, x1)      port((x0, x1) -> condition(x0, x1), (x0, x1) -> predicate(x0, x1))

## Establishing Existence

An object of type X exists for which a predicate holds:

    ∃ x ∈ X ⊤(x)      exists(coll, x -> predicate(x))

    At least one element in the collection should satisfy the predicate.

An object of a subset of X exists for which a predicate holds:

    ∃ x ∈ X | condition(x) ⊤(x)      exists(coll, x -> condition(x), x -> predicate(x))

    At least one element in the collection should satisfy the predicate.
    Since iterating over a collection may be slow, we provide an option for more performant indexed approach next.

For performance, one can use an *index* instead of a collection to determine existence:

    ∃ x ∈ X | condition(x) ⊤(x)      exists(index, x -> predicate(x))

    The index is a Set-like structure (a Set or a dynamically updated index class which we provide)
    for fast iteration over collection elements that satisfy the condition. 

For any object of type X, an object of type Y exists for which that the predicate holds:

    ∀ x ∈ X ∃ y ∈ Y ⊤(x, y)      exists(collX, collY, (x, y) -> predicate(x, y)))

    For each x, there should be at least one object y that satisfies the predicate.

For performance, one can use an *join* of collections to determine the existence:

    ∀ x ∈ X ∃ y ∈ Y ⊤(x, y)      exists(join, (x, y) -> predicate(x, y)))

    The join is a Map-like structure (a Map or a dynamically updated join class which we provide) 
    for fast matching between collections of X and Y on some 'join' field. 

For any object of a subset of X, an object of type Y exists for which the predicate holds:

    ∀ x ∈ X | condition(x) ∃ y ∈ Y ⊤(x, y)      all(x -> condition(x), exists(index, (x, y) -> predicate(x, y)))

For any object of type X that has been changed, an object of type Y exists for which a predicate holds:

    ∀ x0, x1 ∈ X ∃ y ∈ Y ⊤(x0, x1, y)      transit((x0, x1) -> exists(index, (x0, x1, y) -> predicate(x0, x1, y))) 
    ∀ x0, x1 ∈ X ∃ y ∈ Y ⊤(x0, x1, y)      port((x0, x1) -> exists(index, (x0, x1, y) -> predicate(x0, x1, y)))

Same for only those x0, x1 which satisfy a condition:

    ∀ x0, x1 ∈ X | condition(x0, x1) ∃ y ∈ Y ⊤(y, x0, x1)      transit((x0, x1) -> condition(x0, x1), (x0, x1) -> exists(index, (x0, x1, y) -> predicate(x0, x1, y)))
    ∀ x0, x1 ∈ X | condition(x0, x1) ∃ y ∈ Y ⊤(y, x0, x1)      port((x0, x1) -> condition(x0, x1), (x0, x1) -> exists(index, (x0, x1, y) -> predicate(x0, x1, y)))

## Other Constraints
The library also allows defining constraints based on entity lifecycle and access events:

    create(x -> predicate(x)) # holds for any created object
    update(x -> predicate(x)) # holds for any updated object
    delete(x -> predicate(x)) # holds for any deleted object
    read(x -> predicate(x))  # holds for any read/loaded object
    write(x -> predicate(x)) # holds for any written/saved object
    (as well as variants with condition())

## Transaction stages and recovery

Invariants and effects default to validation at checkpoint or commit; intents default to immediate authorization.
Every applicable intent must pass, including parent-context and transaction-local restrictions. Express alternative
permissions inside one predicate with `||`.

Successful checkpoints keep reporting and immediate checks active and preserve begin-stage once-per-key history.
Failed checkpoints and commits make the handle rollback-only and detach string-ID event lookup. Explicit rollback
runs rollback callbacks; close releases resources without running them. Correct the application data and begin a fresh
transaction for another attempt. Previously performed application effects are not automatically undone.

Checks-only validation collects ordinary violations. When declared effects participate, checks and effects run in
order until the first violation, then all remaining evaluation stops. Unexpected failures and authorization denials
stop immediately. See the [usage guide](docs/Usage.md#checkpoints-authorization-and-effects) for details.

## Automatic transaction-local indexes

Declare a typed `IndexDefinition` in a context or transaction and access its independent contents through
`tr.index(definition)`. SingleIndex/MultiIndex support key lookups; SingleJoin/MultiJoin support relationships.
Read events enroll loaded objects by default; create/update/delete and before/after reports maintain the index.
Bulk-add methods seed data without generating read events. Uniform construction at begin does not force eager
population: multi-valued maintenance folds enrollment and extracts keys when needed; single-valued indexes preserve
replacement history. Earlier queries flush pending changes before returning.

Automatic maintenance and read enrollment have independent overrides at app/instance, context, transaction, and index
levels. Manual mode skips automatic routing and extraction. Contents use object identity, are isolated per handle,
and release on disposal. Shared persistent entity indexes remain outside this feature. See
[managed indexing](docs/Usage.md#managed-indexes) and the [runnable example](examples/night_city/Readme.md#managed-relationships).

## Library shutdown

`close()` stops new transactions, waits for active transactions to complete, and releases library resources. The default
wait has no time limit. Configure a finite timeout in code:

    ex.shutdownPolicy(ShutdownPolicy.graceful(Duration.ofSeconds(30)));
    ex.close();

or in `existential.properties`:

    shutdown.timeoutMillis=30000

Zero means unlimited waiting. If the wait times out or is interrupted, finish active transactions and call `close()`
again. Failed shutdown diagnostics include bounded operation/transaction metadata and known resource counts; they do
not claim to detect arbitrary application leaks. Shutdown does not run rollback callbacks for abandoned transactions. Do
not call `close()` from a transaction
callback; close the library after that transaction call returns.

## Performance

Since evaluating the constrains on each object change can degrade performance,
the library by default postpones evaluating to the end of business transaction.

To facilitate performance, the library:
- By default, evaluates rules at the end of a business transaction - such as
  before committing to persistent storage - rather than immediately on object change.
  The user can override this behaviour.
- During a transaction, the user sends 'events' into the library, notifying on object changes. 
  Only those objects participate in evaluation for which events have been issued.  
- Multiple events of same type issued for same object are 'folded' into a single event, reducing the number of
  evaluations.
- User can configure the rules globally or within the context of a specific business operation (for example, an API
  endpoint),
  thus reducing the number of rules to apply.

## Documentation

See /docs directory for further documentation.  
See [Usage.md](docs/Usage.md) for quick-start and usage patterns.
See [Night City](examples/night_city/Readme.md) for the example sources and specification-backed scenarios.
Contributors can start with the [project documentation](docs/dev/project/Readme.md).

## Troubleshooting

See [Troubleshooting.md](Troubleshooting.md) for solutions for common issues.

## License

See /License.md

## Compatibility

Breaking changes may happen before version 1.0.0; after 1.0.0 they require a version update.

## Support and Contributions

Existential is built for engineers. For a code-related support request, open an issue with a minimal, runnable example
and the complete output it produces, including the error and traceback where applicable. Explain the expected result
as well. Descriptions without a reproducible example cannot be diagnosed reliably.

Code-related issues also need an accompanying pull request that contains the proposed fix and a regression test.

## Community

- Contributing: /Contributing.md
- Conduct: /Conduct.md
- Support: /Support.md
- Security: /Security.md
