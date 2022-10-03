package com.reckordp.berkirimfile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ServerManual extends AppCompatActivity {
    private JukirServer server;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Thread testThread = new Thread(() -> {
        boolean benar;
        try {
            benar = server.isServer();
        } catch (ConnectException | SocketTimeoutException e) {
            benar = false;
        }

        boolean finalBenar = benar;
        mainHandler.post(() -> {
            if (finalBenar) {
                Toast.makeText(ServerManual.this, "Server ada", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ServerManual.this, "Tidak ada server", Toast.LENGTH_LONG).show();
            }
        });
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String tujuan;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_manual);

        tujuan = getIntent().getStringExtra(PenerimaViewHolder.ALAMAT_SERVER);
        try {
            server = new JukirServer(InetAddress.getByName(tujuan));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        ((TextView)findViewById(R.id.host)).setText(tujuan);
        findViewById(R.id.button_test).setOnClickListener(this::testTujuan);
        findViewById(R.id.button_kirim).setOnClickListener(v -> {
            Intent intent = new Intent(ServerManual.this, RuangTunggu.class);
            intent.putExtra(PenerimaViewHolder.ALAMAT_SERVER, tujuan);
            startActivity(intent);
        });
    }

    private void testTujuan(View v) {
        testThread.start();
    }
}
