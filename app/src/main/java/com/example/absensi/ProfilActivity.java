package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilActivity extends AppCompatActivity {

    private ImageView logoSmknImageView;
    private TextView namaTextView;
    private TextView userIdTextView;
    private ImageView keluarImageView;
    private Button profileButton;
    private Button homeButton;
    private Button rekapButton;
    private ImageView logoutImageView;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "user_data";
    private static final String KEY_NAMA = "nama";
    private static final String KEY_NISN = "nisn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inisialisasi View
        logoSmknImageView = findViewById(R.id.logo_smkn);
        namaTextView = findViewById(R.id.userNameTextView);
        userIdTextView = findViewById(R.id.userIdTextView);
        keluarImageView = findViewById(R.id.logoutImageView);

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Mendapatkan data nama dan NISN dari SharedPreferences
        String userName = sharedPreferences.getString(KEY_NAMA, "Nama Pengguna"); // Nilai default jika tidak ada
        String userId = sharedPreferences.getString(KEY_NISN, "NISN"); // Nilai default jika tidak ada

        // Menampilkan data pengguna
        namaTextView.setText(userName);
        userIdTextView.setText(userId);

        // Listener untuk tombol "Keluar" (tetap menggunakan ImageView)
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
        profileButton = findViewById(R.id.profile_button);
        homeButton = findViewById(R.id.home_button);
        rekapButton = findViewById(R.id.rekap_button);
        logoutImageView = findViewById(R.id.logoutImageView);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                // Hapus data login saat logout
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}