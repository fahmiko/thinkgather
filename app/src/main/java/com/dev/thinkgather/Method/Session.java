package com.dev.thinkgather.Method;

import android.content.Context;
import android.content.SharedPreferences;

import com.dev.thinkgather.Model.Member;

public class Session {
    private Context context;
    private SharedPreferences preferences;

    public Session(Context context){
        this.context = context;
        preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
    }

    //Berfungsi unruk menyimpan device token, device token digunakan untuk dapat menerima notifikasi dari admin
    public void saveDeviceToken(String token){
        SharedPreferences sf = context.getSharedPreferences("device_token",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("device_token", token);
        editor.apply();
    }

    public String getDeviceToken(){
        SharedPreferences sf = context.getSharedPreferences("device_token", Context.MODE_PRIVATE);
        return sf.getString("device_token", null);
    }

    public void saveLogin(Member member){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id_member", member.getIdMember());
        editor.putString("foto", member.getFoto());
        editor.putString("nama", member.getNama());
        editor.putString("email", member.getEmail());
        editor.putString("institusi", member.getInstitusi());
        editor.putString("minat", member.getMinatKeilmuan());
        editor.apply();
    }

    public void sessionDestroy(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();
    }

    public boolean checkLogin(){
        String id = preferences.getString("id_member", null);
        try {
            if(id.isEmpty()){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }

    public String getStringLogin(String name){
        return preferences.getString(name, null);
    }
}
