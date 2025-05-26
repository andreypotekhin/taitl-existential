package com.taitl.existential.keys;

import com.taitl.existential.helper.Args;

/**
 * A string representing a type along with its generics, for example "Set<Car>".<p>
 * <p>
 * This class is used to overcome effects of Java type erasure, in scenarios where we need to know the generic qualifier.<p>
 * <p>
 * For the types without generics, it corresponds to the short name of the class (like "String").<br>
 * For the types qualified with generics, it corresponds to class short name with generic qualifier, like {@code Set<House>}.<p>
 * <p>
 * Examples: <br>
 * Classes without generics: Class: Car, TypeKey: string "Car"<br>
 * Classes with generics: Class: {@code Set<House>}, TypeKey: string "{@code Set<House>}"<br>
 * <p>
 * Q: Why do we need to overcome Java type erasure? After all, Java compiler survives with it?<br>
 * A: Because we want ability to define event handlers on a distinct fully-qualified type. For
 * instance, we might want to different handlers for different types of a document:
 * OnChange<Document<HTML>>(...)
 * OnChange<Document<JSON>>(...)
 * <p>
 *
 * @author Andrey Potekhin
 * @see com.taitl.existential.EventHandlers
 */
public class TypeKey<T>
{
    protected String typeid;

    /**
     * Constructs a TypeKey from a class without generics.
     * Example: TypeKey("Car")
     *
     * @param clz Class to construct TypeKey from
     */
    public TypeKey(Class<?> clz)
    {
        setTypeid(clz, "");
    }

    /**
     * Constructs a TypeKey from a class and a generic qualifier.
     * Example: TypeKey(Document.class, "JSON")
     *
     * @param clz              Class to construct TypeKey from, like Document.class
     * @param genericQualifier Generic qualifier, like {@code "JSON"}
     */
    public TypeKey(Class<?> clz, String genericQualifier)
    {
        Args.cool(clz, "clz", genericQualifier, "genericQualifier");
        Args.require(!genericQualifier.isBlank(), "Argument 'genericQualifier' cannot be blank");
        setTypeid(clz, genericQualifier);
    }

    /**
     * Constructs a TypeKey from a class and a generic qualifier.
     * Example: {@code TypeKey("Document<JSON>")}
     *
     * @param classNameQualifiedWithGenerics Class name qualified with generics, like {@code "Document<JSON>"}
     */
    public TypeKey(String classNameQualifiedWithGenerics)
    {
        Args.cool(classNameQualifiedWithGenerics, "classNameQualifiedWithGenerics");
        Args.require(!classNameQualifiedWithGenerics.isBlank(),
                "Argument 'classNameQualifiedWithGenerics' cannot be blank");
        requireValidTypeKey(classNameQualifiedWithGenerics);
        typeid = classNameQualifiedWithGenerics;
    }

    public static <T> TypeKey<T> valueOf(Class<?> clz)
    {
        return new TypeKey<>(clz, "");
    }

    public static <T> TypeKey<T> valueOf(Class<?> clz, String genericQualifier)
    {
        return new TypeKey<>(clz, genericQualifier);
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

    protected void setTypeid(Class<?> clz, String genericQualifier)
    {
        Args.cool(clz, "clz", genericQualifier, "genericQualifier");
        String className = clz.getSimpleName();
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
        Args.cool(key, "class name");
        key = key.trim();
        Args.require(!key.isBlank(), "Class name cannot be blank");
        if (key.contains("<") || key.contains(">"))
        {
            Args.require(key.contains("<") && key.contains(">"),
                    "Class name must be of proper format: 'Class<GenericQualifier>'");
            int leftBracket = key.indexOf("<");
            int rightBracket = key.lastIndexOf(">");
            Args.require(leftBracket < rightBracket, "Right bracket must not come before left bracket");
            requireValidTypeKey(key.substring(leftBracket + 1, rightBracket));
        }
    }
}
