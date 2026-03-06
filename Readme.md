# Existential

Existential is a constraint library for expressing and enforcing invariants between program entities.

It provides a small set of math-inspired notations for describing application logic. Existential
implements two logic quantifiers, ∀ ("for any") and ∃ ("exists"), allowing you to create logical
expressions about application entities (elements of the business domain) and to guarantee those
expressions hold. For instance, the library lets you constrain a single field, multiple fields,
or how an object may change over time.
Performance comes from evaluating expressions only at transaction boundaries, collapsing repeated changes
between those points into a single change. Memory efficiency comes from reusing singleton expression instances
when possible.

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
- The '.invariant(X.class).' part is omitted in front of all() and exists() method calls, for brevity.

## Establishing Truth

For any object of type X, a predicate holds true:

    ∀ x ∈ X ⊤(x)      all(x -> predicate(x))

    Here, predicate() is any boolean function.

For any object of type X which satisfies a condition, a predicate holds true:

    ∀ x ∈ X | condition(x) ⊤(x)      all(x -> condition(x), x -> predicate(x))

For any object of type X that has been changed in the course of a business transaction, predicate holds true:

    ∀ x0, x1 ∈ X, ⊤(x0, x1)      transit((x0, x1) -> predicate(x0, x1))
    
    Transit describes a change where both states are non-null.
    x0 is the entity's initial state at the start of the transaction,
    x1 is its final state at the end of the transaction.

For any object of type X that has been created, changed, or deleted in the course of a business transaction,
predicate holds true (use Porting when one side may be null):

    ∀ x0, x1 ∈ X, ⊤(x0, x1)      port((x0, x1) -> predicate(x0, x1))

    Port describes a change where one of x0 or x1 may be null (but not both).
    If x0 is null, the entity considered being created during the transaction.
    If x1 is null, the entity considered being deleted during the transaction.

Same as above when x0, x1 must also satisfy some condition:

    ∀ x0, x1 ∈ X | condition(x0, x1) ⊤(x0, x1)      transit((x0, x1) -> condition(x0, x1), (x0, x1) -> predicate(x0, x1))
    ∀ x0, x1 ∈ X | condition(x0, x1) ⊤(x0, x1)      port((x0, x1) -> condition(x0, x1), (x0, x1) -> predicate(x0, x1))

## Establishing Existence

An object of type X exists for which a predicate holds:

    ∃ x ∈ X ⊤(x)      exists(coll, predicate(x))
    
    This guarantees that at least one element in the collection satisfies the predicate.
    Since this evaluation may be slow (a linear scan), we'll show a more performant approach next.

For more efficiency, use an *index* to determine the existence:

    ∃ x ∈ X, ⊤(x)      exists(index, predicate(x))
    
    Here, index is a data structure (which we provide) allowing fast 
    (constant) access time to the underlying collection. 
    Using an index can greatly improve performance.

For any object of type X, an object of type Y exists such that the predicate holds:

    ∀ x ∈ X ∃ y ∈ Y ⊤(x, y)      exists(index, predicate(x, y)))
    
    This guarantees that for each x, there is at least one object y that satisfies the predicate.
    Here, index is a data structure (which we provide) mapping X to Y with fast (constant) access time.

For any object of type X that satisfies a condition, an object of type Y exists for which the predicate holds:

    ∀ x ∈ X | condition(x) ∃ y ∈ Y ⊤(x, y)      all(x -> condition(x), exists(index, predicate(x, y)))

For any object of type X that has been changed, an object of type Y exists for which a predicate holds:

    ∀ x0, x1 ∈ X ∃ y ∈ Y ⊤(x0, x1, y)      transit((x0, x1) -> exists(index, predicate(x0, x1, y))) 
    ∀ x0, x1 ∈ X ∃ y ∈ Y ⊤(x0, x1, y)      port((x0, x1) -> exists(index, predicate(x0, x1, y)))

Same when x0, x1 must also satisfy some condition:

    ∀ x0, x1 ∈ X | condition(x0, x1) ∃ y ∈ Y ⊤(y, x0, x1)      transit((x0, x1) -> condition(x0, x1), (x0, x1) -> exists(index, predicate(x0, x1, y)))
    ∀ x0, x1 ∈ X | condition(x0, x1) ∃ y ∈ Y ⊤(y, x0, x1)      port((x0, x1) -> condition(x0, x1), (x0, x1) -> exists(index, predicate(x0, x1, y)))

## Other Constraints
The library also allows to configure other constraints on application entities, such as:

    create(x -> predicate(x)) # holds for any created object
    update(x -> predicate(x)) # holds for any updated object
    delete(x -> predicate(x)) # holds for any deleted object
    read(x -> predicate(x)) # holds for any read/loaded object
    write(x -> predicate(x)) # holds for any written/saved object
    (and the like)

## Performance

Evaluating the rules such as the above on a bigger collection can take much time, especially if evaluations 
happen on each element change.

To facilitate performance, the library:
- Avoids immediate rule evaluation and instead evaluates rules at the end of a business transaction, such as
  before committing the changed data to persistent storage.
- During a transaction, the user sends 'events' into the library, notifying on object changes.
- Multiple events of the same object are folded into a single event, reducing the number of performed validations.
- User can configure rules globally or within the context of a specific business operation (for example, an API endpoint),
reducing the number of evaluated rules.

## Documentation

See /docs directory for further documentation.  
See /docs/Usage.md for quick-start and usage patterns.

## Troubleshooting

See /Troubleshooting.md for solutions for common issues.

## License

See /License.md

## Community

- Contributing: /Contributing.md
- Conduct: /Conduct.md
- Support: /Support.md
- Security: /Security.md
