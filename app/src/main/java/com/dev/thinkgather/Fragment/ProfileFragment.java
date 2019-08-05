package com.dev.thinkgather.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.thinkgather.Activity.Home;
import com.dev.thinkgather.Activity.Main;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.FilePath;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.Model.GetMember;
import com.dev.thinkgather.Model.Member;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServiceMember;
import com.github.clans.fab.FloatingActionButton;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    @BindView(R.id.pr_photo) CircleImageView prPhoto;
    @BindView(R.id.pr_name) TextView prName;
    @BindView(R.id.pr_publikasi) TextView prPublikasi;
    @BindView(R.id.btn_edit_email) ImageView btnEditEmail;
    @BindView(R.id.pr_email) TextView prEmail;
    @BindView(R.id.btn_edit_minat) ImageView btnEditMinat;
    @BindView(R.id.pr_minat) TextView prMinat;
    @BindView(R.id.btn_edit_institusi) ImageView btnEditInstitusi;
    @BindView(R.id.pr_institusi) TextView prInstitusi;
    Unbinder unbinder;
    Member member;
    Session session = Application.getSession();
    File imageFile;
    ServiceMember serviceMember;


    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        initComponents();
        FloatingActionButton button = view.findViewById(R.id.fab_menu_save);
        FloatingActionButton upload = view.findViewById(R.id.fab_menu_upload);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(v);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFoto();
            }
        });
        return view;
    }

    private void uploadFoto() {
        final Intent intent = new Intent();
        intent.setType("image/jpeg");
        intent.setAction(Intent.ACTION_PICK);
        Intent intentChoice = Intent.createChooser(
                intent, "Pilih Gambar untuk di upload");
        startActivityForResult(intentChoice, 1);
    }

    private void initComponents() {
        serviceMember = ServiceClient.getClient().create(ServiceMember.class);
        member = new Member(
                this.session.getStringLogin("id_member"),
                this.session.getStringLogin("nama"),
                this.session.getStringLogin("email"),
                this.session.getStringLogin("institusi"),
                this.session.getStringLogin("minat"),
                this.session.getStringLogin("foto")
        );
        prPublikasi.setText(session.getStringLogin("jml_publikasi"));
        Glide.with(getContext())
                .load(ServiceClient.BASE_URL+"uploads/members/"+member.getFoto())
                .into(prPhoto);
        prName.setText(member.getNama());
        prEmail.setText(member.getEmail());
        prInstitusi.setText(member.getInstitusi());
        prMinat.setText(member.getMinatKeilmuan());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_edit_email, R.id.btn_edit_minat, R.id.btn_edit_institusi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_edit_email:
                showDialogText("Email", prEmail);
                break;
            case R.id.btn_edit_minat:
                showDialogText("Minat", prMinat);
                break;
            case R.id.btn_edit_institusi:
                showDialogText("Institusi", prInstitusi);
                break;
        }
    }

    private void saveData(View v) {
        int validasi = 0;
        if(prEmail.getText().toString().equals(this.member.getEmail())){ validasi += 1; }
        if(prMinat.getText().toString().equals(this.member.getMinatKeilmuan())){ validasi += 1; }
        if(prInstitusi.getText().toString().equals(this.member.getInstitusi())){ validasi += 1; }

        if(validasi == 0){
            Toast.makeText(v.getContext(), "Tidak ada perubahan!", Toast.LENGTH_SHORT).show();
        }else{
            submitData();
        }
    }

    private void showDialogText(String title, final TextView textView){
        final EditText text = new EditText(getContext());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        text.setText(textView.getText().toString());
        builder.setTitle(title).setMessage("Ubah Data");
        builder.setView(text);
        builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(text.getText().toString());
            }
        }).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage;
        if (resultCode == getActivity().RESULT_OK && requestCode == 1) {
            if (data == null) {
                Toast.makeText(getContext(), "Foto gagal di-load", Toast.LENGTH_LONG).show();
            }

            selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageFile = new File(cursor.getString(columnIndex));
                cursor.close();
                uploadFotoProses();
            } else {
                Toast.makeText(getContext(), "Foto gagal di-load", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadFotoProses() {
        MultipartBody.Part body;
        try {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            body = MultipartBody.Part.createFormData("picture", imageFile.getName(),
                    requestFile);
        }catch (Exception e){
            body = null;
        }
        RequestBody reqMember = MultipartBody.create(
                MediaType.parse("multipart/form-data"),
                session.getStringLogin("id_member"));
        serviceMember.editFoto(body, reqMember).enqueue(new Callback<GetMember>() {
            @Override
            public void onResponse(Call<GetMember> call, Response<GetMember> response) {
                if(response.body().getStatus().equals("success")){
                    session.saveLogin(response.body().getResult().get(0));
                    Glide.with(getActivity())
                            .load(ServiceClient.BASE_URL+"uploads/members/"+session.getStringLogin("foto"))
                            .into(prPhoto);
                    Main.main.updateDrawer();
                }
            }

            @Override
            public void onFailure(Call<GetMember> call, Throwable t) {

            }
        });
    }

    private void submitData(){
        Member memberSubmit = new Member(member.getIdMember(), prEmail.getText().toString(), prInstitusi.getText().toString(), prMinat.getText().toString());
        serviceMember.editMember(memberSubmit).enqueue(new Callback<GetMember>() {
            @Override
            public void onResponse(Call<GetMember> call, Response<GetMember> response) {
                if(response.body().getStatus().equals("success")){
                    session.saveLogin(response.body().getResult().get(0));
                    Toast.makeText(getContext(), "Data Berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    Main.main.updateDrawer();
                }
            }

            @Override
            public void onFailure(Call<GetMember> call, Throwable t) {

            }
        });
    }
}
