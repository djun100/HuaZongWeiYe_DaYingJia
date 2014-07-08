package com.hzwydyj.finace.activitys;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.fragments.News_keeped_Detail_Fragment;

/**
 * 
 * @author LuoYi
 *
 */
public class KeepDetailView_ViewPager extends FragmentActivity {

	@Override
	protected void onStart() {
		super.onStart();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStop(this);
	}

	String 						position;
	private TextView 			titlebar_title;
	/** 新闻数据list */
	private ArrayList<String> 	newsData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vp_main);

		titlebar_title 		= (TextView) findViewById(R.id.titlebar_title);
		titlebar_title.setText("我的收藏");

		newsData 			= getIntent().getStringArrayListExtra("link");
		position 			= getIntent().getStringExtra("position");

		InitViewPager();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			this.finish();
			break;

		default:
			break;
		}
	}

	private ViewPager mPager;

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		mPager.setAdapter(new HD_priceView_Global_FragmentPagerAdapter(getSupportFragmentManager()));
		mPager.setCurrentItem(Integer.parseInt(position));
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	class HD_priceView_Global_FragmentPagerAdapter extends FragmentPagerAdapter {

		public HD_priceView_Global_FragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			Fragment fragment 	= new News_keeped_Detail_Fragment();
			Bundle args 		= new Bundle();
			args.putString("newsid", newsData.get(position));
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			if (newsData != null) {
				return newsData.size();
			} else {
				return 0;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}
	}
}
