package edu.gatech.seclass.encode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class Main {

/**
 * This is a Georgia Tech provided code example for use in assigned private GT repositories. Students and other users of this template
 * code are advised not to share it with other students or to make it available on publicly viewable websites including
 * repositories such as github and gitlab.  Such sharing may be investigated as a GT honor code violation. Created for CS6300.
 *
 * Main class for Individual Project
 * =======================================================================
 *
 * encode - encodes words in a file
 *
 * Description:
 *   This utility reads an input file and overwrites the file with a transformed
 * version of its contents. The first optional transformation is that all "words" in the
 * file may be reversed. "Words" are interpreted to be strings of characters
 * separated by delimiters. If no "-d" option is specified with delimiters,
 * then all whitespace is interpreted as delimiters. Alternatively "-d" can be
 * given a string of (any) characters to use as delimiters. The end of a file always
 * signifies the end of a word. If the "-w" option is specified, then words are
 * also reversed.
 *   The next optional transformation is to keep or remove certain alphanumeric
 * characters. The "-k" or "-r" options can be given with a string of alphanumeric
 * characters. If "-k" is used, then all alphanumeric characters not in the given string
 * are removed. If "-r" is used, then all alphanumeric characters in the given string
 * are removed. All non-alphanumeric characters (and whitespace) are left unchanged.
 *   Next, the "-c" option can be used to reverse the capitalization of all
 * alphabetic characters within the file. All non-alphabetic characters and whitespace
 * are left unchanged.
 *   Finally, the "-x" option can be used to replace contiguous strings of delimiter
 * characters with a single specified character. The default delimiter is whitespace,
 * unless "-d" is used to specify delimiters.
 *   If no options are given and only the filename is specified, then a special case
 * of the "-w" option is used where all non-alphanumeric characters are interpreted as
 * delimiters and words are reversed.
 *
 * Usage:
 *    encode OPT <filename>
 *
 * where:
 *    OPT can be zero or more of:
 *       -d <string>
 *       -w
 *       -x <char>
 *       (-r|-k) <string>
 *       -c
 *
 * Command line arguments behave as follows:
 *    <filename>:          The file on which the encode operation has to be performed.
 *    -d <delimiters>:     if specified, the utility will treat the required string
 *                         argument as the list of delimiter characters used by all other
 *                         options to separate words, where a word is a sequence of
 *                         characters terminated by one or more delimiter characters (or the
 *                         end of the file). If this option is not specified, all whitespace
 *                         characters are treated as the default delimiters. The delimiter
 *                         characters in the file will not be changed by -r, -k, or -c options.
 *    -w:                  if specified, the utility will reverse the characters in each word,
 *                         where a word is a sequence of characters terminated by any of the
 *                         current delimiter characters (as specified by -d or the default
 *                         of whitespace), or the end of the file.
 *    -x <char>:           if specified, the utility will replace the current delimiter
 *                         characters (as specified by -d or the default of whitespace)
 *                         between each word with the provided character. Contiguous,
 *                         uninterrupted sequences of delimiter characters are replaced by
 *                         only one of the provided character. This option is always executed
 *                         last.
 *    (-r|-k) <string>:    if specified, the alphanumeric characters within "string"
 *                         are either removed (-r) or kept (-k) in the output.
 *                         This has no effect on non-alphanumeric characters and
 *                         whitespace. Characters in the "string" are case-insensitive.
 *                         This operation is also not performed on any delimiters.
 *    -c:                  if specified, all alphabetic characters in the output
 *                         have their capitalization reversed. This operation is not
 *                         performed on any delimiters.
 *
 * If no options are specified, the utility will default to applying -w, using all
 * non-alphanumeric characters as delimiters (as if all non-alphanumeric characters
 * were passed to -d).
 *
 * Notes and Assumptions:
 *    - The last command-line argument is always interpreted as the filename.
 *    - OPT options can be provided in any order.
 *    - It is assumed that all options are provided at most once each.
 *    - "-r" and "-k" are mutually exclusive.
 *    - The original file is overwritten with the transformed output from a temporary file.
 *    - The string argument to "-d" will not be parsed if it is "-d", "-w", "-x", "-r",
 *      "-k", or "-c". These will instead be interpreted as those options.
 *    - The UTF-8 character set is assumed for all strings and characters.
 *    - At least one delimiter must be present within the file for any reversals to take place.
 *
 * @author James Damgar (jdamgar3@gatech.edu)
 * @version 1.1 - Deliverable #2
 * =======================================================================
 */

    // Global flag for printing DEBUG messages
    //    0 == No debug messages
    //    1 == Minimal debug messages
    //    2 == Verbose debug messages
    //    3 == Very verbose debug messages
    private static final int DEBUGLEVEL = 0;

    // Private class to encapsulate options for an individual execution of this tool
    private static class EncodeOptions {
        public boolean nonAlphaDelim            = false;              // Use all non-alphanumeric chars as delimiters
        public boolean whiteSpaceDelim          = false;              // All whitespace as delimiters
        public boolean reverseWords             = false;              // Reverse words?
        public ArrayList<Character> delimiters  = new ArrayList<>();  // Delimiters specified with "-d"
        public boolean keep                     = false;              // Only one of keep|remove true at a time
        public boolean remove                   = false;
        public ArrayList<Character> targetChars = new ArrayList<>();  // Keep or remove these characters
        public Character replacementChar        = null;               // Char to replace strings of delimiters with
        public boolean reverseCap               = false;              // Reverse alphabetic character capitalization?
        public String filename                  = "";                 // File to transform and overwrite
    }


    // The main entry-point to the utility
    public static void main(String[] args) {

        // Set up our stateful options
        EncodeOptions opts = new EncodeOptions();

        // Return code for debug tracking
        int rc = 0;

        // Parse command-line input and check for errors
        rc = parseArgs(args, opts);

        // If an argument parsing error, show usage
        if (rc == 1) {
            usage();
        }

        // Check to make sure remove and keep were not both specified
        if (rc == 0 && opts.remove && opts.keep) {
            printDebug(1, "Error: Remove (-r) and keep (-k) arguments cannot both be used.");
            usage();
            rc = 1;
        }

        // Proceed if all is well
        if (rc == 0) {
            // Summarize the current state of the program
            printDebug(1,"Non-alpha Delim?:\t"+opts.nonAlphaDelim);
            printDebug(1,"Whitespace Delim?:\t"+opts.whiteSpaceDelim);
            printDebug(1,"Reverse Words?:\t"+opts.reverseWords);
            printDebug(1,"Delimiters:\t\t\t"+opts.delimiters.toString());
            printDebug(1,"Remove?:\t\t\t\t"+opts.remove);
            printDebug(1,"Keep?:\t\t\t\t"+opts.keep);
            printDebug(1,"Target Chars:\t\t"+opts.targetChars.toString());
            if(opts.replacementChar == null) {
                printDebug(1, "Replacement Char:\tNone");
            }
            else {
                printDebug(1, "Replacement Char:\t" + opts.replacementChar);
            }
            printDebug(1,"Reverse Cap?:\t\t"+opts.reverseCap);
            printDebug(1,"Filename?:\t\t\t"+opts.filename);

            // Process the file. The following method will generate a new, temporary file with the
            // transformed output and then replace the original file with this version
            rc = processFile(opts);
        }
        printDebug(1,"Encode return code: "+rc);
    }

    // Helper method to print usage statement
    private static void usage() {
        System.err.println("Usage: encode [-d string] [-w] [-x char] [-r string | -k string] [-c] <filename>");
    }

    // Helper method to parse command-line arguments.
    // Input:  String argument list
    // Output: String containing a parsing error message, if an error exists
    private static int parseArgs(String[] args, EncodeOptions opts) {

        String cliError = "";
        boolean atLeastOneOption = false;  // At least one option specified?
        if (args.length > 0) {

            // Iterate over arguments
            int i = 0;
            while (i < args.length) {

                // Last argument should be the filename
                if (i == args.length-1) {

                    // Make sure this string is not one of the other arguments
                    if (args[i].charAt(0) == '-') {
                        cliError = "ERROR: Last argument must be a filename.";
                        break;
                    }
                    opts.filename = args[i];

                    // Check to make sure that the file exists
                    File inputFile = new File(opts.filename);
                    if (!Files.exists(inputFile.toPath())) {
                        System.err.println("File Not Found");
                        return 2;  // File issue rc
                    }

                    // Check to make sure that file is readable
                    if (!Files.isReadable(inputFile.toPath())) {
                        System.err.println("File Not Readable");
                        return 2;  // File issue rc
                    }
                }
                // Check for delimiter characters argument
                else if (args[i].equals("-d")) {

                    // At least one option
                    atLeastOneOption = true;

                    // Make sure that a string was specified and that it is not another option.
                    // Assume that the filename is the last arg.
                    if (i+2 < args.length && !args[i+1].equals("-d") && !args[i+1].equals("-w") &&
                            !args[i+1].equals("-x") && !args[i+1].equals("-r") && !args[i+1].equals("-k") &&
                            !args[i+1].equals("-c")) {

                        // Split up the string into characters and add them as delimiters.
                        // Delimiters can be alphanumeric or not.
                        for (char c : args[i+1].toCharArray()) {
                            opts.delimiters.add(c);
                        }
                        i++;
                    }
                    else {
                        // A string of characters must be given
                        cliError = "ERROR: -d takes a string of delimiter characters.";
                        break;
                    }
                }
                // Check for the string reversal flag
                else if (args[i].equals("-w")) {

                    // At least one option
                    atLeastOneOption = true;

                    // Doing word reversals
                    opts.reverseWords = true;
                }
                // Check for remove characters argument
                else if (args[i].equals("-r")) {

                    // At least one option
                    atLeastOneOption = true;

                    // Set remove flag
                    opts.remove = true;

                    // Make sure that a string was specified.
                    // Assume that the filename is the last arg.
                    if (i+2 < args.length) {

                        // Split up the string into characters and add them as "special" characters
                        for (char c : args[i+1].toCharArray()) {
                            // Only add uppercase/lowercase versions of alphanumeric characters.
                            // Ignore non-alphanumeric characters.
                            if (isAlphanumeric(c)) {
                                opts.targetChars.add(Character.toUpperCase(c));
                                opts.targetChars.add(Character.toLowerCase(c));
                            }
                        }
                        i++;
                    }
                    else {
                        // A string of characters must be given
                        cliError = "ERROR: -r takes an alphanumeric string of removal characters.";
                        break;
                    }
                }
                // Check for keep characters argument
                else if (args[i].equals("-k")) {

                    // At least one option
                    atLeastOneOption = true;

                    // Set keep flag
                    opts.keep = true;

                    // Make sure that a string was specified.
                    // Assume that the filename is the last arg.
                    if (i+2 < args.length) {

                        // Split up the string into characters and add them as "special" characters
                        for (char c : args[i+1].toCharArray()) {
                            // Only add uppercase/lowercase versions of alphanumeric characters.
                            // Ignore non-alphanumeric characters.
                            if (isAlphanumeric(c)) {
                                opts.targetChars.add(Character.toUpperCase(c));
                                opts.targetChars.add(Character.toLowerCase(c));
                            }
                        }
                        i++;
                    }
                    else {
                        // A string of characters must be given
                        cliError = "ERROR: -k takes an alphanumeric string of keep characters.";
                        break;
                    }
                }
                // Check for replacement character
                else if (args[i].equals("-x")) {

                    // At least one option
                    atLeastOneOption = true;

                    // Make sure that the next argument exists as a single character and that we
                    // still have the filename to go.
                    if (i+2 < args.length && args[i+1].length() == 1) {

                        opts.replacementChar = args[i+1].charAt(0);
                    }
                    else {
                        cliError = "ERROR: -x takes a single replacement character.";
                        break;
                    }
                    i++;
                }
                // Check for the reverse capitalization parameter
                else if (args[i].equals("-c")) {

                    // At least one option
                    atLeastOneOption = true;

                    // Indicate reversing capitalization
                    opts.reverseCap = true;
                }
                else {
                    cliError = "ERROR: Argument '"+args[i]+"' not understood.";
                }

                // Move to the next argument
                i++;
            }
        }
        else {
            // Must have at least the filename as an argument
            cliError = "ERROR: No filename given.";
        }

        // If no options were specified, default to applying all non-alphanumeric
        // characters as delimiters with word reversals
        if (!atLeastOneOption) {
            opts.reverseWords = true;   // "-w" for reversals
            opts.nonAlphaDelim = true;  // Use all non-alphanumeric delimiters
        }
        else if (opts.delimiters.isEmpty()) {
            // Otherwise, check to see if the "-d" option was omitted.
            // In this case, use all whitespace as delimiters
            opts.whiteSpaceDelim = true;
        }

        // Print any debug messages
        if (cliError.length() > 0) {
            printDebug(1, cliError);
            return 1;
        }
        return 0;
    }

    // Perform file processing given current settings. This method will generate a "temp" working file
    // and overwrite the original input file when finished.
    // Input:   Options
    // Output:  Return code. 0 == Success. Otherwise, failure.
    private static int processFile(EncodeOptions opts) {

        int rc = 0;

        // Open the input file for reading, character by character
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            // Open input and output streams to files
            fis = new FileInputStream(opts.filename);
            fos = new FileOutputStream(opts.filename + ".tmp");
        } catch (FileNotFoundException e) {
            // Exception encountered with input/output files. Cannot continue...
            e.printStackTrace();
            rc = 2;
        }
        if (rc == 0) {

            // Setup input/output reader/writer.
            // Assume UTF-8 character sets.
            InputStreamReader isr  = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br      = new BufferedReader(isr);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter bw      = new BufferedWriter(osw);

            // Buffer sets of characters from the source file into "words" between delimiters and
            // optionally reverse those words. Before writing out any character (from words, delimiters, etc.) first
            // check if that character is on any "keep" or "removal" list and react accordingly. Finally,
            // if that character is alphabetic and the "-c" option was specified, then reverse capitalization.
            StringBuilder word = new StringBuilder();
            int i;
            char c;
            boolean lastWasDelim = false;  // Currently within a string of delimiters
            boolean foundDelim = false;    // Found at least one delimiter
            while (true) {
                // Attempt to read the next character from the source file
                try {
                    i = br.read();
                } catch (IOException e) {
                    // Exception encountered. Fail out here
                    e.printStackTrace();
                    rc = 3;
                    break;
                }
                if (i < 0) {
                    // All done with the file.
                    // Process the last word if it exists. Here EOF is treated as a delimiter in the
                    // case of all whitespace being delimiters
                    processWord(word, bw, (foundDelim || opts.whiteSpaceDelim), opts);
                    break;
                }

                // Convert to character
                c = (char) i;

                // Check to see if this character is a delimiter character
                if (isDelimiter(c, opts)) {

                    // Found at least one!
                    foundDelim = true;

                    // If the most recent character was a delimiter also, then there is no word to process.
                    // Otherwise, there should be a word to process.
                    if (!lastWasDelim) {
                        // Process the latest buffered word and reset for the next word
                        processWord(word, bw, true, opts);
                        word = new StringBuilder();
                    }

                    // Set the following in case we hit a run of delimiters in a row
                    lastWasDelim = true;

                    // Write the character out without any rule checking
                    writeCharacter(c, bw, opts, false);
                }
                else {

                    // This character is not one of the designated delimiters.
                    // Buffer it into the latest "word".
                    word.append(c);
                    lastWasDelim = false;
                }
            }

            // Flush and close resources
            try {
                br.close();
            } catch (IOException e) {
                printDebug(1, e.toString());
            }
            try {
                bw.flush();
                bw.close();
            } catch (IOException e) {
                printDebug(1, e.toString());
            }
        }

        // If all was good, move on to the last step.
        // If the "-x" option was not used, simply copy the temporary file over and replace the original.
        // If the "-x" option was used, make another pass on the temporary file and filter all strings
        // of 1 or more delimiters into a single replacement character.
        if (rc == 0) {
            if (opts.replacementChar == null) {

                // Simply copy and replace the file
                File orig = new File(opts.filename);
                File tmp = new File(opts.filename + ".tmp");
                try {
                    Files.move(tmp.toPath(), orig.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                    rc = 4;
                }
            }
            else {

                // Process with the "-x" replacement character
                try {
                    // Open input and output streams to files
                    fis = new FileInputStream(opts.filename + ".tmp");
                    fos = new FileOutputStream(opts.filename);
                } catch (FileNotFoundException e) {
                    // Exception encountered with input/output files. Cannot continue...
                    e.printStackTrace();
                    rc = 2;
                }
                if (rc == 0) {
                    // Setup input/output reader/writer.
                    // Assume UTF-8 character sets.
                    InputStreamReader isr  = new InputStreamReader(fis, StandardCharsets.UTF_8);
                    BufferedReader br      = new BufferedReader(isr);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                    BufferedWriter bw      = new BufferedWriter(osw);

                    // Stream through file, replacing streams of delimiters
                    int i;
                    char c;
                    boolean lastWasDelim = false;  // Currently within a string of delimiters
                    while (true) {
                        // Attempt to read the next character from the source file
                        try {
                            i = br.read();
                        } catch (IOException e) {
                            // Exception encountered. Fail out here
                            e.printStackTrace();
                            rc = 3;
                            break;
                        }
                        if (i < 0) {
                            // All done with the file.
                            if (lastWasDelim) {
                                writeCharacter(opts.replacementChar, bw, opts, false);
                            }
                            break;
                        }

                        // Convert to character
                        c = (char) i;

                        // Check to see if this character is a delimiter character
                        if (isDelimiter(c, opts)) {

                            // Set the following in case we hit a run of delimiters in a row
                            lastWasDelim = true;
                        }
                        else {

                            // This character is not one of the designated delimiters.
                            // If we are ending a run of delimiters, output the replacement character first.
                            if (lastWasDelim) {
                                writeCharacter(opts.replacementChar, bw, opts, false);
                            }

                            // Now write out the non-delimiter
                            writeCharacter(c, bw, opts, false);
                            lastWasDelim = false;
                        }
                    }

                    // Flush and close resources
                    try {
                        br.close();
                    } catch (IOException e) {
                        printDebug(1, e.toString());
                    }
                    try {
                        bw.flush();
                        bw.close();
                    } catch (IOException e) {
                        printDebug(1, e.toString());
                    }
                }
            }
        }

        // Return result
        return rc;
    }

    // Helper method to determine if a String is alphanumeric
    private static boolean isAlphanumeric(String str) {

        // Start out assuming true
        boolean isAlpha = true;

        // An empty string is non-alphanumeric
        if (str.length() == 0) {
            isAlpha = false;
        }

        // Check each character in the string
        for (char c : str.toCharArray()) {
            if(!isAlphanumeric(c)) {
                // One failing characters fails the whole string
                isAlpha = false;
                break;
            }
        }

        // Return the result
        printDebug(1,"'"+str+"' is alphanumeric?: "+isAlpha);
        return isAlpha;
    }

    // Helper method to determine if the specified character is alphanumeric.
    private static boolean isAlphanumeric(char c) {
        // Simply check that it is a letter or a digit
        boolean result = (Character.isLetter(c) || Character.isDigit(c));
        printDebug(3,"'"+c+"' is alphanumeric?: "+result);
        return result;
    }

    // Helper method to determine if the specified character is one of the delimiters.
    private static boolean isDelimiter(char c, EncodeOptions opts) {

        // Default is no
        boolean isDelim = false;

        // Check our rules
        if (opts.nonAlphaDelim) {
            // All non-alphanumeric characters are delimiters.
            isDelim = !isAlphanumeric(c);
        }
        else if (opts.whiteSpaceDelim) {
            // All whitespace characters are delimiters.
            isDelim = Character.isWhitespace(c);
        }
        else {
            // See if this character matches any in the specified list (if "-d" was used)
            isDelim = opts.delimiters.contains(c);
        }

        // Return the result
        printDebug(3,"'"+c+"' is delim?: "+isDelim);
        return isDelim;
    }

    // Helper method to selectively output a character to the given output stream.
    // If the character is alphanumeric and checking of rules is enabled...
    //   - If "-k" was used, make sure it is in the "keep" list.
    //   - If "-r" was used, make sure it is *not* in the "remove" list
    //   - If "-c" was used, reverse capitalization if still outputting this character.
    private static void writeCharacter(char c, BufferedWriter bw, EncodeOptions opts, boolean checkRules) {

        // By default, output the non-transformed character
        boolean doWrite = true;
        char outputC = c;

        // Perform possible transformations if enabled and the character is alphanumeric
        if (checkRules && isAlphanumeric(c)) {
            // Check for "keep" and "remove"
            if (opts.keep) {
                // Make sure this character is on the "keep" list
                doWrite = opts.targetChars.contains(c);
            }
            else if (opts.remove) {
                // Make sure this character is *not* on the "remove" list
                doWrite = !opts.targetChars.contains(c);
            }

            // See if capitalization should be reversed on a still-output'able character
            if (doWrite && opts.reverseCap) {
                if (Character.isUpperCase(c)) {
                    outputC = Character.toLowerCase(c);
                }
                else {
                    outputC = Character.toUpperCase(c);
                }
            }
        }

        // Do the write, if appropriate
        if (doWrite) {
            try {
                bw.write(outputC);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to process an entire string word of text.
    // This processing will consist of the following steps:
    //    (1) Reverse the characters in the string if at least one delimiter
    //        was found and reversals were indicated.
    //    (2) Output each character, checking "keep" and "remove" rules.
    //        Capitalization may be reversed for alphanumeric characters.
    private static void processWord(StringBuilder word, BufferedWriter bw, boolean foundDelim, EncodeOptions opts) {

        printDebug(2, "Processing word: >"+word.toString()+"<");

        // Reverse the word if at least one delimiter was found and the option is set
        StringBuilder newWord = word;
        if (foundDelim && opts.reverseWords) {
            newWord = word.reverse();
        }
        // Output each character, checking appropriate rules
        for (char c : newWord.toString().toCharArray()) {
            writeCharacter(c, bw, opts, true);
        }
    }

    // Helper method to print debug message, if appropriate
    private static void printDebug(int level, String msg) {
        if (DEBUGLEVEL >= level) {
            System.out.println("DEBUG["+level+"] : "+msg);
        }
    }
}