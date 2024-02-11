package com.example.umbrella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.umbrella.dto.LockerDto;
import com.example.umbrella.dto.MemberDto;
import com.example.umbrella.dto.UmbrellaDTO;
import com.example.umbrella.service.RetrofitClient;
import com.example.umbrella.service.RetrofitInterface;
import com.google.gson.Gson;

import net.daum.android.map.MapActivity;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener, MapView.POIItemEventListener {

    private MapView mapView;
    private ViewGroup mapViewContainer;

    private List<LockerDto> lockerList;
    static List<UmbrellaDTO> umbrellaList;
    private MapPOIItem marker;
    private RetrofitInterface retrofitInterface;
    private RetrofitClient retrofitClient;

    private ImageButton mypageBtn, mapBtn;

    private LinearLayout mypage_layout, map_layout;
    double latitude,longitude;

    Call<List<LockerDto>> callLocker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mypageBtn = (ImageButton)findViewById(R.id.mypageBtn);
        mapBtn = (ImageButton)findViewById(R.id.mapBtn);
        map_layout = (LinearLayout) findViewById(R.id.map_view);
        mypage_layout = (LinearLayout) findViewById(R.id.mypage);

        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();


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
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map_layout.setVisibility(View.VISIBLE);
                mypage_layout.setVisibility(View.INVISIBLE);
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
                    Log.e("보관함리스트",lockerList.toString());

                    for(int i=0; i<lockerList.size(); i++)
                    {
                        getLatLngFromAddress(lockerList.get(i).getLockerAddr());
                        Log.e("보관함상세",lockerList.get(i).getLockerAddr());
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);

                        marker = new MapPOIItem();
                        marker.setItemName(lockerList.get(i).getLockercode());
                        marker.setTag(i);
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // Set the marker type
                        mapView.addPOIItem(marker);
                        mapView.setMapCenterPoint(mapPoint, true);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "가져오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LockerDto>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "연결실패", Toast.LENGTH_SHORT).show();
            }
        });

        mapView.setPOIItemEventListener(this);

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

        Log.e("마커","전");

        Call<List<UmbrellaDTO>> call = retrofitInterface.getUmbrellaList(mapPOIItem.getItemName());

        Log.e("마커","후");
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
}