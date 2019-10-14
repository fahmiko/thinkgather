package com.dev.thinkgather.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.thinkgather.Activity.DetailPost;
import com.dev.thinkgather.Fragment.HomeFragment;
import com.dev.thinkgather.Fragment.ProfileFragment;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.Model.Favorite;
import com.dev.thinkgather.Model.GetFavorite;
import com.dev.thinkgather.Model.GetPublikasi;
import com.dev.thinkgather.Model.Member;
import com.dev.thinkgather.Model.PostData;
import com.dev.thinkgather.Model.Publikasi;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServicePublikasi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublikasiAdapter extends RecyclerView.Adapter<PublikasiAdapter.MyViewHolder> {

    private List<Publikasi> publikasiList;
    private String action, activity;
    private Context context;
    private Session session;
    private ServicePublikasi servicePublikasi;
    private List<Favorite> favoriteList;

    public PublikasiAdapter(Context context, List<Publikasi> publikasiList) {
        session = Application.getSession();
        servicePublikasi = ServiceClient.getClient().create(ServicePublikasi.class);
        this.context = context;
        this.publikasiList = publikasiList;
        this.action = "";
        this.activity = "";
        retrieveDataFavorite();
    }

    public PublikasiAdapter(Context context, List<Publikasi> publikasiList, String action, String activity){
        session = Application.getSession();
        servicePublikasi = ServiceClient.getClient().create(ServicePublikasi.class);
        this.context = context;
        this.publikasiList = publikasiList;
        this.action = action;
        this.activity = activity;
        retrieveDataFavorite();
    }

    public List<Favorite> getFavoriteList() {
        return favoriteList;
    }

    private void retrieveDataFavorite() {
        favoriteList = new ArrayList<>();
        servicePublikasi.getFavoriteById(session.getStringLogin("id_member")).enqueue(new Callback<GetFavorite>() {
            @Override
            public void onResponse(Call<GetFavorite> call, Response<GetFavorite> response) {
                favoriteList.addAll(response.body().getResult());
            }

            @Override
            public void onFailure(Call<GetFavorite> call, Throwable t) {

            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_post, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {
        final Publikasi publikasi = publikasiList.get(i);

        holder.judul.setText(publikasi.getJudul());
        holder.tanggal.setText(Application.indonesiaFormatDate(publikasi.getTanggal()));
        if(!publikasi.getGambar().equals("")){
            Glide.with(context.getApplicationContext())
                    .load(ServiceClient.BASE_URL+"uploads/publikasi/"+publikasi.getGambar())
                    .into(holder.gambar);
        }
        holder.imgFavorite.setImageResource(R.drawable.ic_star_off_black_24dp);

        for(int j = 0; j < favoriteList.size(); j++){
            if(favoriteList.get(j).getIdPublikasi().equals(publikasi.getIdPublikasi())){
                holder.imgFavorite.setImageResource(R.drawable.ic_star_active_24dp);
            }
        }

        if(action.equals("my_collection")){
            holder.imgFavorite.setVisibility(View.GONE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(v.getContext(), DetailPost.class).putExtra("Publikasi", publikasi));
            }
        });

        holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favorite favorite = new Favorite(session.getStringLogin("id_member"), publikasi.getIdPublikasi());
                servicePublikasi.updateFavorite(favorite).enqueue(new Callback<PostData>() {
                    @Override
                    public void onResponse(Call<PostData> call, Response<PostData> response) {
                        retrieveDataFavorite();
                        HomeFragment.homeFragment.loadData();
                        switch (activity){
                            case "profile":

                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<PostData> call, Throwable t) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return publikasiList.size();
    }

    public static
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tanggal, judul;
        ImageView gambar, imgFavorite;
        LinearLayout layout;


        public MyViewHolder(View view) {
            super(view);
            tanggal = itemView.findViewById(R.id.tanggal);
            judul   = itemView.findViewById(R.id.judul);
            gambar  = itemView.findViewById(R.id.gambar);
            imgFavorite = itemView.findViewById(R.id.img_favorite);
            layout  = itemView.findViewById(R.id.lay_post);
        }
    }
}