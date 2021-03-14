## Assignment 5 Design Information

##### 1. When the application is started, the player may choose to (1) Play a word game, (2) View statistics, or (3) Adjust the game settings.

These 3 options are represented by the Game, Statistics, and GameSettings classes, respectively. The player and choosing of the options is not represented by the design because that is only necessary for the UI/UX.

##### 2. When choosing to _adjust the game settings,_ the player (1) may choose for the game to end after a certain _number of minutes,_ from 1 to 5, defaulting to 3, (2) may adjust the _size of the square board,_ between 4(x4) and 8(x8)_,_ defaulting to 4, and (3) may adjust the _weights_ of the letters of the alphabet between 1 and 5, defaulting to 1.

To realize this, I created a Game class that _has_ a GameSettings class. The GameSettings class holds the attributes minutes, board\_size, and weight with the correct defaults as above. It also has the set\_game\_settings() function to allow the Game class to set the settings when the user changes them on the UI. It will take in all 3 attributes as arguments.

##### 3. When choosing to _play a word game,_ a player will:
  ##### a. Be shown a &#39;board&#39; of randomly generated letters.
  #####  b. Be shown a timer counting down the _number of minutes_ available for the game, as set in the settings.
  ##### c. Start with 0 points, which is not required to be displayed during the game.
  ##### d. Until the game timer counts to zero and the game ends_:_
    1. Enter a unique word made up of two or more letters on the board.  The word must contain only letters from the board that are each adjacent to the next (horizontally, vertically, or diagonally) and a single letter on the board may not be used twice.  The word does not need to be checked against a dictionary (for simplicity, we will assume the player enters only real words that are spelled correctly).
    2. Choose to re-roll the board at a cost of 5 points.  The board will be re-created in the same way it is generated at the start of each game, replacing each letter.  The timer will not be reset or paused.  The player&#39;s score may go into negative values.
    3. Choose to exit the game early.
  ##### e. At the end of the game, when the timer has run out or the player chooses to exit, the final score for the game will be displayed.
  ##### f. Each word will score 1 point per letter (&#39;Qu&#39; will count as 2 letters), and the cost for each board reset will be subtracted.
  ##### g. After the player views the score, they will continue back to the main menu.

To realize this, I created a Board class that has an attribute letters which is a 2d array of chars with size taken from the GameSettings. It uses the method generate\_letters(size, weight) method which takes in size and weight to set the letters on the board. It also has a set\_letter method for when a user adds a letter to the board that will take in a row and column and letter as arguments.

The minutes\_remaining attribute on Game uses the Timer utility to keep track of how much time is left for this board. The points attribute is also added to the Game class. I also added a status attribute to Game to keep track of whether or not the game is complete, which is derived from the minutes\_remaining attribute (whether or not its 0) We can use the add\_points method which takes points as an attribute and an on\_complete method for when the game ends and we want to set the minutes\_remaining to 0. Lastly, I added on\_reroll() for when the board is re-created and this method can call add\_points() with the argument of -5 points.

##### 4. Whenever the board is generated, or re-generated it will meet the following criteria:
  ##### a.  The board will consist of a square full of letters.  The square should have the number of letters, both horizontally and vertically, indicated by the _size of the square board_ from the game settings (4x4, 5x5, 6x6, 7x7, or 8x8).
  ##### b. ⅕ (rounded up) of the letters will be vowels (a,e,i,o,u). ⅘ will be consonants.
  ##### c. The letter Q will be displayed as &#39;Qu&#39; (so that Q never appears alone).
  ##### d. The location and particular letters should be randomly selected from a distribution of letters reflecting the _weights_ of letters from the settings.  A letter with a weight of 5 should be 5 times as likely to be chosen as a letter with a weight of 1 (assuming both are consonants or both are vowels).  In this way, more common letters can be set to appear more often.
  ##### e. A letter may appear on the board more than once.

This is done using the existing generate\_letters(size, weight) method that exists on the board, with the above criteria being handled in the actual implementation of the method.

##### 5. When choosing to view statistics, the player may view (1) _game score statistics_, or (2) _word statistics_.

To realize this, I created a Statistics class that is associated with multiple Game classes. It essentially holds an array of all games and the status attribute of Game allows the Statistic class to know which games are complete. The setup() method added to the Game class allows Games to be registered/added to the Statistic class.

Then the Statistic class also has two methods: get\_game\_statistics(), get\_word\_statistics(). These handle getting different statistics based on the games it is holding.

##### 6. For game score statistics, the player will view the list of scores, in descending order by final game score, displaying:
  ##### a. The final game score
  ##### b. The number of times the board was reset
  ##### c. The number of words entered in the game

The player may select any of the game scores from this list to view the settings for that game&#39;s _board size, number of minutes,_ and the _highest scoring word_ played in the game (if multiple words score an equal number of points, the first played will be displayed)_._

The get\_game\_statistics() function will return an array of objects that contain the

1. Final score for that game
2. Number of resets for that game
3. Number of words for that game
4. The Game Settings for that game

This will allow the UI to display any of the above information.

I also added num\_resets attribute to the Game class that will be incremented on every call to on\_reroll().

To handle the the first played word, the Games array already is ordered in time of creation because new games are simply appended to the list of games held in the Statistics class.

##### 7. For the word statistics, the player will view the list of words used, starting from the most frequently played, displaying:
    i. The word
    ii. The number of times the word has been played, across all games

The get\_word\_statistics() method will iterate through the games that the Statistics class holds and check the board associated with that game. This will allow it to see the words that were played as well as how many times each word was played. It can then aggregate these across all games to get the desired statistics.

##### 8. The user interface must be intuitive and responsive.

This is not represented in my design, as it will be handled entirely within the GUI implementation.

##### 9. The performance of the game should be such that students do not experience any considerable lag between their actions and the response of the application.

This is not represented in my design, as it will be handled entirely within the GUI implementation.

##### 10. For simplicity, you may assume there is a _single system_ running the application.

My design itself is what represents the entirety of a single system.
