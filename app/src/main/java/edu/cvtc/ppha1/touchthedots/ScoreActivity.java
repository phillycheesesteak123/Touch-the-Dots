package edu.cvtc.ppha1.touchthedots;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;


public class ScoreActivity extends AppCompatActivity {

    private static final String TAG = "SurvivalModeActivity";

    private TextView survivalScoreTxt;
    private TextView burstScoreTxt;
    private ScoreHelper scoreHelper;
    private String formattedScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        survivalScoreTxt = findViewById(R.id.scoreTimeTxt);
        burstScoreTxt = findViewById(R.id.scoreTxt);

        scoreHelper = new ScoreHelper(this);
        Cursor survivalCursor = scoreHelper.getScore("survival");
        Cursor burstCursor = scoreHelper.getScore("burst");


        // If the cursor is 0, that means that there are no entries within the database
        if(survivalCursor.getCount() == 0 || burstCursor.getCount() == 0) {
            Log.d(TAG, "No entries");
            return;
        }


        while (survivalCursor.moveToNext()) {
            // This is used to get the time score, which is a double, from the database
            // and format it back into the time format seen in the SurvivalModeActivity
            formattedScore = formatScore(Integer.parseInt(survivalCursor.getString(1)));

            survivalScoreTxt.setText(formattedScore);
        }

        while (burstCursor.moveToNext()) {
            burstScoreTxt.setText(burstCursor.getString(2));
        }

        survivalCursor.close();
        burstCursor.close();

    }


    private String formatScore(int score) {

        int seconds = ((score % 86400) % 3600) % 60;
        int minutes = ((score % 86400) % 3600) / 60;
        int hours = (score % 86400) / 3600 ;

        return String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds);
    }

}