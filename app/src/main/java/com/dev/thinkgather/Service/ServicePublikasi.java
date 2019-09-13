package com.dev.thinkgather.Service;
import com.dev.thinkgather.Model.*;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServicePublikasi {

    @GET("Rest_publikasi/publikasi")
    Call<GetPublikasi> getAllPublikasi();

    @GET("Rest_publikasi/komentar")
    Call<GetKomentar> getKomentar(
            @Header("id_publikasi") String id_publikasi
    );

    @GET("Rest_publikasi/publikasi")
    Call<GetPublikasi> getPublikasiById(
            @Header("id_publikasi") String id_publikasi
    );

    @GET("Rest_members/members")
    Call<GetPublikasi> getPublikasiByInstitusi(
            @Header("institusi") String institusi
    );

    @Multipart
    @POST("Rest_publikasi/new")
    Call<PostData> tambahPublikasi(
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part docs,
            @Part("id_member") RequestBody id_member,
            @Part("judul") RequestBody judul,
            @Part("deskripsi") RequestBody deskripsi,
            @Part("haki") RequestBody haki,
            @Part("tanggal") RequestBody tanggal
    );

    @FormUrlEncoded
    @POST("Rest_publikasi/edit")
    Call<GetPublikasi> editPublikasi(
            @Field("id_publikasi") String id_publikasi,
            @Field("judul") String judul,
            @Field("deskripsi") String deskripsi,
            @Field("haki") String haki
    );


    @POST("Rest_publikasi/komentar")
    Call<PostData> tambahKomentar(
        @Body Komentar komentar
    );

    @GET("Rest_publikasi/delete")
    Call<PostData> deletePublikasi(
            @Header("id_publikasi") String id_publikasi
    );
}
