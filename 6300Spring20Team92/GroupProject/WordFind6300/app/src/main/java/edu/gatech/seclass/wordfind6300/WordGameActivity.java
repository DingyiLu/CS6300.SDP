package edu.gatech.seclass.wordfind6300;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View.*;
import android.widget.*;
import android.view.Gravity;
import java.util.*;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.*;

public class WordGameActivity extends /*AppCompatActivity,*/ ListActivity {
    private static WordGameActivity activity; //TODO fix this static - there must be a better way :(
    static public GameCells gameCells;

    private ArrayList<String> usedWords =new ArrayList<String>();
    private class UsedWord{
        String word;
        double score;
        boolean dictionary;
        public UsedWord(String word, double score, boolean dictionary) {
            this.word = word;
            this.score = score;
            this.dictionary = dictionary;
        }
        public String toString() {
            return this.dictionary ? this.word + " " + String.format(":%.2f", this.score) :
                    this.word + " " + String.format(":%d", (int) this.score);
        }
    }
    private ArrayList<UsedWord> usedWordsScores = new ArrayList<UsedWord>();
    private ArrayAdapter<UsedWord> usedWordScoresAdapter;

    private TextView scoreView;
    private int gameScore = 0;
    private int gameResets = 0;
    private int seconds = 0;
    private Handler threadHandler;
    private int animations = 3;
    private Animation animation_flash;
    private Animation animation_rotate;
    private Thread timerThread;
    private static Thread emulatorThread;
    private StatStorage statStorage;
    private Toast toastMsg;

    private double gameDictionaryScore;
    private String bestDictionaryWord = "";
    private double bestDictionaryScore = 0;

    private LetterBoundary hintLetter = null;
    private int hintNum = 0;
    private boolean stopEmulator = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WordGameActivity.activity = this;
        setContentView(R.layout.activity_word_game);

        gameCells = (GameCells) findViewById(R.id.gameCells);
        Button closeButton = (Button) findViewById(R.id.button_exit);
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.endGame();
            }
        });
        Button resetButton = (Button) findViewById(R.id.button_reset);
        resetButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                activity.showToast("One second please, examining new board...", true);
                activity.gameScore -= 5;
                activity.gameResets += 1;
                gameCells.populateBoard(MainActivity.dictionary.inUse);
                GameCells an = (GameCells) findViewById(R.id.gameCells);
                an.startAnimation(animation_rotate);
                activity.showScore();
                if (MainActivity.robot.inUse) {
                    // for hints
                    MainActivity.robot.analyzeBoardPaths(gameCells.letterBoundary);
                    if (MainActivity.robot.playsGame && WordGameActivity.emulatorThread != null) {
                        stopEmulator = true;
                        emulatorThread.interrupt();
                        showToast("restting board paths", true);
                        startEmulator();
                    }
                }
            }
        });

        scoreView = (TextView) findViewById(R.id.text_score);
        this.toastMsg = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

        usedWordScoresAdapter = new ArrayAdapter<WordGameActivity.UsedWord>(this, android.R.layout.simple_list_item_1, usedWordsScores);
        setListAdapter(usedWordScoresAdapter);

        this.showScore();
        this.startTimer((TextView) findViewById(R.id.text_timer));

        animation_flash = AnimationUtils.loadAnimation(this, R.anim.game_anim_flash);
        animation_rotate = AnimationUtils.loadAnimation(this, R.anim.game_anim_rotate);

        statStorage = new StatStorage(getApplicationContext());
    }

    public static GameCells getGameCells() {
        return gameCells;
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        gameCells.populateBoard(MainActivity.dictionary.inUse);
        if (hasFocus && MainActivity.robot.inUse) {
            MainActivity.robot.analyzeBoardPaths(gameCells.letterBoundary);
            if (MainActivity.robot.playsGame) {
                startEmulator();
            }
        }
    }

    private void startTimer(final TextView timerView) {
        // endGame() uses UI and so has to run in the main UI thread. ThreadHandler listens for messages to run on the main thread.
        threadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                activity.endGame();
            }
        };

        //Timer runs in a separate thread so that GUI actions do not block timing
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                activity.timerThread = Thread.currentThread();
                activity.seconds = 0;
                final int runTimeSecs = (Settings.getSettings().getMinutes() * 60);
                for (activity.seconds = 0; activity.seconds <= runTimeSecs-1; activity.seconds++) {
                    if (Thread.currentThread().interrupted()) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        //Log.e("TimerException", "exc:"+ex);
                        break;
                    }
                    if (activity.seconds < runTimeSecs-1) {
                        timerView.post(new Runnable() {
                            @Override
                            public void run() {
                                int remainSec = runTimeSecs - activity.seconds;
                                int second = remainSec % 60;
                                int minute = remainSec / 60;
                                String ms = "";
                                if (minute >= 60) {
                                    int hour = minute / 60;
                                    minute %= 60;
                                    ms = "" + hour + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
                                } else {
                                    ms = minute + ":" + (second < 10 ? "0" + second : second);
                                }
                                timerView.setText("Time Remaining:" + ms);
                            }
                        });
                    }
                    else {
                        Message message = activity.threadHandler.obtainMessage();
                        message.sendToTarget();
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

    private void endGame() {
        activity.timerThread.interrupt();
        if (this.emulatorThread != null) {
            this.stopEmulator = true;
            this.emulatorThread.interrupt();
        }

        // Get longest word played
        String longest_word = "";
        for (String word : usedWords) {
            if (word.length() > longest_word.length()) {
                longest_word = word;
            }
        }
        // Add game-level stats
        this.statStorage.insertGameStatTask(this.gameScore, this.gameResets, this.usedWords.size(),
                Settings.getSettings().getDimensions(), Settings.getSettings().getMinutes(),
                longest_word, longest_word.length());

        //Increment word frequencies
        for (String word: this.usedWords){
            this.statStorage.incrementWordStatTask(word);
        }

        // build end of game dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String gameScore = "Game Score:"+activity.gameScore;
        gameScore += "\nResets:"+activity.gameResets;
        gameScore += "\nLongest Word:"+longest_word;

        if (MainActivity.dictionary.inUse) {
            gameScore += String.format("\n\nDictionary Score:%.2f", this.gameDictionaryScore);
            gameScore += String.format("\nDictionary Best Word:%s", this.bestDictionaryWord);
        }

        builder.setMessage(gameScore)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final Toast toast = Toast.makeText(getApplicationContext(), "Thanks for playing word game", Toast.LENGTH_SHORT);
                        toast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 800);
                        finish();
                    }
                })
        ;
        AlertDialog alert = builder.create();
        alert.setTitle("Game Ended");
        alert.show();
    }

//    public void message(String msg) {
//        TextView m = (TextView) findViewById(R.id.text_message);
//        m.setText(msg);
//        m.invalidate();
//    }

    public void showHint(String hintWord, LetterBoundary letter) {
        if (!MainActivity.dictionary.inUse || hintWord.trim().length() < 2) {
            return;
        }
        if (hintLetter != null ) {
            if (hintLetter.equals(letter)) {
                return;
            }
            //hintToast.cancel();
        }
        String hintPrefix = gameCells.getWordFromLetters();
        ArrayList<LetterBoundary> adjoinings = gameCells.getAdjoingLetters(letter);
        ArrayList<String> hintWords = new ArrayList<>();
        hintNum += 1;
        LetterBoundary pathRoot = gameCells.usedBoxes.get(0);

        //System.out.println("\n\n===> Activity "+hintNum+" looking for hint for prefix:"+hintWord);
        for (LetterBoundary lb : adjoinings) {
            String adjWord = hintWord.trim() + lb.letter.getChars();
            //System.out.println(" ==> Activity "+hintNum+" looking for hint for adj word:"+adjWord);

            ArrayList<String> adjWords = MainActivity.robot.getWordCountWithPrefix(pathRoot, adjWord);
            for (String ws : adjWords) {
                Log.d("DEBUG", "AJD: '"+ws+"'");
                if (!hintWords.contains(ws) && !usedWords.contains(ws)) {
                    Log.d("DEBUG", "AJD not Used: '"+ws+"'");
                    hintWords.add(ws);
                }
            }
        }
        System.out.println("===> Activity " + hintNum+ " found words:"+ hintWords.size() + " for " + hintWord+"\n");
        for (String w: hintWords) {
            System.out.println("  hint_word:" + w);
        }
        String hint = "From your word '"+hintPrefix+"' there are ";
        if (hintWords.size()>0) {
            hint += hintWords.size() + " more words to find";
        }
        else {
            hint += "no more words";
        }
        hintLetter = letter;
        this.showToast(hint, false);
        //hintToast = Toast.makeText(getApplicationContext(), hint, Toast.LENGTH_SHORT);
        //hintToast.show();
    }

    public void showWord(String usersWord) {
        TextView m = (TextView) findViewById(R.id.text_entered_word);
        if (usersWord.length()==0) {
            m.setVisibility(View.INVISIBLE);
        }
        else {
            m.setVisibility(View.VISIBLE);
            m.setText(usersWord);
        }
    }

    public void showToast(String msg, boolean longer) {
        this.toastMsg.cancel();
        if (MainActivity.robot.playsGame) {
            return;
        }
        int len = Toast.LENGTH_SHORT;
        if (longer) len = Toast.LENGTH_LONG;
        this.toastMsg = Toast.makeText(getApplicationContext(), msg, len);
        //this.toastMsg.setText(msg);
        this.toastMsg.setGravity(Gravity.TOP|Gravity.RIGHT, 100, 400);
        this.toastMsg.show();
    }

    public void showScore() {
        String score = "Score:"+this.gameScore + "  Resets:"+ this.gameResets;
        if (MainActivity.dictionary != null && MainActivity.dictionary.inUse) {
            score += String.format( "  Dictionary:%.2f", this.gameDictionaryScore);
        }
        this.scoreView.setText(score);
    }

    private void animate(String word) {
        if (word.length()>1) {
            GameCells an = (GameCells) findViewById(R.id.gameCells);
            an.startAnimation(animation_flash);
        }
    }

    public void scoreWord(GameBoard gameBoard, String usersWord) {
        hintLetter = null;
        // total scores
        String errMsg = "";
        if (usersWord.length() < 2) {
            errMsg = "word too short";
        }
        else {
            if (this.usedWords.contains(usersWord)) {
                errMsg = usersWord + " was already used";
            }
        }

        if (errMsg.length() > 0) {
            this.showToast(errMsg, false);
            return;
        }

        String toastStr = "";

        if (!MainActivity.dictionary.inUse) {
            toastStr = String.format("%s scored %d", usersWord, usersWord.length());
            //latest on top of list
            this.usedWordsScores.add(0, new WordGameActivity.UsedWord(usersWord, usersWord.length(), false));
        }
        else {
            double dictScore = MainActivity.dictionary.getWordScore(usersWord);
            if (dictScore < 0) {
                showToast(usersWord + " - is not a dictionary word", false);
                animate(usersWord);
                return;
            }
            if (dictScore > this.bestDictionaryScore) {
                this.bestDictionaryScore = dictScore;
                this.bestDictionaryWord = usersWord;
            }
            this.gameDictionaryScore += dictScore;
            toastStr = String.format("%s scored %d with dictionary score %.2f", usersWord, usersWord.length(), dictScore);
            if (MainActivity.robot.playsGame) {
                this.usedWordsScores.add(0, new WordGameActivity.UsedWord(usersWord, usersWord.length(), false));
            }
            else {
                this.usedWordsScores.add(new WordGameActivity.UsedWord(usersWord, dictScore, true));
                Collections.sort(this.usedWordsScores, new Comparator<WordGameActivity.UsedWord>() {
                    @Override
                    public int compare(WordGameActivity.UsedWord o1, WordGameActivity.UsedWord o2) {
                        return o1.score < o2.score ? 1 : -1;
                    }
                });
            }
        }
        this.gameScore += usersWord.length();
        this.usedWords.add(usersWord);

        usedWordScoresAdapter.notifyDataSetChanged();
        this.showScore();
        this.showToast(toastStr, false);
    }

    private void playPath(FoundWord path) {
        int index = 0;
        LetterBoundary lastMove = null;
        int moves = path.path.size();

        for (int move=0; move <= moves; move++ ) {
            LetterBoundary lb = null;
            if (move<moves) {
                lb = path.path.get(move);
            }
            else {
                lb = path.path.get(moves-1);
            }

            final LetterBoundary letBound = lb;
            int m = 0;
            if (index ==0) {m = -1;}
            if (index ==path.path.size()) {m = 1;}
            if (Thread.currentThread().interrupted()) { break; }
            final int mode = m;

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    LetterBoundary b = letBound;
                    b.setHilite(true);
                    if (mode==-1)
                        gameCells.touch_start((b.left + b.right) / 2, (b.top + b.bottom) / 2);
                    if (mode==0)
                        gameCells.touch_move((b.left + b.right) / 2, (b.top + b.bottom) / 2);
                    if (mode==1) {
                        gameCells.touch_move((b.left + b.right) / 2, (b.top + b.bottom) / 2);
                        gameCells.invalidate();
                        gameCells.touch_up((b.left + b.right) / 2, (b.top + b.bottom) / 2);
                        gameCells.invalidate();
                    }
                    gameCells.invalidate();
                }
            });

            try { Thread.sleep(1000);} catch (InterruptedException e) { break; }
            index += 1;
        }
    }

    void emulateGame() {
        ArrayList<FoundWord> paths = MainActivity.robot.getBoardPaths();
        for (FoundWord path : paths) {
            if (this.stopEmulator) {
                break;
            }
            this.playPath(path);
        }
    }

    void startEmulator() {
        if (this.emulatorThread != null) {
            this.emulatorThread.interrupt();
        }
        this.stopEmulator= false;
        this.emulatorThread = new Thread() {
            @Override
            public void run() {
                emulateGame();
            }
        };
        this.emulatorThread.start();
    }
}

