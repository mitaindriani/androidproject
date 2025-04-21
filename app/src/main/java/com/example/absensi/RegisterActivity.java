package com.example.absensi; // Ganti dengan package aplikasi Anda

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextNama;
    private EditText editTextNisn;
    private EditText editTextPassword;
    private Button buttonRegister;
    private TextView textViewLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi view
        editTextNama = findViewById(R.id.editTextNama);
        editTextNisn = findViewById(R.id.editTextNisn);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);

        // Inisialisasi DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Set listener untuk tombol Daftar
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mendapatkan nilai dari input fields
                String nama = editTextNama.getText().toString().trim();
                String nisn = editTextNisn.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validasi input
                if (nama.isEmpty() || nisn.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Periksa apakah NISN sudah terdaftar
                if (dbHelper.checkUser(nisn)) {
                    Toast.makeText(RegisterActivity.this, "NISN sudah terdaftar", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lakukan proses registrasi dan simpan ke database
                long result = dbHelper.addUser(nama, nisn, password);

                if (result > 0) {
                    Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                    finish(); // Kembali ke halaman login setelah registrasi berhasil
                } else {
                    Toast.makeText(RegisterActivity.this, "Registrasi gagal!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set listener untuk TextView Login
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigasi kembali ke LoginActivity
                finish(); // Atau gunakan Intent untuk navigasi eksplisit
            }
        });
    }
}