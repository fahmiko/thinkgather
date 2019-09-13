package com.dev.thinkgather.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.Model.GetInstansi;
import com.dev.thinkgather.Model.GetMember;
import com.dev.thinkgather.Model.GetPublikasi;
import com.dev.thinkgather.Model.Instansi;
import com.dev.thinkgather.Model.Member;
import com.dev.thinkgather.Model.PostData;
import com.dev.thinkgather.Model.Publikasi;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServiceMember;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    public static Register register;
    Session session = Application.getSession();
    @BindView(R.id.regName) EditText regName;
    @BindView(R.id.regMail) EditText regMail;
    @BindView(R.id.regUsername) EditText regUsername;
    @BindView(R.id.regPassword) EditText regPassword;
    @BindView(R.id.regPassword2) EditText regPassword2;
    @BindView(R.id.regBtn) Button regBtn;
    @BindView(R.id.regProgressBar) ProgressBar regProgressBar;
    @BindView(R.id.regInstansi) EditText regInstansi;
    private ServiceMember serviceMember;
    private List<Instansi> instansiList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        register = this;
        serviceMember = ServiceClient.getClient().create(ServiceMember.class);
        instansiList = new ArrayList<>();
        loadInstansi();
        regInstansi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(instansiList.size() == 0){ loadInstansi(); }
                showSelect(v);
            }
        });
    }

    private void loadInstansi() {
        serviceMember.getInstansi().enqueue(new Callback<GetInstansi>() {
            @Override
            public void onResponse(Call<GetInstansi> call, Response<GetInstansi> response) {
                instansiList.addAll(response.body().getResult());
            }

            @Override
            public void onFailure(Call<GetInstansi> call, Throwable t) {

            }
        });
    }

    private void showSelect(View view) {
        new SimpleSearchDialogCompat<>(view.getContext(), "Cari Instansi",
                "Pencarian", null, createData(),
                new SearchResultListener<SampleModelInstansi>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat dialog, SampleModelInstansi item, int position) {
                        regInstansi.setText(item.getTitle());
                        dialog.dismiss();
                    }
                }
        ).show();
    }

    private ArrayList<SampleModelInstansi> createData(){
        ArrayList<SampleModelInstansi> items = new ArrayList<>();
        for(int i = 0; i < instansiList.size(); i++){
            items.add(new SampleModelInstansi(instansiList.get(i).getInstitusi()));
        }
        return items;
    }

    @OnClick(R.id.regBtn)
    public void onClick() {
        regBtn.setVisibility(View.INVISIBLE);
        Member member = new Member(
                regName.getText().toString(),
                regMail.getText().toString(),
                regInstansi.getText().toString(),
                regPassword2.getText().toString(),
                regUsername.getText().toString(),
                regPassword.getText().toString(),
                session.getDeviceToken()
        );

        serviceMember.registerMember(member).enqueue(new Callback<GetMember>() {
            @Override
            public void onResponse(Call<GetMember> call, Response<GetMember> response) {
                if(response.code() == 200){
                    if(response.body().getStatus().equals("success")){
                        if(response.body().getResult().size() != 0){
                            session.saveLogin(response.body().getResult().get(0));
                            finish();
                            startActivity(new Intent(getApplicationContext(), Main.class));
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        regBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetMember> call, Throwable t) {

            }
        });
    }
}

class SampleModelInstansi implements Searchable{
    private String title;

    public SampleModelInstansi(String title){
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
