# Existential

Existential is a library allowing for use certain math-like notations to describe application logic. It implements two commonly used first-order logic quantifiers, ∀ ("for any") and ∃ ("exists"), allowing to create expressions about application entities (elements of the domain), and to guarantee that such expressions always hold true (propositions). For instance, the library allows to create constraints on a class field, constrains spanning multiple fields, rules for how object is allowed to change over time, and also govern relationships between entities of different types. Performance is achieved by only evaluating the expressions at specific points in time (and code), such as a transaction boundary, disregarding any repeat changes that may be happening to the entities between such points. Memory efficiency is achieved by using static and singleton objects where possible for storing the expressions, which lets reusing expression objects for multiple entities.

Existential does not aim to provide means for derivation of new truths from what is already established; it is not aimed to automated proofing of theorems, nor for solving logical equations. It lets establish certain truths about program entities, and guarantees program failure (such as an exception) in case these truths are violated. It can be said that Existential allows for establishing a set of axioms about application entities.

## What it is not

Existential does not aim to provide means for derivation of new truths from what is already established; it is not aimed to automated proofing of theorems, nor for solving logical equations. It lets establish certain truths about program entities, and guarantees program failure (such as an exception) in case these truths are violated. It can be said that Existential allows for establishing a set of rules about application entities.

## Out of Scope

This library is not aiming to implement any non-trivial set of math logic notations. Rather, it implements a limited set of notations to provide expression language for reasoning about application entities, while focusing on performance and memory efficiency.

## Formalisms

∀ == "For Any" (aka universal quantification) 
∃ == "Exists" (aka existential quantification) 
∈ == "Element of" (member of a set) 
| = "Such as" (aka set comprehension) 
P == logical predicate (boolean expression) 
⊤ == "Truth", a logical predicate (boolean expression) always rendering true 
In the further examples, the *new* keyword is dropped in front of Any for brevity. Java/Groovy must use new to create objects. Other languages (Kotlin, Go) can do without *new*. 

## Establishing Truth

For any object of type X, a predicate (boolean expression) is always true

∀ x ∈ X ⊤(x)                                           **All<X>(x -> *predicate(x)*)

Here, *predicate*() is any boolean function, for example (x != null), x.hasParent() and the like.

For any object of type X which satisfies some condition, a predicate is always true

∀ x ∈ X | *condition(x)* ⊤(x)                     **All<X>(x -> *condition(x), x* -> *predicate(x)*)

For any object of type X which has been changed during business transaction, the predicate is always true

∀ x0 ∈ X0, x1 ∈ X1 ⊤(x0, x1)                 **All<Mutation<X>>((x0, x1) -> *predicate(x0, x1)*)

Here, X0 is the data at the start of business transaction (initial state); X1 is the data at the end of transaction (result state). x0 is entity's initial state, x1 is its final (before save) state. Mutation<X> indicates that we are describing a change of an entity of type X. If Mutation is of type Transition, then one of x0, x1 may be null. In such case, if x0 is null, this indicates x is a new object created as the part of transaction. If x1 is null, it indicates x is the object being deleted as part of transaction.

Same as above when x0, x1 must also satisfy some condition:

∀ x0 ∈ X0, x1 ∈ X1 | *condition(x0, x1)* ⊤(x0, x1) ** All<Mutation<X>>((x0, x1) -> *condition(x0, x1),* (x0, x1) -> *predicate(x0, x1)*)

## Establishing Existence

An object of type X exists for which a predicate is true

∃ x ∈ X ⊤(x)                                              Exists<X>(coll, *predicate(x*))

Here *coll* is a collection of type X where we should look for objects to establish existence. There should be at least one object in this collection which satisfies the predicate. This scans through whole collection, so we'll describe more performant approach next.

More efficiently, use an *index* to determine existence: an object of type X of key k exists in an index where predicate is true

∃ x ∈ X, ⊤(x)                                              Exists<X>(index, key(x), *predicate(x*))

This uses an *index* for performance, instead of scanning through collection.

For any object of type X an object of type Y exists where predicate is true

∀ x ∈ X ∃ y ∈ Y ⊤(y, x)                              All<X>(x -> new Exists<Y>(coll, *predicate(y, x*)))

Here c*oll* here is a collection of type Y where we should look to find at least one object that satisfies the predicate.

For any object of type X which satisifies a condition an object of type Y exists for which predicate is true

∀ x ∈ X | *condition(x)* ∃ y ∈ Y ⊤(y, x)         All<X>(x -> *condition(x), x* -> new Exists<Y>(coll, *predicate(y, x*)))

For any object of type X which has been changed, an object of type Y exists for which predicate is true

∀ x0 ∈ X0, x1 ∈ X1 ∃ y ∈ Y ⊤(y, t0, t1)      All<Mutation<X>((x0, x1) -> new Exists<Y>(coll, *predicate(y, x0, x1*)))

Same as above when x0, x1 must also satisfy some condition

∀ x0 ∈ X0, x1 ∈ X1 | *condition(x0, x1)* ∃ y ∈ Y ⊤(y, x0, x1)   All<Mutation<X>((x0, x1) -> *condition(x0, x1),* (x0, x1) -> new Exists<Y>(coll, *predicate(p, x0, x1*)))