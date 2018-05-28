package com.example.kalpesh.snakegame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Vibrator;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

public class SnakeActivity extends Activity  {

    // Declare an instance of SnakeView
    SnakeView snakeView;
    // We will initialize it in onCreate
    // once we have more details about the Player's device

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //find out the width and height of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        boolean mute = getIntent().getBooleanExtra("MUTE",false);
        AudioManager manager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Create a new View based on the SnakeView class
        snakeView = new SnakeView(this, size, vibrator, mute, manager);

        setContentView(snakeView);

    }

    // Start the thread in snakeView when this Activity
    // is shown to the player
    @Override
    protected void onResume() {
        super.onResume();
        snakeView.resume();
    }

    // Make sure the thread in snakeView is stopped
    // If this Activity is about to be closed
    @Override
    protected void onPause() {

        super.onPause();
        snakeView.pause();
    }

    @Override
    public void onBackPressed() {
        onPause();
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
//        ab.setTitle("PAUSE");
        ab.setMessage("What do you want do?");
        ab.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onResume();
            }
        });
        ab.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(),start.class));
            }
        });
        ab.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onResume();
                snakeView.startGame();
            }
        });
        ab.show();
    }
}
