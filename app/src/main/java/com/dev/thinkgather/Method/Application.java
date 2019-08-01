package com.dev.thinkgather.Method;

import android.content.Context;
import android.content.Intent;

import com.brouding.simpledialog.SimpleDialog;

public class Application extends android.app.Application {
    private static Session session;

    @Override
    public void onCreate() {
        super.onCreate();
        session = new Session(getApplicationContext());
    }

    public static Session getSession(){
        return session;
    }

    public static SimpleDialog.Builder getProgress(Context context, String msg) {
        return new SimpleDialog.Builder(context)
                .showProgress(true)
                .setContent(msg)
                .setBtnCancelText("Minimize")
                .setCancelable(true);
    }

    public static String indonesiaFormatDate(String date){
        String[] dateSplit = date.split("-");
        String month = null;
        int dateInt = Integer.parseInt(dateSplit[1]);
        switch (dateInt){
            case 1:
                month = "Januari";
                break;
            case 2:
                month = "Februari";
                break;
            case 3:
                month = "Maret";
                break;
            case 4:
                month = "April";
                break;
            case 5:
                month = "Mei";
                break;
            case 6:
                month = "Juni";
                break;
            case 7:
                month = "juli";
                break;
            case 8:
                month = "Agustus";
                break;
            case 9:
                month = "September";
                break;
            case 10:
                month = "Oktober";
                break;
            case 11:
                month = "November";
                break;
            case 12:
                month = "Desember";
                break;
        }
        return dateSplit[2]+" "+month+" "+dateSplit[0];
    }

}
