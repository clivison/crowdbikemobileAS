package br.ufpe.cin.contexto.bikecidadao;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bikecidadao.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.br.adapter.bikecidadao.Entity;
import br.ufpe.cin.contexto.bikecidadao.async.AsyncGetRoutes;
import br.ufpe.cin.util.bikecidadao.ConnectivityUtil;
import br.ufpe.cin.util.bikecidadao.OnGetRoutesCompletedCallback;

public class HeatMapActivity extends AppCompatActivity implements OnMapReadyCallback, OnGetRoutesCompletedCallback {



    private GoogleMap googleMap;

    private double latitude = -8.05;
    private double longitude = -34.88;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);
        setActivityEnvironment();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(!ConnectivityUtil.isNetworkAvaiable(this)){
            Toast.makeText(getApplicationContext(), getText(R.string.no_network_avaible), Toast.LENGTH_LONG).show();
        }
    }

    private void setActivityEnvironment() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        String result = null;
        List<Entity> contextElement = null;
        List<LatLng> list = new ArrayList<>();
        LatLng local = new LatLng(latitude, longitude);

        this.googleMap = map;
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setBuildingsEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        try {

            // busca as rotas assincronamente, para melhorar a percepção de retorno do usuário
            AsyncGetRoutes asyncGetRoutes = new AsyncGetRoutes(HeatMapActivity.this);
            asyncGetRoutes.execute();

        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), getText(R.string.unknow_error), Toast.LENGTH_LONG).show();
        }

        map.addMarker(new MarkerOptions().position(local).title("Você está aqui!!!"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 14));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // método que inicia quando a tarefa assíncrona getRoutes AsyncGetRoutes completar
    public void onGetRoutesCompleted(List<LatLng> pontos) {
        if(pontos!=null) {
            // cria o mapa de calor
            HeatmapTileProvider mProvider;
            mProvider = new HeatmapTileProvider.Builder().data(pontos).build();
            this.googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        } else {
            // caso não tenham sido encontrados rotas no raio dada a posição lat/lng
            Toast.makeText(getApplicationContext(), getText(R.string.no_routes), Toast.LENGTH_LONG).show();
        }

    }

}