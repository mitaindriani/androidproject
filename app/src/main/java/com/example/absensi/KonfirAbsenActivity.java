package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
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

public class KonfirAbsenActivity extends AppCompatActivity {

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
    private static final String ABSENSI_FILE = "absensi_data.ser";
    private boolean isAbsenPulang = false;

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

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        byte[] compressedImage = getIntent().getByteArrayExtra("image_bytes");
        absenType = getIntent().getStringExtra("absen_type");
        String waktuTerlambat = getIntent().getStringExtra("waktu_terlambat");
        String lokasi = getIntent().getStringExtra("lokasi");

        isAbsenPulang = "pulang".equalsIgnoreCase(absenType);

        if (compressedImage != null) {
            fotoAbsen = BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            fotoAbsen = Bitmap.createBitmap(fotoAbsen, 0, 0, fotoAbsen.getWidth(), fotoAbsen.getHeight(), matrix, true);
            fotoAbsenImageView.setImageBitmap(fotoAbsen);
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
            lokasiTextView.setVisibility(View.GONE);
        }


        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdfTanggal = new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()); // <--- PERBAIKAN DI SINI
                String tanggalAbsen = sdfTanggal.format(new Date());
                String namaPengguna = sharedPreferences.getString("nama", "Nama Tidak Ditemukan");
                String jamSekarang = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                byte[] byteArrayFoto = null;
                if (fotoAbsen != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    fotoAbsen.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArrayFoto = stream.toByteArray();
                }

                if (!isAbsenPulang) {
                    AbsensiItem absensiItem = new AbsensiItem(tanggalAbsen, namaPengguna, jamSekarang, waktuTerlambatTextView.getText().toString().replace("Waktu Terlambat: ", ""), "-", true, byteArrayFoto);
                    saveAbsensi(absensiItem);
                    Toast.makeText(KonfirAbsenActivity.this, "Absen Datang Berhasil!", Toast.LENGTH_SHORT).show();
                } else {
                    updateAbsensiPulang(tanggalAbsen, namaPengguna, jamSekarang);
                    Toast.makeText(KonfirAbsenActivity.this, "Absen Pulang Berhasil!", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(KonfirAbsenActivity.this, RekapActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void updateAbsensiPulang(String tanggal, String nama, String jamPulang) {
        List<AbsensiItem> existingAbsensi = loadAbsensi();
        for (AbsensiItem item : existingAbsensi) {
            if (item.getTanggal().equals(tanggal) && item.getNama().equals(nama) && item.getJamPulang().equals("-")) {
                item.setJamPulang(jamPulang);
                break;
            }
        }
        saveListAbsensi(existingAbsensi);
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
            Toast.makeText(this, "Data absensi disimpan", Toast.LENGTH_SHORT).show();
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
            // Jika file tidak ditemukan atau terjadi error lain, kembalikan list kosong
            // e.printStackTrace();
        }
        return absensiList;
    }
}