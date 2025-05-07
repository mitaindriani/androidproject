package com.example.absensi; // Ganti dengan package aplikasi Anda

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "absensi_db";
    private static final int DATABASE_VERSION = 1;

    // Nama Tabel
    private static final String TABLE_USERS = "users";

    // Kolom Tabel Users
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAMA = "nama";
    private static final String COLUMN_NISN = "nisn";
    private static final String COLUMN_PASSWORD = "password";

    // Perintah SQL untuk membuat tabel users
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAMA + " TEXT NOT NULL,"
                    + COLUMN_NISN + " TEXT UNIQUE NOT NULL,"
                    + COLUMN_PASSWORD + " TEXT NOT NULL"
                    + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Jika ada perubahan skema database, tambahkan logika migrasi di sini
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Metode untuk menambahkan user baru ke database
    public long addUser(String nama, String nisn, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA, nama);
        values.put(COLUMN_NISN, nisn);
        values.put(COLUMN_PASSWORD, password); // Perhatikan: Ini menyimpan password dalam plain text. Jangan lakukan ini di aplikasi produksi.

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Metode untuk memeriksa apakah NISN sudah terdaftar
    public boolean checkUser(String nisn) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_NISN + " = ?";
        String[] selectionArgs = {nisn};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    // Metode untuk memverifikasi login dan mendapatkan data User
    public User getUser(String nisn, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_NAMA, COLUMN_NISN}; // Tidak perlu mengambil password lagi
        String selection = COLUMN_NISN + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {nisn, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA));
            String nisn_db = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NISN));
            user = new User(id, nama, nisn_db);
            cursor.close();
        }
        db.close();
        return user;
    }

    // Inner class User to represent user data
    public static class User {
        private int id;
        private String nama;
        private String nisn;

        public User(int id, String nama, String nisn) {
            this.id = id;
            this.nama = nama;
            this.nisn = nisn;
        }

        public int getId() {
            return id;
        }

        public String getNama() {
            return nama;
        }

        public String getNisn() {
            return nisn;
        }
    }
}
