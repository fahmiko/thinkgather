package com.dev.thinkgather.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Model.Publikasi;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;

import java.util.List;

public class PublikasiAdapter extends RecyclerView.Adapter<PublikasiAdapter.MyViewHolder> {

    private List<Publikasi> publikasiList;
    private Context context;

    public PublikasiAdapter(Context context, List<Publikasi> publikasiList) {
        this.context = context;
        this.publikasiList = publikasiList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_post, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.judul.setText(publikasiList.get(i).getJudul());
        holder.tanggal.setText(Application.indonesiaFormatDate(publikasiList.get(i).getTanggal()));
        if(!publikasiList.get(i).getGambar().equals("")){
            Glide.with(context.getApplicationContext())
                    .load(ServiceClient.BASE_URL+"uploads/publikasi/"+publikasiList.get(i).getGambar())
                    .into(holder.gambar);
        }
    }

    @Override
    public int getItemCount() {
        return publikasiList.size();
    }

    public static
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tanggal;
        TextView judul;
        ImageView gambar;

        public MyViewHolder(View view) {
            super(view);
            tanggal = itemView.findViewById(R.id.tanggal);
            judul   = itemView.findViewById(R.id.judul);
            gambar  = itemView.findViewById(R.id.gambar);
        }
    }
}