package com.example.umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.umbrella.dto.MemberDto;
import com.example.umbrella.service.RetrofitClient;
import com.example.umbrella.service.RetrofitInterface;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText id, pwd;
    Button loginBtn, joinBtn;
    RetrofitInterface userRetrofitInterface;
    Call<MemberDto> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = (EditText) findViewById(R.id.id);
        pwd = (EditText) findViewById(R.id.pwd);

        loginBtn = (Button) findViewById(R.id.login_button);
        joinBtn = (Button) findViewById(R.id.join_button);

        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        RetrofitInterface userRetrofitInterface = RetrofitClient.getRetrofitInterface();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemberDto memberDto = new MemberDto(id.getText().toString(), 0, pwd.getText().toString(), 0);
                Gson gson = new Gson();
                String userInfo = gson.toJson(memberDto);

                Log.e("JSON", userInfo);

                Call<ResponseBody> call = userRetrofitInterface.loginUser(memberDto);

                call.clone().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e("연결 ","성공");
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                            try {
                                if (response.body().string().equals("success")) {
                                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "아이디/비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                                }

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 가입 버튼 클릭시 회원가입 화면으로 이동
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });
    }
}
