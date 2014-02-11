package com.example.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class helps with database setup. None of its methods (except for the constructor)
 * should be called directly.
 */
public class SQLiteDBHelper extends SQLiteOpenHelper {
	
	// Table name
	public static final String TABLE_HIGH_SCORE = "high_score";
	
	// Table columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_INITIAL = "name";
	public static final String COLUMN_SCORE = "score";
	public static final String COLUMN_DIFFICULTY = "difficulty";

	
	// Database name
	private static final String DATABASE_NAME = "snake.db";
	
	// Increment this number to clear everything in database
	private static final int DATABASE_VERSION = 1;

	/**
	 * Returns an instance of this helper object given the activity
	 * @param context
	 */
	public SQLiteDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		 * TODO: Create database table "events"
		 * COLUMN_ID should be of type "integer primary key autoincrement"
		 * All other columns should be of type "text not null"
		 * Columns names have been created as constants at top of this class
		 */
		String sql = "CREATE TABLE " + TABLE_HIGH_SCORE + " ( " +
				COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_INITIAL + " TEXT NOT NULL, " +
				COLUMN_SCORE + " TEXT NOT NULL, " +
				COLUMN_DIFFICULTY + " TEXT NOT NULL );" ;
		db.execSQL(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteDBHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIGH_SCORE);
		onCreate(db);
	}

}