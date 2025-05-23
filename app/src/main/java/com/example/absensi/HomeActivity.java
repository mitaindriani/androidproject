package com.example.absensi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
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
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

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
    private Location currentLocation;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String ABSENSI_FILE = "absensi_data.ser";
    private static final String JADWAL_PREFS = "master_jadwal";

    // Koordinat Perumda Air Minum Tugu Tirta Kota Malang
    private static final LatLng SCHOOL_LOCATION = new LatLng(-7.970889, 112.668351);
    private static final float RADIUS_IN_METERS = 100; // Radius 100 meter


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

        // Inisialisasi LocationRequest
        locationRequest = new LocationRequest.Builder(
                LocationRequest.PRIORITY_HIGH_ACCURACY, // Prioritas
                5000L // Interval update dalam milidetik
        )
                .setMinUpdateIntervalMillis(2000L) // Interval tercepat untuk update
                .setWaitForAccurateLocation(false) // Opsional: apakah menunggu lokasi akurat sebelum mengembalikan
                .build();

        // Inisialisasi LocationCallback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    currentLocation = location;
                    Log.d("Lokasi Pengguna", "Latitude: " + currentLocation.getLatitude() + ", Longitude: " + currentLocation.getLongitude());
                    // Update tampilan peta dengan lokasi pengguna
                    updateUserLocationOnMap(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                }
            }
        };

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
        // getLocation(); // Dapatkan lokasi awal saat Activity dibuat (tidak perlu update berkelanjutan di sini)

        // Set status awal tombol absen pulang
        btnAbsenPulang.setEnabled(sudahAbsenDatang);
        Log.d("HomeActivity", "onCreate: sudahAbsenDatang = " + sudahAbsenDatang);
        Log.d("HomeActivity", "onCreate: btnAbsenPulang.isEnabled() = " + btnAbsenPulang.isEnabled());

        // Set OnClickListener untuk tombol absen masuk
        btnAbsenMasuk.setOnClickListener(v -> {
            if (currentLocation != null) {
                if (isWithinRadius(currentLocation, SCHOOL_LOCATION, RADIUS_IN_METERS)) {
                    Calendar calendar = Calendar.getInstance();
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                        if (!isTerlambatAbsenMasuk()) {
                            Intent cameraIntent = new Intent(HomeActivity.this, ActivityCamera.class);
                            cameraIntent.putExtra("absen_type", "masuk");
                            if (currentLocation != null) {
                                Intent intent = new Intent(HomeActivity.this, ActivityCamera.class); // Atau KonfirAbsenActivity langsung
                                intent.putExtra("absen_type", "masuk"); // Atau "pulang"
                                intent.putExtra("latitude", currentLocation.getLatitude());
                                intent.putExtra("longitude", currentLocation.getLongitude());
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, "Lokasi tidak tersedia. Pastikan GPS aktif.", Toast.LENGTH_LONG).show();
                            }
                            startActivity(cameraIntent);
                        } else {
                            // Panggil dialog dengan tombol "Tetap Absen"
                            showTerlambatAbsenMasukDialog();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Tidak ada absensi di hari Sabtu dan Minggu.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNotInRadiusDialog("absen masuk");
                }
            } else {
                Toast.makeText(this, "Gagal mendapatkan lokasi. Coba lagi nanti.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener untuk tombol absen pulang (MEMUNCULKAN POP-UP KONFIRMASI)
        btnAbsenPulang.setOnClickListener(v -> {
            Log.d("HomeActivity", "absenPulang Button Clicked");
            if (sudahAbsenDatang) {
                if (currentLocation != null) {
                    if (isWithinRadius(currentLocation, SCHOOL_LOCATION, RADIUS_IN_METERS)) {
                        // Periksa apakah hari ini bukan Sabtu atau Minggu
                        Calendar calendar = Calendar.getInstance();
                        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                        if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                            // Periksa apakah sudah waktunya pulang (dengan grace period 5 menit)
                            if (isWaktunyaPulang()) {
                                showKonfirmasiAbsenPulangDialog();
                            } else {
                                showBelumWaktunyaPulangDialog();
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "Tidak ada absensi di hari Sabtu dan Minggu.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        showNotInRadiusDialog("absen pulang");
                    }
                } else {
                    Toast.makeText(this, "Gagal mendapatkan lokasi. Coba lagi nanti.", Toast.LENGTH_SHORT).show();
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
            return true; // Jika jadwal pulang tidak diatur atau hari libur, anggap boleh pulang
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        Log.d("HomeActivity", "isWaktunyaPulang - Jadwal Pulang: " + jadwalPulang + ", Waktu Sekarang: " + currentTime);

        try {
            Date waktuPulang = sdf.parse(jadwalPulang);
            Date waktuSekarang = sdf.parse(currentTime);

            if (waktuPulang != null && waktuSekarang != null) {
                // Return true if current time is equal or after scheduled time (allowing grace period)
                return !waktuSekarang.before(waktuPulang);
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
                // Allow late up to 5 minutes grace period
                return diffInMinutes > 5;
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
                // Toleransi 5 menit keterlambatan
                return diffInMillis > (5 * 60 * 1000); // 5 menit dalam milidetik
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Metode baru untuk menghitung durasi keterlambatan
    private String getDurasiKeterlambatan() {
        String jadwalMasuk = jadwalMasukTextView.getText().toString();
        if (jadwalMasuk.equals("-") || jadwalMasuk.equals("Libur")) {
            return "N/A";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        try {
            Date waktuMasuk = sdf.parse(jadwalMasuk);
            Date waktuSekarang = sdf.parse(currentTime);

            if (waktuMasuk != null && waktuSekarang != null) {
                long diffInMillis = waktuSekarang.getTime() - waktuMasuk.getTime();
                if (diffInMillis <= 0) { // Jika tidak terlambat atau waktu pas
                    return "0 detik";
                }

                long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
                long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);

                if (hours > 0) {
                    return String.format(Locale.getDefault(), "%d jam %d menit", hours, minutes % 60);
                } else if (minutes > 0) {
                    return String.format(Locale.getDefault(), "%d menit %d detik", minutes, seconds % 60);
                } else {
                    return String.format(Locale.getDefault(), "%d detik", seconds);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Tidak dapat dihitung";
    }


    // --- MODIFIKASI DIMULAI DI SINI ---
    private void showTerlambatAbsenMasukDialog() {
        String durasiTerlambat = getDurasiKeterlambatan(); // Dapatkan durasi keterlambatan
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Peringatan Absen Masuk");
        builder.setMessage("Anda terlambat absen masuk sebesar: " + durasiTerlambat + ".\nApakah Anda tetap ingin absen?");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        // Tambahkan tombol "Tetap Absen"
        builder.setNegativeButton("Tetap Absen", (dialog, which) -> {
            Intent cameraIntent = new Intent(HomeActivity.this, ActivityCamera.class);
            cameraIntent.putExtra("absen_type", "masuk");
            startActivity(cameraIntent);
            dialog.dismiss(); // Tutup dialog setelah mengarahkan ke kamera
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showNotInRadiusDialog(String jenisAbsen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Peringatan");
        builder.setMessage("Anda tidak berada dalam radius yang ditentukan untuk " + jenisAbsen + ".");
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

        builder.setPositiveButton("Ya", (dialog, which) -> {
            if (currentLocation != null) { // Pastikan lokasi tersedia
                absenPulang(); // Panggil metode absenPulang yang sekarang akan menggunakan currentLocation
            } else {
                Toast.makeText(HomeActivity.this, "Gagal mendapatkan lokasi untuk absen pulang.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void absenPulang() {
        Log.d("HomeActivity", "absenPulang: Tombol absen pulang ditekan setelah konfirmasi");
        SimpleDateFormat sdfTanggal = new SimpleDateFormat("EEEE, dd MMMM", new Locale("in", "ID")); // Ubah format tanggal agar sesuai dengan yang Anda gunakan di `updateDateTime`
        String tanggalAbsen = sdfTanggal.format(new Date());
        String jamPulang = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());



        // Panggil updateAbsensiPulang dengan lokasiPulang
        updateAbsensiPulang(tanggalAbsen, namaPengguna, jamPulang);

        Toast.makeText(this, "Absen Pulang Berhasil!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity.this, RekapActivity.class);
        startActivity(intent);
        finish(); // Penting untuk mengakhiri activity HomeActivity
    }

    // Perbarui signature metode ini untuk menerima String lokasiPulang
    private void updateAbsensiPulang(String tanggal, String nama, String jamPulang) {
        SharedPreferences userData = getSharedPreferences("user_data", MODE_PRIVATE);
        String nisnPengguna = userData.getString("nisn", "");

        List<AbsensiItem> existingAbsensi = loadAbsensi();
        boolean updated = false;
        for (AbsensiItem item : existingAbsensi) {
            // Periksa tanggal, nama, dan NISN, serta pastikan jam pulang masih kosong
            if (item.getTanggal().equals(tanggal) && item.getNama().equals(nama) && item.getJamPulang().equals("-") && item.getNisn().equals(nisnPengguna)) {
                item.setJamPulang(jamPulang);
                updated = true;
                break;
            }
        }
        if (updated) {
            saveListAbsensi(existingAbsensi);
            sharedPreferences.edit().putBoolean("sudah_absen_datang", false).apply();
            btnAbsenPulang.setEnabled(false);
        } else {
            Toast.makeText(this, "Data absen masuk tidak ditemukan untuk hari ini atau sudah absen pulang.", Toast.LENGTH_SHORT).show();
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
        // Ubah format tanggal untuk menyertakan tahun (contoh: "Selasa, 21 Mei 2025")
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("in", "ID"));
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
        // Lokasi Perumda Air Minum Tugu Tirta Kota Malang
        LatLng tuguTirtaMalang = new LatLng(-7.970889, 112.668351);

        // Tambahkan marker pada lokasi Tugu Tirta
        googleMap.addMarker(new MarkerOptions().position(tuguTirtaMalang).title("Perumda Air Minum Tugu Tirta Kota Malang"));

        // Tambahkan lingkaran radius
        googleMap.addCircle(new CircleOptions()
                .center(tuguTirtaMalang)
                .radius(RADIUS_IN_METERS)
                .fillColor(0x40FF0000) // Warna merah transparan
                .strokeColor(0xFFFF0000) // Warna garis merah
                .strokeWidth(2));

        // Pindahkan kamera ke lokasi Tugu Tirta dengan level zoom tertentu
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tuguTirtaMalang, 15));

        // Aktifkan fitur My Location jika izin diberikan
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLocation = location;
                        Log.d("Location", "Latitude: " + currentLocation.getLatitude() + ", Longitude: " + currentLocation.getLongitude());
                        // Anda bisa menambahkan marker lokasi pengguna jika diperlukan
                        // LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        // googleMap.addMarker(new MarkerOptions().position(userLocation).title("Lokasi Anda"));
                    } else {
                        Toast.makeText(this, "Gagal mendapatkan lokasi. Pastikan GPS aktif.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isWithinRadius(Location userLocation, LatLng center, float radius) {
        float[] results = new float[1];
        Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                center.latitude, center.longitude, results);
        return results[0] <= radius;
    }

    private void updateUserLocationOnMap(LatLng userLocation) {
        if (googleMap != null) {
            googleMap.clear(); // Bersihkan marker dan lingkaran sebelumnya
            // Tambahkan marker baru di lokasi pengguna
            googleMap.addMarker(new MarkerOptions().position(userLocation).title("Lokasi Anda"));
            // Tambahkan lingkaran radius lagi
            googleMap.addCircle(new CircleOptions()
                    .center(SCHOOL_LOCATION) // Gunakan SCHOOL_LOCATION sebagai pusat radius
                    .radius(RADIUS_IN_METERS)
                    .fillColor(0x40FF0000)
                    .strokeColor(0xFFFF0000)
                    .strokeWidth(2));
            // Pindahkan kamera ke lokasi pengguna (opsional, tergantung kebutuhan)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        startUpdatingTime();
        loadJadwalHariIni();

        // Memulai update lokasi saat resume
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        // Periksa kembali status absen datang saat resume
        sudahAbsenDatang = sharedPreferences.getBoolean("sudah_absen_datang", false); // Diubah menjadi false agar konsisten
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
        // Hentikan update lokasi saat pause
        fusedLocationClient.removeLocationUpdates(locationCallback);
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
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Memulai update lokasi setelah izin diberikan
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                }

                // Aktifkan My Location pada map setelah mendapatkan izin
                if (googleMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }
                }
            } else {
                Toast.makeText(this, "Izin lokasi ditolak. Fitur absensi berbasis lokasi tidak dapat digunakan.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

