package com.ieselcaminas.pmdm.scanwifispotservice;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.xml.transform.Templates;

public class MainActivity extends AppCompatActivity {
    TextView textResult = null;
    public static final int MY_PERMISSIONS_REQUEST_SCAN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textResult = findViewById(R.id.textView);
    }

    public void startService(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_SCAN);
            Toast.makeText(this, "Asked permision", Toast.LENGTH_SHORT).show();

        } else {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        }

    }

    public void stopService(View view) {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
        print();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SCAN: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED
                        ) {
                    Intent intent = new Intent(this, MyService.class);
                    startService(intent);
                } else {
                    // permission denied
                    Dialog d = new AlertDialog.Builder(MainActivity.this).
                            setTitle("Error").
                            setMessage("I need permission to scan").
                            create();
                    d.show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void print() {

        String[] projection = new String[]{"_id", "bssid", "ssid", "security", "description"};
        String uriStr = "content://net.victoralonso.unit7.creatingwificontentprovider/Wifi";
        Uri usersUri = Uri.parse(uriStr);
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(usersUri, projection, null, null, null);
        if (cur.moveToFirst()) {
            String bssid, ssid, security, description;
            int colBssid = cur.getColumnIndex(projection[1]);
            int colSsid = cur.getColumnIndex(projection[2]);
            int colSecurity = cur.getColumnIndex(projection[3]);
            int colDesc = cur.getColumnIndex(projection[4]);
            textResult.setText("");
            do {
                bssid = cur.getString(colBssid);
                ssid = cur.getString(colSsid);
                security = cur.getString(colSecurity);
                description = cur.getString(colDesc);

                textResult.append(bssid + " - " + ssid + " - " + security + " - " + description + "\n");
            } while (cur.moveToNext());
        }
    }
}
