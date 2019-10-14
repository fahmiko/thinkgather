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
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.dev.thinkgather.Adapter.PublikasiAdapter;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.FilePath;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.Model.GetMember;
import com.dev.thinkgather.Model.GetPublikasi;
import com.dev.thinkgather.Model.Member;
import com.dev.thinkgather.Model.Publikasi;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServiceMember;
import com.dev.thinkgather.Service.ServicePublikasi;
import com.github.clans.fab.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.pr_artikel) TextView prArtikel;
    @BindView(R.id.pr_buku) TextView prBuku;
    @BindView(R.id.pr_haki) TextView prHaki;
    @BindView(R.id.btn_edit_email) ImageView btnEditEmail;
    @BindView(R.id.pr_email) TextView prEmail;
    @BindView(R.id.btn_edit_minat) ImageView btnEditMinat;
    @BindView(R.id.pr_minat) TextView prMinat;
    @BindView(R.id.btn_edit_institusi) ImageView btnEditInstitusi;
    @BindView(R.id.pr_institusi) TextView prInstitusi;
    @BindView(R.id.nodata) TextView nodata;
    @BindView(R.id.recycler_content) RecyclerView recyclerContent;
    Unbinder unbinder;
    Member member;
    Session session = Application.getSession();
    File imageFile;
    ServiceMember serviceMember;
    ServicePublikasi servicePublikasi;
    int artikel, haki, buku;
    PublikasiAdapter publikasiAdapter;
    List<Publikasi> publikasiList;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        initComponents();
        getDataPublikasi();
        return view;
    }

    private void getDataPublikasi() {
        servicePublikasi.getAllPublikasi().enqueue(new Callback<GetPublikasi>() {
            @Override
            public void onResponse(Call<GetPublikasi> call, Response<GetPublikasi> response) {
                publikasiList.clear();
                if(response.body().getResult().size() != 0){
                    for(int i = 0; i < response.body().getResult().size(); i++){
                        if(response.body().getResult().get(i).getIdMember().equals(session.getStringLogin("id_member"))){
                            publikasiList.add(response.body().getResult().get(i));
                        }
                    }
                    publikasiAdapter.notifyDataSetChanged();
                    nodata.setVisibility(View.GONE);
                    updateCount(publikasiList);
                }
            }

            @Override
            public void onFailure(Call<GetPublikasi> call, Throwable t) {

            }
        });

    }

    private void updateCount(List<Publikasi> publikasiList) {
        artikel = 0; haki = 0; buku = 0;
        for (int i = 0; i < publikasiList.size(); i++){
            if(publikasiList.get(i).getHaki().equals("Artikel")){
                artikel++;
            }if(publikasiList.get(i).getHaki().equals("HAKI")){
                haki++;
            }if(publikasiList.get(i).getHaki().equals("Buku")){
                buku++;
            }
        }
        prArtikel.setText(String.valueOf(artikel));
        prBuku.setText(String.valueOf(buku));
        prHaki.setText(String.valueOf(haki));
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
        publikasiList = new ArrayList<>();
        serviceMember = ServiceClient.getClient().create(ServiceMember.class);
        servicePublikasi = ServiceClient.getClient().create(ServicePublikasi.class);
        recyclerContent.setLayoutManager(new LinearLayoutManager(getContext()));
        member = new Member(
                this.session.getStringLogin("id_member"),
                this.session.getStringLogin("nama"),
                this.session.getStringLogin("email"),
                this.session.getStringLogin("institusi"),
                this.session.getStringLogin("minat"),
                this.session.getStringLogin("foto")
        );
        try {
            if(!member.getFoto().equals("")){
                Glide.with(getContext())
                        .load(ServiceClient.BASE_URL+"uploads/members/"+member.getFoto())
                        .into(prPhoto);
            }else{
                prPhoto.setImageResource(R.drawable.avatar_profile);
            }
        }catch (Exception e){
            prPhoto.setImageResource(R.drawable.avatar_profile);
        }
        prName.setText(member.getNama());
        prEmail.setText(member.getEmail());
        prInstitusi.setText(member.getInstitusi());
        prMinat.setText(member.getMinatKeilmuan());
        publikasiAdapter = new PublikasiAdapter(getContext(), publikasiList, "my_collection", "profile");
        recyclerContent.setAdapter(publikasiAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_edit_email, R.id.btn_edit_minat, R.id.btn_edit_institusi, R.id.pr_name})
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
            case R.id.pr_name:
                CharSequence[] sequence = {"Upload Foto", "Ganti Nama"};
                new AlertDialog.Builder(getContext())
                        .setTitle("Edit")
                        .setItems(sequence, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        uploadFoto();
                                        break;
                                    case 1:
                                        showDialogText("Nama", prName);
                                        break;
                                }
                            }
                        }).show();
                break;
        }
    }

    private void saveData(View v) {
        int validasi = 0;
        if(prName.getText().toString().equals(this.member.getNama())){ validasi += 1; }
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
        LayoutInflater inflater = getLayoutInflater();
        View customText = inflater.inflate(R.layout.customtext, null);
        final TextInputEditText text = customText.findViewById(R.id.text_input);
        text.setHint(title);
        text.setText(textView.getText().toString());
//        final TextInputEditText text = new TextInputEditText(getContext());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title).setMessage("Ubah Data");
        builder.setView(customText);
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
        Member memberSubmit = new Member(member.getIdMember(), prName.getText().toString(), prEmail.getText().toString(), prInstitusi.getText().toString(), prMinat.getText().toString());
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
