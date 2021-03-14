# Word Game - SoftWare Design
### jdamgar3@gatech.edu | Georgia Tech OMSCS CS6300 | Spring 2020

## Design Assumptions and Notes 
The following assumptions were made while producing this design:
- The design represents the underlying logic and data structures of the game and does not reflect the GUI classes or views needed to render the game.
- The GUIUser class represents the "entry point" to the game logic's model/control for the GUI's view. Controls presented as UI elements in the GUI will drive the GUIUser class to make calls to Game and GameSettings instances to alter the state of the game.
- Although not depicted, it is assumed that "basic" fields such as integers (int) strings (string) and date-times (DateTime) for each class will have "getter" methods to return those values. For example, when the GUI needs a GameLog's playerScore to render in its view.
- Although not depicted, most classes will feature constructors to initialize an object instance of the class with default field values.
- GameLog instances produced during the stop() method of a Game will be housed within a database or other permanent record that the GameStatistics class can draw from during its viewGameLogs() and viewGameLog() methods. Similarly, WordStat instances will also be persisted so that they can be fetched with the viewWordStats() method. The first time these are called will result in the fields of the GameStatistics class being populated.
- GameStatistics is a static class presenting static methods. There will be no object instances of this class.
- There will be only 1 GUIUser instance representing a user's interaction with the application.
- The Letter class will feature static globalLetters and globalLetterWeights fields that indicate the current alphabet and letter weights in use by the application. The letters are drawn from the English alphabet (26 letters) with 'Q' represented by 'Qu' and scoring 2 points as opposed to 1 for other letters.
- The Letter class will also return Letter object instances via the static getLetter() method. These instances will feature a weight derived from the current global weight for that Letter and X/Y coordinates on a GameBoard as member fields.
- Constraints in the design are noted with bracketed annotations in the design document.


## Requirements
1. When the application is started, the player may choose to (1) Play a word game, (2) View statistics, or (3) Adjust the game settings.\
**The GUIUser class represents the GUI layer's entry point to the game logic. The GUI provides the user interface and rendering of elements of the game and provides for the user's control. The GUI establishes a GUIUser object. Within the GUI, buttons are presented to 'View Game Statistics', 'Adjust Game Settings', or 'Play a Game'. If the 'View Game Statistics' button is pressed, the user is presented with an option to 'View Game Logs' or 'View Word Stats'. These drive calls to the global static (singleton) GameStatistics class to viewGameLogs() or viewWordStats(), respectively, which present listings of game logs and word stats. A GUI user may drill down into a particular game log which drives a call to viewGameLog(). If the user selects 'Adjust Game Settings', then a GameSettings object is created with the user's selection for game time limit (timeLimitMinutes), board size (boardSize), and individual letter weight (letters array). This GameSettings object (or a default instance if no adjustments were made) is then used when a user selects 'Play a Game'. First a Game object is created for the GUIUser and a call to setGameSettings() is made with the GameSettings object that was tuned. Then, the GUI drives a call to the start() method of the Game to begin a new game. A constraint is in place to limit the system to one GUIUser at a time.**

2. When choosing to adjust the game settings, the player (1) may choose for the game to end after a certain number of minutes, from 1 to 5, defaulting to 3, (2) may adjust the size of the square board, between 4(x4) and 8(x8), defaulting to 4, and (3) may adjust the weights of the letters of the alphabet between 1 and 5, defaulting to 1.\
**The GUIUser establishes a GameSettings instance with a timeLimitMinutes time limit, a board size (boardSize), and individual letter weights (letters array). Constraints are in place to limit the boardSize to 4-8 and the timeLimitMinutes to 1-5, inclusive. Both have defaults if not specified by the user. The getLetters() method of the GameSettings class provides a way for the GUI to query for the list of letters in the alphabet. The GUI renders a view allowing for the user to adjust weights individually. This is propagated with adjustLetterWeight() calls which modify the "current" global weights in the Letter class. A GameSettings object defaults to establishing a list of all 26 English letters (letters array).**

3. When choosing to play a word game, a player will:

    a. Be shown a ‘board’ of randomly generated letters.\
    **The GUI drives a call to the GUIUser's game's viewGameBoard() method to fetch a 2-dimensional string representation of the game board. The GUI renders the board using this 2-D array.**

    b. Be shown a timer counting down the number of minutes available for the game, as set in the settings.\
    **The GUI drives a call to the GUIUser's game's getStartTime() method and calculates based on its gameSettings' timeLimitMinutes how many minutes/seconds are left. This is then rendered as an active timer in the game view.**

    c. Start with 0 points, which is not required to be displayed during the game.
    **The Game object records a playerScore to capture the player's point total during a game.**

    d. Until the game timer counts to zero and the game ends:

        i. Enter a unique word made up of two or more letters on the board.  The word must contain only letters from the board that are each adjacent to the next (horizontally, vertically, or diagonally) and a single letter on the board may not be used twice.  The word does not need to be checked against a dictionary (for simplicity, we will assume the player enters only real words that are spelled correctly).
      **A user is able to view the current game board within the GUI. The user may attempt to add a word through the GUIUser's game's addWord() method that takes in an array of string letters, and x and y coordinates on the board. Internally, the Game object determines if this is a valid play and returns a boolean (true/false) indicator on success/failure. The Game uses calls to the checkLettersAdjacent() and checkLetterDuplicate() methods of its GameBoard instance to ensure the letters are adjacent on the board and that letters are not used more than once. When a new Game is first created with start(), a GameBoard instance is created and a call to its randomGenerateLetters() method is done to initialize the board.**

    or

        ii. Choose to re-roll the board at a cost of 5 points.  The board will be re-created in the same way it is generated at the start of each game, replacing each letter.  The timer will not be reset or paused.  The player’s score may go into negative values.
      **The GUI may drive a GUIUser's call to its game's reset() method. This will cause the player's score to be decremented by 5 and will drive a call to the Game's GameBoard instance's randomGenerateLetters() method to re-initialize the game board. The Game increments its boardResetCount counter as well.**

    or

        iii. Choose to exit the game early.
      **The GUI may drive a GUIUser's call to its game's stop() method. This method handles the logic of ending the game, generating a GameLog, and incrementing word statistics.**

    e. At the end of the game, when the timer has run out or the player chooses to exit, the final score for the game will be displayed.
    **Regardless of the cause, after the GUIUser's game's stop() method is invoked -- either manually by the GUI or by the Game's internal monitoring of the time left in the game -- the game will end. The GUI will drive a call to the game's getPlayerScore() method and render the final score for the game in its view.**

    f. Each word will score 1 point per letter (‘Qu’ will count as 2 letters), and the cost for each board reset will be subtracted.
    **After a GUIUser makes a successful call to its game's addWord() method (i.e. it returns true), this method will internally calculate the score for the word and increment the playerScore field as a result. If a GUIUser makes a call to the game's reset() method, then apart from resetting the gameBoard, the playerScore of the game will be decremented and the boardResetCount counter will be incremented.**

    g. After the player views the score, they will continue back to the main menu.\
    **The GUI will handle a call to the GUIUser's game's getPlayerScore() method to fetch and render the score for the player and handle the navigation duties of rendering its 'main menu' once again for the user to interact with. This is not shown explicitely in this UML diagram.**

4. Whenever the board is generated, or re-generated it will meet the following criteria:

    a. The board will consist of a square full of letters.  The square should have the number of letters, both horizontally and vertically, indicated by the size of the square board from the game settings (4x4, 5x5, 6x6, 7x7, or 8x8).\
    **When the GUI drives a call to the GUIUser's game's start() method, the Game instance initializes a new GameBoard (gameBoard field) with a boardSize matching the value from the game's GameSettings instance (gameSettings field) -- which was either an object with default values or one customized by the user through the GUI. A call is then made to the GameBoard instance's randomGenerateLetters() method which performs the task of making calls to the Letter class' static generateLetters() method to fetch a set of letters to populate the GameBoard's 2-D Letter array (letters field). This matches boardSize dimensions specified. Any call to a Game instance's reset() method will also drive a call to its gameBoard's randomGenerateLetters() method to re-fetch a set of letters and populate its array.**

    b. ⅕ (rounded up) of the letters will be vowels (a,e,i,o,u). ⅘ will be consonants.\
    **The Letter class' generateLetters() method takes in arguements for the number of Letters to generate and the percentage of those that are vowels. Provided the input is valid, the method will return an array of Letter objects satisfying the desired vowel to consonant ratio. The individual Letters are chosen "randomly". The GameBoard's randomGenerateLetters() method drives a call to generateLetters() to make this happen.**

    c. The letter Q will be displayed as ‘Qu’ (so that Q never appears alone).\
    **The letter Q will be represented by 'Qu' in the Letter class. It should be noted that the Letter class is constrained to deriving letters from the English alphabet. The GUI's views will take care of rendering Q as 'Qu' in the view of a GameBoard as well.**

    d. The location and particular letters should be randomly selected from a distribution of letters reflecting the weights of letters from the settings.  A letter with a weight of 5 should be 5 times as likely to be chosen as a letter with a weight of 1 (assuming both are consonants or both are vowels).  In this way, more common letters can be set to appear more often.\
    **Whenever a (singleton) GUI user uses the GUIUser class to initialize a GameSettings object, the GUI also drives calls to that GameSettings instance's adjustLetterWeight() method for each letter of the English alphabet. The GameSettings instance also captures, for posterity, the Letters chosen and their weights (letters and letterWeights fields). For any one running instance of the game system, the Letter class features global Letter and weight arrays (globalLetters and globalLetterWeights fields) which track the "current" weights of each letter in the alphabet. The GameSettings instance's calls to the Letter adjustLetterWeight() method cause these to change. Later, when a GameBoard instance makes a call to the Letter class' generateLetters() method, this method will take care of the logic of ensuring, based on the current Letter weights, that a proper ratio of Letters are returned.**

    e. A letter may appear on the board more than once.\
    **The Letter class' generateLetters() method may return an array with Letters appearing more than once for use by the GameBoard.**

5. When choosing to view statistics, the player may view (1) game score statistics, or (2) word statistics.\
**The GUI will present a button to the user to 'View Game Statistics'. If this button is pressed, the user is presented with an option to 'View Game Logs' or 'View Word Stats'. The first drives a GUIUser instance to call the global (singleton) GameStatistics' viewGameLogs() method. This returns a list of GameLogs in string form. The GUI will render this list and allow the user to select one. Doing so will drive a call to the GameStatistics' viewGameLog() method with this GameLog's ID. This will cause the whole GameLog to be returned to the GUIUser class and allow the GUI to render elements of the log. If the user instead selects 'View Word Stats', the GUIUser instance will instead call the viewWordStats() method of the GameStatistics static class. This will return a listing of words and their number of the times played over the lifetime of the system, sorted by frequency, descending.**

6. For game score statistics, the player will view the list of scores, in descending order by final game score, displaying:\
**Here the GUI will be interacting with the individual GameLog returned above.**

    a. The final game score\
  **The GameLog's playerScore is rendered by the GUI.**

    b. The number of times the board was reset\
  **The GameLog's boardResetCount is rendered by the GUI.**

    c. The number of words entered in the game\
  **The GameLog's wordCount is rendered by the GUI.**

    The player may select any of the game scores from this list to view the settings for that game’s board size, number of minutes, and the highest scoring word played in the game (if multiple words score an equal number of points, the first played will be displayed).\
  **The GameLog features a GameSettings field (gameSettings) recorded from the end of a Game (after its stop() method is invoked). After the GUI drives a fetch for a desired GameLog, that log's gameSettings instance may then be inspected by the GUI to fetch its boardSize and timeLimitMinutes for rendering. Then a Game finishes via its stop() method, it will calculate the highest scoring word and record that and its value within the GameLog as its highScoreWord (Word) and highScoreWordScore (int) fields. These may then be inspected by the GUI for rendering.**

7. For the word statistics, the player will view the list of words used, starting from the most frequently played, displaying:\
**If the GUI user selects the 'View Word Stats' option in the GUI, then a call to the GameStatistics' viewWordStats() method is invoked. This returns a string listing of words and their frequency across all games, sorted by frequency, descending. The GUI may then render this list in the view for the game.**

    i. The word

    ii. The number of times the word has been played, across all games

8. The user interface must be intuitive and responsive.\
**This requirement is outside of the scope of the UML design presented but should be considered when implementing the GUI views of the game. Efforts have been made above to decompose the logic of the game into intuitive components with high cohesion and low(er) coupling.**

9. The performance of the game should be such that students do not experience any considerable lag between their actions and the response of the application.
**This requirement is outside of the scope of the UML design presented but should be considered when implementing the individual method algorithms such that they are fast to execute and when choosing the GUI technology used and how it updates its views and responds to user input.**

10. For simplicity, you may assume there is a single system running the application.\
**The assumption with this design is that each instance of the application is self-contained and running on a single system.**