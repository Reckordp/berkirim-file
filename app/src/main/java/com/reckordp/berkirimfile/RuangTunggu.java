package com.reckordp.berkirimfile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RuangTunggu extends AppCompatActivity {
    private String namaFile;
    private Uri letakFile;
    private long ukuran;
    private long telahDibaca;
    private String tujuan;
    private InputStream pembaca;
    private ProgressBar bar;
    private TextView persen;
    private ViewGroup rootView;
    private PenghantarServer penghantar = null;
    private final Thread paketMengirim = new Thread(() -> {
        boolean sukses = false;
        try {
            bukaAcaraMembaca();
            while (ukuran > telahDibaca && penghantar.err == 0) bacaDanKirim();
            pembaca.close();
            penghantar.close();
            sukses = true;
        } catch (ConnectException e) {
            System.err.println("Tidak ada Server");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!sukses) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setPositiveButton(android.R.string.ok, (dialog1, which) -> finish());
            dialog.setMessage("Pengiriman Gagal");
            dialog.show();
        } else {
            finish();
        }
    });
    private final ActivityResultCallback<ActivityResult> fileDipilih = result -> {
        Intent intentHasil = result.getData();
        if (result.getResultCode() == RESULT_OK && intentHasil != null) {
            prosesMengirim(intentHasil.getData());
        }
    };
    private final ActivityResultLauncher<Intent> memilihFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), fileDipilih);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruang_tunggu);
        pemilihanBarangKirim();
        tujuan = getIntent().getStringExtra(PenerimaViewHolder.ALAMAT_SERVER);
        persen = (TextView)findViewById(R.id.persen);
        bar = (ProgressBar)findViewById(R.id.cendekia);
        rootView = (ViewGroup) bar.getParent();
    }

    private void pemilihanBarangKirim() {
        if (penghantar != null) return;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        memilihFile.launch(intent);
    }

    private void prosesMengirim(Uri letak) {
        Cursor gudangInfo = getContentResolver().query(letak, null,
                null, null, null);
        int indexName = gudangInfo.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int indexSize = gudangInfo.getColumnIndex(OpenableColumns.SIZE);
        gudangInfo.moveToFirst();

        letakFile = letak;
        namaFile = gudangInfo.getString(indexName);
        ukuran = gudangInfo.getLong(indexSize);
        gudangInfo.close();

        if (penghantar == null) {
            AlertDialog.Builder pesan = new AlertDialog.Builder(this);
            pesan.setPositiveButton(android.R.string.ok, null);
            pesan.setMessage(namaFile);
            pesan.show();
            paketMengirim.start();
        }
    }

    private void bukaAcaraMembaca() throws IOException {
        pembaca = getContentResolver().openInputStream(letakFile);
        penghantar = new PenghantarServer(namaFile, tujuan);
        runOnUiThread(() -> {
            persen.setText("1%");
            bar.setProgress(1, true);
            rootView.postInvalidate();
        });
    }

    private void bacaDanKirim() throws IOException {
        byte[] erty = new byte[250];
        int tdb = pembaca.read(erty);
        telahDibaca += (tdb == 0 ? 1 : tdb);
        penghantar.kirim(erty, tdb);

        runOnUiThread(() -> {
            int kini = (int) (telahDibaca * 100 / ukuran);
            persen.setText(String.valueOf(kini).concat("%"));
            bar.setProgress(kini, true);
        });
    }

    static class PenghantarServer {
        static int SOCKET_PORT_SERVER = 7777;

        Socket penghantar;
        InputStream baca;
        OutputStream tulis;
        int err = 0;

        public PenghantarServer(String nama, String tujuan) throws IOException {
            this(new Socket(tujuan, SOCKET_PORT_SERVER));
            panggilanPertama(nama);
        }

        public PenghantarServer(Socket hantar) throws IOException {
            penghantar = hantar;
            baca = penghantar.getInputStream();
            tulis = penghantar.getOutputStream();
        }

        public boolean ujiPort() throws IOException {
            kirimDalam(buatFlag(0x90, 0, null), 2);
            return err == 0;
        }

        public void panggilanPertama(String nama) throws IOException {
            byte[] byteNama = nama.getBytes(StandardCharsets.UTF_8);
            byte[] flag = buatFlag(0x91, nama.length(), byteNama);
            kirimDalam(flag, byteNama.length + 2);
        }

        public void kirim(byte[] keterangan, int panjang) throws IOException {
            kirimDalam(buatFlag(0x92, panjang, keterangan), panjang + 2);
        }

        private byte[] buatFlag(int tanda, int panjang, byte[] asal) {
            byte[] dasar = new byte[255];
            dasar[0] = (byte) tanda;
            dasar[1] = (byte) panjang;
            if (panjang != 0 && asal != null) {
                System.arraycopy(asal, 0, dasar, 2, panjang);
            }
            return dasar;
        }

        private void kirimDalam(byte[] keterangan, int panjang) throws IOException {
            byte[] jawaban = new byte[2];
            tulis.write(keterangan, 0, panjang);
            baca.read(jawaban, 0, 2);
            err = (jawaban[0] == (byte) 0x80) ? 0 : 2;
            tulis.flush();
        }

        public void close() throws IOException {
            tulis.write(buatFlag(0x9f, 0, null), 0, 2);
            tulis.close();
            baca.close();
            penghantar.close();
        }
    }
}