package com.hzwydyj.finace.present.view;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.hzwydyj.finace.R;

public class PointListView extends ListView implements OnScrollListener {

	private float pointLastY = -1;
	private Scroller pointScroller;
	private OnScrollListener pointScrollListener;

	private PointListViewListener pointListViewListener;

	private PointListViewHeader pointHeaderView;
	private RelativeLayout pointHeaderViewContent;
	private TextView pointHeaderTimeView;
	private int pointHeaderViewHeight;
	private boolean pointEnablePullRefresh = true;
	private boolean pointPullRefreshing = false;

	private PointListViewFooter pointFooterView;
	private boolean pointEnablePullLoad;
	private boolean pointPullLoading;
	private boolean pointIsFooterReady = false;

	private int pointTotalItemCount;

	private int pointScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400;
	private final static int PULL_LOAD_MORE_DELTA = 50;
	private final static float OFFSET_RADIO = 1.8f;

	public PointListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public PointListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public PointListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		pointScroller = new Scroller(context, new DecelerateInterpolator());
		super.setOnScrollListener(this);

		pointHeaderView = new PointListViewHeader(context);
		pointHeaderViewContent = (RelativeLayout) pointHeaderView.findViewById(R.id.listview_header_content);
		pointHeaderTimeView = (TextView) pointHeaderView.findViewById(R.id.listview_header_time);
		addHeaderView(pointHeaderView);

		pointFooterView = new PointListViewFooter(context);

		pointHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			public void onGlobalLayout() {
				pointHeaderViewHeight = pointHeaderViewContent.getHeight();
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (pointIsFooterReady == false) {
			pointIsFooterReady = true;
			addFooterView(pointFooterView);
		}
		super.setAdapter(adapter);
	}

	public void setPullRefreshEnable(boolean enable) {
		pointEnablePullRefresh = enable;
		if (!pointEnablePullRefresh) { 
			pointHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			pointHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	public void setPullLoadEnable(boolean enable) {
		pointEnablePullLoad = enable;
		if (!pointEnablePullLoad) {
			pointFooterView.hide();
			pointFooterView.setOnClickListener(null);
		} else {
			pointPullLoading = false;
			pointFooterView.show();
			pointFooterView.setState(PointListViewFooter.STATE_NORMAL);
			pointFooterView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	public void stopRefresh() {
		if (pointPullRefreshing == true) {
			pointPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	public void stopLoadMore() {
		if (pointPullLoading == true) {
			pointPullLoading = false;
			pointFooterView.setState(PointListViewFooter.STATE_NORMAL);
		}
	}

	@SuppressWarnings("deprecation")
	public void setRefreshTime(String time) {
		pointHeaderTimeView.setText(new Date().toLocaleString());
	}

	private void invokeOnScrolling() {
		if (pointScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) pointScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		pointHeaderView.setVisiableHeight((int) delta + pointHeaderView.getVisiableHeight());
		if (pointEnablePullRefresh && !pointPullRefreshing) {
			if (pointHeaderView.getVisiableHeight() > pointHeaderViewHeight) {
				pointHeaderView.setState(PointListViewHeader.STATE_READY);
			} else {
				pointHeaderView.setState(PointListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0);
	}

	private void resetHeaderHeight() {
		int height = pointHeaderView.getVisiableHeight();
		if (height == 0)
			return;
		if (pointPullRefreshing && height <= pointHeaderViewHeight) {
			return;
		}
		int finalHeight = 0;
		if (pointPullRefreshing && height > pointHeaderViewHeight) {
			finalHeight = pointHeaderViewHeight;
		}
		pointScrollBack = SCROLLBACK_HEADER;
		pointScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = pointFooterView.getBottomMargin() + (int) delta;
		if (pointEnablePullLoad && !pointPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				pointFooterView.setState(PointListViewFooter.STATE_READY);
			} else {
				pointFooterView.setState(PointListViewFooter.STATE_NORMAL);
			}
		}
		pointFooterView.setBottomMargin(height);
	}

	private void resetFooterHeight() {
		int bottomMargin = pointFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			pointScrollBack = SCROLLBACK_FOOTER;
			pointScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		pointPullLoading = true;
		pointFooterView.setState(PointListViewFooter.STATE_LOADING);
		if (pointListViewListener != null) {
			pointListViewListener.onLoadMore();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (pointLastY == -1) {
			pointLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			pointLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - pointLastY;
			pointLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0 && (pointHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (getLastVisiblePosition() == pointTotalItemCount - 1 && (pointFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
		default:
			pointLastY = -1;
			if (getFirstVisiblePosition() == 0) {
				if (pointEnablePullRefresh && pointHeaderView.getVisiableHeight() > pointHeaderViewHeight) {
					pointPullRefreshing = true;
					pointHeaderView.setState(PointListViewHeader.STATE_REFRESHING);
					if (pointListViewListener != null) {
						pointListViewListener.onRefresh();
					}
				}
				resetHeaderHeight();
			}
			if (getLastVisiblePosition() == pointTotalItemCount - 1) {
				if (pointEnablePullLoad && pointFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (pointScroller.computeScrollOffset()) {
			if (pointScrollBack == SCROLLBACK_HEADER) {
				pointHeaderView.setVisiableHeight(pointScroller.getCurrY());
			} else {
				pointFooterView.setBottomMargin(pointScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		pointScrollListener = l;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (pointScrollListener != null) {
			pointScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		pointTotalItemCount = totalItemCount;
		if (pointScrollListener != null) {
			pointScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	public void setPointListViewListener(PointListViewListener l) {
		pointListViewListener = l;
	}

	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	public interface PointListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}
}
