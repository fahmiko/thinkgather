package com.dev.thinkgather.Activity;

import android.content.Intent;
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
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServiceMember;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    ServiceMember service;
    Session session = Application.getSession();
    @BindView(R.id.login_mail) EditText loginMail;
    @BindView(R.id.login_password) EditText loginPassword;
    @BindView(R.id.loginBtn) Button loginBtn;
    @BindView(R.id.login_progress) ProgressBar loginProgress;
    @BindView(R.id.button_register) Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        service = ServiceClient.getClient().create(ServiceMember.class);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }

    @OnClick(R.id.loginBtn)
    public void onClick() {
        if (loginMail.getText().toString() == "" || loginPassword.getText().toString() == "") {
            Toast.makeText(this, "Email dan password tidak valid", Toast.LENGTH_SHORT).show();
        } else {
            loginBtn.setVisibility(View.GONE);
            buttonRegister.setVisibility(View.GONE);
            Member member = new Member(loginMail.getText().toString(), loginPassword.getText().toString(), session.getDeviceToken());
            service.loginMember(member).enqueue(new Callback<GetMember>() {
                @Override
                public void onResponse(Call<GetMember> call, Response<GetMember> response) {
                    if (response.code() == 200) {
                        if (response.body().getResult().size() == 0) {
                            Toast.makeText(getApplicationContext(), "Username & password tidak valid", Toast.LENGTH_SHORT).show();
                        } else {
//                            Toast.makeText(getApplicationContext(), "Login valid", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Main.class));
                            session.saveLogin(response.body().getResult().get(0));
                            finish();
                        }
                    }
                    loginBtn.setVisibility(View.VISIBLE);
                    buttonRegister.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<GetMember> call, Throwable t) {
                    loginBtn.setVisibility(View.VISIBLE);
                    buttonRegister.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
