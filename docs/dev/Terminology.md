## Library Terminology 

### Terminology

Op: a business operation. Operation key: the business operation identifier, such as "/api/user/update"   
Path: OS path-like notation for operation keys   
  - Abstract path can contain wildcards, such as "/api/*/update"
  - Concrete path doesn't contain wildcards
Evaluable: anything that can be evaluated  
Statement: an Evaluable that does not necessarily return a value  
Expression: an Evaluable that returns a value
Predicate: an Expression that returns a boolean value
Invariant: a list of Predicate expressions evaluated as constraints
Effect: a list of Statements executed for side effects
Intent: a list of Statements that authorize or gate access to actions on entities 
  - Example: a 'read' intent configured on entity type to allow reading/loading from persistence
  - all other entity types become not readable, unless they also have 'read' intent configured for them
  - if no 'read' intent is configured on any entity type, then all entity types are readable by default
Rule: an umbrella term for invariants, effects and intents 
Event: an application or library event, such as: 
  - modifying an entity (e.g. Update<User>) 
  - accessing an entity (e.g. Read<User>)
  - starting or committing a transaction (e.g. Begin<Transaction>) and the like
  - Events are sent to the library by calling the event() method
  - Access events are sent to the library by calling read() and write()
  - Transaction events are automatically sent to the library when initiating or completing a transaction (begin(), commit(), rollback(), checkpoint() methods)
Runtime event: an event that is sent to the library during transaction execution. Includes event type, entity type and reference to the affected entity.   
Context: a set of rules associated with a business operation 
  - A Context uses operation key to associate with an Op 
  - A Context can be defined with a wildcard operation key
  - Parent context is any Context whose name matches, without being equal to, the Context's name
  - Matching context is any wildcard context whose name matches, without being equal to, the Context's name
  - The rules from parent apply to child contexts
  - The rules from matching contexts apply to matched contexts
Transaction: a set of rules associated with a Context
  - Transactions are defined within a Context
  - Transactions are more dynamic than the Contexts, allowing use of local scope (method parameters,
  local variables) and members of the defining class (for anonymous nested classes) in event handlers' code
Library Transaction (Tr): a unit of execution in the library, associated with an operation key
  - Association with an operation key allows to select relevant Contexts and Transactions for evaluation
  - Transaction lifecycle is triggered by calling begin(), ending with commit(), rollback(), optional checkpoint()
  - For each relevant Context and Transaction, their configured rules are evaluated
  - The rules are evaluated at the end of transaction (commit or checkpoint), unless assigned to an earlier stage
  - In case of a constraint violation, an exception is raised and violations are reported
Quantifier: a logical expression such as All or Exists
Mutation: an event that records both before and after states of an entity 
Porting: a Mutation that can have a null in the before or after state (but not both) 
  - The null in 'before' state indicates creation of an entity
  - The null in 'after' state indicates deletion of an entity
  - Both 'before' and 'after' states being non-null indicate a change (mutation) of an entity

For additional documentation, see /Readme.md, /docs and /docs/dev.
