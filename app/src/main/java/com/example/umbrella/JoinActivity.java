package com.example.umbrella;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.umbrella.dto.MemberDto;
import com.example.umbrella.service.RetrofitClient;
import com.example.umbrella.service.RetrofitInterface;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity {
    private EditText id, username, pwd, pwdck, phone, certNum;
    private TextView idCheck, pwdCheck, certNumCheck;
    private Button idCheckBtn, phoneCheckBtn, joinBtn, cancelBtn;

    // 유효성 검사 타입 변수(0:실패, 1:성공)
    int idType, pwdType, certNumType = 0;

    private static final String TAG = "JoinActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String SENDER_PHONE_NUMBER = "07010041004";
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        id = findViewById(R.id.userId);
        username = findViewById(R.id.userName);
        pwd = findViewById(R.id.userPwd);
        pwdck = findViewById(R.id.userPwdck);
        phone = findViewById(R.id.userPhone);
        certNum = findViewById(R.id.certNum);

        idCheck = findViewById(R.id.idCheckText);
        pwdCheck = findViewById(R.id.pwdCheckText);
        certNumCheck = findViewById(R.id.certNumText);

        idCheckBtn = findViewById(R.id.idCheck_button);
        phoneCheckBtn = findViewById(R.id.phoneCheck_button);
        joinBtn = findViewById(R.id.join_button);
        cancelBtn = findViewById(R.id.cancel_button);

        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        RetrofitInterface userRetrofitInterface = RetrofitClient.getRetrofitInterface();

        // 아이디 중복확인
        idCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemberDto memberDto = new MemberDto();
                memberDto.setId(id.getText().toString());
                Gson gson = new Gson();
                String userInfo = gson.toJson(memberDto);

                Log.e("JSON", userInfo);

                Call<ResponseBody> call = userRetrofitInterface.idCheck(memberDto);

                call.clone().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.isSuccessful()) {
                            try {
                                if (!(id.getText().toString().isEmpty())) {
                                    if (response.body().string().equals("success")) {
                                        idCheck.setVisibility(View.VISIBLE);
                                        idCheck.setText("사용 가능한 아이디입니다.");
                                        idCheck.setTextColor(Color.GREEN);
                                        idType = 1;
                                    } else {
                                        idCheck.setVisibility(View.VISIBLE);
                                        idCheck.setText("이미 사용 중인 아이디입니다.");
                                        idCheck.setTextColor(Color.RED);
                                        idType = 0;
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });

        // 인증 버튼 클릭 시
        phoneCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });

        // SMS 전송에 필요한 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
        }

        // 회원가입
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 비밀번호 확인
                if (!(pwd.getText().toString().equals(pwdck.getText().toString()))) {
                    pwdCheck.setVisibility(View.VISIBLE);
                    pwdCheck.setText("비밀번호가 일치하지 않습니다.");
                    pwdCheck.setTextColor(Color.RED);
                    pwdType = 0;
                } else {
                    if (!(pwd.getText().toString().isEmpty() && pwdck.getText().toString().isEmpty())) {
                        pwdCheck.setVisibility(View.VISIBLE);
                        pwdCheck.setText("비밀번호가 일치합니다.");
                        pwdCheck.setTextColor(Color.GREEN);
                        pwdType = 1;
                    }
                }

                verifyCode();

                MemberDto memberDto = new MemberDto();
                memberDto.setId(id.getText().toString());
                memberDto.setName(username.getText().toString());
                memberDto.setPw(pwd.getText().toString());
                memberDto.setPhone(phone.getText().toString());
                Gson gson = new Gson();
                String userInfo = gson.toJson(memberDto);

                Log.e("JSON", userInfo);

                Call<ResponseBody> call = userRetrofitInterface.joinUser(memberDto);

                call.clone().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (id.getText().toString().isEmpty() || username.getText().toString().isEmpty() ||
                                pwd.getText().toString().isEmpty() || pwdck.getText().toString().isEmpty() ||
                                phone.getText().toString().isEmpty() || certNum.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "빈칸 없이 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (idType == 1 && pwdType == 1 && certNumType == 1) {  // 아이디 중복, 비밀번호 확인, 인증코드 확인 체크
                                if (response.isSuccessful()) {
                                    try {
                                        if (response.body().string().equals("success")) {
                                            Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "올바른 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 취소 버튼 클릭시 로그인 화면으로 이동
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // 인증 코드 발송
    private void sendVerificationCode() {
        String phoneNumber = phone.getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "휴대폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        verificationCode = generateVerificationCode();
        String message = "[Web 발신]\n우산의 집 인증코드 : " + verificationCode;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
        } else {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, SENDER_PHONE_NUMBER, message, null, null);
                Toast.makeText(this, "인증 코드를 전송했습니다.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "SMS 전송 중 오류 발생: " + e.getMessage());
                Toast.makeText(this, "SMS 전송 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 6자리 인증 코드 생성
    private String generateVerificationCode() {
        Random random = new Random();
        String auth_num = String.format("%06d", random.nextInt(1000000));
        Log.e("생성된 인증코드 확인", auth_num);
        return auth_num;
    }

    // 생성된 인증 코드와 입력한 인증 코드를 비교
    private void verifyCode() {
        String enteredCode = certNum.getText().toString().trim();

        if (!(enteredCode.isEmpty())) {
            if (enteredCode.equals(verificationCode)) {
                certNumCheck.setVisibility(View.VISIBLE);
                certNumCheck.setText("인증되었습니다.");
                certNumCheck.setTextColor(Color.GREEN);
                certNumType = 1;
            } else {
                certNumCheck.setVisibility(View.VISIBLE);
                certNumCheck.setText("인증 코드가 일치하지 않습니다.");
                certNumCheck.setTextColor(Color.RED);
                certNumType = 0;
            }
        }
    }

    // SMS 전송 권한 여부 확인
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendVerificationCode();
                Toast.makeText(this, "SMS 전송 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS 전송 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String sender = smsMessage.getOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();

                        Log.d(TAG, "Sender: " + sender + ", Message: " + messageBody);

                        // 여기에서 SMS 내용(messageBody)을 처리합니다.
                        // 예: 발송된 인증번호 확인
//                        if (messageBody.contains(generatedSmsCode)) {
//                            Toast.makeText(JoinActivity.this, "인증번호가 확인되었습니다.", Toast.LENGTH_SHORT).show();
//                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsReceiver);
    }
}
