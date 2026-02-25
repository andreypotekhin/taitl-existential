package com.taitl.existential.keys;

import java.lang.reflect.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.Generics.*;
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
    protected final String key;

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
        Type type = anonymousSuperclassTypeArgument(getClass(), TypeKey.class);
        this.key = typeName(type, useFullName);
    }

    /**
     * Constructs TypeKey for a class without generics.
     * Example: TypeKey(Car.class): "Car"
     *
     * @param typeClass Class to construct TypeKey from
     */
    public TypeKey(Class<?> typeClass)
    {
        this.key = createKey(typeClass, "", false);
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
        this.key = createKey(typeClass, "", useFullName);
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
        this.key = createKey(typeClass, genericQualifier, false);
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
        this.key = createKey(typeClass, genericQualifier, useFullName);
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
        this.key = classNameQualifiedWithGenerics;
    }

    public static <T> TypeKey<T> valueOf(Class<?> typeClass, boolean useFullName)
    {
        return new TypeKey<>(typeClass, useFullName);
    }

    public static <T> TypeKey<T> valueOf(Class<?> typeClass, String genericQualifier, boolean useFullName)
    {
        return new TypeKey<>(typeClass, genericQualifier, useFullName);
    }

    public static <T> TypeKey<T> valueOf(String classNameQualifiedWithGenerics)
    {
        return new TypeKey<>(classNameQualifiedWithGenerics);
    }

    public static <T> TypeKey<T> valueOf(Type type, boolean useFullName)
    {
        return new TypeKey<>(type, useFullName);
    }

    public static <T> TypeKey<T> valueOf(T t, String genericQualifier, boolean useFullName)
    {
        return new TypeKey<>(t.getClass(), genericQualifier, useFullName);
    }

    public static <T> TypeKey<T> valueOf(T t, boolean useFullName)
    {
        return new TypeKey<>(t.getClass(), useFullName);
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

    protected TypeKey(Type type, boolean useFullName)
    {
        sane(type, "type");
        this.key = typeName(type, useFullName);
        validate(this.key);
    }

    protected static String createKey(Class<?> typeClass, String genericQualifier, boolean useFullName)
    {
        sane(typeClass, "typeClass", genericQualifier, "genericQualifier");
        String className = useFullName ? typeClass.getCanonicalName() : typeClass.getSimpleName();
        String key;
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
        return key;
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
}
