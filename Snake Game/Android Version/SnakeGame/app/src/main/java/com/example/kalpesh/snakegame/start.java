package com.example.kalpesh.snakegame;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;


public class start extends Activity {

    boolean mute = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void startGame(View view){
        Intent intent = new Intent(getApplicationContext(), help.class);
        intent.putExtra("MUTE",mute);
        startActivity(intent);
    }

    public void exitGame(View view){
        ActivityCompat.finishAffinity(start.this);
    }

    public void showHighScore(View view){
        Intent intent = new Intent(getApplicationContext(), highscore.class);
        startActivity(intent);
    }

    public void muteSound(View view){
        Button button = (Button)findViewById(R.id.soundLabel);
        if (mute){
            mute = false;
            button.setText("SOUND OFF");
        }else{
            mute = true;
            button.setText("SOUND ON");
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("are you sure to exit Snake Game?");
        ab.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //if you want to finish all activity
                ActivityCompat.finishAffinity(start.this);
            }
        });
        ab.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        ab.show();
    }
}
