package com.example.colere.easyfood.Common;

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
}