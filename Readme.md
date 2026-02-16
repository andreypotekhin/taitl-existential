# Existential

Existential is a constraint library allowing to define and maintain invariants between program entities.

Existential is a library allowing for use of certain math-like notations for describing application logic.
It implements two logic quantifiers, ∀ ("for any") and ∃ ("exists"), allowing to create logical
expressions about application entities (elements of the business domain), and to guarantee that such
expressions hold true. For instance, the library allows to create constraints on a class field,
multiple fields, rules on how an object is allowed to change over time.
Performance is achieved by only evaluating the expressions at specific
points in time (transaction boundaries), treating repeat changes between those points as single change. 
Memory efficiency is achieved by using singleton objects for expressions and reusing expression objects where possible.

## Limitations

Existential is an in-memory library, so its checks do not span beyond what was loaded as part of
a business operation.

## What it is not

Existential does not attempt to derive new truths from what's already known,
nor it is aimed to proofing or equation solving. It allows the user to declare 
certain truths about program's entities, and guarantees a failure, 
such as an exception, in case these truths are violated.

The library does not aim to implement any 'complete' set of mathematical logic notations. 
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
Java/Groovy must use *new*. Other languages (Kotlin, Go) can do without *new*. 

## Establishing Truth

For any object of type X, a predicate is always true:

∀ x ∈ X ⊤(x)                                           All\<X\>(x -> *predicate(x)*)

Here, *predicate*() is any boolean function.

For any object of type X which satisfies a condition, a predicate is always true:

∀ x ∈ X | *condition(x)* ⊤(x)                     All\<X\>(x -> *condition(x), x* -> *predicate(x)*)

For any object of type X which has been changed in the course of a business transaction, predicate is always true:

∀ x0, x1 ∈ X, ⊤(x0, x1)                 All\<Mutation\<X\>\>((x0, x1) -> *predicate(x0, x1)*)

Here, x0 is entity initial state - at the start of business transaction; 
x1 is its final (before save) state - at the end of transaction.
*Mutation\<X\>* indicates that we are describing a change of an entity of type X.

∀ x0, x1 ∈ X, ⊤(x0, x1)                 All\<Transition\<X\>\>((x0, x1) -> *predicate(x0, x1)*)

Same as above, but with Transition we are describing a change of an entity of type X
where one of x0, x1 may be null (but not both). 
If x0  is null, it is an indication that x is a new object created in the course of transaction. 
If x1 is null, it is an indication that x is the object that has been deleted as part of transaction.

Same as above when x0, x1 must also satisfy some condition:

∀ x0, x1 ∈ X | *condition(x0, x1)* ⊤(x0, x1)  All\<Mutation\<X\>\>((x0, x1) -> *condition(x0, x1),* (x0, x1) -> *predicate(x0, x1)*)
∀ x0, x1 ∈ X | *condition(x0, x1)* ⊤(x0, x1)  All\<Transition\<X\>\>((x0, x1) -> *condition(x0, x1),* (x0, x1) -> *predicate(x0, x1)*)

## Establishing Existence

An object of type X exists for which a predicate holds:

∃ x ∈ X ⊤(x)                                              Exists\<X\>(coll, *predicate(x*))

Here *coll* is a collection where we should look for objects to establish existence.
There should be at least one object in the collection which satisfies the predicate.
This scans through whole collection, so we'll describe a more performant approach next.
For more efficiency, use an *index* to determine the existence:

∃ x ∈ X, ⊤(x)                                              Exists\<X\>(index, key(x), *predicate(x*))

Using an *index* with constant access times can greatly help performance.

For any object of type X an object of type Y exists such that predicate holds:

∀ x ∈ X ∃ y ∈ Y ⊤(x, y)                              All\<X\>(x -> Exists\<Y\>(coll, *predicate(x, y*)))

This guarantees that in the *coll*, there is at least one object y that satisfies the predicate.

Same with index: All\<X\>(x -> Exists\<Y\>(index, key(y), *predicate(x, y)*))

For any object of type X which satisfies a condition, an object of type Y exists for which predicate holds:

∀ x ∈ X | *condition(x)* ∃ y ∈ Y ⊤(x, y)         All\<X\>(x -> *condition(x)*, x -> Exists\<Y\>(coll, *predicate(x, y*)))

For any object of type X which has been changed, an object of type Y exists for which a predicate holds
(X0 and X1 stand for 'before' and 'after' states):

∀ x0, x1 ∈ X ∃ y ∈ Y ⊤(x0, x1, y)      All\<Mutation\<X\>\>((x0, x1) -> Exists\<Y\>(coll, *predicate(x0, x1, y*)))
∀ x0, x1 ∈ X ∃ y ∈ Y ⊤(x0, x1, y)      All\<Mutation\<X\>\>((x0, x1) -> Exists\<Y\>(index, key(y), *predicate(x0, x1, y*)))

Same when x0, x1 must also satisfy some condition:

∀ x0, x1 ∈ X | *condition(x0, x1)* ∃ y ∈ Y ⊤(y, x0, x1)   All\<Mutation\<X\>\>((x0, x1) -> *condition(x0, x1),* (x0, x1) -> Exists\<Y\>(coll, *predicate(p, x0, x1*)))
∀ x0, x1 ∈ X | *condition(x0, x1)* ∃ y ∈ Y ⊤(y, x0, x1)   All\<Mutation\<X\>\>((x0, x1) -> *condition(x0, x1),* (x0, x1) -> Exists\<Y\>(index, key(y), *predicate(p, x0, x1*)))

## Documentation
See /docs directory for further documentation.