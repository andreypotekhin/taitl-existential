package com.taitl.existential.keys;

import com.taitl.existential.EventHandlers;
import com.taitl.existential.constants.Strings;

/**
 * A string representing a type along with its generics, for example "Set<Car>".
 * <p>
 * This class is used to offset/negate the effects of Java type erasure, in scenarios where we need to know the generic qualifier. 
 * <p>
 * For the types without generics, it corresponds to the short name of the class (like "String").<br>
 * For the types qualified with generics, it corresponds to class short name with generic qualifier, like {@code Set<House>}.
 * <p>
 * Examples: <br>
 *   Class without generics: Class: Car, TypeKey: "Car"<br> 
 *   Class with generics: Class: {@code Set<House>}, TypeKey: "{@code Set<House>}"
 * <p>
 * - Why do we need to offset/negate Java type erasure? After all, Java compiler survives with it.<br>
 * - Because we want ability to define an event handler or an expression on fully-qualified type. For
 * instance, we might want to define different handlers depending on type of document: 
 *   OnChange<Document<HTML>>(...)
 *   OnChange<Document<JSON>>(...)
 * <p>
 * @author Andrey Potekhin
 * @see EventHandlers
 */
public class TypeKey<T>
{
    protected String typeid;

    public TypeKey(Class<?> clz, String genericQualifier)
    {
        if (clz == null)
        {
            throw new IllegalArgumentException(Strings.ARG_CLZ);
        }
        setTypeid(clz, genericQualifier);
    }

    public TypeKey(Class<?> clz)
    {
        this(clz, null);
    }

    public TypeKey(String classNameQualifiedWithGenerics)
    {
        typeid = classNameQualifiedWithGenerics;
    }

    public static <T> TypeKey<T> valueOf(Class<?> clz, String genericQualifier)
    {
        return new TypeKey<>(clz, genericQualifier);
    }

    public static <T> TypeKey<T> valueOf(Class<?> clz)
    {
        return new TypeKey<>(clz, null);
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
        if (clz == null)
        {
            throw new IllegalArgumentException(Strings.ARG_CLZ);
        }
        if (genericQualifier == null || genericQualifier.isEmpty())
        {
            typeid = clz.getSimpleName();
        }
        else if (genericQualifier.startsWith("<") && genericQualifier.endsWith(">"))
        {
            typeid = clz.getSimpleName() + genericQualifier;
        }
        else
        {
            typeid = clz.getSimpleName() + "<" + genericQualifier + ">";
        }
    }
}
