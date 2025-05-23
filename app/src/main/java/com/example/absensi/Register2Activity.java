package com.example.absensi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Register2Activity extends AppCompatActivity {

    private String nama;
    private String nisn;
    private String password;

    private EditText editTextKelas;
    private EditText editTextJurusan;
    private EditText editTextEmail;
    private Button buttonRegister;
    private TextView textViewLogin;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        Intent intent = getIntent();
        if (intent != null) {
            nama = intent.getStringExtra("NAMA");
            nisn = intent.getStringExtra("NISN");
            password = intent.getStringExtra("PASSWORD");
        } else {
            Toast.makeText(this, "Kesalahan data pendaftaran. Silakan coba lagi.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        editTextKelas = findViewById(R.id.editTextKelas);
        editTextJurusan = findViewById(R.id.editTextJurusan);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);

        dbHelper = new DatabaseHelper(this);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kelas = editTextKelas.getText().toString().trim();
                String jurusan = editTextJurusan.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                if (kelas.isEmpty() || jurusan.isEmpty() || email.isEmpty()) {
                    Toast.makeText(Register2Activity.this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper.checkUser(nisn)) {
                    Toast.makeText(Register2Activity.this, "NISN sudah terdaftar. Silakan login atau gunakan NISN lain.", Toast.LENGTH_LONG).show();
                    Intent loginIntent = new Intent(Register2Activity.this, LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                    finish();
                    return;
                }

                // THIS IS THE UPDATED LINE
                long result = dbHelper.addUser(nama, nisn, password, kelas, jurusan, email);

                if (result > 0) {
                    Toast.makeText(Register2Activity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(Register2Activity.this, LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                    finish();
                } else {
                    Toast.makeText(Register2Activity.this, "Registrasi gagal!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(Register2Activity.this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);
                finish();
            }
        });
    }
}