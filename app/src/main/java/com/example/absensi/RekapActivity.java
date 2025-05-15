package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.SharedPreferences;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class RekapActivity extends AppCompatActivity {

    private Button profileNav;
    private Button homeNav;
    private Button rekapNav;
    private RecyclerView recyclerViewRekap;
    private List<AbsensiItem> absensiList;
    private RekapAdapter adapter;
    private ImageView logoutImageView;
    private static final String ABSENSI_FILE = "absensi_data.ser";
    private static final String USER_DATA = "user_data";  // Tambahkan konstanta untuk SharedPreferences
    private static final String KEY_NISN = "nisn";      // Tambahkan konstanta untuk key NISN

    private SharedPreferences sharedPreferences;
    private String nisnPengguna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences(USER_DATA, MODE_PRIVATE);
        nisnPengguna = sharedPreferences.getString(KEY_NISN, "NISN Tidak Ditemukan"); // Ambil NISN pengguna yang login

        recyclerViewRekap = findViewById(R.id.recyclerViewRekap);
        recyclerViewRekap.setLayoutManager(new LinearLayoutManager(this));
        absensiList = loadAbsensi(); // Load data absensi saat activity dibuat

        // Filter data absensi untuk menampilkan hanya data pengguna yang login
        List<AbsensiItem> filteredList = filterAbsensiByNisn(absensiList, nisnPengguna);
        adapter = new RekapAdapter(filteredList);
        recyclerViewRekap.setAdapter(adapter);

        LinearLayout bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null && bottomNavigation.getChildCount() == 3) {
            profileNav = (Button) bottomNavigation.getChildAt(0);
            homeNav = (Button) bottomNavigation.getChildAt(1);
            rekapNav = (Button) bottomNavigation.getChildAt(2);
            logoutImageView = findViewById(R.id.logoutImageView);

            profileNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RekapActivity.this, ProfilActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            homeNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RekapActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            rekapNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RekapActivity.this, "Anda sudah berada di halaman Rekap", Toast.LENGTH_SHORT).show();
                }
            });

            logoutImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RekapActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data absensi setiap kali activity resume agar data terbaru ditampilkan
        absensiList = loadAbsensi();
        List<AbsensiItem> filteredList = filterAbsensiByNisn(absensiList, nisnPengguna); // Filter data
        adapter.updateData(filteredList);
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

    // Metode untuk memfilter data absensi berdasarkan NISN pengguna
    private List<AbsensiItem> filterAbsensiByNisn(List<AbsensiItem> absensiList, String nisn) {
        List<AbsensiItem> filteredList = new ArrayList<>();
        for (AbsensiItem item : absensiList) {
            if (item.getNisn().equals(nisn)) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }
}