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

import com.google.android.material.bottomsheet.BottomSheetDialog;

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

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WeightActivity.class);
            startActivity(intent);

            finish();
        });
    }

    @Override
    protected  void onDestroy(){
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 101);
        }
        else {
            sendGoalSms();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 101) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendGoalSms();
            }
            else {
                Toast.makeText(this, "SMS notifications disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fabButtonAdd() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);

        View view = getLayoutInflater().inflate(R.layout.fab_button_weight, null);
        bottomSheet.setContentView(view);

        EditText weightEntry = view.findViewById(R.id.weightInput);
        Button saveButton = view.findViewById(R.id.btnSaveWeight);

        saveButton.setOnClickListener(v -> {
            String weight = weightEntry.getText().toString();
            if(!weight.isEmpty()) {
                float finalWeight = Float.parseFloat(weight);
//                stopping here for now, a bit confused.
            }
        });

    }
}