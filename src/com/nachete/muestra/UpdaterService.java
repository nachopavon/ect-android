package com.nachete.muestra;

import java.util.List;

import org.json.JSONException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Responsible for pulling twitter updates from twitter.com and putting it into
 * the database.
 */
public class UpdaterService extends Service {
	static final String TAG = "UpdaterService";
	static final String ACTION_NEW_TWITTER_STATUS = "ACTION_NEW_TWITTER_STATUS";
  MensajeHandler elgg;
  Handler handler;
  Updater updater;
  DbHelper dbHelper;
  SQLiteDatabase db;
  SharedPreferences prefs;

  @Override
  public void onCreate() {
    super.onCreate();
    // Get shared preferences
    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
          public void onSharedPreferenceChanged(SharedPreferences arg0,
              String arg1) {
            elgg = null;
          }
        });

    // Setup handler
    handler = new Handler();

    // Initialize DB
    dbHelper = new DbHelper(this);
    db = dbHelper.getWritableDatabase();
  }

  @Override
  public void onStart(Intent i, int startId) {
    super.onStart(i, startId);
    updater = new Updater();
    handler.post(updater);
    Log.d(TAG, "onStart'ed");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    handler.removeCallbacks(updater); // stop the updater
    db.close();
    Log.d(TAG, "onDestroy'd");
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  /** Updates the database from twitter.com data */
  class Updater implements Runnable {
	  static final int NOTIFICATION_ID = 47;
    static final long DELAY = 100000L;
    Notification notification;
    NotificationManager notificationManager;
    PendingIntent pendingIntent;
    
    Updater() {
        notificationManager = (NotificationManager) UpdaterService.this
            .getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification( android.R.drawable.stat_sys_download,
            "Elgg", System.currentTimeMillis());
        pendingIntent = PendingIntent.getActivity(UpdaterService.this, 0,
            new Intent(UpdaterService.this, Timeline.class), 0);
      }

    public void run() {
    	boolean haveNewStatus = false;
        Log.d(UpdaterService.TAG, "Updater ran.");
      Log.d(UpdaterService.TAG, "Updater ran.");
      String username = prefs.getString("username", null);
      String password = prefs.getString("password", null);
      try {
    	//
    	  
    	  List<MensajeBean> timeline = elgg().recogerMensajes(elgg.obtenerToken(username, password), username);
          for (MensajeBean status : timeline) {
            ContentValues values = DbHelper.statusToContentValues(status);
			// TODO Auto-generated catch block

          // Insert will throw exceptions for duplicate IDs 
          try {
            db.insertOrThrow(DbHelper.TABLE, null, values );
            Log.d(TAG, "run() got new status: " + status.getStatus_text());
            haveNewStatus = true;
          } catch(SQLException d) {d.printStackTrace();}
          Log.d(TAG, "Got status: " + status.toString());
        }
      } catch (JSONException e) {
        Log.e(TAG, "Updater.run exception: " + e);
     
      }
      try {
      	//
      	  
      	  List<MensajeBean> timeline = elgg().recogerBlogs(elgg.obtenerToken(username, password), username);
            for (MensajeBean status : timeline) {
              ContentValues values = DbHelper.statusToContentValues(status);
  			// TODO Auto-generated catch block

            // Insert will throw exceptions for duplicate IDs 
            try {
              db.insertOrThrow(DbHelper.TABLE, null, values );
              Log.d(TAG, "run() got new status: " + status.getStatus_text());
              haveNewStatus = true;
            } catch(SQLException d) {d.printStackTrace();}
            Log.d(TAG, "Got blog: " + status.toString());
          }
        } catch (JSONException e) {
          Log.e(TAG, "Updater.run exception: " + e);
       
        }
      
   // If there's new status, send a broadcast & notify user
      if (haveNewStatus) {
        sendBroadcast(new Intent(ACTION_NEW_TWITTER_STATUS));
        Log.d(TAG, "run() sent ACTION_NEW_TWITTER_STATUS broadcast.");

        // Create the notification
        notification.setLatestEventInfo(UpdaterService.this,
            "Actualización de Elgg", "Tienes nuevos mensajes en Elgg",
            pendingIntent);
        notification.when = System.currentTimeMillis();

        notificationManager.notify(NOTIFICATION_ID, notification);
      }
      // Set this to run again later
      handler.postDelayed(this, DELAY);
    }
  }

  // Initializes twitter, if needed
  private MensajeHandler elgg() {
    if (elgg == null) {
      // TODO Fix case when login doesn't work
      String username = prefs.getString("username", "");
      String password = prefs.getString("password", "");

      if (username != null && password != null)
        elgg = new MensajeHandler(MyElgg.ELGG_SERVICES);    	  
    }
    return elgg;
  }

}
