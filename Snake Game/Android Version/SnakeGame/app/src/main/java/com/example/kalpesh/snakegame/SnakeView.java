package com.example.kalpesh.snakegame;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Random;

public class SnakeView extends SurfaceView implements Runnable  {

    // All the code will run separately to the UI
    private Thread m_Thread = null;

    // This variable determines when the game is playing
    // It is declared as volatile because
    // it can be accessed from inside and outside the thread
    private volatile boolean m_Playing;

    // This is what we draw on
    private Canvas m_Canvas;
    // This is required by the Canvas class to do the drawing
    private SurfaceHolder m_Holder;
    // This lets us control colors etc
    private Paint m_Paint;

    // This will be a reference to the Activity
    private Context m_context;

    // Sound
    private SoundPool m_SoundPool;
    private int m_get_mouse_sound = -1;
    private int m_dead_sound = -1;

    // vibrate
    Vibrator m_vibrator;

    // For tracking movement m_Direction
    public enum Direction {UP, RIGHT, DOWN, LEFT}
    // Start by heading to the right
    private Direction m_Direction = Direction.RIGHT;

    // What is the screen resolution
    private int m_ScreenWidth;
    private int m_ScreenHeight;

    // Control pausing between updates
    private long m_NextFrameTime;
    // Update the game 10 times per second
    private long FPS = 7;
    // There are 1000 milliseconds in a second
    private final long MILLIS_IN_A_SECOND = 1000;
    // We will draw the frame much more often

    // The current m_Score
    private int m_Score;

    // The location in the grid of all the segments
    private int[] m_SnakeXs;
    private int[] m_SnakeYs;

    // How long is the snake at the moment
    private int m_SnakeLength;

    // Where is the mouse
    private int m_MouseX;
    private int m_MouseY;

    // The size in pixels of a snake segment
    private int m_BlockSize;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int m_NumBlocksHigh; // determined dynamically


    public SnakeView(Context context, Point size, Vibrator vibrator, boolean mute, AudioManager manager) {
        super(context);

        m_context = context;

        m_ScreenWidth = size.x;
        m_ScreenHeight = size.y;

        //Determine the size of each block/place on the game board
        m_BlockSize = m_ScreenWidth / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        m_NumBlocksHigh = m_ScreenHeight / m_BlockSize;

        // Set the sound up
        loadSound();

        // Initialize the drawing objects
        m_Holder = getHolder();
        m_Paint = new Paint();

        // If you score 200 you are rewarded with a crash achievement!
        m_SnakeXs = new int[200];
        m_SnakeYs = new int[200];

        // Start the game
        startGame();

        m_vibrator = vibrator;
        // for mute option
        if(mute){
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }else{
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }

    @Override
    public void run() {
        // The check for m_Playing prevents a crash at the start
        // You could also extend the code to provide a pause feature
        while (m_Playing) {

            // Update 10 times a second
            if(checkForUpdate()) {
                updateGame();
                drawGame();
            }

        }
    }

    public void pause() {
        m_Playing = false;
        try {
            m_Thread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        m_Playing = true;
        m_Thread = new Thread(this);
        m_Thread.start();
    }

    public void startGame() {
        // Start with just a head, in the middle of the screen
        m_SnakeLength = 1;
        m_SnakeXs[0] = NUM_BLOCKS_WIDE / 2;
        m_SnakeYs[0] = m_NumBlocksHigh / 2;

        // And a mouse to eat
        spawnMouse();

        // Reset the m_Score
        m_Score = 0;

        // Setup m_NextFrameTime so an update is triggered immediately
        m_NextFrameTime = System.currentTimeMillis();
    }

    public void loadSound() {
        m_SoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            // Create objects of the 2 required classes
            // Use m_Context because this is a reference to the Activity
            AssetManager assetManager = m_context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the two sounds in memory
            descriptor = assetManager.openFd("get_mouse_sound.ogg");
            m_get_mouse_sound = m_SoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("death_sound.ogg");
            m_dead_sound = m_SoundPool.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }
    }

    public void spawnMouse() {
        Random random = new Random();
        m_MouseX = random.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        m_MouseY = random.nextInt(m_NumBlocksHigh - 1) + 1;
    }

    private void eatMouse(){
        //  Got one! Squeak!!
        // Increase the size of the snake
        m_SnakeLength++;
        //replace the mouse
        spawnMouse();
        //add to the m_Score
        m_Score = m_Score + 1;
        FPS = FPS + (m_Score/10);
        m_SoundPool.play(m_get_mouse_sound, 1, 1, 0, 0, 1);
        m_vibrator.vibrate(50);
    }

    private void moveSnake(){
        // Move the body
        for (int i = m_SnakeLength; i > 0; i--) {
            // Start at the back and move it
            // to the position of the segment in front of it
            m_SnakeXs[i] = m_SnakeXs[i - 1];
            m_SnakeYs[i] = m_SnakeYs[i - 1];

            // Exclude the head because
            // the head has nothing in front of it
        }

        // Move the head in the appropriate m_Direction
        switch (m_Direction) {
            case UP:
                m_SnakeYs[0]--;
                break;

            case RIGHT:
                m_SnakeXs[0]++;
                break;

            case DOWN:
                m_SnakeYs[0]++;
                break;

            case LEFT:
                m_SnakeXs[0]--;
                break;
        }
    }

    private boolean detectDeath(){
        // Has the snake died?
        boolean dead = false;

        // Hit a wall?
        if (m_SnakeXs[0] == -1) dead = true;
        if (m_SnakeXs[0] >= NUM_BLOCKS_WIDE) dead = true;
        if (m_SnakeYs[0] == -1) dead = true;
        if (m_SnakeYs[0] == m_NumBlocksHigh) dead = true;

        // Eaten itself?
        for (int i = m_SnakeLength - 1; i > 0; i--) {
            if ((i > 4) && (m_SnakeXs[0] == m_SnakeXs[i]) && (m_SnakeYs[0] == m_SnakeYs[i])) {
                dead = true;
            }
        }
        return dead;
    }

    public void updateGame() {
        // Did the head of the snake touch the mouse?
        if (m_SnakeXs[0] == m_MouseX && m_SnakeYs[0] == m_MouseY) {
            eatMouse();
        }

        moveSnake();

        if (detectDeath()) {

            m_vibrator.vibrate(50);
            m_SoundPool.play(m_dead_sound, 1, 1, 0, 0, 1);

            // show scores
            Intent intent = new Intent(getContext(), result.class);
            intent.putExtra("SCORE",m_Score);
            m_context.startActivity(intent);
        }
    }

    public void drawGame() {
        // Prepare to draw
        if (m_Holder.getSurface().isValid()) {
            m_Canvas = m_Holder.lockCanvas();

            // HEX code : #6d9e20 RGB - 109,158,32
            m_Canvas.drawColor(Color.argb(255, 109, 158, 32));

            // Set the color of the paint to draw the snake and mouse with
            m_Paint.setColor(Color.argb(255, 255, 255, 255));

            // Choose how big the score will be
            m_Paint.setTextSize(100);
            Paint m_head_paint = new Paint();
            m_head_paint.setTextSize(100);
            m_head_paint.setColor(Color.argb(255,0,0,0));
            m_Canvas.drawText("Score:" + m_Score, 10, 100, m_head_paint);

            //Draw the snake head
            m_head_paint.setColor(Color.argb(255,255,0,0));
            m_Canvas.drawRect(m_SnakeXs[0] * m_BlockSize,
                    (m_SnakeYs[0] * m_BlockSize),
                    (m_SnakeXs[0] * m_BlockSize) + m_BlockSize,
                    (m_SnakeYs[0] * m_BlockSize) + m_BlockSize,
                    m_head_paint);
            //Draw Rest of the body
            for (int i = 1; i < m_SnakeLength; i++) {
                m_Canvas.drawRect(m_SnakeXs[i] * m_BlockSize,
                        (m_SnakeYs[i] * m_BlockSize),
                        (m_SnakeXs[i] * m_BlockSize) + m_BlockSize,
                        (m_SnakeYs[i] * m_BlockSize) + m_BlockSize,
                        m_Paint);
            }

            //draw the mouse
            m_Canvas.drawRect(m_MouseX * m_BlockSize,
                    (m_MouseY * m_BlockSize),
                    (m_MouseX * m_BlockSize) + m_BlockSize,
                    (m_MouseY * m_BlockSize) + m_BlockSize,
                    m_Paint);

            // Draw the whole frame
            m_Holder.unlockCanvasAndPost(m_Canvas);
        }
    }

    public boolean checkForUpdate() {

        // Are we due to update the frame
        if(m_NextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            m_NextFrameTime =System.currentTimeMillis() + MILLIS_IN_A_SECOND / FPS;

            // Return true so that the update and draw
            // functions are executed
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {



        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            //This notifies us the player’s finger has left the screen
            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_UP:
                float current_x = motionEvent.getX();
                float current_y = motionEvent.getY();
                switch(m_Direction){
                    case UP:
                        if(current_x > m_SnakeXs[0] * m_BlockSize){
                            m_Direction = Direction.RIGHT;
                        }else{
                            m_Direction = Direction.LEFT;
                        }
                        break;
                    case DOWN:
                        if (current_x > m_SnakeXs[0] * m_BlockSize){
                            m_Direction = Direction.RIGHT;
                        }else{
                            m_Direction = Direction.LEFT;
                        }
                        break;
                    case LEFT:
                        if(current_y > m_SnakeYs[0] * m_BlockSize){
                            m_Direction = Direction.DOWN;
                        }else {
                            m_Direction = Direction.UP;
                        }
                        break;
                    case RIGHT:
                        if (current_y > m_SnakeYs[0] * m_BlockSize){
                            m_Direction = Direction.DOWN;
                        }else {
                            m_Direction = Direction.UP;
                        }
                        break;
                }
        }
        return true;
    }
}
