package edu.gatech.seclass.wordfind6300;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/*
  TypeConverters for converting to/from ArrayList to Room DB field
 */
class Converters {
    @TypeConverter
    public static ArrayList<Integer> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromArrayList(ArrayList<Integer> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}

/*
  Database table for Word Statistics
 */
@Entity
class WordStat {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "word")
    public String word;

    @NonNull
    @ColumnInfo(name = "frequency")
    public int frequency;
}
@Dao
interface WordStatDao {
    @Query("SELECT * FROM WordStat ORDER BY frequency desc")
    LiveData<List<WordStat>> getAll();

    @Query("SELECT * FROM WordStat ORDER BY frequency desc")
    List<WordStat> getAllDirect();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WordStat wordStat);

    @Delete
    void delete(WordStat wordStat);
}


/*
  Database table for Game/Score Statistics
 */
@Entity
class GameStat {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "score")
    public int score;

    @ColumnInfo(name = "resets")
    public int resets;

    @ColumnInfo(name = "words")
    public int words;

    @ColumnInfo(name = "dimensions")
    public int dimensions;

    @ColumnInfo(name = "minutes")
    public int minutes;

    //@ColumnInfo(name = "weights")
    //public int[] weights;

    @ColumnInfo(name = "highestWord")
    public String highestWord;

    @ColumnInfo(name = "highestWordScore")
    public int highestWordScore;
}
@Dao
interface GameStatDao {
    @Query("SELECT * FROM GameStat ORDER BY score desc")
    LiveData<List<GameStat>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GameStat gameStat);

    @Query("SELECT * FROM GameStat WHERE id=:id LIMIT 1")
    LiveData<GameStat> getGameByID(int id);

    @Delete
    void delete(GameStat gameStat);
}

/*
  Database table for Settings
 */
@Entity
class GameSettings {

    // Game settings with id=1 represents as the "current" game settings.
    // This special row is queried for to identify what the "current" settings are.
    // All other settings are given a different, auto-generated id.
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "dimensions")
    public int dimensions;

    @ColumnInfo(name = "minutes")
    public int minutes;

    @ColumnInfo(name = "useDictionary")
    public boolean useDictionary;

    @ColumnInfo(name = "weights")
    public ArrayList<Integer> weights;
}
@Dao
interface GameSettingsDao {
    @Query("SELECT * FROM GameSettings")
    LiveData<List<GameSettings>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GameSettings gameSettings);

    @Query("SELECT * FROM GameSettings WHERE id=:id LIMIT 1")
    LiveData<GameSettings> getSettingsByID(int id);

    @Query("SELECT * FROM GameSettings WHERE id=1 LIMIT 1")
    LiveData<GameSettings> getSettingsCurrent();

    @Delete
    void delete(GameSettings gameSettings);
}

/*
  The local database itself
 */
@Database(entities = {WordStat.class, GameStat.class,GameSettings.class}, version = 4, exportSchema = false)
@TypeConverters({Converters.class})
abstract class StatDatabase extends RoomDatabase {
    public abstract WordStatDao wordStatDao();
    public abstract GameStatDao gameStatDao();
    public abstract GameSettingsDao gameSettingsDao();
}


/*
  Public project class for accessing the database as an intermediary
 */
public class StatStorage {

    // Private access to the true database
    private String DB_NAME = "statdb";
    private StatDatabase statdb;

    // Establish a database connection using the context
    public StatStorage(Context context) {
        statdb = Room.databaseBuilder(context, StatDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    // Get a complete list of words and their frequencies, ordered by frequency
    public LiveData<List<WordStat>> getWordStatsTask() {
        Log.d("STORAGE", "Getting list of Word Stats");
        return statdb.wordStatDao().getAll();
    }

    // Get a complete list of game/score stats, ordered by final score
    public LiveData<List<GameStat>> getGameStatsTask() {
        Log.d("STORAGE", "Getting list of Game Stats");
        return statdb.gameStatDao().getAll();
    }

    // Get a particular game stat by ID
    public LiveData<GameStat> getGameByIDTask(int id) {
        return statdb.gameStatDao().getGameByID(id);
    }

    // Insert a word and frequency, overwriting the older value
    public void insertWordStatTask(String word, int frequency) {
        Log.d("STORAGE", "Inserting new Word Stat: word="+word+" with frequency="+frequency);
        final WordStat stat = new WordStat();
        stat.word = word;
        stat.frequency = frequency;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                statdb.wordStatDao().insert(stat);
                return null;
            }
        }.execute();
    }

    public void incrementWordStatTask(String word) {
        Log.d("STORAGE", "Incrementing Word Stat: word="+word);
        final WordStat stat = new WordStat();
        stat.word = word;
        stat.frequency = 1;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<WordStat> stats = statdb.wordStatDao().getAllDirect();
                for (WordStat otherStat : stats ) {
                    if ( otherStat.word.equalsIgnoreCase(stat.word)) {
                        stat.frequency = otherStat.frequency + 1;
                        break;
                    }
                }
                statdb.wordStatDao().insert(stat);
                return null;
            }
        }.execute();
    }

    // Insert a new game stat row, (will overwrite, but shouldn't happen currently)
    public void insertGameStatTask(int score, int resets, int words, int dimensions,
                                   int minutes, String highestWord,
                                   int highestWordScore) {
        Log.d("STORAGE", "Inserting new Game Stat: " +
                "score='"+score+"', resets='"+resets+"', words='"+words+"', dimensions='"+dimensions+"', " +
                "minutes='"+minutes+"', highestWord='"+highestWord+"', highestWordScore='"+highestWordScore+"'");
        final GameStat stat = new GameStat();
        stat.score = score;
        stat.resets = resets;
        stat.words = words;
        stat.dimensions = dimensions;
        stat.minutes = minutes;
        stat.highestWord = highestWord;
        stat.highestWordScore = highestWordScore;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                statdb.gameStatDao().insert(stat);
                return null;
            }
        }.execute();
    }

    // Game settings-oriented methods below...

    // Save the current settings to the database (only ever 1 "current" settings)
    public void setGameSettingsCurrentTask(int dimensions, int minutes, boolean useDictionary,
                                           ArrayList<Integer> weights) {
        Log.d("STORAGE", "Setting new current game settings to: " +
                "dimensions='"+dimensions+"', minutes='"+minutes+"', weights='"+weights);
        final GameSettings settings = new GameSettings();
        settings.id = 1;
        settings.dimensions = dimensions;
        settings.minutes = minutes;
        settings.useDictionary = useDictionary;
        settings.weights = weights;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                statdb.gameSettingsDao().insert(settings);
                return null;
            }
        }.execute();
    }

    // Insert a new game settings row (likely linked to a past GameStat record)
    public void insertGameSettingsTask(int dimensions, int minutes, boolean useDictionary,
                                       ArrayList<Integer> weights) {
        Log.d("STORAGE", "Inserting new game settings to: " +
                "dimensions="+dimensions+", minutes="+minutes+", weights="+weights);
        final GameSettings settings = new GameSettings();
        settings.dimensions = dimensions;
        settings.minutes = minutes;
        settings.useDictionary = useDictionary;
        settings.weights = weights;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                statdb.gameSettingsDao().insert(settings);
                return null;
            }
        }.execute();
    }

    // Get the current game settings, if it exists. If none exists, return null.
    public LiveData<GameSettings> getGameSettingsCurrentTask() {
        return statdb.gameSettingsDao().getSettingsCurrent();
    }

    // Get a particular game stat by ID
    public LiveData<GameSettings> getGameSettingsByIDTask(int id) {
        return statdb.gameSettingsDao().getSettingsByID(id);
    }
}
