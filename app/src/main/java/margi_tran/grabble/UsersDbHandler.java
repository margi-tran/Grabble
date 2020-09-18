package margi_tran.grabble;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 *
    UsersDbHandler.java
 *
 *  This class extends ExternalDbOpenerHelper to deal with querying the users database.
 *
 *  @author Margi Tran */

public class UsersDbHandler extends ExternalDbOpenHelper {
    private static final String TAG = LoginActivity.class.getName();

    private static final String DB_NAME = "users.db";
    private static final String TABLE = "users";
    private static final String[] COLUMNS =
            {"_id", "username", "password", "highscore", "no_letters_collected",
            "no_of_words_created", "total_distance_travelled", "date_last_logged",
            "q_distance_travelled", "q_words_created", "q_distance_goal", "q_words_goal"};

    public UsersDbHandler(Context context) {
        super(context, DB_NAME);
    }

    public boolean userCredentialsAreCorrect(String username, String password) {
        String selection = "username=?";
        String[] selectionArgs = { username };
        Cursor cursor = database.query(TABLE,
                new String[] {"password"}, selection, selectionArgs, null, null, null);
        Log.v(TAG, DatabaseUtils.dumpCursorToString(cursor));

        if(cursor.moveToFirst()) {
            String passwordInDb = cursor.getString(0);
            cursor.close();
            if(password.equals(passwordInDb))
                return true;
            return false;
        }
        else {
            return false;
        }
    }

    public ArrayList<String> getUserSharedPrefsData(String username) {
        ArrayList<String> dataRow = new ArrayList<>();
        String selection = "username=?";
        String[] selectionArgs = { username };
        Cursor cursor = database.query(TABLE, COLUMNS, selection, selectionArgs, null, null, null);
        Log.v(TAG, DatabaseUtils.dumpCursorToString(cursor));

        cursor.moveToFirst();
        for(int i = 1+2; i < COLUMNS.length; i++) // add 2 to i to ignore the columns: _id and username
            dataRow.add(cursor.getString(i));
        return dataRow;
    }

    public void updateUserSharedPrefs(String username, ArrayList<String> data) {
        String sql = "UPDATE users SET" + " "
                        + "highscore=" + data.get(0) + ", "
                        + "no_letters_collected=" + data.get(1) + ", "
                        + "no_of_words_created=" + data.get(2) + ", "
                        + "total_distance_travelled='" + data.get(3) + "', "
                        + "date_last_logged='" + data.get(4) + "', "
                        + "q_distance_travelled='" + data.get(5) + "', "
                        + "q_words_created=" + data.get(6) + ", "
                        + "q_distance_goal=" + data.get(7) + ", "
                        + "q_words_goal=" + data.get(8) + " "
                        + "WHERE username='" + username + "';";
        database.execSQL(sql);
    }

    public void updateUserSharedPrefsToDb(Context context) {
        ArrayList<String> data = new ArrayList<>();
        data.add(SharedPreferencesHelper.getHighscore(context) + "");
        data.add(SharedPreferencesHelper.getNumberOfLettersCollected(context) + "");
        data.add(SharedPreferencesHelper.getNumbersOfWordsCreated(context) + "");
        data.add(SharedPreferencesHelper.getDistanceTravelled(context) + "");
        data.add(SharedPreferencesHelper.getLastLoggedInDate(context) + "");
        data.add(SharedPreferencesHelper.get_q_DistanceTravelled(context) + "");
        data.add(SharedPreferencesHelper.get_q_NumbersOfWordsCreated(context) + "");
        data.add(SharedPreferencesHelper.getDistanceGoal(context) + "");
        data.add(SharedPreferencesHelper.getWordGoal(context) + "");
        String username = SharedPreferencesHelper.getUsername(context);

        String sql = "UPDATE users SET" + " "
                + "highscore=" + data.get(0) + ", "
                + "no_letters_collected=" + data.get(1) + ", "
                + "no_of_words_created=" + data.get(2) + ", "
                + "total_distance_travelled='" + data.get(3) + "', "
                + "date_last_logged='" + data.get(4) + "', "
                + "q_distance_travelled='" + data.get(5) + "', "
                + "q_words_created=" + data.get(6) + ", "
                + "q_distance_goal=" + data.get(7) + ", "
                + "q_words_goal=" + data.get(8) + " "
                + "WHERE username='" + username + "';";
        database.execSQL(sql);
    }


    public ArrayList<UserWithHighscore> getUsersWithHighscore() {
        ArrayList<UserWithHighscore> usersWithHighscore = new ArrayList<>();

        Cursor cursor = database.query(TABLE,
                new String[] {"username", "highscore"}, null, null, null, null, "highscore DESC", null);

        while(cursor.moveToNext())
            usersWithHighscore.add(new UserWithHighscore(cursor.getString(0), cursor.getString(1)));
        return usersWithHighscore;
    }


    public boolean usernameIsUnique(String username) {
        String selection = "username=?";
        String[] selectionArgs = { username };
        Cursor cursor = database.query(TABLE,
                new String[] {"username"}, selection, selectionArgs, null, null, null);
        Log.v(TAG, DatabaseUtils.dumpCursorToString(cursor));

        if(cursor.moveToFirst())
            return false;
        else
            return true;
    }

    public void registerUser(String username, String password) {
        String sql = "INSERT INTO users "
                + "(username,password)"
                + "VALUES('" + username + "','" + password + "');";
        database.execSQL(sql);
    }
}

