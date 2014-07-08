package com.hzwydyj.finace.view;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.KData;
import com.hzwydyj.finace.data.PriceData;
import com.hzwydyj.finace.utils.DensityUtil;
import com.hzwydyj.finace.utils.HQ;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

public class TimeDataDraw_SurfaceView extends SurfaceView implements Callback, Runnable {

	SurfaceHolder surfaceHolder;
	private boolean isThreadRunning = true;
	Canvas canvas;
	private MyLogger log = MyLogger.yLog();

	private Util util = new Util();
	private String ex = Const.SHGOLD1;
	private String decimal;
	private String code;
	private boolean initFlag = false;

	public boolean isInitFlag() {
		return initFlag;
	}

	public void setInitFlag(boolean initFlag) {
		this.initFlag = initFlag;
	}

	private String timePeriod = "";
	private Paint mPaint = null;
	/** 背景线框 */
	private Paint mPaint_bg;
	/** 背景虚线框 */
	private Paint mPaint_bg_dash;
	private Paint mPaint_text;

	private float width; // 屏幕宽度
	private float height;// 屏幕高度
	private float bottomWhite;// 底部留白
	/** 最大底边 */
	private float maxH; // 绘制图标横坐标的高度
	private float minH;
	/** 画图绝对宽度 */
	private float tableWidth; // 表格宽度
	private float tableHeight;// 表格高度
	private float leftWhite;// 左边预留空白宽度
	private float rightWhite;// 右边预留空白
	private float topWhite; // 上留空白
	private float kHeight; // K线主图高度
	/** 每点之间的间距单位 */
	private float interval; // 每一天k线柱状宽度
	private int points = 38;// 点数
	private float maxPrice = 0; // 最高价
	private float minPrice = 1000000;// 最低价
	private float lastClose = 0;// 最后收盘价
	private KData lastUpdateData = null;
	private String stockName;// 股票名称 K线title
	/** 副图绝对高度 */
	private float tradeHeight;// 交易量高度
	private float maxTradeVolume = 0;// 最大交易量
	private String beginDay;// 起始日期
	private String endDay;// 结束日期
	public float x = -1;// 指示线的横坐标位置 初始化时不显示
	/** 原始数据 */
	public List<KData> ls = null;
	/** 均值 */
	public List<Float> shareAverage = null;
	private int n = 1;// 第N点的数据
	private float kButton;// k线下边线坐标
	private float qujian;

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	Context context;

	Path path_bg_dash;

	private DecimalFormat df_base;

	public TimeDataDraw_SurfaceView(Context context, String ex, String decimal) {
		super(context);
		// log.i("TimeDataDraw_SurfaceView 构造");
		this.context = context;
		this.ex = ex;
		this.decimal = decimal;

		if (decimal != null) {
			if ("0".equals(decimal)) {
				df_base = Const.df0;
			} else if ("1".equals(decimal)) {
				df_base = Const.df1;
			} else if ("2".equals(decimal)) {
				df_base = Const.df2;
			} else if ("3".equals(decimal)) {
				df_base = Const.df3;
			} else if ("4".equals(decimal)) {
				df_base = Const.df4;
			}
		}

		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);// 注册回调方法

		textSize = DensityUtil.dip2px(context, 12);
		textSizeWhite = DensityUtil.dip2px(context, 2);
		bottomWhite = DensityUtil.dip2px(context, 16);
		leftWhite = DensityUtil.dip2px(context, 50);
		rightWhite = DensityUtil.dip2px(context, 1);
		topWhite = DensityUtil.dip2px(context, 2);

		mPaint_bg = new Paint();
		mPaint_bg.setStyle(Style.FILL);
		mPaint_bg.setStrokeWidth(1.0f);
		mPaint_bg.setColor(temp_write);

		mPaint_bg_dash = new Paint();
		mPaint_bg_dash.setStyle(Style.STROKE);
		mPaint_bg_dash.setColor(temp_write2);
		PathEffect effects2 = new DashPathEffect(new float[] { 6, 8, 6, 8 }, 1);
		mPaint_bg_dash.setPathEffect(null);
		mPaint_bg_dash.setPathEffect(effects2);
		path_bg_dash = new Path();

		mPaint_text = new Paint();
		mPaint_text.setStyle(Style.FILL);
		mPaint_text.setPathEffect(null);
		mPaint_text.setStrokeWidth(1f);
		mPaint_text.setAntiAlias(true);
		mPaint_text.setTextSize(textSize);
		FontMetrics fontMetrics = mPaint_text.getFontMetrics();
		fontHeight = fontMetrics.bottom - fontMetrics.top;
		offY = fontHeight / 2 - fontMetrics.bottom;
		x_a = leftWhite - textSizeWhite;

		mPaint = new Paint();
		mPaint.setStyle(Style.FILL);
		mPaint.setPathEffect(null);
		mPaint.setStrokeWidth(1f);
		mPaint.setAntiAlias(true);
	}

	float fontHeight;
	float offY;
	float x_a;

	int nextRed1 = Color.argb(255, 100, 0, 0);
	int nextRed2 = Color.argb(255, 100, 0, 0);
	int nextRed3 = Color.argb(255, 100, 0, 0);
	int nextRed4 = Color.argb(255, 100, 100, 0);
	int temp_write = Color.argb(255, 100, 100, 100);
	int temp_write2 = Color.argb(255, 60, 60, 60);

	int textSize;
	int textSizeWhite;

	public synchronized void draw() {

		try {// 第一种方法防止退出时异常: 当isThreadRunning为false时, 最后还是会执行一次drawView方法,
				// 但此时surfaceView已经销毁
				// 因此才来判断surfaceHolder
			if (surfaceHolder != null) {
				// 1. 在surface创建后锁定画布
				canvas = surfaceHolder.lockCanvas();
				canvas.drawColor(Color.BLACK);

				height = canvas.getHeight();
				width = canvas.getWidth();

				maxH = height - bottomWhite;
				kHeight = (maxH - topWhite) * 0.7f;
				tradeHeight = maxH - kHeight - topWhite;
				tableWidth = width - rightWhite - leftWhite;

				// 价量分界线
				canvas.drawLine(leftWhite, topWhite + kHeight, width - rightWhite, topWhite + kHeight, mPaint_bg);
				// 底边
				canvas.drawLine(leftWhite, maxH, width - rightWhite, maxH, mPaint_bg);
				// 上边
				canvas.drawLine(leftWhite, topWhite, width - rightWhite, topWhite, mPaint_bg);
				// 左边
				canvas.drawLine(leftWhite, maxH, leftWhite, topWhite, mPaint_bg);
				// 右边
				canvas.drawLine(width - rightWhite, maxH, width - rightWhite, topWhite, mPaint_bg);

				// 分时线 中值水平线
				for (int i = 1; i < 6; i++) {
					if (i == 4) {
						continue;
					}
					if (i == 5) {
						path_bg_dash.moveTo(leftWhite, topWhite + kHeight + tradeHeight / 2);
						path_bg_dash.lineTo(width - rightWhite, topWhite + kHeight + tradeHeight / 2);

					} else {
						path_bg_dash.moveTo(leftWhite, topWhite + kHeight * i / 4);
						path_bg_dash.lineTo(width - rightWhite, topWhite + kHeight * i / 4);
					}
					canvas.drawPath(path_bg_dash, mPaint_bg_dash);
				}

				// 竖虚线
				for (int i = 1; i < 6; i++) {
					path_bg_dash.moveTo(leftWhite + tableWidth * i / 6, topWhite);
					path_bg_dash.lineTo(leftWhite + tableWidth * i / 6, height - bottomWhite);
					canvas.drawPath(path_bg_dash, mPaint_bg_dash);
				}

				String price01 = "0.00", price02 = "0.00", price03 = "0.00", price04 = "0.00", price05 = "0.00";
				String Volume01 = "0", Volume02 = "0";

				if (initFlag) {

					double rul = kHeight / (qujian * 2);
					float trul = 0;
					if (maxTradeVolume > 0) {
						trul = tradeHeight / maxTradeVolume;
					}

					mPaint_text.setColor(Color.RED);
					mPaint_text.setTextSize(textSize);
					mPaint_text.setTextAlign(Paint.Align.RIGHT);
					// 计算文字高度
					FontMetrics fontMetrics = mPaint_text.getFontMetrics();
					float fontHeight = fontMetrics.bottom - fontMetrics.top;
					float offY = fontHeight / 2 - fontMetrics.bottom;
					/** 左边字x起始点 */
					float x_a = leftWhite - textSizeWhite;

					price01 = String.valueOf(df_base.format(lastClose + qujian));
					price02 = String.valueOf(df_base.format(lastClose + qujian / 2));
					price03 = String.valueOf(df_base.format((lastClose)));
					price04 = String.valueOf(df_base.format(lastClose - qujian / 2));
					price05 = String.valueOf(df_base.format(lastClose - qujian));
					Volume01 = String.valueOf((int) (maxTradeVolume));
					Volume02 = String.valueOf((int) (maxTradeVolume / 2));

					mPaint.setStyle(Style.STROKE);
					int l = ls.size();
					// log.i("l=ls.size=" + l);

					for (int i = 0; i < l && i < shareAverage.size(); i++) {
						if (i > 0 && shareAverage.size() > 0) {
							mPaint.setColor(Color.WHITE);
							mPaint.setStrokeWidth(2);
							canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
									(float) (kHeight + topWhite - (util.getFloat(ls.get(i - 1).getK_close()) - (lastClose - qujian)) * rul), (float) (interval * i + interval / 2 + leftWhite),
									(float) (kHeight + topWhite - (util.getFloat(ls.get(i).getK_close()) - (lastClose - qujian)) * rul), mPaint);
							mPaint.setColor(Color.YELLOW);
							canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (kHeight + topWhite - (shareAverage.get(i - 1) - (lastClose - qujian)) * rul),
									(float) (interval * i + interval / 2 + leftWhite), (float) (kHeight + topWhite - (shareAverage.get(i) - (lastClose - qujian)) * rul), mPaint);
						}

						mPaint.setStrokeWidth(1);
						mPaint.setColor(Color.RED);
						if (i > 0 && util.getFloat(ls.get(i - 1).getK_close()) > util.getFloat(ls.get(i).getK_close())) {
							mPaint.setColor(Color.GREEN);
						}
						// 交易量
						canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), maxH, (float) (interval * i + interval / 2 + leftWhite), maxH - util.getFloat(ls.get(i).getK_volume())
								* trul, mPaint);

					}
					// drawTitle(canvas);
					// moveLine(canvas);
				}

				// 交易量刻度
				mPaint_text.setTextAlign(Paint.Align.RIGHT);
				mPaint_text.setColor(Color.RED);
				canvas.drawText(price01, x_a, topWhite + offY * 2, mPaint_text);
				canvas.drawText(price02, x_a, topWhite + offY + kHeight * 1 / 4, mPaint_text);
				mPaint_text.setColor(Color.WHITE);
				canvas.drawText(price03, x_a, topWhite + offY + kHeight * 2 / 4, mPaint_text);
				mPaint_text.setColor(Color.GREEN);
				canvas.drawText(price04, x_a, topWhite + offY + kHeight * 3 / 4, mPaint_text);
				canvas.drawText(price05, x_a, topWhite - textSizeWhite + kHeight, mPaint_text);

				// 交易量刻度
				mPaint_text.setColor(Color.YELLOW);
				canvas.drawText(Volume01, x_a, topWhite + offY * 2 + textSizeWhite + kHeight, mPaint_text);
				canvas.drawText(Volume02, x_a, topWhite + offY + kHeight + tradeHeight / 2, mPaint_text);

				// 时间刻度

				mPaint_text.setColor(Color.YELLOW);
				mPaint_text.setTextAlign(Paint.Align.LEFT);
				if (tmp.length >= 2) {
					canvas.drawText(tmp[0], leftWhite, height, mPaint_text);
					mPaint_text.setTextAlign(Paint.Align.RIGHT);
					canvas.drawText(tmp[1], width, height, mPaint_text);
				}
				mPaint_text.setTextAlign(Paint.Align.RIGHT);
				if (tmp.length >= 4) {
					canvas.drawText(tmp[2], (width - rightWhite + leftWhite) / 2, height, mPaint_text);
					mPaint_text.setTextAlign(Paint.Align.LEFT);
					canvas.drawText(tmp[3], (width - rightWhite + leftWhite) / 2, height, mPaint_text);
				}
				if (tmp.length >= 6) {
					mPaint_text.setTextAlign(Paint.Align.CENTER);
					canvas.drawText(tmp[4], (width - rightWhite - leftWhite) / 4 + leftWhite, height, mPaint_text);
					canvas.drawText(tmp[5], (width - rightWhite - leftWhite) * 3 / 4 + leftWhite, height, mPaint_text);
				}

				mPaint.setAntiAlias(true);
				mPaint_text.setAntiAlias(true);
			} else {
				// log.i("surfaceHolder==null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// canvas是根据surfaceHolder得到的, 最后一次surfaceView已经销毁, canvas当然也不存在了.
			if (canvas != null) {
				// 3. 将画布解锁并显示在屏幕上
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// log.i("surfaceChanged");
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// 创建surfaceView时启动线程
		// log.i("surfaceCreated");
		isThreadRunning = true;
		new Thread(this).start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// 当surfaceView销毁时, 停止线程的运行. 避免surfaceView销毁了线程还在运行而报错.
		// log.i("surfaceDestroyed");

		isThreadRunning = false;
		// 第三种方法防止退出时异常. 当surfaceView销毁时让线程暂停300ms .
		// 醒来再执行run()方法时,isThreadRunning就是false了.
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setLastClose(float lastclose) {
		lastClose = lastclose;
	}

	public void setCode(String code) {
		this.code = code;
		if (HQ.SHGOLD_EX.equals(ex)) {
			points = HQ.SHGOLD_POINTS;
			timePeriod = HQ.SHGOLD_PERIOD;
		} else if (HQ.HJXH_EX.equals(ex)) {
			points = HQ.HJXH_POINTS;
			timePeriod = HQ.HJXH_PERIOD;
		} else if (HQ.STOCKINDEX_EX.equals(ex)) {
			points = HQ.STOCKINDEX_POINTS;
			timePeriod = HQ.STOCKINDEX_PERIOD;
		} else if (HQ.WH_EX.equals(ex)) {
			points = HQ.WH_POINTS;
			timePeriod = HQ.WH_PERIOD;
		} else if (HQ.NYMEX_EX.equals(ex)) {
			points = HQ.NYMEX_POINTS;
			timePeriod = HQ.NYMEX_PERIOD;
		} else if (HQ.COMEX_EX.equals(ex)) {
			points = HQ.COMEX_POINTS;
			timePeriod = HQ.COMEX_PERIOD;
		} else if (HQ.IPE_EX.equals(ex)) {
			points = HQ.IPE_POINTS;
			timePeriod = HQ.IPE_PERIOD;
		} else if (HQ.TTJ_EX.equals(ex)) {
			points = HQ.TTJ_POINTS;
			timePeriod = HQ.TTJ_PERIOD;
		} else if (HQ.SHQH_EX.equals(ex)) {
			points = HQ.SHQH_POINTS;
			timePeriod = HQ.SHQH_PERIOD;
		} else if (HQ.YGY_EX.equals(ex)) {
			points = HQ.YGY_POINTS;
			timePeriod = HQ.TTJ_PERIOD;
		} else if (HQ.HXCE_EX.equals(ex)) {// 13
			points = HQ.HXCE_POINTS;
			timePeriod = HQ.HXCE_PERIOD;
		} else if (HQ.QDCE_EX.equals(ex)) {// 15
			points = HQ.QDCE_POINTS;
			timePeriod = HQ.QDCE_PERIOD;
		} else if (HQ.HF_EX.equals(ex)) {// 16
			points = HQ.HF_POINTS;
			timePeriod = HQ.HF_PERIOD;
		} else if (HQ.BOCE_EX.equals(ex)) {// 17
			points = HQ.BOCE_POINTS;
			timePeriod = HQ.BOCE_PERIOD;
		} else if (HQ.BS_EX.equals(ex)) {// 19
			points = HQ.BS_POINTS;
			timePeriod = HQ.BS_PERIOD;
		} else if (HQ.DYYT_EX.equals(ex)) {// 20
			points = HQ.DYYT_POINTS;
			timePeriod = HQ.DYYT_PERIOD;
		} else if (HQ.TKS_EX.equals(ex)) {// 21
			points = HQ.TKS_POINTS;
			timePeriod = HQ.TKS_PERIOD;
		}

		if (Const.A0001.equals(code) || Const.A01.equals(code)) {
			points = 240;
			timePeriod = Const.CHINA_PERIOD;
		}
		if (Const.AOI.equals(code)) {
			points = 420;
			timePeriod = Const.AOI_PERIOD;
		}
		if (Const.FTSE.equals(code) || Const.DAX.equals(code) || Const.CAC40.equals(code)) {
			points = 600;
			timePeriod = Const.FTSEDAXCAC40_PERIOD;
		}
		if (Const.NASDAQ.equals(code) || Const.DJIA.equals(code) || Const.SP500.equals(code) || Const.CRB.equals(code)) {
			points = 450;
			timePeriod = Const.NASDAQDJIASP500CRB_PERIOD;
		}
		if (Const.NIKKI.equals(code) || Const.KSE.equals(code)) {
			points = 360;
			timePeriod = Const.NIKKIKSE_PERIOD;
		}
		if (Const.STI.equals(code) || Const.KLSE.equals(code)) {
			points = 480;
			timePeriod = Const.STIKLSE_PERIOD;
		}
		if (Const.PCOMP.equals(code)) {
			points = 330;
			timePeriod = Const.PCOMP_PERIOD;
		}
		if (Const.TWI.equals(code)) {
			points = 270;
			timePeriod = Const.TWI_PERIOD;
		}
		if (Const.SET.equals(code)) {
			points = 390;
			timePeriod = Const.SET_PERIOD;
		}
		if (Const.SENSEX.equals(code)) {
			points = 390;
			timePeriod = Const.SENSEX_PERIOD;
		}

		tmp = timePeriod.split("-");
	}

	String[] tmp;

	float tmpMinprice;
	float tmpMaxprice;
	float tmpMaxTradeVol;

	public synchronized void updateNew(KData data) {

		if (lastUpdateData != null) {
			String timeLong = lastUpdateData.getK_timeLong();
			String min = util.formatTimeMin(timeLong);
			String newTimeLong = data.getK_timeLong();
			String newMin = util.formatTimeMin(newTimeLong);
			if (newMin.equals(min) || (Long.valueOf(newTimeLong) - Long.valueOf(timeLong)) <= 180) {
				KData dataTmp = ls.get(ls.size() - 1);
				dataTmp.setK_average(data.getK_average());
				dataTmp.setK_close(data.getK_close());
				dataTmp.setK_date(data.getK_date());
				dataTmp.setK_timeLong(data.getK_timeLong());
				dataTmp.setK_high(data.getK_high());
				dataTmp.setK_low(data.getK_low());
				dataTmp.setK_open(data.getK_open());
				dataTmp.setK_volume(data.getK_volume());
			} else {
				ls.add(data);
			}
		} else {
			// ls.add(data);
			// shareAverage.add(util.getFloat(data.getK_average()));
		}

		float totalValue = 0f;
		float totalTrade = 0f;
		shareAverage = new ArrayList<Float>();
		for (int i = 0; i < ls.size(); i++) {
			totalValue = totalValue + util.getFloat(ls.get(i).getK_close()) * util.getFloat(ls.get(i).getK_volume());
			totalTrade = totalTrade + util.getFloat(ls.get(i).getK_volume());
			shareAverage.add(totalValue / totalTrade);
			tmpMinprice = util.getFloat(ls.get(i).getK_low());
			tmpMaxprice = util.getFloat(ls.get(i).getK_high());
			tmpMaxTradeVol = util.getFloat(ls.get(i).getK_volume());
			if (maxPrice < tmpMaxprice) {
				maxPrice = tmpMaxprice;
			}
			if (minPrice > tmpMinprice) {
				minPrice = tmpMinprice;
			}
			if (maxTradeVolume < tmpMaxTradeVol) {
				maxTradeVolume = tmpMaxTradeVol;
			}

		}

		qujian = 0l;
		if ((maxPrice - lastClose) > (lastClose - minPrice)) {
			qujian = (float) ((maxPrice - lastClose));
		} else {
			qujian = (float) ((lastClose - minPrice));
		}
		if (qujian < 0) {
			qujian = 0 - qujian;
		}

		lastUpdateData = data;
	}

	public synchronized void updateData(List<KData> data) {
		ls = data;

		shareAverage = new ArrayList<Float>();
		interval = tableWidth / points;
		float totalValue = 0f;
		float totalTrade = 0f;
		for (int i = 0; i < ls.size(); i++) {
			float volume_f = util.getFloat(ls.get(i).getK_volume());
			totalValue = totalValue + util.getFloat(ls.get(i).getK_close()) * volume_f;
			totalTrade = totalTrade + volume_f;
			shareAverage.add(totalValue / totalTrade);
			tmpMinprice = util.getFloat(ls.get(i).getK_low());
			tmpMaxprice = util.getFloat(ls.get(i).getK_high());
			tmpMaxTradeVol = util.getFloat(ls.get(i).getK_volume());
			if (maxPrice < tmpMaxprice) {
				maxPrice = tmpMaxprice;
			}
			if (minPrice > tmpMinprice) {
				minPrice = tmpMinprice;
			}
			if (maxTradeVolume < tmpMaxTradeVol) {
				maxTradeVolume = tmpMaxTradeVol;
			}

		}

		// log.i("shareAverage->" + shareAverage.size());

	}

	// public TimeDataDraw(Context context, float width, float height,
	// List<PriceData> demodata, List<Float> average) {
	// super(context);
	// mPaint = new Paint();
	// shareAverage = average;
	// // ls = demodata;
	// days = ls.size();
	// this.width = width;
	// this.height = height;
	// maxH = height - 10;
	// leftWhite = 10;
	// rightWhite = 40;
	// topWhite = 15;
	// midHeight = 15;
	// kHeight = (maxH - topWhite) * 0.7f;
	// tradeHeight = maxH - kHeight - topWhite - midHeight;
	// tableWidth = width - rightWhite - leftWhite;
	// interval = tableWidth / days;
	// for (int i = 0; i < days; i++) {
	// float tmpMinprice = util.getFloat(ls.get(i).getPrice_low());
	// float tmpMaxprice = util.getFloat(ls.get(i).getPrice_high());
	// float tmpMaxTradeVol = util.getFloat(ls.get(i).getPrice_volume());
	// if (maxPrice < tmpMaxprice) {
	// maxPrice = tmpMaxprice;
	// }
	// if (minPrice > tmpMinprice) {
	// minPrice = tmpMinprice;
	// }
	// if (maxTradeVolume < tmpMaxTradeVol) {
	// maxTradeVolume = tmpMaxTradeVol;
	// }
	//
	// }
	//
	// beginDay = "2010-04-08";
	// endDay = "2010-06-07";
	// stockName = "分时数据";
	//
	// }

	// 移动标记线
	public void moveLine(Canvas canvas) {
		if (n < ls.size() - 1) {
			canvas.drawLine(x, this.topWhite, x, kHeight + topWhite, mPaint);

			canvas.drawLine(x, this.maxH - this.tradeHeight, x, this.maxH, mPaint);
			if (n < ls.size() - 1) {
				float y = (kHeight + topWhite - (util.getFloat(ls.get(n).getK_close()) - (lastClose - qujian)) * (kHeight / (qujian * 2)));
				canvas.drawLine(leftWhite, y, width - rightWhite, y, mPaint);

			}
		}

	}

	// 移动显示日期
	public void moveDate(Canvas canvas) {
		if (x != -1) {
			float dx = leftWhite;
			if (x >= tableWidth / 2 + leftWhite)
				dx = x - this.interval / 2 - 55;
			else
				dx = x - this.interval / 2;
			if (n < ls.size() - 1) {
				String date = ls.get(n).getK_date();
				String show = " 时间：" + date + "   价：" + ls.get(n).getK_close() + "   量：" + ls.get(n).getK_volume();
				canvas.drawText(show, leftWhite + 1, maxH - tradeHeight - 3, mPaint);
			}
		}
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return this.x;
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			postInvalidate();
		}
	};

	public void redraw() {
		new Thread(this).start();
	}

	public void run() {

		while (!Thread.currentThread().isInterrupted() && isThreadRunning) {
			try {
				draw();
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			// Message msg = handler.obtainMessage();
			// handler.dispatchMessage(msg);

		}
		//
		// }

		// @Override
		// public boolean onTouchEvent(MotionEvent event) {
		// /* 取得手指触控屏幕的位置 */
		// float x = event.getX();
		// try {
		// /* 触控事件的处理 */
		// switch (event.getAction()) {
		// /* 移动位置 */
		// case MotionEvent.ACTION_MOVE:
		// if (x - this.leftWhite - interval / 2 <= 0) {
		// x = this.leftWhite + interval / 2;
		// } else if (x > this.width - this.rightWhite - interval / 2) {
		// x = this.width - this.rightWhite - interval / 2;
		//
		// } else {
		// n = (int) ((x - leftWhite) / interval);
		// x = this.leftWhite + n * interval + interval / 2;
		// setN(n);
		// }
		// setX(x);
		// break;
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return true;
		//
		// }
	}
}