package com.example.kalpesh.snakegame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class highscore extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        // to show high score
        TextView highScoreLabel = (TextView) findViewById(R.id.highScoreLabel);
        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        int highScore = settings.getInt("HIGH_SCORE", 0);
        highScoreLabel.setText(highScore + "");
    }

    public void resetHighScore(View view){
        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("HIGH_SCORE", 0);
        editor.commit();
        finish();
        // refresh to show the updated highscore
        Intent intent = new Intent(getApplicationContext(), highscore.class);
        startActivity(intent);
    }

    public void back(View view){
        finish();
    }
}
