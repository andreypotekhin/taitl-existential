package com.taitl.ex.logic.evaluation.split_events;

import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Splits a type key into a set of less-generic type keys, by reducing generic parameter detail.
 *
 * Examples:
 * <pre>
 * T<A<X>> -> T<A<X>>, T<A<?>>, T<A>
 * T<A<X>,B<Y>> -> T<A<X>,B<Y>>, T<A<?>,B<Y>>, T<A<X>,B<Y>>, T<A<X>,B<?>>, T<A<X>,B>, T<A<?>,B<Y>>, T<A<?>,B<?>>, T<A<?>,B>, , T<A,B<Y>>, T<A,B<?>>, T<A,B>
 * </pre>
 */
public class SplitTypeKey
{
    public <T> Set<TypeKey<T>> split(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        Node node = parse(typeKey.toString());
        Set<TypeKey<T>> result = new LinkedHashSet<>();
        for (String key : variants(node))
        {
            if (!node.args.isEmpty() && node.name.equals(key))
            {
                continue;
            }
            result.add(TypeKey.valueOf(key));
        }
        return result;
    }

    protected List<String> variants(Node node)
    {
        List<String> result = new ArrayList<>();
        if (node.args.isEmpty())
        {
            result.add(node.name);
            return result;
        }

        List<List<String>> perArg = new ArrayList<>();
        for (Node arg : node.args)
        {
            LinkedHashSet<String> argVariants = new LinkedHashSet<>(variants(arg));
            if (arg.args.isEmpty())
            {
                argVariants.add("?");
            }
            perArg.add(new ArrayList<>(argVariants));
        }

        combinations(node.name, perArg, 0, new ArrayList<>(), result);
        result.add(node.name);
        return result;
    }

    protected void combinations(String root, List<List<String>> perArg, int i, List<String> current,
            List<String> out)
    {
        if (i == perArg.size())
        {
            out.add(root + "<" + String.join(",", current) + ">");
            return;
        }
        for (String option : perArg.get(i))
        {
            current.add(option);
            combinations(root, perArg, i + 1, current, out);
            current.remove(current.size() - 1);
        }
    }

    protected Node parse(String raw)
    {
        String key = raw.trim();
        int left = key.indexOf('<');
        if (left < 0)
        {
            return new Node(key, List.of());
        }
        String name = key.substring(0, left).trim();
        String inner = key.substring(left + 1, key.length() - 1);
        List<Node> args = new ArrayList<>();
        for (String arg : splitTopLevel(inner))
        {
            args.add(parse(arg));
        }
        return new Node(name, args);
    }

    protected List<String> splitTopLevel(String s)
    {
        List<String> parts = new ArrayList<>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
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
                parts.add(s.substring(start, i).trim());
                start = i + 1;
            }
        }
        parts.add(s.substring(start).trim());
        return parts;
    }

    protected static class Node
    {
        protected final String name;
        protected final List<Node> args;

        protected Node(String name, List<Node> args)
        {
            this.name = name;
            this.args = args;
        }
    }
}
