package com.dev.thinkgather.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.thinkgather.Adapter.KomentarAdapter;
import com.dev.thinkgather.Fragment.HomeFragment;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.Model.GetKomentar;
import com.dev.thinkgather.Model.GetPublikasi;
import com.dev.thinkgather.Model.Komentar;
import com.dev.thinkgather.Model.PostData;
import com.dev.thinkgather.Model.Publikasi;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServicePublikasi;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        postDetailDateName.setText(Application.indonesiaFormatDate(publikasi.getTanggal()) + " | by " + publikasi.getNama());
        Glide.with(getApplicationContext())
                .load(ServiceClient.BASE_URL + "uploads/publikasi/" + publikasi.getGambar())
                .into(postDetailImg);
        komentarList = new ArrayList<>();
        service = ServiceClient.getClient().create(ServicePublikasi.class);
        adapter = new KomentarAdapter(this, komentarList);
        recyclerContent.setLayoutManager(new LinearLayoutManager(this));
        recyclerContent.setAdapter(adapter);
        Glide.with(getApplicationContext())
                .load(ServiceClient.BASE_URL + "uploads/members/" + session.getStringLogin("foto"))
                .into(postDetailCurrentuserImg);
        getSupportActionBar().setTitle("Publikasi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
        switch (view.getId()) {
            case R.id.post_detail_add_comment_btn:
                addKomentar();
                break;
        }
    }

    private void addKomentar() {
        if (postDetailComment.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Harap masukan kata", Toast.LENGTH_SHORT).show();
        } else {
            Komentar komentar = new Komentar(publikasi.getIdPublikasi(), session.getStringLogin("id_member"), postDetailComment.getText().toString());
            postDetailComment.setText("");
            service.tambahKomentar(komentar).enqueue(new Callback<PostData>() {
                @Override
                public void onResponse(Call<PostData> call, Response<PostData> response) {
                    if (response.code() == 200) {
                        loadData();
                    }
                }

                @Override
                public void onFailure(Call<PostData> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dt_edit:
                editPublikasi();
                break;
            case R.id.dt_download:
                if(!publikasi.getBuku().equals("")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(ServiceClient.BASE_URL + "uploads/buku/" + publikasi.getBuku()), "text/html");
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Buku tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editPublikasi() {
        if (!session.getStringLogin("nama").equals(publikasi.getNama())) {
            Toast.makeText(this, "Anda tidak memiliki akses", Toast.LENGTH_SHORT).show();
        } else {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.detail_edit, null);

            final TextInputEditText judul = view.findViewById(R.id.dt_pop_judul);
            final TextInputEditText desc  = view.findViewById(R.id.dt_pop_desc);
            final TextInputEditText haki  = view.findViewById(R.id.dt_pop_haki);
            Button submit = view.findViewById(R.id.dt_submit);

            judul.setText(publikasi.getJudul());
            desc.setText(publikasi.getDeskripsi());
            haki.setText(publikasi.getHaki());

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    service.editPublikasi(publikasi.getIdPublikasi(), judul.getText().toString(),
                            desc.getText().toString(), haki.getText().toString()).enqueue(new Callback<GetPublikasi>() {
                        @Override
                        public void onResponse(Call<GetPublikasi> call, Response<GetPublikasi> response) {
                            if(response.code() == 200){
                                finish();
                                HomeFragment.homeFragment.loadData();
                                Main.main.startActivity(new Intent(Main.main.getApplicationContext(), DetailPost.class)
                                .putExtra("Publikasi", response.body().getResult().get(0)));
                            }
                        }

                        @Override
                        public void onFailure(Call<GetPublikasi> call, Throwable t) {

                        }
                    });

                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit Publikasi").setView(view);
            builder.show();
        }
    }
}