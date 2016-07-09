package br.ufpe.cin.contexto.bikecidadao;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bikecidadao.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import br.ufpe.cin.br.adapter.bikecidadao.Marcador;
import br.ufpe.cin.br.adapter.bikecidadao.Ocorrencia;
import br.ufpe.cin.contexto.bikecidadao.async.AsyncGetOcurrences;
import br.ufpe.cin.util.bikecidadao.OnGetOccurrencesCompletedCallback;

/**
 * Simple activity demonstrating ClusterManager.
 */
public class ClusterMapDisplayActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener, OnGetOccurrencesCompletedCallback {

    private ClusterManager<Marcador> mClusterManager;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng currentLatLng;

    public static final String[] OCCURRENCES = {"Local de acidente", "Tráfego intenso", "Sinalização ruim", "Via danificada"};

    private HashMap<String,String> markers;


    @Override
    protected void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        setContentView(R.layout.activity_cluster_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mLastLocation = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if(mLastLocation !=null){
            currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));
        }

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10));

        mClusterManager = new ClusterManager<Marcador>(this, this.googleMap);
        this.googleMap.setOnCameraChangeListener(mClusterManager);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        this.googleMap = map;

        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setBuildingsEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        try {
            AsyncGetOcurrences asyncGetOcurrences = new AsyncGetOcurrences(ClusterMapDisplayActivity.this);
            asyncGetOcurrences.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onGetOccurrencesCompleted(List<Ocorrencia> occurrences) {
        if(occurrences!=null) {
            for (Ocorrencia occurrence : occurrences) {

                LatLng latLng = new LatLng(Double.parseDouble(occurrence.getLat()), Double.parseDouble(occurrence.getLng()));
                Marker marker = this.addMarker(latLng, occurrence.getOccurenceCode());
                markers.put(marker.getId(), String.valueOf(occurrence.getIdOcorrencia()));
            }
        }

    }

    public Marker addMarker(LatLng latLng, int occurenceTypeID){

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(OCCURRENCES[occurenceTypeID]).icon(BitmapDescriptorFactory.fromBitmap(getBitmapIcon(occurenceTypeID)));
        markerOptions.snippet("Toque aqui para remover marcador");
        return googleMap.addMarker(markerOptions);
    }

    public Bitmap getBitmapIcon(int occurenceTypeID){
        View markerView = getMarkerView(occurenceTypeID);
        Bitmap markerBmp =  createDrawableFromView(markerView);
        return markerBmp;
    }

    public View getMarkerView(int occurenceTypeID){
        //OCCURRENCES = {"Local de acidente", "Tráfego intenso", "Sinalização Ruim", "Via danificada"};

        View marker = null;
        int markerViewTypeID = getMarkViewTypeID(occurenceTypeID);
        marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(markerViewTypeID, null);
        return marker;

    }

    public Bitmap createDrawableFromView(View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private int getMarkViewTypeID(int occurenceTypeID){
        int markerViewTypeID = R.layout.mark_layout;

        switch (occurenceTypeID){
            case 0:
                markerViewTypeID = R.layout.accident_spot;
                break;
            case 1:
                markerViewTypeID = R.layout.heavy_traffic;
                break;
            case 2:
                markerViewTypeID = R.layout.bad_sinalization;
                break;
            case 3:
                markerViewTypeID = R.layout.rout_damaged;
                break;
        }
        return markerViewTypeID;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }


}