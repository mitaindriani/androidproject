package com.example.absensi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    Button btnAbsenMasuk;
    Button btnAbsenPulang;
    Button navigation_profile;
    Button navigation_home;
    Button navigation_rekap;

    private TextView attendanceDateTextView;
    private TextView attendanceTimeTextView;
    private TextView jadwalMasukTextView;
    private TextView jadwalPulangTextView;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ImageView logoutImageView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences jadwalPrefs;
    private boolean sudahAbsenDatang = false;
    private String namaPengguna;

    private static final int REQUEST_IMAGE_CAPTURE_MASUK = 1;
    private static final String ABSENSI_FILE = "absensi_data.ser";
    private static final String JADWAL_PREFS = "master_jadwal";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inisialisasi MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Inisialisasi FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inisialisasi SharedPreferences untuk status absen dan jadwal
        sharedPreferences = getSharedPreferences("absensi_status", MODE_PRIVATE);
        jadwalPrefs = getSharedPreferences(JADWAL_PREFS, MODE_PRIVATE);
        sudahAbsenDatang = sharedPreferences.getBoolean("sudah_absen_datang", false);

        // Ambil nama pengguna dari SharedPreferences
        SharedPreferences userData = getSharedPreferences("user_data", MODE_PRIVATE);
        namaPengguna = userData.getString("nama", "Nama Tidak Ditemukan");

        // Inisialisasi tombol
        btnAbsenMasuk = findViewById(R.id.btnAbsenMasuk);
        btnAbsenPulang = findViewById(R.id.btnAbsenPualang);
        navigation_profile = findViewById(R.id.navigation_profile);
        navigation_home = findViewById(R.id.navigation_home);
        navigation_rekap = findViewById(R.id.navigation_rekap);
        logoutImageView = findViewById(R.id.logoutImageView);

        // Inisialisasi TextView untuk tanggal, waktu, dan jadwal
        attendanceDateTextView = findViewById(R.id.attendanceDateTextView);
        attendanceTimeTextView = findViewById(R.id.attendanceTimeTextView);
        jadwalMasukTextView = findViewById(R.id.jadwalMasukTextView); // Pastikan ada TextView dengan ID ini di layout
        jadwalPulangTextView = findViewById(R.id.jadwalPulangTextView); // Pastikan ada TextView dengan ID ini di layout

        // Memulai update waktu dan memuat jadwal
        startUpdatingTime();
        loadJadwalHariIni();

        // Set status awal tombol absen pulang
        btnAbsenPulang.setEnabled(sudahAbsenDatang);
        Log.d("HomeActivity", "onCreate: sudahAbsenDatang = " + sudahAbsenDatang);
        Log.d("HomeActivity", "onCreate: btnAbsenPulang.isEnabled() = " + btnAbsenPulang.isEnabled());

        // Set OnClickListener untuk tombol absen masuk
        btnAbsenMasuk.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                if (!isTerlambatAbsenMasuk()) {
                    Intent cameraIntent = new Intent(HomeActivity.this, ActivityCamera.class);
                    cameraIntent.putExtra("absen_type", "masuk");
                    startActivity(cameraIntent);
                } else {
                    showTerlambatAbsenMasukDialog();
                }
            } else {
                Toast.makeText(HomeActivity.this, "Tidak ada absensi di hari Sabtu dan Minggu.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener untuk tombol absen pulang (MEMUNCULKAN POP-UP KONFIRMASI)
        btnAbsenPulang.setOnClickListener(v -> {
            Log.d("HomeActivity", "absenPulang Button Clicked");
            if (sudahAbsenDatang) {
                // Periksa apakah hari ini bukan Sabtu atau Minggu
                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                    // Periksa apakah sudah waktunya pulang dan belum lewat 1 menit
                    if (isWaktunyaPulang() && !isTerlambatAbsenPulang()) {
                        showKonfirmasiAbsenPulangDialog();
                    } else if (isTerlambatAbsenPulang()) {
                        showTerlambatAbsenPulangDialog();
                    } else {
                        showBelumWaktunyaPulangDialog();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Tidak ada absensi di hari Sabtu dan Minggu.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(HomeActivity.this, "Anda belum melakukan absen masuk!", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener untuk navigasi
        navigation_profile.setOnClickListener(v -> navigateTo(ProfilActivity.class));
        navigation_home.setOnClickListener(v -> navigateTo(HomeActivity.class));
        navigation_rekap.setOnClickListener(v -> navigateTo(RekapActivity.class));


        logoutImageView.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(HomeActivity.this, targetActivity);
        startActivity(intent);
        finish();
    }

    private void loadJadwalHariIni() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String hariIni = "";

        switch (dayOfWeek) {
            case Calendar.MONDAY:
                hariIni = "senin";
                break;
            case Calendar.TUESDAY:
                hariIni = "selasa";
                break;
            case Calendar.WEDNESDAY:
                hariIni = "rabu";
                break;
            case Calendar.THURSDAY:
                hariIni = "kamis";
                break;
            case Calendar.FRIDAY:
                hariIni = "jumat";
                break;
            case Calendar.SATURDAY:
            case Calendar.SUNDAY:
                jadwalMasukTextView.setText("Libur");
                jadwalPulangTextView.setText("Libur");
                btnAbsenMasuk.setEnabled(false);
                btnAbsenPulang.setEnabled(false);
                return;
            default:
                // Hari tidak dikenal
                jadwalMasukTextView.setText("-");
                jadwalPulangTextView.setText("-");
                return;
        }

        String masukKey = hariIni + "_masuk";
        String pulangKey = hariIni + "_pulang";

        String jamMasuk = jadwalPrefs.getString(masukKey, "-");
        String jamPulang = jadwalPrefs.getString(pulangKey, "-");

        jadwalMasukTextView.setText(jamMasuk);
        jadwalPulangTextView.setText(jamPulang);
        btnAbsenMasuk.setEnabled(true);
        btnAbsenPulang.setEnabled(sudahAbsenDatang); // Set kembali berdasarkan status
    }

    private boolean isWaktunyaPulang() {
        String jadwalPulang = jadwalPulangTextView.getText().toString();
        if (jadwalPulang.equals("-") || jadwalPulang.equals("Libur")) {
            return true; // Jika jadwal pulang tidak diatur atau hari libur, anggap boleh pulang (untuk tombol aktif)
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        try {
            Date waktuPulang = sdf.parse(jadwalPulang);
            Date waktuSekarang = sdf.parse(currentTime);
            if (waktuPulang != null && waktuSekarang != null) {
                return waktuSekarang.compareTo(waktuPulang) >= 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isTerlambatAbsenPulang() {
        String jadwalPulang = jadwalPulangTextView.getText().toString();
        if (jadwalPulang.equals("-") || jadwalPulang.equals("Libur")) {
            return false; // Tidak terlambat jika jadwal tidak ada atau hari libur
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        try {
            Date waktuPulang = sdf.parse(jadwalPulang);
            Date waktuSekarang = sdf.parse(currentTime);
            if (waktuPulang != null && waktuSekarang != null) {
                long diffInMillis = waktuSekarang.getTime() - waktuPulang.getTime();
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
                return diffInMinutes > 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isTerlambatAbsenMasuk() {
        String jadwalMasuk = jadwalMasukTextView.getText().toString();
        if (jadwalMasuk.equals("-") || jadwalMasuk.equals("Libur")) {
            return false; // Tidak terlambat jika jadwal tidak ada atau hari libur
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        try {
            Date waktuMasuk = sdf.parse(jadwalMasuk);
            Date waktuSekarang = sdf.parse(currentTime);
            if (waktuMasuk != null && waktuSekarang != null) {
                long diffInMillis = waktuSekarang.getTime() - waktuMasuk.getTime();
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
                return diffInMinutes > 5;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showTerlambatAbsenMasukDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Peringatan");
        builder.setMessage("Waktu absen masuk telah lewat.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showBelumWaktunyaPulangDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Peringatan");
        builder.setMessage("Belum waktunya untuk absen pulang. Silakan coba lagi sesuai jadwal.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showTerlambatAbsenPulangDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Peringatan");
        builder.setMessage("Waktu absen pulang telah lewat.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showKonfirmasiAbsenPulangDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Absen Pulang");
        builder.setMessage("Apakah Anda yakin ingin melakukan absen pulang sekarang?");

        builder.setPositiveButton("Ya", (dialog, which) -> absenPulang());
        builder.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void absenPulang() {
        Log.d("HomeActivity", "absenPulang: Tombol absen pulang ditekan setelah konfirmasi");
        SimpleDateFormat sdfTanggal = new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault());
        String tanggalAbsen = sdfTanggal.format(new Date());
        String jamPulang = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        updateAbsensiPulang(tanggalAbsen, namaPengguna, jamPulang);

        Toast.makeText(this, "Absen Pulang Berhasil!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity.this, RekapActivity.class);
        startActivity(intent);
    }

    private void updateAbsensiPulang(String tanggal, String nama, String jamPulang) {
        SharedPreferences userData = getSharedPreferences("user_data", MODE_PRIVATE);
        String nisnPengguna = userData.getString("nisn", "");

        List<AbsensiItem> existingAbsensi = loadAbsensi();
        boolean updated = false;
        for (AbsensiItem item : existingAbsensi) {
            if (item.getTanggal().equals(tanggal) && item.getNama().equals(nama) && item.getJamPulang().equals("-") && item.getNisn().equals(nisnPengguna)) {
                item.setJamPulang(jamPulang);
                updated = true;
                break;
            }
        }
        if (updated) {
            saveListAbsensi(existingAbsensi);
        } else {
            Toast.makeText(this, "Data absen masuk tidak ditemukan untuk hari ini.", Toast.LENGTH_SHORT).show();
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

    private void saveListAbsensi(List<AbsensiItem> list) {
        try (FileOutputStream fileOut = new FileOutputStream(getFileStreamPath(ABSENSI_FILE));
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(list);
            // Toast.makeText(this, "Data absensi disimpan", Toast.LENGTH_SHORT).show(); // Jangan tampilkan toast di sini
        } catch (IOException e) {
            Toast.makeText(this, "Gagal menyimpan data absensi", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM", new Locale("in", "ID"));
        String formattedDate = dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedTime = timeFormat.format(calendar.getTime());
        attendanceDateTextView.setText(formattedDate);
        attendanceTimeTextView.setText(formattedTime);
    }

    private void startUpdatingTime() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(userLocation).title("Anda berada di sini"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    } else {
                        Toast.makeText(this, "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        startUpdatingTime();
        loadJadwalHariIni();
        // Periksa kembali status absen datang saat resume
        sudahAbsenDatang = sharedPreferences.getBoolean("sudah_absen_datang", true);
        Log.d("HomeActivity", "onResume: sudahAbsenDatang = " + sudahAbsenDatang);
        btnAbsenPulang.setEnabled(sudahAbsenDatang);
        Log.d("HomeActivity", "onResume: btnAbsenPulang.isEnabled() setelah diatur = " + btnAbsenPulang.isEnabled());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }
}