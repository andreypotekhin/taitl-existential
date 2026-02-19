# Existential

Existential is a constraint library that lets you define and maintain invariants between program entities.

Existential is a library that enables certain math-like notations for describing application logic.
It implements two logic quantifiers, ∀ ("for any") and ∃ ("exists"), allowing you to create logical
expressions about application entities (elements of the business domain), and to guarantee that such
expressions hold true. For instance, the library allows you to create constraints on a class field,
multiple fields, and rules that describe how an object may change over time.
Performance is achieved by evaluating expressions only at specific
points in time (transaction boundaries), treating repeated changes between those points as a single change.
Memory efficiency is achieved by using singleton objects for expressions and reusing expression objects where possible.

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
P == logical predicate (a boolean function)  
⊤ == "Truth", a logical predicate always rendering true

Note: in the examples below, the *new* keyword is dropped in front of All and Exists for brevity.
Java/Groovy must use *new*. Other languages (Kotlin, Go) may omit *new*.

## Establishing Truth

For any object of type X, a predicate holds true:

    ∀ x ∈ X ⊤(x)      All<X>(x -> predicate(x))

    Here, predicate() is any boolean function.

For any object of type X which satisfies a condition, a predicate holds true:

    ∀ x ∈ X | condition(x) ⊤(x)      All<X>(x -> condition(x), x -> predicate(x))

For any object of type X which has been changed in the course of a business transaction, predicate holds true:

    ∀ x0, x1 ∈ X, ⊤(x0, x1)      All<Mutation<X>>((x0, x1) -> predicate(x0, x1))
    
    Mutation<X> indicates that we are describing a change of an entity of type X. 
    x0 is the entity's initial state - the state at the start of a business transaction
    x1 is its final state at the end of transaction.

For any object of type X which has been created, changed or deleted in the course of a business transaction, predicate holds true:

    ∀ x0, x1 ∈ X, ⊤(x0, x1)      All<Transition<X>>((x0, x1) -> predicate(x0, x1))

    Transition<X> indicates that we are describing a change of an entity where one of x0, x1, but not both, may be null.
    If x0 is null, it indicates that x is a new object created in the course of the transaction.
    If x1 is null, it indicates that x is the object that has been deleted as part of the transaction.

Same as above when x0, x1 must also satisfy some condition:

    ∀ x0, x1 ∈ X | condition(x0, x1) ⊤(x0, x1)      All<Mutation<X>>((x0, x1) -> condition(x0, x1), (x0, x1) -> predicate(x0, x1))
    ∀ x0, x1 ∈ X | condition(x0, x1) ⊤(x0, x1)      All<Transition<X>>((x0, x1) -> condition(x0, x1), (x0, x1) -> predicate(x0, x1))

## Establishing Existence

An object of type X exists for which a predicate holds:

    ∃ x ∈ X ⊤(x)      Exists<X>(coll, predicate(x))
    
    Here, coll is a collection where we should look for objects to establish existence.
    There should be at least one object in the collection which satisfies the predicate.

This scans through the whole collection, so a more performant approach is shown next.
For more efficiency, use an *index* to determine the existence:

    ∃ x ∈ X, ⊤(x)      Exists<X>(index, key(x), predicate(x))
    
    Using an index with constant access time can greatly improve performance.

For any object of type X, an object of type Y exists such that the predicate holds:

    ∀ x ∈ X ∃ y ∈ Y ⊤(x, y)      All<X>(x -> Exists<Y>(index, key(x), predicate(x, y)))
    
    Guarantees that there is at least one object y that satisfies the predicate.

For any object of type X that satisfies a condition, an object of type Y exists for which the predicate holds:

    ∀ x ∈ X | condition(x)* ∃ y ∈ Y ⊤(x, y)      All<X>(x -> condition(x), x -> Exists<Y>(index, key(x), predicate(x, y)))

For any object of type X that has been changed, an object of type Y exists for which a predicate holds:

    ∀ x0, x1 ∈ X ∃ y ∈ Y ⊤(x0, x1, y)      All<Mutation<X>>((x0, x1) -> Exists<Y>(index, key(y), predicate(x0, x1, y))) 
    ∀ x0, x1 ∈ X ∃ y ∈ Y ⊤(x0, x1, y)      All<Transition<X>>((x0, x1) -> Exists<Y>(index, key(y), predicate(x0, x1, y)))

Same when x0, x1 must also satisfy some condition:

    ∀ x0, x1 ∈ X | condition(x0, x1) ∃ y ∈ Y ⊤(y, x0, x1)      All<Mutation<X>>((x0, x1) -> condition(x0, x1), (x0, x1) -> Exists<Y>(index, key(y), predicate(p, x0, x1)))
    ∀ x0, x1 ∈ X | condition(x0, x1) ∃ y ∈ Y ⊤(y, x0, x1)      All<Transition<X>>((x0, x1) -> condition(x0, x1), (x0, x1) -> Exists<Y>(index, key(y), predicate(p, x0, x1)))

## Documentation

See /docs directory for further documentation.
