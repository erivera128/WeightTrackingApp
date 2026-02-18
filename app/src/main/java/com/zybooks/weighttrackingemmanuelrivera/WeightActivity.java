package com.zybooks.weighttrackingemmanuelrivera;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WeightActivity extends AppCompatActivity {

    private WeightTrackerDB dbHelper;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weight);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new WeightTrackerDB(this);
        userId = getIntent().getLongExtra("userId", -1);
        String username = getIntent().getStringExtra("username");

        TextView welcome = findViewById(R.id.welcome);
        if (username != null && !username.isEmpty()) {
            welcome.setText(getString(R.string.welcome_user, username));
        }

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> showAddOptions());
    }

    private void showAddOptions() {
        final String[] options = {
                getString(R.string.add_weight_option),
                getString(R.string.add_goal_option)
        };

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_new_weight_or_goal)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEntryBottomSheet(true);
                    } else {
                        showEntryBottomSheet(false);
                    }
                })
                .show();
    }

    private void showEntryBottomSheet(boolean isWeight) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.fab_button_weight, null);
        bottomSheet.setContentView(view);

        TextView entryTitle = view.findViewById(R.id.entryTitle);
        EditText valueInput = view.findViewById(R.id.weightInput);
        Button saveButton = view.findViewById(R.id.btnSaveWeight);

        if (isWeight) {
            entryTitle.setText(R.string.new_weight);
            valueInput.setHint(R.string.enter_weight);
        } else {
            entryTitle.setText(R.string.new_goal);
            valueInput.setHint(R.string.enter_goal);
        }

        saveButton.setOnClickListener(v -> {
            String rawValue = valueInput.getText().toString().trim();
            if (rawValue.isEmpty()) {
                Toast.makeText(this, R.string.enter_valid_number, Toast.LENGTH_SHORT).show();
                return;
            }

            if (userId < 0) {
                Toast.makeText(this, R.string.user_not_found, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float value = Float.parseFloat(rawValue);
                long insertId = isWeight
                        ? dbHelper.insertWeight(userId, value)
                        : dbHelper.insertGoal(userId, value);

                if (insertId == -1) {
                    Toast.makeText(this, R.string.save_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                            this,
                            isWeight ? R.string.weight_saved : R.string.goal_saved,
                            Toast.LENGTH_SHORT
                    ).show();
                    bottomSheet.dismiss();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.enter_valid_number, Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheet.show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}