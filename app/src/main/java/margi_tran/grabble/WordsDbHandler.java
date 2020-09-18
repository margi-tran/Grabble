package margi_tran.grabble;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

/**
 *
    WordsDbHandler.java
 *
 *  This class extends ExternalDbOpenerHelper to deal with querying the words database.
 *
 *  @author Margi Tran */

public class WordsDbHandler extends ExternalDbOpenHelper {
    private static final String TAG = LoginActivity.class.getName();

    private static final String DB_NAME = "words.db";
    private static final String TABLE = "words";
    private static final String[] COLUMNS = {"_id", "word"};

    public WordsDbHandler(Context context) {
        super(context, DB_NAME);
    }

    public boolean isWordInDB(String word) {
        String selection = "word LIKE '" + word + "'";

        Cursor cursor = database.query(TABLE, COLUMNS, selection, null, null, null, null, null);
        if(cursor.moveToFirst()) { // the word exists in the database
            Log.i(TAG, "Word is in: " +
                    cursor.getString(cursor.getColumnIndex(COLUMNS[0])) + " " +
                    cursor.getString(cursor.getColumnIndex(COLUMNS[1])));
            return true;
        }
        cursor.close();
        Log.i(TAG, "Word: " + word + " does not exist in the db.");
        return false; // the word does not exist in the database
    }

    /**
     * This method returns a set of words that are similar to the given word.
     * A word is similar if:
     *  1) one letter is different from the given word
     *  2) two adjacent letters are different from the given word
     */
    public Set<String> getSimilarWords(String word) {
        Set<String> similarWords = new HashSet<>();

        // find words that are similar to the given word with one letter changed
        // e.g. saronic and caronic are similar to Aaronic
        for(int i = 0; i < 7; i++) {
            StringBuilder sb = new StringBuilder(word);
            sb.setCharAt(i, '_');
            String currentWord = sb.toString();
            String selection = "word LIKE '" + currentWord + "'";
            Cursor cursor = database.query(TABLE, COLUMNS, selection, null, null, null, null, null);
            while(cursor.moveToNext())
                similarWords.add(cursor.getString(cursor.getColumnIndex(COLUMNS[1])));
            cursor.close();
        }

        // find words that are similar to the given word with two adjacent letters changed
        // e.g. chronic and boronic are similar to aaronic
        for(int i = 0; i < 6; i++) {
            StringBuilder sb = new StringBuilder(word);
            sb.setCharAt(i, '_');
            sb.setCharAt(i+1, '_');
            String currentWord = sb.toString();
            String selection = "word LIKE '" + currentWord + "'";
            Cursor cursor = database.query(TABLE, COLUMNS, selection, null, null, null, null, null);
            while(cursor.moveToNext())
                similarWords.add(cursor.getString(cursor.getColumnIndex(COLUMNS[1])));
            cursor.close();
        }

        similarWords.remove(word);
        return similarWords;
    }
}
