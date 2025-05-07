package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button; // Import Button
import android.widget.TextView;
import android.widget.Toast;

public class ProfilActivity extends AppCompatActivity {

    private ImageView logoSmknImageView;
    private TextView namaTextView;
    private TextView userIdTextView;
    private ImageView keluarImageView;
    private Button profileButton; // Gunakan Button
    private Button homeButton;    // Gunakan Button
    private Button rekapButton;   // Gunakan Button
    private ImageView logoutImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inisialisasi View
        logoSmknImageView = findViewById(R.id.logo_smkn);
        namaTextView = findViewById(R.id.userNameTextView);
        userIdTextView = findViewById(R.id.userIdTextView);
        keluarImageView = findViewById(R.id.logoutImageView);

        // Mendapatkan data pengguna dari Intent
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
        }

        // Listener untuk tombol "Keluar"
        keluarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfilActivity.this, "Simulasi Logout", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Bottom Navigation
        profileButton = findViewById(R.id.profile_button); // Langsung findViewById
        homeButton = findViewById(R.id.home_button);       // Langsung findViewById
        rekapButton = findViewById(R.id.rekap_button);     // Langsung findViewById
        logoutImageView = findViewById(R.id.logoutImageView);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Saat ini berada di halaman Profil
                Toast.makeText(ProfilActivity.this, "Anda sudah di Profil", Toast.LENGTH_SHORT).show();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        rekapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, RekapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}