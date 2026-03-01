package com.taitl.existential.paths;

import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class AbstractPathTest
{
    @Test
    @DisplayName("Accepts root and detects no parent")
    void acceptsRootAndDetectsNoParent()
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
    void allowsWildcardPaths()
    {
        AbstractPath path = new AbstractPath("/app/*/update");

        assertThat(path.isWildcard(), is(true));
    }

    @Test
    @DisplayName("Get parent returns parent path")
    void getParentReturnsParentPath()
    {
        AbstractPath path = new AbstractPath("/app/orders/update");

        assertThat(path.getParent().toString(), is("/app/orders"));
    }

    @Test
    @DisplayName("Get parent throws for top level")
    void getParentThrowsForTopLevel()
    {
        AbstractPath path = new AbstractPath("/app");

        assertThat(assertThrows(IllegalStateException.class, path::getParent).getMessage(),
                allOf(containsString("Context key"), containsString("has no parent key")));
    }

    @Test
    @DisplayName("Validate rejects missing leading slash")
    void validateRejectsMissingLeadingSlash()
    {
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            new AbstractPath("app/orders");
        }).getMessage(), containsString("should start with a slash"));
    }

    @Test
    @DisplayName("Validate rejects ending slash")
    void validateRejectsEndingSlash()
    {
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            new AbstractPath("/app/orders/");
        }).getMessage(), containsString("should not end with a slash"));
    }
}
