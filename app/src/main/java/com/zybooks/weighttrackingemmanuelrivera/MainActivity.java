package com.zybooks.weighttrackingemmanuelrivera;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.zybooks.weighttrackingemmanuelrivera.viewmodel.AuthViewModel;

public class MainActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        Button loginButton = findViewById(R.id.loginButton);
        Button signUpButton = findViewById(R.id.signUpButton);
        EditText usernameInput = findViewById(R.id.editTextText);
        EditText passwordInput = findViewById(R.id.editTextTextPassword);

        loginButton.setOnClickListener(v -> {
            authViewModel.handleLogin(usernameInput.getText().toString().trim(), passwordInput.getText().toString());
        });

        signUpButton.setOnClickListener(v -> {
            authViewModel.handleSignUp(usernameInput.getText().toString().trim(), passwordInput.getText().toString());
        });


        authViewModel.getAuthResult().observe(this, user -> {
            Intent intent = new Intent(MainActivity.this, WeightActivity.class);
            intent.putExtra("username", user.getUsername());
            intent.putExtra("userId", user.getUserId());
            startActivity(intent);
            finish();
        });

        authViewModel.getErrorState().observe(this, errorMessage -> {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        });
    }
}