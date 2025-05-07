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

    @NonNull
    @Override
    public RekapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rekap, parent, false);
        return new RekapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RekapViewHolder holder, int position) {
        AbsensiItem item = absensiList.get(position);

        holder.textTanggal.setText(item.getTanggal());
        holder.textStatus.setText(item.isBerhasil() ? "Berhasil mengambil absen" : "Gagal mengambil absen");
        holder.textStatus.setTextColor(item.isBerhasil() ? 0xFF00C853 : 0xFFD32F2F);
        holder.textNama.setText(item.getNama());
        holder.textJamDatang.setText("Jam Datang: " + item.getJamDatang());
        holder.textTerlambat.setText("Terlambat: " + item.getTerlambat());
        holder.textJamPulang.setText("Jam Pulang: " + item.getJamPulang());

        byte[] fotoBytes = item.getFotoAbsen();
        if (fotoBytes != null) {
            Bitmap fotoBitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
            holder.imageProfil.setImageBitmap(fotoBitmap);
        } else {
            holder.imageProfil.setImageResource(R.drawable.jae);
        }
    }

    @Override
    public int getItemCount() {
        return absensiList.size();
    }

    static class RekapViewHolder extends RecyclerView.ViewHolder {
        TextView textTanggal, textStatus, textNama;
        TextView textJamDatang, textTerlambat, textJamPulang;
        ImageView imageProfil;

        RekapViewHolder(View itemView) {
            super(itemView);
            textTanggal = itemView.findViewById(R.id.textTanggal);
            textStatus = itemView.findViewById(R.id.textStatus);
            textNama = itemView.findViewById(R.id.textNama);
            textJamDatang = itemView.findViewById(R.id.textJamDatang);
            textTerlambat = itemView.findViewById(R.id.textTerlambat);
            textJamPulang = itemView.findViewById(R.id.textJamPulang);
            imageProfil = itemView.findViewById(R.id.imageProfil);
        }
    }

    public void updateData(List<AbsensiItem> newList) {
        absensiList.clear();
        absensiList.addAll(newList);
        notifyDataSetChanged();
    }
}