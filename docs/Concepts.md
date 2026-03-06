# Concepts and Terminology

## Main
See /Readme.md for library overview.

## Getting started
See Usage.md for the quick-start. 

### Concepts
#### Operation keys
Operation keys are path-like strings used to identify business operation and find all matching contexts.
Examples:
- `/app/orders/create`
- `/admin/users/reset-password`

Rules:
- Must start with a slash (`/`).
- Must contain at least one path segment (cannot be just `/`).
- Must not end with a slash.
- Must not include wildcard characters (`*`).

#### Type keys
In above examples, we use entity .class to specify the type of entity for the constraint. 
Sometimes, however, our entities can be of a generic type, for instance, Document<HTML>, Document<JSON>.
To be able to distinguish between such different generic types, we have TypeKey class to use instead of Class.

Use the code similar to this to create a TypeKey that captures exact generic keys.
(uses anonymous subclass to allow capturing the generic type information at runtime:
`new TypeKey<List<Order>>() {}`.
