package com.example.myapplication;

import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import  static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.mapbox.geojson.GeoJsonSource;
import com.mapbox.maps.style.SymbolLayer;

import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

import androidx.core.app.ActivityCompat;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;

import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.ImageHolder;
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer;
import com.mapbox.maps.extension.style.layers.properties.PropertyValue;
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import java.util.ArrayList;
import java.util.List;



public class MapBox extends AppCompatActivity {


    MapView mapView;
    FloatingActionButton floatingActionButton;
    private boolean isMapTrackingMode = false;
    private List<PointAnnotation> markerList = new ArrayList<>();


   //permmision
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(MapBox.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapBox.this, "Permission not Granted", Toast.LENGTH_SHORT).show();
            }
        }
    });


    private final OnIndicatorBearingChangedListener onIndicatorBearingChangedListener = new OnIndicatorBearingChangedListener() {
        @Override
        public void onIndicatorBearingChanged(double v) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).build());
        }
    };
    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(20.0).build());
            getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
        }
    };
    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {

            getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getLocationComponent(mapView).removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
            getGestures(mapView).removeOnMoveListener(onMoveListener);
            floatingActionButton.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);

        floatingActionButton = findViewById(R.id.fabAddMarker);


        if (ActivityCompat.checkSelfPermission(MapBox.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        private void addMarker(double longitude, double latitude) {
            
            final double MIN_LONGITUDE = -180.0;
            final double MAX_LONGITUDE = 180.0;
            final double MIN_LATITUDE = -90.0;
            final double MAX_LATITUDE = 90.0;

            if (isValidCoordinate(longitude, latitude)) {
                
                PointAnnotationOptions options = new PointAnnotationOptions()
                        .withGeometry(Point.fromLngLat(longitude, latitude))
                        .withIconImage(ImageHolder.from(R.drawable.baseline_add_location_24));  // Replace with your marker icon ID

                mapView.getMapboxMap().getStyle(style -> {
                    if (style != null) {
                        
                        String sourceId = "marker-source-id";

                        style.addImage(new GeoJsonSource(sourceId, options.geometry()));
                        style.addImage(new SymbolLayer("baseline_add_location_24", sourceId);
                                
                    } else {
                       
                        Toast.makeText(this, "Map style not loaded", Toast.LENGTH_SHORT).show();
                    }
                };
        }
        floatingActionButton.hide();
            
            //use custom map ,style satellite also...

        mapView.getMapboxMap().loadStyleUri("mapbox://styles/ckbai/cltpdfdgr002501pnh0zn1272", new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(20.0).build());
                LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
                locationComponentPlugin.setEnabled(true);
                LocationPuck2D locationPuck2D = new LocationPuck2D();

                locationPuck2D.setBearingImage(ImageHolder.from(R.drawable.baseline_add_location_24));


                locationComponentPlugin.setLocationPuck(locationPuck2D);
                locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                getGestures(mapView).addOnMoveListener(onMoveListener);

                floatingActionButton.setOnClickListener(view -> {
                    locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                    locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                    getGestures(mapView).addOnMoveListener(onMoveListener);
                    floatingActionButton.hide();
                });
            }
        });
    }

    }
}
