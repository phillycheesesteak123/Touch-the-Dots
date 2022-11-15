package edu.cvtc.ppha1.touchthedots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up the spinner and adapter to get the array of items.
        spinner = findViewById(R.id.mode_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.modes_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
            spinner.setAdapter(adapter);
        }

    }


    // Whenever the user clicks the score button, this will take them to the score activity
    public void launchScoreActivity(View view) {
        Intent scoreIntent = new Intent(MainActivity.this, ScoreActivity.class);

        startActivity(scoreIntent);
    }

    // Whenever the user clicks the play button, this will take them to whatever mode they selected in the spinner
    public void playOnClick(View view) {
        Intent survivalModeIntent = new Intent(MainActivity.this, SurvivalModeActivity.class);
        Intent burstModeIntent = new Intent(MainActivity.this, BurstModeActivity.class);
        String survivalModeString = getString(R.string.survival_mode_title);

        if (spinner.getSelectedItem().toString().equals(survivalModeString)) {
            startActivity(survivalModeIntent);
        } else {
            startActivity((burstModeIntent));
        }
    }


    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    // A toast will pop up whenever the user selects a mode in the spinner.
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String modeSpinner = adapterView.getItemAtPosition(i).toString();
        displayToast(modeSpinner);
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

} // Last Bracket