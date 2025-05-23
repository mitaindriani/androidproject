package com.example.absensi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextNisn;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    // --- ADMIN CREDENTIALS ---
    private static final String ADMIN_NISN = "admin"; // Example admin NISN
    private static final String ADMIN_PASSWORD = "admin"; // Example admin password
    // -------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        editTextNisn = findViewById(R.id.editTextNisn);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonMasuk);
        textViewRegister = findViewById(R.id.textViewRegister);

        // Initialize DatabaseHelper and SharedPreferences
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        // Check if user is already logged in (commented out as per your previous request)
        /*
        if (sharedPreferences.contains("nisn")) {
            // User is already logged in, go directly to HomeActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Finish LoginActivity so user can't go back
            return; // Exit onCreate
        }
        */

        // Set listener for Login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nisn = editTextNisn.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (nisn.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "NISN dan Password harus diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                // --- ADMIN LOGIN CHECK (MODIFIED) ---
                if (nisn.equals(ADMIN_NISN) && password.equals(ADMIN_PASSWORD)) {
                    Toast.makeText(LoginActivity.this, "Login Admin Berhasil! Memuat Master Jadwal...", Toast.LENGTH_SHORT).show();
                    // Navigate to MasterJadwalActivity
                    Intent intent = new Intent(LoginActivity.this, MasterJadwalActivity.class); // Changed here
                    startActivity(intent);
                    finish(); // Finish LoginActivity
                    return; // Exit onClick to prevent further checks
                }
                // ------------------------------------

                // Attempt to get the user from the database (for regular users)
                DatabaseHelper.User user = dbHelper.getUser(nisn, password);

                if (user != null) {
                    Toast.makeText(LoginActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();

                    // Store all user data in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("nama", user.getNama());
                    editor.putString("nisn", user.getNisn());
                    editor.putString("kelas", user.getKelas());
                    editor.putString("jurusan", user.getJurusan());
                    editor.putString("email", user.getEmail());
                    editor.apply(); // Apply the changes to SharedPreferences

                    // Navigate to the main activity (HomeActivity)
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // Finish LoginActivity
                } else {
                    Toast.makeText(LoginActivity.this, "NISN atau Password salah", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}