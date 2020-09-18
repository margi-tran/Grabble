package margi_tran.grabble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
    ViewLeaderboardActivity.java
 *
 *  @author Margi Tran */

public class ViewLeaderboardActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();

    private static final String USERNAME_LABEL_PREFIX = "user";
    private static final String HIGHSCORES_LABEL_PREFIX = "points";
    Map<Integer, TextView> usernamesMapping;
    Map<Integer, TextView> highscoresMapping;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_leaderboard);
        mapUsernameTextViews();
        mapHighscoresTextViews();
        init();
    }

    private void mapHighscoresTextViews() {
        highscoresMapping = new HashMap<>();
        for(int i = 1; i <= 5; i++) {
            String idName = HIGHSCORES_LABEL_PREFIX + i;
            int id = getResources().getIdentifier(idName, "id", getPackageName());
            TextView textView = (TextView) findViewById(id);
            highscoresMapping.put(i, textView);
        }
    }

    private void mapUsernameTextViews() {
        usernamesMapping = new HashMap<>();
        for(int i = 1; i <= 5; i++) {
            String idName = USERNAME_LABEL_PREFIX + i;
            int id = getResources().getIdentifier(idName, "id", getPackageName());
            TextView textView = (TextView) findViewById(id);
            usernamesMapping.put(i, textView);
        }
    }

    public void init() {
        UsersDbHandler usersDbHandler = new UsersDbHandler(this);
        ArrayList<UserWithHighscore> usersWithHighscore = usersDbHandler.getUsersWithHighscore();
        usersDbHandler.close();

        for(int i = 0; i < 5; i++) {
            if(i < usersWithHighscore.size()) {
                String username = usersWithHighscore.get(i).getUsername();
                String highscore = usersWithHighscore.get(i).gethighscore();

                usernamesMapping.get(i + 1).setText(username);
                highscoresMapping.get(i + 1).setText(highscore + " PTS");
            }
        }

        TextView userHighscore = (TextView) findViewById(R.id.own_highscore);
        userHighscore.setText("Your highscore: " + SharedPreferencesHelper.getHighscore(this) + " PTS");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}