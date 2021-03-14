package edu.gatech.seclass.wordfind6300;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private EditText minutesInput;
    private EditText sizeInput;
    private EditText letterWeightInput;
    private Switch useDictionary;
    private Spinner letters;
    private StatStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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

        // Connect to persistent storage
        storage = new StatStorage(getApplicationContext());

        // Proceed to populating the view
        minutesInput = (EditText)findViewById(R.id.minutesInput);
        sizeInput = (EditText)findViewById(R.id.sizeInput);
        minutesInput.setText(String.valueOf(Settings.getSettings().getMinutes()));
        sizeInput.setText(String.valueOf(Settings.getSettings().getDimensions()));
        Button buttonExit = (Button) findViewById(R.id.button_exit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the current settings to the database
                Settings cs = Settings.getSettings();
                ArrayList<Integer> weights = new ArrayList<Integer>();
                for(int w : cs.getWeights()) {
                    weights.add(w);
                }
                Log.d("SETTINGS", "Saving settings with:" +
                        " dimensions=" + cs.getDimensions() +
                        " minutes=" + cs.getMinutes() +
                        " useDictionary=" + cs.getUseDictionary() +
                        " weights=" + weights);
                storage.setGameSettingsCurrentTask(cs.getDimensions(), cs.getMinutes(),
                        cs.getUseDictionary(), weights);

                // Finish activity
                finish();
            }
        });
        useDictionary = (Switch)findViewById(R.id.switchUseDictionary);
        useDictionary.setChecked(Settings.getSettings().getUseDictionary());

        useDictionary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.getSettings().setUseDictionary(isChecked);
            }
        });

        letterWeightInput = (EditText)findViewById(R.id.letterWeightInput);
        letterWeightInput.setText(String.valueOf(Settings.getSettings().getWeights()[0]));

        String[] arraySpinner = new String[] {
                "A", "B", "C", "D", "E", "F", "G", "E", "F",
                "G", "H", "I", "J", "K", "L", "M", "N", "O",
                "P", "Qu", "R", "S", "T", "U", "V", "W", "X",
                "Y", "Z"
        };
        letters = (Spinner) findViewById(R.id.letterInput);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        letters.setAdapter(adapter);

        letters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Log.d("Button", String.valueOf(position));
                letterWeightInput.setText(String.valueOf(Settings.getSettings().getWeights()[position])); // TODO: error handling
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        // Load "current" settings (if they exist)
        storage.getGameSettingsCurrentTask().observe(this, new Observer<GameSettings>() {
            @Override
            public void onChanged(@Nullable GameSettings settings) {
                if (settings != null) {
                    Log.d("SETTINGS", "Loading settings with: id=" + settings.id +
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
                    // Update view elements
                    minutesInput.setText(String.valueOf(cs.getMinutes()));
                    sizeInput.setText(String.valueOf(cs.getDimensions()));
                    useDictionary.setChecked(cs.getUseDictionary());
                    for (int i = 0; i < settings.weights.size(); i++) {
                        letterWeightInput.setText(String.valueOf(cs.getWeights()[i]));
                    }
                }
            }
        });
    }

    private int getArrayIndex(String[] arr, String toFind) {
        for(int i = 0; i < arr.length; i++){
            if(arr[i] == toFind){
                return i;
            }
        }
        return -1;
    }

    public void createDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.ok, null)
                .show();
    }

    public void onClick(View view)
    {
        boolean saveSuccess = false;
        switch (view.getId()) {
            case R.id.button_save_minutes:
                Log.d("Button", "save minutes button clicked");
                String minutes = minutesInput.getText().toString();
                saveSuccess = Settings.getSettings().setMinutes(Integer.parseInt(minutes));
                break;
            case R.id.button_save_size:
                Log.d("Button", "save size button clicked");
                String size = sizeInput.getText().toString();
                saveSuccess = Settings.getSettings().setDimensions(Integer.parseInt(size));
                break;
            case R.id.button_save_weight:
                Log.d("Button", "save weight button clicked");
                String letterWeight = letterWeightInput.getText().toString();
                String letter = letters.getSelectedItem().toString();
                String[] arraySpinner = new String[] {
                        "A", "B", "C", "D", "E", "F", "G", "E", "F",
                        "G", "H", "I", "J", "K", "L", "M", "N", "O",
                        "P", "Qu", "R", "S", "T", "U", "V", "W", "X",
                        "Y", "Z"
                };
                int position = getArrayIndex(arraySpinner, letter);
                saveSuccess = Settings.getSettings().setWeightByPosition(position, Integer.parseInt(letterWeight));
                break;
        }
        if (!saveSuccess) {
            createDialog("Error", "Invalid input");
        } else {
            createDialog("Success", "Successfully saved input");
        }
    }
}
