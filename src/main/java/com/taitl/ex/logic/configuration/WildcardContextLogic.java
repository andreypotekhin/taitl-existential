package com.taitl.ex.logic.configuration;

import com.taitl.existential.keys.*;

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
