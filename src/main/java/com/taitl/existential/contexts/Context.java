package com.taitl.existential.contexts;

import java.util.Set;
import java.util.function.Supplier;

import com.taitl.existential.EventSplitter;
import com.taitl.existential.helper.Args;
import com.taitl.existential.rules.Rule;
import com.taitl.existential.transactions.Transaction;

public class Context
{
    /**
     * Parent context. Rules defined in parent context run prior to
     * rules in child.
     */
    private Context parent;

    Set<Rule> rules;

    Supplier<? extends EventSplitter> eventSplitterFactory = Contexts.eventSplitterFactory;

    /**
     * Associate a custom Transaction with Context.
     *
     * Associating a custom Transaction with Context allows to define
     * rules, such as invariants and intents, for the context.
     *
     * Custom transaction may declare its own member fields, thus
     * allowing to carry over information between the rules.
     *
     * Example:
     *   Contexts.get("/app/school")
     *     .transaction(() -> new Transaction(){{
     *        require(new Invariants<Student>() {{
     *             all(student -> student.awake());
     *        }});
     *        require(new Invariants<Teacher>() {{
     *             all(teacher -> teacher.notOnLeave());
     *        }});
     *        intents(new Intents<Student>() {{
     *             read();
     *             write();
     *        }});
     *        intents(new Intents<Teacher>() {{
     *             read();
     *        }});
     *    }})
     */
    public Context transaction(Supplier<? extends Transaction> supplier)
    {
        // TODO: remember the supplier for when a new Transaction instance is needed.
        return this;
    }

    public boolean hasParent()
    {
        return parent != null;
    }

    public Context getParent()
    {
        return parent;
    }

    public void eventSplitter(Supplier<? extends EventSplitter> supplier)
    {
        Args.cool(supplier, "supplier");
        eventSplitterFactory = supplier;
    }
}
