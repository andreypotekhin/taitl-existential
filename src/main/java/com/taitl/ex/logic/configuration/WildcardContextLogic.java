package com.taitl.ex.logic.configuration;

import com.taitl.existential.keys.*;

/**
 * Matches one validated context pattern against one validated operation-path level.
 * An asterisk can match characters inside a segment, including an empty segment portion,
 * but cannot cross a slash. Other regular-expression characters are treated literally.
 *
 * The match covers the entire level. Ancestor traversal and ordering remain the responsibility
 * of RuleOrderingLogic, keeping this component focused on context-pattern semantics.
 */
public class WildcardContextLogic
{
    public boolean matches(String pattern, String path)
    {
        ContextKey.validate(pattern);
        ContextKey.validate(path);
        if (pattern.equals(path))
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
        return path.matches(regex.toString());
    }
}
