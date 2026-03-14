package com.taitl.existential.transactions;

import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class TrTest
{
    protected Existential ex;
    protected Existential prev;
    protected Tr tr;

    @BeforeEach
    void setup() throws Exception
    {
        ex = new Existential();
        prev = Ex.instance(ex);
        ex.configure().context("/memo").effect(String.class).create(value -> {
        }, "noop");
        tr = ex.begin("/memo");
    }

    @AfterEach
    void cleanup()
    {
        Ex.instance(prev);
        ex.close();
    }

    @Nested
    class Memo
    {
        @Test
        @DisplayName("Stores and retrieves memo state by identity and type key")
        void storesByIdentityAndTypeKey() throws Exception
        {
            String live = new String("live");
            String before = new String("before");
            TypeKey<String> typeKey = new TypeKey<>(String.class);

            tr.memo(before, live, typeKey);

            assertTrue(tr.hasMemo(live, typeKey));
        }

        @Test
        @DisplayName("Rejects same instance as live and before")
        void rejectsSameInstance() throws Exception
        {
            String live = new String("live");

            MemoException error = assertThrows(MemoException.class, () -> tr.memo(live, live, String.class));

            assertTrue(error.getMessage().contains("detached snapshot"));
            assertTrue(error.getMessage().contains("/Troubleshooting.md#memo-state-missing"));
        }

        @Test
        @DisplayName("Rejects duplicate memo for same entity and type key")
        void rejectsDuplicate() throws Exception
        {
            String live = new String("live");

            tr.memo(new String("before-1"), live, String.class);

            MemoException error = assertThrows(MemoException.class,
                    () -> tr.memo(new String("before-2"), live, String.class));

            assertTrue(error.getMessage().contains("already registered"));
        }

        @Test
        @DisplayName("Facade can register memo state by transaction id")
        void facadeByTransactionId() throws Exception
        {
            String live = new String("live");
            String before = new String("before");

            Ex.memo(before, live, String.class, tr.id());

            assertTrue(tr.hasMemo(live, new TypeKey<>(String.class)));
        }
    }
}
