package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilActivity extends AppCompatActivity {

    private ImageView logoSmknImageView;
    private TextView namaTextView;
    private TextView userIdTextView;
    private LinearLayout userInfoLinearLayout;
    private LinearLayout keluarLinearLayout;
    private LinearLayout profileButtonLinearLayout;
    private LinearLayout homeButtonLinearLayout;
    private LinearLayout rekapButtonLinearLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inisialisasi View
        logoSmknImageView = findViewById(R.id.logo_smkn);
        namaTextView = findViewById(R.id.userNameTextView); // Menggunakan ID yang sama dengan HomeActivity
        userIdTextView = findViewById(R.id.userIdTextView);   // Menggunakan ID yang sama dengan HomeActivity
        userInfoLinearLayout = findViewById(R.id.userInfoLinearLayout);
        keluarLinearLayout = findViewById(R.id.keluarLinearLayout);
        profileButtonLinearLayout = findViewById(R.id.profile_button_layout);
        homeButtonLinearLayout = findViewById(R.id.home_button_layout);
        rekapButtonLinearLayout = findViewById(R.id.rekap_button_layout);

        // Mendapatkan data pengguna dari Intent (jika ada) atau sumber lain
        Intent intent = getIntent();
        if (intent != null) {
            String userName = intent.getStringExtra("nama");
            String userId = intent.getStringExtra("id");
            if (userName != null) {
                namaTextView.setText(userName);
            }
            if (userId != null) {
                userIdTextView.setText(userId);
            }
        } else {
            // Jika tidak ada data Intent, Anda bisa mengambil dari SharedPreferences atau sumber lain
            namaTextView.setText("Nama Pengguna");
            userIdTextView.setText("ID Pengguna");
        }

        // Listener untuk tombol "Keluar" (simulasi logout)
        keluarLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementasikan logika logout di sini
                Toast.makeText(ProfilActivity.this, "Simulasi Logout", Toast.LENGTH_SHORT).show();
                // Contoh kembali ke LoginActivity
                Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Listener untuk tombol navigasi bawah
        profileButtonLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sudah berada di halaman profil, tidak perlu melakukan apa-apa
                Toast.makeText(ProfilActivity.this, "Anda berada di Profil", Toast.LENGTH_SHORT).show();
            }
        });

        homeButtonLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        rekapButtonLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, RekapActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}