package com.dev.thinkgather.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Favorite {

    @SerializedName("id_favorite")
    @Expose
    private String idFavorite;
    @SerializedName("id_member")
    @Expose
    private String idMember;
    @SerializedName("id_publikasi")
    @Expose
    private String idPublikasi;

    public Favorite(String idMember, String idPublikasi) {
        this.idMember = idMember;
        this.idPublikasi = idPublikasi;
    }

    public String getIdFavorite() {
        return idFavorite;
    }

    public void setIdFavorite(String idFavorite) {
        this.idFavorite = idFavorite;
    }

    public String getIdMember() {
        return idMember;
    }

    public void setIdMember(String idMember) {
        this.idMember = idMember;
    }

    public String getIdPublikasi() {
        return idPublikasi;
    }

    public void setIdPublikasi(String idPublikasi) {
        this.idPublikasi = idPublikasi;
    }
}
