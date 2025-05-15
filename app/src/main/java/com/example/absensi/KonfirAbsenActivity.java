package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
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
import android.content.SharedPreferences;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.InputStream;

public class KonfirAbsenActivity extends AppCompatActivity {

    private static final String TAG = KonfirAbsenActivity.class.getSimpleName();
    private static final String ABSENSI_FILE = "absensi_data.ser";
    private static final String USER_DATA = "user_data";
    private static final String KEY_NISN = "nisn"; // Menggunakan NISN
    private static final String KEY_SUDAH_ABSEN_DATANG = "sudah_absen_datang";
    private static final String KEY_NAMA = "nama";

    ImageView fotoAbsenImageView;
    TextView jenisAbsenTextView;
    Button btnKonfirmasi;
    Button btnAmbilUlang;
    String absenType;
    Bitmap fotoAbsen;
    TextView jamDatangTextView;
    TextView waktuTerlambatTextView;
    TextView lokasiTextView;
    private SharedPreferences sharedPreferences;
    private String currentTime;
    private boolean isAbsenPulang = false;
    private String namaPengguna;
    private String nisnPengguna; // Tambahkan variabel untuk menyimpan NISN
    private Uri imageUri;
    private int cameraOrientation; // Tambahkan untuk menyimpan orientasi kamera

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfir_absen);

        fotoAbsenImageView = findViewById(R.id.fotoAbsenImageView);
        jenisAbsenTextView = findViewById(R.id.jenisAbsenTextView);
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi);
        btnAmbilUlang = findViewById(R.id.btnAmbilUlang);
        jamDatangTextView = findViewById(R.id.jamDatangTextView);
        waktuTerlambatTextView = findViewById(R.id.waktuTerlambatTextView);
        lokasiTextView = findViewById(R.id.lokasiTextView);

        sharedPreferences = getSharedPreferences(USER_DATA, MODE_PRIVATE);
        namaPengguna = sharedPreferences.getString(KEY_NAMA, "Nama Tidak Ditemukan");
        nisnPengguna = sharedPreferences.getString(KEY_NISN, "NISN Tidak Ditemukan"); // Ambil NISN dari SharedPreferences

        // Mendapatkan data dari Intent
        byte[] compressedImage = getIntent().getByteArrayExtra("image_bytes");
        absenType = getIntent().getStringExtra("absen_type");
        String waktuTerlambat = getIntent().getStringExtra("waktu_terlambat");
        String lokasi = getIntent().getStringExtra("lokasi");
        String imageUriString = getIntent().getStringExtra("image_uri");
        cameraOrientation = getIntent().getIntExtra("camera_orientation", 0); //ambil orientasi
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
        }

        isAbsenPulang = "pulang".equalsIgnoreCase(absenType);

        if (compressedImage != null) {
            fotoAbsen = BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.length);
            fotoAbsen = rotateBitmap(fotoAbsen, cameraOrientation);
            fotoAbsenImageView.setImageBitmap(fotoAbsen);
        } else if (imageUri != null) {
            try {
                fotoAbsen = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                fotoAbsen = rotateBitmap(fotoAbsen, cameraOrientation);
                fotoAbsenImageView.setImageBitmap(fotoAbsen);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading image: " + e.getMessage());
            }
        }


        jenisAbsenTextView.setText("Absen " + absenType.substring(0, 1).toUpperCase() + absenType.substring(1));

        if (!isAbsenPulang) {
            SimpleDateFormat sdfJam = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            currentTime = sdfJam.format(Calendar.getInstance().getTime());
            jamDatangTextView.setText("Jam Datang: " + currentTime);
            waktuTerlambatTextView.setText("Waktu Terlambat: " + waktuTerlambat);
            lokasiTextView.setText("Lokasi: " + lokasi);
        } else {
            jamDatangTextView.setVisibility(View.GONE);
            waktuTerlambatTextView.setVisibility(View.GONE);
            lokasiTextView.setText("Lokasi: " + lokasi);
        }

        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdfTanggal = new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault());
                String tanggalAbsen = sdfTanggal.format(new Date());
                String jamSekarang = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                byte[] byteArrayFoto = null;
                if (fotoAbsen != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    fotoAbsen.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArrayFoto = stream.toByteArray();
                }

                if (!isAbsenPulang) {
                    // Simpan data absen masuk
                    AbsensiItem absensiItem = new AbsensiItem(tanggalAbsen, namaPengguna, jamSekarang, waktuTerlambatTextView.getText().toString().replace("Waktu Terlambat: ", ""), "-", true, byteArrayFoto, nisnPengguna); // Simpan dengan NISN dan set berhasil = true
                    saveAbsensi(absensiItem);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_SUDAH_ABSEN_DATANG, true);
                    editor.apply();
                    Toast.makeText(KonfirAbsenActivity.this, "Absen Datang Berhasil!", Toast.LENGTH_SHORT).show();
                } else {
                    // Update data absen pulang
                    boolean pulangBerhasil = updateAbsensiPulang(tanggalAbsen, nisnPengguna, jamSekarang); // Gunakan NISN
                    if (pulangBerhasil) {
                        Toast.makeText(KonfirAbsenActivity.this, "Absen Pulang Berhasil!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(KonfirAbsenActivity.this, "Gagal Absen Pulang: Data Absen Masuk Tidak Ditemukan!", Toast.LENGTH_LONG).show();
                    }
                }

                Intent intent = new Intent(KonfirAbsenActivity.this, RekapActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean updateAbsensiPulang(String tanggal, String nisn, String jamPulang) {
        List<AbsensiItem> existingAbsensi = loadAbsensi();
        for (AbsensiItem item : existingAbsensi) {
            if (item.getTanggal().equals(tanggal) && item.getNisn().equals(nisn) && item.getJamPulang().equals("-")) { // Gunakan NISN untuk pencocokan
                item.setJamPulang(jamPulang);
                saveListAbsensi(existingAbsensi);
                return true;
            }
        }
        return false;
    }

    private void saveAbsensi(AbsensiItem item) {
        List<AbsensiItem> existingAbsensi = loadAbsensi();
        existingAbsensi.add(0, item);
        saveListAbsensi(existingAbsensi);
    }

    private void saveListAbsensi(List<AbsensiItem> list) {
        try (FileOutputStream fileOut = new FileOutputStream(getFileStreamPath(ABSENSI_FILE));
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(list);
        } catch (IOException e) {
            Toast.makeText(this, "Gagal menyimpan data absensi", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private List<AbsensiItem> loadAbsensi() {
        List<AbsensiItem> absensiList = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(getFileStreamPath(ABSENSI_FILE));
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            absensiList = (List<AbsensiItem>) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // e.printStackTrace();
        }
        return absensiList;
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