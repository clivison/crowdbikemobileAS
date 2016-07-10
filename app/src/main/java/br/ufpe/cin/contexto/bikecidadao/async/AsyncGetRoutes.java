package br.ufpe.cin.contexto.bikecidadao.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.br.adapter.bikecidadao.AdapterRoute;
import br.ufpe.cin.br.adapter.bikecidadao.Entity;
import br.ufpe.cin.db.bikecidadao.RemoteRepositoryController;
import br.ufpe.cin.util.bikecidadao.OnGetRoutesCompletedCallback;

/**
 * Created by jairson on 09/07/16.
 */
public class AsyncGetRoutes extends AsyncTask<String, Void, List<LatLng>> {

    private Context contexto;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public AsyncGetRoutes(Context ctx) {
        this.contexto = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<LatLng> doInBackground(String... params) {

        double latitude = -8.05;
        double longitude = -34.88;

        List<LatLng> result = null;

        try {
            result = this.getRoutes(latitude, longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    @Override
    protected void onPostExecute(List<LatLng> pontos) {
        Log.v("AsyncGetRoutes", "Retorno do servidor / Async Routes");
        super.onPostExecute(pontos);

        ((OnGetRoutesCompletedCallback) contexto).onGetRoutesCompleted(pontos);
    }

    private List<LatLng> getRoutes(double pLatitude, double pLongitude) throws Exception {

        String json = "";
        String result= "";
        String line = "";

        List<Entity> contextElement = null;
        List<LatLng> list = new ArrayList<>();

        try {
            String uri = "http://130.206.112.159:1026/v1/queryContext";
            String getAll = "{\"entities\": [{\"type\": \"Route\",\"isPattern\": \"true\",\"id\": \".*\"}],\"restriction\": " +
                    "{\"scopes\": [{\"type\" : \"FIWARE::Location\",\"value\" : {\"circle\": {\"centerLatitude\": \"" +
                    pLatitude +"\",\"centerLongitude\": \"" +pLongitude +"\",\"radius\": \"2000\"}}}]}}";
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, getAll);
            Response response;
            Request request = new Request.Builder()
                    .url(uri)
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .build();

            int executeCount = 0;

            do {
                response = client.newCall(request).execute();
                executeCount++;
            } while(response.code() == 408 && executeCount < 5);

            result = response.body().string();
            json = new JSONObject(result).toString();


            if (json != null && !json.contains("No context element found")) {
                // parser do objeto JSON
                contextElement = AdapterRoute.parseListEntity(result);
                // do JSON para List de Rotas
                for (Entity entity : contextElement) {
                    // de cada lista de rotas para seus pontos
                    list.addAll(AdapterRoute.toRoute(entity).getPontos());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }
}
