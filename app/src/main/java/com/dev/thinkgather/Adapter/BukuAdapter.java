package com.dev.thinkgather.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.thinkgather.Model.Publikasi;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BukuAdapter extends RecyclerView.Adapter<BukuAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<Publikasi> publikasi;
    private List<Publikasi> filterPublikasi;

    public BukuAdapter(Context context, List<Publikasi> publikasi){
        this.context = context;
        this.publikasi = publikasi;
        this.filterPublikasi = publikasi;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_book, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        final Publikasi pb = filterPublikasi.get(i);
        viewHolder.name.setText(pb.getJudul());
        viewHolder.subName.setText(pb.getHaki());
        viewHolder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(ServiceClient.BASE_URL + "uploads/buku/" + pb.getBuku()), "text/html");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterPublikasi.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail) ImageView thumbnail;
        @BindView(R.id.name) TextView name;
        @BindView(R.id.sub_name) TextView subName;
        @BindView(R.id.btn_download) TextView btnDownload;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()){
                    filterPublikasi = publikasi;
                }else{
                    List<Publikasi> filteredList = new ArrayList<>();
                    for (Publikasi row : publikasi){
                        if(row.getJudul().toLowerCase().contains(charString.toLowerCase()) || row.getHaki().contains(charString)){
                            filteredList.add(row);
                        }
                    }
                    filterPublikasi = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filterPublikasi;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterPublikasi = (ArrayList<Publikasi>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public List<Publikasi> getPublikasiFiltered(){
        return filterPublikasi;
    }
}
