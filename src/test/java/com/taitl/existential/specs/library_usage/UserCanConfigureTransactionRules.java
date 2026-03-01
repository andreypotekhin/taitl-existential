package com.taitl.existential.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.specs.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanConfigureTransactionRules extends SpecBase
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
    @DisplayName("User can configure intents on a transaction object")
    void configureIntentsOnTransactionObject() throws Exception
    {
        // @formatter:off
        ex.configure(op)
            .context()
                .transaction(() -> {
                    Transaction tr = new Transaction("undefined", "undefined");
                    Intent<Cat> writeIntent = new Intent<>(Cat.class);
                    writeIntent.write();
                    tr.intent(writeIntent);
                    return tr;
                })
                .build();
        // @formatter:on

        String tran = ex.begin(op).id();
        ex.write(cat, tran);
        IntentViolation ex = assertThrows(IntentViolation.class, () -> this.ex.write("other", tran));
        assertTrue(ex.getMessage().contains("No intent is configured"));
        this.ex.rollback(tran);
    }
}
