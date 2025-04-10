package com.example.absensi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextNamaRegister, editTextEmailRegister, editTextPasswordRegister;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextNamaRegister = findViewById(R.id.editTextNamaRegister);
        editTextEmailRegister = findViewById(R.id.editTextEmailRegister);
        editTextPasswordRegister = findViewById(R.id.editTextPasswordRegister);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementasi logika register di sini
                String nama = editTextNamaRegister.getText().toString();
                String email = editTextEmailRegister.getText().toString();
                String password = editTextPasswordRegister.getText().toString();

                // Contoh sederhana (ganti dengan logika register sesuai kebutuhan)
                Toast.makeText(RegisterActivity.this, "Register berhasil: " + nama + ", " + email + ", " + password, Toast.LENGTH_SHORT).show();

                finish(); // Kembali ke LoginActivity setelah register berhasil
            }
        });
    }
}