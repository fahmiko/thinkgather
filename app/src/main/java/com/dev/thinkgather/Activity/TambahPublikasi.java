package com.dev.thinkgather.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brouding.simpledialog.SimpleDialog;
import com.bumptech.glide.Glide;
import com.dev.thinkgather.Fragment.HomeFragment;
import com.dev.thinkgather.Method.Application;
import com.dev.thinkgather.Method.FilePath;
import com.dev.thinkgather.Method.Session;
import com.dev.thinkgather.Model.PostData;
import com.dev.thinkgather.R;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServicePublikasi;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahPublikasi extends AppCompatActivity {
    public static TambahPublikasi tambahPublikasi;
    @BindView(R.id.popup_user_image) ImageView popupUserImage;
    @BindView(R.id.popup_title) EditText popupTitle;
    @BindView(R.id.popup_description) EditText popupDescription;
    @BindView(R.id.popup_haki) Spinner popupHaki;
    @BindView(R.id.popup_tanggal) EditText popupTanggal;
    @BindView(R.id.popup_buku) MaterialButton popupBuku;
    @BindView(R.id.text_upload) TextView textUpload;
    @BindView(R.id.popup_img) ImageView popupImg;
    @BindView(R.id.popup_add) ImageView popupAdd;
    public SimpleDialog progress;
    String dateSave;
    File imageFile, doctFile;
    Uri selectedFiles;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat simpleDateFormat;
    Session session = Application.getSession();
    ServicePublikasi service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post);
        ButterKnife.bind(this);
        initComponents();
    }

    private void initComponents() {
        tambahPublikasi = this;
        dateSave = "";
        service = ServiceClient.getClient().create(ServicePublikasi.class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tambah Karya");
        getSupportActionBar().show();
        simpleDateFormat = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.PublicationList, android.R.layout.simple_spinner_dropdown_item);
        popupHaki.setAdapter(arrayAdapter);
    }

    private void uploadFoto() {
        final Intent intent = new Intent();
        intent.setType("image/jpeg");
        intent.setAction(Intent.ACTION_PICK);
        Intent intentChoice = Intent.createChooser(
                intent, "Pilih Gambar untuk di upload");
        startActivityForResult(intentChoice, 1);
    }

    private void launchPicker(){
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(101)
                .withHiddenFiles(false)
                .withFilter(Pattern.compile(".*\\.pdf$"))
                .withTitle("Pilih File PDF")
                .start();
    }

    private void uploadDokumen() {
        launchPicker();
//        final Intent intent = new Intent();
//        intent.setType("application/pdf");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        Intent intentChoice = Intent.createChooser(
//                intent, "Pilih Dokumen untuk di upload");
//        startActivityForResult(intentChoice, 2);
    }

    private void submitData() {
        if (checkValidation()) {
            progress = Application.getProgress(this, "Sedang Upload").show();
            MultipartBody.Part body, files;
            try {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
                body = MultipartBody.Part.createFormData("picture", imageFile.getName(),
                        requestFile);
            } catch (Exception e) {
                body = null;
            }

            try {
//                files = prepareFilePart("files", selectedFiles);
                RequestBody requestFile2 = RequestBody.create(MediaType.parse("application/pdf"), doctFile);
                files = MultipartBody.Part.createFormData("files", doctFile.getName(),
                        requestFile2);
            } catch (Exception e) {
                files = null;
            }

            RequestBody reqTitle = MultipartBody.create(
                    MediaType.parse("multipart/form-data"), popupTitle.getText().toString());
            RequestBody reqDescription = MultipartBody.create(
                    MediaType.parse("multipart/form-data"), popupDescription.getText().toString());
            RequestBody reqHaki = MultipartBody.create(
                    MediaType.parse("multipart/form-data"), popupHaki.getSelectedItem().toString());
            RequestBody reqTanggal = MultipartBody.create(
                    MediaType.parse("multipart/form-data"), dateSave);
            RequestBody reqMember = MultipartBody.create(
                    MediaType.parse("multipart/form-data"), session.getStringLogin("id_member"));
            service.tambahPublikasi(body, files, reqMember, reqTitle, reqDescription, reqHaki, reqTanggal).enqueue(new Callback<PostData>() {
                @Override
                public void onResponse(Call<PostData> call, Response<PostData> response) {
                    if(response.body().getStatus().equals("success")){
                        finish();
                        (HomeFragment.homeFragment).loadData();
                    }else if(response.body().getStatus().equals("failed")){
                        Toast.makeText(getApplicationContext(), response.body().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PostData> call, Throwable t) {

                }
            });
        }
    }

    @OnClick({R.id.popup_buku, R.id.popup_add, R.id.popup_tanggal})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.popup_buku:
                uploadDokumen();
                break;
            case R.id.popup_add:
                uploadFoto();
                break;
            case R.id.popup_tanggal:
                showDialogDate();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Uri selectedImage;
        if (resultCode == RESULT_OK && requestCode == 1) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "Foto gagal di-load", Toast.LENGTH_LONG).show();
            }

            selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageFile = new File(cursor.getString(columnIndex));
                Glide.with(this).load(imageFile.getAbsoluteFile()).into(popupImg);
                cursor.close();
            } else {
                Toast.makeText(getApplicationContext(), "Foto gagal di-load", Toast.LENGTH_LONG).show();
            }
        }

        if (resultCode == RESULT_OK && requestCode == 2) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "Dokumen gagal di-load", Toast.LENGTH_LONG).show();
            }
            selectedFiles = data.getData();
            textUpload.setText(selectedFiles.toString());
        }

        if (requestCode == 101 && resultCode == RESULT_OK){
            String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            doctFile = new File(path);
            textUpload.setText(doctFile.getName());
        }
    }

    private boolean checkValidation() {
        if(popupTitle.getText().toString().isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    public void showDialogDate() {
        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                popupTanggal.setText(Application.indonesiaFormatDate(simpleDateFormat.format(newDate.getTime())));
                dateSave = simpleDateFormat.format(newDate.getTime());
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("application/pdf"),
                        doctFile
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, doctFile.getName(), requestFile);
    }

//    public void showDialogTime(){
//        // Get Current Time
//        final Calendar c = Calendar.getInstance();
//        int mHour = c.get(Calendar.HOUR_OF_DAY);
//        int mMinute = c.get(Calendar.MINUTE);
//        final int mSecond = c.get(Calendar.SECOND);
//
//        // Launch Time Picker Dialog
//        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
//                new TimePickerDialog.OnTimeSetListener() {
//
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay,
//                                          int minute) {
//                        clock.setText(" "+hourOfDay + ":" + minute+":"+mSecond);
//                    }
//                }, mHour, mMinute, false);
//        timePickerDialog.show();
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                submitData();
                break;
            case R.id.reset:
                resetData();
                break;
        }
        return false;
    }

    private void resetData() {
        popupTitle.setText("");
        popupTanggal.setText("");
        popupHaki.setSelection(0);
        popupDescription.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
