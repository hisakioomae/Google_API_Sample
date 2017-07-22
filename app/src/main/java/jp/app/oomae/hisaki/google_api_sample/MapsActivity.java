package jp.app.oomae.hisaki.google_api_sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationRequest;
import  com.google.android.gms.location.LocationListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback ,
GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest request;
    private FusedLocationProviderApi api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //パーミッションの確認，要求
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //位置情報のリクエスト情報を取得
        request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(15);
        api = LocationServices.FusedLocationApi;

        //Google playへの接続クライアントを生成
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //デフォルト位置のセット
        LatLng current = new LatLng(35, 139);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current,16f));

    }


    @Override
    protected void onResume(){
        super.onResume();
        //GooglePlayへ接続
        if(client != null){
            client.connect();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        //位置リクエストの解除及びGooglePlayから切断
        if(client != null && client.isConnected()){
            api.removeLocationUpdates(client,this);
        }
        client.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle){
        //ACCESS_FINE_LOCATIONのパーミッション確認
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        //位置情報の監視を開始
        api.requestLocationUpdates(client,request,this);
    }

    //接続が中断されたときの処理
    @Override
    public void onConnectionSuspended(int i){
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
    }

    //位置情報が更新されたとき，カメラ位置の移動
    @Override
    public void onLocationChanged(Location location){
        if(mMap == null){
            return;
        }
        LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current,16f));
    }

    @Override
    public void onRequestPermissionsResult(int code,@NonNull String[] perms,@NonNull int[] results){
        if(code == 1 && results[0] == PackageManager.PERMISSION_GRANTED){
            //任意の処理
            //
        }
    }

}
