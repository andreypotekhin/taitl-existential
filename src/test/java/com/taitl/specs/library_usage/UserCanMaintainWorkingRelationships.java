package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.app.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class UserCanMaintainWorkingRelationships
{
    @Test
    void usesSharedApplicationOperationsForManagedRelationshipsAndRecovery()
    {
        assertEquals(List.of("Rejected a non-black cat and rolled back explicitly.",
                "Created a cat using seeded transaction-local dwellings.",
                "Maintained relocation relationships through checkpoint and commit."), ManagedCityStory.run());
    }
}
