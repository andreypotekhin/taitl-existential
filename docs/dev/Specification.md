# Library Specification Index

The normative claims now live in focused [specifications](specifications/). Each claim has one authoritative location
and a descriptive heading. Existing `+` marks were preserved; they do not replace current test and release evidence. The
[traceability matrix](project/TraceabilityMatrix.md) records scope, evidence, gaps, and delivery order.

## Topic documents

- [Library and Configuration] · [Design][LibraryAndConfiguration.design.md]
- [Transactions and Evaluation] · [Design][TransactionsAndEvaluation.design.md]
- [Events and Constraints] · [Design][EventsAndConstraints.design.md]
- [Runtime Execution Plans](specifications/ExecutionPlans.spec.md) · [Design](design/ExecutionPlans.design.md)
- [Quantifiers and Indexes] · [Design][QuantifiersAndIndexes.design.md]
- [Intents and Effects](specifications/IntentsAndEffects.spec.md) · [Design](design/IntentsAndEffects.design.md)
- [Housekeeping](specifications/Housekeeping.spec.md) · [Design](design/Housekeeping.design.md)
- [Distribution](specifications/Distribution.spec.md) · [Design](design/Distribution.design.md)

The sections below retain the former document anchors for existing links. New links should target topic documents.

### Terminology

See [Terminology.md](Terminology.md).

### Library

See [Library](specifications/LibraryAndConfiguration.spec.md#library).

#### Library configuration

See [Library configuration](specifications/LibraryAndConfiguration.spec.md#library-configuration).

#### Library usage

See [Library usage](specifications/TransactionsAndEvaluation.spec.md#library-usage).

#### Library lifecycle phases

See [Library lifecycle phases](specifications/TransactionsAndEvaluation.spec.md#library-lifecycle-phases).

### Workflows

Use the topic links above and the [release roadmap](project/Roadmap.md).

#### Configuration workflow

Use the topic links above and the [release roadmap](project/Roadmap.md).

##### Configuring contexts

See [Configuring contexts](specifications/LibraryAndConfiguration.spec.md#configuring-contexts).

##### Wildcard contexts

See [Wildcard contexts](specifications/LibraryAndConfiguration.spec.md#wildcard-contexts).

##### Configuring transactions

See [Configuring transactions](specifications/TransactionsAndEvaluation.spec.md#configuring-transactions).

##### Configuring constraints

See [Configuring constraints](specifications/LibraryAndConfiguration.spec.md#configuring-constraints).

##### Configuring effects

See [Configuring effects](specifications/IntentsAndEffects.spec.md#configuring-effects).

##### Configuring intents

See [Configuring intents](specifications/IntentsAndEffects.spec.md#configuring-intents).

##### Execution stages

See [Execution stages](specifications/TransactionsAndEvaluation.spec.md#execution-stages).

##### Configuring Custom Events

See [Configuring Custom Events](specifications/EventsAndConstraints.spec.md#configuring-custom-events).

#### Evaluation workflow

See [Evaluation workflow](specifications/TransactionsAndEvaluation.spec.md#evaluation-workflow).

##### Preconditions evaluation

See [Preconditions evaluation](specifications/TransactionsAndEvaluation.spec.md#preconditions-evaluation).

##### Immediate evaluation

See [Immediate evaluation](specifications/TransactionsAndEvaluation.spec.md#immediate-evaluation).

##### Validation evaluation

See [Validation evaluation](specifications/TransactionsAndEvaluation.spec.md#validation-evaluation).

## Concepts

Use the topic links above and the [release roadmap](project/Roadmap.md).

### Configs

See [Configs](specifications/LibraryAndConfiguration.spec.md#configs).

### Contexts

See [Contexts](specifications/LibraryAndConfiguration.spec.md#contexts).

### Events

See [Events](specifications/EventsAndConstraints.spec.md#events).

### Constraints

See [Constraints](specifications/EventsAndConstraints.spec.md#constraints).

##### Constraint on a single entity

See [Constraint on a single entity](specifications/EventsAndConstraints.spec.md#constraint-on-a-single-entity).

##### Constraints on multiple entities

See [Constraints on multiple entities](specifications/EventsAndConstraints.spec.md#constraints-on-multiple-entities).

##### Constraints on entity evolution

See [Constraints on entity evolution](specifications/EventsAndConstraints.spec.md#constraints-on-entity-evolution).

##### Constraints on evolution of multiple entities

See [Constraints on evolution of multiple entities].

##### Constraints on subclasses

See [Constraints on subclasses](specifications/EventsAndConstraints.spec.md#constraints-on-subclasses).

##### Constraint violation reporting

See [Constraint violation reporting](specifications/EventsAndConstraints.spec.md#constraint-violation-reporting).

### Quantifiers

See [quantifiers and indexes](specifications/QuantifiersAndIndexes.spec.md).

#### Universal Quantifier

See [Universal Quantifier](specifications/QuantifiersAndIndexes.spec.md#universal-quantifier).

#### Existence Quantifier

See [Existence Quantifier](specifications/QuantifiersAndIndexes.spec.md#existence-quantifier).

#### Indexes

See [Indexes](specifications/QuantifiersAndIndexes.spec.md#indexes).

### Effects

See [Effects](specifications/IntentsAndEffects.spec.md#effects).

### Intents

See [Intents](specifications/IntentsAndEffects.spec.md#intents).

#### Custom Event Types

See [Custom Event Types](specifications/EventsAndConstraints.spec.md#custom-event-types).

### Housekeeping

See [housekeeping](specifications/Housekeeping.spec.md).

#### Caching

See [Caching](specifications/Housekeeping.spec.md#caching).

#### Transaction Cleanup

See [Transaction Cleanup](specifications/Housekeeping.spec.md#transaction-cleanup).

#### Global Cleanup

See [Global Cleanup](specifications/Housekeeping.spec.md#global-cleanup).

### Maven

See [Maven](specifications/Distribution.spec.md#maven).

### Implementation

Use the topic links above and the [release roadmap](project/Roadmap.md).

#### Public interface

Use the topic links above and the [release roadmap](project/Roadmap.md).

##### Public classes

See [Public classes](specifications/LibraryAndConfiguration.spec.md#public-classes).

##### Internal indexing

See [Internal indexing](specifications/QuantifiersAndIndexes.spec.md#internal-indexing).

### Appendix

No additional claims were defined here in the original specification.

### Out of scope / Not used

No additional claims were defined here in the original specification.

[Library and Configuration]: specifications/LibraryAndConfiguration.spec.md
[LibraryAndConfiguration.design.md]: design/LibraryAndConfiguration.design.md
[Transactions and Evaluation]: specifications/TransactionsAndEvaluation.spec.md
[TransactionsAndEvaluation.design.md]: design/TransactionsAndEvaluation.design.md
[Events and Constraints]: specifications/EventsAndConstraints.spec.md
[EventsAndConstraints.design.md]: design/EventsAndConstraints.design.md
[Quantifiers and Indexes]: specifications/QuantifiersAndIndexes.spec.md
[QuantifiersAndIndexes.design.md]: design/QuantifiersAndIndexes.design.md
[Constraints on evolution of multiple entities]:
  specifications/EventsAndConstraints.spec.md#constraints-on-evolution-of-multiple-entities
