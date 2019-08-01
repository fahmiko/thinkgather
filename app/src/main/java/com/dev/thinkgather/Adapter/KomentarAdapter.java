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
import com.dev.thinkgather.Model.Komentar;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class KomentarAdapter extends RecyclerView.Adapter<KomentarAdapter.MyViewHolder> {

    Context context;
    List<Komentar> komentarList;

    public KomentarAdapter(Context context, List<Komentar> komentarList) {
        this.context = context;
        this.komentarList = komentarList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_comment, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.commentUsername.setText(komentarList.get(i).getNama());
        holder.commentContent.setText(komentarList.get(i).getKomentar());
        try {
            if(!komentarList.get(i).getFoto().equals("")){
                Glide.with(context)
                        .load(ServiceClient.BASE_URL + "uploads/members/" + komentarList.get(i).getFoto())
                        .into(holder.commentUserImg);
            }
        }catch (Exception e){
            holder.commentUserImg.setImageDrawable(context.getDrawable(R.drawable.avatar_profile));
        }
    }

    @Override
    public int getItemCount() {
        return komentarList.size();
    }

    static
    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_username) TextView commentUsername;
        @BindView(R.id.comment_content) TextView commentContent;
        @BindView(R.id.comment_user_img) CircleImageView commentUserImg;
        @BindView(R.id.comment_date) TextView commentDate;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }
}
