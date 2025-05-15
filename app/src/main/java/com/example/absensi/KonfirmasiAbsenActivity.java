package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class KonfirmasiAbsenActivity extends AppCompatActivity {

    ImageView fotoAbsenImageView;
    TextView jenisAbsenTextView;
    Button btnKonfirmasi;
    Button btnAmbilUlang;
    String absenType;
    Bitmap fotoAbsen;
    private static final String TAG = "KonfirmasiAbsenActivity";

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
            try {
                // 1. Decode byte array menjadi Bitmap
                fotoAbsen = BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.length);

                // 2. Gunakan ByteArrayInputStream untuk mendapatkan InputStream dari byte array
                InputStream inputStream = new ByteArrayInputStream(compressedImage);
                ExifInterface exifInterface = new ExifInterface(inputStream);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                inputStream.close();

                // 3. Putar bitmap jika perlu
                Bitmap rotatedBitmap = rotateBitmap(fotoAbsen, orientation);
                fotoAbsen = rotatedBitmap; //update fotoAbsen
                fotoAbsenImageView.setImageBitmap(rotatedBitmap);


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading image: " + e.getMessage());
                fotoAbsenImageView.setImageBitmap(fotoAbsen);

            }
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

    private Bitmap rotateBitmap(Bitmap source, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return source;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.postRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.postRotate(-90);
                matrix.postScale(-1, 1);
                break;
            default:
                return source;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
            if (source != null && !source.isRecycled()) {
                source.recycle();
            }
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}