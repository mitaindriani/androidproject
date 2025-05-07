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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);

        recyclerViewRekap = findViewById(R.id.recyclerViewRekap);
        recyclerViewRekap.setLayoutManager(new LinearLayoutManager(this));
        absensiList = loadAbsensi(); // Load data absensi saat activity dibuat

        adapter = new RekapAdapter(absensiList);
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
        adapter.updateData(absensiList);
    }

    private List<AbsensiItem> loadAbsensi() {
        List<AbsensiItem> absensiList = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(getFileStreamPath("absensi_data.ser"));
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            absensiList = (List<AbsensiItem>) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Jika file tidak ditemukan atau terjadi error lain, kembalikan list kosong
            // e.printStackTrace();
        }
        return absensiList;
    }
}