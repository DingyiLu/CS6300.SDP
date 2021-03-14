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

public class GameStatsFragment extends Fragment {
    TableLayout gameStatsTable;
    StatStorage storage;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_stats, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.gameStatsTable = (TableLayout) getView().findViewById(R.id.game_stats_table);
        TableRow gameStatsRow = new TableRow(this.mContext);

        TextView rankHeader = new TextView(this.mContext);
        rankHeader.setText(" Rank ");
        rankHeader.setTextColor(Color.BLACK);
        gameStatsRow.addView(rankHeader);

        TextView scoreHeader = new TextView(this.mContext);
        scoreHeader.setText(" Score");
        scoreHeader.setTextColor(Color.BLACK);
        gameStatsRow.addView(scoreHeader);

        TextView resetHeader = new TextView(this.mContext);
        resetHeader.setText(" Resets ");
        resetHeader.setTextColor(Color.BLACK);
        gameStatsRow.addView(resetHeader);

        TextView wordCountHeader = new TextView(this.mContext);
        wordCountHeader.setText(" Words ");
        wordCountHeader.setTextColor(Color.BLACK);
        gameStatsRow.addView(wordCountHeader);

        this.gameStatsTable.addView(gameStatsRow);


        // Todo: Use this
        // The following demonstrates how to read GameStat entries from the persistent
        // storage database. An "Observer" is used since the data is loaded form the database
        // asynchronously from the main UI thread. This activity should react to the onChanged()
        // event of the getGameStatsTask() to render and display games
        this.storage = new StatStorage(this.mContext.getApplicationContext());
        this.storage.getGameStatsTask().observe(this, new Observer<List<GameStat>>() {
            @Override
            public void onChanged(@Nullable List<GameStat> stats) {
                // Todo: Do something interesting here with the DB data that was fetched
                resetRows();
                int rank = 1;
                for(GameStat stat : stats) {
                    Log.d("GAME", "id="+stat.id +
                            " score="+stat.score +
                            " resets="+stat.resets +
                            " words="+stat.words +
                            " dimensions="+stat.dimensions +
                            " minutes="+stat.minutes +
                            " highestWord="+stat.highestWord +
                            " highestWordScore="+stat.highestWordScore);
                    addToTable(rank, stat);
                    rank++;
                }
            }
        });
    }

    private void resetRows(){
        // We want to leave the header row
        int numToRemove = this.gameStatsTable.getChildCount() - 1;
        this.gameStatsTable.removeViews(1, numToRemove);
    }
    private void addToTable(int rank, GameStat stat) {
        TableRow statRow = new TableRow(this.mContext);
        statRow.setId(stat.id);
        statRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIndividualGame(view);
            }
        });

        TextView rankView = new TextView(this.mContext);
        rankView.setText("" + rank);
        rankView.setTextColor(Color.BLACK);
        rankView.setGravity(Gravity.CENTER);
        statRow.addView(rankView);

        TextView scoreView = new TextView(this.mContext);
        scoreView.setText("" + stat.score);
        scoreView.setTextColor(Color.BLACK);
        scoreView.setGravity(Gravity.CENTER);
        statRow.addView(scoreView);

        TextView resetsView = new TextView(this.mContext);
        resetsView .setText("" + stat.resets);
        resetsView .setTextColor(Color.BLACK);
        resetsView .setGravity(Gravity.CENTER);
        statRow.addView(resetsView );

        TextView wordsView = new TextView(this.mContext);
        wordsView.setText("" + stat.words);
        wordsView.setTextColor(Color.BLACK);
        wordsView.setGravity(Gravity.CENTER);
        statRow.addView(wordsView);

        this.gameStatsTable.addView(statRow);
    }
    public void showIndividualGame(View view){
        Log.d("Row", "game stat row clicked");
        int gameId = view.getId();
        this.storage.getGameByIDTask(gameId).observe(this, new Observer<GameStat>() {
            @Override
            public void onChanged(GameStat gameStat) {
                individualGameAlert(gameStat);
            }
        });
    }
    public void individualGameAlert(GameStat gameStat){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setMessage("Board Size: " + gameStat.dimensions + "\nMinutes: " + gameStat.minutes + "\nBest Word: " + gameStat.highestWord)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
        ;
        AlertDialog alert = builder.create();
        alert.setTitle("Game " + gameStat.id + " statistics");
        alert.show();
    }
}
