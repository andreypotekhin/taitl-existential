# Existential

Existential is a library allowing for use of certain math-like notations for describing application logic.
It implements two logic quantifiers, ∀ ("for any") and ∃ ("exists"), allowing to create logical
expressions about application entities (elements of the business domain), and to guarantee that such
expressions hold true. For instance, the library allows to create constraints on a class field,
on multiple fields, rules on how an object is allowed to change over time, govern relationships
between different entities. Performance is achieved by only evaluating the expressions at specific
points of time (transaction boundaries), disregarding any repeat changes that may be happening in between. 
Memory efficiency is achieved by using singleton objects for expressions and reuse expression objects where possible.

## Limitations

Existential is fully in-memory library, so its checks do not span beyond what was loaded as part of
a business operation/transaction.

## What it is not

Existential is a constraint library focused on defining and maintaining invariants between program entities.

Existential does not attempt to derive new truths from what's already known,
nor it is aimed to proofing or equation solving. It allows the user to declare 
certain truths about the program's entities, and guarantees a failure 
(such as an exception) in case these truths are violated.

This library is not aiming to implement any non-trivial set of mathematical logic notations. 
Rather, it provides a limited set of notations to allow for declarative reasoning about program
entities, and focuses on performance and memory efficiency.

## Formalisms

∀ == "For Any" (a universal quantification)  
∃ == "Exists" (an existential quantification)  
∈ == "Element of" (member of a set)  
| = "Such as" (a set comprehension)   
P == logical predicate (a boolean function)  
⊤ == "Truth", a logical predicate always rendering true  

In the further examples, the *new* keyword is dropped in front of All and Exists for brevity.
Java/Groovy must use *new* to create objects. Other languages (Kotlin, Go) can do without *new*. 

## Establishing Truth

For any object of type X, a predicate (boolean expression) is always true:

∀ x ∈ X ⊤(x)                                           All\<X\>(x -> *predicate(x)*)

Here, *predicate*() is any boolean function.

For any object of type X which satisfies a condition, a predicate is always true:

∀ x ∈ X | *condition(x)* ⊤(x)                     All\<X\>(x -> *condition(x), x* -> *predicate(x)*)

For any object of type X which has been changed in the course of a business transaction, the predicate is always true:

∀ x0 ∈ X0, x1 ∈ X1 ⊤(x0, x1)                 All\<Mutation\<X\>\>((x0, x1) -> *predicate(x0, x1)*)

Here, X0 is the data at the start of business transaction (initial state); X1 is the data at the end of transaction (result state).
x0 is entity's initial state, x1 is its final (before save) state.
*Mutation\<X\>* indicates that we are describing a change of an entity of type X. 
If Mutation is of type Transition, then one of x0, x1 may be null. 
In case of a Transition, if x0 value is null, it is an indication that x is a new object created in the course of the transaction. 
If x1 is null, it is an indication that x is the object that has been deleted/destroyed as part of the transaction.

Same as above when x0, x1 must also satisfy some condition:

∀ x0 ∈ X0, x1 ∈ X1 | *condition(x0, x1)* ⊤(x0, x1)  All\<Mutation\<X\>\>((x0, x1) -> *condition(x0, x1),* (x0, x1) -> *predicate(x0, x1)*)

## Establishing Existence

An object of type X exists for which a predicate is true:

∃ x ∈ X ⊤(x)                                              Exists\<X\>(coll, *predicate(x*))

Here *coll* is a collection of type X where we should look for objects to establish existence.
There should be at least one object in the collection which satisfies the predicate.
This scans through whole collection, so we'll describe more performant approach next.

For more efficiency, use an *index* to determine the existence: an object of type X of key k exists in an index where predicate is true:

∃ x ∈ X, ⊤(x)                                              Exists\<X\>(index, key(x), *predicate(x*))

Using an *index* with constant access times, instead of scanning through a collection, can help performance.

For any object of type X an object of type Y exists where predicate is true:

∀ x ∈ X ∃ y ∈ Y ⊤(x, y)                              All\<X\>(x -> Exists\<Y\>(coll, *predicate(x, y*)))

Here *coll* is a collection of type Y where should be at least one object that satisfies the predicate.
An index can also be benefitial here: All\<X\>(x -> Exists\<Y\>(index, key(y), *predicate(x, y)*))

For any object of type X which satisfies a condition an object of type Y exists for which predicate is true:

∀ x ∈ X | *condition(x)* ∃ y ∈ Y ⊤(x, y)         All\<X\>(x -> *condition(x)*, x -> Exists\<Y\>(coll, *predicate(x, y*)))

For any object of type X which has been changed, an object of type Y exists for which a predicate is true
(X0 and X1 stand for 'before' and 'after' states, as described above):

∀ x0 ∈ X0, x1 ∈ X1 ∃ y ∈ Y ⊤(x0, x1, y)      All\<Mutation\<X\>\>((x0, x1) -> Exists\<Y\>(coll, *predicate(x0, x1, y*)))

Same as above when x0, x1 must also satisfy some condition:

∀ x0 ∈ X0, x1 ∈ X1 | *condition(x0, x1)* ∃ y ∈ Y ⊤(y, x0, x1)   All\<Mutation\<X\>\>((x0, x1) -> *condition(x0, x1),* (x0, x1) -> Exists\<Y\>(coll, *predicate(p, x0, x1*)))