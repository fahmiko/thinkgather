package com.dev.thinkgather.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.Model.GetMember;
import com.dev.thinkgather.Model.Member;
import com.dev.thinkgather.Model.PostData;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServiceMember;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    ServiceMember serviceMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        register = this;
        serviceMember = ServiceClient.getClient().create(ServiceMember.class);
    }

    @OnClick(R.id.regBtn)
    public void onClick() {
        regBtn.setVisibility(View.GONE);
        Member member = new Member(
                regName.getText().toString(),
                regMail.getText().toString(),
                regInstansi.getText().toString(),
                regPassword2.getText().toString(),
                regUsername.getText().toString(),
                regPassword.getText().toString(),
                session.getDeviceToken()
        );

        serviceMember.registerMember(member).enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.body().getStatus().equals("success")){
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_SHORT);
                    finish();

                }else{
                    Toast.makeText(getApplicationContext(),response.message(), Toast.LENGTH_SHORT).show();
                }
                regBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Cek koneksi Internet", Toast.LENGTH_SHORT).show();
                regBtn.setVisibility(View.VISIBLE);
            }
        });
    }
}
