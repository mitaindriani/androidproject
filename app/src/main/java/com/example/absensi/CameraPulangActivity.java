package com.example.absensi; // Ganti dengan nama package aplikasi Anda

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraPulangActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "CameraActivity";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 101;

    private SurfaceView cameraPreview;
    private SurfaceHolder surfaceHolder;
    private android.hardware.Camera camera;
    private Button captureButton;
    private String currentPhotoPath;
    private BottomNavigationView bottomNavigationView;
    private String absenType; // Untuk menyimpan jenis absensi (masuk/pulang)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerapulang);

        cameraPreview = findViewById(R.id.camera_preview);
        captureButton = findViewById(R.id.capture_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation_camera);

        surfaceHolder = cameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Mendapatkan jenis absensi dari Intent
        absenType = getIntent().getStringExtra("absen_type");
        if (absenType != null) {
            Log.d(TAG, "Jenis Absensi: " + absenType);
            // Anda bisa menampilkan ini di UI jika perlu
        }

        // Memeriksa dan meminta izin kamera jika belum diberikan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Izin sudah diberikan, inisialisasi kamera
            initializeCamera();
        }

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });


    }

    private void initializeCamera() {
        try {
            camera = android.hardware.Camera.open();
            camera.setDisplayOrientation(90); // Sesuaikan orientasi jika perlu
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            Log.d(TAG, "Kamera diinisialisasi");
        } catch (IOException | RuntimeException e) {
            Log.e(TAG, "Gagal menginisialisasi atau memulai kamera: " + e.getMessage());
            Toast.makeText(this, "Gagal membuka kamera.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            Log.d(TAG, "Kamera dilepas");
        }
    }

    private void takePhoto() {
        if (camera != null) {
            camera.takePicture(null, null, new android.hardware.Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                    File photoFile = createImageFile();
                    if (photoFile != null) {
                        // Simpan byte data gambar ke file
                        try {
                            java.io.FileOutputStream fos = new java.io.FileOutputStream(photoFile);
                            fos.write(data);
                            fos.close();
                            Log.d(TAG, "Foto berhasil disimpan di: " + photoFile.getAbsolutePath());

                            // Kirim path foto ke KonfirActivity
                            Intent intent = new Intent(CameraPulangActivity.this, KonfirAbsenActivity.class);
                            intent.putExtra("photoPath", photoFile.getAbsolutePath());
                            startActivity(intent);
                            // Tidak perlu finish() di sini agar pengguna bisa kembali ke kamera jika perlu

                        } catch (java.io.IOException e) {
                            Log.e(TAG, "Gagal menyimpan foto: " + e.getMessage());
                            Toast.makeText(CameraPulangActivity.this, "Gagal menyimpan foto.", Toast.LENGTH_SHORT).show();
                        }
                        // Restart preview kamera
                        camera.startPreview();
                    } else {
                        Log.e(TAG, "Gagal membuat file untuk menyimpan foto.");
                        Toast.makeText(CameraPulangActivity.this, "Gagal menyimpan foto.", Toast.LENGTH_SHORT).show();
                        camera.startPreview();
                    }
                }
            });
        }
    }

    private File createImageFile() {
        // Buat nama file gambar yang unik
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin kamera diberikan, inisialisasi kamera
                initializeCamera();
            } else {
                // Izin kamera ditolak, tampilkan pesan atau arahkan pengguna kembali
                Toast.makeText(this, "Izin kamera diperlukan untuk mengambil foto absensi.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // Surface dibuat, inisialisasi kamera di sini jika izin sudah diberikan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initializeCamera();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // Jika terjadi perubahan pada surface
        if (camera != null) {
            try {
                camera.stopPreview();
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                Log.e(TAG, "Gagal mengatur preview setelah perubahan surface: " + e.getMessage());
            }
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // Surface dihancurkan, lepaskan kamera
        releaseCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Periksa kembali izin kamera saat activity resume
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && surfaceHolder.getSurface() != null) {
            initializeCamera();
        }
    }
}