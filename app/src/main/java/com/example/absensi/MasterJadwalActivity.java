package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MasterJadwalActivity extends AppCompatActivity {

    private Button btnSimpanJadwal;
    private ImageView logoutImageView;
    private TextView text_smkn;
    private ImageView logo_smkn;

    private EditText seninMasukEditText;
    private EditText seninPulangEditText;
    private EditText selasaMasukEditText;
    private EditText selasaPulangEditText;
    private EditText rabuMasukEditText;
    private EditText rabuPulangEditText;
    private EditText kamisMasukEditText;
    private EditText kamisPulangEditText;
    private EditText jumatMasukEditText;
    private EditText jumatPulangEditText;

    private SharedPreferences sharedPreferences;
    private static final String JADWAL_PREFS = "master_jadwal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_jadwal);

        btnSimpanJadwal = findViewById(R.id.btnSimpanJadwal);
        logoutImageView = findViewById(R.id.logoutImageView);
        text_smkn = findViewById(R.id.text_smkn);
        logo_smkn = findViewById(R.id.logo_smkn);

        seninMasukEditText = findViewById(R.id.seninMasukEditText);
        seninPulangEditText = findViewById(R.id.seninPulangEditText);
        selasaMasukEditText = findViewById(R.id.selasaMasukEditText);
        selasaPulangEditText = findViewById(R.id.selasaPulangEditText);
        rabuMasukEditText = findViewById(R.id.rabuMasukEditText);
        rabuPulangEditText = findViewById(R.id.rabuPulangEditText);
        kamisMasukEditText = findViewById(R.id.kamisMasukEditText);
        kamisPulangEditText = findViewById(R.id.kamisPulangEditText);
        jumatMasukEditText = findViewById(R.id.jumatMasukEditText);
        jumatPulangEditText = findViewById(R.id.jumatPulangEditText);

        sharedPreferences = getSharedPreferences(JADWAL_PREFS, MODE_PRIVATE);

        // Load jadwal yang tersimpan saat Activity dibuat
        loadJadwal();

        btnSimpanJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanJadwal();
            }
        });

        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void loadJadwal() {
        seninMasukEditText.setText(sharedPreferences.getString("senin_masuk", "08:00"));
        seninPulangEditText.setText(sharedPreferences.getString("senin_pulang", "17:00"));
        selasaMasukEditText.setText(sharedPreferences.getString("selasa_masuk", "08:00"));
        selasaPulangEditText.setText(sharedPreferences.getString("selasa_pulang", "17:00"));
        rabuMasukEditText.setText(sharedPreferences.getString("rabu_masuk", "08:00"));
        rabuPulangEditText.setText(sharedPreferences.getString("rabu_pulang", "17:00"));
        kamisMasukEditText.setText(sharedPreferences.getString("kamis_masuk", "08:00"));
        kamisPulangEditText.setText(sharedPreferences.getString("kamis_pulang", "17:00"));
        jumatMasukEditText.setText(sharedPreferences.getString("jumat_masuk", "08:00"));
        jumatPulangEditText.setText(sharedPreferences.getString("jumat_pulang", "17:00"));
    }

    private void simpanJadwal() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("senin_masuk", seninMasukEditText.getText().toString());
        editor.putString("senin_pulang", seninPulangEditText.getText().toString());
        editor.putString("selasa_masuk", selasaMasukEditText.getText().toString());
        editor.putString("selasa_pulang", selasaPulangEditText.getText().toString());
        editor.putString("rabu_masuk", rabuMasukEditText.getText().toString());
        editor.putString("rabu_pulang", rabuPulangEditText.getText().toString());
        editor.putString("kamis_masuk", kamisMasukEditText.getText().toString());
        editor.putString("kamis_pulang", kamisPulangEditText.getText().toString());
        editor.putString("jumat_masuk", jumatMasukEditText.getText().toString());
        editor.putString("jumat_pulang", jumatPulangEditText.getText().toString());
        editor.apply();
        Toast.makeText(this, "Jadwal berhasil disimpan", Toast.LENGTH_SHORT).show();

        // Opsional: Kembali ke halaman sebelumnya atau tetap di halaman edit
        // finish();
    }

    private void logout() {
        // Hapus data login dari SharedPreferences
        SharedPreferences userData = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = userData.edit();
        editor.clear();
        editor.apply();

        // Pindah ke LoginActivity
        Intent intent = new Intent(MasterJadwalActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}