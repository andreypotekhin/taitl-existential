package com.taitl.exlogic.unused;

import com.taitl.existential.constants.Strings;

/**
 * (Unused) A string representing a class, along with its generics, for example "Set<Car>".
 * 
 * For types without generics, it corresponds to the short name of class (like "String"). If class is qualified
 * with generics, it corresponds to class short name with generic qualifier, like "Set<House>".
 * 
 * Examples: 
 *   Class without generics: Class: Car, Type: "Car"
 *   Class with generics: Class: Set<House>, Type: "Set<House>"
 * 
 * @author Andrey Potekhin
 * @see EventAndTypeKey
 *
 */
public class Type
{
    protected String typeid;

    public Type(Object t, String genericQualifier)
    {
        if (t == null)
        {
            throw new IllegalArgumentException(Strings.ARG_T);
        }
        setTypeid(t.getClass(), genericQualifier);
    }

    public Type(Class<?> clz, String genericQualifier)
    {
        if (clz == null)
        {
            throw new IllegalArgumentException(Strings.ARG_CLZ);
        }
        setTypeid(clz, genericQualifier);
    }

    public Type(String classNameQualifiedWithGenerics)
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
            throw new IllegalArgumentException(Strings.ARG_CLZ);
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

    // TODO: equals, hashCode

}
