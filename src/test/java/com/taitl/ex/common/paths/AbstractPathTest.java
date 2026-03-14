package com.taitl.ex.common.paths;

import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class AbstractPathTest
{
    @Nested
    class Construction
    {
        @Test
        @DisplayName("Accepts root and detects no parent")
        void acceptsRoot()
        {
            AbstractPath path = new AbstractPath("/");

            assertThat(path.hasParent(), is(false));
            assertThat(path.isWildcard(), is(false));
            assertThat(path.toString(), is("/"));
        }

        @Test
        void trimsInput()
        {
            AbstractPath path = new AbstractPath(" /app/orders ");

            assertThat(path.toString(), is("/app/orders"));
        }

        @Test
        @DisplayName("Allows wildcard paths")
        void allowsWildcard()
        {
            AbstractPath path = new AbstractPath("/app/*/update");

            assertThat(path.isWildcard(), is(true));
        }
    }

    @Nested
    class Parent
    {
        @Test
        @DisplayName("Get parent returns parent path")
        void returnsParent()
        {
            AbstractPath path = new AbstractPath("/app/orders/update");

            assertThat(path.getParent().toString(), is("/app/orders"));
        }

        @Test
        @DisplayName("Get parent throws for top level")
        void throwsForTopLevel()
        {
            AbstractPath path = new AbstractPath("/app");

            assertThat(assertThrows(IllegalStateException.class, path::getParent).getMessage(),
                    allOf(containsString("Context key"), containsString("has no parent key")));
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
                new AbstractPath("app/orders");
            }).getMessage(), containsString("should start with a slash"));
        }

        @Test
        @DisplayName("Validate rejects ending slash")
        void rejectsEndingSlash()
        {
            assertThat(assertThrows(IllegalArgumentException.class, () -> {
                new AbstractPath("/app/orders/");
            }).getMessage(), containsString("should not end with a slash"));
        }
    }
}
