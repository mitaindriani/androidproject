package com.example.absensi;

public class AbsensiItem {
    private String tanggal;
    private String nama;
    private String jamDatang;
    private String terlambat;
    private String jamPulang;
    private boolean berhasil;

    public AbsensiItem(String tanggal, String nama, String jamDatang, String terlambat, String jamPulang, boolean berhasil) {
        this.tanggal = tanggal;
        this.nama = nama;
        this.jamDatang = jamDatang;
        this.terlambat = terlambat;
        this.jamPulang = jamPulang;
        this.berhasil = berhasil;
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
}