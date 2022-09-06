package com.reckordp.berkirimfile;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PenerimaAdapter extends  RecyclerView.Adapter<PenerimaViewHolder> {
    private List<JukirServer> deretPenerima;
    private InetAddress ipDiri = null;
    private final Thread pencariServer = new Thread(() -> {
        if (ipDiri == null) return;
        String hostName = ipDiri.getHostName();
        String asal = hostName.substring(0, hostName.lastIndexOf(".") + 1);
        try {
            deretPenerima.add(new JukirServer(InetAddress.getByName(asal + 2)));
            notifyItemChanged(0);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    });


    public PenerimaAdapter() {
        deretPenerima = new ArrayList<>();
    }

    public void taruhIp(InetAddress ip) {
        ipDiri = ip;
        pencariServer.start();
    }

    @NonNull
    @Override
    public PenerimaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int res = deretPenerima.isEmpty() ? R.layout.layout_memuat : R.layout.layout_penerima;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup vg = (ViewGroup) inflater.inflate(res, parent, false);
        return new PenerimaViewHolder(vg);
    }

    @Override
    public void onBindViewHolder(@NonNull PenerimaViewHolder holder, int position) {
        if (deretPenerima.isEmpty()) return;
        holder.taruhServer(deretPenerima.get(position));
    }

    @Override
    public int getItemCount() { return deretPenerima.isEmpty() ? 1 : deretPenerima.size(); }
}