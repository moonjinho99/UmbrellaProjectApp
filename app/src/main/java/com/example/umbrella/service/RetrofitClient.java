package com.example.umbrella.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    private static RetrofitInterface retrofitInterface;

   private static String baseUrl = "http://172.30.1.61:8000";
//     private static String baseUrl ="http://192.168.50.219:8000";


//    private static String baseUrl = "http://172.30.1.24:8000";

    private RetrofitClient(){
        retrofit2.Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    public static RetrofitClient getInstance(){
        if(instance == null)
        {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public static RetrofitInterface getRetrofitInterface(){
        return retrofitInterface;
    }

}
