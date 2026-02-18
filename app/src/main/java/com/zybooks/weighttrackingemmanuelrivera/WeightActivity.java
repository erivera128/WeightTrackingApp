package com.zybooks.weighttrackingemmanuelrivera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeightActivity extends AppCompatActivity {

    private WeightTrackerDB dbHelper;
    private long userId;
    private static final int RECENT_WEIGHT_LIMIT = 10;

    private TextView currentWeightView;
    private TextView currentWeightGoalView;
    private WeightAdapter weightAdapter;

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
        currentWeightView = findViewById(R.id.currentWeight);
        currentWeightGoalView = findViewById(R.id.currentWeightGoal);
        if (username != null && !username.isEmpty()) {
            welcome.setText(getString(R.string.welcome_user, username));
        }

        setupRecyclerView();
        refreshDashboard();

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> showAddOptions());
    }

    private void setupRecyclerView() {
        RecyclerView weightEntries = findViewById(R.id.weightEntries);
        weightAdapter = new WeightAdapter();
        weightEntries.setLayoutManager(new LinearLayoutManager(this));
        weightEntries.setAdapter(weightAdapter);
    }

    private void refreshDashboard() {
        if (userId < 0) {
            currentWeightView.setText(R.string.current_weight_missing);
            currentWeightGoalView.setText(R.string.current_goal_missing);
            weightAdapter.setEntries(new ArrayList<>());
            return;
        }

        Float latestWeight = dbHelper.latestWeight(userId);
        Float latestGoal = dbHelper.getLatestGoal(userId);

        if (latestWeight == null) {
            currentWeightView.setText(R.string.current_weight_missing);
        } else {
            currentWeightView.setText(getString(
                    R.string.current_weight_value,
                    String.format(Locale.US, "%.1f", latestWeight)
            ));
        }

        if (latestGoal == null) {
            currentWeightGoalView.setText(R.string.current_goal_missing);
        } else {
            currentWeightGoalView.setText(getString(
                    R.string.current_goal_value,
                    String.format(Locale.US, "%.1f", latestGoal)
            ));
        }

        List<WeightTrackerDB.WeightEntry> recentWeights = dbHelper.getRecentWeight(userId, RECENT_WEIGHT_LIMIT);
        weightAdapter.setEntries(recentWeights);
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
                    refreshDashboard();
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

    private static class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> implements com.zybooks.weighttrackingemmanuelrivera.WeightAdapter {

        private final List<WeightTrackerDB.WeightEntry> entries = new ArrayList<>();

        public void setEntries(List<WeightTrackerDB.WeightEntry> newEntries) {
            entries.clear();
            entries.addAll(newEntries);
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