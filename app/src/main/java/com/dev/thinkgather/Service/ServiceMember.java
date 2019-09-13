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
import retrofit2.http.PartMap;

public interface ServiceMember {
    @GET("Rest_members/members")
    Call<GetMember> getAllMember();

    @GET("Rest_members/members")
    Call<GetMember> getMemberById(
            @Header("id_member") String id_member
    );

    @GET("Rest_members/instansi")
    Call<GetInstansi> getInstansi();

    @POST("Rest_members/login")
    Call<GetMember> loginMember(
            @Body Member member
    );

    @POST("Rest_members/register")
    Call<GetMember> registerMember(
        @Body Member member
    );

    @Multipart
    @POST("Rest_members/editFoto")
    Call<GetMember> editFoto(
            @Part MultipartBody.Part file,
            @Part("id_member") RequestBody id_member
    );

    @POST("Rest_members/edit")
    Call<GetMember> editMember(
        @Body Member member
    );
}
