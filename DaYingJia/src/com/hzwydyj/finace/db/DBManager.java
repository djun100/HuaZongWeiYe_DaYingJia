package com.hzwydyj.finace.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.analytics.tracking.android.Log;
import com.hzwydyj.finace.data.NewsKeep;
import com.hzwydyj.finace.utils.MyLogger;

public class DBManager {
	private DBOpenHelper helper;
	private SQLiteDatabase db;
	private MyLogger log = MyLogger.yLog();

	public DBManager(Context context) {
		helper = new DBOpenHelper(context);
		openDB();
		// log.i("DBManager");
	}

	public void add(List<NewsKeep> newkKeeps) {
		// 开始事务
		db.beginTransaction();

		try {

			ContentValues cv = new ContentValues();
			cv.put(DBConst.KEY_NEWSKEEP_NEWSAUTHOR_COLUMN, "author1");
			cv.put(DBConst.KEY_NEWSKEEP_NEWS_CONTENT_COLUMN, "newscontent1");
			cv.put(DBConst.KEY_NEWSKEEP_NEWS_TIME_COLUMN, "newstime1");
			cv.put(DBConst.KEY_NEWSKEEP_NEWS_TITLE_COLUMN, "newstitle1");
			cv.put(DBConst.KEY_NEWSKEEP_NEWSTYPE_COLUMN, "type1");

			db.insert("newskeep", null, cv);

			db.setTransactionSuccessful();
		} catch (Exception e) {
			// log.i("数据存储异常");
		} finally {
			db.endTransaction();
		}
	}

	public void add(NewsKeep newsKeep) {
		// 开始事务
		db.beginTransaction();

		try {   

			ContentValues cv = new ContentValues();
			cv.put(DBConst.KEY_NEWSKEEP_NEWSAUTHOR_COLUMN, newsKeep.getAuthor());
			cv.put(DBConst.KEY_NEWSKEEP_NEWS_CONTENT_COLUMN, newsKeep.getNewscontent());
			cv.put(DBConst.KEY_NEWSKEEP_NEWS_TIME_COLUMN, newsKeep.getNewstime());
			cv.put(DBConst.KEY_NEWSKEEP_NEWS_TITLE_COLUMN, newsKeep.getNewstitle());
			cv.put(DBConst.KEY_NEWSKEEP_NEWSTYPE_COLUMN, newsKeep.getType());
			cv.put(DBConst.KEY_NEWSKEEP_NEWSTYPE_COLUMN, newsKeep.getType());
			cv.put(DBConst.KEY_NEWSKEEP_NEWS_ID_COLUMN, newsKeep.getNews_id());
			db.insert(DBConst.DB_TABLE, null, cv);

			db.setTransactionSuccessful();// 设置事务成功完成
		} catch (Exception e) {
			// log.i("数据存储异常");
		} finally {
			db.endTransaction();
		}

	}

	public boolean query(String newsid) {
		ArrayList<NewsKeep> persons = new ArrayList<NewsKeep>();
		Cursor c = queryCursorBynewsID(newsid);
		while (c.moveToNext()) {

			NewsKeep keepitem = new NewsKeep();
			keepitem._id = c.getInt(c.getColumnIndex("_id"));
			keepitem.newstime = c.getString(c.getColumnIndex("newstime"));
			keepitem.newstitle = c.getString(c.getColumnIndex("newstitle"));
			keepitem.newscontent = c.getString(c.getColumnIndex("newscontent"));
			keepitem.author = c.getString(c.getColumnIndex("author"));
			keepitem.type = c.getString(c.getColumnIndex("type"));
			persons.add(keepitem);

			return true;
		}
		c.close();
		return false;
	}

	public void deleteItem(String newsid) {
		// DELETE FROM Teachers WHERE Age>30
		// log.i("deleteItem-" + newsid);
		db.execSQL("delete from newskeep where newsid = " + newsid);
		// Cursor c = db.rawQuery("delete from newskeep where newsid = " + newsid, null);
	}

	public Cursor queryAllCursor() {
		// log.i("queryAllCursor");
		Cursor c = db.rawQuery("select * from newskeep order by newsid desc", null);
		if (c == null) {
			// log.i("queryAllCursor return null");
		}
		return c;
	}

	public Cursor queryCursorBynewsID(String newsID) {
		// log.i("queryCursorBynewsID");
		Cursor c = db.rawQuery("select * from newskeep where newsid=" + newsID, null);
		if (c == null) {
			// log.i("queryAllCursor return null");
		}
		return c;
	}

	public void closeDB() {
		try {
			db.close();
		} catch (Exception e) {
			// log.i("db close exception");
		}

	}

	public void openDB() {
		try {
			db = helper.getWritableDatabase();
		} catch (Exception e) {
			// log.i("db close exception");
		}

	}

}
