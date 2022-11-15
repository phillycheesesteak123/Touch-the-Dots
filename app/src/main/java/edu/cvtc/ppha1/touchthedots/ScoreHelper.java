package edu.cvtc.ppha1.touchthedots;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

public class ScoreHelper extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_NAME = "scores_ppha1.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "scores";

    public static final String COLUMN_SCORE_SURVIVAL = "score_survival";
    public static final String COLUMN_SCORE_BURST = "score_burst";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COLUMN_SCORE_SURVIVAL + " DOUBLE, " + COLUMN_SCORE_BURST + " INTEGER)";

    public ScoreHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        Log.d("SurvivalModeActivity","The table was created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE if exists " + TABLE_NAME);
    }

    public void insertScore(double survivalScore, int burstScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE_SURVIVAL, survivalScore);
        values.put(COLUMN_SCORE_BURST, burstScore);

        db.insert(TABLE_NAME, null, values);
    }

    // This method gets the specified score type via the string parameter that will be passed in.
    public Cursor getScore(String scoreType) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        String[] scoreColumns = {
                _ID,
                COLUMN_SCORE_SURVIVAL,
                COLUMN_SCORE_BURST
        };

        String orderBySurvivalScore = COLUMN_SCORE_SURVIVAL + " DESC";
        String orderByBurstScore = COLUMN_SCORE_BURST + " DESC";

        // This is used to get both the survival or burst score's highest score.
        if (scoreType == "survival") {
            cursor = db.query(TABLE_NAME, scoreColumns, null, null, null, null, orderBySurvivalScore, "1");
        } else if (scoreType == "burst") {
            cursor = db.query(TABLE_NAME, scoreColumns, null, null, null, null, orderByBurstScore, "1");
        }

        return cursor;
    }

}
