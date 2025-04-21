package com.example.absensi; // Ganti dengan package aplikasi Anda

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextNama; // Sekarang akan digunakan untuk NISN
    private EditText editTextPassword;
    private Button buttonMasuk;
    private TextView textViewRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi view
        editTextNama = findViewById(R.id.editTextNama); // Menggunakan EditText dengan ID editTextNama untuk input NISN
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonMasuk = findViewById(R.id.buttonMasuk);
        textViewRegister = findViewById(R.id.textViewRegister);

        // Inisialisasi DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Set OnClickListener untuk tombol Masuk
        buttonMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nisn = editTextNama.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validasi input
                if (nisn.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "NISN dan password harus diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lakukan verifikasi login dari database
                if (dbHelper.checkUser(nisn, password)) {
                    Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // Optional: Menutup LoginActivity agar tidak bisa kembali dengan tombol "Back"
                } else {
                    Toast.makeText(LoginActivity.this, "Login gagal. NISN atau password salah", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set OnClickListener untuk TextView Register
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                // Tidak perlu finish() di sini, karena pengguna mungkin ingin kembali ke LoginActivity setelah register
            }
        });
    }
}