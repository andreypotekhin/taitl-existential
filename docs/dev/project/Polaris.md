# Polaris

The goal of this library is to allow end user to define constraints on application entities
and automatically guarantee their enforcement in a most performant way. 

In order for the constraints checks to be performant, the library defines transaction model
so that constraints are only checked on a transaction border, rather than on any change 
of an entity during business operation.

To further advance performance, the library uses to full extent the techniques that 
help maximize performance of constraint evaluation such as caching,  
assigning constraints to narrow operation context, tracking entities on transaction level
and the like. 

Development process also tracks performance as part of build process, and must 
quickly address any degradation/regression.

## Project Management Documentation
Roadmap: [Roadmap.md](Roadmap.md)
Definition of Done: [DefinitionOfDone.md](DefinitionOfDone.md)
See docs/dev/project/ for other project management docs.
