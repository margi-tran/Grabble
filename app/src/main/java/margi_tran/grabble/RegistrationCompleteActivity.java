package margi_tran.grabble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 *
    RegistrationCompleteActivity.java
 *
 *  @author Margi Tran */

public class RegistrationCompleteActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_complete);
        init();
    }

    public void init() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    public void goBackToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
