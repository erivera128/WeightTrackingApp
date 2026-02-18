package com.zybooks.weighttrackingemmanuelrivera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private WeightTrackerDB dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new WeightTrackerDB(this);
        database = dbHelper.getWritableDatabase();

        Button loginButton = findViewById(R.id.loginButton);
        Button signUpButton = findViewById(R.id.signUpButton);
        EditText usernameInput = findViewById(R.id.editTextText);
        EditText passwordInput = findViewById(R.id.editTextTextPassword);


        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter a username and password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.verifyUser(username, password)) {
                long userId = dbHelper.getUserId(username);
                Intent intent = new Intent(MainActivity.this, WeightActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        signUpButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (username.isEmpty() || password.length() < 8) {
                Toast.makeText(this, "Enter a username and password with at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            long userId = dbHelper.createUser(username, password);

            if (userId == -1) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Account created. You can now log in.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy(){
        dbHelper.close();
        super.onDestroy();
    }

    private void sendGoalSms() {
        try {
            SmsManager smsManager = this.getSystemService(SmsManager.class);

            smsManager.sendTextMessage("1234567890", null, "Goal reached!!", null, null);
            Toast.makeText(this, "Goal sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Text failed", Toast.LENGTH_SHORT).show();
        }
    }
    public void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 101);
        } else {
            sendGoalSms();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendGoalSms();
            } else {
                Toast.makeText(this, "SMS notifications disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}