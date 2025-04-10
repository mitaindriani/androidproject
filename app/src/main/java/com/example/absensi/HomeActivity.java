package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private Button buttonDismiss;
    private Button buttonAbsenMasuk;
    private Button buttonAbsenPulang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buttonDismiss = findViewById(R.id.button_dismiss);
        buttonAbsenMasuk = findViewById(R.id.button_absen_masuk);
        buttonAbsenPulang = findViewById(R.id.button_absen_pulang);

        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tambahkan logika untuk menyembunyikan atau menutup card_selamat_datang
                // Misalnya:
                // CardView cardSelamatDatang = findViewById(R.id.card_selamat_datang);
                // cardSelamatDatang.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, "Dismiss clicked", Toast.LENGTH_SHORT).show();
            }
        });

        buttonAbsenMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tambahkan logika untuk proses absen masuk
                Toast.makeText(HomeActivity.this, "Absen Masuk clicked", Toast.LENGTH_SHORT).show();
            }
        });

        buttonAbsenPulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tambahkan logika untuk proses absen pulang
                Toast.makeText(HomeActivity.this, "Absen Pulang clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Implementasi onClickListeners untuk item navigasi bawah (Profile, Home, Rekap)
        // Anda bisa menggunakan findViewById untuk mendapatkan referensi ke LinearLayout
        // atau ImageView/TextView di setiap item dan mengatur onClickListener.

        LinearLayout profileNav = findViewById(R.id.bottom_navigation).getChildAt(0);
        profileNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
                // Tambahkan intent atau logika untuk membuka halaman Profile
            }
        });

        LinearLayout homeNav = findViewById(R.id.bottom_navigation).getChildAt(1);
        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
                // Jika sudah berada di HomeActivity, mungkin tidak perlu melakukan apa-apa
            }
        });

        LinearLayout rekapNav = findViewById(R.id.bottom_navigation).getChildAt(2);
        rekapNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Rekap clicked", Toast.LENGTH_SHORT).show();
                // Tambahkan intent atau logika untuk membuka halaman Rekap
            }
        });
    }
}