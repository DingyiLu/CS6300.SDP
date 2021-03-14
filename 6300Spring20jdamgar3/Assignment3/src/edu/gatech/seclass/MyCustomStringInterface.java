package edu.gatech.seclass;

/**
 * This is a Georgia Tech provided code example for use in assigned private GT repositories. Students and other users of this template
 * code are advised not to share it with other students or to make it available on publicly viewable websites including
 * repositories such as github and gitlab.  Such sharing may be investigated as a GT honor code violation. Created for CS6300.
 *
 * This is an interface for a simple class that represents a string, defined
 * as a sequence of characters.
 *
 * This interface should NOT be altered in any way.
 */
public interface MyCustomStringInterface {

    /**
     * Returns the current string.
     * If the string is null, or has not been set to a value with setString, it should return null.
     *
     * @return Current string
     */
    String getString();

    /**
     * Sets the value of the current string
     *
     * @param string The value to be set
     */
    void setString(String string);

    /**
     * Returns the alphabetic character which appears most often in the string (capitalization insensitive).
     * Non-alphabetic characters are not counted.  If two alphabetic characters appear an equal number of times,
     * return the first to appear in the string.
     *
     * If the current string is null, empty, contains no alphabetic characters, or has not been set to a value,
     * the method should throw a NullPointerException.
     *
     *
     * Examples:
     * - mostCommonChar would return m for string "My lucky numbers are 5, 25, and 12.".
     *
     * @throws NullPointerException  If the current string is null, empty, contains no alphabetic characters, or has
     * not been set to a value.
     * @return the alphabetic character which appears most frequently in the string
     */
    char mostCommonChar();

    /**
     * Returns a string equivalent to the original string after removing all of the characters which appear in the string
     * either >= or <= the number of times (n) input, with letters being capitalization insensitive.
     *
     * If 'more' is true, all characters appearing less than or equal to n times will be removed in the returned string.
     * If 'more' is false, all characters appearing greater than or equal to n times will be removed in the returned string.
     *
     * Examples:
     * - For n=2 and more=false, "HELLO 98, byebye 2" would be converted to "HO98,2".
     * - For n=2 and more=true, "HELLO 98, byebye 2" would be converted to "E  ee ".
     *
     * @param n number of times a character must appear to be removed or not (depending on 'more' value)
     * @param more Boolean that indicates whether characters appearing <= or >= n times will be removed
     * @return String with the indicated characters removed
     * @throws NullPointerException     If the current string is null
     * @throws IllegalArgumentException If n is not an integer > 0.
     */
    String filterLetters(int n, boolean more);

    /**
     * Replace the individual numbers in the current string, between startPosition and endPosition
     * (included), where a number is defined as a continuous sequence of digits, with the length of each replaced number
     * The first character in the string is considered to be in Position 1.
     *
     *
     * Examples:
     * - String "0460" would be converted to "4"
     * - String "326 abc 123" would be converted to "3 abc 3"
     *
     * @param startPosition Position of the first character to consider
     * @param endPosition   Position of the last character to consider

     * @throws IllegalArgumentException    If "startPosition" < 1 or "startPosition" > "endPosition"
     * @throws MyIndexOutOfBoundsException If "endPosition" is out of bounds (greater than the length of the string)
     * and 1 <= "startPosition" <= "endPosition"
     */
    void numberLengthsInSubstring(int startPosition, int endPosition);
}
