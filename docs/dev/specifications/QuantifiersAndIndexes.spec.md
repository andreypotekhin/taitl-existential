# Quantifiers and Indexes Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
Requirement identifiers are stable. The claims describe library behavior, not legal commitments.

See the paired [design](../design/QuantifiersAndIndexes.design.md) and [specification index](../Specification.md).

## Universal Quantifier

<a id="qi-001"></a>

**QI-001** User can create an invariant for an entity class using the 'All' quantifier.

<a id="qi-002"></a>

**QI-002** User can restrict the 'All' quantifier to only apply to certain entities, by specifying a condition.

<a id="qi-003"></a>

**QI-003** User can define the 'All' quantifier on an entity class.

<a id="qi-004"></a>

**QI-004** User can define the 'All' quantifier on an entity transition class (Transition<T>).

## Existence Quantifier

<a id="qi-005"></a>

**QI-005** User can create an invariant on an entity class using the 'Exists' quantifier.

<a id="qi-006"></a>

**QI-006** User can require entity existence when certain condition is met (same class, within entity class code).

<a id="qi-007"></a>

**QI-007** User can require entity existence when certain condition is met (different classes, outside entity class
code).

<a id="qi-008"></a>

**QI-008** User can specify 'Exists' quantifier as a parameter to the 'All' quantifier, thus creating an All-Exists
invariant.

<a id="qi-009"></a>

**QI-009** User can define the 'Exists' quantifier on a collection.

<a id="qi-010"></a>

**QI-010** User can define the 'Exists' quantifier on a map (index).

## Indexes

<a id="qi-011"></a>

**QI-011** User can use an index to speed up evaluation of Exists expression.
  We use SetIndex and JoinIndex classes for this

<a id="qi-012"></a>

**QI-012** User can create an index to pass information between the rules.
  We use Index and JoinIndex classes for this

## Internal indexing

<a id="qi-013"></a>

**QI-013** System automatically finalizes configuration for op upon start of a transaction.

<a id="qi-014"></a>

**QI-014** System indexes configuration rules per op when configuration is finalized.
Configuration indexes are owned by Config object for business op.

<a id="qi-015"></a>

**QI-015** System indexes the received events at runtime.
The runtime indexes are owned by Tr object.
Evaluation runs (e.g. at Validation state) use configuration and runtime indexes for performance.
