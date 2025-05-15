package com.example.absensi;

import java.io.Serializable;

public class AbsensiItem implements Serializable {
    private String tanggal;
    private String nama;
    private String jamDatang;
    private String terlambat;
    private String jamPulang;
    private boolean berhasil;
    private byte[] fotoAbsen;
    private String nisn; // Tambahkan atribut NISN

    public AbsensiItem(String tanggal, String nama, String jamDatang, String terlambat, String jamPulang, boolean berhasil, byte[] fotoAbsen, String nisn) {
        this.tanggal = tanggal;
        this.nama = nama;
        this.jamDatang = jamDatang;
        this.terlambat = terlambat;
        this.jamPulang = jamPulang;
        this.berhasil = berhasil;
        this.fotoAbsen = fotoAbsen;
        this.nisn = nisn; // Inisialisasi NISN
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getNama() {
        return nama;
    }

    public String getJamDatang() {
        return jamDatang;
    }

    public String getTerlambat() {
        return terlambat;
    }

    public String getJamPulang() {
        return jamPulang;
    }

    public boolean isBerhasil() {
        return berhasil;
    }

    public byte[] getFotoAbsen() {
        return fotoAbsen;
    }

    public void setJamPulang(String jamPulang) {
        this.jamPulang = jamPulang;
    }

    public String getNisn() {
        return nisn;
    }
}