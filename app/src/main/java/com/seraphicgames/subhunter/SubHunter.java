package com.seraphicgames.subhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SyncStats;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.lang.Math;

import android.graphics.Paint;
import android.graphics.Point;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.view.MotionEvent;

public class SubHunter extends AppCompatActivity {
    ArrayList<Point> shotBlocks;

    ImageView gameView;
    Canvas canvas;
    Bitmap myBitMap;
    Paint paint;

    int boomTextColor = Color.BLACK;
    int backgroundColor = Color.WHITE;
    int numberHorizontalPixels;
    int numberVerticalPixels;
    int blockSize;
    int gridWidth = 30;
    int gridHeight;
    float horizontalTouched = -100;
    float verticalTouched = -100;
    int subHorizontalPosition;
    int subVerticalPosition;
    boolean hit = false;
    int shotsTaken;
    double distanceFromSub;
    boolean debugging = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the current device's screen resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        numberHorizontalPixels = size.x;
        numberVerticalPixels = size.y;

        blockSize = numberHorizontalPixels / gridWidth;
        gridHeight = numberVerticalPixels / blockSize;

        shotBlocks = new ArrayList<>();

        Log.d("Debugging", "In onCreate");

        myBitMap = Bitmap.createBitmap(numberHorizontalPixels, numberVerticalPixels, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(myBitMap);
        gameView = new ImageView(this);
        paint = new Paint();


        gameView.setImageBitmap(myBitMap);
        setContentView(gameView);
        newGame();
        draw();
    }

    /*
     This code will execute when a new
     game needs to be started. It will
     happen when the app is first started
     and after the player wins a game.
    */
    void newGame() {
        Random random = new Random();
        shotBlocks.clear();
        hit = false;
        subHorizontalPosition = random.nextInt(gridWidth) + 1;
        subHorizontalPosition *= blockSize;
        subVerticalPosition = random.nextInt(gridHeight) + 1;
        subVerticalPosition *= blockSize;
        shotsTaken = 0;

        Log.d("Debugging", "In newGame");
    }
    /*
     Here we will do all the drawing.
     The grid lines, the HUD and
     the touch indicator
     */

    void draw() {
        gameView.setImageBitmap(myBitMap);

        canvas.drawColor(Color.WHITE);

        paint.setColor(Color.BLACK);

        for (int i = 1; i < gridWidth; i++) {
            Point start = new Point(blockSize * i, 0);
            Point stop = new Point(blockSize * i, numberVerticalPixels);

            canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
        }

        for (int i = 1; i < gridHeight; i++) {
            Point start = new Point(0, blockSize * i);
            Point stop = new Point(numberHorizontalPixels, blockSize * i);

            canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
        }

        drawShotBlocks();

        paint.setTextSize(blockSize);
        paint.setColor(Color.BLUE);
        canvas.drawText(
                "Shots Taken: " + shotsTaken +
                        " Distance: " + distanceFromSub,
                blockSize, blockSize * 1.75f,
                paint);

        Log.d("Debugging", "In draw");
    }

    void drawShotBlocks() {
        for (Point blockPoint : shotBlocks) {
            Rect rect = new Rect();

            rect.left = blockSize * blockPoint.x;
            rect.top = blockSize * blockPoint.y;

            rect.right = blockSize * (blockPoint.x + 1);
            rect.bottom = blockSize * (blockPoint.y + 1);
            Log.d("Rect", rect.toString());
            paint.setColor(Color.RED);
            canvas.drawRect(rect, paint);
        }
    }

     /*
     This part of the code will
     handle detecting that the player
     has tapped the screen
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("Debugging", "In onTouchEvent");

        if((event.getAction() & MotionEvent.ACTION_MASK) != MotionEvent.ACTION_DOWN)
            return true;

        if (hit){
            newGame();
            return true;
        }

        float x = event.getX();
        float y = event.getY();

        int horizontalIndex = (int) Math.floor(x / (float) blockSize);
        int verticalIndex = (int) Math.floor(y / (float) blockSize);

        Point shotBlock = new Point(horizontalIndex, verticalIndex);

        if (shotBlocks.contains(shotBlock)) {
            return true;
        }

        shotBlocks.add(shotBlock);

        Point subPoint = new Point(subHorizontalPosition, subVerticalPosition);
        Point touchPoint = new Point((int) x, (int) y);
        distanceFromSub = pointDistance(subPoint, touchPoint);

        if (distanceFromSub < blockSize) {
            hit = true;
            boom();
            return true;
        }
        takeShot();
        return true;
    }

    private double pointDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    /*
     The code here will execute when
     the player taps the screen. It will
     calculate the distance from the sub'
     and decide a hit or miss
     */
    void takeShot() {
        shotsTaken++;
        Log.d("Debugging", "In takeShot");
        draw();
    }

    // This code says "BOOM!"
    // This code prints the debugging text

    void boom() {
        gameView.setImageBitmap(myBitMap);
        canvas.drawColor(backgroundColor);
        paint.setTextSize(blockSize * 10);
        paint.setColor(boomTextColor);
        Point canvasPosition = new Point();
        canvasPosition.x = (gridWidth - 30) / 2 * blockSize;
        canvasPosition.y = (gridHeight - 5) * blockSize;
        canvas.drawText("BOOM", canvasPosition.x, canvasPosition.y, paint);

        Log.d("Debugging", "In boom");
    }

    void printDebuggingText() {
        Log.d("numberHorizontalPixels",
                "" + numberHorizontalPixels);
        Log.d("numberVerticalPixels",
                "" + numberVerticalPixels);
        Log.d("blockSize", "" + blockSize);
        Log.d("gridWidth", "" + gridWidth);
        Log.d("gridHeight", "" + gridHeight);
        Log.d("horizontalTouched",
                "" + horizontalTouched);
        Log.d("verticalTouched",
                "" + verticalTouched);
        Log.d("subHorizontalPosition",
                "" + subHorizontalPosition);
        Log.d("subVerticalPosition",
                "" + subVerticalPosition);
        Log.d("hit", "" + hit);
        Log.d("shotsTaken", "" + shotsTaken);
        Log.d("debugging", "" + debugging);
        Log.d("distanceFromSub",
                "" + distanceFromSub);
    }
}