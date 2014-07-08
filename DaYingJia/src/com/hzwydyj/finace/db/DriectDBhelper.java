package com.hzwydyj.finace.db;

import java.util.ArrayList;
import java.util.List;

import com.hzwydyj.finace.data.ShiPanSeedingBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DriectDBhelper {

	Context context = null;
	private DriectSQlite dbHelper;

	public DriectDBhelper(Context context) {
		super();
		this.context = context;
		dbHelper = new DriectSQlite(context);

	}

	/**
	 * 插入全部数据
	 */
	public void insert(ShiPanSeedingBean pr, String tabName) {
		if (pr == null) {
			return;
		}
		SQLiteDatabase db = null;
		try {
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("recordid", pr.getRecordid());
			values.put("rid", pr.getRid());
			values.put("lid", pr.getLid());
			values.put("uid", pr.getUid());
			values.put("message", pr.getMessage());
			values.put("is_shield", pr.getIs_shield());
			values.put("is_wonderful", pr.getIs_wonderful());
			values.put("is_call", pr.getIs_call());
			values.put("level", pr.getLevel());
			values.put("is_system", pr.getIs_system());
			values.put("dateline", pr.getDateline());
			values.put("authority", pr.getAuthority());
			values.put("avatar_middle", pr.getAvatar_middle());
			values.put("avatar_small", pr.getAvatar_small());
			values.put("showname", pr.getShowname());
			values.put("c_showname", pr.getC_showname());
			Log.i("tabName is add all number succeed:", tabName);
			db.insert(tabName, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

	}

	/**
	 * 查询最新的10条数据
	 * 
	 * @return
	 */
	public List<ShiPanSeedingBean> queryBestNewTenData(String tabName) {
		List<ShiPanSeedingBean> list = new ArrayList<ShiPanSeedingBean>();
		SQLiteDatabase db = null;
		try {
			db = dbHelper.getWritableDatabase();
			Log.i("tabName is query new ten succeed:", tabName);
			String sql = "select  * from (select * from " + tabName + " order by direct_id desc limit 10 offset 0 ) ct order by ct.direct_id asc";
			Cursor cursor = db.rawQuery(sql, null);
			ShiPanSeedingBean ps = null;
			while (cursor.moveToNext()) {
				ps = new ShiPanSeedingBean();
				ps.setRecordid(cursor.getString(1));
				ps.setRid(cursor.getString(2));
				ps.setLid(cursor.getString(3));
				ps.setUid(cursor.getString(4));
				ps.setMessage(cursor.getString(5));
				ps.setIs_shield(cursor.getString(6));
				ps.setIs_wonderful(cursor.getString(7));
				ps.setIs_call(cursor.getString(8));
				ps.setLevel(cursor.getString(9));
				ps.setIs_system(cursor.getString(10));
				ps.setDateline(cursor.getString(11));
				ps.setAuthority(cursor.getString(12));
				ps.setAvatar_middle(cursor.getString(13));
				ps.setAvatar_small(cursor.getString(14));
				ps.setShowname(cursor.getString(15));
				ps.setC_showname(cursor.getString(16));
				list.add(ps);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return list;
	}
	
	public List<ShiPanSeedingBean> queryArticleOneTenData(String tabName) {
		List<ShiPanSeedingBean> list = new ArrayList<ShiPanSeedingBean>();
		SQLiteDatabase db = null;
		try {
			db = dbHelper.getWritableDatabase();
			Log.i("tabName is query new ten succeed:", tabName);
			String sql = "select  * from (select * from " + tabName + " order by direct_id desc limit 1 offset 0 ) ct order by ct.direct_id asc";
			Cursor cursor = db.rawQuery(sql, null);
			ShiPanSeedingBean ps = null;
			while (cursor.moveToNext()) {
				ps = new ShiPanSeedingBean();
				ps.setRecordid(cursor.getString(1));
				ps.setRid(cursor.getString(2));
				ps.setLid(cursor.getString(3));
				ps.setUid(cursor.getString(4));
				ps.setMessage(cursor.getString(5));
				ps.setIs_shield(cursor.getString(6));
				ps.setIs_wonderful(cursor.getString(7));
				ps.setIs_call(cursor.getString(8));
				ps.setLevel(cursor.getString(9));
				ps.setIs_system(cursor.getString(10));
				ps.setDateline(cursor.getString(11));
				ps.setAuthority(cursor.getString(12));
				ps.setAvatar_middle(cursor.getString(13));
				ps.setAvatar_small(cursor.getString(14));
				ps.setShowname(cursor.getString(15));
				ps.setC_showname(cursor.getString(16));
				list.add(ps);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return list;
	}

	/**
	 * 查询下一个十条
	 * 
	 * @param start
	 *            从第几行开始
	 * @return
	 */

	public List<ShiPanSeedingBean> queryNextTenData(String tabName, int start) {
		List<ShiPanSeedingBean> list = new ArrayList<ShiPanSeedingBean>();
		SQLiteDatabase db = null;
		try {
			db = dbHelper.getWritableDatabase();
			Log.i("tabName is query next ten succeed:", tabName);
			String sql = "select  * from (select * from " + tabName + " order by direct_id desc limit 10 offset k" + start + ") ct order by ct.direct_id asc";
			Cursor cursor = db.rawQuery(sql, null);
			ShiPanSeedingBean ps = null;
			while (cursor.moveToNext()) {
				ps = new ShiPanSeedingBean();
				ps.setRecordid(cursor.getString(1));
				ps.setRid(cursor.getString(2));
				ps.setLid(cursor.getString(3));
				ps.setUid(cursor.getString(4));
				ps.setMessage(cursor.getString(5));
				ps.setIs_shield(cursor.getString(6));
				ps.setIs_wonderful(cursor.getString(7));
				ps.setIs_call(cursor.getString(8));
				ps.setLevel(cursor.getString(9));
				ps.setIs_system(cursor.getString(10));
				ps.setDateline(cursor.getString(11));
				ps.setAuthority(cursor.getString(12));
				ps.setAvatar_middle(cursor.getString(13));
				ps.setAvatar_small(cursor.getString(14));
				ps.setShowname(cursor.getString(15));
				ps.setC_showname(cursor.getString(16));
				list.add(ps);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return list;
	}
	
	
	/**
	 * 查询所有
	 * 
	 * @return
	 */
	public List<ShiPanSeedingBean> queryAllData(String tabName) {
		List<ShiPanSeedingBean> list = new ArrayList<ShiPanSeedingBean>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Log.i("tabName is query all succeed:", tabName);
		Cursor cursor = db.query(tabName, null, null, null, "recordid", null, null);
		ShiPanSeedingBean ps = null;
		try {
			while (cursor.moveToNext()) {
				ps = new ShiPanSeedingBean();
				ps.setRecordid(cursor.getString(1));
				ps.setRid(cursor.getString(2));
				ps.setLid(cursor.getString(3));
				ps.setUid(cursor.getString(4));
				ps.setMessage(cursor.getString(5));
				ps.setIs_shield(cursor.getString(6));
				ps.setIs_wonderful(cursor.getString(7));
				ps.setIs_call(cursor.getString(8));
				ps.setLevel(cursor.getString(9));
				ps.setIs_system(cursor.getString(10));
				ps.setDateline(cursor.getString(11));
				ps.setAuthority(cursor.getString(12));
				ps.setAvatar_middle(cursor.getString(13));
				ps.setAvatar_small(cursor.getString(14));
				ps.setShowname(cursor.getString(15));
				ps.setC_showname(cursor.getString(16));
				list.add(ps);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return list;
	}
	
	

	/**
	 * 删除数据
	 * 
	 * @param oldNumber
	 *            要删除的条数
	 */
	public void deleteOldData(String tabName, int oldNumber) {
		SQLiteDatabase db = null;
		Log.i("tabName is delete number succeed:", tabName);
		String sql = "delete from " + tabName + " where direct_id = " + oldNumber;
		try {
			db = dbHelper.getWritableDatabase();
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * 删除所有的数据
	 */
	public void deleteAllData(String tabName) {
		SQLiteDatabase db = null;
		try {
			db = dbHelper.getWritableDatabase();
			Log.i("tabName is delete all succeed:", tabName);
			db.delete(tabName, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

	}
}
