package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilActivity extends AppCompatActivity {

    private ImageView logoSmknImageView;
    private TextView namaTextView;
    private TextView userIdTextView;
    private ImageView keluarImageView;
    private Button profileButton;
    private Button homeButton;
    private Button rekapButton;
    private ImageView logoutImageView;
    private SharedPreferences sharedPreferences;

    // Declare new TextViews for "Data Siswa" section
    private TextView ttlTextView;
    private TextView noHpTextView;
    private TextView sekolahTextView;
    private TextView jurusanTextView;
    private TextView kelasPeminatanTextView;
    private TextView emailTextView; // Assuming you'll add an email TextView if not already there

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Corrected layout file name to activity_profil

        // Initialize Shared Preferences
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        // --- Header Section Initialization ---
        // logoSmknImageView = findViewById(R.id.logo_smkn); // Not used currently
        logoutImageView = findViewById(R.id.logoutImageView); // Use this one for the logout button in header

        // --- Profile Info Section Initialization ---
        namaTextView = findViewById(R.id.userNameTextView);
        userIdTextView = findViewById(R.id.userIdTextView); // This is for NISN display

        // --- "Data Siswa" Section Initialization ---
        // You need to add IDs for these TextViews in your activity_profil.xml if they don't exist
        // As per your XML, you have 'jurusanTextView' already, but not 'ttl', 'noHp', 'sekolah', 'kelasPeminatan', 'email'
        // For demonstration, I'll use IDs you provided, and placeholder for others:
        // You might need to add these IDs to your activity_profil.xml:
        // android:id="@+id/ttlTextView"
        // android:id="@+id/noHpTextView"
        // android:id="@+id/sekolahTextView"
        // android:id="@+id/kelasPeminatanTextView"
        // android:id="@+id/emailTextView" // If you add one for email

        ttlTextView = findViewById(R.id.ttlTextView); // You need to add this in XML
        noHpTextView = findViewById(R.id.noHpTextView); // You need to add this in XML
        sekolahTextView = findViewById(R.id.sekolahTextView); // You need to add this in XML
        jurusanTextView = findViewById(R.id.jurusanTextView); // Already exists in your XML
        kelasPeminatanTextView = findViewById(R.id.kelasPeminatanTextView); // You need to add this in XML
        // If you add an email TextView in your XML:
        // emailTextView = findViewById(R.id.emailTextView); // Add this if you put email in the layout

        // --- Retrieve and Set User Data from SharedPreferences ---
        String userName = sharedPreferences.getString("nama", "Nama Tidak Ditemukan");
        String userNisn = sharedPreferences.getString("nisn", "NISN Tidak Ditemukan");
        String userKelas = sharedPreferences.getString("kelas", "Kelas Tidak Ditemukan");
        String userJurusan = sharedPreferences.getString("jurusan", "Jurusan Tidak Ditemukan");
        String userEmail = sharedPreferences.getString("email", "Email Tidak Ditemukan");

        // Set values for the profile section
        namaTextView.setText(userName);
        // You have "XI RPL 2\n19740516 199705 1 001" as example for userIdTextView
        // Adjust this to display kelas and nisn from SharedPreferences
        userIdTextView.setText(userKelas + "\n" + userNisn);

        // Set values for the "Data Siswa" section
        // Note: You might not be collecting TTL, No HP, Sekolah, or Kelas Peminatan yet.
        // For those, you'd need to add them to your registration flow and database.
        // For now, I'll use the dummy data or the email/jurusan you ARE collecting.

        // TTL and No HP are not in your current registration flow.
        // You'll need to decide if you want to add them to Register2Activity
        // or hardcode them, or leave them blank.
        ttlTextView.setText("Malang, 4 Maret 2000"); // Example dummy data
        noHpTextView.setText("089333827674");      // Example dummy data
        sekolahTextView.setText("SMK Negeri 1 Kepanjen"); // Example dummy data

        // Display Jurusan and Email from registration
        jurusanTextView.setText(userJurusan);
        kelasPeminatanTextView.setText(userKelas); // Assuming 'kelasPeminatan' refers to the 'kelas' field
        // If you add an email TextView:
        // emailTextView.setText(userEmail);

        // --- Logout Button Listener (Header) ---
        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear user data
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Toast.makeText(ProfilActivity.this, "Berhasil Logout", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // --- Bottom Navigation Listeners ---
        profileButton = findViewById(R.id.profile_button);
        homeButton = findViewById(R.id.home_button);
        rekapButton = findViewById(R.id.rekap_button);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfilActivity.this, "Anda sudah di Profil", Toast.LENGTH_SHORT).show();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        rekapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, RekapActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}