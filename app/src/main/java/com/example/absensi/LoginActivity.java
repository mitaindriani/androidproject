package com.example.absensi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextNama, editTextPassword;
    private Button buttonMasuk;
    private TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextNama = findViewById(R.id.editTextNama);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonMasuk = findViewById(R.id.buttonMasuk);
        textViewRegister = findViewById(R.id.textViewRegister);

        buttonMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementasi logika login di sini
                String nama = editTextNama.getText().toString();
                String password = editTextPassword.getText().toString();

                // Contoh sederhana (ganti dengan logika login sesuai kebutuhan
                if (nama.equals("admin") && password.equals("password")) { // Ganti dengan logika login sesuai kebutuhan
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class); // Buat HomeActivity.java terlebih dahulu
                    startActivity(intent);
                    finish(); // Menutup LoginActivity agar tidak bisa kembali ke halaman login dengan tombol back
                } else {
                    Toast.makeText(LoginActivity.this, "Nama atau password salah", Toast.LENGTH_SHORT).show();
                }
            }
        });
//change
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}