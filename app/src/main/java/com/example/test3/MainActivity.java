package com.example.test3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private EditText etName;
    private Button btnEnter;
    private Button btnReset;
    private Button fullreset;

    private SharedPreferences sharedPreferences;
    private TaskStorage taskStorage;

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_NAME = "user_name";
    private static final String ADMIN_PASSWORD = "xZ67";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.greeting);
        etName = findViewById(R.id.name);
        btnEnter = findViewById(R.id.login);
        btnReset = findViewById(R.id.reset);
        fullreset = findViewById(R.id.fullreset);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        taskStorage = new TaskStorage(this);

        etName.setText("");

        // Restore welcome message if a user was previously saved
        String savedName = sharedPreferences.getString(KEY_NAME, "");
        if (!savedName.isEmpty()) {
            tvWelcome.setText("Welcome back, " + savedName + "!");
        } else {
            tvWelcome.setText("Welcome");
        }

        // Login Handler
        btnEnter.setOnClickListener(v -> {
            String inputName = etName.getText().toString().trim();

            if (inputName.length() < 2) {
                etName.setError("Name must be at least 2 characters long");
                Toast.makeText(this, "Name must be at least 2 characters long", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save user session
            sharedPreferences.edit().putString(KEY_NAME, inputName).apply();

            tvWelcome.setText("Welcome, " + inputName + "!");
            btnEnter.setEnabled(false);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                btnEnter.setEnabled(true);
                startActivity(new Intent(this, TasksActivity.class));
            }, 500);
        });

        // Single-User Reset Handler
        btnReset.setOnClickListener(v -> {
            String inputName = etName.getText().toString().trim();

            if (inputName.isEmpty()) {
                Toast.makeText(this, "Please enter or keep a name to reset", Toast.LENGTH_SHORT).show();
                return;
            }

            showResetConfirmationDialog(inputName);
        });

        // Factory Reset Handler
        fullreset.setOnLongClickListener(v -> {
            showFullResetPasswordDialog();
            return true;
        });
    }

    private void showResetConfirmationDialog(String inputName) {
        new AlertDialog.Builder(this)
                .setTitle("Reset Data")
                .setMessage("Are you sure you want to delete all data for '" + inputName + "'?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    taskStorage.clearUserData(inputName);

                    String savedUser = sharedPreferences.getString(KEY_NAME, "");
                    if (savedUser.equalsIgnoreCase(inputName)) {
                        sharedPreferences.edit().remove(KEY_NAME).apply();
                    }

                    etName.setText("");
                    tvWelcome.setText("Welcome");
                    Toast.makeText(this, "All data reset for " + inputName, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showFullResetPasswordDialog() {
        final EditText inputPassword = new EditText(this);
        inputPassword.setHint("Enter admin password");
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("Factory Reset App")
                .setMessage("Enter password to erase all data for ALL users:")
                .setView(inputPassword)
                .setPositiveButton("Wipe All Data", (dialog, which) -> {
                    String enteredPassword = inputPassword.getText().toString().trim();

                    if (enteredPassword.equals(ADMIN_PASSWORD)) {
                        //Clear both SharedPreferences files
                        getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE).edit().clear().apply();
                        sharedPreferences.edit().clear().apply();

                        etName.setText("");
                        tvWelcome.setText("Welcome");

                        Toast.makeText(this, "All app data wiped successfully!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Incorrect password! Reset cancelled.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}