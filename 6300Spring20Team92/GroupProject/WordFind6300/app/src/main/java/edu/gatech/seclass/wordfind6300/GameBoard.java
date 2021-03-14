package edu.gatech.seclass.wordfind6300;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameBoard {

    // Static, class-wide values for vowels and consonants
    public static final String[] VOWELS = {
            "a", "e", "i", "o", "u"
    };
    public static final String[] CONSONANTS = {
            "b", "c", "d", "f", "g", "h", "j", "k", "l",
            "m", "n", "p", "qu", "r", "s", "t", "v", "w",
            "x", "y", "z"
    };


    // Instance member variables
    private int dim;   // Should be 4-8, inclusive
    private Letter[][] boardLetters;
    // TODO: private edu.gatech.seclass.wordfind6300.Settings settings;


    // Construct a GameBoard
    public GameBoard(Settings settings) {

        // Set board dimensions, array of letters, and internal settings
        dim = settings.getDimensions();
        boardLetters = new Letter[dim][dim];  // Rows, then Columns
        // TODO: settings = s;
    }


    // Generate a new GameBoard and return the array of Letters that were generated.
    // The letters of the game board are randomly generated with the following constraints:
    //   - 1/5th (rounded up) will be vowels
    //   - 4/5ths will be consonants
    //   - The settings specified "weights" for each letter are taken into account. So that a
    //     letter with weight 5 is 5 times more likely to be picked than a letter with weight 1.
    public void generateBoard() {

        // Erase board
        boardLetters = new Letter[dim][dim];

        // Determine how many vowels and consonants we need
        int neededVowels = (int) Math.ceil((dim * dim) / 5.0);
        int neededCons = ( dim * dim ) - neededVowels;

        // Shuffle the various locations on the board
        List<Loc> locations = new ArrayList<Loc>();
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                locations.add(new Loc(row, col));
            }
        }
        Collections.shuffle(locations);

        // We will now fill in these shuffled locations on the board, 1-by-1
        int index = 0;

        // Start with vowels
        for (int i = 0; i < neededVowels; i++) {

            // Perform a weighted sampling based on weight to get a letter
            Letter letter = getLetter(VOWELS);

            // Fill in this letter at the current location
            Loc currentLoc = locations.get(index);
            letter.setLoc(currentLoc.row, currentLoc.col);

            // Place it on the board, taking into account row/column mapping
            boardLetters[currentLoc.row][currentLoc.col] = letter;

            // Increment location index
            index++;
        }

        // Then, do consonants
        for (int i = 0; i < neededCons; i++) {

            // Perform a weighted sampling based on weight to get a letter
            Letter letter = getLetter(CONSONANTS);

            // Fill in this letter at the current location
            Loc currentLoc = locations.get(index);
            letter.setLoc(currentLoc.row, currentLoc.col);

            // Place it on the board, taking into account row/column mapping
            boardLetters[currentLoc.row][currentLoc.col] = letter;

            // Increment location index
            index++;
        }
    }


    // Get the current 2-D representation of the GameBoard of Letters
    public Letter[][] getBoard() {
        return boardLetters;
    }


    // Verify the current word according to adjacency and duplication rules.
    // A word is valid if and only if:
    //   - It is unique for this game instance
    //   - It consists of 2 or more Letters
    //   - All letters in the word are adjacent
    //   - No letters within the word are used twice
    // Return true if this is valid word. false otherwise.
    public /*boolean*/ String verifyWord(ArrayList<Letter> word, ArrayList<String> usedWords) {

        // Ensure that this word is unique
        String thisWord = "";
        for (Letter letter : word) {
            thisWord += letter.getChars();
        }
        for (String usedWord : usedWords) {
            if (thisWord.equalsIgnoreCase(usedWord)) {
                //return false;
                return "word was already used";
            }
        }

        // At least 2 Letters ("Qu" counts as 1 letter in this respect)
        if (word.size() < 2) {
            //return false;
            return "word is too short";
        }

        // Check that next letter is adjacent. Two letters are adjacent if they only
        // differ by a max of 1 in the X and/or Y directions
        for (int i = 0; i < word.size() - 1; i++) {
            if (Math.abs(word.get(i).getRow() - word.get(i+1).getRow()) > 1 || Math.abs(word.get(i).getCol() - word.get(i+1).getCol()) > 1) {
                //return false;
                return "word does not have adjacent letters";
            }
        }

        // Check that no two letters are used twice
        for (int i = 0; i < word.size() - 1; i++) {
            for (int j = i + 1; j < word.size(); j++) {
                if (word.get(i).getRow() == word.get(j).getRow() && word.get(i).getCol() == word.get(j).getCol()) {
                    //return false;
                    return "word uses the same letter more than once";
                }
            }
        }

        // Must be valid if we got here!
        //return true;
        return "";
    }


    // Helper method to perform a random sampling of an array of letters.
    // The sampling takes into account this GameBoard's setting's weights
    private Letter getLetter(String[] choices) {

        Letter letter = null;

        double totalWeight = 0.0;
        for (String choice : choices) {
            // TODO: Use settings member to get weight for letter
            totalWeight += 1.0; // Equal weight, for now..
        }

        double randomValue = Math.random() * totalWeight;
        double counter = 0.0;

        for (String choice : choices) {
            // TODO: Use settings member to get weight for letter
            counter += 1.0;
            if (counter >= randomValue) {
                // Use this choice
                letter = new Letter(choice);
                break;
            }
        }

        // Return the selected letter
        return letter;
    }


    // Utility class for location, in rows and columns
    private class Loc {
        int row, col;
        public Loc(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}
