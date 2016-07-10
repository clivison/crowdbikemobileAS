package br.ufpe.cin.br.adapter.bikecidadao;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.ufpe.cin.db.bikecidadao.model.GeoLocation;


public class AdapterRoute {
	
	public static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
    
    private static Entity parseEntity(String s) {
    	Entity e = new Entity(); 
    	Gson gson = new Gson();
    	List<Attributes> lAtt = new ArrayList<Attributes>();
    	try {
    		JSONParser jsonParser = new JSONParser();
    		JSONObject jsonObject = (JSONObject) jsonParser.parse(s);
    		JSONObject structure = (JSONObject) jsonObject.get("contextElement");
    		Type listType = new TypeToken<ArrayList<Attributes>>() {}.getType();
            lAtt =  gson.fromJson(structure.get("attributes").toString(), listType);
     
            e.setId(structure.get("id").toString());
            e.setType(structure.get("type").toString());
            e.setAttributes(lAtt);
            
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
		return e;
    }

    public static List<Entity> parseListEntity(String s) throws Exception {
		List<Entity> listEntity = new ArrayList<Entity>();
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(s.trim());
		JSONArray lang = (JSONArray) jsonObject.get("contextResponses");
		if(lang != null){
			Iterator i = lang.iterator();
			// take each value from the json array separately
		    while (i.hasNext()) {
				JSONObject innerObj = (JSONObject) i.next();
				if(innerObj != null)
				listEntity.add(AdapterRoute.parseEntity(innerObj.toString()));
			}
		}
		return listEntity;

    }
	public static Rota toRoute(Entity e) throws ParseException {

		int contador;
		double latitude;
		double longitude;
		Rota r = new Rota();
		//r.setIdRota(Long.parseLong(e.getId()));

		contador = 1;
		for (Attributes att : e.getAttributes()) {

				if (att.getName().equals("p" + contador)) {

					String[] tokensVal = att.getValue().split(",");
					latitude = new Double(tokensVal[0].trim());
					longitude = new Double(tokensVal[1].trim());

					LatLng umPonto = new LatLng(latitude, longitude);
					r.addPonto(umPonto);
					contador++;
				}
		}

		return r;

	}



}
