package org.surrel.geoposts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDbHelper extends SQLiteOpenHelper {
	// Requests
	private static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE notes (" +
					"ID INTEGER PRIMARY KEY, " +
					"lat REAL, " +
					"lon REAL, " +
					"title TEXT, " +
					"text TEXT, " +
					"user TEXT, " +
					"karma INTEGER, " +
					"creation NUMERIC, " +
					"lifetime INTEGER, " +
					"lang TEXT, " +
					"cat INTEGER); " +
					"CREATE INDEX toUser ON notes ( user );";
	private static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS notes";

	// If you change the database schema, you must increment the database version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Notes.db";

	public NotesDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade policy is
		// to simply to discard the data and start over
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}


}
