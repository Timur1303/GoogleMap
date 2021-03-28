package kg.app.googlemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import kg.app.googlemap.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap map;
    private ActivityMainBinding ui;
    private static final int LOCATION_REQUEST_CODE = 101;

    private List<LatLng> coordinates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());
        checkSettings();
        initMap();
        setupListeners();

    }

    private void checkSettings() {
        if (coordinates != null && getSharedPreferences(Prefs.PREFS_KEY, Context.MODE_PRIVATE) != null) {
            coordinates = (Prefs.getLocation());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupListeners() {
        ui.btnClear.setOnClickListener(v -> {
            map.clear();
            coordinates.clear();
        });
        ui.btnNormal.setOnClickListener(v -> {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        });
        ui.btnPolyline.setOnClickListener(v -> {
            addPolygons();
            Prefs.savePolygon(coordinates);
            Toast.makeText(this, "Polygon saved!", Toast.LENGTH_SHORT).show();
            Log.e("TAG", "onClickPolygon: " + coordinates.toString());
        });

        ui.btnDraw.setOnClickListener(view -> {
            addPolygons();
            Prefs.savePolygon(coordinates);
            Toast.makeText(this, "Polygon returned!", Toast.LENGTH_SHORT).show();
            Log.e("TAG", "onClickPolygon: " + coordinates.toString());
        });

    }

    private void addPolygons() {
        if (coordinates != null) {
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.strokeWidth(13f);
            polygonOptions.strokeColor(Color.BLUE);
            polygonOptions.addAll(coordinates);
            map.addPolygon(polygonOptions);
            //coordinates.clear();
       }
    }



    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, LOCATION_REQUEST_CODE);
            }
        }
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMapLongClickListener(this);

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_REQUEST_CODE);
            return;
        }
        map.setMyLocationEnabled(true);

       position();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                }
        }
    }

    private void position() {
        CameraPosition position =
                CameraPosition
                        .builder()
                        .target(new LatLng(42.8795974, 74.6189578))
                        .zoom(16.12f).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 5000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "FINISH", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public void onMapClick(LatLng latLng) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("GeekTech");
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow));
        markerOptions.anchor(0, 0.6789f);
        map.addMarker(markerOptions);
        coordinates.add(latLng);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.remove();
        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

}