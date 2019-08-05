package com.dev.thinkgather;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.thinkgather.Activity.DetailPost;
import com.dev.thinkgather.Method.Application;
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
    List<String> location = new ArrayList<>();
    ServicePublikasi servicePublikasi;
    public static MapsActivity mapsActivity;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapsActivity = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        servicePublikasi = ServiceClient.getClient().create(ServicePublikasi.class);
        loadPublikasi(getIntent().getStringExtra("minat"));
        String[] strings = getIntent().getStringArrayExtra("location");

        for (int i = 0; i < strings.length ; i++){
            location.add(strings[i]);
        }
        checkLocationPermission();
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
                    updateDetailMarker(marker.getTitle());
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

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            return p1;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void updateDetailMarker(String marker){
        // Each image in array will be displayed at each item beginning.
        // Each item text.
        String judul = "", member = "", deskripsi = "", haki = "", tanggal = "", gambar = "";

        for (int i = 0; i < filterPublikasi.size(); i++){
            gambar += filterPublikasi.get(i).getGambar()+"_";
            judul += filterPublikasi.get(i).getJudul()+"_";
//            member += filterPublikasi.get(i).getNama()+"_";
//            deskripsi += filterPublikasi.get(i).getDeskripsi()+"_";
//            haki += filterPublikasi.get(i).getHaki()+"_";
            tanggal += Application.indonesiaFormatDate(filterPublikasi.get(i).getTanggal())+"_";
        }

        String[] listItemArr = judul.split("_");
        String[] listItemMb  = member.split("_");
        String[] listDesc    = deskripsi.split("_");
        String[] listHaki    = haki.split("_");
        String[] listItemTgl = tanggal.split("_");
        String[] listGambar  = gambar.split("_");
        List<Map<String, Object>> dialogItemList = new ArrayList<>();

        for(int i = 0 ; i < listItemArr.length; i++){
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("judul", listItemArr[i]);
//            itemMap.put("member", listItemMb[i]);
//            itemMap.put("deskripsi", listDesc[i]);
//            itemMap.put("haki", listHaki[i]);
            itemMap.put("tanggal", listItemTgl[i]);
            itemMap.put("gambar", listGambar[i]);
            dialogItemList.add(itemMap);
        }

//        SimpleAdapter simpleAdapter = new SimpleAdapter(MapsActivity.this, dialogItemList,
//                R.layout.detail_edit,
//                new String[]{"judul", "member", "deskripsi", "haki", "tgl"},
//                new int[]{R.id.map_judul, R.id.map_member, R.id.map_desc,R.id.map_haki,R.id.map_tgl});
        CustomAdapter simpleAdapter = new CustomAdapter(MapsActivity.this, dialogItemList,R.layout.list_post,
                new String[]{"judul", "tanggal", "gambar"},
                new int[]{R.id.judul, R.id.tanggal, R.id.gambar},filterPublikasi);

        View view = getLayoutInflater().inflate(R.layout.header_maps,null);
        TextView header = view.findViewById(R.id.header_title);
        header.setText(marker);

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

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Prizinan Aplikasi")
                        .setMessage("Izinkana aplikasi mengakses lokasi")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}

class CustomAdapter extends SimpleAdapter{
    List<Publikasi> publikasiList;
    Context context;
    public CustomAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, List<Publikasi> publikasis) {
        super(context, data, resource, from, to);
        this.publikasiList = publikasis;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view =  super.getView(position, convertView, parent);
        ImageView imageView = view.findViewById(R.id.gambar);
        Glide.with(context).load(ServiceClient.BASE_URL+"uploads/publikasi/"+publikasiList.get(position).getGambar()).into(imageView);
        LinearLayout cardView = view.findViewById(R.id.linearLayout);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DetailPost.class).putExtra("Publikasi", publikasiList.get(position)));
//                (MapsActivity.mapsActivity).finish();
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}