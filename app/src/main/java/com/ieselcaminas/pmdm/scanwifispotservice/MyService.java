package com.ieselcaminas.pmdm.scanwifispotservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {
    public MyService() {
    }

    MyAsyncTask myAsyncTask=null;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate(){
        super.onCreate();

        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();



    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        myAsyncTask.exit=true;
        myAsyncTask.cancel(true);
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        public boolean exit;





        @Override
        protected Void doInBackground(Void... voids) {

            final WifiManager wifiManager = (WifiManager) MyService.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context c, Intent intent) {
                    if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                        List<ScanResult> mScanResults = wifiManager.getScanResults();
                        saveWifiSpots(mScanResults);
                    }
                }

                private void saveWifiSpots(List<ScanResult> mScanResults) {
                    for (ScanResult s : mScanResults) {
                        ContentValues values = new ContentValues();
                        values.put("bssid", s.BSSID);
                        values.put("ssid", s.SSID);
                        values.put("security", s.capabilities);
                        values.put("description", "");
                        String uriStr = "content://net.victoralonso.unit7.creatingwificontentprovider/Wifi";
                        Uri usersUri = Uri.parse(uriStr);
                        ContentResolver cr = getContentResolver();
                        cr.insert(usersUri, values);
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            registerReceiver(mWifiScanReceiver, intentFilter);

            exit = false;
            while (!exit) {
                wifiManager.startScan();
                try {
                    Thread.sleep(1000 * 5);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }
}
