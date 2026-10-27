# Access events and general rules

The [first comparison](../runs/20261027T050744Z.AccessEvents.md) measures 10,000 Night City cats reporting read,
read-and-lock, write, and access, once/eight times per object. Three fresh-JVM samples compare typed-only workflows
against the prior revision and establish a valid baseline for restored general On constraints/intents.

Exact callback and cleanup assertions accompany success, rejection/rollback, and fresh recovery. General handlers
previously skipped are not treated as a faster baseline. Repeated reporting exposes additional intent grouping/order
cost; future optimization must preserve authorization and declaration order. No numerical gate is established.
