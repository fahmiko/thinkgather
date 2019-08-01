package com.dev.thinkgather;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dev.thinkgather.Model.GetMember;
import com.dev.thinkgather.Model.GetPublikasi;
import com.dev.thinkgather.Model.Member;
import com.dev.thinkgather.Model.Publikasi;
import com.dev.thinkgather.Service.ServiceClient;
import com.dev.thinkgather.Service.ServiceMember;
import com.dev.thinkgather.Service.ServicePublikasi;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.dialogplus.DialogPlus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<Publikasi> publikasi = new ArrayList<>();
    List<Publikasi> filterPublikasi = new ArrayList<>();
    ServicePublikasi servicePublikasi;
    List<String> location = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        servicePublikasi = ServiceClient.getClient().create(ServicePublikasi.class);
        loadPublikasi("Gizi");
        String[] strings = getIntent().getStringArrayExtra("location");
        for (int i = 0; i < strings.length ; i++){
            location.add(strings[i]);
        }
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
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<LatLng> latitude = new ArrayList<>();


        for (int i = 0 ; i < location.size(); i++){
            latitude.add(i, getLocationFromAddress(getApplicationContext(),location.get(i)));
        }

        for (int i = 0 ; i < location.size(); i++){
            builder.include(latitude.get(i));
        }

        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        for (int i = 0; i < latitude.size(); i++){
            mMap.addMarker(new MarkerOptions().position(latitude.get(i)).title(location.get(i)));
        }

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                filteringData(marker.getTitle());
                if(filterPublikasi.size() != 0){
                    updateDetailMarker();
                }
                return false;
            }
        });
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
            return p1;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void updateDetailMarker(){
        // Each image in array will be displayed at each item beginning.
        // Each item text.
        String judul = "", tanggal = "";


        for (int i = 0; i < filterPublikasi.size(); i++){
            judul += filterPublikasi.get(i).getJudul()+"_";
            tanggal += filterPublikasi.get(i).getTanggal()+"_";
        }

        String[] listItemArr = judul.split("_");
        String[] listItemTgl = tanggal.split("_");
        List<Map<String, Object>> dialogItemList = new ArrayList<>();

        for(int i = 0 ; i < listItemArr.length; i++){
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("text", listItemArr[i]);
            itemMap.put("tanggal", listItemTgl[i]);
            dialogItemList.add(itemMap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(MapsActivity.this, dialogItemList,
                R.layout.list_post,
                new String[]{"text", "tanggal"},
                new int[]{R.id.judul,R.id.tanggal});
        View view = getLayoutInflater().inflate(R.layout.header_maps,null);

        DialogPlus dialogPlus = DialogPlus.newDialog(MapsActivity.this)
                .setAdapter(simpleAdapter)
                .setHeader(view)
                .setExpanded(true)
                .create();
        dialogPlus.show();

    }

    private void loadPublikasi(String nama){
        servicePublikasi.getPublikasiByInstitusi(nama).enqueue(new Callback<GetPublikasi>() {
            @Override
            public void onResponse(Call<GetPublikasi> call, Response<GetPublikasi> response) {
                if(response.code() == 200){
                    if(response.body().getResult().size() != 0){
                        publikasi.addAll(response.body().getResult());
                    }
                }
            }

            @Override
            public void onFailure(Call<GetPublikasi> call, Throwable t) {

            }
        });
    }

    private void filteringData(String map){
        filterPublikasi.clear();
        for(int i = 0; i < publikasi.size(); i++){
            if(publikasi.get(i).getInstitusi().toLowerCase().equals(map.toLowerCase())){
                filterPublikasi.add(publikasi.get(i));
            }
        }
    }
}
