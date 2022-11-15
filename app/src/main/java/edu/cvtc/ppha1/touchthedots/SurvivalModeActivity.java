package edu.cvtc.ppha1.touchthedots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SurvivalModeActivity extends AppCompatActivity {

    private LinearLayout displayFinalScoreLayout;
    private TextView finalTimeScore, timerTxt;
    private Button dot[] = new Button[5], square[] = new Button[2];

    private DisplayMetrics displayMetrics;
    private Random random;

    private boolean validateFirstClick = true;

    private TimerTask timerTask;
    private Timer timer;
    private double time = 0.0;

    Handler handler;
    Runnable r;

    ScoreHelper scoreHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survival_mode);

        displayFinalScoreLayout = findViewById(R.id.displayScore);

        timerTxt = findViewById(R.id.survivalTimerTxt);
        finalTimeScore = findViewById(R.id.displayTimeScoreText);

        // Setting the buttons dot and square into an array
        dot[0] = findViewById(R.id.dot1);
        dot[1] = findViewById(R.id.dot2);
        dot[2] = findViewById(R.id.dot3);
        dot[3] = findViewById(R.id.dot4);
        dot[4] = findViewById(R.id.dot5);

        square[0] = findViewById(R.id.square1);
        square[1] = findViewById(R.id.square2);

        // This is used to get the screen size of the current display
        displayMetrics  = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        timer = new Timer();

        // This handler and the runnable "r" is used to check if the user is inactive for more
        // than 2 seconds
        handler = new Handler();

        r = new Runnable() {
            @Override
            public void run() {
                gameOver();
            }
        };


        // Initialize Database
        scoreHelper = new ScoreHelper(this);

    }

    // Checks to see if the user is interacting with the screen
    @Override
    public void onUserInteraction() {
        stopHandler();
        startHandler();
        super.onUserInteraction();
    }

    // The stopHandler() method is meant to remove any
    // runnable that have not begun processing from the que.
    public void stopHandler() {
        handler.removeCallbacks(r);
    }

    // The startHandler() method is meant to stop the user if they are inactive
    // for more than 2 seconds.
    public void startHandler() {
        handler.postDelayed(r, 2000);
    }

    // Whenever the user clicks on the dot, it will start the timer and
    // generate the dots into random locations.
    public void dotOnClick_survivalMode(View view) {

        // Check if this is the user's first click, if it is, then start the timer
        // and handler
        if (validateFirstClick) {
            validateFirstClick = false;
            startTimer();
            startHandler();
        }

        // Generate clicked dot into random location
        generateRandomPosition(view);

        // Generate squares into random locations
        for(int i = 0; i < square.length; i++) {
            generateRandomPosition(square[i]);
        }
    }

    // If the user clicks on a square, it's game over.
    public void squareOnClick_survivalMode(View view) {
        gameOver();
    }

    // This is the in-game timer
    private void startTimer() {

        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;

                        int roundNumber = (int) Math.round(time);

                        int seconds = ((roundNumber % 86400) % 3600) % 60;
                        int minutes = ((roundNumber % 86400) % 3600) / 60;
                        int hours = (roundNumber % 86400) / 3600 ;

                        timerTxt.setText(formatTime(seconds, minutes, hours));
                    }
                });
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }


    private String formatTime(int seconds, int minutes, int hours) {
        return String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Generates random position for dots
    // This is still work in-progress
    private  void generateRandomPosition(View view) {
        random = new Random();
        float radius = (float) view.getWidth() / 2;

        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        int displayArea = displayWidth * displayHeight;

        float buffer = radius + 25;

        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        // Generates random number for X and Y coordinates of a dot
        float x = viewWidth + random.nextInt(displayWidth - 2 * viewWidth);
        float y = viewHeight + radius + random.nextInt(displayHeight - 3 * viewHeight);

        // Get the current x and y coordinate of the current dot.
        final float currentX = view.getX();
        final float currentY = view.getY();

        // This is meant to store the x and y coordinates of all dots
        float coordinatesArray[] = new float[5];

        for (int i = 0; i < coordinatesArray.length; i++) {
            coordinatesArray[i] = (dot[i].getX() * dot[i].getY()) + dot[i].getWidth();
        }

        // This selects all dots within the dot array.
        for (int i = 0; i < dot.length; i++) {

            // I want to check if the newly generated coordinates and their radius plus the buffer, if it is greater than the
            // area of the display, then that means that the dot will not show up in the screen.
            if ((x * y) + radius + buffer < displayArea) {

                // If the coordinates are less than the area, then that means they reside in the display
                // If the the newly generated dots and its radius plus the buffer is less than the
                // coordinate of any dot, then they are overlapping
                if (coordinatesArray[i]  < (x * y) + radius + buffer) {

                    // If the clicked dot is equal to the coordinates, that means they are the same dot, and it needs to be
                    // generated somewhere in the display.
                    if ((currentX * currentY) + view.getWidth() == coordinatesArray[i]) {
                        generateRandomPosition(view);
                    }

                } else {
                    // The new coordinates are greater than a particular dot in the coordinates array.
                    // This should mean that they're not overlapping

                    view.animate().x(x).y(y).setDuration(0).start();
                }

            } else { // The newly generated coordinates are out of the dimensions of the screen.
                generateRandomPosition(view);
            }

        }

    }

    // This is used to generate random positions for the dots and squares
//    private void generateRandomPosition(View view) {
//        random = new Random();
//        int radius = view.getWidth() / 2;
//
//        int displayWidth = displayMetrics.widthPixels;
//        int displayHeight = displayMetrics.heightPixels;
//        int displayArea = displayWidth * displayHeight;
//
//        int viewWidth = view.getWidth();
//        int viewHeight = view.getHeight();
//        int viewArea = viewHeight * viewWidth;
//
//        // Generates random number for X and Y coordinates of a dot
//        final float x = random.nextInt(displayWidth - 2 * viewWidth);
//        final float y = viewHeight + radius + random.nextInt(displayHeight - 3 * viewHeight);
//
//        view.animate().x(x).y(y).setDuration(0).start();
//    }

    private void gameOver() {

        // Disable the dots and squares
        for(int i = 0; i < square.length; i++) {
            square[i].setEnabled(false);
        }

        for(int i = 0; i < dot.length; i++) {
            dot[i].setEnabled(false);
        }

        // This displays the final score layout and the final score time
        displayFinalScoreLayout.setVisibility(View.VISIBLE);
        finalTimeScore.setText(timerTxt.getText());

        // Stop the timer
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        //Inserts the time score to the database
        scoreHelper.insertScore(time, 0);

    }

    // This method is called whenever the user clicks the "Play Again" button
    public void resetGame(View view) {
        // Reset the time and timer then starts them again
        time = 0.0;
        timer = new Timer();
        startTimer();

        // Reset TextEditor values to 0
        timerTxt.setText(R.string.timer_txt);
        finalTimeScore.setText(timerTxt.getText());

        // Setting the final score visibility to invisible and make sure it does not take
        // up space
        displayFinalScoreLayout.setVisibility(View.GONE);

        // Randomly generate the location of the dots, squares and re-enables them whenever
        // the user clicks "Play Again"
        for(int i = 0; i < dot.length; i++) {
            generateRandomPosition(dot[i]);
            dot[i].setEnabled(true);
        }

        for(int i = 0; i < square.length; i++) {
            generateRandomPosition(square[i]);
            square[i].setEnabled(true);
        }

    }

    // When the user clicks "Go Back," it will return them to the main menu
    public void returnToMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }

} // Last Bracket