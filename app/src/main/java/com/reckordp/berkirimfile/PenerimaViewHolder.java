package com.reckordp.berkirimfile;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class PenerimaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    static final String ALAMAT_SERVER = "ALAMAT SERVER";
    private JukirServer infoServer;
    private Context ctx;

    public PenerimaViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        ctx = itemView.getContext();
    }

    public void taruhServer(JukirServer jukir) {
        infoServer = jukir;
        ((TextView)itemView.findViewById(R.id.nama_inet)).setText(jukir.host);
    }

    @Override
    public void onClick(View v) {
        Class<?> ke = infoServer.manual ? ServerManual.class : RuangTunggu.class;
        Intent intent = new Intent(ctx, ke);
        intent.putExtra(ALAMAT_SERVER, infoServer.host);
        ctx.startActivity(intent);
    }
}
