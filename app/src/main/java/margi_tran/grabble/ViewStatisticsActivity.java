package margi_tran.grabble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
    ViewStatisticsActivity.java
 *
 *  @author Margi Tran */

public class ViewStatisticsActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();

    private TextView lettersCollectedView, wordsCreatedView, distanceTravelledView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_statistics);
        init();
    }

    public void init() {
        lettersCollectedView = (TextView) findViewById(R.id.desc1);
        wordsCreatedView = (TextView) findViewById(R.id.desc2);
        distanceTravelledView = (TextView) findViewById(R.id.desc3);

        double totalDistTravelledInKM = SharedPreferencesHelper.getDistanceTravelled(this) / 1000;

        String lettersCollectedText = SharedPreferencesHelper.getNumberOfLettersCollected(this) + "";
        String wordsCreatedText = SharedPreferencesHelper.getNumbersOfWordsCreated(this) + "";

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.FLOOR);
        String distanceTravelledText = df.format(totalDistTravelledInKM) + " km";

        lettersCollectedView.setText(lettersCollectedText);
        wordsCreatedView.setText(wordsCreatedText);
        distanceTravelledView.setText(distanceTravelledText);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}