package com.example.qrcodescanner_ti21c2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //view Objects
    private Button buttonScanning;
    private TextView textViewName, textViewClass, textViewId;
    //qr code scanner
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // View Object
        buttonScanning = (Button) findViewById(R.id.buttonScan);
        textViewName = (TextView) findViewById(R.id.textViewNama);
        textViewClass = (TextView) findViewById(R.id.textViewKelas);
        textViewId = (TextView) findViewById(R.id.textViewNim);

        //insialisasi scan object
        qrScan = new IntentIntegrator(this);

        //implementasi onclick listener
        buttonScanning.setOnClickListener(this);
    }

    //untuk hasil scanning
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //jika qrcode tidak ada sama sekali
            if (result.getContents() == null) {
                Toast.makeText(this, "Hasil SCANNING tidak ada", Toast.LENGTH_LONG).show();
            }else if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                Intent visitUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                startActivity(visitUrl);
            }else if (result.getContents().contains("tel:")) {
                //Mendapat data kode telpon
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(result.getContents()));
                startActivity(intent);

                String lokasiku = String.valueOf(result.getContents());
                Uri gmmIntentUri = Uri.parse(lokasiku);
                Intent mapIntent = new Intent((Intent.ACTION_VIEW), gmmIntentUri);

                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
            String alamat = result.getContents();
            String at = "@gmail";

            if(alamat.contains(at)) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipients = {alamat.replace("http://", "")};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Email");
                intent.putExtra(Intent.EXTRA_TEXT, "Type Here");
                intent.putExtra(Intent.EXTRA_CC, "");
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));
            } else{
                //jika qrcode ada/ditemukan datanya
                try {
                    //Konversi datanya ke json
                    JSONObject obj = new JSONObject(result.getContents());
                    //di set nilai datanya ke textview
                    textViewName.setText(obj.getString("nama"));
                    textViewClass.setText(obj.getString("kelas"));
                    textViewId.setText(obj.getString("nim"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        qrScan.initiateScan();
    }
}