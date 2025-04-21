package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RekapActivity extends AppCompatActivity {

    private LinearLayout profileNav;
    private LinearLayout homeNav;
    private LinearLayout rekapNav;
    private RecyclerView recyclerViewRekap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);

        // Inisialisasi RecyclerView
        recyclerViewRekap = findViewById(R.id.recyclerViewRekap);
        recyclerViewRekap.setLayoutManager(new LinearLayoutManager(this));

        // Contoh data - ganti dengan data sesungguhnya dari database Anda
        List<AbsensiItem> absensiList = new ArrayList<>();
        absensiList.add(new AbsensiItem(
                "Jumat 10 Maret 2025",
                "NAM YOON SOO",
                "07:16:34",
                "00:00:00",
                "-",
                true
        ));
        absensiList.add(new AbsensiItem(
                "Kamis 09 Maret 2025",
                "NAM YOON SOO",
                "07:16:34",
                "00:00:00",
                "17:53:20",
                true
        ));
        absensiList.add(new AbsensiItem(
                "Rabu 08 Maret 2025",
                "NAM YOON SOO",
                "07:16:34",
                "00:00:00",
                "17:53:20",
                true
        ));

        // Set adapter
        RekapAdapter adapter = new RekapAdapter(absensiList);
        recyclerViewRekap.setAdapter(adapter);

        // Bottom Navigation
        LinearLayout bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null && bottomNavigation.getChildCount() == 3) {
            profileNav = (LinearLayout) bottomNavigation.getChildAt(0);
            homeNav = (LinearLayout) bottomNavigation.getChildAt(1);
            rekapNav = (LinearLayout) bottomNavigation.getChildAt(2);

            profileNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RekapActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
                    // Tambahkan intent atau navigasi ke halaman Profile
                }
            });

            homeNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Kembali ke MainActivity atau halaman Home
                }
            });

            rekapNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Saat ini berada di halaman Rekap
                }
            });
        }
    }
}