package com.dev.thinkgather.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Publikasi implements Serializable {
    @SerializedName("id_publikasi")
    @Expose
    private String idPublikasi;
    @SerializedName("id_member")
    @Expose
    private String idMember;
    @SerializedName("judul")
    @Expose
    private String judul;
    @SerializedName("deskripsi")
    @Expose
    private String deskripsi;
    @SerializedName("gambar")
    @Expose
    private String gambar;
    @SerializedName("buku")
    @Expose
    private String buku;
    @SerializedName("haki")
    @Expose
    private String haki;
    @SerializedName("tanggal")
    @Expose
    private String tanggal;
    @SerializedName("minat_keilmuan")
    @Expose
    private String minat;
    @SerializedName("institusi")
    @Expose
    private String institusi;
    @SerializedName("nama")
    @Expose
    private String nama;


    public Publikasi(String idMember, String judul, String deskripsi, String gambar, String buku, String haki) {
        this.idMember = idMember;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.gambar = gambar;
        this.buku = buku;
        this.haki = haki;
    }

    public String getIdPublikasi() {
        return idPublikasi;
    }

    public String getIdMember() {
        return idMember;
    }

    public String getJudul() {
        return judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getGambar() {
        return gambar;
    }

    public String getBuku() {
        return buku;
    }

    public String getHaki() {
        return haki;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getMinat() { return minat; }

    public String getInstitusi() { return institusi; }

    public String getNama() { return nama; }
}
