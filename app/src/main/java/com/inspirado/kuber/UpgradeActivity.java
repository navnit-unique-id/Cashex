package com.inspirado.kuber;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class UpgradeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ugrade);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        String url = getIntent().getStringExtra("url");
        String url1 = getIntent().getStringExtra("url1");
        String message = getIntent().getStringExtra("message");
        ((TextView) findViewById(R.id.message)).setText(message);
        Button upgradeBtn = (Button) findViewById(R.id.upgradeBtn);
        Button closeBtn = (Button) findViewById(R.id.CloseBtn);

        upgradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(url)));
                }catch(Exception ex){
                    startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(url1)));
                }
            //    finishAffinity();
             //   System.exit(0);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);                }
        });
    }
}
