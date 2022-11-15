package edu.cvtc.ppha1.touchthedots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class BurstModeActivity extends AppCompatActivity {

    private TextView timerTxt, scoreTxt, displayScoreTxt;
    private Button dot[] = new Button[5];
    private LinearLayout displayFinalScoreLayout;
    private int scoreCount = 0;

    private boolean validateFirstClick = true, validateOnFinish = false;

    private final int seconds = 30000;
    private long timeLeft = seconds;
    private CountDownTimer countDownTimer;

    private DisplayMetrics displayMetrics;
    private Random random;

    private ScoreHelper scoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burst_mode);

        displayFinalScoreLayout = findViewById(R.id.displayScore);

        // This sets the buttons into an array
        dot[0] = findViewById(R.id.dot1);
        dot[1] = findViewById(R.id.dot2);
        dot[2] = findViewById(R.id.dot3);
        dot[3] = findViewById(R.id.dot4);
        dot[4] = findViewById(R.id.dot5);

        timerTxt = findViewById(R.id.burstScoreTimer);
        scoreTxt = findViewById(R.id.burstScoreText);
        displayScoreTxt = findViewById(R.id.displayScoreText);

        // This gets the width and height of the display
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        timerTxt.setText((timeLeft / 1000) + "s");

        scoreHelper = new ScoreHelper(this);

    }

    public void dotOnClick_burstMode(View view) {
        // Checks to see if this is the first click then sets it to false to prevent timer
        // being called everytime a dot is clicked
        if (validateFirstClick) {
            validateFirstClick = false;
            startTimer();
        }

        // Checks to see if the onFinish method is called, if it isn't then allow user to score
        if (!validateOnFinish) {
            scoreTxt.setText(String.valueOf((scoreCount += 1)));
            generateRandomPosition(view);
        }

    }

    // This starts the countdown timer for burst mode
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long milliseconds) {
                timeLeft = milliseconds;

                updateCountDownText();
            }

            @Override
            public void onFinish() { // When the timer hits 0 then...

                // Disable the dots
                for(int i = 0; i < dot.length; i++) {
                    dot[i].setEnabled(false);
                }

                // Reset the validation to its original intended value
                validateOnFinish = true;

                // Set the current score to final score
                displayScoreTxt.setText(scoreTxt.getText());

                // Insert score into database
                scoreHelper.insertScore(0, scoreCount);

                // Display the final score layout
                displayFinalScoreLayout.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void updateCountDownText() {
        int seconds = (int) (timeLeft / 1000) % 60;

        timerTxt.setText(formatTime(seconds));
    }

    private String formatTime(int seconds) {
        return  String.format(Locale.getDefault(), "%02d", seconds) + "s";
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


    // This method is called whenever the user clicks the "Play Again" button
    public void resetGame(View view) {
        // Setting the validators to their original value
        validateFirstClick = true;
        validateOnFinish = false;

        // Resetting score to 0
        scoreCount = 0;
        scoreTxt.setText(getString(R.string.score_txt));

        // Resetting the time and the final score
        timeLeft = seconds;
        timerTxt.setText((timeLeft / 1000) + "s");
        displayScoreTxt.setText(getText(R.string.score_txt));

        // Setting the final score visibility to invisible and make sure it does not take
        // up space
        displayFinalScoreLayout.setVisibility(View.GONE);

        // Randomly generates the positions of the dots and re-enables them
        for(int i = 0; i < dot.length; i++) {
            generateRandomPosition(dot[i]);
            dot[i].setEnabled(true);
        }
    }

    // When the user clicks "Go Back," it will return them to the main menu
    public void returnToMenu(View view) {
        // If the user back out before the timer is finished, cancel the count down timer.
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }

} // Last Bracket