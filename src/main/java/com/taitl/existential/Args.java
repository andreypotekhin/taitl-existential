package com.taitl.helper;

/**
 * Lightweight checking/validations for method arguments.
 * Throws IllegalArgumentException if a condition is not met.
 * 
 * This class just throws an exception. If more processing needed,
 * for instance, notifying an observer, consider Debug.require().
 *  
 * @author Andrey Potekhin
 * 
 * @see State
 * @see Outcome
 */
public class Args
{
  /**
   * Protected constructor for an utility class.
   */
  protected Args()
  {
  }

  /**
   * Throws IllegalArgumentException if method argument is null.
   */
  public static void cool(Object o, String argName)
  {
    if (o == null)
    {
      throw new IllegalArgumentException(String.format("Argument '%s' must not be null", argName));
    }
  }

  /**
   * Throws IllegalArgumentException if condition not met.
   */
  public static void require(boolean condition, String message)
  {
    if (!condition)
    {
      throw new IllegalArgumentException(message);
    }
  }
}
