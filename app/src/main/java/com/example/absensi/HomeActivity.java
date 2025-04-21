package com.example.absensi; // Ganti dengan package aplikasi Anda

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View; // Import View untuk OnClickListener
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    Button btnAbsenMasuk;
    Button btnAbsenPulang;
    Button navigation_profile;
    Button navigation_home;
    Button navigation_rekap;

    private static final int REQUEST_IMAGE_CAPTURE_MASUK = 1;
    private static final int REQUEST_IMAGE_CAPTURE_PULANG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnAbsenMasuk = findViewById(R.id.btnAbsenMasuk);
        btnAbsenPulang = findViewById(R.id.btnAbsenPulang);
        navigation_profile = findViewById(R.id.navigation_profile);
        navigation_home = findViewById(R.id.navigation_home);
        navigation_rekap = findViewById(R.id.navigation_rekap);

        // Di dalam onCreate() HomeActivity
        btnAbsenMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(HomeActivity.this, ActivityCamera.class);
                cameraIntent.putExtra("absen_type", "masuk");
                startActivity(cameraIntent);
            }
        });

        btnAbsenPulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(HomeActivity.this, ActivityCamera.class);
                cameraIntent.putExtra("absen_type", "pulang");
                startActivity(cameraIntent);
            }
        });

        // Di dalam OnClickListener untuk navigation_profile di HomeActivity.java
        navigation_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Pindah ke Profil", Toast.LENGTH_SHORT).show();
                Intent profileIntent = new Intent(HomeActivity.this, ProfilActivity.class);
                profileIntent.putExtra("nama", "Nama Pengguna Saat Ini"); // Ganti dengan data nama sebenarnya
                profileIntent.putExtra("id", "ID Pengguna Saat Ini");     // Ganti dengan data ID sebenarnya
                startActivity(profileIntent);
            }
        });

        navigation_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kita sudah berada di HomeActivity
                Toast.makeText(HomeActivity.this, "Anda sudah di Home", Toast.LENGTH_SHORT).show();
            }
        });

        navigation_rekap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Arahkan ke Activity Rekap (ganti RekapActivity.class dengan activity yang sesuai)
                Toast.makeText(HomeActivity.this, "Pindah ke Rekap", Toast.LENGTH_SHORT).show();
                Intent rekapIntent = new Intent(HomeActivity.this, RekapActivity.class);
                startActivity(rekapIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE_MASUK) {
                // Mendapatkan foto yang diambil dari kamera
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                // Membuat Intent untuk berpindah ke KonfirmasiAbsenActivity
                Intent konfirmasiIntent = new Intent(this, KonfirmasiAbsenActivity.class);
                konfirmasiIntent.putExtra("image", imageBitmap); // Mengirim data gambar
                konfirmasiIntent.putExtra("absen_type", "masuk"); // Mengirim jenis absensi
                startActivity(konfirmasiIntent);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE_PULANG) {
                // Mendapatkan foto yang diambil dari kamera
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                // Membuat Intent untuk berpindah ke KonfirmasiAbsenActivity
                Intent konfirmasiIntent = new Intent(this, KonfirmasiAbsenActivity.class);
                konfirmasiIntent.putExtra("image", imageBitmap); // Mengirim data gambar
                konfirmasiIntent.putExtra("absen_type", "pulang"); // Mengirim jenis absensi
                startActivity(konfirmasiIntent);
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Pengambilan foto dibatalkan", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal mengambil foto", Toast.LENGTH_SHORT).show();
        }
    }
}