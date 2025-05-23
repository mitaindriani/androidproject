package com.example.absensi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "absensi_db";
    private static final int DATABASE_VERSION = 2; // IMPORTANT: Increment to force onUpgrade

    // Table Name
    private static final String TABLE_USERS = "users";

    // Column Names for Users Table
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAMA = "nama";
    private static final String COLUMN_NISN = "nisn";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_KELAS = "kelas";     // NEW COLUMN
    private static final String COLUMN_JURUSAN = "jurusan"; // NEW COLUMN
    private static final String COLUMN_EMAIL = "email";     // NEW COLUMN

    // SQL statement to create the users table
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAMA + " TEXT NOT NULL,"
                    + COLUMN_NISN + " TEXT UNIQUE NOT NULL," // NISN should be unique
                    + COLUMN_PASSWORD + " TEXT NOT NULL,"
                    + COLUMN_KELAS + " TEXT,"     // Added new columns
                    + COLUMN_JURUSAN + " TEXT,"   // Added new columns
                    + COLUMN_EMAIL + " TEXT"      // Added new columns
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
        // This is a basic upgrade strategy: drop and recreate.
        // In a real production app, you'd implement a more robust migration
        // that preserves existing user data.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Method to add a new user to the database with all fields
    public long addUser(String nama, String nisn, String password, String kelas, String jurusan, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA, nama);
        values.put(COLUMN_NISN, nisn);
        values.put(COLUMN_PASSWORD, password); // Warning: Storing passwords in plain text is insecure!
        values.put(COLUMN_KELAS, kelas);
        values.put(COLUMN_JURUSAN, jurusan);
        values.put(COLUMN_EMAIL, email);

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Method to check if a NISN is already registered
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

    // Method to verify login and retrieve complete User data
    public User getUser(String nisn, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_ID,
                COLUMN_NAMA,
                COLUMN_NISN,
                COLUMN_KELAS,
                COLUMN_JURUSAN,
                COLUMN_EMAIL
        };
        String selection = COLUMN_NISN + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {nisn, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA));
            String nisn_db = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NISN));
            String kelas = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KELAS));
            String jurusan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JURUSAN));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));

            // Create a User object with all retrieved data
            user = new User(id, nama, nisn_db, kelas, jurusan, email);
            cursor.close();
        }
        db.close();
        return user;
    }

    // Inner class User to represent user data (updated to hold all fields)
    public static class User {
        private int id;
        private String nama;
        private String nisn;
        private String kelas;
        private String jurusan;
        private String email;

        // Constructor for the User object
        public User(int id, String nama, String nisn, String kelas, String jurusan, String email) {
            this.id = id;
            this.nama = nama;
            this.nisn = nisn;
            this.kelas = kelas;
            this.jurusan = jurusan;
            this.email = email;
        }

        // Getters for all user properties
        public int getId() {
            return id;
        }

        public String getNama() {
            return nama;
        }

        public String getNisn() {
            return nisn;
        }

        public String getKelas() {
            return kelas;
        }

        public String getJurusan() {
            return jurusan;
        }

        public String getEmail() {
            return email;
        }
    }
}