package de.eagleeye.dandd.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import de.eagleeye.dandd.R;

public class LoginActivity extends AppCompatActivity {
    public static final int ONLINE = 100;
    public static final int OFFLINE = 101;

    private EditText ip;
    private EditText user;
    private EditText password;

    private Button login;
    private Button offline;
    private Button settings;

//    private MySQLConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar =findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        ip = findViewById(R.id.login_ip);
        user = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);

        ip.setText(getPreferences(MODE_PRIVATE).getString("login_ip", ""));
        user.setText(getPreferences(MODE_PRIVATE).getString("login_user",""));

//        connection = MySQLConnection.getSQLConnection();
//        if(connection == null) connection = new MySQLConnection();
//
//        login = findViewById(R.id.login_btn_login);
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                connection.connect(ip.getText().toString(), "dandd", user.getText().toString(), password.getText().toString());
//            }
//        });

        offline = findViewById(R.id.login_btn_offline);
        offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(OFFLINE);
                finish();
            }
        });

        settings = findViewById(R.id.login_btn_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //connection.saveSQLConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(MySQLConnection.hasValidConnection()){
//            Snackbar.make(findViewById(R.id.login_bottom_controls), "Already Connected", BaseTransientBottomBar.LENGTH_LONG).show();
//            Handler h = new Handler();
//            h.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                }
//            }, 1500);
//        }
//        connection.setOnStateChangedListener(this);
    }

    @Override
    public void onBackPressed() {
        Snackbar.make(findViewById(R.id.login_bottom_controls), "Please Log in or use Offline Data!", BaseTransientBottomBar.LENGTH_LONG).show();
    }
}
