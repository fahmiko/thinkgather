package com.dev.thinkgather.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Member {
    @SerializedName("id_member")
    @Expose
    private String idMember;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("institusi")
    @Expose
    private String institusi;
    @SerializedName("minat_keilmuan")
    @Expose
    private String minatKeilmuan;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("foto")
    @Expose
    private String foto;
    @SerializedName("token")
    @Expose
    private String token;

    public Member(String nama, String email, String institusi, String minatKeilmuan, String username, String password, String token) {
        this.nama = nama;
        this.email = email;
        this.institusi = institusi;
        this.minatKeilmuan = minatKeilmuan;
        this.username = username;
        this.password = password;
        this.token = token;
    }

    public Member(String idMember, String email, String institusi, String minatKeilmuan) {
        this.idMember = idMember;
        this.email = email;
        this.institusi = institusi;
        this.minatKeilmuan = minatKeilmuan;
    }

    public Member(String idMember, String nama, String email, String institusi, String minatKeilmuan, String foto) {
        this.idMember = idMember;
        this.nama = nama;
        this.email = email;
        this.institusi = institusi;
        this.minatKeilmuan = minatKeilmuan;
        this.foto = foto;
    }

    public Member(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
    }

    public String getIdMember() {
        return idMember;
    }

    public String getNama() {
        return nama;
    }

    public String getEmail() {
        return email;
    }

    public String getInstitusi() {
        return institusi;
    }

    public String getMinatKeilmuan() {
        return minatKeilmuan;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFoto() {
        return foto;
    }

    public String getToken() {
        return token;
    }
}
