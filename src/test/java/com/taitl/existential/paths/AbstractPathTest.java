package com.taitl.existential.paths;

import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class AbstractPathTest
{
    @Test
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
    void allowsWildcardPaths()
    {
        AbstractPath path = new AbstractPath("/app/*/update");

        assertThat(path.isWildcard(), is(true));
    }

    @Test
    void getParentReturnsParentPath()
    {
        AbstractPath path = new AbstractPath("/app/orders/update");

        assertThat(path.getParent().toString(), is("/app/orders"));
    }

    @Test
    void getParentThrowsForTopLevel()
    {
        AbstractPath path = new AbstractPath("/app");

        assertThat(assertThrows(IllegalStateException.class, path::getParent).getMessage(),
                containsString("has no parent key"));
    }

    @Test
    void validateRejectsMissingLeadingSlash()
    {
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            new AbstractPath("app/orders");
        }).getMessage(), containsString("should start with a slash"));
    }

    @Test
    void validateRejectsEndingSlash()
    {
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            new AbstractPath("/app/orders/");
        }).getMessage(), containsString("should not end with a slash"));
    }
}
