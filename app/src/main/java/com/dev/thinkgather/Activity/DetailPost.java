package com.dev.thinkgather.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.thinkgather.Adapter.KomentarAdapter;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.Model.GetKomentar;
import com.dev.thinkgather.Model.Komentar;
import com.dev.thinkgather.Model.PostData;
import com.dev.thinkgather.Model.Publikasi;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServicePublikasi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPost extends AppCompatActivity {

    public static DetailPost detailPost;

    Session session = Application.getSession();
    RecyclerView.Adapter adapter;
    List<Komentar> komentarList;
    ServicePublikasi service;
    Publikasi publikasi;

    @BindView(R.id.post_detail_img) ImageView postDetailImg;
    @BindView(R.id.post_detail_title) TextView postDetailTitle;
    @BindView(R.id.post_detail_date_name) TextView postDetailDateName;
    @BindView(R.id.post_detail_desc) TextView postDetailDesc;
    @BindView(R.id.post_detail_currentuser_img) CircleImageView postDetailCurrentuserImg;
    @BindView(R.id.post_detail_comment) EditText postDetailComment;
    @BindView(R.id.post_detail_add_comment_btn) Button postDetailAddCommentBtn;
    @BindView(R.id.post_detail_user_img) ImageView postDetailUserImg;
    @BindView(R.id.recycler_content) RecyclerView recyclerContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_post);
        ButterKnife.bind(this);
        initComponents();
        loadData();
    }

    private void initComponents() {
        detailPost = this;
        publikasi = (Publikasi) getIntent().getSerializableExtra("Publikasi");
        postDetailTitle.setText(publikasi.getJudul());
        postDetailDesc.setText(publikasi.getDeskripsi());
        postDetailDateName.setText(Application.indonesiaFormatDate(publikasi.getTanggal()));
        Glide.with(getApplicationContext())
                .load(ServiceClient.BASE_URL + "uploads/publikasi/" + publikasi.getGambar())
                .into(postDetailImg);
        komentarList = new ArrayList<>();
        service = ServiceClient.getClient().create(ServicePublikasi.class);
        adapter = new KomentarAdapter(this, komentarList);
        recyclerContent.setLayoutManager(new LinearLayoutManager(this));
        recyclerContent.setAdapter(adapter);
        Glide.with(getApplicationContext())
                .load(ServiceClient.BASE_URL+"uploads/members/"+session.getStringLogin("foto"))
                .into(postDetailCurrentuserImg);
    }

    public void loadData() {
        service.getKomentar(publikasi.getIdPublikasi()).enqueue(new Callback<GetKomentar>() {
            @Override
            public void onResponse(Call<GetKomentar> call, Response<GetKomentar> response) {
                if (response.code() == 200) {
                    komentarList.clear();
                    if (response.body().getResult().size() != 0) {
                        komentarList.addAll(response.body().getResult());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<GetKomentar> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.post_detail_add_comment_btn)
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.post_detail_add_comment_btn:
            addKomentar();
            break;
        }
    }

    private void addKomentar() {
        Komentar komentar = new Komentar(publikasi.getIdPublikasi(), session.getStringLogin("id_member"),postDetailComment.getText().toString());
        postDetailComment.setText("");
        service.tambahKomentar(komentar).enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.code() == 200){
                    loadData();
                }
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {

            }
        });
    }
}
