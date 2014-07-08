package com.hzwydyj.finace.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DriectSQlite extends SQLiteOpenHelper {
	
	//建立数据的名字
	public DriectSQlite(Context context) {
		super(context, DBConst.DB_NAME_DRIECT, null, 1);
	}
	//建表，以及表的所有字段
	@Override
/*	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE driect (direct_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"recordid TEXT UNIQUE ,rid TEXT,lid TEXT,uid TEXT," +
				"replycid TEXT,replyuid TEXT,replynickname TEXT, username TEXT, nickname TEXT,message TEXT," +
				" is_shield TEXT,is_wonderful TEXT, is_top TEXT,is_call TEXT,level TEXT, is_system TEXT," +
				" clapping_count TEXT,dateline TEXT )";
		db.execSQL(sql);
	}*/
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + DBConst.DB_TABLE_DRIECT1 + " (direct_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"recordid TEXT UNIQUE ,rid TEXT,lid TEXT,uid TEXT," +
				"message TEXT," +
				" is_shield TEXT, is_wonderful TEXT,is_call TEXT,level TEXT, is_system TEXT," +
				" dateline TEXT,authority TEXT, avatar_middle TEXT,avatar_small TEXT, showname TEXT,c_showname TEXT )";
		db.execSQL(sql);
		
		String sql1 = "CREATE TABLE " + DBConst.DB_TABLE_DRIECT2 + " (direct_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"recordid TEXT UNIQUE ,rid TEXT,lid TEXT,uid TEXT," +
				"message TEXT," +
				" is_shield TEXT, is_wonderful TEXT,is_call TEXT,level TEXT, is_system TEXT," +
				" dateline TEXT,authority TEXT, avatar_middle TEXT,avatar_small TEXT, showname TEXT,c_showname TEXT )";
		db.execSQL(sql1);
		
		String sql2 = "CREATE TABLE " + DBConst.DB_TABLE_DRIECT3 + " (direct_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"recordid TEXT UNIQUE ,rid TEXT,lid TEXT,uid TEXT," +
				"message TEXT," +
				" is_shield TEXT, is_wonderful TEXT,is_call TEXT,level TEXT, is_system TEXT," +
				" dateline TEXT,authority TEXT, avatar_middle TEXT,avatar_small TEXT, showname TEXT,c_showname TEXT )";
		db.execSQL(sql2);
		
		String sql3 = "CREATE TABLE " + DBConst.DB_TABLE_DRIECT4 + " (direct_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"recordid TEXT UNIQUE ,rid TEXT,lid TEXT,uid TEXT," +
				"message TEXT," +
				" is_shield TEXT, is_wonderful TEXT,is_call TEXT,level TEXT, is_system TEXT," +
				" dateline TEXT,authority TEXT, avatar_middle TEXT,avatar_small TEXT, showname TEXT,c_showname TEXT )";
		db.execSQL(sql3);
	}
	
	@Override    
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
