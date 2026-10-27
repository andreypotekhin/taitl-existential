package com.taitl.specs.library_usage;

import com.taitl.existential.transactions.Tr;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.exceptions.*;
import com.taitl.specs.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanConfigureTransaction extends SpecBase
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

    @Nested
    class Scenarios
    {
        @Test
        @DisplayName("User can configure intents on a transaction object")
        void configureIntentsOnTransaction() throws Exception
        {
            // @formatter:off
            ex.configure()
                .context(op)
                    .transaction(() -> {
                        Transaction tr = new Transaction(op, "transaction");
                        Intent<Cat> writeIntent = new Intent<>(Cat.class);
                        writeIntent.write();
                        tr.intent(writeIntent);
                        return tr;
                    });
            // @formatter:on

            Tr tranTr = ex.begin(op);
            try
            {
                String tran = tranTr.id();
                ex.write(cat, tran);
                IntentViolation ex = assertThrows(IntentViolation.class,
                        () -> UserCanConfigureTransaction.this.ex.write("other", tran));
                assertEquals(com.taitl.existential.diagnostics.DiagnosticCode.INTENT_MISSING, ex.diagnostic().code());
                tranTr.rollback();
            }
            finally
            {
                tranTr.close();
            }
        }
    }
}
