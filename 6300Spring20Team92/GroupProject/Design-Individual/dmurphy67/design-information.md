# Assignment #5 : Software Design 


## Design Notes

*   All class attributes are accessed via getters and setters. However these operations are not shown on the class diagram for brevity. So class attributes are shown as private in class diagrams.
*   GUI objects that display information to the user will be attributes of only limited classes - WordGame, Menu, Settings and Statistics. For example, classes that WordGame uses (such as board and timer) will not directly manage GUI components but rather return their state to WordGame to display to the user.
*   The knowledge that ‘Q’ must always be followed by ‘u’ is required knowledge only for GameRunner. All other classes do not need to be aware of this rule. 


## Requirements


#### 1 - When the application is started, the player may choose to (1) Play a word game, (2) View statistics, or (3) Adjust the game settings.  

When the application starts (GameRunner.main()) it creates a Settings object which sets all default values for the game settings. GameRunner also creates a single ScoreStatistics and a single WordStatistics instance to keep track of scores.

GameRunner creates a Menu instance and uses the operation Menu.getUserChoice() to display the valid options the user may select and an exit option (to exit the app). The user makes their choice and the chosen option is returned to GameRunner. If exit is chosen GameRunner stops running.


#### 2 - When choosing to _adjust the game settings, _the player (1) may choose for the game to end after a certain _number of minutes,_ from 1 to 5, defaulting to 3, (2) may adjust the _size of the square board, _between 4(x4) and 8(x8)_, _defaulting to 4, and (3) may adjust the _weights _of the letters of the alphabet between 1 and 5, defaulting to 1.

When the user selects to adjust settings, GameRunner calls the operation Settings.editSettings(). Settings creates a GUI to allow editing of dimension, minutes and weight for the 26 letters. Also a ‘Cancel’ button to cancel any edits and a ‘Reset’ button to reset all settings to the defaults.


#### 3 - When choosing to _play a word game._

GameRunner creates a WordGame instance and runs it passing a reference to Settings, ScoreStatistics and WordStatistics. WordGame sets its attributes of score, word count, resets and highestWordScore to zero. 

WordGame creates a GameTimer with the specified number of game minutes. GameTimer will execute in its own parallel thread to ensure that it keeps elapsed time even when the game’s thread is blocked waiting on user input. Timer will callback to operation WordGame.updateTime() with the remaining minutes so WordGame can update its GUI with the time remaining. 

WordGame creates a GameBoard and calls operation GameBoard.generateBoard() with the character weights to randomize the board (details below). 


#### 3a - Be shown a ‘board’ of randomly generated letters.

WordGame retrieves the board from operation GameBoard.getBoard() operation (as a char[]) and displays the board characters. Whenever ‘Q’ is present in the value returned from getBoard() GameRunner will insert any missing ‘U’ after it for display. 


#### 3b - Be shown a timer counting down the _number of minutes_ available for the game, as set in the settings.

GameTimer will call back operation WordGame.updateTime() every minute to have WordGame display the minutes remaining. When the minutes remaining is zero WordGame calls WordGame.endGame() which - 

1) calls ScoreStatistics.addGame() with the score and highest word score of the current game 2) displays the game score (subtracting 5 for each of any resets) and waits for the user to respond 

3) exits back to GameRunner.

To record game scores - when the app starts GameRunner creates both a single ScoreStatistics and single WordStatistics instance. These objects exist for the entire time the app is running. Then when a game ends WordGame calls operation ScoreStatistics.addGame() with the result of the game (score, resets, words, hghestWord and a copy of the current Settings). In turn, ScoreStatistcs creates a new EndGameState to record the last game score (and Settings) and ScoreStatistics adds that to it’s list of gameScores.


#### 3c - Start with 0 points, which is not required to be displayed during the game.

WordGame initializes its score to zero.


#### 3d - Until the game timer counts to zero and the game ends_:_

When GameTimer calls WordGame.updateTime() with 0 minutes remaining, GameRunner calls GameRunner.endGame().


#### 3d1 - Enter a unique word made up of two or more letters on the board.  The word must contain only letters from the board that are each adjacent to the next (horizontally, vertically, or diagonally) and a single letter on the board may not be used twice.  The word does not need to be checked against a dictionary (for simplicity, we will assume the player enters only real words that are spelled correctly).

##### User GUI
WordGame presents a GUI dialog showing the board display having a) a word input field and an enter button b) a ‘Re-Roll’ button c) an ‘Exit’ button. WordGame also allows the user to build a word using mouse drags of adjacent letters. If the board contains the same letter in more than one location the user must use the mouse (not character input) to indicate which letter position is being used (since character input would be ambiguous as to which letter position is being used). This is the reason why the game will support a GUI capable of mouse drags to allow the user to disambiguate the same character in multiple positions. In this case, a mouse up indicates that the user has finished the word.

As the user builds a word (by mouse drags or character input) the word is passed to GameBoard for verification. The verification returns a String that repesents the valid characters the user has selected. Invalid characters (either not on the board or not adjacencies or character postion already used in the word) are omitted from the returned String. WordGame redisplays the resulting word to the user to indicate which input letters are valid.

If the user enters a ‘Q’ not followed by a ‘u’ the ‘u’ is first inserted and the word redisplayed to the user. If the word contains ‘Q’ then the following ‘u’ is stripped from the word before validation. (i.e. GameBoard does not need to have extra complexity to deal with the ‘Qu’ rule.)

#### Characters selected my mouse drags
If the user selects characters by mouse drags, character position is unambigious in cases where the same letter appears in multiple locations on the board. WordGame passes the user's word as an array of board postions to GameBoard for verification using GameBoard.verifyWordByPostion(). Verification returns a String of the word characters that are valid.

#### Characters typed
If the user specifies characters typing, character position is ambigious in cases where the same letter appears in multiple locations on the board. Therfore, in this case, the verification is more complex. WordGame passes the user's word as a String to GameBoard for verification using GameBoard.verifyWordByCharacter(). Verification returns a String of the word characters that are valid.

To handle disambiguity of mulitple letter positions, the verification algorithm validates that the word can be formed from at least one combination of letters and adjacencies that are on the board. The algorithm must examine every possibility for a character match and then examine every adjacency path from that match to verify remaining letters in the word. This approach is required to ensure that the verification algorithm does not give up prematurely without searching all board possibilities for a match. E.g. for ‘cat’, if the first ‘c’ on the board does not have an adjacent ‘a’ then try the next ‘c’ on the board. If the first ‘a’ adjacency does not have a ‘t’ adjacency then look for other ‘a’ adjacencies. 

#### Scoring
Once the user enters a word, WordGame a) checks the length is >=2 and b) checks its WordGame.usedWord list attribute to ensure the word has not been used before.

If the word is valid, WordGame calculates the new score based on the number of letters in the word. WordGame updates WordGame.usedWord list to add the new word. The word with the highest score in the game is updated in WordGame.highestWord attribute. GameRunner.words is increased by 1. GameRunner calls operation WordStatistics.addWord() to track the frequency of word usage which uses a map to track the words used and their frequencies. 

If the word is invalid WordGame notifies the user and allows the user to edit the word. No score updates are made. If exit is clicked WordGame.endGame() is called.

#### 3d2 - Choose to re-roll the board at a cost of 5 points.  The board will be re-created in the same way it is generated at the start of each game, replacing each letter.  The timer will not be reset or paused.  The player’s score may go into negative values.

WordGame calls GameBoard.generateBoard() to re-generate the board and subtracts 5 from the score. WordGame.resets is increased by 1.


#### 3d3 - Choose to exit the game early.

WordGame calls its operation endGame() to update statistics, show the score, ends the game and returns to GameRunner.


#### 3e - At the end of the game, when the timer has run out or the player chooses to exit, the final score for the game will be displayed.

WordGame calls its operation endGame() to update statistics, show the score, ends the game and returns to GameRunner.


#### 3f - Each word will score 1 point per letter (‘Qu’ will count as 2 letters), and the cost for each board reset will be subtracted.

If the word is valid, WordGame scores the word by counting the number of letters in the word and adds to the score.


#### 3g - After the player views the score, they will continue back to the main menu.

Operation WordGame.run() terminates and control returns to GameRunner which displays the game menu again.


#### 4 - Whenever the board is generated, or re-generated it will meet the following criteria:


#### 4a - The board will consist of a square full of letters.  The square should have the number of letters, both horizontally and vertically, indicated by the size of the square board from the game settings (4x4, 5x5, 6x6, 7x7, or 8x8).  

GameBoard is constructed with a reference to Settings and creates its dimensions from the Settings.dimension.


#### 4b - ⅕ (rounded up) of the letters will be vowels (a,e,i,o,u). ⅘ will be consonants.

Operation GameBoard.generateBoard() first calculates the number of required vowels by dividing (rounded up) its dimension squared by 5. E.g. if the board dimension is 5, the number of vowels is 5*5/5 = 5.


#### 4c - The letter Q will be displayed as ‘Qu’ (so that Q never appears alone). 

GameRunner maps all ‘Q’ to ‘Qu’ before displaying the character. 


#### 4d - The location and particular letters should be randomly selected from a distribution of letters reflecting the _weights _of letters from the settings.  A letter with a weight of 5 should be 5 times as likely to be chosen as a letter with a weight of 1 (assuming both are consonants or both are vowels).  In this way, more common letters can be set to appear more often.

Operation GameBoard.generateBoard() -

a) iterates through every letter of the 26 in the alphabet 

b) for each letter assigns it to the vowel or consonant category 

c) for each letter generates ‘n’ instances of the letter in either the ‘vowels’ or ‘consonants’ array depending on the letter’s category, where ‘n’ is the letter’s weight from Settings. This ensures that the letter’s weight is represented during random selection. E.g. if the vowels all have weight 1 except ‘e’ which has weight 3 then the vowels array looks like [a,e,e,e,i,o,u].

d) for the number of vowels required, a uniformly distributed random number [0 to length of vowels array] is generated and a vowel selected from vowels array. The vowel is placed randomly in an unused cell of the board.

e) for the remaining empty board cells and random number [0 to length of consonants array] is generated and a consonant selected from consonants. The letter is placed in the empty board cell.


#### 4e - A letter may appear on the board more than once.

The above algorithm handles this case.


#### 5 - When choosing to view statistics, the player may view (1) _game score statistics_, or (2) _word statistics_.

GameRunner calls operation ScoreStatistics.showScores() or WordStatistics.showScores() depending on the user’s input.


#### 6 - For game score statistics, the player will view the list of scores, in descending order by final game score, displaying: a) The final game score b) The number of times the board was reset c) The number of words entered in the game


#### The player may select any of the game scores from this list to view the settings for that game’s _board size, number of minutes, _and the _highest scoring word_ played in the game (if multiple words score an equal number of points, the first played will be displayed)_._

GameRunner calls ScoreStatistics.showScores() which shows the list of gameScores it has recorded. Each gameScore has the end of game final score, highest word score and a copy of the games Settings. The list is ordered for viewing by highest to lowest score, then earliest end-game time and shows the final game score, number of resets and number of words.

Next to each game is displayed a button that allows the user to view that game’s settings. When clicked, ScoreStatistics calls operation Settings.viewSettings() and passes that game’s settings to display the settings (dimensions, minutes, highest word score) for that specific game.


#### 7- For the word statistics, the player will view the list of words used, starting from the most frequently played, displaying: a) The word b) The number of times the word has been played, across all games

GameRunner calls operation WordStatistics.showFrequencies() to display the words used across all games and their frequencies. 


#### 8 - **The user interface must be intuitive and responsive.**

The above design provides a highly responsive UI. In particular, game timing does not stop user input and thread blocking during user input does not block timing since they are on different threads. I.e. the game may time out even when it is waiting for the user to enter a word. 

As already discussed, WordGame allows the user to enter words by typing or mouse drags on a GUI. WordGame continually updates the user as to which characters that they have entered are valid.

Available actions are always made clear to players - e.g. concise button labels indicate they can enter a word, reset the board or exit the game.

#### 9 The performance of the game should be such that students do not experience any considerable lag between their actions and the response of the application.

The amounts of data for this are small and all operations described above will complete quickly. Board updates will be fast and the algorithm for checking for valid words can be optimized using a node tree structure to ensure fast validation.

#### 10 For simplicity, you may assume there is a single system running the application.

The app would run on a single system without problems.
