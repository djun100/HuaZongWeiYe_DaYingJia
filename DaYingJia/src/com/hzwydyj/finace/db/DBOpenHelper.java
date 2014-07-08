package com.hzwydyj.finace.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBOpenHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;

	private static final String DB_CREATE = "create table " + DBConst.DB_TABLE + " (" + DBConst.KEY_ID
			+ " integer primary key AUTOINCREMENT, " + DBConst.KEY_NEWSKEEP_NEWS_ID_COLUMN + " text not null UNIQUE, "
			+ DBConst.KEY_NEWSKEEP_NEWSAUTHOR_COLUMN + " text not null, " + DBConst.KEY_NEWSKEEP_NEWS_CONTENT_COLUMN
			+ " text not null, " + DBConst.KEY_NEWSKEEP_NEWS_TIME_COLUMN + " text not null, "
			+ DBConst.KEY_NEWSKEEP_NEWS_TITLE_COLUMN + " text not null, " + DBConst.KEY_NEWSKEEP_NEWSTYPE_COLUMN
			+ " text not null );";

	public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, DBConst.DB_NAME, null, DB_VERSION);
	}

	public DBOpenHelper(Context context) {
		super(context, DBConst.DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF IT EXISTS " + DBConst.DB_TABLE);
		onCreate(db);
	}

}
