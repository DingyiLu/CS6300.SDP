package edu.gatech.seclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents an implementation of the MyCustomStringInterface interface provided as a part of Assignment 3
 * for the CS6300 course.
 *
 * This class represents the implementation of the concept of a string, defined as a sequence of characters.
 *
 * @author James Damgar (jdamgar3@gatech.edu)
 */
public class MyCustomString implements MyCustomStringInterface {

    /**
     * Class-level set of "alphabetic" characters
     */
    public static final List<String> ALPHABET = List.of(
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
            "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
            "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6",
            "7", "8", "9"
    );

    /**
     * Class-level set of "digit" characters
     */
    public static final List<String> DIGITS = List.of(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    );

    /**
     * Private instance-level actual string representation
     */
    private String str = null;

    /**
     * Returns the current string.
     * If the string is null, or has not been set to a value with setString, it should return null.
     *
     * @return Current string
     */
    public String getString() {
        return this.str;
    }

    /**
     * Sets the value of the current string
     *
     * @param string The value to be set
     */
    public void setString(String string) {
        this.str = string;
    }

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
    public char mostCommonChar() {

        // The most common character
        char mostCommon;

        // Check for null or empty string case (also covers if this has not yet been assigned a value)
        if(this.str == null || this.str.length() == 0) {
            throw new NullPointerException();
        }
        mostCommon = this.str.charAt(0);

        // Create a hash mapping from valid alphabetic characters to counts
        HashMap<String, Integer> charCount = new HashMap<>();
        for(String c : ALPHABET) {
            charCount.put(c, 0);
        }

        // Iterate over the current string, incrementing counters for alphabetic characters
        for(int i = 0; i < this.str.length(); i++) {
            String c = String.valueOf(this.str.charAt(i)).toLowerCase();
            if(ALPHABET.contains(c)) {
                charCount.put(c, charCount.get(c) + 1);
            }
        }

        // Iterate over the hash, looking for the most common character(s)
        int best = 0;
        for(String c : ALPHABET) {
            int val = charCount.get(c);
            if(val > best) {
                best = val;
            }
        }

        // Throw an exception if there were no alphabetic characters
        if(best == 0) {
            throw new NullPointerException();
        }

        // Find the most common characters
        ArrayList<String> mostCommons = new ArrayList<>();
        for(String c : ALPHABET) {
            if(charCount.get(c) == best) {
                mostCommons.add(c);
            }
        }

        // If this is a single character, then we know the answer
        if(mostCommons.size() == 1) {
            mostCommon = mostCommons.get(0).charAt(0);
        }
        else {
            // Otherwise, return the most common character which appears first in the string
            for(int i = 0; i < this.str.length(); i++) {
                String c = String.valueOf(this.str.charAt(i)).toLowerCase();
                if(mostCommons.contains(c)) {
                    mostCommon = c.charAt(0);
                    break;
                }
            }
        }

        // Return the "first" most common character, factoring in ties
        return mostCommon;
    }

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
    public String filterLetters(int n, boolean more) {

        // The filtered string
        StringBuilder filtered = new StringBuilder();

        // Check for null string
        if(this.str == null) {
            throw new NullPointerException();
        }

        // Check for illegal argument
        if(n <= 0) {
            throw new IllegalArgumentException();
        }

        // Find all of the unique, case-insensitive characters in the string
        HashMap<String, Integer> charCount = new HashMap<>();
        for(int i = 0; i < this.str.length(); i++) {
            String c = String.valueOf(this.str.charAt(i)).toLowerCase();

            // Unique only
            if(charCount.containsKey(c)) {
                // Seen before
                charCount.put(c, charCount.get(c) + 1);
            }
            else {
                // First occurrence
                charCount.put(c, 1);
            }
        }

        // Process the string based on the parameters of this method.
        for(int i = 0; i < this.str.length(); i++) {
            String c = String.valueOf(this.str.charAt(i));
            String cl = c.toLowerCase();
            if(more) {
                // Remove characters appearing less than or equal to n times
                if(charCount.get(cl) > n) {
                    filtered.append(c);
                }
            }
            else {
                // Remove characters appearing equal to or more than n times
                if(charCount.get(cl) < n) {
                    filtered.append(c);
                }
            }
        }

        // Return the filtered string
        return filtered.toString();
    }

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
    public void numberLengthsInSubstring(int startPosition, int endPosition) {

        // Throw an exception if we're outside our custom bounds
        if(this.str == null || endPosition > this.str.length()) {
            throw new MyIndexOutOfBoundsException();
        }

        // Throw an exception if startPosition or endPosition are not legal
        if(startPosition < 1 || startPosition > endPosition) {
            throw new IllegalArgumentException();
        }

        // Produce our transformed string
        StringBuilder newStr = new StringBuilder();
        int run = 0;  // The current "run" length of digits
        for(int i = 0; i < this.str.length(); i++) {

            // Fetch current character as a string
            String c = String.valueOf(this.str.charAt(i));

            // Only consider within the specific bounds (adjust for 0-indexing)
            if(i+1 >= startPosition && i+1 <= endPosition) {

                // Check if this is a digit
                if(DIGITS.contains(c)) {

                    // If so, increment our "run" counter
                    run++;
                }
                else {
                    // Otherwise, if we were in a run of digits, output the count as a character
                    // in the transformed string
                    if(run > 0) {
                        newStr.append(run);
                        run = 0;
                    }

                    // Regardless, also add the current non-digit
                    newStr.append(c);
                }
            }
            else {
                // If a run was in progress and we just reached the end of the scope, output the run
                if(run > 0) {
                    newStr.append(run);
                    run = 0;
                }

                // Add the current character outside the positional scope
                newStr.append(c);
            }
        }

        // Check if the string ended and we need to output the last run count
        if(run > 0) {
            newStr.append(run);
        }

        // Change string to transformed string
        this.str = newStr.toString();
    }
}
