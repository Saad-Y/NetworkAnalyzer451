package com.example.networkanalyzer451;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "AnalyticsFragment";
    private static final String DATABASE_NAME = "network_data.db";
    private static final int DATABASE_VERSION = 1;
    // Table name and column names
    private static final String TABLE_NAME = "cell_data";
    private static final String COLUMN_OPERATOR = "operator";
    private static final String COLUMN_SIGNAL_POWER = "signal_power";
    private static final String COLUMN_NETWORK_TYPE = "network_type";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_OPERATOR + " TEXT, " +
                COLUMN_SIGNAL_POWER + " INTEGER, " +
                COLUMN_NETWORK_TYPE + " TEXT)";
        db.execSQL(createTableQuery);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade policy
    }
    // Method to fetch data from the database based on selected date range
    public List<CellData> fetchDataFromDatabase(String startDate, String endDate) {
        List<CellData> cellDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                COLUMN_OPERATOR,
                COLUMN_SIGNAL_POWER,
                COLUMN_NETWORK_TYPE
        };
        String selection = "date BETWEEN ? AND ?";
        String[] selectionArgs = {startDate, endDate};
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String operator = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPERATOR));
                    int signalPower = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SIGNAL_POWER));
                    String networkType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NETWORK_TYPE));
                    cellDataList.add(new CellData(operator, signalPower, networkType));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching data from database: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return cellDataList;
    }
}