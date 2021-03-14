package edu.gatech.seclass.encode;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyMainTest {

    private ByteArrayOutputStream outStream;
    private ByteArrayOutputStream errStream;
    private PrintStream outOrig;
    private PrintStream errOrig;
    private Charset charset = StandardCharsets.UTF_8;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        errStream = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errStream);
        outOrig = System.out;
        errOrig = System.err;
        System.setOut(out);
        System.setErr(err);
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(outOrig);
        System.setErr(errOrig);
    }

    /*
     *  TEST UTILITIES
     */

    // Create File Utility
    private File createTmpFile() throws Exception {
        File tmpfile = temporaryFolder.newFile();
        tmpfile.deleteOnExit();
        return tmpfile;
    }

    // Write File Utility
    private File createInputFile(String input) throws Exception {
        File file =  createTmpFile();

        OutputStreamWriter fileWriter =
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);

        fileWriter.write(input);

        fileWriter.close();
        return file;
    }


    //Read File Utility
    private String getFileContent(String filename) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /*
     * TEST FILE CONTENT
     */
    private static final String SMALLFILE1 = "@bcd$ 98&^";
    private static final String SMALLFILE2 = "~! #$ ^&*(";
    private static final String SMALLFILE3 = "qwe1RTY2as";
    private static final String LARGEFILE1 = "abc defgh ijklmno pqrstu vwxyz 123456 789 0AB CDEF GHIJKL MNOPQRS TUVWXYZ ~!@#$%^&*(){}[];':12,./<>?";
    private static final String REMOVECHARS1 = "aBcDe";
    private static final String REMOVECHARS2 = "DeFGhi";
    private static final String KEEPCHARS1 = "fGhIj";
    private static final String KEEPCHARS2 = "bdXYZ";
    private static final String KEEPCHARS3 = "cccZZ";
    private static final String KEEPCHARS4 = "weaXXZ";
    private static final String DELIMCHARS1 = "knqtvKNQTV"; // Modified for upper/lowercase
    private static final String DELIMCHARS2 = "$&^)(";
    private static final String DELIMCHARS3 = "ad9yz";


    // test cases

    /*
     *   TEST CASES
     */

    // Purpose: In a larger file with all types of characters, with whitespace as a delimiter, test the removal of 5
    //   different alphanumeric characters, with reverse capitalization.
    // NOTE: Modified from Assignment 6 due to an original misunderstanding of the behavior of the "-w" flag.
    // Frame #: Test Case 43 		(Key = 3.1.1.0.1.2.1.1.)
    @Test
    public void encodeTest1() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-w", "-r", REMOVECHARS1, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = " HGF ONMLKJI UTSRQP ZYXWV 654321 987 0 f lkjihg srqponm zyxwvut ?></.,21:';][}{)(*&^%$#@!~";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a larger file with all types of characters, with whitespace as a delimiter, test the removal of 5
    //   different alphanumeric characters, *no* reverse capitalization
    // NOTE: Modified from Assignment 6 due to an original misunderstanding of the behavior of the "-w" flag.
    // Frame #: Test Case 44 		(Key = 3.1.1.0.1.2.1.2.)
    @Test
    public void encodeTest2() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-w", "-r", REMOVECHARS1, inputFile.getPath()};
        Main.main(args);

        String expected = " hgf onmlkji utsrqp zyxwv 654321 987 0 F LKJIHG SRQPONM ZYXWVUT ?></.,21:';][}{)(*&^%$#@!~";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a larger file with all types of characters, with whitespace as a delimiter, test keeping 5
    //   different alphanumeric characters and removing the rest, with reverse capitalization
    // NOTE: Modified from Assignment 6 due to an original misunderstanding of the behavior of the "-w" flag.
    // NOTE: Modified with Deliverable #2 to account for keeping alphanumeric instead of just alphabetic characters.
    // Frame #: Test Case 45 		(Key = 3.1.1.0.2.2.1.1.)
    @Test
    public void encodeTest3() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-w", "-k", KEEPCHARS1, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = " HGF JI      f jihg   ?></.,:';][}{)(*&^%$#@!~";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a larger file with all types of characters, with whitespace as a delimiter, test keeping 5
    //   different alphanumeric characters and removing the rest, *no* reverse capitalization
    // NOTE: Modified from Assignment 6 due to an original misunderstanding of the behavior of the "-w" flag.
    // NOTE: Modified with Deliverable #2 to account for keeping alphanumeric instead of just alphabetic characters.
    // Frame #: Test Case 46 		(Key = 3.1.1.0.2.2.1.2.)
    @Test
    public void encodeTest4() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-w", "-k", KEEPCHARS1, inputFile.getPath()};
        Main.main(args);

        String expected = " hgf ji      F JIHG   ?></.,:';][}{)(*&^%$#@!~";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a larger file with all types of characters, use 5 alphanumeric delimiters, test the removal of 5
    //   different alphanumeric characters, with reverse capitalization
    // NOTE: Modified with Deliverable #2 to account for keeping alphanumeric instead of just alphabetic characters,
    //       skipping transformations on delimiters, and using "-d" to dictate delimiters.
    // Frame #: Test Case 47 		(Key = 3.1.2.1.1.2.1.1.)
    // Failure Type: BUG. Reveals Bug #11.
    @Test
    public void encodeTest5() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-w", "-d", DELIMCHARS1, "-r", REMOVECHARS1, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "JI HGF kMLnP OqSRt Uvjihg f 0 987 654321 ZYXWKm lNpoQ srTuV?></.,21:';][}{)(*&^%$#@!~ zyxw";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a larger file with all types of characters, use 5 alphanumeric delimiters, test the removal of 5
    //   different alphanumeric characters, *no* reverse capitalization
    // NOTE: Modified with Deliverable #2 to account for keeping alphanumeric instead of just alphabetic characters,
    //       skipping transformations on delimiters, and using "-d" to dictate delimiters.
    // Frame #: Test Case 48 		(Key = 3.1.2.1.1.2.1.2.)
    @Test
    public void encodeTest6() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-w", "-d", DELIMCHARS1, "-r", REMOVECHARS1, inputFile.getPath()};
        Main.main(args);

        String expected = "ji hgf kmlnp oqsrt uvJIHG F 0 987 654321 zyxwKM LNPOQ SRTUV?></.,21:';][}{)(*&^%$#@!~ ZYXW";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a smaller file with all types of characters, test the removal of 5
    //   different alphanumeric characters, with reverse capitalization. Use whitespace
    //   as a delimiter.
    // NOTE: Modified from Assignment 6 due to an original misunderstanding of the behavior of the "-w" flag.
    // Frame #: Test Case 11 		(Key = 2.1.1.0.1.2.1.1.)
    @Test
    public void encodeTest7() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-w", "-r", REMOVECHARS2, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "$CB@ ^&89";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a smaller file with all types of characters, test the removal of 5
    //   different alphanumeric characters, *no* reverse capitalization. Use whitespace
    //   as a delimiter.
    // NOTE: Modified from Assignment 6 due to an original misunderstanding of the behavior of the "-w" flag.
    // Frame #: Test Case 12 		(Key = 2.1.1.0.1.2.1.2.)
    @Test
    public void encodeTest8() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-w", "-r", REMOVECHARS2, inputFile.getPath()};
        Main.main(args);

        String expected = "$cb@ ^&89";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a smaller file with all types of characters, test using a set of special character delimiters,
    //   test the removal of 5 different alphanumeric characters, with reverse capitalization
    // NOTE: Modified with Deliverable #2 to account for keeping alphanumeric instead of just alphabetic characters,
    //       skipping transformations on delimiters, and using "-d" to dictate delimiters.
    // Frame #: Test Case 19 		(Key = 2.1.2.2.1.2.1.1.)
    @Test
    public void encodeTest9() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-w", "-d", DELIMCHARS2, "-r", REMOVECHARS2, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "CB@$89 &^";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a smaller file with all types of characters, test using a set of special character delimiters,
    //   test the removal of 5 different alphanumeric characters, *no* reverse capitalization
    // NOTE: Modified with Deliverable #2 to account for keeping alphanumeric instead of just alphabetic characters,
    //       skipping transformations on delimiters, and using "-d" to dictate delimiters.
    // Frame #: Test Case 20 		(Key = 2.1.2.2.1.2.1.2.)
    @Test
    public void encodeTest10() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-w", "-d", DELIMCHARS2, "-r", REMOVECHARS2, inputFile.getPath()};
        Main.main(args);

        String expected = "cb@$89 &^";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Test that files containing all special characters can be handled.
    // NOTE: Modified after a better understanding of how the "-w" option works.
    // Frame #: Test Case 3  		<single>  (follows [if])
    @Test
    public void encodeTest11() throws Exception {
        File inputFile = createInputFile(SMALLFILE2);

        String args[] = {inputFile.getPath()};
        Main.main(args);

        String expected = "~! #$ ^&*(";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Test and verify that attempting to use special characters as keep characters are ignored.
    // NOTE: This may generate some kind of error response instead by the application.
    // NOTE: Fixed since Assignment 6 to account for this error condition.
    // NOTE: Changed with deliverable #2 to tolerate non-alphanumeric characters in the "keep" list. This has
    //       the effect of not "keeping" any alpha-numeric characters and leaving all other characters unchanged.
    // Frame #: Test Case 9  		<error>  (follows [if])
    //
    // Failure Type: BUG. Reveals Bug #10. I believe this bug to be an inability for the application
    // to recognize non-alphanumeric characteres as "keep" (-k) or "remove" (-r) characters, which causes
    // an IndexOutOfBoundException while parsing the command-line input.
    @Test
    public void encodeTest12() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-k", "@#$%^", inputFile.getPath()};
        Main.main(args);

        String expected = "@$ &^";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Test whitespace characters can be explicitly defined for use as delimiters.
    // NOTE: Fixed since Assignment 6 since I forgot two of the removal characters.
    // NOTE: Modified with deliverable #2 to account for "-d" to specify delimiters.
    // Frame #: Test Case 56 		(Key = 3.1.2.3.1.2.1.2.)
    @Test
    public void encodeTest13() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-w", "-d", "     ", "-r", REMOVECHARS1, inputFile.getPath()};
        Main.main(args);

        String expected = " hgf onmlkji utsrqp zyxwv 654321 987 0 F LKJIHG SRQPONM ZYXWVUT ?></.,21:';][}{)(*&^%$#@!~";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Test that no removals/keeps occur when (-r|-k) parameters are not specified.
    // NOTE: Fixed with deliverable #2 to account for default behavior of applying "-w" with non-alphanumeric delimiters.
    // Frame #: Test Case 6  		<single>  (follows [if])
    @Test
    public void encodeTest14() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {inputFile.getPath()};
        Main.main(args);

        String expected = "cba hgfed onmlkji utsrqp zyxwv 654321 987 BA0 FEDC LKJIHG SRQPONM ZYXWVUT ~!@#$%^&*(){}[];':21,./<>?";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Test that an empty file works and results in an empty response.
    // Frame #: Test Case 1  		<single>
    //
    // Failure Type: BUG. Reveals Bug #7. I believe this bug to be the utility still interpretting the "-w" option
    // as also taking a list of delimiter characters.
    @Test
    public void encodeTest15() throws Exception {
        File inputFile = createInputFile("");

        String args[] = {"-w", DELIMCHARS1, "-r", REMOVECHARS1, inputFile.getPath()};
        Main.main(args);

        String expected = "";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    /**
     * The following 15 additional tests were added for Deliverable #1 of
     * the personal project.
     */

    // Purpose: Test that whitespace characters are an invalid argument to "-r"  or "-k".
    // NOTE: Modified since Assignment 6 since the implementation I chose to go with treats non-alphabetic
    //       arguments to "-r" or "-k" as an error.
    // NOTE: Modified with deliverable #2 to account for "-d" as the delimiter option and that non-alphanumeric
    //       characters with the "-r" or "-k" options are ignored.
    // Frame #: Test Case 10 		<error>  (follows [if])
    //
    // Failure Type: BUG. Reveals Bug #10. I believe this bug to be an inability for the application
    // to recognize non-alphanumeric characteres as "keep" (-k) or "remove" (-r) characters, which causes
    // an IndexOutOfBoundException while parsing the command-line input.
    @Test
    public void encodeTest16() throws Exception {
        File inputFile = createInputFile(SMALLFILE2);

        String args[] = {"-w", "-d", "$", "-r", "      ", inputFile.getPath()};
        Main.main(args);

        String expected = "# !~$(*&^ ";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with all kinds of characters and no delimiter specified,
    //   test keeping 5 alphanumeric characters, with reverse capitalization.
    // NOTE: Modified with deliverable #2 so that "-k" and "-r" effect alphanumeric characters and not just alphabetic.
    // Frame #: Test Case 13 		(Key = 2.1.1.0.2.2.1.1.)
    @Test
    public void encodeTest17() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-k", KEEPCHARS2, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "@BD$ &^";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with all kinds of characters and no delimiter specified,
    //   test keeping 5 alphanumeric characters, *without* reverse capitalization.
    // NOTE: Modified with deliverable #2 so that "-k" and "-r" effect alphanumeric characters and not just alphabetic.
    // Frame #: Test Case 14 		(Key = 2.1.1.0.2.2.1.2.)
    @Test
    public void encodeTest18() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-k", KEEPCHARS2, inputFile.getPath()};
        Main.main(args);

        String expected = "@bd$ &^";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with all kinds of characters, test with 5 alphanumeric delimiters,
    //   the removal of 5 alphanumeric characters, with reverse capitalization.
    // NOTE: Updated with deliverable #2 to account for "-d" as the delimiter option and for
    //       ignoring delimiter characters in transformations.
    // Frame #: Test Case 15 		(Key = 2.1.2.1.1.2.1.1.)
    @Test
    public void encodeTest19() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-w", "-d", DELIMCHARS3,"-r", REMOVECHARS2, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "CB@d $9^&8";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with all kinds of characters, test with 5 alphanumeric delimiters,
    //   the removal of 5 alphanumeric characters, *without* reverse capitalization.
    // NOTE: Updated with deliverable #2 to account for "-d" as the delimiter option and for
    //       ignoring delimiter characters in transformations.
    // Frame #: Test Case 16 		(Key = 2.1.2.1.1.2.1.2.)
    @Test
    public void encodeTest20() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-w", "-d", DELIMCHARS3,"-r", REMOVECHARS2, inputFile.getPath()};
        Main.main(args);

        String expected = "cb@d $9^&8";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with all kinds of characters, test with 5 alphanumeric delimiters,
    //   the keeping only 5 alphanumeric characters, with reverse capitalization.
    // NOTE: Updated with deliverable #2 to account for "-d" as the delimiter option and for
    //       ignoring delimiter characters in transformations.
    // Frame #: Test Case 17 		(Key = 2.1.2.1.2.2.1.1.)
    @Test
    public void encodeTest21() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-w", "-d", DELIMCHARS3, "-k", KEEPCHARS3, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "C@d $9^&";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with all kinds of characters, test with 5 alphanumeric delimiters,
    //   the keeping only 5 alphanumeric characters, *without* reverse capitalization.
    // NOTE: Updated with deliverable #2 to account for "-d" as the delimiter option and for
    //       ignoring delimiter characters in transformations.
    // Frame #: Test Case 18 		(Key = 2.1.2.1.2.2.1.2.)
    @Test
    public void encodeTest22() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-w", "-d", DELIMCHARS3, "-k", KEEPCHARS3, inputFile.getPath()};
        Main.main(args);

        String expected = "c@d $9^&";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with alphanumeric characters and no delimiter specified,
    //   test the removal of 5 alphanumeric characters with reverse capitalization.
    // Frame #: Test Case 27 		(Key = 2.2.1.0.1.2.1.1.)
    @Test
    public void encodeTest23() throws Exception {
        File inputFile = createInputFile(SMALLFILE3);

        String args[] = {"-r", REMOVECHARS1, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "QW1rty2S";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with alphanumeric characters and no delimiter specified,
    //   test the removal of 5 alphanumeric characters *without* reverse capitalization.
    // Frame #: Test Case 28 		(Key = 2.2.1.0.1.2.1.2.)
    @Test
    public void encodeTest24() throws Exception {
        File inputFile = createInputFile(SMALLFILE3);

        String args[] = {"-r", REMOVECHARS1, inputFile.getPath()};
        Main.main(args);

        String expected = "qw1RTY2s";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with alphanumeric characters and no delimiter specified,
    //   test the keeping of only 5 alphanumeric characters with reverse capitalization.
    // NOTE: Modified with deliverable #2 to account for alphanumeric rather than alphabetic characters
    //       being applicable for "-k" and "-r".
    // Frame #: Test Case 29 		(Key = 2.2.1.0.2.2.1.1.)
    @Test
    public void encodeTest25() throws Exception {
        File inputFile = createInputFile(SMALLFILE3);

        String args[] = {"-k", KEEPCHARS4, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "WEA";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with alphanumeric characters and no delimiter specified,
    //   test the keeping of only 5 alphanumeric characters *without* reverse capitalization.
    // NOTE: Modified with deliverable #2 to account for alphanumeric rather than alphabetic characters
    //       being applicable for "-k" and "-r".
    // Frame #: Test Case 30 		(Key = 2.2.1.0.2.2.1.2.)
    @Test
    public void encodeTest26() throws Exception {
        File inputFile = createInputFile(SMALLFILE3);

        String args[] = {"-k", KEEPCHARS4, inputFile.getPath()};
        Main.main(args);

        String expected = "wea";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with alphanumeric characters, with 5 alphanumeric delimiters,
    //   test the removal of 5 alphanumeric characters, with reverse capitalization.
    // NOTE: Modified with deliverable #2 to use "-d" for delimiters and ignore delimiters in transformations.
    // Frame #: Test Case 31 		(Key = 2.2.2.1.1.2.1.1.)
    @Test
    public void encodeTest27() throws Exception {
        File inputFile = createInputFile(SMALLFILE3);

        String args[] = {"-w", "-d", DELIMCHARS3, "-r", REMOVECHARS1, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "2ytr1WQaS";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with alphanumeric characters, with 5 alphanumeric delimiters,
    //   test the removal of 5 alphanumeric characters, *without* reverse capitalization.
    // NOTE: Modified with deliverable #2 to use "-d" for delimiters and ignore delimiters in transformations.
    // Frame #: Test Case 32 		(Key = 2.2.2.1.1.2.1.2.)
    @Test
    public void encodeTest28() throws Exception {
        File inputFile = createInputFile(SMALLFILE3);

        String args[] = {"-w", "-d", DELIMCHARS3, "-r", REMOVECHARS1, inputFile.getPath()};
        Main.main(args);

        String expected = "2YTR1wqas";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with alphanumeric characters, with 5 alphanumeric delimiters,
    //   test the keeping of only 5 alphanumeric characters, with reverse capitalization.
    // NOTE: Modified with deliverable #2 to use "-d" for delimiters and ignore delimiters in transformations.
    // Frame #: Test Case 33 		(Key = 2.2.2.1.2.2.1.1.)
    @Test
    public void encodeTest29() throws Exception {
        File inputFile = createInputFile(SMALLFILE3);

        String args[] = {"-w", "-d", DELIMCHARS3, "-k", KEEPCHARS4, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "EWa";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a small file with alphanumeric characters, with 5 alphanumeric delimiters,
    //   test the keeping of only 5 alphanumeric characters, *without* reverse capitalization.
    // NOTE: Modified with deliverable #2 to use "-d" for delimiters and ignore delimiters in transformations.
    // Frame #: Test Case 34 		(Key = 2.2.2.1.2.2.1.2.)
    @Test
    public void encodeTest30() throws Exception {
        File inputFile = createInputFile(SMALLFILE3);

        String args[] = {"-w", "-d", DELIMCHARS3, "-k", KEEPCHARS4, inputFile.getPath()};
        Main.main(args);

        String expected = "ewa";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }


    /**
     * Additional tests added for Deliverable #3 to expose tests for an alternative encode version.
     *
     * NOTE: These test cases are not related to any particular Test Frames from earlier assignments.
     *
     * NOTE: Some of these bugs were exposed by my original tests cases above. Below, I try to zero in on
     * the various bugs to make sure they indicate what I *think* they mean.
     *
     */

    // Purpose: Expose the following bug:
    //
    // Failure Type: BUG. Reveals Bug #11. I believe this bug to be that when using reverse capitalization (-c),
    // delimiter characters are removed.
    @Test
    public void encodeTest31() throws Exception {
        File inputFile = createInputFile("abcdefg");

        String args[] = {"-d", "d", "-r", "d", "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "ABCDEFG";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Expose the following bug:
    //
    // Failure Type: BUG. Reveals Bug #8. I believe this bug to be that when using reverse capitalization (-c),
    // this operation is done first, with the delimiter character check done afterwards. This causes alphabetic
    // characters that were delimiters to be interpretted as non-delimiters and vice-versa.
    @Test
    public void encodeTest32() throws Exception {
        File inputFile = createInputFile("abcdefg");

        String args[] = {"-d", "D", "-k", "abfg", "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "ABFG";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Expose the following bug:
    //
    // Failure Type: BUG. Reveals Bug #10. I believe this bug to be an inability for the application
    // to recognize non-alphanumeric characteres as "keep" (-k) or "remove" (-r) characters, which causes
    // an IndexOutOfBoundException while parsing the command-line input.
    @Test
    public void encodeTest33() throws Exception {
        File inputFile = createInputFile("abc 123 efg");

        String args[] = {"-d", "2", "-k", "@#$", inputFile.getPath()};
        Main.main(args);

        String expected = "  ";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Expose the following bug:
    //
    // Failure Type: BUG. Reveals Bug #4. I believe that this bug is that the provided application allows for
    // specifying more than one replacement character (-x), even though this should be a single character argument.
    @Test
    public void encodeTest34() throws Exception {
        File inputFile = createInputFile("abc 123@#$ efg");

        String args[] = {"-d", "123", "-k", "abfg", "-x", "**", inputFile.getPath()};
        Main.main(args);

        String expected = "ab *@#$ fg";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Expose the following bug:
    //
    // Failure Type: BUG. Reveals Bug #1. I believe that this bug is that the use of multiple instances
    // of an option causes the option not to be applied (but there is, however, no error message).
    @Test
    public void encodeTest35() throws Exception {
        File inputFile = createInputFile("abc 123 efg");

        String args[] = {"-d", " 2", "-k", "abfg", "-x", "*", "-x", "*", inputFile.getPath()};
        Main.main(args);

        String expected = "ab*fg";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Expose the following bug:
    //
    // Failure Type: BUG. Reveals Bug #7. I believe this bug to be the utility still interpretting the "-w" option
    // as also taking a list of delimiter characters.
    @Test
    public void encodeTest36() throws Exception {
        File inputFile = createInputFile("abc5 5efg");

        String args[] = {"-w", " ", "-k", "accc 5555", "-x", "#", inputFile.getPath()};

        Main.main(args);

        String expected = "abc5 5efg";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Expose the following bug:
    //
    // Failure Type: BUG. Reveals Bug #x.
    @Test
    public void encodeTest37() throws Exception {
        File inputFile = createInputFile("abc5 5efg");

        String args[] = {"-d", "5", "-k", "accc 5555", "-x", "#", inputFile.getPath()};

        Main.main(args);

        String expected = "ac# #";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }
}
