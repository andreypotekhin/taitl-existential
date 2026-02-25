package com.taitl.ex.logic.unused;

import java.util.*;

/**
 * A string representing a class, along with its generics, for example "Doc<MD>".
 * 
 * For the types without generics, it corresponds to the short name of class (like "String").
 * If class is qualified with generics, it corresponds to class short name with generic
 * qualifier, like "Set<House>".
 * 
 * Examples: 
 *   Class without generics: Class: Car, Type: "Car"
 *   Class with generics: Class: Set<House>, Type: "Set<House>"
 * 
 * @see EventAndTypeKey
 * @deprecated Use EventKey
 */
@Deprecated
public class Type0
{
    protected String typeid;

    public Type0(Object t, String genericQualifier)
    {
        if (t == null)
        {
            throw new IllegalArgumentException("Argument 't' should not be null");
        }
        setTypeid(t.getClass(), genericQualifier);
    }

    public Type0(Class<?> clz, String genericQualifier)
    {
        if (clz == null)
        {
            throw new IllegalArgumentException("Argument 'clz' should not be null");
        }
        setTypeid(clz, genericQualifier);
    }

    public Type0(String classNameQualifiedWithGenerics)
    {
        typeid = classNameQualifiedWithGenerics;
    }

    public String toString()
    {
        return typeid;
    }

    protected void setTypeid(Class<?> clz, String genericQualifier)
    {
        if (clz == null)
        {
            throw new IllegalArgumentException("Argument 'clz' should not be null");
        }
        if (genericQualifier == null || genericQualifier.isEmpty())
        {
            typeid = clz.getSimpleName();
        }
        else
        {
            typeid = clz.getSimpleName() + "<" + genericQualifier + ">";
        }
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (!(other instanceof Type0))
        {
            return false;
        }
        Type0 that = (Type0) other;
        return Objects.equals(typeid, that.typeid);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(typeid);
    }

}
