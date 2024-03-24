package com.example.umbrella;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.umbrella.dto.LockerDto;
import com.example.umbrella.dto.MemberDto;
import com.example.umbrella.dto.UmbrellaDTO;
import com.example.umbrella.service.RetrofitClient;
import com.example.umbrella.service.RetrofitInterface;
import com.google.gson.Gson;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener, MapView.POIItemEventListener {
    private int type = 0;   // 0: 비밀번호 비교, 1: 비밀번호 변경
    private MapView mapView;
    private ViewGroup mapViewContainer;

    private RentalGridListAdapter adapter = new RentalGridListAdapter();

    private List<LockerDto> lockerList;
    static List<UmbrellaDTO> umbrellaList;

    private List<UmbrellaDTO> rentalUmbList;
    private MapPOIItem marker;
    private RetrofitInterface retrofitInterface;
    private RetrofitClient retrofitClient;

    private ImageButton mypageBtn, mapBtn;

    private LinearLayout mypage_layout, map_layout;

    private LinearLayout rentalLayout, updateInfoLayout;
    private GridView rentalGrid;

    private Button rentalBtn, managedInfo;


    //내정보-> 내정보 관리
    private Button managed_updatePwBtn, managed_updateInfoBtn;

    private TextView managedId, managedName, managedPhone;

    private ImageView qrImageView;


    double latitude, longitude;

    TextView id, name;

    Call<List<LockerDto>> callLocker;

    Call<List<UmbrellaDTO>> callRentalUmb;

    Call<ResponseBody> returnUmb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mypageBtn = (ImageButton) findViewById(R.id.mypageBtn);
        mapBtn = (ImageButton) findViewById(R.id.mapBtn);
        map_layout = (LinearLayout) findViewById(R.id.map_view);
        mypage_layout = (LinearLayout) findViewById(R.id.mypage);

        id = findViewById(R.id.memberid);
        name = findViewById(R.id.membername);

        rentalGrid = findViewById(R.id.rental_umbrellaGridView);
        rentalLayout = findViewById(R.id.rentalUmbLayout);
        rentalBtn = findViewById(R.id.rentalMyUmbBtn);

        updateInfoLayout = findViewById(R.id.updateInfoLayout);
        managedInfo = findViewById(R.id.managedInfoBtn);

        managed_updatePwBtn = findViewById(R.id.managed_updatePwBtn);
        managed_updateInfoBtn = findViewById(R.id.managed_updateInfoBtn);
        managedId = findViewById(R.id.managedId);
        managedName = findViewById(R.id.managedname);
        managedPhone = findViewById(R.id.managedPhone);
        managedId.setText(LoginActivity.loginInfo.get(0).getId());
        managedName.setText(LoginActivity.loginInfo.get(0).getName());
        managedPhone.setText(LoginActivity.loginInfo.get(0).getPhone());

        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

//        qrImageView = findViewById(R.id.qr_code_image);
        // 텍스트를 QR 코드로 변환하여 ImageView에 표시
//        generateAndDisplayQRCode("re01");

        Intent intent = getIntent();

        String userId = intent.getStringExtra("id");
        String userName = intent.getStringExtra("name");

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("키해시는 :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // 권한ID를 가져옵니다
        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET);

        int permission2 = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permission3 = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);

        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED || permission3 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(
                        new String[]{android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1000);
            }
            return;
        }

        mypageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map_layout.setVisibility(View.INVISIBLE);
                mypage_layout.setVisibility(View.VISIBLE);
                rentalLayout.setVisibility(View.VISIBLE);

                id.setText(userId);
                name.setText(userName);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map_layout.setVisibility(View.VISIBLE);
                mypage_layout.setVisibility(View.INVISIBLE);
            }
        });

        rentalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rentalLayout.setVisibility(View.VISIBLE);
                updateInfoLayout.setVisibility(View.INVISIBLE);
            }
        });

        managedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rentalLayout.setVisibility(View.INVISIBLE);
                updateInfoLayout.setVisibility(View.VISIBLE);
            }
        });

        //내정보-> 다이얼로그 띄우기

        managed_updatePwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdatePwDialog(MainActivity.this);
            }
        });

        managed_updateInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        lockerList = new ArrayList<>();
        //지도에 보관함 표시
        callLocker = retrofitInterface.getLockerList();

        //지도를 띄우자
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        callLocker.clone().enqueue(new Callback<List<LockerDto>>() {
            @Override
            public void onResponse(Call<List<LockerDto>> call, Response<List<LockerDto>> response) {
                lockerList = response.body();
                if (response.isSuccessful()) {
                    Log.e("보관함리스트", lockerList.toString());

                    for (int i = 0; i < lockerList.size(); i++) {
                        getLatLngFromAddress(lockerList.get(i).getLockerAddr());
                        Log.e("보관함상세", lockerList.get(i).getLockerAddr());
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);

                        marker = new MapPOIItem();
                        marker.setItemName(lockerList.get(i).getLockercode());
                        marker.setTag(i);
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // Set the marker type
                        mapView.addPOIItem(marker);
                        mapView.setMapCenterPoint(mapPoint, true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "가져오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LockerDto>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "연결실패", Toast.LENGTH_SHORT).show();
            }
        });

        mapView.setPOIItemEventListener(this);

        Map<String, Object> myInfo = new HashMap<>();
        myInfo.put("rentalId", userId);
        myInfo.put("rentalStatus", 1);

        callRentalUmb = retrofitInterface.getMyRentalUmbrella(myInfo);


        callRentalUmb.clone().enqueue(new Callback<List<UmbrellaDTO>>() {
            @Override
            public void onResponse(Call<List<UmbrellaDTO>> call, Response<List<UmbrellaDTO>> response) {
                rentalUmbList = response.body();

                RentalGridListAdapter rentalGridListAdapter = new RentalGridListAdapter();

                for (int i = 0; i < rentalUmbList.size(); i++) {
                    rentalGridListAdapter.addItem(rentalUmbList.get(i));
                }

                rentalGrid.setAdapter(rentalGridListAdapter);
            }

            @Override
            public void onFailure(Call<List<UmbrellaDTO>> call, Throwable t) {

            }
        });


    }

    // 권한 체크 이후로직
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        // READ_PHONE_STATE의 권한 체크 결과를 불러온다
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode == 1000) {
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            // 권한 체크에 동의를 하지 않으면 안드로이드 종료
            if (check_result == false) {
                finish();
            }
        }
    }

    private void getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);

            if (addresses != null && addresses.size() > 0) {
                Address firstAddress = addresses.get(0);

                latitude = firstAddress.getLatitude();
                longitude = firstAddress.getLongitude();

                Log.d("LatLng", "Latitude: " + latitude + ", Longitude: " + longitude);

                // Now you have the latitude and longitude
            } else {
                Log.e("Geocoding", "No results found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        Log.e("마커", "전");

        Call<List<UmbrellaDTO>> call = retrofitInterface.getUmbrellaList(mapPOIItem.getItemName());

        Log.e("마커", "후");
        call.clone().enqueue(new Callback<List<UmbrellaDTO>>() {
            @Override
            public void onResponse(Call<List<UmbrellaDTO>> call, Response<List<UmbrellaDTO>> response) {
                umbrellaList = new ArrayList<UmbrellaDTO>();
                umbrellaList = response.body();

                Intent intent = new Intent(MainActivity.this, UmbrellalistActivity.class);
                intent.putExtra("name", mapPOIItem.getItemName());
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<UmbrellaDTO>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "통신 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    // 스캔 시 작동 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QRCodeScannerUtil.handleResult(requestCode, resultCode, data, new QRCodeScannerUtil.QRScanResultHandler() {
            @Override
            public void onSuccess(String scannedText) {
                Log.e("스캔 결과: ", scannedText);
                Toast.makeText(MainActivity.this, "스캔 결과: " + scannedText, Toast.LENGTH_LONG).show();

//                String returnBoxcode = "re01";
//                String returnBoxDetailcode = returnBoxcode+"_"+RentalGridListAdapter.returnUmbName.toString();
//
//                Map<String,Object> returnUmbMap = new HashMap<>();
//
//                returnUmbMap.put("returnBoxDetailcode",returnBoxDetailcode);
//                returnUmbMap.put("returnBoxcode",returnBoxcode);
//                returnUmbMap.put("umbrellacode",RentalGridListAdapter.returnUmbName);
//                returnUmbMap.put("memberId",LoginActivity.loginId);
//
//                returnUmb = retrofitInterface.returnUmbrella(returnUmbMap);
//
//                returnUmb.clone().enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        Toast.makeText(MainActivity.this,"반납되었습니다." , Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(MainActivity.this, ReturnResultActivity.class);
//                        intent.putExtra("scannedText", scannedText);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                    }
//                });
            }

            @Override
            public void onFailure() {
                Toast.makeText(MainActivity.this, "스캔 취소", Toast.LENGTH_LONG).show();
            }
        });
    }


    // 입력한 값으로 QR코드 생성 후 이미지로 변환
//    private void generateAndDisplayQRCode(String text) {
//        Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(text);
//        if (qrCodeBitmap != null) {
//            qrImageView.setImageBitmap(qrCodeBitmap);
//        } else {
//            // QR 코드 생성에 실패한 경우 처리할 코드
//        }
//    }

    //패스워드 변경
    public void showUpdatePwDialog(Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.updatepw_dialog, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        /*
        current_pw : 입력한 현재 패스워드
        check_pw_msg : 입력한 현재 패스워드가 일치하는지 확인하고 출력시키는 TextView
                        (invisible로 해뒀기 때문에 일치하면 visible로 바꿔주고 글색을 초록색으로,
                        일치하지않으면 visible로 바꾸고 글색을 빨간색으로 변경)
        update_pw : 입력한 수정할 패스워드
        updatepw_btn : 변경하기 클릭
        cancel_btn : 취소 클릭

        #### LoginActivity.loginInfo.get(0)에 로그인한 회원정보가 들어있음 ####
        ==> type : List<MemberDTO>
        */
        EditText current_pw = dialogView.findViewById(R.id.current_pw);
        EditText update_pw = dialogView.findViewById(R.id.update_pw);
        TextView check_pw_msg = dialogView.findViewById(R.id.check_pw);
        Button updatepw_btn = dialogView.findViewById(R.id.updatePwBtn);
        Button cancel_btn = dialogView.findViewById(R.id.cancelBtn);

        dialogBuilder.setTitle("패스워드 변경");

        updatepw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 공란이 존재하는 경우
                if (current_pw.getText().toString().isEmpty() || update_pw.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "공란 없이 입력해 주세요", Toast.LENGTH_SHORT).show();
                }

                // 비밀번호 비교
                if (type == 0) {
                    MemberDto memberDto = new MemberDto();
                    memberDto.setId(LoginActivity.loginInfo.get(0).getId());
                    memberDto.setPw(current_pw.getText().toString());
                    Gson gson = new Gson();
                    String userInfo = gson.toJson(memberDto);

                    Log.e("JSON", userInfo);

                    Call<ResponseBody> pwCheckCall = retrofitInterface.pwCheck(memberDto);

                    // 비밀번호 변경 시 입력한 비밀번호와 DB에 저장된 비밀번호를 비교
                    pwCheckCall.clone().enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.e("연결 ", "성공");
                            if (response.isSuccessful()) {
                                try {
                                    // 입력한 비밀번호가 DB에 저장되어 있는 비밀번호와 일치하는 경우
                                    if (response.body().string().equals("success")) {
                                        check_pw_msg.setVisibility(View.VISIBLE);
                                        check_pw_msg.setText("비밀번호 일치");
                                        check_pw_msg.setTextColor(Color.GREEN);
                                        type = 1;
                                    } else {
                                        check_pw_msg.setVisibility(View.VISIBLE);
                                        check_pw_msg.setText("비밀번호가 일치하지 않습니다");
                                        check_pw_msg.setTextColor(Color.RED);
                                        type = 0;
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("연결 ", t.getMessage());
                            Toast.makeText(getApplicationContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                // 비밀번호 변경
                else if (type == 1) {
                    MemberDto memberDto = new MemberDto();
                    memberDto.setId(LoginActivity.loginInfo.get(0).getId());
                    memberDto.setPw(update_pw.getText().toString());
                    Gson gson = new Gson();
                    String userInfo = gson.toJson(memberDto);

                    Log.e("JSON", userInfo);

                    Call<ResponseBody> pwUpdateCall = retrofitInterface.pwUpdate(memberDto);

                    // 비밀번호 변경
                    pwUpdateCall.clone().enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.e("연결 ", "성공");
                            if (response.isSuccessful()) {
                                try {
                                    if (response.body().string().equals("success")) {
                                        Toast.makeText(getApplicationContext(), "비밀번호 변경 성공, 다시 로그인 해주세요", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();

                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);

                                        type = 0;
                                    } else {
                                        Toast.makeText(getApplicationContext(), "비밀번호 변경 실패", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("연결 ", t.getMessage());
                            Toast.makeText(getApplicationContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }


    //회원 정보 변경
    public void showUpdateInfoDialog(Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.updateinfo_dialog, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

}