package margi_tran.grabble;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 *
    SharedPreferencesHelper.java
 *
 *  This is a helper class for retrieving and updating the shared pref values.
 *
 *  @author Margi Tran */

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "GrabblePrefs";
    private static final String USERNAME = "username";
    private static final String HIGHSCORE = "highscore";
    private static final String NUMBER_OF_LETTERS_COLLECTED = "number_of_letters_collected";
    private static final String NUMBER_OF_WORDS_CREATED = "number_of_words_created";
    private static final String TOTAL_DISTANCE_TRAVELLED = "total_distance_travelled";

    // the following keys are related to quests
    private static final String DATE_LAST_LOGGED = "date_last_logged";
    private static final String Q_DISTANCE_TRAVELLED = "q_distance_travelled";
    private static final String Q_WORDS_CREATED = "q_words_created";
    private static final String Q_DISTANCE_GOAL = "q_distance_goal";
    private static final String Q_WORDS_GOALS = "q_words_goal";

    public static void setUsername(Context context, String username) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USERNAME, username);
        editor.commit();
    }

    public static String getUsername(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return settings.getString(USERNAME, null);
    }

    public static void addToHighscore(Context context, int score) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        int prevScore = settings.getInt(HIGHSCORE, 0);
        editor.putInt(HIGHSCORE, prevScore + score);
        editor.commit();
    }

    public static int getHighscore(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return settings.getInt(HIGHSCORE, 0);
    }

    public static void addToNumberOfWordsCreated(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        int prev = settings.getInt(NUMBER_OF_WORDS_CREATED, 0);
        editor.putInt(NUMBER_OF_WORDS_CREATED, prev + 1);
        editor.commit();
    }

    public static int getNumbersOfWordsCreated(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return settings.getInt(NUMBER_OF_WORDS_CREATED, 0);
    }

    public static void addToNumberOfLettersCollected(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        int prev = settings.getInt(NUMBER_OF_LETTERS_COLLECTED, 0);
        editor.putInt(NUMBER_OF_LETTERS_COLLECTED, prev + 1);
        editor.commit();
    }

    public static int getNumberOfLettersCollected(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return settings.getInt(NUMBER_OF_LETTERS_COLLECTED, 0);
    }

    /**
     * @param context
     * @param dist This is in meters.
     */
    public static void addToTotalDistancedTravelled(Context context, double dist) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        double prev = getDistanceTravelled(context);
        double addedTotal = prev + dist;
        editor.putString(TOTAL_DISTANCE_TRAVELLED, addedTotal + "");
        editor.commit();
    }

    /**
     * The total distance travelled returned is in meters.
     */
    public static double getDistanceTravelled(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return Double.parseDouble(settings.getString(TOTAL_DISTANCE_TRAVELLED, "0"));
    }

    public static void setDate(Context context, String day, String month) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        String dateString = day + "," + month;
        editor.putString(DATE_LAST_LOGGED, dateString);
        editor.commit();
    }

    /**
     * The format of the string returned is
     *      day,month
     * where day is an integer in [1..31] and month is an integer in [1..12]
     */
    public static String getLastLoggedInDate(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return settings.getString(DATE_LAST_LOGGED, "0,0");
    }

    /**
     * @param context
     * @param dist This is in meters.
     */
    public static void addTo_q_distanceTravelled(Context context, double dist) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        double prev = get_q_DistanceTravelled(context);
        double addedTotal = prev + dist;
        editor.putString(Q_DISTANCE_TRAVELLED, addedTotal + "");
        editor.commit();
    }

    public static void set_q_distanceTravelled(Context context, double dist) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Q_DISTANCE_TRAVELLED, dist + "");
        editor.commit();
    }

    /**
     * The distance travelled returned is in meters.
     */
    public static double get_q_DistanceTravelled(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return Double.parseDouble(settings.getString(Q_DISTANCE_TRAVELLED, "0"));
    }

    /**
     * @param context
     * @param goal The goal distance is in km.
     */
    public static void setDistanceGoal(Context context, String goal) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Q_DISTANCE_GOAL, goal);
        editor.commit();
    }

    /**
     * The distance returned is in km.
     */
    public static double getDistanceGoal(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return Double.parseDouble(settings.getString(Q_DISTANCE_GOAL, "0"));
    }

    public static void addTo_q_NumberOfWordsCreated(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        int prev = settings.getInt(Q_WORDS_CREATED, 0);
        editor.putInt(Q_WORDS_CREATED, prev + 1);
        editor.commit();
    }

    public static int get_q_NumbersOfWordsCreated(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return settings.getInt(Q_WORDS_CREATED, 0);
    }

    public static void set_q_NumbersOfWordsCreated(Context context, int goal) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Q_WORDS_CREATED, goal);
        editor.commit();
    }

    public static int getWordGoal(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return settings.getInt(Q_WORDS_GOALS, 0);
    }

    public static void setWordGoal(Context context, int goal) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Q_WORDS_GOALS, goal);
        editor.commit();
    }

    public static void setSharedPrefsForUser(Context context, String username) {
        UsersDbHandler usersDbHandler = new UsersDbHandler(context);
        ArrayList<String> data = usersDbHandler.getUserSharedPrefsData(username);

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(HIGHSCORE, Integer.parseInt(data.get(0)));
        editor.putInt(NUMBER_OF_LETTERS_COLLECTED, Integer.parseInt(data.get(1)));
        editor.putInt(NUMBER_OF_WORDS_CREATED, Integer.parseInt(data.get(2)));
        editor.putString(TOTAL_DISTANCE_TRAVELLED, data.get(3));
        editor.putString(DATE_LAST_LOGGED, data.get(4));
        editor.putString(Q_DISTANCE_TRAVELLED, data.get(5));
        editor.putInt(Q_WORDS_CREATED, Integer.parseInt(data.get(6)));
        editor.putString(Q_DISTANCE_GOAL, data.get(7));
        editor.putInt(Q_WORDS_GOALS, Integer.parseInt(data.get(8)));
        editor.commit();

        SharedPreferencesHelper.setUsername(context, username);
    }
}
