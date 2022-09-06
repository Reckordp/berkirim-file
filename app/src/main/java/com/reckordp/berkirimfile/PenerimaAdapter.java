package com.reckordp.berkirimfile;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class PenerimaAdapter extends RecyclerView.Adapter<PenerimaViewHolder> {
    private List<JukirServer> deretPenerima;
    private InetAddress ipDiri = null;
    private final Thread pencariServer = new Thread(() -> {
        if (ipDiri == null) return;
        JukirServer jukir;
        String hostName = ipDiri.getHostName();
        int pemisahId = hostName.lastIndexOf(".");
        short ukuranId = (short) (hostName.length() - pemisahId - 1);
        String asal = hostName.substring(0, pemisahId + 1);
        int diri = Integer.parseInt(hostName.substring(pemisahId + 1, ukuranId));

        for (int i = 0; i < 255; i++) {
            if (diri == i) continue;
            try {
                jukir = new JukirServer(InetAddress.getByName(asal + i));
                if (jukir.isServer()) deretPenerima.add(jukir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!deretPenerima.isEmpty()) {
            notifyItemChanged(0);
            if (deretPenerima.size() > 1) {
                notifyItemRangeInserted(1, deretPenerima.size() - 1);
            }
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