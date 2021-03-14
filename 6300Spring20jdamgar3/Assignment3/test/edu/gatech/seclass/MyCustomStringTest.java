package edu.gatech.seclass;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This is a Georgia Tech provided code example for use in assigned private GT repositories. Students and other users of this template
 * code are advised not to share it with other students or to make it available on publicly viewable websites including
 * repositories such as github and gitlab.  Such sharing may be investigated as a GT honor code violation. Created for CS6300.
 *
 * This is an test class for a simple class that represents a string, defined
 * as a sequence of characters.
 *
 * You should implement your tests in this class.  Do not change the test names.
 */

public class MyCustomStringTest {

    private MyCustomStringInterface mycustomstring;

    @Before
    public void setUp() {
        mycustomstring = new MyCustomString();
    }

    @After
    public void tearDown() {
        mycustomstring = null;
    }

    //Test Purpose: This is the first instructor example test.
    @Test
    public void testMostCommonChar1() {
        mycustomstring.setString("I'd b3tt3r put s0me d161ts in this 5tr1n6, right?");
        assertEquals('t', mycustomstring.mostCommonChar());
    }

    //Test Purpose: Ensure that a NullPointerException is thrown if the string has not been initialized
    @Test(expected = NullPointerException.class)
    public void testMostCommonChar2(){
        mycustomstring.mostCommonChar();
    }

    //Test Purpose: Ensure that a NullPointerException is thrown if the string is null
    @Test(expected = NullPointerException.class)
    public void testMostCommonChar3() {
        mycustomstring.setString(null);
        mycustomstring.mostCommonChar();
    }

    //Test Purpose: Ensure that a NullPointerException is thrown if the string is set to an empty value
    @Test(expected = NullPointerException.class)
    public void testMostCommonChar4() {
        mycustomstring.setString("");
        mycustomstring.mostCommonChar();
    }

    //Test Purpose: Ensure that a NullPointerException is thrown if the string contains no alphabetic characters
    @Test(expected = NullPointerException.class)
    public void testMostCommonChar5() {
        mycustomstring.setString("~!@ #$%^&*()_-+=<>?/`|{}[]:;',.");
        mycustomstring.mostCommonChar();
    }

    //Test Purpose: Ensure that counting of characters is case-insensitive
    @Test
    public void testMostCommonChar6() {
        mycustomstring.setString("aaa bbb ccc ddd eee CC 111 222 333 444 555");
        assertEquals('c', mycustomstring.mostCommonChar());
    }

    //Test Purpose: Ensure that ties are broken with character making first occurrence in the string
    @Test
    public void testMostCommonChar7() {
        mycustomstring.setString("9876a 6789a 9876A 6789A");
        assertEquals('9', mycustomstring.mostCommonChar());
    }

    //Test Purpose: This is the second instructor example test.
    @Test
    public void testFilterLetters1() {
        mycustomstring.setString("1234!!! H3y, L3t'5 put 50me d161ts in this 5tr1n6!11!1");
        assertEquals("24Hy,L'pu0med6sinhisrn6", mycustomstring.filterLetters(3, false));
    }

    //Test Purpose: This the third instructor example test.
    @Test
    public void testFilterLetters2() {
        mycustomstring.setString("1234!!! H3y, L3t'5 put 50me d161ts in this 5tr1n6!11!1");
        assertEquals("1!!!  t t  11t  t t1!11!1", mycustomstring.filterLetters(3, true));
    }

    //Test Purpose: Ensure that a NullPointerException is thrown if the current string is null
    @Test(expected = NullPointerException.class)
    public void testFilterLetters3() {
        mycustomstring.setString(null);
        mycustomstring.filterLetters(3, true);
    }

    //Test Purpose: Ensure that an IllegalArgumentException is thrown if n == 0 (edge case)
    @Test(expected = IllegalArgumentException.class)
    public void testFilterLetters4() {
        mycustomstring.setString("I'm soon to fail");
        mycustomstring.filterLetters(0, true);
    }

    //Test Purpose: Ensure that an IllegalArgumentException is thrown if n < 0
    @Test(expected = IllegalArgumentException.class)
    public void testFilterLetters5() {
        mycustomstring.setString("I'm also going to fail!");
        mycustomstring.filterLetters(-33, false);
    }

    //Test Purpose: Ensure that an empty string works OK in the "more" case (edge case)
    @Test
    public void testFilterLetters6() {
        mycustomstring.setString("");
        assertEquals("", mycustomstring.filterLetters(1, true));
    }

    //Test Purpose: Ensure that an empty string works OK in the "less" case (edge case)
    @Test
    public void testFilterLetters7() {
        mycustomstring.setString("");
        assertEquals("", mycustomstring.filterLetters(1, false));
    }

    //Test Purpose: Ensure that if a string does not have any letters occurring fewer than n times,
    //then it will not see any filtering of characters if more==true
    @Test
    public void testFilterLetters8() {
        String orig = "A string with every letter at least twice (2)! A string with every letter at least twice(2)!";
        mycustomstring.setString(orig);
        assertEquals(orig, mycustomstring.filterLetters(1, true));
    }

    //Test Purpose: Ensure that if a string contains letters that all appear fewer than n times,
    //then it will not see any filtering of characters if more==false
    @Test
    public void testFilterLetters9() {
        String orig = "A_string_that_should_not be filtered! 0123456789";
        mycustomstring.setString(orig);
        assertEquals(orig, mycustomstring.filterLetters(6, false));
    }

    //Test Purpose: Ensure that if n==1 and more==false, then a string will be completely
    //filtered out to the empty string if it has content, since every character appears at least once
    @Test
    public void testFilterLetters10() {
        mycustomstring.setString("Th1s string $hould be completely 3mpty wh3n d0ne! ~!@#$%^&*()-_=+`*/");
        assertEquals("", mycustomstring.filterLetters(1, false));
    }

    //Test Purpose: Ensure that if only a single character occurs more than n times with more==true,
    //then that character is all that's left after filtering
    @Test
    public void testFilterLetters11() {
        mycustomstring.setString("#This #is #a #very #special #string #with #a #hidden #character");
        assertEquals("##########", mycustomstring.filterLetters(9, true));
    }

    //Test Purpose: Ensure that capitalization does not matter when filtering out characters that
    //occur greater than or equal to n times with more==false
    @Test
    public void testFilterLetters12() {
        mycustomstring.setString("We_wi11_Weath3r_the w@yW@rd st0rm on th3 w3B?!");
        assertEquals("e_i11_eath3r_the @y@rd st0rm on th3 3B?!", mycustomstring.filterLetters(6, false));
    }

    //Test Purpose: Ensure that capitalization does not matter when filtering out characters that
    //occur less than or equal to n times with more==true
    @Test
    public void testFilterLetters13() {
        mycustomstring.setString("TTThHheEe doOomMmeDD cCchHhaAARRrsSs::: Xx");
        assertEquals("TTThHheEe doOomMmeDD cCchHhaAARRrsSs::: ", mycustomstring.filterLetters(2, true));
    }

    //Test Purpose: This is the fourth instructor example test.
    @Test
    public void testNumberLengthsInSubstring1() {
        mycustomstring.setString("I'd b3tt3r put 50me 123 d161ts in this 5tr1n6, right?");
        mycustomstring.numberLengthsInSubstring(17, 27);
        assertEquals("I'd b3tt3r put 51me 3 d21ts in this 5tr1n6, right?", mycustomstring.getString());
    }

    //Test Purpose: This is the fifth instructor example test, demonstrating a test for an exception.  Your exceptions should be tested in this format.
    @Test(expected = MyIndexOutOfBoundsException.class)
    public void testNumberLengthsInSubstring2() {
        mycustomstring.numberLengthsInSubstring(2, 10);
    }

    //Test Purpose: Ensure that an IllegalArgumentException is thrown if startPosition < 1
    @Test(expected = IllegalArgumentException.class)
    public void testNumberLengthsInSubstring3() {
        mycustomstring.setString("Some string we'll never look at!");
        mycustomstring.numberLengthsInSubstring(0, 10);
    }

    //Test Purpose: Ensure that an IllegalArgumentException is thrown if startPosition > endPosition
    @Test(expected = IllegalArgumentException.class)
    public void testNumberLengthsInSubstring4() {
        mycustomstring.setString("Another useless string...");
        mycustomstring.numberLengthsInSubstring(5, 4);
    }

    //Test Purpose: Ensure that a MyIndexOutOfBoundsException is thrown for a non-empty string if endPosition
    //is greater than the string's length
    @Test(expected = MyIndexOutOfBoundsException.class)
    public void testNumberLengthsInSubstring5() {
        mycustomstring.setString("A string with 27 characters");
        mycustomstring.numberLengthsInSubstring(3, 28);
    }

    //Test Purpose: Ensure that replacement occurs correctly with digits at "position 1" in the string and
    //startPosition==1. Use the broadest possible substring (the whole string)
    @Test
    public void testNumberLengthsInSubstring6() {
        mycustomstring.setString("11111 should be converted to five?!");
        mycustomstring.numberLengthsInSubstring(1, mycustomstring.getString().length());
        assertEquals("5 should be converted to five?!", mycustomstring.getString());
    }

    //Test Purpose: Ensure that replacement occurs correctly with digits at the end of a substring when
    //the substring terminates at the end of the actual string (i.e. the right edge of the string)
    @Test
    public void testNumberLengthsInSubstring7() {
        mycustomstring.setString("We 1 should 21 work 333, even 4321 here: 00505");
        mycustomstring.numberLengthsInSubstring(3, mycustomstring.getString().length());
        assertEquals("We 1 should 2 work 3, even 4 here: 5", mycustomstring.getString());
    }

    //Test Purpose: Ensure that a [startPosition, endPosition] window of one character in length
    //still works, converting the digit at that position to "1"
    @Test
    public void testNumberLengthsInSubstring8() {
        mycustomstring.setString("The long digit: 99999");
        mycustomstring.numberLengthsInSubstring(19, 19);
        assertEquals("The long digit: 99199", mycustomstring.getString());
    }

    //Test Purpose: Ensure that a [startPosition, endPosition] substring window occurring within a
    //longer sequence of digits performs an accurate transformation on the string
    @Test
    public void testNumberLengthsInSubstring9() {
        mycustomstring.setString("Th1s --> 9876543210123456789 <-- should 098 work11 0KKkk");
        mycustomstring.numberLengthsInSubstring(12, 26);
        assertEquals("Th1s --> 981589 <-- should 098 work11 0KKkk", mycustomstring.getString());
    }

    //Test Purpose: Ensure that a [startPosition, endPosition] substring window that straddles two
    //sequences of digits accurately transforms these to a count it sees in each sequence
    @Test
    public void testNumberLengthsInSubstring10() {
        mycustomstring.setString("Straddle time: 11111111 222 222 222 333333333");
        mycustomstring.numberLengthsInSubstring(19, 41);
        assertEquals("Straddle time: 1115 3 3 3 53333", mycustomstring.getString());
    }

}
