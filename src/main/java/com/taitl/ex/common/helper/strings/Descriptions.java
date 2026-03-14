package com.taitl.ex.common.helper.strings;

import com.taitl.existential.evaluables.Ev;
import com.taitl.existential.evaluables.Expression;
import com.taitl.existential.interfaces.Describable;

import static com.taitl.ex.common.helper.Args.check;
import static com.taitl.ex.common.helper.Args.sane;

/**
 * Utilities for optional human-friendly descriptions.
 */
public class Descriptions
{
    private static final String REQUIRED_MESSAGE =
            "Behavior rules require descriptions when Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS is enabled.";

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

    public static void require(boolean requireDescriptions, String description)
    {
        if (!requireDescriptions)
        {
            return;
        }
        check(description != null && !description.isEmpty(), REQUIRED_MESSAGE);
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
