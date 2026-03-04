package com.taitl.existential.paths;

import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class ConcretePathTest
{
    @Nested
    class Construction
    {
        @Test
        void trimsInput()
        {
            ConcretePath path = new ConcretePath(" /app/orders ");

            assertThat(path.toString(), is("/app/orders"));
        }

        @Test
        @DisplayName("Detects parent presence")
        void detectsParentPresence()
        {
            assertThat(new ConcretePath("/app/orders").hasParent(), is(true));
            assertThat(new ConcretePath("/app").hasParent(), is(false));
        }
    }

    @Nested
    class Parent
    {
        @Test
        @DisplayName("Get parent returns parent path")
        void returnsParent()
        {
            ConcretePath path = new ConcretePath("/app/orders/update");

            assertThat(path.getParent().toString(), is("/app/orders"));
        }

        @Test
        @DisplayName("Get parent throws for top level")
        void throwsForTopLevel()
        {
            ConcretePath path = new ConcretePath("/app");

            assertThat(assertThrows(IllegalStateException.class, path::getParent).getMessage(),
                    allOf(containsString("Operation key"), containsString("has no parent key")));
        }
    }

    @Nested
    class Validation
    {
        @Test
        @DisplayName("Validate rejects missing leading slash")
        void rejectsMissingLeadingSlash()
        {
            assertThat(assertThrows(IllegalArgumentException.class, () -> {
                new ConcretePath("app/orders");
            }).getMessage(), containsString("should start with a slash"));
        }

        @Test
        @DisplayName("Validate rejects single slash")
        void rejectsSingleSlash()
        {
            assertThat(assertThrows(IllegalArgumentException.class, () -> {
                new ConcretePath("/");
            }).getMessage(), containsString("cannot be a single slash"));
        }

        @Test
        @DisplayName("Validate rejects ending slash")
        void rejectsEndingSlash()
        {
            assertThat(assertThrows(IllegalArgumentException.class, () -> {
                new ConcretePath("/app/orders/");
            }).getMessage(), containsString("end with a slash"));
        }

        @Test
        @DisplayName("Validate rejects wildcards")
        void rejectsWildcards()
        {
            assertThat(assertThrows(IllegalArgumentException.class, () -> {
                new ConcretePath("/app/*/update");
            }).getMessage(), containsString("cannot have wildcards"));
        }
    }
}
