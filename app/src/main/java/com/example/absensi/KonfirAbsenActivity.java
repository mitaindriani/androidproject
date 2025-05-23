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
    TextView jamPulangTextView;
    TextView waktuTerlambatTextView; // Deklarasi

    private SharedPreferences sharedPreferences;
    private String currentTime;
    private boolean isAbsenPulang = false;
    private String namaPengguna;
    private String nisnPengguna; // Variabel untuk menyimpan NISN
    private Uri imageUri;
    private int cameraOrientation; // Untuk menyimpan orientasi kamera

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfir_absen);

        // Inisialisasi Views
        fotoAbsenImageView = findViewById(R.id.fotoAbsenImageView);
        jenisAbsenTextView = findViewById(R.id.jenisAbsenTextView);
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi);
        btnAmbilUlang = findViewById(R.id.btnAmbilUlang);
        jamDatangTextView = findViewById(R.id.jamDatangTextView);
        jamPulangTextView = findViewById(R.id.jamPulangTextView);
        // --- PERBAIKAN DI SINI ---
        waktuTerlambatTextView = findViewById(R.id.waktuTerlambatTextView); // BARIS INI DITAMBAHKAN
        // --- AKHIR PERBAIKAN ---

        sharedPreferences = getSharedPreferences(USER_DATA, MODE_PRIVATE);
        namaPengguna = sharedPreferences.getString(KEY_NAMA, "Nama Tidak Ditemukan");
        nisnPengguna = sharedPreferences.getString(KEY_NISN, "NISN Tidak Ditemukan");

        // Mendapatkan data dari Intent
        byte[] compressedImage = getIntent().getByteArrayExtra("image_bytes");
        absenType = getIntent().getStringExtra("absen_type");
        String waktuTerlambat = getIntent().getStringExtra("waktu_terlambat");
        String imageUriString = getIntent().getStringExtra("image_uri");
        cameraOrientation = getIntent().getIntExtra("camera_orientation", 0);

        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
        }

        isAbsenPulang = "pulang".equalsIgnoreCase(absenType);

        // Menampilkan foto absen
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

        // Mengatur teks jenis absen
        jenisAbsenTextView.setText("Absen " + absenType.substring(0, 1).toUpperCase() + absenType.substring(1));

        // Mengatur tampilan berdasarkan jenis absen (datang/pulang)
        if (!isAbsenPulang) {
            SimpleDateFormat sdfJam = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            currentTime = sdfJam.format(Calendar.getInstance().getTime());
            jamDatangTextView.setText("Jam Datang: " + currentTime);
            // Tambahkan pengecekan null sebelum setText, meskipun seharusnya tidak null setelah findViewById
            if (waktuTerlambatTextView != null) {
                waktuTerlambatTextView.setText("Waktu Terlambat: " + waktuTerlambat);
            }
            // --- PERBAIKAN UNTUK JAM PULANG BELUM TERISI ---
            if (jamPulangTextView != null) {
                jamPulangTextView.setText("Jam Pulang: Belum Absen"); // Mengatur teks default atau kosong
                jamPulangTextView.setVisibility(View.VISIBLE); // Pastikan terlihat
            }
            // --- AKHIR PERBAIKAN ---
        } else {
            // Untuk "Absen Pulang", sembunyikan "Jam Datang" dan "Waktu Terlambat" jika tidak relevan
            jamDatangTextView.setVisibility(View.GONE);
            waktuTerlambatTextView.setVisibility(View.GONE); // Ini juga akan berfungsi sekarang

            // Pastikan jamPulangTextView terlihat dan atur waktu saat ini untuk "Absen Pulang"
            SimpleDateFormat sdfJam = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            currentTime = sdfJam.format(Calendar.getInstance().getTime());
            if (jamPulangTextView != null) {
                jamPulangTextView.setText("Jam Pulang: " + currentTime);
                jamPulangTextView.setVisibility(View.VISIBLE);
            }
        }

        // Listener untuk tombol Konfirmasi
        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdfTanggal = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
                String tanggalAbsen = sdfTanggal.format(new Date());
                String jamSekarang = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                byte[] byteArrayFoto = null;
                if (fotoAbsen != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    fotoAbsen.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArrayFoto = stream.toByteArray();
                }

                String finalLocation = "-"; // Lokasi selalu "-" karena fitur geocoding dihapus

                if (!isAbsenPulang) {
                    // Simpan data absen masuk
                    // Pastikan waktuTerlambatTextView tidak null sebelum mengakses teksnya
                    String terlambatText = (waktuTerlambatTextView != null && waktuTerlambatTextView.getVisibility() == View.VISIBLE) ?
                            waktuTerlambatTextView.getText().toString().replace("Waktu Terlambat: ", "") :
                            "00:00:00"; // Nilai default jika null

                    AbsensiItem absensiItem = new AbsensiItem(tanggalAbsen, namaPengguna, jamSekarang,
                            terlambatText,
                            "-", true, byteArrayFoto, nisnPengguna, finalLocation); // Jam pulang awalnya "-"

                    saveAbsensi(absensiItem);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_SUDAH_ABSEN_DATANG, true);
                    editor.apply();
                    Toast.makeText(KonfirAbsenActivity.this, "Absen Datang Berhasil!", Toast.LENGTH_SHORT).show();
                } else {
                    // Update data absen pulang
                    boolean pulangBerhasil = updateAbsensiPulang(tanggalAbsen, nisnPengguna, jamSekarang, finalLocation);
                    if (pulangBerhasil) {
                        Toast.makeText(KonfirAbsenActivity.this, "Absen Pulang Berhasil!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(KonfirAbsenActivity.this, "Gagal Absen Pulang: Data Absen Masuk Tidak Ditemukan!", Toast.LENGTH_LONG).show();
                    }
                }

                // Navigasi ke RekapActivity setelah konfirmasi
                Intent intent = new Intent(KonfirAbsenActivity.this, RekapActivity.class);
                startActivity(intent);
                finish(); // Tutup KonfirAbsenActivity
            }
        });

        // Listener untuk tombol Ambil Ulang
        btnAmbilUlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat Intent untuk kembali ke AbsenActivity (halaman kamera)
                Intent intent = new Intent(KonfirAbsenActivity.this, AbsenActivity.class);
                // Kirim kembali jenis absen agar AbsenActivity tahu apakah ini untuk "datang" atau "pulang"
                intent.putExtra("absen_type", absenType);
                startActivity(intent);
                finish(); // Tutup KonfirAbsenActivity
            }
        });
    }

    /**
     * Memperbarui data absen pulang untuk item absensi yang sudah ada.
     * Mencari item berdasarkan tanggal dan NISN yang belum memiliki jam pulang.
     * @param tanggal Tanggal absensi.
     * @param nisn NISN pengguna.
     * @param jamPulang Jam pulang.
     * @param lokasiPulang Lokasi pulang.
     * @return true jika berhasil memperbarui, false jika tidak ditemukan.
     */
    private boolean updateAbsensiPulang(String tanggal, String nisn, String jamPulang, String lokasiPulang) {
        List<AbsensiItem> existingAbsensi = loadAbsensi();
        for (AbsensiItem item : existingAbsensi) {
            // Cari entri untuk tanggal dan NISN saat ini yang belum memiliki waktu pulang
            if (item.getTanggal().equals(tanggal) && item.getNisn().equals(nisn) && item.getJamPulang().equals("-")) {
                item.setJamPulang(jamPulang);
                item.setLokasiPulang(lokasiPulang);
                saveListAbsensi(existingAbsensi);
                return true;
            }
        }
        return false;
    }

    /**
     * Menyimpan satu item absensi baru ke daftar.
     * Item baru akan ditambahkan di awal daftar.
     * @param item AbsensiItem yang akan disimpan.
     */
    private void saveAbsensi(AbsensiItem item) {
        List<AbsensiItem> existingAbsensi = loadAbsensi();
        existingAbsensi.add(0, item);
        saveListAbsensi(existingAbsensi);
    }

    /**
     * Menyimpan seluruh daftar absensi ke file internal storage.
     * @param list Daftar AbsensiItem yang akan disimpan.
     */
    private void saveListAbsensi(List<AbsensiItem> list) {
        try (FileOutputStream fileOut = new FileOutputStream(getFileStreamPath(ABSENSI_FILE));
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(list);
        } catch (IOException e) {
            Toast.makeText(this, "Gagal menyimpan data absensi", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * Memuat daftar absensi dari file internal storage.
     * @return Daftar AbsensiItem yang dimuat, atau daftar kosong jika file tidak ditemukan/error.
     */
    private List<AbsensiItem> loadAbsensi() {
        List<AbsensiItem> absensiList = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(getFileStreamPath(ABSENSI_FILE));
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            absensiList = (List<AbsensiItem>) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // e.printStackTrace(); // Uncomment ini untuk melihat error jika ada
        }
        return absensiList;
    }

    /**
     * Memutar Bitmap berdasarkan orientasi yang diberikan.
     * Berguna untuk memperbaiki orientasi gambar yang diambil dari kamera.
     * @param source Bitmap asli.
     * @param orientation Orientasi gambar (dari ExifInterface).
     * @return Bitmap yang sudah diputar, atau null jika terjadi OutOfMemoryError.
     */
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