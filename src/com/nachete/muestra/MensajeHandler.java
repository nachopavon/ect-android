package com.nachete.muestra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.os.Message;
import android.util.Log;

import com.nachete.muestra.RestClient.RequestMethod;

public class MensajeHandler {
  static RestClient elgg ;
  	public MensajeHandler(String url){
	 elgg = new RestClient(url);
  }
	
	public String obtenerToken(String usuario, String clave) throws JSONException {
	elgg.AddParam ("method", "auth.gettoken");
	elgg.AddParam("username", usuario);
	elgg.AddParam("password", clave);
	try {
	    elgg.Execute(RequestMethod.POST);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	 
	JSONObject response = null;

		response = new JSONObject(elgg.getResponse());

		return response.getString("result");

	}
	
	public String enviarMensajePOST(String token, String usuario, String texto) throws JSONException {
	elgg.AddParam ("method", "wire.save_post");
	elgg.AddParam("auth_token", token);
	elgg.AddParam("username", usuario);
	elgg.AddParam("text", texto);
	try {
	    elgg.Execute(RequestMethod.POST);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	JSONObject response2 = null;
	response2 = new JSONObject(elgg.getResponse());
	if (response2.getJSONObject("result").getString("success").equalsIgnoreCase("true"))
		return "Su status se actualizó correctamente";
	else 
		return "No se pudo actualizar su estado";
		}
	
	
	
	public List<MensajeBean> recogerMensajes(String token, String usuario)throws JSONException {
		elgg.AddParam("method", "wire.get_friends_posts");
		elgg.AddParam("auth_token", token);
		elgg.AddParam("username", usuario);
		try {
	    	elgg.Execute(RequestMethod.GET);
	    	Log.i("HOLA", elgg.getResponse());
		} catch (Exception e) {
			e.printStackTrace();
		
		
	}
	List<MensajeBean> resultado = new ArrayList<MensajeBean>();
	
		JSONObject response_objects = new JSONObject(elgg.getResponse());

		Log.i("Class objeto result -> ", response_objects.get("result").getClass().getName());
		
		JSONObject temp = response_objects.getJSONObject("result");
		
		Iterator< String> keys = temp.keys();
		String key;
		String value;
		MensajeBean mensaje;
		while (keys.hasNext()) {
			mensaje = new MensajeBean();
			resultado.add(mensaje);
			key = (String) keys.next();
			Log.i("key -> ", key.toString());
			mensaje.setStatus_id(key);
			mensaje.setStatus_text(temp.getJSONObject(key).getString("description"));
			mensaje.setStatus_time_created(temp.getJSONObject(key).getString("time_created"));
			mensaje.setStatus_username(temp.getJSONObject(key).getString("username"));
			
		}
		return resultado;
	
	
	}
	public List<MensajeBean> recogerBlogs(String token, String usuario)throws JSONException {
		elgg.AddParam("method", "blog.get_friends_posts");
		elgg.AddParam("auth_token", token);
		elgg.AddParam("username", usuario);
		try {
	    	elgg.Execute(RequestMethod.GET);
	    	Log.i("HOLA", elgg.getResponse());
		} catch (Exception e) {
			e.printStackTrace();
	
		}
		List<MensajeBean> resultado = new ArrayList<MensajeBean>();
		
		JSONObject response_objects = new JSONObject(elgg.getResponse());

		Log.i("Class objeto result -> ", response_objects.get("result").getClass().getName());
		
		JSONObject temp = response_objects.getJSONObject("result");
		
		Iterator< String> keys = temp.keys();
		String key;
		String value;
		MensajeBean mensaje;
		while (keys.hasNext()) {
			mensaje = new MensajeBean();
			resultado.add(mensaje);
			key = (String) keys.next();
			Log.i("key -> ", key.toString());
			mensaje.setStatus_id(key);
			mensaje.setStatus_blog(temp.getJSONObject(key).getString("content"));
			mensaje.setStatus_text(temp.getJSONObject(key).getString("title"));
			mensaje.setStatus_time_created(temp.getJSONObject(key).getString("time_created"));
			mensaje.setStatus_username(temp.getJSONObject(key).getString("username"));
		}
		return resultado;
	}
	
}



