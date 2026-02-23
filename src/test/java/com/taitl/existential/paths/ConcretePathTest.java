package com.taitl.existential.paths;

import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class ConcretePathTest
{
    @Test
    void trimsInput()
    {
        ConcretePath path = new ConcretePath(" /app/orders ");

        assertThat(path.toString(), is("/app/orders"));
    }

    @Test
    void detectsParentPresence()
    {
        assertThat(new ConcretePath("/app/orders").hasParent(), is(true));
        assertThat(new ConcretePath("/app").hasParent(), is(false));
    }

    @Test
    void getParentReturnsParentPath()
    {
        ConcretePath path = new ConcretePath("/app/orders/update");

        assertThat(path.getParent().toString(), is("/app/orders"));
    }

    @Test
    void getParentThrowsForTopLevel()
    {
        ConcretePath path = new ConcretePath("/app");

        assertThat(assertThrows(IllegalStateException.class, path::getParent).getMessage(),
                containsString("has no parent key"));
    }

    @Test
    void validateRejectsMissingLeadingSlash()
    {
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            new ConcretePath("app/orders");
        }).getMessage(), containsString("should start with a slash"));
    }

    @Test
    void validateRejectsSingleSlash()
    {
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            new ConcretePath("/");
        }).getMessage(), containsString("cannot be a single slash"));
    }

    @Test
    void validateRejectsEndingSlash()
    {
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            new ConcretePath("/app/orders/");
        }).getMessage(), containsString("end with a slash"));
    }

    @Test
    void validateRejectsWildcards()
    {
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            new ConcretePath("/app/*/update");
        }).getMessage(), containsString("cannot have wildcards"));
    }
}
