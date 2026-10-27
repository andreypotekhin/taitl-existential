package com.taitl.ex.logic.diagnostics.actions;

public class EscapeDiagnosticText
{
    public String call(String text)
    {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            switch (c)
            {
            case '\n':
                escaped.append("\\n");
                break;
            case '\r':
                escaped.append("\\r");
                break;
            case '\t':
                escaped.append("\\t");
                break;
            default:
                if (Character.isISOControl(c) || c == '\u2028' || c == '\u2029')
                {
                    escaped.append(String.format(java.util.Locale.ROOT, "\\u%04x", (int) c));
                }
                else
                {
                    escaped.append(c);
                }
            }
        }
        return escaped.toString();
    }
}
