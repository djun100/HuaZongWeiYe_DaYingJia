/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
package com.hzwydyj.finace.present.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzwydyj.finace.R;

public class PointListViewFooter extends LinearLayout {
	public final static int 				STATE_NORMAL = 0;
	public final static int					STATE_READY = 1;
	public final static int 				STATE_LOADING = 2;

	private Context 						pointContext;

	private View 							pointContentView;
	private View 							pointProgressBar;
	private TextView 						pointHintView;

	public PointListViewFooter(Context context) {
		super(context);
		initView(context);
	}

	public PointListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public void setState(int state) {
		pointHintView.setVisibility(View.INVISIBLE);
		pointProgressBar.setVisibility(View.INVISIBLE);
		pointHintView.setVisibility(View.INVISIBLE);
		if (state == STATE_READY) {
			pointHintView.setVisibility(View.VISIBLE);
			pointHintView.setText(R.string.point_listview_footer_hint_ready);
		} else if (state == STATE_LOADING) {
			pointProgressBar.setVisibility(View.VISIBLE);
		} else {
			pointHintView.setVisibility(View.VISIBLE);
			pointHintView.setText(R.string.point_listview_footer_hint_normal);
		}
	}

	public void setBottomMargin(int height) {
		if (height < 0)
			return;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pointContentView.getLayoutParams();
		lp.bottomMargin = height;
		pointContentView.setLayoutParams(lp);
	}

	public int getBottomMargin() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pointContentView.getLayoutParams();
		return lp.bottomMargin;
	}

	/**
	 * normal status
	 */
	public void normal() {
		pointHintView.setVisibility(View.VISIBLE);
		pointProgressBar.setVisibility(View.GONE);
	}

	/**
	 * loading status
	 */
	public void loading() {
		pointHintView.setVisibility(View.GONE);
		pointProgressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * hide footer when disable pull load more
	 */
	public void hide() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pointContentView.getLayoutParams();
		lp.height = 0;
		pointContentView.setLayoutParams(lp);
	}

	/**
	 * show footer
	 */
	public void show() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pointContentView.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		pointContentView.setLayoutParams(lp);
	}

	private void initView(Context context) {
		pointContext 				= context;
		LinearLayout moreView 		= (LinearLayout) LayoutInflater.from(pointContext).inflate(R.layout.point_listview_footer, null);
		addView(moreView);
		moreView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		pointContentView 			= moreView.findViewById(R.id.xlistview_footer_content);
		pointProgressBar 			= moreView.findViewById(R.id.xlistview_footer_progressbar);
		pointHintView 				= (TextView) moreView.findViewById(R.id.xlistview_footer_hint_textview);
	}

}
