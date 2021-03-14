package edu.gatech.seclass.wordfind6300;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.util.List;

public class WordStatsFragment extends Fragment {
    TableLayout wordStatsTable;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_word_stats, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.wordStatsTable = (TableLayout) getView().findViewById(R.id.word_stats_table);
        TableRow wordStatsRow = new TableRow(this.mContext);

        TextView rankHeader = new TextView(this.mContext);
        rankHeader.setText(" Rank ");
        rankHeader.setTextColor(Color.BLACK);
        wordStatsRow.addView(rankHeader);

        TextView wordHeader = new TextView(this.mContext);
        wordHeader.setText(" Word ");
        wordHeader.setTextColor(Color.BLACK);
        wordStatsRow.addView(wordHeader);

        TextView freqHeader = new TextView(this.mContext);
        freqHeader.setText(" Frequency ");
        freqHeader.setTextColor(Color.BLACK);
        wordStatsRow.addView(freqHeader);

        this.wordStatsTable.addView(wordStatsRow);


        // Todo: Use this
        // The following demonstrates how to read WordStat entries from the persistent
        // storage database. An "Observer" is used since the data is loaded form the database
        // asynchronously from the main UI thread. This activity should react to the onChanged()
        // event of the getWordStatsTask() to render and display words and frequencies on
        // this panel
        StatStorage ss = new StatStorage(this.mContext.getApplicationContext());
        ss.getWordStatsTask().observe(this, new Observer<List<WordStat>>() {
            @Override
            public void onChanged(@Nullable List<WordStat> stats) {
                // Todo: Do something interesting here with the DB data that was fetched
                resetRows();
                int rank = 1;
                for(WordStat stat : stats) {
                    Log.d("WORD", stat.word + " : " + stat.frequency);
                    addToTable(rank, stat);
                    rank++;
                }
            }
        });
    }
    private void resetRows(){
        // We want to leave the header row
        int numToRemove = this.wordStatsTable.getChildCount() - 1;
        this.wordStatsTable.removeViews(1, numToRemove);
    }
    private void addToTable(int rank, WordStat stat) {
        TableRow statRow = new TableRow(this.mContext);

        TextView rankView = new TextView(this.mContext);
        rankView.setText("" + rank);
        rankView.setTextColor(Color.BLACK);
        rankView.setGravity(Gravity.CENTER);
        statRow.addView(rankView);

        TextView wordView = new TextView(this.mContext);
        wordView.setText(stat.word);
        wordView.setTextColor(Color.BLACK);
        wordView.setGravity(Gravity.CENTER);
        statRow.addView(wordView);

        TextView freqView = new TextView(this.mContext);
        freqView.setText("" + stat.frequency);
        freqView.setTextColor(Color.BLACK);
        freqView.setGravity(Gravity.CENTER);
        statRow.addView(freqView);

        this.wordStatsTable.addView(statRow);
    }
}
