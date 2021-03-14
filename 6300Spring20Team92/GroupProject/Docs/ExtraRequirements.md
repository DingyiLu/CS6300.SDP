# Extra Requirements
**Author**: David Murphy

**Version**: 1.0

## Non-functional requirements

* Performance - Since the app provides an interactive GUI to the user, all functions must occur with minimal delay. Therefore algorithms (e.g. to verify that an entered word meets all letter adjacency rules) must complete quickly.

* Timing - Timing of a game must be independent of what state the game is in. For example, if the game's GUI thread is blocked waiting for user input the timer must still be able to count down time and end the game correctly. For that reason, the timer will be implemented in a thread separate from the GUI thread and be able to interrupt (and end) the game at exactly the right time irrespective of what state the game is in at the time-out interrupt.

* Learnability - The user interface should be clear and concise, sufficient that a user can play the game without written instructions.

* Connectivity - The user should be able to play the game when disconnected from the internet.

* Installability - The user should be able to install the game on their phone in a straightforward manner from the Google Play Store.

### New Non-functional requirements discovered as a result of initial coding efforts.

* Variable Screen Sizes - The UI layouts must be able to scale appropriately depending on the screen resolutions of different devices.
 
* The app must be able to persist its state between runs. This will include statistics and settings.

## Additional Requirements

* During requirements discussions, the team discussed that having a way to verify that the words the player entered are valid English words. If we have time we would like to implement this feature. To do that we should create an Android resource that is a repository of valid dictionary words. This feature would be off by default but could be enabled by the user.

* The team discussed how best to alow the user to enter words. On a smart phone the virtual keyboard takes a significant amount of screen real estate which would limit the game's useability. The team would therefore look to find an alternative way for the user to enter words - e.g. possibly using a touch interface.

