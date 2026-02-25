package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.logic.transactions.TransactionLogic;
import com.taitl.existential.Ex;
import com.taitl.existential.configs.Config;
import com.taitl.existential.configs.Context;
import com.taitl.existential.configs.Transaction;
import com.taitl.existential.transactions.Tr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateTranTest
{
    TransactionLogic tl;
    CreateTran createTran;

    @BeforeEach
    void setup()
    {
        tl = Ex.transactions().logic();
        createTran = new CreateTran(tl);
    }

    @Test
    void forConfigBuildsContextTransactionsAndCustom()
    {
        Context c1 = context("/ctx1");
        Context c2 = context("/ctx2");
        Config config = new Config();
        config.addContext(c1);
        config.addContext(c2);
        Transaction custom = new Transaction("custom", "custom");

        Tr tr = createTran.forConfig("/op", config, custom, CreateTran::forContext);
        List<Transaction> transactions = tr.transactions();

        assertThat(tr.op, is("/op"));
        assertThat(transactions, hasSize(3));
        assertThat(transactions.get(0).context, is(c1));
        assertThat(transactions.get(0).name, is(c1.name()));
        assertThat(transactions.get(0).op, is("/op"));
        assertThat(transactions.get(1).context, is(c2));
        assertThat(transactions.get(1).name, is(c2.name()));
        assertThat(transactions.get(1).op, is("/op"));
        assertThat(transactions.get(2), sameInstance(custom));
        assertThat(custom.op, is("/op"));
    }

    @Test
    void forContextsBuildsFromCreateTransaction()
    {
        Context c1 = context("/ctx1");
        Context c2 = context("/ctx2");

        Tr tr = createTran.forContexts("/op2", List.of(c1, c2), null, CreateTran::forContext);
        List<Transaction> transactions = tr.transactions();

        assertThat(tr.op, is("/op2"));
        assertThat(transactions, hasSize(2));
        assertThat(transactions.get(0).context, is(c1));
        assertThat(transactions.get(0).name, is(c1.name()));
        assertThat(transactions.get(0).op, is("/op2"));
        assertThat(transactions.get(1).context, is(c2));
        assertThat(transactions.get(1).name, is(c2.name()));
        assertThat(transactions.get(1).op, is("/op2"));
    }

    @Test
    void forContextRejectsMismatchedTransactionOp()
    {
        Context context = new Context("/ctx").transaction(() -> new Transaction("/other", "custom"));

        IllegalArgumentException e =
                assertThrows(IllegalArgumentException.class, () -> CreateTran.forContext(context));

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
                assertThrows(IllegalArgumentException.class, () -> CreateTran.forContext(context));

        assertThat(e.getMessage(), containsString("must match parent context '/ctx/child'"));
    }

    private static Context context(String name)
    {
        return new Context(name).transaction(() -> new Transaction(name, "seed"));
    }
}
