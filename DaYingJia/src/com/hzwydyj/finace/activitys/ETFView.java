package com.hzwydyj.finace.activitys;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.fragments.ETF_Fragment;
import com.hzwydyj.finace.utils.MoveBg;

/**
 * 
 * @author LuoYi
 * 
 */
public class ETFView extends FragmentActivity {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.etfview);

		initBackBtn();
		initViewPager();
		initIndicatiors();
	}

	/**
	 * 返回按钮
	 */
	private void initBackBtn() {
		Button backbtn = (Button) findViewById(R.id.backbtn);
		backbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				ETFView.this.finish();
			}
		});
	}

	/**
	 * 标题点击后切换ViewPager
	 * 
	 * @param v
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.f_titile_01:
			viewPager.setCurrentItem(0);
			break;
		case R.id.f_titile_02:
			viewPager.setCurrentItem(1);
			break;

		default:
			break;
		}
	}

	// ///////////////////////////////
	// // ViewPager
	// ///////////////////////////////

	private ViewPager viewPager;
	/** 指示点的图片宽度 */
	private int bmpW;
	private LinearLayout bottom_layout, bottom_layout_b1, bottom_layout_b2, bottom_layout_b3;
	private ImageView img, img_b1, img_b2, img_b3;
	private DisplayMetrics dm;

	private void initIndicatiors() {
		bottom_layout = (LinearLayout) findViewById(R.id.indicatiors);
		bottom_layout_b1 = (LinearLayout) findViewById(R.id.indicatiors_b1);
		bottom_layout_b2 = (LinearLayout) findViewById(R.id.indicatiors_b2);
		bottom_layout_b3 = (LinearLayout) findViewById(R.id.indicatiors_b3);
		img = new ImageView(this);
		img_b1 = new ImageView(this);
		img_b2 = new ImageView(this);
		img_b3 = new ImageView(this);

		img.setImageResource(R.drawable.etf_ind);
		img_b1.setImageResource(R.drawable.btn_radio_on_disabled_holo_dark);
		img_b2.setImageResource(R.drawable.btn_radio_on_disabled_holo_dark);
		img_b3.setImageResource(R.drawable.btn_radio_on_disabled_holo_dark);

		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.etf_ind).getWidth();
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		startLeft = MoveBg.moveFrontBg(img, -bmpW / 2 + dm.widthPixels * (1) / 4, -bmpW / 2 + dm.widthPixels * (1) / 4, 0, 0);

		MoveBg.moveFrontBg(img_b1, -bmpW / 2 + dm.widthPixels * (1) / 4, -bmpW / 2 + dm.widthPixels * (1) / 4, 0, 0);
		MoveBg.moveFrontBg(img_b2, -bmpW / 2 + dm.widthPixels * (2) / 4, -bmpW / 2 + dm.widthPixels * (2) / 4, 0, 0);
		MoveBg.moveFrontBg(img_b3, -bmpW / 2 + dm.widthPixels * (3) / 4, -bmpW / 2 + dm.widthPixels * (3) / 4, 0, 0);

		// bottom_layout_b1.addView(img_b1);
		// bottom_layout_b2.addView(img_b2);
		// bottom_layout_b3.addView(img_b3);
		bottom_layout.addView(img);
	}

	private ETF_FragmentAdapter adapter;

	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		adapter = new ETF_FragmentAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	int startLeft;

	public class MyOnPageChangeListener implements OnPageChangeListener {
		public void onPageScrollStateChanged(int arg0) {
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageSelected(int arg0) {
			startLeft = MoveBg.moveFrontBg(img, startLeft, -bmpW / 2 + dm.widthPixels * (1 + arg0 * 2) / 4, 0, 0);
		}
	}

	class ETF_FragmentAdapter extends FragmentPagerAdapter {

		public ETF_FragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new ETF_Fragment();
			Bundle args = new Bundle();
			args.putInt("position", position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return Const.TABHOME_VIEW_NUMBER;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "标签1";
			case 1:
				return "标签2";
			case 2:
				return "标签3";
			}
			return null;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

}
