package com.taitl.existential.keys;

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
    protected String typeid;

    /**
     * Constructs TypeKey for a class without generics.
     * Example: TypeKey("Car")
     *
     * @param clz Class to construct TypeKey from
     */
    public TypeKey(Class<?> clz)
    {
        setTypeid(clz, "", false);
    }

    /**
     * Constructs TypeKey for a class without generics, optionally using fully-qualified class name.
     * Example: TypeKey("com.example.Car") or TypeKey("Car")
     *
     * @param clz         Class to construct TypeKey from
     * @param useFullName If true, uses fully-qualified class name instead of short name
     */
    public TypeKey(Class<?> clz, boolean useFullName)
    {
        setTypeid(clz, "", useFullName);
    }

    /**
     * Constructs TypeKey for a class with generics.
     * Example: TypeKey(Document.class, "JSON")
     *
     * @param clz              Class to construct TypeKey from, like Document.class
     * @param genericQualifier Generic qualifier, like {@code "JSON"}
     */
    public TypeKey(Class<?> clz, String genericQualifier)
    {
        sane(clz, "clz", genericQualifier, "genericQualifier");
        check(!genericQualifier.isBlank(), "Argument 'genericQualifier' cannot be blank");
        setTypeid(clz, genericQualifier, false);
    }

    /**
     * Constructs TypeKey for a class with generics, optionally using fully-qualified class name.
     * Example: TypeKey(Document.class, "JSON") or TypeKey("com.example.Document", "JSON")
     *
     * @param clz              Class to construct TypeKey from, like Document.class
     * @param genericQualifier Generic qualifier, like {@code "JSON"}
     * @param useFullName      If true, uses fully-qualified class name instead of short name
     */
    public TypeKey(Class<?> clz, String genericQualifier, boolean useFullName)
    {
        sane(clz, "clz", genericQualifier, "genericQualifier");
        check(!genericQualifier.isBlank(), "Argument 'genericQualifier' cannot be blank");
        setTypeid(clz, genericQualifier, useFullName);
    }

    /**
     * Constructs TypeKey for a class name string with possible generic qualifier.
     * Example: {@code TypeKey("Document<JSON>")}
     *
     * @param classNameQualifiedWithGenerics Class name qualified with generics, like {@code "Document<JSON>"}
     */
    public TypeKey(String classNameQualifiedWithGenerics)
    {
        sane(classNameQualifiedWithGenerics, "classNameQualifiedWithGenerics");
        check(!classNameQualifiedWithGenerics.isBlank(),
                "Argument 'classNameQualifiedWithGenerics' cannot be blank");
        requireValidTypeKey(classNameQualifiedWithGenerics);
        typeid = classNameQualifiedWithGenerics;
    }

    public static <T> TypeKey<T> valueOf(Class<?> clz)
    {
        return new TypeKey<>(clz);
    }

    public static <T> TypeKey<T> valueOf(Class<?> clz, String genericQualifier)
    {
        return new TypeKey<>(clz, genericQualifier);
    }

    /**
     * Constructs TypeKey from a class using fully-qualified class name.
     */
    public static <T> TypeKey<T> valueOfFull(Class<?> clz)
    {
        return new TypeKey<>(clz, true);
    }

    /**
     * Constructs TypeKey from a class and generic qualifier using fully-qualified class name.
     */
    public static <T> TypeKey<T> valueOfFull(Class<?> clz, String genericQualifier)
    {
        return new TypeKey<>(clz, genericQualifier, true);
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
        return typeid.hashCode();
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
        if (o.typeid == null)
        {
            return (this.typeid == null);
        }
        return o.typeid.equals(this.typeid);
    }

    public String toString()
    {
        return typeid;
    }

    protected void setTypeid(Class<?> clz, String genericQualifier, boolean useFullName)
    {
        sane(clz, "clz", genericQualifier, "genericQualifier");
        String className = useFullName ? clz.getName() : clz.getSimpleName();
        if (genericQualifier.isEmpty())
        {
            typeid = className;
        }
        else if (genericQualifier.startsWith("<") && genericQualifier.endsWith(">"))
        {
            typeid = className + genericQualifier;
        }
        else
        {
            typeid = className + "<" + genericQualifier + ">";
        }
        requireValidTypeKey(typeid);
    }

    protected static void requireValidTypeKey(String key)
    {
        key = trimmed(key, "class name");
        check(!key.isBlank(), "Class name cannot be blank");
        if (key.contains("<") || key.contains(">"))
        {
            check(key.contains("<") && key.contains(">"),
                    "Class name must be of proper format: 'Class<GenericQualifier>'");
            int leftBracket = key.indexOf("<");
            int rightBracket = key.lastIndexOf(">");
            check(leftBracket < rightBracket, "Right bracket must not come before left bracket");
            requireValidTypeKey(key.substring(leftBracket + 1, rightBracket));
        }
    }
}
