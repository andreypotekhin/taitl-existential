package com.taitl.ex.common.creator;

import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class CreatorDeviceTest
{
    @Test
    void injectUsesBinaryNameForLocalClasses()
    {
        class LocalA
        {
        }
        class LocalB
        {
        }

        assertNull(LocalA.class.getCanonicalName());
        assertNull(LocalB.class.getCanonicalName());

        CreatorDevice device = new CreatorDevice();
        device.inject(LocalA.class, LocalA::new);
        device.inject(LocalB.class, LocalB::new);

        Supplier<? extends LocalA> supplierA = device.getSupplier(LocalA.class);
        Supplier<? extends LocalB> supplierB = device.getSupplier(LocalB.class);

        assertNotNull(supplierA);
        assertNotNull(supplierB);
        assertThat(supplierA.get(), instanceOf(LocalA.class));
        assertThat(supplierB.get(), instanceOf(LocalB.class));
    }
}
