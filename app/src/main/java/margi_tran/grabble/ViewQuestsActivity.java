package margi_tran.grabble;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
    ViewQuestsActivity.java
 *
 *  @author Margi Tran */

public class ViewQuestsActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();

    private TextView distanceProgressView, createWordsProgressView,
                distanceDescView, createWordsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quests);
        init();
    }

    public void init() {
        distanceDescView = (TextView) findViewById(R.id.distDesc);
        distanceProgressView = (TextView) findViewById(R.id.distProg);

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.FLOOR);

        double distanceTravelledForQuest = SharedPreferencesHelper.get_q_DistanceTravelled(this) / 1000;
        double distanceGoal = SharedPreferencesHelper.getDistanceGoal(this);
        distanceDescView.setText("Earn " +  QuestPoints.getPointsForDistanceQuest(distanceGoal) +
                " points for completing.");
        if(distanceTravelledForQuest >= distanceGoal) {
            distanceProgressView.setText("Completed");
            distanceProgressView.setTextColor(Color.GREEN);
            distanceProgressView.setTypeface(null, Typeface.BOLD);
        } else {
            // this variable is needed for adding a decimal point to the distanceTravelledForQuest
            // variable if it produces a whole number
            double d = Double.parseDouble(df.format(distanceTravelledForQuest) + "");
            distanceProgressView.setText(d + " / " + distanceGoal + " km");
        }


        createWordsView = (TextView) findViewById(R.id.wordsDesc);
        createWordsProgressView = (TextView) findViewById(R.id.wordsProg);
        int wordGoal = SharedPreferencesHelper.getWordGoal(this);
        int noWordsCreatedForQuest = SharedPreferencesHelper.get_q_NumbersOfWordsCreated(this);
        createWordsView.setText("Earn " +  QuestPoints.getPointsForWordsQuest(wordGoal) +
                " points for completing.");
        if(noWordsCreatedForQuest >= wordGoal) {
            createWordsProgressView.setText("Completed");
            createWordsProgressView.setTextColor(Color.GREEN);
            createWordsProgressView.setTypeface(null, Typeface.BOLD);
        } else {
            createWordsProgressView.setText(noWordsCreatedForQuest + " / " + wordGoal);
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
