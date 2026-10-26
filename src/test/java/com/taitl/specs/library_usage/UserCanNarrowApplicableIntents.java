package com.taitl.specs.library_usage;

import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanNarrowApplicableIntents
{
    private static final String OP = "/api/catalog/items";

    private Existential ex;

    @BeforeEach
    void setUp()
    {
        ex = new Existential();
    }

    @AfterEach
    void tearDown()
    {
        ex.close();
    }

    @Test
    void childIntentNarrowsPassingParentIntent()
    {
        // @formatter:off
        ex.configure()
                .context("/api")
                    .intent(String.class)
                        .read();
        ex.configure()
                .context(OP)
                    .intent(String.class)
                        .read(value -> false, "child restriction");
        // @formatter:on

        Tr tr = ex.begin(OP);
        assertThrows(IntentViolation.class, () -> ex.read("item", tr.id()));
        tr.rollback();
    }

    @Test
    void sameContextIntentsAreConjunctiveAndAlternativesCanBeExplicit()
    {
        // @formatter:off
        ex.configure()
                .context(OP)
                    .intent(String.class)
                        .read(value -> true)
                        .read(value -> value.startsWith("public-"));
        // @formatter:on

        Tr tr = ex.begin(OP);
        assertThrows(IntentViolation.class, () -> ex.read("private-item", tr.id()));
        tr.rollback();

        Existential alternative = new Existential();
        // @formatter:off
        alternative.configure()
                .context(OP)
                    .intent(String.class)
                        .read(value -> value.startsWith("public-") || value.startsWith("shared-"));
        // @formatter:on
        Tr allowed = alternative.begin(OP);
        assertDoesNotThrow(() -> alternative.read("shared-item", allowed.id()));
        allowed.rollback();
        alternative.close();
    }

    @Test
    void transactionLocalIntentNarrowsSharedIntent()
    {
        // @formatter:off
        ex.configure()
                .context(OP)
                    .intent(String.class)
                        .read();
        // @formatter:on

        Transaction custom = new Transaction(OP, "runtime restriction");
        custom.intent(new Intent<>(String.class).read(value -> false, "local restriction"));
        Tr tr = ex.begin(OP, custom);
        assertThrows(IntentViolation.class, () -> ex.read("item", tr.id()));
        tr.rollback();
    }
}
