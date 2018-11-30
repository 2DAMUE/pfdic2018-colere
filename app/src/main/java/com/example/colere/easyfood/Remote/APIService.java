package com.example.colere.easyfood.Remote;


import com.example.colere.easyfood.Model.MyResponse;
import com.example.colere.easyfood.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
        {
            "Content-Type:application/json",
                "Authorization:key=AAAATcp9ZjU:APA91bFF07z4XsykX-6wPYGfVx_dTHSe22oOQS8ATXuMo77k_0eA3mXtFCXkq93nKXQSIkqlpzVUkpwnMiuUSNCtvwfNzN5KGaoubSWdeE29M2PJ36gLRoyFgOw6Cwf6KOlG0gQyx5jv"
        }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}