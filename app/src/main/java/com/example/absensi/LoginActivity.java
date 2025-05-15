package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import com.example.absensi.DatabaseHelper.User;
import android.util.Log; // Import for logging

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername; // Lebih deskriptif: editTextUsername
    private EditText editTextPassword;
    private Button buttonMasuk;
    private TextView textViewRegister;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    // Informasi login admin
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String PREF_NAME = "user_data"; // Konstanta untuk nama SharedPreferences
    private static final String KEY_NAMA = "nama";
    private static final String KEY_NISN = "nisn";
    private static final String KEY_IS_ADMIN = "is_admin"; // Menyimpan status admin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi view
        editTextUsername = findViewById(R.id.editTextNama); // ID tetap editTextNama, tapi variabel jadi username
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonMasuk = findViewById(R.id.buttonMasuk);
        textViewRegister = findViewById(R.id.textViewRegister);

        // Inisialisasi DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Set OnClickListener untuk tombol Masuk
        buttonMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin(); // Memanggil method terpisah untuk menangani logika login
            }
        });

        // Set OnClickListener untuk TextView Register
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                // Tidak perlu finish() di sini.
            }
        });
    }

    private void handleLogin() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        try { // Tambahkan try-catch untuk menangani potensi error database
            // Cek apakah login adalah untuk admin
            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                Toast.makeText(LoginActivity.this, "Login admin berhasil!", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_IS_ADMIN, true); // Simpan status admin
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, MasterJadwalActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            // Lakukan verifikasi login pengguna biasa dari database
            User user = dbHelper.getUser(username, password);
            if (user != null) {
                Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                // Simpan data pengguna ke SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_NAMA, user.getNama());
                editor.putString(KEY_NISN, user.getNisn());
                editor.putBoolean(KEY_IS_ADMIN, false); // Simpan status user
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Login gagal. Username atau password salah", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("LoginActivity", "Error during login: " + e.getMessage()); // Log error
            Toast.makeText(LoginActivity.this, "Terjadi kesalahan: " + e.getMessage(), Toast.LENGTH_LONG).show(); // Beri tahu user
        }
    }
}
