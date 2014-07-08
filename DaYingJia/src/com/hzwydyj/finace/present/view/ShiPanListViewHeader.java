/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.hzwydyj.finace.present.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hzwydyj.finace.R;

public class ShiPanListViewHeader extends LinearLayout {
	private LinearLayout 				pointContainer;
	private ImageView 					pointArrowImageView;
	private ProgressBar 				pointProgressBar;
	private TextView 					pointHintTextView;
	private int 						pointState = STATE_NORMAL;

	private Animation 					pointRotateUpAnim;
	private Animation 					pointRotateDownAnim;

	private final int 					ROTATE_ANIM_DURATION = 180;

	public final static int 			STATE_NORMAL = 0;
	public final static int 			STATE_READY = 1;
	public final static int 			STATE_REFRESHING = 2;

	public ShiPanListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ShiPanListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams lp 	= new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0);
		pointContainer 					= (LinearLayout) LayoutInflater.from(context).inflate(R.layout.point_listview_header, null);
		addView(pointContainer, lp);
		setGravity(Gravity.BOTTOM);

		pointArrowImageView 			= (ImageView) findViewById(R.id.listview_header_arrow);
		pointHintTextView 				= (TextView) findViewById(R.id.listview_header_hint_textview);
		pointProgressBar 				= (ProgressBar) findViewById(R.id.listview_header_progressbar);

		pointRotateUpAnim 				= new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		pointRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		pointRotateUpAnim.setFillAfter(true);
		pointRotateDownAnim 			= new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		pointRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		pointRotateDownAnim.setFillAfter(true);
	}

	public void setState(int state) {
		if (state == pointState)
			return;

		if (state == STATE_REFRESHING) { // 显示进度
			pointArrowImageView.clearAnimation();
			pointArrowImageView.setVisibility(View.INVISIBLE);
			pointProgressBar.setVisibility(View.VISIBLE);
		} else { // 显示箭头图片
			pointArrowImageView.setVisibility(View.VISIBLE);
			pointProgressBar.setVisibility(View.INVISIBLE);
		}

		switch (state) {
		case STATE_NORMAL:
			if (pointState == STATE_READY) {
				pointArrowImageView.startAnimation(pointRotateDownAnim);
			}
			if (pointState == STATE_REFRESHING) {
				pointArrowImageView.clearAnimation();
			}
			pointHintTextView.setText(R.string.point_listview_header_hint_history);
			break;
		case STATE_READY:
			if (pointState != STATE_READY) {
				pointArrowImageView.clearAnimation();
				pointArrowImageView.startAnimation(pointRotateUpAnim);
				pointHintTextView.setText(R.string.point_listview_header_hint_ready_history);
			}
			break;
		case STATE_REFRESHING:
			pointHintTextView.setText(R.string.point_listview_header_hint_loading);
			break;
		default:
		}
		pointState = state;
	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pointContainer.getLayoutParams();
		lp.height = height;
		pointContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return pointContainer.getHeight();
	}
}
