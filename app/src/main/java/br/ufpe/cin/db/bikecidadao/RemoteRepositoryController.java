package br.ufpe.cin.db.bikecidadao;

import android.content.Context;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.ufpe.cin.br.adapter.bikecidadao.Attributes;
import br.ufpe.cin.br.adapter.bikecidadao.Entity;
import br.ufpe.cin.br.adapter.bikecidadao.Metadata;
import br.ufpe.cin.db.bikecidadao.model.GeoLocation;
import br.ufpe.cin.db.bikecidadao.model.TrackInfo;
import br.ufpe.cin.util.bikecidadao.IdGenerator;

/**
 * Created by jal3 on 12/02/2016.
 */
public class RemoteRepositoryController {

    private Context context;
    private Gson gson;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public RemoteRepositoryController(Context context){
        this.context = context;
        initVariables();
    }

    private void initVariables(){
        gson = new Gson();
    }

    public void saveTracking(TrackInfo trackInfo) {

        boolean primeiro;
        int contador;
        Entity entity = new Entity();
        String id = String.valueOf(IdGenerator.generateUniqueId());
        List<Attributes> attributes = new ArrayList<Attributes>();
        //attributes.add(new Attributes("momentoInicio", "String",  AdapterOcurrence.df.format(trackInfo.getStartTime()) +"", null));
        //attributes.add(new Attributes("momentofim", "String", AdapterOcurrence.df.format(trackInfo.getStartTime()) +"", null));
        //attributes.add(new Attributes("distanciaTotal", "String", trackInfo.getDistance()+"", null));
        //attributes.add(new Attributes("tempoTotal", "String", trackInfo.getElapsedTime()+"", null));

        Collection<GeoLocation> col = trackInfo.getTrackingPoints();
        Iterator<GeoLocation> it = col.iterator();

        primeiro = true;
        for (contador = 1; it.hasNext(); contador++) {

            GeoLocation geoLocation = it.next();

            if (primeiro) {
                List<Metadata> metadatas = new ArrayList<Metadata>();
                metadatas.add(new Metadata("location", "String", "WGS84"));
                attributes.add(new Attributes("largada","coords", geoLocation.getLatitude() + ", " + geoLocation.getLongitude(), metadatas));
                primeiro = false;
            }

            attributes.add(new Attributes("p" + contador,"String", geoLocation.getLatitude() + ", " + geoLocation.getLongitude(), null));

        }

        entity.setType("Route");
        entity.setId(id);
        entity.setAttributes(attributes);

        Gson gson = new Gson();
        String uri = "http://130.206.112.159:1026/v1/contextEntities/";
        uri += id;

        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, gson.toJson(entity));
            Request request = new Request.Builder().url(uri).post(body).build();
            Response response;

            int executeCount = 0;
            do {
                response = client.newCall(request).execute();
                executeCount++;
            }   while(response.code() == 408 && executeCount < 5);

            /*String str1 = response.toString();
            String str2 = gson.toJson(entity);
            String str3 = request.toString();

            System.out.println(str1);*/


        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /*public String getRoutes(double pLatitude, double pLongitude) throws Exception {

        int responseCode = 0;
        String json = "";
        String result= "";
        String line = "";

        BufferedReader rd;
        try {
            String uri = "http://130.206.112.159:1026/v1/queryContext";
            String getAll = "{\"entities\": [{\"type\": \"Route\",\"isPattern\": \"true\",\"id\": \".*\"}],\"restriction\": " +
                    "{\"scopes\": [{\"type\" : \"FIWARE::Location\",\"value\" : {\"circle\": {\"centerLatitude\": \"" +
                    pLatitude +"\",\"centerLongitude\": \"" +pLongitude +"\",\"radius\": \"2000\"}}}]}}";
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, getAll);
            Request request = new Request.Builder()
                    .url(uri)
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .build();

            Response response;

            int executeCount = 0;
            do
            {
                response = client.newCall(request).execute();
                executeCount++;
            }
            while(response.code() == 408 && executeCount < 5);

            result = response.body().string();
            json = new JSONObject(result).toString();

        } catch (Exception e) {
            responseCode = 408;
            e.printStackTrace();
        }

        return json;

    }*/
}
