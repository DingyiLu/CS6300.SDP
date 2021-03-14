# Test Plan

**Author**: Rishub Kumar

**Version**: 3.0
* Added one more test case as well as updated the rest of our test case results based on our final version of the app
* Updating strategy and test plan based on what actually occured and how we tested in reality

**Version**: 2.0
* Finished testing of MVP version of our app and filled out actual results from test cases as well as pass or fail

## 1 Testing Strategy

### 1.1 Overall strategy

As we are developing our app, we will perform tests by looking at the test cases table in Section 2. This will allow us to test the individual parts and components in our game that match with what we are currently developing. These will both be done by the developers during the application development phase, and as more and more components are built, we will be able to see how they fit together and test the overall integration as well.

We will then have system testing during the User Testing phase. The Tester and Developer roles are responsible for this as well. If we discover bugs or missing features, we will report these in our bug tracking system so that all developers are aware and able to pick up the bugs as they see fit. This can be done asynchronously and as long as our test cases are thorough, this will allow us to test all aspects of our application.


### 1.2 Test Selection

We will use category partitioning as one method. This will be used mainly for the individual tests that we are performing during developement but also during the overall system testing. By partitioning the expected behavior into separate categories, we can isolate errors better while still testing individual components as well as how components interact with each other.

For system/regression testing, we will still use this category partitioning while mainly taking advantage of finite state machine testing. This will allow us to test how our app moves between different states depending on users actions. It will test different paths and directions a user can go in and how the system as a whole responds to the user's actions. These paths will be laid out in section 2, and based on the project requirements, we will be able to make sure we are covering all edge cases.

### 1.3 Adequacy Criterion

For our overall system testing, our adequacy criterion will be based no how many of the expected results match our actual results. We will also take into account how many bugs we have so that we know what features are not working correctly as expected and/or are incomplete. Again, by making sure our list of test acses is thorough, we will be able to know how complete our overall application is and what requirements it is missing.

### 1.4 Bug Tracking

For bugs, we will use a Trello board that we have created for our team. This will allow us to see which ones we still need to do as well as which are being tested and which are done. With one central place for all bugs, developers can easily pick up bugs as needed and prioritize which ones should be taken in first.

### 1.5 Technology

For system testing, we can make use of android emulators and actual android devices. This will allow us to test different screen sizes and devices to make sure our application is flexible and can be run on any device, with an adaptable layout.

## 2 Test Cases

*This section should be the core of this document. You should provide a table of test cases, one per row. For each test case, the table should provide its purpose, the steps necessary to perform the test, the expected result, the actual result (to be filled later), pass/fail information (to be filled later), and any additional information you think is relevant.*

Purpose | Steps | Expected result | Actual Result | P/F
--- | --- | --- | --- | ---
Menu options work correctly | Open the application and attempt to click the different options (game, statistics, settings) | Options should lead to what they describe | Options lead to what they describe | P
Correct settings are set when a game is started | Adjust game settings to values other than defaults, start a game, and see if minutes, size of board, and weights of letters are correct | Minutes, size, and weights should match the settings | Minutes, size, and weights match the settings | P
Game starts with 0 points | Start a game, look at # of points | Points should be 0 | User starts at 0 points | P
Players score may go into negative values | Start game, immediately reset with 0 points | Points should be -5, and not stay at 0 | Points are at -5 | P
Game ends when time runs out or when user chooses to exit the game and the final score is displayed | Start and a game and let the timer run out or exit the game | Game is ended and final score is displayed | Game score and number of resets is displayed | P
User loses 5 points on a reroll | Start a game and choose to reroll the board | User should see points go down by 5 and board regenerated | Board is regenerated and user loses 5 points | P
Words can only be selected if they use adjacent letters | Start a game and try selecting letters that are not adjacent | Word should be rejected | Word cannot be selected | P
Words can be selected diagonally | Start a game and try selecting letters diagonally | Words should be allowed | Diagonal word is accepted | P
A single letter on the board may not be used twice | Try selecting a word while using a single letter twice | Word should be rejected | Word is rejected | P
'Qu' should count as 2 while other letters should be 1 | Start a game, create a word with 'Qu' in it | Qu should add 2 points while other letters should add 1 | "qu" counts as 2 points | P
Verify that board constraints are satisfied | Generate the board by starting a game or rerolling, Analyze Board | Board should be a square of size from game settings, letter Q is displayed as Qu, 1/5 of letters vowels and 4/5 consonants | 5 vowels and 20 consonants, so breakdown is 1/5 vowels and 4/5 consonants for a board size of 5x5 | P
Verify Game Statistics is displayed correctly | Play two games, go to game statistics, look at what is displayed | Final score should match score of the games, number of resets should match, number words entered should match, scores should be displayed in descending order | Game Stats table is displayed correctly | P
Verify Word statistics is displayed correctly | Play 1 game, keep track of words and how many times each word was played, view word statistics | statistics should match what was kept track of | Word Statistics are displayed correctly | P
Application should have persistent storage | Open application and adjust settings and play a game. View game statistics with this game added. Close Application and open it back up | Statistics and settings should not be reset to defaults | Statistics and settings match what was shown right before application was closed | P
Performance and Lag | Start application, adjust settings, play a game, view statistics | There should not be any lag between actions | No lag between actions | P