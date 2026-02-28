## Source code

### Package structure
Package structure overview:
- com.taitl.existential: public code (classes, interfaces) for use by end-user
- com.taitl.ex and subpackages: private code/implementation
  -  com.taitl.ex.common: common/ubiquitous classes (Creator, Args, State)
  -  com.taitl.ex.cross: cross-cut concepts (caching, logging)
  -  com.taitl.ex.concrete: concrete implementations (for example ConcreteExists) for the classes the end-user
     creates with 'new'
  -  com.taitl.ex.core: core classes, such as ExistentialConfigs, immediately used by public code
  -  com.taitl.ex.logic: business logic implementation
  -  com.taitl.ex.configuration: configuration logic (e.g. BuildContexts)
  -  com.taitl.ex.events: event processing logic (e.g. ReceiveEvent)
  -  com.taitl.ex.library: dealing with library as a whole
  -  com.taitl.ex.transactions: transaction logic (e.g. BeginTransaction, RollbackTransaction)
  -  com.taitl.ex.validation: validation logic (e.g. ValidateTransaction)
