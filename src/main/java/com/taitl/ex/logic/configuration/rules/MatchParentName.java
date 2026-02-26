package com.taitl.ex.logic.configuration.rules;

import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.strings.Text.*;

/**
 * Validates that a child context name matches a parent configuration/context name.
 *
 * Directional rule:
 * Parent name must match the child context name (same path or parent-prefix semantics),
 * while still allowing wildcard child contexts that match the parent.
 */
public final class MatchParentName
{
    private MatchParentName()
    {
    }

    public static String require(String childContextName, String parentName, String parentLabel)
    {
        String child = trimmed(childContextName, "childContextName");
        String parent = trimmed(parentName, "parentName");
        String label = trimmed(parentLabel, "parentLabel");

        ContextKey.validate(child);
        ContextKey.validate(parent);

        check(matches(child, parent),
                String.format(
                        "Context name '%s' must match %s '%s' (child path must extend parent path; wildcard-compatible).",
                        child,
                        label,
                        parent));
        return child;
    }

    public static boolean matches(String childContextName, String parentName)
    {
        String child = trimmed(childContextName, "childContextName");
        String parent = trimmed(parentName, "parentName");
        ContextKey.validate(child);
        ContextKey.validate(parent);

        // Primary rule: parent (possibly wildcard) matches child or one of child's parents.
        if (matchesPathOrAnyParent(parent, child))
        {
            return true;
        }

        // Allow wildcard child contexts that semantically match the parent.
        if (child.contains("*") && matchesPathOrAnyParent(child, parent))
        {
            return true;
        }

        return false;
    }

    static boolean matchesPathOrAnyParent(String pattern, String path)
    {
        String current = path;
        while (true)
        {
            if (wildcardPathMatches(pattern, current))
            {
                return true;
            }
            if ("/".equals(current))
            {
                return false;
            }
            current = parentPath(current);
        }
    }

    static String parentPath(String path)
    {
        int lastSlash = path.lastIndexOf('/');
        return (lastSlash == 0) ? "/" : path.substring(0, lastSlash);
    }

    static boolean wildcardPathMatches(String pattern, String value)
    {
        if (pattern.equals(value))
        {
            return true;
        }

        StringBuilder regex = new StringBuilder("^");
        for (int i = 0; i < pattern.length(); i++)
        {
            char c = pattern.charAt(i);
            if (c == '*')
            {
                regex.append("[^/]*");
            }
            else
            {
                if ("\\.[]{}()+-^$|?".indexOf(c) >= 0)
                {
                    regex.append('\\');
                }
                regex.append(c);
            }
        }
        regex.append('$');
        return value.matches(regex.toString());
    }
}
