package com.example.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.applegobbler.DBEntry;
import com.example.applegobbler.HighScoreActivity;

public class HighScoreHelper {
	
	
	private static SQLiteDatabase database;
	private SQLiteDBHelper sqHelper;
	
	private String[] allColumns = { SQLiteDBHelper.COLUMN_ID,
			SQLiteDBHelper.COLUMN_INITIAL, SQLiteDBHelper.COLUMN_SCORE, SQLiteDBHelper.COLUMN_DIFFICULTY };

	
	public HighScoreHelper(Context context)
	{
		initDB(context);
	}
	
	private void initDB(Context context)
	{
		sqHelper = new SQLiteDBHelper(context);
		database = sqHelper.getReadableDatabase();
	}
	public void addEntry(String initial, int score, String diff)
	{
		ContentValues cv = new ContentValues();
		cv.put(SQLiteDBHelper.COLUMN_INITIAL, initial);
		cv.put(SQLiteDBHelper.COLUMN_SCORE, score);
		cv.put(SQLiteDBHelper.COLUMN_DIFFICULTY, diff);
		
		database.insert(SQLiteDBHelper.TABLE_HIGH_SCORE, null, cv);
	}

	public ArrayList<DBEntry> getAllEvents() {
		ArrayList<DBEntry> allEntries = new ArrayList<DBEntry>();

		Cursor cursor = database.query(SQLiteDBHelper.TABLE_HIGH_SCORE,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			DBEntry event = new DBEntry(cursor.getString(cursor
					.getColumnIndex(SQLiteDBHelper.COLUMN_INITIAL)),
					cursor.getString(cursor
							.getColumnIndex(SQLiteDBHelper.COLUMN_SCORE)),
					cursor.getString(cursor
							.getColumnIndex(SQLiteDBHelper.COLUMN_DIFFICULTY)));
			allEntries.add(event);
			cursor.moveToNext();
		}

		cursor.close();
		return allEntries;
	}

}
