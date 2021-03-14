package edu.gatech.seclass.sdpencryptor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * The following class represents my implementation of the SDPEncryptorActivity Android activity
 * class for Assignment 4 of the OMSCS CS6300 Software Development Process course.
 *
 * @author James Damgar (jdamgar3@gatech.edu)
 */
public class SDPEncryptorActivity extends AppCompatActivity {

    // Valid alphabetical letters
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";

    // Valid numbers
    private static final String NUMBERS = "0123456789";

    // Valid values for target alphabet spinner selector
    private static final String DEFAULT_TARGET = "Normal";
    private static final String[] TARGETS = new String[] {
      "Normal", "Reverse", "QWERTY"
    };

    // The "Normal", "Reverse", and "QWERTY" alphabets
    private static final String ALPHA_NORMAL  = LETTERS;
    private static final String ALPHA_REVERSE = "zyxwvutsrqponmlkjihgfedcba";
    private static final String ALPHA_QWERTY  = "qwertyuiopasdfghjklzxcvbnm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the appropriate layout for this app
        setContentView(R.layout.activity_sdpencryptor);

        // Setup the target spinner with specific values and set the default value
        Spinner s = findViewById(R.id.targetAlphabet);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TARGETS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        int defaultPos = adapter.getPosition(DEFAULT_TARGET);
        s.setAdapter(adapter);
        s.setSelection(defaultPos);
    }


    /**
     * Handle the clicking of the "Encrypt" button within the application.
     * @param view The element of the layout linked to this function
     */
    public void handleButtonClick(View view) {

        // Fetch a reference to the messageInput field and extract the text
        EditText inputET = findViewById(R.id.messageInput);
        String input = inputET.getText().toString().trim();

        // Fetch a reference to the shift
        EditText shiftET = findViewById(R.id.shiftNumber);
        String shift = shiftET.getText().toString().trim();

        // Fetch a reference to the target alphabet
        Spinner s = findViewById(R.id.targetAlphabet);
        String target = s.getSelectedItem().toString();

        // Fetch a reference ot the output text field
        TextView cipherTV = findViewById(R.id.cipherText);

        // Check that the message input is valid
        int inputState = validateInputText(input);

        // Check that shift count is valid
        int shiftCount = validShiftCount(shift);

        // Error indicator
        boolean error = true;

        // Proceed if input message is valid
        if(inputState == 0) {

            // Proceed if shift count is valid
            if(shiftCount > 0) {

                // The input message is good and the shift count is good.
                // Proceed to shift the message according to the target
                // alphabet.
                String cipher = shiftMessage(input, shiftCount, target);

                // Set the the output text field
                cipherTV.setText(cipher);

                // No errors
                error = false;
            }
        }

        // Set appropriate error messages, if needed
        if(error) {
            cipherTV.setText("");
        }
        if(inputState == 1) {
            inputET.setError(getText(R.string.missingmessage));
        }
        else if (inputState == 2) {
            inputET.setError(getText(R.string.errormessage));
        }
        if(shiftCount <= 0) {
            shiftET.setError(getText(R.string.errorshift));
        }
    }


    /**
     * Helper method to produce a ciphertext from input message text.
     * @param input The input message to encrypt
     * @param shift The number of characters to shift
     * @param target The target alphabet
     * @return The encrypted cipher text
     */
    private String shiftMessage(String input, int shift, String target) {

        String cipher = input;

        // Different behavior based on target
        String alpha_target = null;
        switch(target) {
            case "Reverse":
                alpha_target = ALPHA_REVERSE;
                break;
            case "QWERTY":
                alpha_target = ALPHA_QWERTY;
                break;
            case "Normal":
            default:
                alpha_target = ALPHA_NORMAL;
                break;
        }

        // Perform the shift
        cipher = doShiftMessage(input, shift, alpha_target);

        // Return encrypted text
        return cipher;
    }

    /**
     * Helper method to actually do the mapping of the input message
     * to a target alphabet and do the shift.
     * @param input The input message to encrypt
     * @param shift The number of characters to shift
     * @param alpha_target The target alphabet
     * @return The encrypted cipher text
     */
    private String doShiftMessage(String input, int shift, String alpha_target) {

        // Build the cipher string character by character
        StringBuilder cipher = new StringBuilder();
        char c;
        int index;
        String sub;
        for(int i = 0; i < input.length(); i++) {

            // Only transform if this is a normal "letter"
            c = input.charAt(i);
            if(isLetter(c)) {

                // Determine target index
                index = (ALPHA_NORMAL.indexOf(Character.toLowerCase(c)) + shift) %
                        ALPHA_NORMAL.length();

                // Preserve case during transformation
                sub = alpha_target.substring(index, index+1);
                if(Character.isUpperCase(c)) {
                    sub = sub.toUpperCase();
                }
                c = sub.charAt(0);
            }

            // Append the latest translated (or not) character
            cipher.append(c);
        }

        // Return encrypted text
        return cipher.toString();
    }


    /**
     * Helper method to validate that the input text is valid.
     * This means that it is non-empty and contains at least
     * one letter.
     * @param input The input text
     * @return 0 if valid string, 1 if empty, 2 if no letters.
     */
    private int validateInputText(String input) {
        // Make sure string is not null or empty
        if(input == null || input.length() == 0) {
            return 1;
        }

        // Make sure that there is at least one "letter"
        for(int i = 0; i < input.length(); i++) {
            // Check if this character is a LETTER
            if(isLetter(input.charAt(i))) {
                return 0;
            }
        }

        // No letters
        return 2;
    }

    /**
     * Helper method to determine if a character is a "letter"
     * @param input The character
     * @return Whether this is a valid letter (case-insensitive)
     */
    private boolean isLetter(char input) {
        String c = String.valueOf(input);
        for(int i = 0; i < LETTERS.length(); i++) {
            if(c.equalsIgnoreCase(LETTERS.substring(i, i+1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to determine if a shift number is valid
     * @param input Integer shift count as a string
     * @return Valid integer >=1 and <26 if valid. 0 if invalid.
     */
    private int validShiftCount(String input) {
        // Make sure string is not null or empty
        if(input != null && input.length() > 0) {
            // Check each character in the string
            for(int i = 0; i < input.length(); i++) {
                // Check if this character is a NUMBER
                String c = String.valueOf(input.charAt(i));
                boolean letterGood = false;
                for(int j = 0; j < NUMBERS.length(); j++) {
                    if(c.equalsIgnoreCase(NUMBERS.substring(j, j+1))) {
                        letterGood = true;
                        break;
                    }
                }
                if(!letterGood) {
                    // Bad character found!
                    return 0;
                }
            }
            // All characters good!
            int number = Integer.parseInt(input);
            if(number >=1 && number < 26) {
                return number;
            }
        }
        return 0;
    }
}
