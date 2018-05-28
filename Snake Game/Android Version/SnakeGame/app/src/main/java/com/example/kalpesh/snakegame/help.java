package com.example.kalpesh.snakegame;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;

public class help extends Activity {

    private boolean mute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        mute = getIntent().getBooleanExtra("MUTE",false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // user touched screen will directed to game
        Intent intent = new Intent(getApplicationContext(), SnakeActivity.class);
        intent.putExtra("MUTE",mute);
        startActivity(intent);
        return true;
    }
}
