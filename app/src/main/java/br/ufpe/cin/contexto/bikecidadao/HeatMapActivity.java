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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.br.adapter.bikecidadao.AdapterRoute;
import br.ufpe.cin.br.adapter.bikecidadao.Entity;
import br.ufpe.cin.br.adapter.bikecidadao.Rota;
import br.ufpe.cin.contexto.bikecidadao.async.AsyncGetOcurrences;
import br.ufpe.cin.db.bikecidadao.RemoteRepositoryController;
import br.ufpe.cin.util.bikecidadao.ConnectivityUtil;

public class HeatMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RemoteRepositoryController remoteRepositoryController;

    private GoogleMap googleMap;

    private double latitude = -8.05;
    private double longitude = -34.88;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);
        setActivityEnvironment();
        initVariables();

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

    private void initVariables() {

        remoteRepositoryController = new RemoteRepositoryController(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        String result = null;
        List<Entity> contextElement = null;
        List<Rota> routes = null;
        List<LatLng> list = new ArrayList<>();
        HeatmapTileProvider mProvider;

        LatLng local = new LatLng(latitude, longitude);

        this.googleMap = map;

        //this.googleMap.setOnMapLongClickListener(this);
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setBuildingsEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        try {

            // busca os pontos com base na lat/long e raio
            result = remoteRepositoryController.getRoutes(latitude, longitude);

            // se a consulta retornou vazio {"errorCode":{"code":"404","reasonPhrase":"No context element found"}}
            if (result != null && result.contains("No context element found")) {
                Toast.makeText(getApplicationContext(), getText(R.string.no_routes), Toast.LENGTH_LONG).show();

            // se foram encontradas rotas para plotagem do mapa
            } else if (result != null) {

                // parser do objeto JSON
                contextElement = AdapterRoute.parseListEntity(result);
                routes = new ArrayList<Rota>();

                // do JSON para List de Rotas
                for (Entity entity : contextElement) {
                    // de cada lista de rotas para seus pontos
                    list.addAll(AdapterRoute.toRoute(entity).getPontos());
                }

                // cria o mapa de calor
                mProvider = new HeatmapTileProvider.Builder().data(list).build();
                map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

            // se resultado = null (houve um erro desconhecido)
            } else {
                Toast.makeText(getApplicationContext(), getText(R.string.unknow_error), Toast.LENGTH_LONG).show();
            }

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

}