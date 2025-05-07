package com.example.absensi; // Ganti dengan package aplikasi Anda

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ActivityCamera extends AppCompatActivity {

    private static final String TAG = "ActivityCamera";
    private TextureView textureView;
    private Button btnCapture;
    private Button btnCancel;
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size previewSize;
    private ImageReader imageReader;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private String absenType;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        textureView = findViewById(R.id.textureView);
        btnCapture = findViewById(R.id.btnCapture);
        btnCancel = findViewById(R.id.btnCancel);

        textureView.setSurfaceTextureListener(textureListener);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        absenType = getIntent().getStringExtra("absen_type");
        if (absenType == null) {
            absenType = "tidak diketahui";
            Log.e(TAG, "onCreate: Jenis absensi tidak diterima dari HomeActivity");
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.d(TAG, "onSurfaceTextureAvailable: SurfaceTexture tersedia");
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.d(TAG, "onSurfaceTextureSizeChanged: Ukuran SurfaceTexture berubah menjadi " + width + "x" + height);
            // Tidak perlu implementasi khusus saat ini, penyesuaian rasio aspek dilakukan saat kamera dibuka
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            Log.d(TAG, "onSurfaceTextureDestroyed: SurfaceTexture dihancurkan");
            return true; // Release resources
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            // Tidak perlu implementasi khusus saat ini
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.e(TAG, "onOpened: Kamera dibuka dengan ID: " + camera.getId());
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.e(TAG, "onDisconnected: Kamera dengan ID: " + camera.getId() + " terputus");
            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "onError: Terjadi error " + error + " pada kamera dengan ID: " + camera.getId());
            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
        }
    };

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        Log.d(TAG, "startBackgroundThread: Background thread dimulai");
    }

    protected void stopBackgroundThread() {
        if (mBackgroundThread != null) {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
                Log.d(TAG, "stopBackgroundThread: Background thread dihentikan");
            } catch (InterruptedException e) {
                Log.e(TAG, "stopBackgroundThread: Error menghentikan background thread", e);
                e.printStackTrace();
            }
        }
    }

    private void openCamera(int width, int height) {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String frontCameraId = null;
            String[] cameraIds = manager.getCameraIdList();
            for (String id : cameraIds) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontCameraId = id;
                    break;
                }
            }

            String selectedCameraId = frontCameraId != null ? frontCameraId : (cameraIds.length > 0 ? cameraIds[0] : null);
            if (selectedCameraId == null) {
                Log.e(TAG, "openCamera: Tidak ada ID kamera yang tersedia");
                return;
            }
            cameraId = selectedCameraId;
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map == null) {
                Log.e(TAG, "openCamera: StreamConfigurationMap is null untuk kamera ID: " + cameraId);
                return;
            }
            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea()
            );
            imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);

            Size[] previewSizes = map.getOutputSizes(SurfaceTexture.class);
            if (previewSizes == null || previewSizes.length == 0) {
                Log.e(TAG, "openCamera: Tidak ada ukuran pratinjau yang didukung untuk kamera ID: " + cameraId);
                return;
            }
            previewSize = chooseOptimalSize(previewSizes, width, height, largest);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                Log.w(TAG, "openCamera: Izin kamera belum diberikan");
                return;
            }
            Log.d(TAG, "openCamera: Membuka kamera dengan ID: " + cameraId + ", pratinjau: " + previewSize.getWidth() + "x" + previewSize.getHeight() + ", gambar: " + largest.getWidth() + "x" + largest.getHeight());
            manager.openCamera(cameraId, stateCallback, mBackgroundHandler);
            transformImage(width, height);

        } catch (CameraAccessException e) {
            Log.e(TAG, "openCamera: Tidak dapat mengakses kamera", e);
            e.printStackTrace();
        } catch (SecurityException e) {
            Log.e(TAG, "openCamera: Izin kamera ditolak oleh sistem", e);
            e.printStackTrace();
        }
    }

    private Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            if (texture == null) {
                Log.e(TAG, "createCameraPreview: SurfaceTexture is null");
                return;
            }
            if (cameraDevice == null) {
                Log.e(TAG, "createCameraPreview: cameraDevice is null, tidak dapat membuat pratinjau");
                return;
            }
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            final CameraDevice currentCameraDevice = cameraDevice; // Tambahkan ini
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (currentCameraDevice == null) { // Gunakan currentCameraDevice
                        return;
                    }
                    cameraCaptureSessions = session;
                    updatePreview();
                    Log.d(TAG, "onConfigured: Sesi pratinjau dikonfigurasi");
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(ActivityCamera.this, "Konfigurasi pratinjau gagal", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onConfigureFailed: Konfigurasi pratinjau gagal");
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "createCameraPreview: Error membuat sesi pratinjau", e);
            e.printStackTrace();
        }
    }

    protected void updatePreview() {
        if (cameraDevice == null) {
            Log.e(TAG, "updatePreview: cameraDevice is null");
            return;
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
            Log.d(TAG, "updatePreview: Pratinjau diperbarui");
        } catch (CameraAccessException e) {
            Log.e(TAG, "updatePreview: Error mengatur permintaan berulang untuk pratinjau", e);
            e.printStackTrace();
        }
    }

    private void takePicture() {
        if (cameraDevice == null) {
            Log.e(TAG, "takePicture: cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map != null) {
                    jpegSizes = map.getOutputSizes(ImageFormat.JPEG);
                } else {
                    Log.e(TAG, "takePicture: StreamConfigurationMap is null");
                    return;
                }
            } else {
                Log.e(TAG, "takePicture: CameraCharacteristics is null");
                return;
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        // Kompres bitmap
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                        byte[] compressedBytes = stream.toByteArray();

                        Intent konfirmasiIntent = new Intent(ActivityCamera.this, KonfirmasiAbsenActivity.class);
                        konfirmasiIntent.putExtra("image_bytes", compressedBytes);
                        konfirmasiIntent.putExtra("absen_type", absenType);
                        // Tambahkan informasi orientasi pengambilan gambar
                        konfirmasiIntent.putExtra("camera_orientation", ORIENTATIONS.get(rotation));
                        startActivity(konfirmasiIntent);
                        finish();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
            };

            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(ActivityCamera.this, "Foto diambil", Toast.LENGTH_SHORT).show();
                    // Tutup sesi pengambilan gambar
                    closeCaptureSession();
                    // Buka kembali kamera dan buat pratinjau setelah penundaan
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            openCamera(textureView.getWidth(), textureView.getHeight());
                        }
                    }, 1000); // Coba 1 detik// Coba penundaan
                }};

            // Tutup sesi pratinjau sebelum membuat sesi pengambilan gambar
            closePreviewSession();

            if (cameraDevice != null) {
                cameraDevice.createCaptureSession(Arrays.asList(reader.getSurface(), new Surface(textureView.getSurfaceTexture())),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                try {
                                    session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                                    Log.d(TAG, "onConfigured (takePicture): Pengambilan gambar dikonfigurasi");
                                } catch (CameraAccessException e) {
                                    Log.e(TAG, "onConfigured (takePicture): Error selama pengambilan gambar", e);
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                Toast.makeText(ActivityCamera.this, "Gagal mengkonfigurasi pengambilan gambar", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onConfigureFailed (takePicture): Gagal mengkonfigurasi pengambilan gambar");
                            }
                        }, mBackgroundHandler
                );
            } else {
                Log.e(TAG, "takePicture: Tidak dapat membuat sesi pengambilan gambar, cameraDevice is null");
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "takePicture: Error mengambil gambar", e);
            e.printStackTrace();
        }
    }

    private void closePreviewSession() {
        if (cameraCaptureSessions != null) {
            cameraCaptureSessions.close();
            cameraCaptureSessions = null;
            Log.d(TAG, "closePreviewSession: Sesi pratinjau ditutup");
        }
    }

    private void closeCaptureSession() {
        // Saat ini, kita menggunakan 'cameraCaptureSessions' untuk pratinjau.
        // Jika Anda membuat sesi pengambilan gambar terpisah, pastikan untuk menutupnya di sini.
        // Dalam implementasi saat ini, kita menutup sesi pratinjau di closePreviewSession.
    }

    private void transformImage(int viewWidth, int viewHeight) {
        if (previewSize == null || textureView == null) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin kamera diberikan", Toast.LENGTH_SHORT).show();
                openCamera(textureView.getWidth(), textureView.getHeight());
            } else {
                Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            Log.d(TAG, "onResume: TextureView tersedia");
            openCamera(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void closeCamera() {
        try {
            if (cameraCaptureSessions != null) {
                cameraCaptureSessions.close();
                cameraCaptureSessions = null;
                Log.d(TAG, "closeCamera: Sesi pengambilan gambar ditutup");
            }
            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
                Log.d(TAG, "closeCamera: Perangkat kamera ditutup");
            }
            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
                Log.d(TAG, "closeCamera: ImageReader ditutup");
            }
        } catch (Exception e) {
            Log.e(TAG, "closeCamera: Error saat menutup kamera", e);
            e.printStackTrace();
        }
    }
}