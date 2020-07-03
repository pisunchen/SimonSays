package com.example.memory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener {

    SharedPreferences preferences;
    String dataName = "data";
    String intName = "int";
    int defaultInt = 0;
    int highScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(dataName, MODE_PRIVATE);
        highScore = preferences.getInt(intName, defaultInt);

        TextView textHighScore = findViewById(R.id.highScore);
        textHighScore.setText("High Score: "+ highScore);

        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }
}


