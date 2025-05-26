package com.taitl.existential.examples.night_city.model;

/**
 * Describes of how something looks, by attaching textual description to an object.
 * Illustrates the use of the library with generic types.
 *
 * Examples:
 *   Look<Mouse>
 *   Look<House>
 *   Look<Being>
 *   Look<Building>
 *   Look<Dwelling<Cat>>
 *
 * @param <T>
 */
public class Look<T>
{
    T something;
    String description;

    public Look(T something, String description)
    {
        this.something = something;
    }
}
