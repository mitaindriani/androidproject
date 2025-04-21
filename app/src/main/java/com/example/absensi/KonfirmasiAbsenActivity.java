package com.example.absensi; // Ganti dengan package aplikasi Anda

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class KonfirmasiAbsenActivity extends AppCompatActivity {

    ImageView fotoAbsenImageView;
    TextView jenisAbsenTextView;
    Button btnKonfirmasi;
    Button btnAmbilUlang;
    String absenType;
    Bitmap fotoAbsen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_absen); // Pastikan layout ini ada

        fotoAbsenImageView = findViewById(R.id.fotoAbsenImageView);
        jenisAbsenTextView = findViewById(R.id.jenisAbsenTextView);
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi);
        btnAmbilUlang = findViewById(R.id.btnAmbilUlang);

        // Menerima data dari Intent
        byte[] compressedImage = getIntent().getByteArrayExtra("image_bytes");
        absenType = getIntent().getStringExtra("absen_type");

        // Menampilkan foto dan jenis absensi
        if (compressedImage != null) {
            fotoAbsen = BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.length);
            fotoAbsenImageView.setImageBitmap(fotoAbsen);
        }
        jenisAbsenTextView.setText("Absen " + absenType.substring(0, 1).toUpperCase() + absenType.substring(1));

        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Di sini Anda akan mengirim data absensi (termasuk foto) ke server atau menyimpan secara lokal
                Toast.makeText(KonfirmasiAbsenActivity.this, "Absen " + absenType + " Berhasil!", Toast.LENGTH_SHORT).show();
                finish(); // Kembali ke HomeActivity
            }
        });

        btnAmbilUlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kembali ke ActivityCamera untuk mengambil foto ulang
                finish();
            }
        });
    }
}