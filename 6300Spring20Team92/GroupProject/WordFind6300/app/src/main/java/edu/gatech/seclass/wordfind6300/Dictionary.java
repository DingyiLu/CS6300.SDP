package edu.gatech.seclass.wordfind6300;

import android.app.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.os.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Dictionary extends Dialog implements android.view.View.OnClickListener {
    private Button cancel;
    private Activity activity = null;
    private ArrayList<String> words = new ArrayList<String>();
    private int totalLetterCount = 3315926;
    private Map<Character, Integer> letterCounts = new HashMap<Character, Integer>();
    private Map<Character, Double> letterScores = new HashMap<Character, Double>();
    public boolean inUse;

    public Dictionary(Activity a) {
        super(a);
    }

    public void setInUse(boolean way) {
        inUse = way;
        if (!way) {
            return;
        }
        if (words.size() > 0) {
            return;
        }
        InputStream is = MainActivity.activity.getResources().openRawResource(R.raw.dictionary);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        Log.d("Dictionary", "starting reading words");
        try {
            String line;
            while((line = br.readLine()) != null) {
                words.add(line);
            }
        }
        catch (IOException e) {
            Log.e("","cannot read dictionary " + e);
        }
        Log.d("Dictionary", "ended reading words, size:"+words.size());

        //Log.d("Dictionary", "started word processing");
        //processDictLetterFrequencyOld();

        is = MainActivity.activity.getResources().openRawResource(R.raw.word_scores);
        br = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while((line = br.readLine()) != null) {
                String[] fields = line.split("\t");
                letterCounts.put(fields[0].charAt(0), Integer.parseInt(fields[1]));
                letterScores.put(fields[0].charAt(0), Double.parseDouble(fields[2]));
            }
        }
        catch (IOException e) {
            Log.e("","cannot read scores " + e);
        }
        Log.d("Dictionary", "end setup");
        //only required pre-run
        //processDictLetterFrequencyData();
    }

    public Map<Character, Integer> getLetterCounts() {
        return this.letterCounts;
    }

    public int getTotalLetterCount() {
        return totalLetterCount;
    }

    private void processDictLetterFrequencyData() {
        for (String word : this.words) {
            for (int i=0; i<word.length(); i++) {
                Character c = word.charAt(i);
                int count = 1;
                if (letterCounts.containsKey(c)) {
                    count += letterCounts.get(c);
                }
                letterCounts.put(c, count);
            }
        }

        TreeMap<Character, Integer> sorted = new TreeMap<>();
        sorted.putAll(letterCounts);
        List<Character> letterByKey = new ArrayList<>(letterCounts.keySet());
        Collections.sort(letterByKey);

        Character maxLetter = ' ';
        long maxCount =0;
        long letterCountTotal = 0;

        for (Character c : letterByKey) {
            long letterCount = letterCounts.get(c);
            letterCountTotal += letterCount;
            if (letterCount > maxCount) {
                maxCount = letterCount;
                maxLetter= c;
            }
        }

        for (Character c : letterByKey) {
            long charCount = letterCounts.get(c);
            double letterScore = ((double)maxCount) / ((double)charCount);
            this.letterScores.put(c, letterScore);
            //System.out.println(c + " count:"+charCount + " max:"+maxLetter+" :"+maxCount +
            //        " score:"+letterScores.get(c));
            //System.out.println(c + "\t"+charCount + "\t"+letterScores.get(c));
        }
        if (letterCountTotal < Integer.MAX_VALUE) {
            this.totalLetterCount = (int) letterCountTotal;
            Log.d("TOTAL", ""+totalLetterCount);
        }
        else {
            throw new IllegalStateException("too many characters in dictionary");
        }
    }

    public double getWordScore(String word) {
        if (!words.contains(word)) {
            return -1;
        }
        double totalScore = 0;
        for (int i=0; i<word.length(); i++) {
            double charScore = this.letterScores.get(word.charAt(i));
            totalScore += charScore;
        }
        return totalScore;
    }

    // Return the number of words in the dictionary that contain the passed-in sub-string
    // as a prefix (including the prefix itself, if it is a word). If the passed-in string
    // is empty or null, return a count of all of the words in the dictionary.
    public int getWordCountWithPrefix(String prefix) {
        int count = this.words.size();
        if (prefix != null && prefix.length() > 0) {
            count = 0;
            for (String word : this.words) {
                if (word.toLowerCase().startsWith(prefix.toLowerCase())) {
                    count++;
                }
            }
        }
        //Log.d("DICT", "Words that start with '"+prefix+"' : " + count);
        return count;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.content_dict_info);
        TextView v = findViewById(R.id.text_view_info);
        String info = "Dictionary mode checks that the words the player enters exist in the English language. Words that are not in the dictionary are not accepted";
        info += "\n\nDictionary mode provides a 'dictionary score' for each word based on the occurence frequency of each letter in the word. ";
        info += "The letter 'e' is the most commonly used letter in English and has a dictionary score of 1 ";
        info += "whereas the letter 'f' occurs 9.5 times less frequently in the language and therefore has a dictionary score of 9.5. ";
        info += "For example, the word 'fee' has a dictionary score of 9.5 + 1.0 + 1.0 = 11.5 ";
        info += "\n\nDictionary mode also sets the letter settings weights according to each letter's occurrence frequency in the language. This allows the user to enter more and longer words than an equal weight letter distribution.";
        info += "\n";
        v.setText(info);

        findViewById(R.id.button_cancel).setOnClickListener(this);
        ImageView iv = (ImageView) findViewById(R.id.image_view_dict);
        iv.setImageResource(R.drawable.dict_info);
        Window window = this.getWindow();
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
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
}