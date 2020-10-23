package com.example.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity
{
    private Button button;
    private Button button2;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        button = (Button) findViewById(R.id.button10);
        button2 = (Button) findViewById(R.id.button6);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInformation();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelp();
            }
        });
    }

    public void openInformation()
    {
        Intent intent = new Intent(this, Information.class);
        startActivity(intent);

    }

    public void openHelp()
    {
        Intent intent2 = new Intent(this,Help.class);
        startActivity(intent2);
    }
}
