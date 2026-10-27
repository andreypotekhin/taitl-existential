package com.taitl.ex.common.helper.strings;

import com.taitl.existential.evaluables.*;
import com.taitl.existential.interfaces.Describable;

import static com.taitl.ex.common.helper.Args.check;
import static com.taitl.ex.common.helper.Args.sane;

/**
 * Utilities for optional human-friendly descriptions.
 */
public class Descriptions
{
    /**
     * Protected constructor for an utility class.
     */
    protected Descriptions()
    {
    }

    /**
     * Returns the description or an empty string when absent.
     */
    public static String text(String description)
    {
        return description == null ? "" : description;
    }

    /**
     * Appends description to base message if present.
     */
    public static String message(String base, String description)
    {
        if (description == null || description.isEmpty())
        {
            return base;
        }
        return base + ": " + description;
    }

    /**
     * Enforces the declaration-time option without revisiting rules accepted under earlier settings.
     * Missing text leaves the caller's builder available for a corrected declaration.
     */
    public static void require(boolean requireDescriptions, String description)
    {
        if (!requireDescriptions)
        {
            return;
        }
        if (description == null || description.isEmpty())
        {
            throw com.taitl.ex.logic.diagnostics.OperationalDiagnostics.argument(
                    "Behavior rules require descriptions when Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS is enabled. "
                            + "Add a description or disable the flag before declaring this rule. "
                            + "See /Troubleshooting.md#required-rule-descriptions");
        }
    }

    /** Validates supplied leaves without mutating their declaration-time policy. */
    public static void require(boolean requireDescriptions, Evs<?> supplied)
    {
        if (requireDescriptions)
        {
            for (Ev<?> ev : supplied.list())
            {
                if (ev instanceof Evs<?>)
                {
                    require(true, (Evs<?>) ev);
                }
                else
                {
                    require(true, ev);
                }
            }
        }
    }

    public static void require(boolean requireDescriptions, Ev<?> ev)
    {
        sane(ev, "ev");
        if (ev instanceof Describable)
        {
            require(requireDescriptions, ((Describable) ev).description());
        }
        else if (ev instanceof Expression<?>)
        {
            require(requireDescriptions, ((Expression<?>) ev).description());
        }
    }
}
