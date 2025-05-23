package com.example.absensi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RekapAdapter extends RecyclerView.Adapter<RekapAdapter.RekapViewHolder> {

    private List<AbsensiItem> absensiList;

    public RekapAdapter(List<AbsensiItem> absensiList) {
        this.absensiList = absensiList;
    }

    /**
     * Metode untuk memperbarui data di adapter dan memberitahu RecyclerView untuk menggambar ulang.
     * Berguna saat data absensi berubah (misalnya, setelah fetch dari database).
     * @param newList Daftar AbsensiItem yang baru.
     */
    public void updateData(List<AbsensiItem> newList) {
        this.absensiList = newList;
        notifyDataSetChanged(); // Memberitahu RecyclerView bahwa data telah berubah
    }

    @NonNull
    @Override
    public RekapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Meng-inflate layout item_absensi.xml untuk setiap item di RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_absensi, parent, false);
        return new RekapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RekapViewHolder holder, int position) {
        // Mendapatkan objek AbsensiItem pada posisi saat ini
        AbsensiItem item = absensiList.get(position);

        // Mengatur teks untuk TextViews
        holder.textTanggal.setText("" + item.getTanggal());
        holder.textNama.setText("Nama: " + item.getNama());
        holder.textJamDatang.setText("Jam Masuk: " + item.getJamMasuk());
        holder.textWaktuTerlambat.setText("Waktu Terlambat: " + item.getWaktuTerlambat());
        holder.textJamPulang.setText("Jam Pulang: " + item.getJamPulang());

        // Menangani tampilan foto absensi
        if (item.getFotoAbsen() != null && item.getFotoAbsen().length > 0) {
            // Mengubah byte array menjadi Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(item.getFotoAbsen(), 0, item.getFotoAbsen().length);
            // Mengatur Bitmap ke ImageView
            // Baris ini adalah baris 61 yang menyebabkan NullPointerException sebelumnya
            holder.imageViewFotoAbsen.setImageBitmap(bitmap);
            holder.imageViewFotoAbsen.setVisibility(View.VISIBLE);
        } else {
            // Jika tidak ada foto, sembunyikan ImageView
            holder.imageViewFotoAbsen.setVisibility(View.GONE);
            // Opsional: Anda bisa mengatur placeholder atau gambar default di sini
            // holder.imageViewFotoAbsen.setImageResource(R.drawable.placeholder_image);
        }
    }

    @Override
    public int getItemCount() {
        // Mengembalikan jumlah total item dalam daftar absensi
        return absensiList.size();
    }

    /**
     * ViewHolder adalah kelas pembantu yang menyimpan referensi ke View dalam setiap item layout.
     * Ini membantu dalam efisiensi RecyclerView dengan menghindari findViewById() berulang kali.
     */
    public static class RekapViewHolder extends RecyclerView.ViewHolder {
        // Deklarasi TextViews dan ImageView yang ada di item_absensi.xml
        TextView textTanggal;
        TextView textNama;
        TextView textJamDatang;
        TextView textWaktuTerlambat;
        TextView textJamPulang;
        ImageView imageViewFotoAbsen; // Ini adalah ImageView yang sebelumnya bermasalah

        public RekapViewHolder(@NonNull View itemView) {
            super(itemView);
            // Menginisialisasi setiap View dengan mencari ID-nya dari itemView
            textTanggal = itemView.findViewById(R.id.textTanggal);
            textNama = itemView.findViewById(R.id.textNama);
            textJamDatang = itemView.findViewById(R.id.textJamDatang);
            textWaktuTerlambat = itemView.findViewById(R.id.textWaktuTerlambat);
            textJamPulang = itemView.findViewById(R.id.textJamPulang);
            // PERBAIKAN: Pastikan ID ini cocok dengan yang ada di item_absensi.xml
            imageViewFotoAbsen = itemView.findViewById(R.id.imageViewFotoAbsen);
        }
    }
}