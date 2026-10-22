package com.taitl.ex.examples.night_city;

import com.taitl.ex.examples.night_city.app.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NightCityStoryTest
{
    @Test
    void demonstratesRejectionRecoveryCreationAndRelocation() throws Exception
    {
        assertEquals(List.of("Rejected a non-black cat and rolled back explicitly.",
                "Created a black cat with a dwelling.",
                "Relocated the cat to a dwelling while preserving its color."), NightCityStory.run());
    }
}
