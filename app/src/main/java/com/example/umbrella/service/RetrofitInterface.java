package com.example.umbrella.service;

import com.example.umbrella.dto.MemberDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    //@으로 메서드를 정리하는 부분

    // 로그인
    @POST("login-user")
    Call<ResponseBody> loginUser(@Body MemberDto jsonUser);

    // 아이디 중복 확인
    @POST("id-check")
    Call<ResponseBody> idCheck(@Body MemberDto jsonUser);

    // 회원가입
    @POST("join-user")
    Call<ResponseBody> joinUser(@Body MemberDto jsonUser);
}
