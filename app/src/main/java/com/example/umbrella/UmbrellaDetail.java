package com.example.umbrella;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.umbrella.service.RetrofitClient;
import com.example.umbrella.service.RetrofitInterface;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.enums.UX;
import kr.co.bootpay.listener.CancelListener;
import kr.co.bootpay.listener.CloseListener;
import kr.co.bootpay.listener.ConfirmListener;
import kr.co.bootpay.listener.DoneListener;
import kr.co.bootpay.listener.ErrorListener;
import kr.co.bootpay.listener.ReadyListener;
import kr.co.bootpay.model.BootExtra;
import kr.co.bootpay.model.BootUser;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UmbrellaDetail extends AppCompatActivity {

    private RetrofitInterface retrofitInterface;
    private RetrofitClient retrofitClient;

    private int stuck = 10;

    TextView locknum_detail;

    TextView price_detail;

    Button rentalButton;

    ImageView umb_img;

    int umbrella_code=0;

    Map<String,Object> rentalUmbMap = new HashMap<>();

    View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umbrella_detail);

        locknum_detail = (TextView) findViewById(R.id.locknum_detail);

        rentalButton = (Button) findViewById(R.id.rentalBtn_detail);

        umb_img = (ImageView) findViewById(R.id.umb_img);

        price_detail = (TextView) findViewById(R.id.price_detail);

        Intent intent = getIntent();
        locknum_detail.setText(intent.getStringExtra("locknum"));
        price_detail.setText(intent.getStringExtra("umbrella_price"));
        umbrella_code = intent.getIntExtra("umbrella_code",0);

        String img_name = intent.getStringExtra("umbrella_photo");
        Log.e("이미지 이름 : ",img_name);
        Picasso.get()
                .load("http://172.30.1.61:8000/img?img_name="+img_name)
                .error(R.drawable.ic_launcher_background)
                .into(umb_img);

        //대여한 우산의 상태와 대여 계정 변경을 위한 초기화
        rentalUmbMap.put("umbrella_code", umbrella_code);
        rentalUmbMap.put("rentalId", LoginActivity.loginId);
        rentalUmbMap.put("rentalStatus", 3);



        // 현재 날짜
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();


        // 현재 날짜를 기준으로 이틀 후의 날짜
        LocalDate twoDaysLaterDate = currentDate.plusDays(2);

        // 날짜 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 현재 날짜와 이틀 후의 날짜를 문자열로 변환
        String currentDateString = currentDate.format(formatter);
        String twoDaysLaterDateString = twoDaysLaterDate.format(formatter);

        rentalUmbMap.put("rentalTime",currentDateString);
        rentalUmbMap.put("returnTime",twoDaysLaterDateString);

        }


        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();


        BootpayAnalytics.init(this, "65c84e9000c78a001d3462aa");
        rentalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onClick_request(view);
                QRCodeScannerUtil.startScan(UmbrellaDetail.this);
            }
        });
    }

    public void onClick_request(View v) {
        // 결제호출
        BootUser bootUser = new BootUser().setPhone("010-1234-5678");
        BootExtra bootExtra = new BootExtra().setQuotas(new int[]{0, 2, 3});

        Bootpay.init(getFragmentManager())
                .setApplicationId("65c84e9000c78a001d3462aa") // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.INICIS) // 결제할 PG 사
                .setMethod(Method.CARD) // 결제수단
                .setContext(this)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setUX(UX.PG_DIALOG)
//                .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setName("상봉역 1번 우산") // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호
                .setPrice(1000) // 결제할 금액
                .addItem("1번 우산", 1, "ITEM_CODE_UMBRELLA", 2000) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                    @Override
                    public void onConfirm(@Nullable String message) {

                        if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
                        else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                        Log.d("confirm", message);
                    }
                })
                .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                    @Override
                    public void onDone(@Nullable String message) {

                        Log.d("done", message);

                        Call<ResponseBody> call = retrofitInterface.rentalUmbrella(rentalUmbMap);

                        call.clone().enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                Toast.makeText(getApplicationContext(),"우산 대여완료",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(UmbrellaDetail.this, RentalfinishActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });




                    }
                })
                .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
                    @Override
                    public void onReady(@Nullable String message) {
                        Log.d("ready", message);
                    }
                })
                .onCancel(new CancelListener() { // 결제 취소시 호출
                    @Override
                    public void onCancel(@Nullable String message) {

                        Log.d("cancel", message);
                    }
                })
                .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                    @Override
                    public void onError(@Nullable String message) {
                        Log.d("error", message);
                    }
                })
                .onClose(
                        new CloseListener() { //결제창이 닫힐때 실행되는 부분
                            @Override
                            public void onClose(String message) {
                                Log.d("close", "close");
                            }
                        })
                .request();
    }

    // 카메라 권한 체크
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        QRCodeScannerUtil.handlePermissionsResult(this, requestCode, permissions, grantResults);
    }

    // 스캔 시 작동 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QRCodeScannerUtil.handleResult(requestCode, resultCode, data, new QRCodeScannerUtil.QRScanResultHandler() {
            @Override
            public void onSuccess(String scannedText) {
                Log.e("스캔 결과: ", scannedText);
                Toast.makeText(UmbrellaDetail.this, "스캔 결과: " + scannedText, Toast.LENGTH_LONG).show();

                // 스캔 성공 시 결제창으로 이동
                onClick_request(view);
            }

            @Override
            public void onFailure() {
                Toast.makeText(UmbrellaDetail.this, "스캔 취소", Toast.LENGTH_LONG).show();
            }
        });
    }
}