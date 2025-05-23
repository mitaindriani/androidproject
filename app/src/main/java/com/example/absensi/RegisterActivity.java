package com.example.absensi;

import android.annotation.SuppressLint;
import android.content.Intent;
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
    // private Button buttonRegister; // This button is no longer used for registration here
    private TextView textViewLogin;
    private TextView textViewSelanjutnya; // This will now trigger the move to Register2Activity
    // private DatabaseHelper dbHelper; // DatabaseHelper is no longer needed in this activity

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi view
        editTextNama = findViewById(R.id.editTextNama);
        editTextNisn = findViewById(R.id.editTextNisn);
        editTextPassword = findViewById(R.id.editTextPassword);
        // buttonRegister = findViewById(R.id.buttonRegister); // Remove this line
        textViewLogin = findViewById(R.id.textViewLogin);
        textViewSelanjutnya = findViewById(R.id.textViewSelanjutnya);

        // dbHelper = new DatabaseHelper(this); // Remove this line

        // Remove the buttonRegister.setOnClickListener block entirely from here.
        // The registration logic (including dbHelper.addUser) has moved to Register2Activity.

        // Set listener untuk TextView Login
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigasi kembali ke LoginActivity
                finish();
            }
        });

        // Set listener untuk textViewSelanjutnya
        textViewSelanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mendapatkan nilai dari input fields
                String nama = editTextNama.getText().toString().trim();
                String nisn = editTextNisn.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validasi input
                if (nama.isEmpty() || nisn.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Nama, NISN, dan Password harus diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Pass data to Register2Activity
                Intent intent = new Intent(RegisterActivity.this, Register2Activity.class);
                intent.putExtra("NAMA", nama);
                intent.putExtra("NISN", nisn);
                intent.putExtra("PASSWORD", password);
                startActivity(intent);
                // Optional: finish() RegisterActivity if you don't want it on the back stack
                // finish();
            }
        });
    }
}