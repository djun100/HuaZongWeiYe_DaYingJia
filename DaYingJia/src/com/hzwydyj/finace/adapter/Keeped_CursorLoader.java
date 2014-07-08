package com.hzwydyj.finace.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.hzwydyj.finace.db.DBConst;
import com.hzwydyj.finace.db.DBManager;

public class Keeped_CursorLoader extends CursorLoader {

	private DBManager dbManager;

	String[] mContactProjection = { DBConst.KEY_NEWSKEEP_NEWS_TITLE_COLUMN, // 0
			DBConst.KEY_NEWSKEEP_NEWS_TIME_COLUMN // 1
	};

	public Keeped_CursorLoader(Context context) {
		super(context);
		dbManager = new DBManager(context);
	}

	/**
	 * 查询数据等操作放在这里执行
	 */
	@Override
	protected Cursor onLoadInBackground() {
		return dbManager.queryAllCursor();
	}
}
