package com.taitl.ex.logic.types.data;

import java.util.*;
import java.lang.reflect.*;

/**
 * A parsed subject descriptor used for cold structural rule matching.
 * It separates the raw type from direct generic arguments, respecting nested angle brackets,
 * and parses upper-bound arguments once. Optional native Type metadata can prove relationships
 * that a rendered name alone cannot establish.
 *
 * Concrete enclosing arguments retain their declared structure; this value does not manufacture
 * wildcard variants or load classes by name. MatchTypePattern owns compatibility decisions,
 * including bound projection through captured superclass and interface signatures.
 */
public class TypePattern
{
    protected String key;
    protected String raw;
    protected List<String> arguments;
    protected Type known;
    protected List<TypePattern> bounds;

    public TypePattern(String type)
    {
        this(type, null);
    }

    public TypePattern(String type, Type known)
    {
        this.known = known;
        key = type;
        int open = type.indexOf('<');
        raw = open < 0 ? type : type.substring(0, open);
        arguments = open < 0 ? Collections.emptyList() : arguments(type.substring(open + 1, type.length() - 1));
        bounds = new ArrayList<>(arguments.size());
        for (String argument : arguments)
        {
            bounds.add(argument.startsWith("? extends ") ? new TypePattern(argument.substring(10)) : null);
        }
    }

    public String toString()
    {
        return key;
    }

    public String raw()
    {
        return raw;
    }

    public List<String> arguments()
    {
        return Collections.unmodifiableList(arguments);
    }

    public TypePattern bound(int index)
    {
        return bounds.get(index);
    }

    public Type argument(int index)
    {
        return known instanceof ParameterizedType ? ((ParameterizedType) known).getActualTypeArguments()[index] : null;
    }

    protected List<String> arguments(String value)
    {
        List<String> result = new ArrayList<>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i < value.length(); i++)
        {
            char c = value.charAt(i);
            if (c == '<')
            {
                depth++;
            }
            else if (c == '>')
            {
                depth--;
            }
            else if (c == ',' && depth == 0)
            {
                result.add(value.substring(start, i).trim());
                start = i + 1;
            }
        }
        result.add(value.substring(start).trim());
        return result;
    }
}
