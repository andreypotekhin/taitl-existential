package com.taitl.existential.specs.library_usage;

import com.taitl.ex.examples.night_city.model.Cat;
import com.taitl.existential.keys.TypeKey;
import com.taitl.existential.specs.SpecBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserCanSendAccessEvents extends SpecBase
{
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
    @DisplayName("User can record access to entity")
    void recordEntityAccess() throws Exception
    {
        String tran = ex.begin(op).id();
        ex.read(cat, tran);
        ex.write(cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can record access to entity using type key")
    void recordEntityAccessWithTypeKey() throws Exception
    {
        String tran = ex.begin(op).id();
        ex.read(cat, new TypeKey<Cat>(Cat.class), tran);
        ex.write(cat, tran);
        ex.commit(tran);
    }
}