package com.taitl.existential.keys;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.Text.*;

/**
 * String representation of a type along with its generic qualifier, for example "Set<Car>".
 * This type key is used to work around Java type erasure when the generic qualifier is
 * needed at runtime.
 * For non-generic types, the key is the class short name, such as "String".
 * For generic types, the key is the class short name with the qualifier, such as
 * {@code Set<House>}.
 * This allows event handlers to be registered for fully-qualified types, for example
 * OnChange<Document<HTML>> and OnChange<Document<JSON>>.
 */
public class TypeKey<T>
{
    private static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#type-key-format";
    private static final String TROUBLESHOOTING_LINK = " See " + TROUBLESHOOTING_SECTION;
    protected String key;

    /**
     * Constructs TypeKey for a class, possibly with generics, using Java reflection.
     * Takes advantage of Java's anonymous class syntax to capture the generic type
     * information at runtime (not available otherwise).
     *
     * Usage: create an anonymous subclass of TypeKey with the desired generic type:
     * {@code new TypeKey<MyClass<GenericParams>>(){}}
     *
     * Example: {@code new TypeKey<Document<JSON>>(){}}
     * Result: {@code "Document<JSON>"}
     */
    protected TypeKey()
    {
        this(false);
    }

    /**
     * Variant of TypeKey() that takes a boolean parameter to use class's
     * fully qualified name (java.lang.String) or simple name (String)
     *
     * @param useFullName Use fully qualified name (clz.getCanonicalName())
     */
    protected TypeKey(boolean useFullName)
    {
        // Get generic superclass, e.g. TypeKey<Document<JSON>>
        Type superclass = getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType))
        {
            throw new IllegalArgumentException("You should call this method with an anonymous subclass of TypeKey,"
                    + " parameterized with a type. Example: new TypeKey<Document<JSON>>(){}"
                    + TROUBLESHOOTING_LINK);
        }
        // Get TypeKey parameter type, e.g. Document<JSON>
        Type type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        key = getRecursiveTypeName(type, useFullName);
    }

    /**
     * Constructs TypeKey for a class without generics.
     * Example: TypeKey(Car.class): "Car"
     *
     * @param typeClass Class to construct TypeKey from
     */
    public TypeKey(Class<?> typeClass)
    {
        setKey(typeClass, "", false);
    }

    /**
     * Constructs TypeKey for a class without generics, optionally using fully-qualified class name.
     * Example: TypeKey(Car.class): "Car" or "com.example.Car"
     *
     * @param typeClass   Class to construct TypeKey from
     * @param useFullName If true, uses fully-qualified class name instead of short name
     */
    public TypeKey(Class<?> typeClass, boolean useFullName)
    {
        setKey(typeClass, "", useFullName);
    }

    /**
     * Constructs TypeKey for a class with generics.
     * Example: TypeKey(Document.class, "JSON"): "Document<JSON>"
     *
     * @param typeClass        Class to construct TypeKey from, like Document.class
     * @param genericQualifier Generic qualifier, like {@code "JSON"}
     */
    public TypeKey(Class<?> typeClass, String genericQualifier)
    {
        sane(typeClass, "typeClass", genericQualifier, "genericQualifier");
        check(!genericQualifier.isBlank(), "Argument 'genericQualifier' cannot be blank");
        setKey(typeClass, genericQualifier, false);
    }

    /**
     * Constructs TypeKey for a class with generics, optionally using fully-qualified class name.
     * Example: TypeKey(Document.class, "JSON"): "Document<JSON>" or "com.example.Document<JSON>"
     *
     * @param typeClass        Class to construct TypeKey from, like Document.class
     * @param genericQualifier Generic qualifier, like {@code "JSON"}
     * @param useFullName      If true, uses fully-qualified class name instead of short name
     */
    public TypeKey(Class<?> typeClass, String genericQualifier, boolean useFullName)
    {
        sane(typeClass, "typeClass", genericQualifier, "genericQualifier");
        check(!genericQualifier.isBlank(), "Argument 'genericQualifier' cannot be blank");
        setKey(typeClass, genericQualifier, useFullName);
    }

    /**
     * Constructs TypeKey for a class name string with possible generic qualifier.
     * Example: TypeKey("Document<JSON>"): "Document<JSON>"
     *
     * @param classNameQualifiedWithGenerics Class name qualified with generics, like {@code "Document<JSON>"}
     */
    public TypeKey(String classNameQualifiedWithGenerics)
    {
        sane(classNameQualifiedWithGenerics, "classNameQualifiedWithGenerics");
        check(!classNameQualifiedWithGenerics.isBlank(),
                "Argument 'classNameQualifiedWithGenerics' cannot be blank");
        validate(classNameQualifiedWithGenerics);
        key = classNameQualifiedWithGenerics;
    }

    public static <T> TypeKey<T> valueOf(Class<?> typeClass)
    {
        return new TypeKey<>(typeClass);
    }

    public static <T> TypeKey<T> valueOf(Class<?> typeClass, String genericQualifier)
    {
        return new TypeKey<>(typeClass, genericQualifier);
    }

    /**
     * Constructs TypeKey from a class using fully-qualified class name.
     */
    public static <T> TypeKey<T> valueOfFull(Class<?> typeClass)
    {
        return new TypeKey<>(typeClass, true);
    }

    /**
     * Constructs TypeKey from a class and generic qualifier using fully-qualified class name.
     */
    public static <T> TypeKey<T> valueOfFull(Class<?> typeClass, String genericQualifier)
    {
        return new TypeKey<>(typeClass, genericQualifier, true);
    }

    public static <T> TypeKey<T> valueOf(String classNameQualifiedWithGenerics)
    {
        return new TypeKey<>(classNameQualifiedWithGenerics);
    }

    public static <T> TypeKey<T> valueOf(T t, String genericQualifier)
    {
        return new TypeKey<>(t.getClass(), genericQualifier);
    }

    public static <T> TypeKey<T> valueOf(T t)
    {
        return new TypeKey<>(t.getClass());
    }

    /**
     * Constructs TypeKey from an object instance using fully-qualified class name.
     */
    public static <T> TypeKey<T> valueOfFull(T t)
    {
        return new TypeKey<>(t.getClass(), true);
    }

    public int hashCode()
    {
        return key.hashCode();
    }

    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (other == null)
        {
            return false;
        }
        if (!(other instanceof TypeKey<?>))
        {
            return false;
        }
        TypeKey<?> o = (TypeKey<?>) other;
        if (o.key == null)
        {
            return (this.key == null);
        }
        return o.key.equals(this.key);
    }

    public String toString()
    {
        return key;
    }

    protected void setKey(Class<?> typeClass, String genericQualifier, boolean useFullName)
    {
        sane(typeClass, "typeClass", genericQualifier, "genericQualifier");
        String className = useFullName ? typeClass.getCanonicalName() : typeClass.getSimpleName();
        if (genericQualifier.isEmpty())
        {
            key = className;
        }
        else if (genericQualifier.startsWith("<") && genericQualifier.endsWith(">"))
        {
            key = className + genericQualifier;
        }
        else
        {
            key = className + "<" + genericQualifier + ">";
        }
        validate(key);
    }

    protected static void validate(String key)
    {
        key = trimmed(key, "class name");
        check(!key.isBlank(), "Class name cannot be blank." + TROUBLESHOOTING_LINK);
        if (key.contains("<") || key.contains(">"))
        {
            check(key.contains("<") && key.contains(">"),
                    "Class name must be of proper format: 'Class<GenericQualifier>'." + TROUBLESHOOTING_LINK);
            int leftBracket = key.indexOf("<");
            int rightBracket = key.lastIndexOf(">");
            check(leftBracket < rightBracket, "Right bracket must not come before left bracket."
                    + TROUBLESHOOTING_LINK);
            validate(key.substring(leftBracket + 1, rightBracket));
        }
    }

    /**
     * Recursively constructs string representation of a type, including its generic parameters.
     * Example: for Document<JSON>, returns "Document<JSON>" or "com.package.Document<com.other.package.JSON>".
     */
    protected String getRecursiveTypeName(Type type, boolean useFullName)
    {
        // For a simple class (like String or JSON), return its name
        if (type instanceof Class<?>)
        {
            Class<?> clz = (Class<?>) type;
            return useFullName ? clz.getCanonicalName() : clz.getSimpleName();
        }

        // If it's a nested parameterized type (like Document<JSON>)
        if (type instanceof ParameterizedType)
        {
            ParameterizedType pType = (ParameterizedType) type;
            Class<?> clz = (Class<?>) pType.getRawType();
            String rawType = useFullName ? clz.getCanonicalName() : clz.getSimpleName();
            // Recurse for each type argument inside the angle brackets
            String arguments = Arrays.stream(pType.getActualTypeArguments())
                    .map(t -> getRecursiveTypeName(t, useFullName)) // RECURSION
                    .collect(Collectors.joining(", "));
            return rawType + "<" + arguments + ">";
        }

        return type.toString();
    }
}
