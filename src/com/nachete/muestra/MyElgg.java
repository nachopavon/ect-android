package com.nachete.muestra;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyElgg extends Activity implements OnClickListener{
	//SharedPreferences prefs;
	public static final String ELGG_PREFERENCES = "ElggPrefs";
    public static final String ELGG_PREFERENCES_USERNAME = "Username"; // String
    public static final String ELGG_PREFERENCES_PASSWORD = "Password"; // String
    public static final String ELGG_BASE_URL = "http://10.238.85.89/elgg/";
    public static final String ELGG_SERVICES = ELGG_BASE_URL + "/services/api/rest/json/";
//    Mensaje mensaje;
    
    Button buttonUpdate;
    Button buttonPrefs;
    EditText textStatus;

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
     // find views by id
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        textStatus = (EditText) findViewById(R.id.textStatus);
        // Add listener
        buttonUpdate.setOnClickListener(this);
        
    }

   @Override
   public void onResume() {
     super.onResume();
     // Start the UpdaterService
     startService(new Intent(this, UpdaterService.class));
   }
   @Override
   public void onStop() {
     super.onStop();
     // Stop the UpdaterService
     stopService(new Intent(this, UpdaterService.class));
   }
   @Override
   public void onClick(View src) {
       
       final String mensaje = textStatus.getText().toString();
        Log.d("HOLA", "Clicked on "+ mensaje);
        
        // Toast
        Toast.makeText(this, textStatus.getText(), Toast.LENGTH_LONG).show();
        //Send Elgg
        //Initialize Elgg
        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String url = prefs.getString("url", "n/a");
        String username = prefs.getString("username", "n/a");
        String password = prefs.getString("password", "n/a");

        if (username != null && password != null){
        MensajeHandler mielgg = new MensajeHandler(ELGG_SERVICES);
        try {
//        	String mensaje = textStatus.getText().toString();
  			mielgg.enviarMensajePOST(mielgg.obtenerToken(username, password), username, textStatus.getText().toString());
  		} catch (JSONException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
        //reset status string
        textStatus.setText("");
        }
      }
   @Override
   public boolean onCreateOptionsMenu(Menu menu){
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.menu, menu);
     return true;
   }
   /** Called when menu item is selected **/
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
     switch (item.getItemId()) {
     case R.id.menuPrefs:
       startActivity(new Intent(this, Prefs.class));
       break;
     case R.id.menuTimeline:
       startActivity(new Intent(this, Timeline.class));
     }
     return true;
   }

   /*    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
     menu.add("Settings");
//     menu.add("Help");
     return true;
    }*/
    // Called when menu item is selected //
/*    @Override
    public boolean onOptionsItemSelected(MenuItem item){
      
      switch(item.getItemId()){
      
      case R.id.menuPrefs:
        // Launch Prefs activity
        Intent i = new Intent(MyElgg.this, Prefs.class);
        startActivity(i);
        Log.d("HOLA", "MenuPrefs starting Prefs");
        Toast.makeText(MyElgg.this, textStatus.getText(), Toast.LENGTH_LONG).show();
        break;
      }
      return true;
      }
*/

}