package com.taitl.ex.logic.evaluation.logic;

import com.taitl.existential.keys.TypeKey;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.taitl.ex.common.helper.Args.sane;

/**
 * Splits a type key into a set of less-generic type keys, by reducing generic parameter detail.
 *
 * Examples:
 * <pre>
 * T&lt;A&lt;X&gt;&gt; -&gt; T&lt;A&lt;X&gt;&gt;, T&lt;A&lt;?&gt;&gt;, T&lt;A&gt;
 * T&lt;A&lt;X&gt;,B&lt;Y&gt;&gt; -&gt; T&lt;A&lt;X&gt;,B&lt;Y&gt;&gt;, T&lt;A&lt;X&gt;,B&lt;?&gt;&gt;, T&lt;A&lt;X&gt;,B&gt;, ..., T&lt;A,B&gt;
 * </pre>
 */
public class SplitByGenericsDimension
{
    public <T> Set<TypeKey<T>> split(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        Node node = parse(typeKey.toString());
        Set<TypeKey<T>> result = new LinkedHashSet<>();
        for (String key : variants(node))
        {
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
