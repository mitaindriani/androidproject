package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import java.io.ByteArrayOutputStream;

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
        setContentView(R.layout.activity_konfirmasi_absen);

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
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            fotoAbsen = Bitmap.createBitmap(fotoAbsen, 0, 0, fotoAbsen.getWidth(), fotoAbsen.getHeight(), matrix, true);
            fotoAbsenImageView.setImageBitmap(fotoAbsen);
        }
        jenisAbsenTextView.setText("Absen " + absenType.substring(0, 1).toUpperCase() + absenType.substring(1));

        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent untuk kembali ke KonfirAbsenActivity
                Intent intent = new Intent(KonfirmasiAbsenActivity.this, KonfirAbsenActivity.class);

                // Kirim data foto
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                fotoAbsen.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArray = baos.toByteArray();
                intent.putExtra("image_bytes", byteArray);
                intent.putExtra("absen_type", absenType);
                // Tambahkan data lain yang diperlukan seperti jam datang, waktu terlambat, lokasi
                intent.putExtra("jam_datang", "10:00 AM"); // Contoh data
                intent.putExtra("waktu_terlambat", "0 minutes"); // Contoh data
                intent.putExtra("lokasi", "Kantor Pusat"); // Contoh data

                startActivity(intent);
                finish(); // Menutup Activity Konfirmasi
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
