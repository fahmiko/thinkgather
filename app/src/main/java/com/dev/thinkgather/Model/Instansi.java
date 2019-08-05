package com.dev.thinkgather.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Instansi{
    @SerializedName("institusi")
    @Expose
    private String institusi;

    public String getInstitusi() {
        return institusi;
    }

    public void setInstitusi(String institusi) {
        this.institusi = institusi;
    }
}