package edu.gatech.seclass.encode;

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

import static org.junit.Assert.*;

/**
 * This is a Georgia Tech provided code example for use in assigned private GT repositories. Students and other users of this template
 * code are advised not to share it with other students or to make it available on publicly viewable websites including
 * repositories such as github and gitlab.  Such sharing may be investigated as a GT honor code violation. Created for CS6300.
 *
 * DO NOT ALTER THIS CLASS.  Use it as an example for MyMainTest.java
 */

public class MainTest {

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
	private static final String FILE1 = "abc tuvw.XYZ";
    private static final String FILE2 = "Howdy Billy, are you going to take cs6300 and cs6400 next semester?";
    private static final String FILE3 = "abcXYZ123ABCxyz";
	

    // test cases

    /*
    *   TEST CASES
    */

    // Purpose: To provide an example of a test case format
    // Frame #: Instructor example 1 from assignment directions
    @Test
    public void mainTest1() throws Exception {
        File inputFile = createInputFile(FILE1);

        String args[] = {inputFile.getPath()};
        Main.main(args);

        String expected = "cba wvut.ZYX";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
    }

    // Purpose: To provide an example of a test case format
    // Frame #: Instructor example 2 from assignment directions
    @Test
    public void mainTest2() throws Exception {
        File inputFile = createInputFile(FILE3);

        String args[] = {"-r", "aZ", inputFile.getPath()};
        Main.main(args);

        String expected = "bcXY123BCxy";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
    }

    // Purpose: To provide an example of a test case format
    // Frame #: Instructor example 3 from assignment directions
    @Test
    public void mainTest3() throws Exception {
        File inputFile = createInputFile(FILE2);

        String args[] = {"-w", ":,", "-k", "aeiouxyz", inputFile.getPath()};
        Main.main(args);

        String expected = "yi yo,?eee xe 0046 a 0036 ea o io uoy ea ";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
    }

    // Purpose: To provide an example of a test case format
    // Frame #: Instructor example 4 from assignment directions
    @Test
    public void mainTest4() throws Exception {
        File inputFile = createInputFile(FILE3);

        String args[] = {"-w", "-c", inputFile.getPath()};
        Main.main(args);

        String expected = "ZYXcba321zyxCBA";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
    }


    // Purpose: To provide an example of a test case format (no arguments passed)
    // Frame #: Instructor error example
    @Test
    public void mainTest5() {
        String args[]  = new String[0]; //no arguments
        Main.main(args);
        assertEquals("Usage: encode [-w [string]] [-r string | -k string] [-c] <filename>", errStream.toString().trim());
    }

}