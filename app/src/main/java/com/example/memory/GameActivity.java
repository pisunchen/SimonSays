package com.example.memory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.Random;

public class GameActivity extends Activity implements View.OnClickListener {
    private SoundPool soundPool;
    private Handler handler;
    private int sample1;
    private int sample2;
    private int sample3;
    private int sample4;
    private TextView textScore;
    private TextView textDifficulty;
    private TextView textSeeRecreate;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button buttonReplay;
    private int difficultyLevel = 1;
    private int[] sequenceToCopy = new int[100];
    private boolean playSequence = false;
    private int toPlay = 0;
    private int playerResponse;
    private int playerScore = 0;
    boolean isResponding;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String dataName = "data";
    String intName = "int";
    int defaultInt = 0;
    int highScore;
    Animation wobble;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        wobble = AnimationUtils.loadAnimation(this, R.anim.wobble);

        preferences = getSharedPreferences(dataName, MODE_PRIVATE);
        editor = preferences.edit();
        highScore = preferences.getInt(intName, defaultInt);

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("sample1.ogg");
            sample1 = soundPool.load(descriptor,0);
            descriptor = assetManager.openFd("sample2.ogg");
            sample2 = soundPool.load(descriptor,0);
            descriptor = assetManager.openFd("sample3.ogg");
            sample3 = soundPool.load(descriptor,0);
            descriptor = assetManager.openFd("sample4.ogg");
            sample4 = soundPool.load(descriptor,0);
        } catch (IOException e) {
            System.out.println("exception caught");
        }

        textScore = findViewById(R.id.textScore);
        textScore.setText("Score: " + playerScore);
        textDifficulty = findViewById(R.id.textDifficulty);
        textDifficulty.setText("Level: " + difficultyLevel);
        textSeeRecreate = findViewById(R.id.textWatchGo);

        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        buttonReplay = findViewById(R.id.buttonReplay);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        buttonReplay.setOnClickListener(this);

        handler = new Handler() {
            public void handleMessage(Message m) {
                super.handleMessage(m);
                if (playSequence) {
                    switch (sequenceToCopy[toPlay]) {
                        case 1:
                            button1.startAnimation(wobble);
                            soundPool.play(sample1,1,1,0,0,1);
                            break;
                        case 2:
                            button2.startAnimation(wobble);
                            soundPool.play(sample2,1,1,0,0,1);
                            break;
                        case 3:
                            button3.startAnimation(wobble);
                            soundPool.play(sample3,1,1,0,0,1);
                            break;
                        case 4:
                            button4.startAnimation(wobble);
                            soundPool.play(sample4,1,1,0,0,1);
                            break;
                    }
                    toPlay++;
                    if (toPlay == difficultyLevel) {
                        sequenceFinished();
                    }
                }
                handler.sendEmptyMessageDelayed(0,900);
            }
        };
        handler.sendEmptyMessage(0);
    }

    public void createSequence() {
        Random random = new Random();
        int randomIter;
        for (int i = 0; i < difficultyLevel; i++) {
            randomIter = random.nextInt(4);
            randomIter++;
            sequenceToCopy[i] = randomIter;
        }
    }

    public void playASequence() {
        createSequence();
        textDifficulty.setText("Level: " + (difficultyLevel - 2));
        isResponding = false;
        toPlay = 0;
        playerResponse = 0;
        textSeeRecreate.setText("MEMORIZE THE PATTERN!");
        playSequence = true;
    }

    public void sequenceFinished() {
        playSequence = false;
        textSeeRecreate.setText("RECREATE THE PATTERN");
        isResponding = true;
    }



    @Override
    public void onClick(View v) {
        if (!playSequence) {
            switch (v.getId()) {
                case R.id.button:
                    soundPool.play(sample1, 1, 1, 0, 0, 1);
                    checkElement(1);
                    break;
                case R.id.button2:
                    soundPool.play(sample2, 1, 1, 0, 0, 1);
                    checkElement(2);
                    break;
                case R.id.button3:
                    soundPool.play(sample3, 1, 1, 0, 0, 1);
                    checkElement(3);
                    break;
                case R.id.button4:
                    soundPool.play(sample4, 1, 1, 0, 0, 1);
                    checkElement(4);
                    break;
                case R.id.buttonReplay:
                    difficultyLevel = 3;
                    playerScore = 0;
                    textScore.setText("Score: "+ playerScore);
                    playASequence();
                    break;
            }
        }
    }

    public void checkElement(int thisElement) {
        if (isResponding) {
            playerResponse++;
            if (sequenceToCopy[playerResponse -1] == thisElement) {
                playerScore += ((thisElement + 1) * 2);
                textScore.setText("Score :" + playerScore);
                if (playerResponse == difficultyLevel) {
                    isResponding = false;
                    difficultyLevel++;
                    playASequence();
                }
            }else {
                    textSeeRecreate.setText("INCORRECT!");;
                    isResponding = false;
                    if (playerScore > highScore) {
                        highScore = playerScore;
                        editor.putInt(intName, highScore);
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "New High Score Achieved", Toast.LENGTH_LONG).show();
                    }
            }
        }
    }



}
