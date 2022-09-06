package com.reckordp.berkirimfile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView penerima = findViewById(R.id.daftar_penerima);
        PenerimaAdapter adapter = new PenerimaAdapter();
        penerima.setAdapter(adapter);
        penerima.setLayoutManager(new LinearLayoutManager(this));
        penerima.setHasFixedSize(true);

        WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
        int ipAddress = wifi.getConnectionInfo().getIpAddress();
        byte[] asal = BigInteger.valueOf(ipAddress).toByteArray();
        byte[] guling = asal.clone();
        for (int i = 0; i < asal.length; i++) {
            asal[i] = guling[asal.length - i - 1];
        }
        try {
            adapter.taruhIp(InetAddress.getByAddress(asal));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}