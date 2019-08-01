package com.dev.thinkgather.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Komentar {

    @SerializedName("id_publikasi")
    @Expose
    private String idPublikasi;
    @SerializedName("id_member")
    @Expose
    private String idMember;
    @SerializedName("komentar")
    @Expose
    private String komentar;
    @SerializedName("tanggal")
    @Expose
    private String tanggal;
    @SerializedName("judul")
    @Expose
    private String judul;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("foto")
    @Expose
    private String foto;

    public Komentar(String idPublikasi, String idMember, String komentar) {
        this.idPublikasi = idPublikasi;
        this.idMember = idMember;
        this.komentar = komentar;
    }

    public String getIdPublikasi() {
        return idPublikasi;
    }

    public String getIdMember() {
        return idMember;
    }

    public String getKomentar() {
        return komentar;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getJudul() {
        return judul;
    }

    public String getNama() {
        return nama;
    }

    public String getFoto() {
        return foto;
    }
}