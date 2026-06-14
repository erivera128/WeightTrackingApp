package com.zybooks.weighttrackingemmanuelrivera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zybooks.weighttrackingemmanuelrivera.viewmodel.WeightViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeightActivity extends AppCompatActivity {

    private WeightViewModel weightViewModel;
    private long userId;
    private TextView currentWeightView;
    private TextView currentWeightGoalView;
    private WeightAdapter weightAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        weightViewModel = new ViewModelProvider(this).get(WeightViewModel.class);

        userId = getIntent().getLongExtra("userId", -1);
        String username = getIntent().getStringExtra("username");

        TextView welcome = findViewById(R.id.welcome);
        currentWeightView = findViewById(R.id.currentWeight);
        currentWeightGoalView = findViewById(R.id.currentWeightGoal);

        if (username != null && !username.isEmpty()) {
            welcome.setText(getString(R.string.welcome_user, username));
        }

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        Button smsSubmitButton = findViewById(R.id.cellSubmit);
        smsSubmitButton.setOnClickListener(v -> onButtonClick(v));

        setupRecyclerView();
        setupObservers();

        weightViewModel.loadDashboardData(userId);

        fab.setOnClickListener(v -> showAddOptions());
    }

    private void setupRecyclerView() {
        RecyclerView weightEntries = findViewById(R.id.weightEntries);
        weightAdapter = new WeightAdapter();
        weightEntries.setLayoutManager(new LinearLayoutManager(this));
        weightEntries.setAdapter(weightAdapter);
    }

    private void setupObservers() {
        weightViewModel.getCurrentWeight().observe(this, weight -> {
            if (weight == null) {
                currentWeightView.setText(R.string.current_weight_missing);
            } else {
                currentWeightView.setText(getString(
                        R.string.current_weight_value,
                        String.format(Locale.US, "%.1f", weight)
                ));
            }
        });

        weightViewModel.getCurrentGoal().observe(this, goal -> {
            if (goal == null) {
                currentWeightGoalView.setText(R.string.current_goal_missing);
            } else {
                currentWeightGoalView.setText(getString(
                        R.string.current_goal_value,
                        String.format(Locale.US, "%.1f", goal)
                ));
            }
        });

        weightViewModel.getRecentWeights().observe(this, entries -> {
            weightAdapter.setEntries(entries != null ? entries : new ArrayList<>());
        });

        weightViewModel.getStatusMessage().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void showAddOptions() {
        final String[] options = {
                getString(R.string.add_weight_option),
                getString(R.string.add_goal_option)
        };

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_new_weight_or_goal)
                .setItems(options, (dialog, which) -> showEntryBottomSheet(which == 0))
                .show();
    }

    private void showEntryBottomSheet(boolean isWeight) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.fab_button_weight, null);
        bottomSheet.setContentView(view);

        TextView entryTitle = view.findViewById(R.id.entryTitle);
        EditText valueInput = view.findViewById(R.id.weightInput);
        Button saveButton = view.findViewById(R.id.btnSaveWeight);

        entryTitle.setText(isWeight ? R.string.new_weight : R.string.new_goal);
        valueInput.setHint(isWeight ? R.string.enter_weight : R.string.enter_goal);

        saveButton.setOnClickListener(v -> {
            String rawValue = valueInput.getText().toString().trim();
            if (rawValue.isEmpty()) {
                Toast.makeText(this, R.string.enter_valid_number, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float value = Float.parseFloat(rawValue);
                weightViewModel.addWeightOrGoal(userId, isWeight, value);
                bottomSheet.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.enter_valid_number, Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheet.show();
    }

    public void onButtonClick(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        } else {
            sendSMS();
        }
    }

    private void sendSMS() {
        EditText phoneText = findViewById(R.id.editTextText2);
        String phoneNumber = phoneText.getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = "Goal reached! SMS sent to: " + phoneNumber;

        try {
            android.telephony.SmsManager smsManager = this.getSystemService(android.telephony.SmsManager.class);
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS Sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

        private final List<WeightTrackerDB.WeightEntry> entries = new ArrayList<>();
        private final List<Float> movingAverages = new ArrayList<>();

        public void setEntries(List<WeightTrackerDB.WeightEntry> newEntries) {
            entries.clear();
            entries.addAll(newEntries);

            movingAverages.clear();
            movingAverages.addAll(WeightMathUtils.calculate7DayMovingAverage(entries));

            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_weight, parent, false);
            return new WeightViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
            WeightTrackerDB.WeightEntry entry = entries.get(position);
            holder.weightValue.setText(String.format(Locale.US, "%.1f lbs", entry.getValue()));
            holder.weightDate.setText(entry.getDate());

            if (position < movingAverages.size()) {
                float avg = movingAverages.get(position);
                androidx.appcompat.widget.TooltipCompat.setTooltipText(holder.itemView, String.format(Locale.US, "7-Day Trend Avg: %.1f lbs", avg));

                holder.weightDate.setText(String.format(Locale.US, "%s | Trend: %.1f", entry.getDate(), avg));
            }
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        static class WeightViewHolder extends RecyclerView.ViewHolder {
            private final TextView weightValue;
            private final TextView weightDate;

            public WeightViewHolder(@NonNull View itemView) {
                super(itemView);
                weightValue = itemView.findViewById(R.id.weightValue);
                weightDate = itemView.findViewById(R.id.weightDate);
            }
        }
    }
}