package edu.gatech.seclass.encode;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
    private static final String LARGEFILE1 = "abc defgh ijklmno pqrstu vwxyz 123456 789 0AB CDEF GHIJKL MNOPQRS TUVWXYZ ~!@#$%^&*(){}[];':12,./<>?";
    private static final String REMOVECHARS1 = "aBcDe";
    private static final String REMOVECHARS2 = "DeFGhi";
    private static final String KEEPCHARS1 = "fGhIj";
    private static final String DELIMCHARS1 = "knqtv";
    private static final String DELIMCHARS2 = "$&^)(";


    // test cases

    /*
     *   TEST CASES
     */

    // Purpose: In a larger file with all types of characters, with whitespace as a delimiter, test the removal of 5
    //   different alphanumeric characters, with reverse capitalization
    // Frame #: Test Case 43 		(Key = 3.1.1.0.1.2.1.1.)
    @Test
    public void encodeTest1() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-r", REMOVECHARS1, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = " HGF ONMLKJI UTSRQP ZYXWV 654321 987 0 f lkjihg srqponm zyxwvut ?></.,21:':][}{)(*&^%$#@!~";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a larger file with all types of characters, with whitespace as a delimiter, test the removal of 5
    //   different alphanumeric characters, *no* reverse capitalization
    // Frame #: Test Case 44 		(Key = 3.1.1.0.1.2.1.2.)
    @Test
    public void encodeTest2() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-r", REMOVECHARS1, inputFile.getPath()};
        Main.main(args);

        String expected = " hgf onmlkji utsrqp zyxvw 654321 987 0 F LKJIHG RSQPONM ZYXWVUT ?></.,21:':][}{)(*&^%$#@!~";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a larger file with all types of characters, with whitespace as a delimiter, test keeping 5
    //   different alphanumeric characters and removing the rest, with reverse capitalization
    // Frame #: Test Case 45 		(Key = 3.1.1.0.2.2.1.1.)
    @Test
    public void encodeTest3() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-k", KEEPCHARS1, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = " FGH IJ   123456 789 0 f ghij   ~!@#$%^&*(){}[];':12,./<>?";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a larger file with all types of characters, with whitespace as a delimiter, test keeping 5
    //   different alphanumeric characters and removing the rest, *no* reverse capitalization
    // Frame #: Test Case 46 		(Key = 3.1.1.0.2.2.1.2.)
    @Test
    public void encodeTest4() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-k", KEEPCHARS1, inputFile.getPath()};
        Main.main(args);

        String expected = " fgh ij   123456 789 0 F GHIJ   ~!@#$%^&*(){}[];':12,./<>?";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a larger file with all types of characters, use 5 alphanumeric delimiters, test the removal of 5
    //   different alphanumeric characters, with reverse capitalization
    // Frame #: Test Case 47 		(Key = 3.1.2.1.1.2.1.1.)
    @Test
    public void encodeTest5() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-w", DELIMCHARS1, "-r", REMOVECHARS1, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "JI HGF KMLNP OQSRT UVjihg f 0 987 654321 ZYXWkm lnpoq srtuv?></.,21:';][}{)(*&^%$#@!~ zyxw";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a larger file with all types of characters, use 5 alphanumeric delimiters, test the removal of 5
    //   different alphanumeric characters, *no* reverse capitalization
    // Frame #: Test Case 48 		(Key = 3.1.2.1.1.2.1.2.)
    @Test
    public void encodeTest6() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-w", DELIMCHARS1, "-r", REMOVECHARS1, inputFile.getPath()};
        Main.main(args);

        String expected = "ji hgf kmlnp oqsrt uvJIHG F 0 987 654321 zyxwKM LNPOQ SRTUV?></.,21:';][}{)(*&^%$#@!~ ZYXW";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a smaller file with all types of characters, test the removal of 5
    //   different alphanumeric characters, with reverse capitalization
    // Frame #: Test Case 11 		(Key = 2.1.1.0.1.2.1.1.)
    @Test
    public void encodeTest7() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-r", REMOVECHARS2, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "$CB@ ^&89";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a smaller file with all types of characters, test the removal of 5
    //   different alphanumeric characters, *no* reverse capitalization
    // Frame #: Test Case 12 		(Key = 2.1.1.0.1.2.1.2.)
    @Test
    public void encodeTest8() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-r", REMOVECHARS2, inputFile.getPath()};
        Main.main(args);

        String expected = "$cb@ ^&89";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a smaller file with all types of characters, test using a set of special character delimiters,
    //   test the removal of 5 different alphanumeric characters, with reverse capitalization
    // Frame #: Test Case 19 		(Key = 2.1.2.2.1.2.1.1.)
    @Test
    public void encodeTest9() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-w", DELIMCHARS2, "-r", REMOVECHARS2, "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "CB@$89 &^";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: In a smaller file with all types of characters, test using a set of special character delimiters,
    //   test the removal of 5 different alphanumeric characters, *no* reverse capitalization
    // Frame #: Test Case 20 		(Key = 2.1.2.2.1.2.1.2.)
    @Test
    public void encodeTest10() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-w", DELIMCHARS2, "-r", REMOVECHARS2, inputFile.getPath()};
        Main.main(args);

        String expected = "cb@$89 &^";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Test that files containing all special characters can be handled.
    // Frame #: Test Case 3  		<single>  (follows [if])
    @Test
    public void encodeTest11() throws Exception {
        File inputFile = createInputFile(SMALLFILE2);

        String args[] = {inputFile.getPath()};
        Main.main(args);

        String expected = "!~ $# (*&^";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Test and verify that attempting to use special characters as keep characters are ignored.
    // NOTE: This may generate some kind of error response instead by the application.
    // Frame #: Test Case 9  		<error>  (follows [if])
    @Test
    public void encodeTest12() throws Exception {
        File inputFile = createInputFile(SMALLFILE1);

        String args[] = {"-k", "@#$%^", inputFile.getPath()};
        Main.main(args);

        String expected = "$dcb@ ^&89";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Test whitespace characters can be explicitely defined for use as delimiters.
    // Frame #: Test Case 56 		(Key = 3.1.2.3.1.2.1.2.)
    @Test
    public void encodeTest13() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {"-w", "     ", "-r", REMOVECHARS1, inputFile.getPath()};
        Main.main(args);

        String expected = " hgfed onmlkji utsrqp zyxwv 654321 987 0 F LKJIHG SRQPONM ZYXWVUT ?></.,21:';][}{)(*&^%$#@!~";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Test that no removals/keeps occur when (-r|-k) parameters are not specified.
    // Frame #: Test Case 6  		<single>  (follows [if])
    @Test
    public void encodeTest14() throws Exception {
        File inputFile = createInputFile(LARGEFILE1);

        String args[] = {inputFile.getPath()};
        Main.main(args);

        String expected = "cba hgfed onmlkji utsrqp zyxwv 654321 987 BA0 FEDC LKJIHG SRQPONM ZYXWVUT ?></.,21:';][}{)(*&^%$#@!~";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }

    // Purpose: Test that an empty file works and results in an empty response.
    // Frame #: Test Case 1  		<single>
    @Test
    public void encodeTest15() throws Exception {
        File inputFile = createInputFile("");

        String args[] = {"-w", DELIMCHARS1, "-r", REMOVECHARS1, inputFile.getPath()};
        Main.main(args);

        String expected = "";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("Unexpected file content!", expected, actual);
    }
}
