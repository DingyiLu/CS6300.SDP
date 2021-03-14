package edu.gatech.seclass.wordfind6300;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static Dictionary dictionary = null;
    public static Robot robot = null;
    public static Activity activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        dictionary = new Dictionary(activity);
        robot = new Robot(activity);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ImageButton b = findViewById(R.id.button_dict_help);
        b.setImageResource(R.drawable.dict_help);

        // Load "current" settings from persistent storage, if they exist
        StatStorage storage = new StatStorage(getApplicationContext());
        storage.getGameSettingsCurrentTask().observe(this, new Observer<GameSettings>() {
            @Override
            public void onChanged(@Nullable GameSettings settings) {
                if (settings != null) {
                    Log.d("GAME", "Loading current settings with: id=" + settings.id +
                            " dimensions=" + settings.dimensions +
                            " minutes=" + settings.minutes +
                            " useDictionary=" + settings.useDictionary +
                            " weights=" + settings.weights);
                    Settings cs = Settings.getSettings();
                    cs.setDimensions(settings.dimensions);
                    cs.setMinutes(settings.minutes);
                    for (int i = 0; i < settings.weights.size(); i++) {
                        cs.setWeightByPosition(i, settings.weights.get(i));
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Button mButton = findViewById(R.id.button_word_game);
        mButton.setOnClickListener(this);
        Button mButtonDict = findViewById(R.id.button_word_game_dictionary);
        mButtonDict.setOnClickListener(this);
        findViewById(R.id.button_dict_help).setOnClickListener(this);
        findViewById(R.id.button_robot_help).setOnClickListener(this);
        findViewById(R.id.robot_play).setOnClickListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.button_word_game:
                MainActivity.robot.setInUse(false);
                MainActivity.dictionary.setInUse(false);
                MainActivity.robot.playsGame = false;
                Intent intentWordGame = new Intent(this, WordGameActivity.class);
                startActivity(intentWordGame);
                break;

            case R.id.button_word_game_dictionary:
                MainActivity.showToast("One second please - examining board...");
                MainActivity.dictionary.setInUse(true);
                MainActivity.robot.setInUse(true); // for hints
                MainActivity.robot.playsGame = false;
                Intent intentWordGameDictionary = new Intent(this, WordGameActivity.class);
                startActivity(intentWordGameDictionary);
                break;

            case R.id.button_dict_help:
                MainActivity.dictionary.show();
                break;

            case R.id.robot_play:
                MainActivity.robot.setInUse(true);
                MainActivity.dictionary.setInUse(true);
                MainActivity.robot.playsGame = true;
                MainActivity.showToast("One second please - examining board...");
                Intent intentWordGameRobot = new Intent(this, WordGameActivity.class);
                startActivity(intentWordGameRobot);
                break;

            case R.id.button_robot_help:
                MainActivity.robot.show();
                break;

            case R.id.button_adjust_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                break;

            case R.id.button_view_statistics:
                Intent intentStatistics = new Intent(this, StatsActivity.class);
                startActivity(intentStatistics);
                break;
        }
    }

    public static void showToast(String msg) {
        if (activity != null) {
            Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.RIGHT, 100, 400);
            toast.show();
        }
    }
}
