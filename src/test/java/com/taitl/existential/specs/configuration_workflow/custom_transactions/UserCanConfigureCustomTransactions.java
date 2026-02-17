package com.taitl.existential.specs.configuration_workflow.custom_transactions;

import com.taitl.existential.specs.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanConfigureCustomTransactions extends SpecBase
{
    {
        autoConfigure = false;
    }

    @BeforeEach
    public void setup()
    {
        super.setup();
    }

    @AfterEach
    public void cleanup()
    {
        super.cleanup();
    }

    @Test
    @DisplayName("Configuring transactions")
    void configuringTransactions()
    {
        assertDoesNotThrow(() -> {
            fixt.configureTransactionRules();
            String tran = ex.begin(op);
            ex.event(cat, tran);
            ex.commit(tran);
        });
    }

    @Test
    @DisplayName("Custom transaction instance")
    void customTransactionInstance()
    {
        assertDoesNotThrow(() -> {
            fixt.configureTransactionRules();
            Transaction custom = new Transaction(op, "request-scope");
            String tran = ex.begin(op, custom);
            ex.event(cat, tran);
            ex.commit(tran);
        });
    }
}
