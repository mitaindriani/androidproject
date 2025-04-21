package com.example.absensi; // Ganti dengan nama package aplikasi Anda

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;

public class KonfirActivity extends AppCompatActivity {

    private static final String TAG = "KonfirActivity";
    private ImageView capturedImage;
    private Button ulangFotoButton;
    private Button absenSekarangButton;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfir);

        capturedImage = findViewById(R.id.captured_image);
        ulangFotoButton = findViewById(R.id.ulang_foto_button);
        absenSekarangButton = findViewById(R.id.absen_sekarang_button);

        // Mendapatkan path foto dari Intent
        photoPath = getIntent().getStringExtra("photoPath");
        Log.d(TAG, "Path Foto Diterima: " + photoPath);

        if (photoPath != null) {
            File imgFile = new File(photoPath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                if (bitmap != null) {
                    capturedImage.setImageBitmap(bitmap);
                } else {
                    Log.e(TAG, "Gagal decode file bitmap dari path: " + photoPath);
                    Toast.makeText(this, "Gagal memuat foto.", Toast.LENGTH_SHORT).show();
                    capturedImage.setImageResource(android.R.drawable.ic_menu_camera); // Placeholder
                }
            } else {
                Log.e(TAG, "File foto tidak ditemukan di path: " + photoPath);
                Toast.makeText(this, "File foto tidak ditemukan.", Toast.LENGTH_SHORT).show();
                capturedImage.setImageResource(android.R.drawable.ic_menu_camera); // Placeholder
            }
        } else {
            Log.e(TAG, "Path foto dari Intent null.");
            Toast.makeText(this, "Gagal menerima path foto.", Toast.LENGTH_SHORT).show();
            finish(); // Tutup activity jika tidak ada path foto
        }

        ulangFotoButton.setOnClickListener(v -> {
            finish(); // Kembali ke CameraActivity
        });

        absenSekarangButton.setOnClickListener(v -> {
            // Lakukan logika absensi di sini
            Toast.makeText(this, "Absen Sekarang Diklik. Path Foto: " + photoPath, Toast.LENGTH_LONG).show();
            // Setelah berhasil absen, mungkin kembali ke HomeActivity
            // Intent intent = new Intent(KonfirActivity.this, HomeActivity.class);
            // startActivity(intent);
            // finish();
        });
    }
}