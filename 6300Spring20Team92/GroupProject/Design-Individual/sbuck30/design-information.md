# Design Information
1. When the application is started, the player may choose to (1) Play a word game, (2) View statistics, or (3) Adjust the game settings. 
* **The Menu component realizes this requirement. The currentState attribute helps the GUI determine what to display and which functions to initialize the display with. It contains 3 functions which will intialize and display the 3 corresponding components: openSettings, startGame, and viewStatistics.**

2. When choosing to adjust the game settings, the player (1) may choose for the game to end after a certain number of minutes, from 1 to 5, defaulting to 3, (2) may adjust the size of the square board, between 4(x4) and 8(x8), defaulting to 4, and (3) may adjust the weights of the letters of the alphabet between 1 and 5, defaulting to 1.
* **When triggering viewStatistics, a CurrentSettings object is initialized with the default values. This is done by generated a GameSettings object. The current settings then have access methods to generate new game settings as the size, time, or weightings are changed. This container class also includes the valid options so that the GameSettings object is lightweight and does not need to perform the validation.**

3. When choosing to play a word game, a player will:
    - Be shown a ‘board’ of randomly generated letters.
    - Be shown a timer counting down the number of minutes available for the game, as set in the settings.
    - Start with 0 points, which is not required to be displayed during the game.
    - Until the game timer counts to zero and the game ends:
        - Enter a unique word made up of two or more letters on the board.  The word must contain only letters from the board that are each adjacent to the next (horizontally, vertically, or diagonally) and a single letter on the board may not be used twice.  The word does not need to be checked against a dictionary (for simplicity, we will assume the player enters only real words that are spelled correctly).
        or
        - Choose to re-roll the board at a cost of 5 points.  The board will be re-created in the same way it is generated at the start of each game, replacing each letter.  The timer will not be reset or paused.  The player’s score may go into negative values.
        or
        - Choose to exit the game early.
    - At the end of the game, when the timer has run out or the player chooses to exit, the final score for the game will be displayed.
    - Each word will score 1 point per letter (‘Qu’ will count as 2 letters), and the cost for each board reset will be subtracted.
    - After the player views the score, they will continue back to the main menu.
* **The GameInstance, Board, BagOfTiles, and Tile classes are used to realizes this requirement. When a game is started, a new GameInstance is created. This pulls the size, weights, and time settings from the CurrentSettings class. This class also includes a counter for currentPoints, the timeRemaining, a currentBoard, an array of playedWords, and the current state. When initialized, a Board is created ith the settings and weights procided from the parent GameInstance. #4 expands upon how BagOfTiles is used to generate the board and the letters' locations. The GameInstance includes a startGame function that starts the timer and listens for user input. enterWord is triggered when a user types in a word. This function starts by calling the checkTimer function to make sure that the game is still valid. It then iterates over the characters in the word and calls the Board's getNeighbors function to ensure that each subsequent letter is a neighbor of the last. If the word is deemed valid, the GameInstance's score is increased based on the number of letters. If the displayString of any tile is Qu, 2 points are awarded. As words are played, the playedWords array is added to. When endGame is triggered by checkTimer, the statistics for the game are generated. These include the time, size, score, and played words. This is fed to the Statistics module for tracking purposes. The endGame function will also change the state so that the results are displayed. closeResults is then triggered when the user exits the results page. This updates the parent menu's current state so that they are brought back to the main menu. resetBoard calls the Board's reset function which replaces the tiles as explained below.**

4. Whenever the board is generated, or re-generated it will meet the following criteria:
   - The board will consist of a square full of letters.  The square should have the number of letters, both horizontally and vertically, indicated by the size of the square board from the game settings (4x4, 5x5, 6x6, 7x7, or 8x8).  
   - ⅕ (rounded up) of the letters will be vowels (a,e,i,o,u). ⅘ will be consonants.
   - The letter Q will be displayed as ‘Qu’ (so that Q never appears alone).  
   - The location and particular letters should be randomly selected from a distribution of letters reflecting the weights of letters from the settings.  A letter with a weight of 5 should be 5 times as likely to be chosen as a letter with a weight of 1 (assuming both are consonants or both are vowels).  In this way, more common letters can be set to appear more often.
   - A letter may appear on the board more than once.
* **This is realized by the Board, BagOfTiles, and Tile classes. The size attribute will be set upon initialization of any board. This is then passed down to the BagOfTiles object when initialized. characterWeights stored in BagOfTiles store the weight for each letter. getTiles then utilizes this to generated numTiles amount of Tiles objects. It will do this by iterating over the number of tiles, and performing a random weighted sample for each tile against character weights. This will handle both tile position and weightings.**

5. When choosing to view statistics, the player may view (1) game score statistics, or (2) word statistics.
* **The parent Statistic class realizes this functionality. It contains a single mode attribute as well as a changeStatisticsMode function. The GUI will utilize these to render the correct page.**

6. For game score statistics, the player will view the list of scores, in descending order by final game score, displaying:
    - The final game score
    - The number of times the board was reset
    - The number of words entered in the game
    - The player may select any of the game scores from this list to view the settings for that game’s board size, number of minutes, and the highest scoring word played in the game (if multiple words score an equal number of points, the first played will be displayed).
* **The GameStatistic mode realizes this requirement. It is comprised of game statistic entries that are generated by the Statistics class when games end. It inherits from Statistic which implements pagination and display functionality. The stored StatisticEntries will store scores or each game, the number of times it has been reset, and the number of words that were played. The GameStatistic objects also contain a boardSize and numberOfMinutes for each game played so that this information may be displayed.**

7. For the word statistics, the player will view the list of words used, starting from the most frequently played, displaying:
    - The word
    - The number of times the word has been played, across all games
* **The WordStatistic mode realizes this requirement. It is comprised of word statistic entries that are generated by the Statistics class when games end. It inherits from Statistic which implements pagination and display functionality. The stored StatisticEntries will store the word counts.**

8. The user interface must be intuitive and responsive.
* **This is not shown in my UML diagram because it will be hanled by the GUI implementiaon.**

9.  The performance of the game should be such that students do not experience any considerable lag between their actions and the response of the application.
* **This is not explicit in my UML diagram because it is largley dependent on the infrastructure of the system. However, states were added to the GameInstance such that loading screens could be added during transitions.**

10. For simplicity, you may assume there is a single system running the application.
* **Since this is running on a single system, any type of network or API communication was not added to the UML diagram.**
