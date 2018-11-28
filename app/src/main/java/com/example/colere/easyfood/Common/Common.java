package com.example.colere.easyfood.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.colere.easyfood.Model.User;

public class Common {
    public static User currentUser;

    public static String convertCodeToStatus(String status){
        if(status.equals("0"))
            return "Orden hecha";
        else if(status.equals("1"))
            return "En camino";
        else
            return "Enviado";
    }

    public static final String DELETE = "Delete";
    public static boolean isConnectedToInterner(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }

        return false;
    }
}