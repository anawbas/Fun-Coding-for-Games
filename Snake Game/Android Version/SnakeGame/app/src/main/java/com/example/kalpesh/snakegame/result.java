package com.example.kalpesh.snakegame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

public class result extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        TextView highScoreLabel = (TextView) findViewById(R.id.highScoreLabel);

        int score = getIntent().getIntExtra("SCORE", 0);
        scoreLabel.setText("Score : " + score + "");

        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        int highScore = settings.getInt("HIGH_SCORE", 0);

        if(score > highScore){
            highScore = score;
            //save
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", score);
            editor.commit();
        }

        highScoreLabel.setText("High Score : " + highScore);


    }

    public void tryAgain(View view){
        startActivity(new Intent(getApplicationContext(),start.class));
    }

    public void exit(View view){
        ActivityCompat.finishAffinity(result.this);
    }
}
