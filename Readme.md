# Existential

Existential is a constraint library for expressing and enforcing invariants between program entities.

It enables a small set of math-inspired notations for describing application logic. Existential
implements two logic quantifiers, ∀ ("for any") and ∃ ("exists"), allowing you to create logical
expressions about application entities (elements of the business domain) and to guarantee those
expressions hold. For instance, the library lets you constrain a single field, multiple fields,
or how an object may change over time.
Performance is achieved by evaluating expressions only at specific
points in time (transaction boundaries), treating repeated changes between those points as a single change.
Memory efficiency comes from reusing singleton expression instances where possible.

## Limitations

Existential is an in-memory library, so its checks do not span beyond what is loaded as part of
a business operation.

## What it is not

Existential does not attempt to derive new truths from what's already known,
nor is it aimed at proving or equation solving. It allows the user to declare
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
- The *new* keyword is dropped in front of All and Exists for brevity. Java/Groovy must use *new*. Other JVM languages (Kotlin/Scala) may omit *new*.
- Math notation is on the left, and corresponding Java notation is on the right.

## Establishing Truth

For any object of type X, a predicate holds true:

    ∀ x ∈ X ⊤(x)      All<X>(x -> predicate(x))

    Here, predicate() is any boolean function.

For any object of type X which satisfies a condition, a predicate holds true:

    ∀ x ∈ X | condition(x) ⊤(x)      All<X>(x -> condition(x), x -> predicate(x))

For any object of type X that has been changed in the course of a business transaction, predicate holds true
(use Mutation when both before and after states are present):

    ∀ x0, x1 ∈ X, ⊤(x0, x1)      All<Mutation<X>>((x0, x1) -> predicate(x0, x1))
    
    Mutation<X> describes a change where both states are non-null.
    x0 is the entity's initial state at the start of the transaction,
    x1 is its final state at the end of the transaction.

For any object of type X that has been created, changed, or deleted in the course of a business transaction,
predicate holds true (use Transition when one side may be null):

    ∀ x0, x1 ∈ X, ⊤(x0, x1)      All<Transition<X>>((x0, x1) -> predicate(x0, x1))

    Transition<X> describes a change where exactly one of x0 or x1 may be null.
    If x0 is null, the entity was created during the transaction.
    If x1 is null, the entity was deleted during the transaction.

Same as above when x0, x1 must also satisfy some condition:

    ∀ x0, x1 ∈ X | condition(x0, x1) ⊤(x0, x1)      All<Mutation<X>>((x0, x1) -> condition(x0, x1), (x0, x1) -> predicate(x0, x1))
    ∀ x0, x1 ∈ X | condition(x0, x1) ⊤(x0, x1)      All<Transition<X>>((x0, x1) -> condition(x0, x1), (x0, x1) -> predicate(x0, x1))

## Establishing Existence

An object of type X exists for which a predicate holds:

    ∃ x ∈ X ⊤(x)      Exists<X>(coll, predicate(x))
    
    Here, coll is a collection where we should look for objects to establish existence.
    The Exists expression guarantees that there is at least one object in the collection which satisfies the predicate.
    This scans through the whole collection, so a more performant approach is shown next.

For more efficiency, use an *index* to determine the existence:

    ∃ x ∈ X, ⊤(x)      Exists<X>(index, key(x), predicate(x))
    
    Using an index with constant access time can greatly improve performance.

For any object of type X, an object of type Y exists such that the predicate holds:

    ∀ x ∈ X ∃ y ∈ Y ⊤(x, y)      All<X>(x -> Exists<Y>(index, key(x), predicate(x, y)))
    
    Guarantees that there is at least one object y that satisfies the predicate.

For any object of type X that satisfies a condition, an object of type Y exists for which the predicate holds:

    ∀ x ∈ X | condition(x) ∃ y ∈ Y ⊤(x, y)      All<X>(x -> condition(x), x -> Exists<Y>(index, key(x), predicate(x, y)))

For any object of type X that has been changed, an object of type Y exists for which a predicate holds:

    ∀ x0, x1 ∈ X ∃ y ∈ Y ⊤(x0, x1, y)      All<Mutation<X>>((x0, x1) -> Exists<Y>(index, key(y), predicate(x0, x1, y))) 
    ∀ x0, x1 ∈ X ∃ y ∈ Y ⊤(x0, x1, y)      All<Transition<X>>((x0, x1) -> Exists<Y>(index, key(y), predicate(x0, x1, y)))

Same when x0, x1 must also satisfy some condition:

    ∀ x0, x1 ∈ X | condition(x0, x1) ∃ y ∈ Y ⊤(y, x0, x1)      All<Mutation<X>>((x0, x1) -> condition(x0, x1), (x0, x1) -> Exists<Y>(index, key(y), predicate(x0, x1, y)))
    ∀ x0, x1 ∈ X | condition(x0, x1) ∃ y ∈ Y ⊤(y, x0, x1)      All<Transition<X>>((x0, x1) -> condition(x0, x1), (x0, x1) -> Exists<Y>(index, key(y), predicate(x0, x1, y)))

## Documentation

See /docs directory for further documentation.
See /Troubleshooting.md for common setup and runtime failures.

## Type Keys

When multiple entity classes share the same short name across packages, use fully-qualified type keys:
`TypeKey.valueOfFull(MyEntity.class)` or `TypeKey.valueOfFull(MyEntity.class, "Qualifier")`.
For library-inferred keys (for example `event(entity, tranID)` overloads), enable
`Flags.BEHAVIOR_TYPE_KEYS_USE_FULL_CLASS_NAMES` to switch inference to fully-qualified class names.

For generic type capture, use the anonymous subclass pattern:
`new TypeKey<List<Order>>() {}`.

Troubleshooting: `/Troubleshooting.md#type-key-format`

## License

Licensed under the Apache License, Version 2.0. See /LICENSE for details.

## Community

- Contributing: /CONTRIBUTING.md
- Code of Conduct: /CODE_OF_CONDUCT.md
- Support: /SUPPORT.md
- Security: /SECURITY.md
