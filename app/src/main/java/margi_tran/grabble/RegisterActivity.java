package margi_tran.grabble;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
    RegisterActivity.java
 *
 *  @author Margi Tran */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();

    private boolean failedAttempt;

    private TextView registerMsgTextView;
    private EditText usernameField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    public void init() {
        failedAttempt = false;
        registerMsgTextView = (TextView) findViewById(R.id.registerErrMsgTextView);
        passwordField = (EditText) findViewById(R.id.passwordField);
        usernameField = (EditText) findViewById(R.id.usernameField);

        usernameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(failedAttempt) {
                        failedAttempt = false;
                        registerMsgTextView.setText("");
                        usernameField.getText().clear();
                        passwordField.getText().clear();
                    }
                }
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void processDetails(View view) {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString();

        UsersDbHandler usersDbHandler = new UsersDbHandler(this);

        if(!username.equals("") && !password.equals("")
                && usersDbHandler.usernameIsUnique(username)) {
            usersDbHandler.registerUser(username, password);
            usersDbHandler.close();
            Intent intent = new Intent(this, RegistrationCompleteActivity.class);
            startActivity(intent);
            finish();
        } else {
            failedAttempt = true;
            registerMsgTextView.setText("That username is not available. Please try again.");
            registerMsgTextView.setTextColor(Color.RED);
        }
    }
}
