package com.dev.thinkgather.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.Model.GetMember;
import com.dev.thinkgather.Model.Member;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServiceMember;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private ServiceMember service;
    private Session session = Application.getSession();
    @BindView(R.id.login_mail) EditText loginMail;
    @BindView(R.id.login_password) EditText loginPassword;
    @BindView(R.id.loginBtn) Button loginBtn;
    @BindView(R.id.button_register) Button buttonRegister;
    @BindView(R.id.imageView) ImageView imageView;
    private static final int READ_STORAGE_PERMISSIONS_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_example);
        ButterKnife.bind(this);
        service = ServiceClient.getClient().create(ServiceMember.class);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        if (getReminingTime().equals("AM")) {
            imageView.setImageDrawable(getDrawable(R.drawable.good_morning_img));
        }else{
            imageView.setImageDrawable(getDrawable(R.drawable.good_night_img));
        }
        galleryPermition();
    }

    private String getReminingTime() {
        Calendar now = Calendar.getInstance();
        String time;
        if (now.get(Calendar.AM_PM) == Calendar.AM) {
            // AM
//            time = ""+now.get(Calendar.HOUR)+":AM";
            time = "AM";
        } else {
            // PM
//            time = ""+now.get(Calendar.HOUR)+":PM";
            time = "PM";
        }
        return time;
    }

    @OnClick({R.id.loginBtn})
    public void onClick() {
        if (loginMail.getText().toString().equals("") || loginPassword.getText().toString().equals("")) {
            Toast.makeText(this, "Email dan password tidak valid", Toast.LENGTH_SHORT).show();
        } else {
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
                }

                @Override
                public void onFailure(Call<GetMember> call, Throwable t) {

                }
            });
        }
    }

    private void galleryPermition(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_STORAGE_PERMISSIONS_REQUEST);
            }
        } else {

        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case READ_STORAGE_PERMISSIONS_REQUEST: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }

        }
    }
}
