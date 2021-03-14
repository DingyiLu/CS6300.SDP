package edu.gatech.seclass.wordfind6300;

import android.app.*;
import android.view.*;
import android.widget.*;
import android.os.*;

import androidx.recyclerview.widget.SortedList;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class FoundWord implements Comparable<FoundWord> {
    String word;
    Double score;
    ArrayList<LetterBoundary> path;

    public FoundWord(String word, double score, ArrayList<LetterBoundary> path) {
        this.word = word;
        this.score = score;
        this.path = path;
    }
    public double getScore() { return this.score;}

    //@Override
    public int compareToDict(FoundWord obj1) {
        if (obj1.score == score) return 0;
        if (obj1.score > score) return 1;
        return -1;
    }
    @Override
    public int compareTo(FoundWord obj1) {
        // stored in a set so values must be unique.
        if (word.length() == obj1.word.length()) return 0;
        if (word.length() < obj1.word.length()) return 1;
        return -1;
    }
}

public class Robot extends Dialog implements android.view.View.OnClickListener {
    private Button cancel;
    public boolean inUse;
    public boolean playsGame;
    private ConcurrentHashMap<String, FoundWord> legalWords = new ConcurrentHashMap<>();
    public static int pathTraceNumber = 0;
    private int searchCounts=0;

    public Robot(Activity a) {
        super(a);
        this.inUse = false;
        this.playsGame = false;
    }

    public void setInUse(boolean way) {
        inUse = way;
    }

    public ArrayList<String> getWordCountWithPrefix(LetterBoundary root, String searchWord) {
        ArrayList<String> words = new ArrayList<>();
        for (FoundWord fw: this.legalWords.values()) {
            //System.out.println("    HINT searchWord:"+searchWord+" found_word:"+fw.word);
            if (fw.word.startsWith(searchWord)) {
                if (fw.path.get(0) == root) {
                    words.add(fw.word);
                }
                //TODO this still not reliable.
                //System.out.println("  ***HINT FOUND prefix:"+searchWord+" word:"+fw.word+" start:"+fw.path.get(0));
            }
        }
        return words;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.content_robot_info);
        TextView v = findViewById(R.id.text_view_info);
        String info = "In Robot mode a robot picks the dictionary words for Word Game and shows you the words picked. ";
        info += "The robot tries to find the highest scoring dictionary words.";
        info += "\n";
        v.setText(info);
        findViewById(R.id.button_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public String wordFromLetters(Stack<LetterBoundary> letters) {
        String word = "";
        for (LetterBoundary lb: letters) {
            word += lb.letter.getChars();
        }
        return word;
    }

    private void searchPath(int level, Stack<LetterBoundary> path) {
        if (level > 3) {
            return;
        }
        LetterBoundary root = path.peek();
        // form the next set of letters to search
        ArrayList<LetterBoundary> adjoinings = WordGameActivity.getGameCells().getAdjoingLetters(root);

        for (LetterBoundary lb : adjoinings) {
            this.searchCounts += 1;
            if (path.contains(lb)) {
                continue;
            }
            path.push(lb);

            // Decide if its a dictionary word
            double score = 0;
            if (path.size() > 1) {
                String pathWord = this.wordFromLetters(path);
                score = MainActivity.dictionary.getWordScore(pathWord);
                if (score > 0) {
                    if (!legalWords.containsKey(path)) {
                        ArrayList<LetterBoundary> wordPath = new ArrayList<>();
                        for (LetterBoundary pathLetter: path) {
                            wordPath.add(pathLetter);
                        }
                        legalWords.put(pathWord, new FoundWord(pathWord, score, wordPath));
                    }
                }
            }

            //System.out.println("===== l:" + level + " rc:"+ cords + " --word:" + newWord + " sc:"+String.format(":%.2f", score) );
            searchPath(level + 1, path);
            LetterBoundary pop = path.pop();
        }
    }

    public void analyzeBoardPaths(LetterBoundary[][] letters) {
        legalWords = new ConcurrentHashMap<>();
        Robot.pathTraceNumber += 1;
        int dim = Settings.getSettings().getDimensions();
        boolean multiThread = true;
        long startTime = System.currentTimeMillis();

        // do the search
        if (multiThread) {
            // Use a set of worker threads
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            for (int r = 0; r < dim; r++) {
                for (int c = 0; c < dim; c++) {
                    // Dispatch this starting cell to the work queue
                    final Stack<LetterBoundary> path = new Stack<>();
                    path.push(letters[r][c]);
                    executorService.execute(new Runnable() {
                        public void run() {
                            searchPath(1, path);
                        }
                    });
                }
            }
            // Shutdown worker threads
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            Stack<LetterBoundary> path = new Stack<>();
            for (int r = 0; r < dim; r++) {
                for (int c = 0; c < dim; c++) {
                    //String word = letters[r][c].letter.getChars();
                    path.push(letters[r][c]);
                    searchPath(1, path);
                    path.pop();
                }
            }
        }

        //SortedSet<FoundWord> words = new TreeSet<FoundWord>(legalWords.values());
        //SortedSet<String> words = new TreeSet<String>(legalWords.keySet());
        //for (FoundWord fw : words) {
//        for (String fw : words) {
//            FoundWord foundEntry = legalWords.get(fw);
//            System.out.println("LEGAL WORD ------------->"+fw + " traceNum:"+Robot.pathTraceNumber);
//            for (LetterBoundary lb : foundEntry.path) {
//                System.out.println("  path:"+lb);
//            }
//        }

        long end = System.currentTimeMillis();
        System.out.println("================> search count "+searchCounts+ " " + (end-startTime));
    }

    public ArrayList<FoundWord> getBoardPaths() {
        //SortedList<FoundWord> emulatorWords = new TreeSet<FoundWord>(legalWords.values());
        //SortedSet<FoundWord> emulatorWords = new TreeSet<FoundWord>(legalWords.values());
        //SortedSet<FoundWord> emulatorWords = new TreeSet<FoundWord>(legalWords.values());
        //TreeMap<FoundWord> sorted = new TreeMap<FoundWord, >(legalWords.values());
        //Set<Entry<String, String>> mappings = sorted.entrySet();
        ArrayList<FoundWord> words = new ArrayList<FoundWord>(legalWords.values());
        Collections.sort(words);
//        for (FoundWord f : words) {
//            System.out.println("+++++>>>"+f.word.length()+ " "+f.word);
//        }
        //TODO not sorted by word length..
        //TODO check generate when dim higher than 5 - not working
        //TODO check when search depth > 3
        return words;
    }
}