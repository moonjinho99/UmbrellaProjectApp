package com.example.umbrella.service;

import com.example.umbrella.dto.LockerDto;
import com.example.umbrella.dto.MemberDto;
import com.example.umbrella.dto.ReturnBoxDto;
import com.example.umbrella.dto.UmbrellaDTO;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    //@으로 메서드를 정리하는 부분

    // 로그인
    @POST("login-user")
    Call<ResponseBody> loginUser(@Body MemberDto jsonUser);

    // 로그인 세션
    @POST("login-userInfo")
    Call<List<MemberDto>> getUserInfo(@Body MemberDto jsonUser);

    // 아이디 중복 확인
    @POST("id-check")
    Call<ResponseBody> idCheck(@Body MemberDto jsonUser);

    // 회원가입
    @POST("join-user")
    Call<ResponseBody> joinUser(@Body MemberDto jsonUser);

    //지도에서 마커표시(보관함)
    @GET("get_locker")
    Call<List<LockerDto>> getLockerList();

    //클릭한 보관함의 우산리스트 확인
    @POST("get_umbrella")
    Call<List<UmbrellaDTO>> getUmbrellaList(@Body String lockercode);

    //우산 대여하기
    @POST("rental_umbrella")
    Call<ResponseBody> rentalUmbrella(@Body Map<String,Object> rentalUmbMap);

    @POST("my_rental_umbrella")
    Call<List<UmbrellaDTO>> getMyRentalUmbrella(@Body Map<String,Object> rentalUmbInfo);

    @POST("return_umbrella")
    Call<ResponseBody> returnUmbrella(@Body Map<String,Object> returnUmbMap);

}