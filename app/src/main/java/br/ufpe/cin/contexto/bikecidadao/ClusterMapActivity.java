package br.ufpe.cin.contexto.bikecidadao;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bikecidadao.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.ufpe.cin.br.adapter.bikecidadao.Marcador;
import br.ufpe.cin.br.adapter.bikecidadao.Ocorrencia;
import br.ufpe.cin.contexto.bikecidadao.async.AsyncGetOcurrences;
import br.ufpe.cin.util.bikecidadao.OnGetOccurrencesCompletedCallback;

/**
 * Simple activity demonstrating ClusterManager.
 */
public class ClusterMapActivity extends AppCompatActivity
                                implements OnGetOccurrencesCompletedCallback, OnMapReadyCallback {

    private GoogleMap googleMap;
    private ClusterManager<Marcador> mClusterManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clustermap);
        setActivityEnvironment();
        setUpMap();
    }

    private void setActivityEnvironment() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        if (googleMap != null) {
            return;
        }

        this.googleMap = map;

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-8.05, -34.88), 5));

        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setBuildingsEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        mClusterManager = new ClusterManager<Marcador>(this, this.googleMap);
        this.googleMap.setOnCameraChangeListener(mClusterManager);

        try {
            AsyncGetOcurrences asyncGetOcurrences = new AsyncGetOcurrences(ClusterMapActivity.this);
            asyncGetOcurrences.execute();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getText(R.string.unknow_error), Toast.LENGTH_LONG).show();
        }
    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public void onGetOccurrencesCompleted(List<Ocorrencia> occurrences) {

        List<Marcador> list = new ArrayList<>();;
        Iterator<Ocorrencia> it = null;

        if (occurrences != null) {

            it = occurrences.iterator();

            while (it.hasNext()) {
                Ocorrencia o = it.next();
                list.add(new Marcador(new Double(o.getLat()), new Double(o.getLng())));

            }

            mClusterManager.addItems(list);

        } else {
            Toast.makeText(getApplicationContext(), getText(R.string.no_occurences), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}