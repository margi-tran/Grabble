package margi_tran.grabble;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 *
    LoginActivity.java
 *
 *  @author Margi Tran */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();

    private boolean failedAttempt;

    private TextView loginMsgTextView;
    private EditText usernameField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    public void init() {
        failedAttempt = false;
        loginMsgTextView = (TextView) findViewById(R.id.loginMsgTextView);
        passwordField = (EditText) findViewById(R.id.passwordField);
        usernameField = (EditText) findViewById(R.id.usernameField);

        // instantly clear username + pw fields when the username field is tapped upon
        // a failed login
        usernameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(failedAttempt) {
                        failedAttempt = false;
                        loginMsgTextView.setText("");
                        usernameField.getText().clear();
                        passwordField.getText().clear();
                    }
                }
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    public void processLogin(View view) {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString();

        UsersDbHandler usersDbHandler = new UsersDbHandler(this);

        if(!username.equals("") && !password.equals("")
                && usersDbHandler.userCredentialsAreCorrect(username, password)) {

            SharedPreferencesHelper.setSharedPrefsForUser(this, username);

            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            finish();
        } else {
            failedAttempt = true;
            if(!username.equals("") && !password.equals("")) {
                loginMsgTextView.setText("Incorrect login information. Please try again.");
                loginMsgTextView.setTextColor(Color.RED);
            }
        }
        usersDbHandler.close();
    }

    public void startRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}