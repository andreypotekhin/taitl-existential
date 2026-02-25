package com.taitl.existential.transactions;

import java.util.*;
import com.taitl.ex.logic.transactions.actions.*;
import com.taitl.existential.configs.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class CreateTranTest
{
    @Test
    void forConfigBuildsContextTransactionsAndCustom()
    {
        Context c1 = context("/ctx1");
        Context c2 = context("/ctx2");
        Config config = new Config();
        config.addContext(c1);
        config.addContext(c2);
        Transaction custom = new Transaction("custom", "custom");

        Tr tr = CreateTran.forConfig("/op", config, custom, CreateTran::generateId, CreateTran::forContext);

        assertThat(tr.op, is("/op"));
        assertThat(tr.transactions, hasSize(3));
        assertThat(tr.transactions.get(0).context, is(c1));
        assertThat(tr.transactions.get(0).name, is(c1.name()));
        assertThat(tr.transactions.get(0).op, is("/op"));
        assertThat(tr.transactions.get(1).context, is(c2));
        assertThat(tr.transactions.get(1).name, is(c2.name()));
        assertThat(tr.transactions.get(1).op, is("/op"));
        assertThat(tr.transactions.get(2), sameInstance(custom));
        assertThat(custom.op, is("/op"));
    }

    @Test
    void forContextsBuildsFromCreateTransaction()
    {
        Context c1 = context("/ctx1");
        Context c2 = context("/ctx2");

        Tr tr = CreateTran.forContexts("/op2", List.of(c1, c2), null, CreateTran::generateId, CreateTran::forContext);

        assertThat(tr.op, is("/op2"));
        assertThat(tr.transactions, hasSize(2));
        assertThat(tr.transactions.get(0).context, is(c1));
        assertThat(tr.transactions.get(0).name, is(c1.name()));
        assertThat(tr.transactions.get(0).op, is("/op2"));
        assertThat(tr.transactions.get(1).context, is(c2));
        assertThat(tr.transactions.get(1).name, is(c2.name()));
        assertThat(tr.transactions.get(1).op, is("/op2"));
    }

    @Test
    void forContextRejectsMismatchedTransactionOp()
    {
        Context context = new Context("/ctx").transaction(() -> new Transaction("/other", "custom"));

        IllegalArgumentException e =
                Assertions.assertThrows(IllegalArgumentException.class, () -> CreateTran.forContext(context));

        assertThat(e.getMessage(), containsString("must match parent context '/ctx'"));
    }

    @Test
    void forContextAllowsChildTransactionOp()
    {
        Context context = new Context("/ctx").transaction(() -> new Transaction("/ctx/child", "custom"));

        Transaction tr = CreateTran.forContext(context);

        assertThat(tr.op, is("/ctx"));
    }

    @Test
    void forContextAllowsWildcardTransactionOp()
    {
        Context context = new Context("/api/cats/create").transaction(() -> new Transaction("/api/*", "custom"));

        Transaction tr = CreateTran.forContext(context);

        assertThat(tr.op, is("/api/cats/create"));
    }

    @Test
    void forContextRejectsShorterTransactionOp()
    {
        Context context = new Context("/ctx/child").transaction(() -> new Transaction("/ctx", "custom"));

        IllegalArgumentException e =
                Assertions.assertThrows(IllegalArgumentException.class, () -> CreateTran.forContext(context));

        assertThat(e.getMessage(), containsString("must match parent context '/ctx/child'"));
    }

    private static Context context(String name)
    {
        return new Context(name).transaction(() -> new Transaction(name, "seed"));
    }
}
