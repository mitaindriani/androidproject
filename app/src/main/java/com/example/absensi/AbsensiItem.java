package com.example.absensi;

import java.io.Serializable; // Penting untuk menyimpan objek ke file
import java.util.Arrays;

public class AbsensiItem implements Serializable {

    // Pastikan semua variabel ini ada dan sesuai dengan yang Anda gunakan
    private String tanggal;
    private String nama;
    private String jamMasuk;
    private String waktuTerlambat;
    private String jamPulang;
    private boolean statusKehadiran; // Asumsi ini adalah parameter boolean yang Anda maksud
    private byte[] fotoAbsen;
    private String nisn;
    private String lokasiMasuk; // Untuk lokasi masuk
    private String lokasiPulang; // Untuk lokasi pulang

    // Konstruktor yang cocok dengan panggilan di KonfirAbsenActivity
    public AbsensiItem(String tanggal, String nama, String jamMasuk, String waktuTerlambat,
                       String jamPulang, boolean statusKehadiran, byte[] fotoAbsen,
                       String nisn, String lokasiMasuk) {
        this.tanggal = tanggal;
        this.nama = nama;
        this.jamMasuk = jamMasuk;
        this.waktuTerlambat = waktuTerlambat;
        this.jamPulang = jamPulang; // Awalnya mungkin "-"
        this.statusKehadiran = statusKehadiran;
        this.fotoAbsen = fotoAbsen;
        this.nisn = nisn;
        this.lokasiMasuk = lokasiMasuk;
        this.lokasiPulang = "-"; // Inisialisasi lokasiPulang secara default
    }

    // --- Getter Methods ---
    public String getTanggal() {
        return tanggal;
    }

    public String getNama() {
        return nama;
    }

    public String getJamMasuk() {
        return jamMasuk;
    }

    public String getWaktuTerlambat() {
        return waktuTerlambat;
    }

    public String getJamPulang() {
        return jamPulang;
    }

    public boolean isStatusKehadiran() {
        return statusKehadiran;
    }

    public byte[] getFotoAbsen() {
        return fotoAbsen;
    }

    public String getNisn() {
        return nisn;
    }

    public String getLokasiMasuk() {
        return lokasiMasuk;
    }

    public String getLokasiPulang() {
        return lokasiPulang;
    }

    // --- Setter Methods (penting untuk update absen pulang) ---
    public void setJamPulang(String jamPulang) {
        this.jamPulang = jamPulang;
    }

    public void setLokasiPulang(String lokasiPulang) {
        this.lokasiPulang = lokasiPulang;
    }

    public void setStatusKehadiran(boolean statusKehadiran) {
        this.statusKehadiran = statusKehadiran;
    }

    // Metode toString() untuk debugging (opsional)
    @Override
    public String toString() {
        return "AbsensiItem{" +
                "tanggal='" + tanggal + '\'' +
                ", nama='" + nama + '\'' +
                ", jamMasuk='" + jamMasuk + '\'' +
                ", waktuTerlambat='" + waktuTerlambat + '\'' +
                ", jamPulang='" + jamPulang + '\'' +
                ", statusKehadiran=" + statusKehadiran +
                ", fotoAbsen=" + Arrays.toString(fotoAbsen) +
                ", nisn='" + nisn + '\'' +
                ", lokasiMasuk='" + lokasiMasuk + '\'' +
                ", lokasiPulang='" + lokasiPulang + '\'' +
                '}';
    }
}
