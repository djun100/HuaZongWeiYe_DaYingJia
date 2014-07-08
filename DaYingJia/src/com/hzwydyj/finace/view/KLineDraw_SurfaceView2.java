/*
 * 使用SurfaceView来做游戏的基本框架. 都可以按照这个格式来做.
 */
package com.hzwydyj.finace.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.hzwydyj.finace.activitys.PriceView;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.KData;
import com.hzwydyj.finace.utils.DensityUtil;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.Util;

public class KLineDraw_SurfaceView2 extends SurfaceView implements Callback, Runnable {
	SurfaceHolder surfaceHolder;
	private boolean isThreadRunning = true;
	Canvas canvas;
	float r = 10;

	private MyLogger log = MyLogger.yLog();
	private Util util = new Util();
	private String ex = Const.SHGOLD1;
	private String index = "";
	private boolean indexMA = true;
	private boolean initFlag = false;
	private Context context;

	public boolean isInitFlag() {
		return initFlag;
	}

	public void setInitFlag(boolean initFlag) {
		this.initFlag = initFlag;
	}

	private int drawLineCount = 0;
	private final int CMA5 = 5;
	private final int CMA10 = 10;
	private final int CMA20 = 20;
	private final int macdParam[] = { 12, 26, 9 };
	private final int volParam[] = { 5, 10, 20 };
	private final int kdjParam[] = { 9, 3, 3 };
	private final int rsiParam[] = { 6, 12, 24 };
	private final int bollParam[] = { 26, 2 };
	private final int obvParam[] = { 12 };
	private final int cciParam[] = { 14 };
	private final int psyParam[] = { 12, 24 };
	private float macdData[][];
	private float volData[][];
	private float kdjData[][];
	private float rsiData[][];
	private float bollData[][];
	private float obvData[][];
	private float cciData[][];
	private float psyData[][];
	/** 原始数据 */
	private List<Float> maSet1list;
	/** 原始数据 */
	private List<Float> maSet2list;
	/** 原始数据 */
	private List<Float> maSet3list;
	/** 点数设置 */
	private int points = 38;

	public int getPoints() {
		return points;
	}

	/** 最后收盘价 */
	private float lastClose = 0;
	private String code;

	private Paint mPaint = null;
	/** 画布总宽度 */
	private float width;
	/** 画布总高度 */
	private float height;
	/** 最下边横线的高度 */
	private float maxH;
	/** 表宽 */
	private float tableWidth;
	/** 表高 */
	private float tableHeight;
	/** 左边留白 */
	private float leftWhite;
	/** 右边留白 */
	private float rightWhite;
	/** 上边留白 */
	private float topWhite;
	/** 主图高度 */
	private float kHeight;
	/** 每根K线宽度 */
	private float interval;
	/** 主图最高价 */
	private float maxPrice = 0;
	/** 主图最低价 */
	private float minPrice = 1000000;
	/** 副图最高价 */
	private float maxZB = -1000000;
	/** 副图最低价 */
	private float minZB = 1000000;
	/** 副图高度 */
	private float tradeHeight;
	/** 最大交易量 */
	// private float maxTradeVolume = 0;
	/** 交易量 显示日期行的宽度 */
	private float midHeight;
	/** touch线的横坐标位置 */
	public float x = -1;
	/** 完整K线数据 */
	public List<KData> ls = null;
	/** 第n天的数据 */
	private int n = 1;

	/**
	 * 获得第n天
	 * 
	 * @return
	 */
	public int getN() {
		return n;
	}

	/**
	 * 设置第n天
	 * 
	 * @param n
	 */
	public void setN(int n) {
		this.n = n;
	}

	/**
	 * 设置指标数据
	 * 
	 * @param index
	 */
	public void setIndex(String index) {
		try {
			// log.i("setIndex");
			macdData = null;
			volData = null;
			kdjData = null;
			rsiData = null;
			bollData = null;
			obvData = null;
			cciData = null;
			psyData = null;

			// 布林指标 ,其他指标都需要macd线在主图上
			if (!Const.INDEX_BOLL.equals(index)) {
				this.index = index;

				indexMA = true;

				// maSet 初始化
				if (maSet1list != null) {
					maSet1list.clear();
				}
				if (maSet2list != null) {
					maSet2list.clear();
				}
				if (maSet3list != null) {
					maSet3list.clear();
				}

				maSet1list = new ArrayList<Float>();
				maSet2list = new ArrayList<Float>();
				maSet3list = new ArrayList<Float>();

				// ma计算
				for (int i = 0; i < ls.size(); i++) {
					float ma5temp = 0;
					float ma10temp = 0;
					float ma20temp = 0;
					if (i >= CMA5 - 1) {
						for (int j = 0; j < CMA5; j++) {
							ma5temp = ma5temp + util.getFloat(ls.get(i - j).getK_close());
						}
						ma5temp = ma5temp / CMA5;
					}
					if (i >= CMA10 - 1) {
						for (int j = 0; j < CMA10; j++) {
							ma10temp = ma10temp + util.getFloat(ls.get(i - j).getK_close());
						}
						ma10temp = ma10temp / CMA10;

					}
					if (i >= CMA20 - 1) {
						for (int j = 0; j < CMA20; j++) {
							ma20temp = ma20temp + util.getFloat(ls.get(i - j).getK_close());
						}
						ma20temp = ma20temp / CMA20;
					}
					maSet1list.add(ma5temp);
					maSet2list.add(ma10temp);
					maSet3list.add(ma20temp);
				}
				// log.i("ma1=" + maSet1list.size() + ",ma2=" +
				// maSet2list.size() + ",ma3=" + maSet3list.size());

				// float totalValue = 0f;
				// float totalTrade = 0f;
				maxPrice = 0; // 最高价
				minPrice = 100000;// 最低价
				// maxTradeVolume = 0;// 最大交易量

				for (int i = 0; i < ls.size(); i++) {
					// totalValue = totalValue +
					// util.getFloat(ls.get(i).getK_close()) *
					// util.getFloat(ls.get(i).getK_volume());
					// totalTrade = totalTrade +
					// util.getFloat(ls.get(i).getK_volume());
					float tmpMinprice = util.getFloat(ls.get(i).getK_low());
					float tmpMaxprice = util.getFloat(ls.get(i).getK_high());
					float tmpMaxTradeVol = util.getFloat(ls.get(i).getK_volume());
					float tmpMaxMa = maSet1list.get(i);
					float tmpMaxMa2 = maSet2list.get(i);
					float tmpMaxMa3 = maSet3list.get(i);
					if (i >= points) {
						if (maxPrice < tmpMaxprice) {
							maxPrice = tmpMaxprice;
						}
						if (maxPrice < tmpMaxMa) {
							maxPrice = tmpMaxMa;
						}
						if (maxPrice < tmpMaxMa2) {
							maxPrice = tmpMaxMa2;
						}
						if (maxPrice < tmpMaxMa3) {
							maxPrice = tmpMaxMa3;
						}

						if (minPrice > tmpMinprice) {
							minPrice = tmpMinprice;
						}
						if (minPrice > tmpMaxMa) {
							minPrice = tmpMaxMa;
						}
						if (minPrice > tmpMaxMa2) {
							minPrice = tmpMaxMa2;
						}
						if (minPrice > tmpMaxMa3) {
							minPrice = tmpMaxMa3;
						}
						// if (maxTradeVolume < tmpMaxTradeVol) {
						// maxTradeVolume = tmpMaxTradeVol;
						// }
					}
				}
			} else {
				// 布林指标
				this.index = index;
				indexMA = false;
				bollData = new float[3][ls.size()];
				float average[] = bollData[0];
				float up[] = bollData[1];
				float down[] = bollData[2];
				float sum = 0.0F;

				// 全部数据更新
				for (int i = 0; i < ls.size(); i++) {
					float aveTemp = 0;
					if (i >= bollParam[0] - 1) {
						for (int j = 0; j < bollParam[0]; j++) {
							aveTemp += util.getFloat(ls.get(i - j).getK_close());
						}
						aveTemp = aveTemp / bollParam[0];
					}
					average[i] = aveTemp;
				}
				for (int i = bollParam[0] - 1; i < (bollParam[0] + bollParam[0]) - 2; i++) {
					float value = util.getFloat(ls.get(i).getK_close()) - average[i];
					sum += value * value;
				}

				float prevalue = 0.0F;
				for (int i = (bollParam[0] + bollParam[0]) - 2; i < ls.size(); i++) {
					sum -= prevalue;
					float value = util.getFloat(ls.get(i).getK_close()) - average[i];
					sum += value * value;
					value = (float) Math.sqrt(sum / (float) bollParam[0]) * bollParam[1];
					up[i] = average[i] + value;
					down[i] = average[i] - value;
					if (i > points && i < points + draw_points) {
						if (maxPrice < up[i]) {
							maxPrice = up[i];
						}
						if (minPrice > down[i]) {
							minPrice = down[i];
						}
					}
					value = util.getFloat(ls.get((i - bollParam[0]) + 1).getK_close()) - average[(i - bollParam[0]) + 1];
					prevalue = value * value;
				}

			}

			if (Const.INDEX_MACD.equals(index)) {
				macdData = new float[3][ls.size()];
				maxZB = -1000000; // 最高价
				minZB = 1000000;// 最低价
				macdData = new float[3][ls.size()];
				for (int i = 0; i < 3; i++) {
					if (macdParam[i] > ls.size() || macdParam[i] < 1)
						return;
				}
				float dif[] = macdData[0];
				float macd[] = macdData[1];
				float d_m[] = macdData[2];
				float di = 0.0F;
				float a = 0.0F;
				float b = 0.0F;
				float para[] = new float[3];
				float sum[] = new float[3];
				int n[] = new int[3];
				for (int i = 0; i < 3; i++) {
					n[i] = macdParam[i];
					para[i] = 2.0F / (float) (n[i] + 1);
					sum[i] = 0.0F;
				}
				for (int i = 0; i < ls.size(); i++) {
					di = util.getFloat(ls.get(i).getK_close());
					if (i < n[0]) {
						sum[0] += di;
						a = i != n[0] - 1 ? 0.0F : sum[0] / (float) n[0];
					} else {
						a = (di - a) * para[0] + a;
					}
					if (i < n[1]) {
						sum[1] += di;
						b = i != n[1] - 1 ? 0.0F : sum[1] / (float) n[1];
					} else {
						b = (di - b) * para[1] + b;
					}
					dif[i] = i < n[0] - 1 || i < n[1] ? 0.0F : a - b;
					if (i > points) {
						if (maxZB <= dif[i]) {
							maxZB = dif[i];
						}
						if (minZB >= dif[i]) {
							minZB = dif[i];
						}
					}
					if (i < n[1] + n[2]) {
						sum[2] += dif[i];
						macd[i] = i != (n[1] + n[2]) - 1 ? 0.0F : sum[2] / (float) n[2];
					} else {
						macd[i] = (float) ((double) (dif[i] - macd[i - 1]) * 0.20000000000000001D) + macd[i - 1];
					}
					if (i > points) {
						if (maxZB <= macd[i]) {
							maxZB = macd[i];
						}
						if (minZB >= macd[i]) {
							minZB = macd[i];
						}
					}

					d_m[i] = dif[i] - macd[i];
					if (i > points) {
						if (maxZB <= d_m[i]) {
							maxZB = d_m[i];
						}
						if (minZB >= d_m[i]) {
							minZB = d_m[i];
						}
					}
				}

			}
			// VOL
			if (Const.INDEX_VOL.equals(index)) {
				volData = new float[4][ls.size()];
				maxZB = -1000000;
				float total[] = volData[3];
				float ma5[] = volData[0];
				float ma10[] = volData[1];
				float ma20[] = volData[2];
				for (int i = 0; i < ls.size(); i++) {

					KData tmp = ls.get(i);
					total[i] = util.getFloat(tmp.getK_volume());
					if (i >= points && i < points + draw_points) {
						if (maxZB < total[i]) {
							maxZB = total[i];
						}
					}
					float ma5temp = 0;
					float ma10temp = 0;
					float ma20temp = 0;
					if (i >= volParam[0] - 1) {
						for (int j = 0; j < volParam[0]; j++) {
							ma5temp = ma5temp + total[i - j];
						}
						ma5temp = ma5temp / volParam[0];
					}
					if (i >= volParam[1] - 1) {
						for (int j = 0; j < volParam[1]; j++) {
							ma10temp = ma10temp + total[i - j];
						}
						ma10temp = ma10temp / volParam[1];

					}
					if (i >= volParam[2] - 1) {
						for (int j = 0; j < volParam[2]; j++) {
							ma20temp = ma20temp + total[i - j];
						}
						ma20temp = ma20temp / volParam[2];
					}
					ma5[i] = ma5temp;
					ma10[i] = ma10temp;
					ma20[i] = ma20temp;

				}
			}
			// KDJ
			if (Const.INDEX_KDJ.equals(index)) {
				kdjData = new float[3][ls.size()];
				maxZB = -1000000;
				minZB = 1000000;
				float maxPrice = 0;
				float minPrice = 0;
				int n1 = kdjParam[0];
				int n2 = kdjParam[1];
				int n3 = kdjParam[2];
				float kvalue[] = kdjData[0];
				float dvalue[] = kdjData[1];
				float jvalue[] = kdjData[2];
				n2 = n2 > 0 ? n2 : 3;
				n3 = n3 > 0 ? n3 : 3;
				maxPrice = util.getFloat(ls.get(n1 - 1).getK_high());
				minPrice = util.getFloat(ls.get(n1 - 1).getK_low());
				for (int j = n1 - 1; j >= 0; j--) {
					if (maxPrice < util.getFloat(ls.get(j).getK_high()))
						maxPrice = util.getFloat(ls.get(j).getK_high());
					if (minPrice > util.getFloat(ls.get(j).getK_low()))
						minPrice = util.getFloat(ls.get(j).getK_low());
				}

				float rsv;
				if (maxPrice <= minPrice)
					rsv = 50F;
				else
					rsv = ((util.getFloat(ls.get(n1 - 1).getK_close()) - minPrice) / (maxPrice - minPrice)) * 100F;
				float prersv;
				kvalue[n1 - 1] = dvalue[n1 - 1] = jvalue[n1 - 1] = prersv = rsv;
				for (int i = 0; i < n1; i++) {
					kvalue[i] = 0.0F;
					dvalue[i] = 0.0F;
					jvalue[i] = 0.0F;
				}

				for (int i = n1; i < ls.size(); i++) {
					maxPrice = util.getFloat(ls.get(i).getK_high());
					minPrice = util.getFloat(ls.get(i).getK_low());
					for (int j = i - 1; j > i - n1; j--) {
						if (maxPrice < util.getFloat(ls.get(j).getK_high()))
							maxPrice = util.getFloat(ls.get(j).getK_high());
						if (minPrice > util.getFloat(ls.get(j).getK_low()))
							minPrice = util.getFloat(ls.get(j).getK_low());
					}

					if (maxPrice <= minPrice) {
						rsv = prersv;
					} else {
						prersv = rsv;
						rsv = ((util.getFloat(ls.get(i).getK_close()) - minPrice) / (maxPrice - minPrice)) * 100F;
					}
					kvalue[i] = (kvalue[i - 1] * (float) (n2 - 1)) / (float) n2 + rsv / (float) n2;
					dvalue[i] = kvalue[i] / (float) n3 + (dvalue[i - 1] * (float) (n3 - 1)) / (float) n3;
					jvalue[i] = 3F * kvalue[i] - 2.0F * dvalue[i];
					if (i > points) {
						if (maxZB < kvalue[i]) {
							maxZB = kvalue[i];
						}
						if (maxZB < dvalue[i]) {
							maxZB = dvalue[i];
						}
						if (maxZB < jvalue[i]) {
							maxZB = jvalue[i];
						}
						if (minZB > kvalue[i]) {
							minZB = kvalue[i];
						}
						if (minZB > dvalue[i]) {
							minZB = dvalue[i];
						}
						if (minZB > jvalue[i]) {
							minZB = jvalue[i];
						}
					}
				}
			}
			// RSI
			if (Const.INDEX_RSI.equals(index)) {
				rsiData = new float[3][ls.size()];
				maxZB = 0;
				minZB = 10000;
				float[] up = new float[3];
				float[] down = new float[3];

				for (int k = 0; k < 3; k++) {
					for (int i = 1; i < rsiParam[k]; i++) {
						if (util.getFloat(ls.get(i).getK_close()) > util.getFloat(ls.get(i - 1).getK_close())) {
							up[k] += (util.getFloat(ls.get(i).getK_close()) - util.getFloat(ls.get(i - 1).getK_close()));
						} else {
							down[k] += (util.getFloat(ls.get(i - 1).getK_close()) - util.getFloat(ls.get(i).getK_close()));
						}
					}
					if (up[k] + down[k] == 0.0F) {
						rsiData[k][rsiParam[k] - 1] = 50F;
					} else {
						rsiData[k][rsiParam[k] - 1] = (up[k] / (up[k] + down[k])) * 100F;
					}
				}
				float[] predown = new float[3];
				float[] preup = new float[3];
				for (int i = 1; i < ls.size(); i++) {
					for (int k = 0; k < 3; k++) {
						if (i < rsiParam[k]) {
							break;
						}
						up[k] -= preup[k];
						down[k] -= predown[k];
						if (util.getFloat(ls.get(i).getK_close()) > util.getFloat(ls.get(i - 1).getK_close())) {
							up[k] += (util.getFloat(ls.get(i).getK_close()) - util.getFloat(ls.get(i - 1).getK_close()));
						} else {
							down[k] += (util.getFloat(ls.get(i - 1).getK_close()) - util.getFloat(ls.get(i).getK_close()));
						}
						if (up[k] + down[k] == 0.0F) {
							rsiData[k][i] = rsiData[k][i - 1];
						}

						else {
							rsiData[k][i] = ((up[k]) / (up[k] + down[k])) * 100F;
						}

						preup[k] = predown[k] = 0.0F;
						if (util.getFloat(ls.get((i - rsiParam[k]) + 1).getK_close()) > util.getFloat(ls.get((i - rsiParam[k])).getK_close())) {
							preup[k] = util.getFloat(ls.get((i - rsiParam[k]) + 1).getK_close()) - util.getFloat(ls.get((i - rsiParam[k])).getK_close());
						} else {
							predown[k] = util.getFloat(ls.get((i - rsiParam[k]) + 1).getK_close()) - util.getFloat(ls.get((i - rsiParam[k])).getK_close());
						}
						if (i > points) {
							if (maxZB < rsiData[k][i]) {
								maxZB = rsiData[k][i];
							}
							if (minZB > rsiData[k][i]) {
								minZB = rsiData[k][i];
							}
						}
					}
				}
			}
			// OBV
			if (Const.INDEX_OBV.equals(index)) {
				obvData = new float[2][ls.size()];
				maxZB = -10000;
				minZB = 10000;
				float obv[] = obvData[0];
				obv[0] = 0.0F;
				float total = util.getFloat(ls.get(0).getK_close()) * util.getFloat(ls.get(0).getK_volume());
				for (int i = 1; i < ls.size(); i++) {
					total += util.getFloat(ls.get(i).getK_close()) * util.getFloat(ls.get(i).getK_volume());
					if (util.getFloat(ls.get(i).getK_close()) > util.getFloat(ls.get(i - 1).getK_close())) {
						obv[i] = obv[i - 1] + (float) (total / 1000L);
					} else {
						if (util.getFloat(ls.get(i).getK_close()) < util.getFloat(ls.get(i - 1).getK_close())) {
							obv[i] = obv[i - 1] - (float) (total / 1000L);
						} else {
							obv[i] = obv[i - 1];
						}
					}
					if (i > points) {
						if (maxZB < obv[i]) {
							maxZB = obv[i];
						}
						if (minZB > obv[i]) {
							minZB = obv[i];
						}
					}
				}
			}
			// CCI
			if (Const.INDEX_CCI.equals(index)) {
				cciData = new float[2][ls.size()];
				maxZB = -10000;
				minZB = 10000;
				float cci[] = cciData[0];
				float ma[] = cciData[1];
				cci[0] = 0.0F;
				double sum = 0.0D;
				for (int i = 0; i < cciParam[0] - 1; i++) {
					sum += (util.getFloat(ls.get(0).getK_high()) + util.getFloat(ls.get(0).getK_low()) + util.getFloat(ls.get(0).getK_close())) / 3F;
				}
				float prec = 0.0F;
				for (int i = cciParam[0] - 1; i < ls.size(); i++) {
					sum -= prec;
					sum += (util.getFloat(ls.get(i).getK_high()) + util.getFloat(ls.get(i).getK_low()) + util.getFloat(ls.get(i).getK_close())) / 3F;
					ma[i] = (float) (sum / (double) cciParam[0]);
					prec = (util.getFloat(ls.get(i - cciParam[0] + 1).getK_high()) + util.getFloat(ls.get(i - cciParam[0] + 1).getK_low()) + util.getFloat(ls.get(i - cciParam[0] + 1).getK_close())) / 3F;
				}
				cci[cciParam[0] - 2] = 0.0F;
				for (int i = cciParam[0] - 1; i < ls.size(); i++) {
					sum = 0.0D;
					for (int j = (i - cciParam[0]) + 1; j <= i; j++)
						sum += Math.abs((util.getFloat(ls.get(j).getK_high()) + util.getFloat(ls.get(j).getK_low()) + util.getFloat(ls.get(j).getK_close())) / 3F - ma[i]);

					if (sum == 0.0D)
						cci[i] = cci[i - 1];
					else
						cci[i] = (float) ((double) ((util.getFloat(ls.get(i).getK_high()) + util.getFloat(ls.get(i).getK_low()) + util.getFloat(ls.get(i).getK_close())) / 3F - ma[i]) / ((0.014999999999999999D * sum) / (double) cciParam[0]));

					if (i > points) {
						if (maxZB < cci[i]) {
							maxZB = cci[i];
						}
						if (minZB > cci[i]) {
							minZB = cci[i];
						}
					}
				}

			}
			// PSY
			if (Const.INDEX_PSY.equals(index)) {
				psyData = new float[2][ls.size()];
				maxZB = -10000;
				minZB = 10000;
				float psy[] = psyData[0];
				double sum = 0.0D;
				for (int i = 1; i < psyParam[0]; i++) {
					if (util.getFloat(ls.get(i).getK_close()) > util.getFloat(ls.get(i - 1).getK_close())) {
						sum++;
					}

				}
				for (int i = psyParam[0]; i < ls.size(); i++) {
					if (util.getFloat(ls.get(i).getK_close()) > util.getFloat(ls.get(i - 1).getK_close()))
						sum++;
					psy[i] = (float) ((sum / (double) psyParam[0]) * 100D);
					int j = (i - psyParam[0]) + 1;
					if (util.getFloat(ls.get(j).getK_close()) > util.getFloat(ls.get(j - 1).getK_close()))
						sum--;

					if (i > points) {
						if (maxZB < psy[i]) {
							maxZB = psy[i];
						}
						if (minZB > psy[i]) {
							minZB = psy[i];
						}
					}
				}

			}

			Message msg = handler.obtainMessage();
			handler.dispatchMessage(msg);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void KLineDraw_sr(Context context, float wid, float hei, String ex) {
		// super(context);
		this.context = context;
		mPaint = new Paint();
		this.width = wid + 0.1f;
		this.height = hei + 0.1f;
		maxH = hei - 10.1f;
		leftWhite = 80.1f;
		rightWhite = 1.1f;
		topWhite = 22.1f;
		midHeight = 22.1f;
		kHeight = (maxH - topWhite) * 0.7f;
		tradeHeight = maxH - kHeight - topWhite - midHeight;
		tableWidth = width - rightWhite - leftWhite;
		this.ex = ex;
		this.setBackgroundColor(Color.BLACK);
		new Thread(this).start();
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setLastClose(float lastclose) {
		lastClose = lastclose;
	}

	/** 需要画出来的点数 */
	private int draw_points = 0;

	public synchronized void updateData(List<KData> data, int point) {

		ls = data;

		// 起始点的位置 = 数据总量 - 需要设置的点数
		points = ls.size() - point;

		// 需要画出来的点数
		draw_points = point;

		interval = tableWidth / point;

		if ("".equals(index)) {
			// log.i("if-index-空");
			setIndex(Const.INDEX_MACD);
			return;
		}

		if (indexMA) {
			// log.i("indexMA=true");
			setIndex(index);
		} else {
			// log.i("indexMA=false");
			setIndex(Const.INDEX_BOLL);
		}

	}

	List<KData> lsDraw_all = new ArrayList<KData>();
	List<Float> maSet1listDraw_all = new ArrayList<Float>();
	List<Float> maSet2listDraw_all = new ArrayList<Float>();
	List<Float> maSet3listDraw_all = new ArrayList<Float>();
	float macdDataDraw_all[][] = null;
	float volDataDraw_all[][] = null;
	float kdjDataDraw_all[][] = null;
	float rsiDataDraw_all[][] = null;
	float bollDataDraw_all[][] = null;
	float obvDataDraw_all[][] = null;
	float cciDataDraw_all[][] = null;
	float psyDataDraw_all[][] = null;

	private int trans_gray = Color.argb(220, 100, 100, 100);

	private float rect_height;
	private float rect_width;
	private float rect_white;

	/**
	 * 移动白线
	 * 
	 * @param canvas
	 * @param ls
	 */
	public void moveLine(Canvas canvas, List<KData> ls) {
		if (n <= ls.size() - 1 && drawLineCount > 0) {
			drawLineCount--;
			mPaint.setColor(Color.WHITE);
			canvas.drawLine(x, this.topWhite, x, kHeight + topWhite, mPaint);

			rect_height = topWhite + rect_white + textSizeWhite * 7 + offY_move * 8;

			float textX = 0f;
			if (n < draw_points / 2) {
				canvas.drawRect(width - rightWhite - rect_white * 2 - rect_width, topWhite + rect_white, width - rightWhite - rect_white, topWhite + rect_white + rect_height, mPaint_move_bg);// 正方形
				textX = width - rightWhite - rect_white - rect_width;
			} else {
				canvas.drawRect(leftWhite + rect_white, topWhite + rect_white, leftWhite + rect_white + rect_width, topWhite + rect_white + rect_height, mPaint_move_bg);// 正方形
				textX = leftWhite + rect_white * 2;
			}

			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color.WHITE);
			// mPaint.setTextSize(DensityUtil.dip2px(context, 10));
			String[] time = ls.get(n).getK_date().split(" ");
			String[] date = time[0].split("/");
			// String[] t = time[1].split(":");
			if ("00:00:00".equals(time[1])) {
				canvas.drawText("时:" + date[2] + date[0] + date[1], textX, topWhite + rect_white + textSizeWhite + offY_move * 2, mPaint);
			} else {
				canvas.drawText("时:" + time[1], textX, topWhite + rect_white + textSizeWhite + offY_move * 2, mPaint);
			}

			canvas.drawText("开:" + df_base.format(Double.parseDouble(ls.get(n).getK_open())), textX, topWhite + rect_white + textSizeWhite * 3 + offY_move * 4, mPaint);
			canvas.drawText("高:" + df_base.format(Double.parseDouble(ls.get(n).getK_high())), textX, topWhite + rect_white + textSizeWhite * 5 + offY_move * 6, mPaint);
			canvas.drawText("低:" + df_base.format(Double.parseDouble(ls.get(n).getK_low())), textX, topWhite + rect_white + textSizeWhite * 7 + offY_move * 8, mPaint);
			canvas.drawText("收:" + df_base.format(Double.parseDouble(ls.get(n).getK_close())), textX, topWhite + rect_white + textSizeWhite * 9 + offY_move * 10, mPaint);

		}

	}

	// // 移动显示日期
	// public void moveDate(Canvas canvas, List<KData> ls) {
	// if (x != -1) {
	// float dx = leftWhite;
	// if (x >= tableWidth / 2 + leftWhite)
	// dx = x - this.interval / 2 - 55;
	// else
	// dx = x - this.interval / 2;
	// if (n < ls.size() - 1) {
	// String date = ls.get(n).getK_date();
	// // String time = date.substring(date.length() - 6);
	// String show = date + "   开：" + ls.get(n).getK_open() + "   收："
	// + ls.get(n).getK_close() + "   高："
	// + ls.get(n).getK_high() + "   低："
	// + ls.get(n).getK_low();
	// canvas.drawText(show, leftWhite + 1, maxH - tradeHeight - 3,
	// mPaint);
	// }
	// }
	// }

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

	private int offset = 0;
	/** 单指还是双指状态 */
	private boolean multiPointer = false;
	/** 第二次点击有效开关 */
	private boolean opentouch2 = false;
	/** 单指缩放开关 */
	private boolean touch2 = false;
	private int count_opentouch2 = 0;

	private boolean choiceline = false;

	public void setChoiceline(Boolean bool) {
		choiceline = bool;
	}

	public boolean getChoiceline() {
		return choiceline;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		multiPointer = event.getPointerCount() > 1 ? true : false;
		drawLineCount = -10;

		// log.i("点击时间" + event.getEventTime());
		try {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				thread_sleep_time = 100;

			case MotionEvent.ACTION_MOVE:

				int temp = DensityUtil.px2dip(getContext(), event.getX());
				// log.i("temp 1->" + temp);
				if (PriceView.move != temp && PriceView.move != 0 && choiceline == false) { // 左移
					// log.i("temp->" + temp);
					// log.i("move->" + Priceview_Activity.move);

					// points判断
					if (Math.abs((temp - PriceView.move)) > 2) {
						// 移动太快的bug
						if (temp - PriceView.move > 0) {
							offset = 1;
						} else if (temp - PriceView.move < 0) {
							offset = -1;
						}
					} else {
						offset = (temp - PriceView.move);
					}
					// log.i("offset->" + offset);
					updateData();
				}

				PriceView.move = temp;
				break;
			case MotionEvent.ACTION_UP:

				PriceView.move = 0;
				thread_sleep_time = 200;
				// offset = 0;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// if (multiPointer == false) {
		if (choiceline) {
			/* 取得手指触控屏幕的位置 */
			float x = event.getX();
			try {
				/* 触控事件的处理 */
				switch (event.getAction()) {
				/* 移动位置 */
				case MotionEvent.ACTION_MOVE:
					drawLineCount = 30;
					if (x - this.leftWhite - interval / 2 <= 0) {
						x = this.leftWhite + interval / 2;
					} else if (x > this.width - this.rightWhite - interval / 2) {
						x = this.width - this.rightWhite - interval / 2;

					} else {

					}

					n = (int) ((x - leftWhite) / interval);
					x = this.leftWhite + n * interval + interval / 2;
					setN(n);
					setX(x);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	private static int thread_sleep_time = 200;

	public void run2() {

		while (!Thread.currentThread().isInterrupted()) {

			try {
				Thread.sleep(thread_sleep_time);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			this.postInvalidate();

		}
	}

	public void update_drawpoints(boolean out) {

		if (out) {
			offset = -9;
			draw_points -= 9;
		} else {
			offset = 9;
			draw_points += 9;
		}
		updateData();
	}

	public synchronized void updateData() {

		// float totalValue = 0f;
		// float totalTrade = 0f;
		maxPrice = 0; // 最高价
		minPrice = 100000;// 最低价
		// maxTradeVolume = 0;// 最大交易量

		// for (int i = points; i < points + draw_points; i++) {
		for (int i = 0; i < ls.size(); i++) {

			// totalValue = totalValue + util.getFloat(ls.get(i).getK_close()) *
			// util.getFloat(ls.get(i).getK_volume());
			// totalTrade = totalTrade + util.getFloat(ls.get(i).getK_volume());
			float tmpMinprice = util.getFloat(ls.get(i).getK_low());
			float tmpMaxprice = util.getFloat(ls.get(i).getK_high());
			float tmpMaxTradeVol = util.getFloat(ls.get(i).getK_volume());

			float tmpMaxMa = maSet1list.get(i);
			float tmpMaxMa2 = maSet2list.get(i);
			float tmpMaxMa3 = maSet3list.get(i);
			if (i >= points - 1 && i < points + draw_points) {

				// log.i("updateData->" + points);
				if (maxPrice < tmpMaxprice) {
					maxPrice = tmpMaxprice;
				}
				if (maxPrice < tmpMaxMa) {
					maxPrice = tmpMaxMa;
				}
				if (maxPrice < tmpMaxMa2) {
					maxPrice = tmpMaxMa2;
				}
				if (maxPrice < tmpMaxMa3) {
					maxPrice = tmpMaxMa3;
				}

				if (minPrice > tmpMinprice) {
					minPrice = tmpMinprice;
				}
				if (minPrice > tmpMaxMa) {
					minPrice = tmpMaxMa;
				}
				if (minPrice > tmpMaxMa2) {
					minPrice = tmpMaxMa2;
				}
				if (minPrice > tmpMaxMa3) {
					minPrice = tmpMaxMa3;
				}

				// if (maxTradeVolume < tmpMaxTradeVol) {
				// maxTradeVolume = tmpMaxTradeVol;
				// }
			}
		}

		if ("".equals(index)) {
			setIndex_touch(Const.INDEX_MACD);
			return;
		}

		if (indexMA) {
			setIndex_touch(index);
		} else {
			setIndex_touch(Const.INDEX_BOLL);
		}

		if (points + draw_points >= ls.size()) {
			right2edge = true;
		}
	}

	private boolean right2edge = false;

	public boolean getRight2edge() {
		return right2edge;
	}

	/**
	 * 设置指标数据
	 * 
	 * @param index
	 */
	public void setIndex_touch(String index) {
		// log.i("setIndex_touch");

		// 不是布林指标
		if (!Const.INDEX_BOLL.equals(index)) {
			this.index = index;

			indexMA = true;
		} else {
			// 布林指标
			this.index = index;
			indexMA = false;
			float up[] = bollData[1];
			float down[] = bollData[2];

			for (int i = (bollParam[0] + bollParam[0]) - 2; i < ls.size(); i++) {
				if (i >= points && i < points + draw_points) {
					if (maxPrice < up[i]) {
						maxPrice = up[i];
					}
					if (minPrice > down[i]) {
						minPrice = down[i];
					}
				}
			}

		}
		if (Const.INDEX_MACD.equals(index)) {
			maxZB = -1000000; // 最高价
			minZB = 1000000;// 最低价
			for (int i = 0; i < 3; i++) {
				if (macdParam[i] > ls.size() || macdParam[i] < 1)
					return;
			}
			float dif[] = macdData[0];
			float macd[] = macdData[1];
			float d_m[] = macdData[2];
			for (int i = 0; i < ls.size(); i++) {
				if (i >= points - 1 && i < points + draw_points) {
					// if (i >= points) {
					if (maxZB <= dif[i]) {
						maxZB = dif[i];
					}
					if (minZB >= dif[i]) {
						minZB = dif[i];
					}
				}
				if (i >= points - 1 && i < points + draw_points) {
					// if (i >= points) {
					if (maxZB <= macd[i]) {
						maxZB = macd[i];
					}
					if (minZB >= macd[i]) {
						minZB = macd[i];
					}
				}

				if (i >= points - 1 && i < points + draw_points) {
					// if (i >= points) {
					if (maxZB <= d_m[i]) {
						maxZB = d_m[i];
					}
					if (minZB >= d_m[i]) {
						minZB = d_m[i];
					}
				}
			}

		}
		// VOL
		if (Const.INDEX_VOL.equals(index)) {
			maxZB = -1000000;
			float total[] = volData[3];
			for (int i = 0; i < ls.size(); i++) {

				if (i >= points && i < points + draw_points) {
					if (maxZB < total[i]) {
						maxZB = total[i];
					}
				}

			}
		}
		// KDJ
		if (Const.INDEX_KDJ.equals(index)) {
			maxZB = -1000000;
			minZB = 1000000;
			int n1 = kdjParam[0];
			float kvalue[] = kdjData[0];
			float dvalue[] = kdjData[1];
			float jvalue[] = kdjData[2];

			for (int i = n1; i < ls.size(); i++) {
				if (i >= points && i < points + draw_points) {
					if (maxZB < kvalue[i]) {
						maxZB = kvalue[i];
					}
					if (maxZB < dvalue[i]) {
						maxZB = dvalue[i];
					}
					if (maxZB < jvalue[i]) {
						maxZB = jvalue[i];
					}
					if (minZB > kvalue[i]) {
						minZB = kvalue[i];
					}
					if (minZB > dvalue[i]) {
						minZB = dvalue[i];
					}
					if (minZB > jvalue[i]) {
						minZB = jvalue[i];
					}
				}
			}
		}
		// RSI
		if (Const.INDEX_RSI.equals(index)) {
			maxZB = 0;
			minZB = 10000;
			for (int i = 1; i < ls.size(); i++) {
				for (int k = 0; k < 3; k++) {
					if (i >= points && i < points + draw_points) {
						if (maxZB < rsiData[k][i]) {
							maxZB = rsiData[k][i];
						}
						if (minZB > rsiData[k][i]) {
							minZB = rsiData[k][i];
						}
					}
				}
			}
		}
		// OBV
		if (Const.INDEX_OBV.equals(index)) {
			maxZB = -10000;
			minZB = 10000;
			float obv[] = obvData[0];
			for (int i = 1; i < ls.size(); i++) {
				if (i >= points && i < points + draw_points) {
					if (maxZB < obv[i]) {
						maxZB = obv[i];
					}
					if (minZB > obv[i]) {
						minZB = obv[i];
					}
				}
			}
		}
		// CCI
		if (Const.INDEX_CCI.equals(index)) {
			maxZB = -10000;
			minZB = 10000;
			float cci[] = cciData[0];
			for (int i = cciParam[0] - 1; i < ls.size(); i++) {

				if (i >= points && i < points + draw_points) {
					if (maxZB < cci[i]) {
						maxZB = cci[i];
					}
					if (minZB > cci[i]) {
						minZB = cci[i];
					}
				}
			}

		}
		// PSY
		if (Const.INDEX_PSY.equals(index)) {
			maxZB = -10000;
			minZB = 10000;
			float psy[] = psyData[0];
			for (int i = psyParam[0]; i < ls.size(); i++) {

				if (i >= points && i < points + draw_points) {
					if (maxZB < psy[i]) {
						maxZB = psy[i];
					}
					if (minZB > psy[i]) {
						minZB = psy[i];
					}
				}
			}

		}

		Message msg = handler.obtainMessage();
		handler.dispatchMessage(msg);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		// 创建surfaceView时启动线程
		isThreadRunning = true;
		new Thread(this).start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// 当surfaceView销毁时, 停止线程的运行. 避免surfaceView销毁了线程还在运行而报错.
		isThreadRunning = false;
		// 第三种方法防止退出时异常. 当surfaceView销毁时让线程暂停300ms .
		// 醒来再执行run()方法时,isThreadRunning就是false了.
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private float bottomWhite;// 底部留白
	/** 字体大小 */
	int textSize;
	/** 字体周边留白 */
	int textSizeWhite;
	/** 字体周边留白-底边留白 */
	int textSizeWhite_bottom;
	private String decimal;

	private DecimalFormat df_base;

	private int d2px_30;
	private int d2px_110;
	private int d2px_190;
	private int d2px_50;
	private int d2px_120;
	private int d2px_200;

	public KLineDraw_SurfaceView2(Context context, String decimal) {
		super(context);
		this.context = context;
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

		mPaint = new Paint();

		topWhite = DensityUtil.dip2px(context, 16);
		bottomWhite = DensityUtil.dip2px(context, 4);
		midHeight = DensityUtil.dip2px(context, 16);
		leftWhite = DensityUtil.dip2px(context, 50);
		rightWhite = DensityUtil.dip2px(context, 8);

		d2px_30 = DensityUtil.dip2px(context, 30);
		d2px_110 = DensityUtil.dip2px(context, 110);
		d2px_190 = DensityUtil.dip2px(context, 190);
		d2px_50 = DensityUtil.dip2px(context, 50);
		d2px_120 = DensityUtil.dip2px(context, 120);
		d2px_200 = DensityUtil.dip2px(context, 200);

		textSize = DensityUtil.dip2px(context, 12);
		textSizeWhite = DensityUtil.dip2px(context, 2);
		textSizeWhite_bottom = DensityUtil.dip2px(context, 2);

		rect_white = DensityUtil.dip2px(context, 2);
		rect_width = DensityUtil.dip2px(context, 72);

		mPaint_bg = new Paint();
		mPaint_bg.setStyle(Style.FILL);
		mPaint_bg.setStrokeWidth(1.0f);
		mPaint_bg.setColor(temp_write);
		mPaint_bg.setAntiAlias(true);

		// 画实线坐标
		mPaint_text = new Paint();
		mPaint_text.setAntiAlias(true);
		mPaint_text.setColor(temp_write);
		mPaint_text.setStyle(Style.FILL);
		mPaint_text.setPathEffect(null);
		mPaint_text.setStrokeWidth(1.0f);
		mPaint_text.setTextSize(textSize);
		mPaint_text.setTextAlign(Paint.Align.RIGHT);

		mPaint_move = new Paint();
		mPaint_move.setStyle(Style.FILL);
		mPaint_move.setColor(Color.BLACK);
		mPaint_move.setStrokeWidth(1.0f);
		mPaint_move.setTextSize(DensityUtil.dip2px(context, 12));
		mPaint_move.setTextAlign(Paint.Align.LEFT);

		// 计算文字高度
		FontMetrics fontMetrics = mPaint_move.getFontMetrics();
		float fontHeight_move = fontMetrics.bottom - fontMetrics.top;
		/** 字的半高 */
		offY_move = fontHeight_move / 2 - fontMetrics.bottom;
		/** 左边字x起始点 */
		x_a_move = leftWhite - textSizeWhite;

		mPaint_move_bg = new Paint();
		mPaint_move_bg.setStyle(Style.STROKE);
		mPaint_move_bg.setColor(Color.RED);
		mPaint_move_bg.setColor(trans_gray);// 设置灰色
		mPaint_move_bg.setStyle(Paint.Style.FILL);// 设置填满

		effects2 = new DashPathEffect(new float[] { 6, 8, 6, 8 }, 1);
	}

	/** 背景线框 */
	private Paint mPaint_bg;
	private Paint mPaint_text;
	private Paint mPaint_move;
	private float x_a_move;
	private float offY_move;
	private Paint mPaint_move_bg;
	private PathEffect effects2;

	/**
	 * 将绘图的方法单独写到这个方法里面.
	 */
	private synchronized void drawVieW2() {
		try {// 第一种方法防止退出时异常: 当isThreadRunning为false时, 最后还是会执行一次drawView方法,
				// 但此时surfaceView已经销毁
				// 因此才来判断surfaceHolder
			if (surfaceHolder != null) {
				// 1. 在surface创建后锁定画布
				// 2. 可以在画布上进行任意的绘画操作( 下面是画一条红色 的线 )
				canvas = surfaceHolder.lockCanvas();
				canvas.drawColor(Color.BLACK);

				height = canvas.getHeight();
				width = canvas.getWidth();

				maxH = height - bottomWhite;
				kHeight = (maxH - topWhite) * 0.7f;

				tradeHeight = maxH - kHeight - topWhite - midHeight;
				tableWidth = width - rightWhite - leftWhite;

				// 底边
				canvas.drawLine(leftWhite, maxH, width - rightWhite, maxH, mPaint_bg);
				// 顶边
				canvas.drawLine(leftWhite, topWhite, width - rightWhite, topWhite, mPaint_bg);

				// 左侧- 上端
				canvas.drawLine(leftWhite, topWhite, leftWhite, topWhite + kHeight, mPaint_bg);
				// 左侧- 下端
				canvas.drawLine(leftWhite, maxH - tradeHeight, leftWhite, maxH, mPaint_bg);

				// 右侧 - 上端
				canvas.drawLine(width - rightWhite, topWhite, width - rightWhite, topWhite + kHeight, mPaint_bg);
				// 右侧 - 下端
				canvas.drawLine(width - rightWhite, maxH - tradeHeight, width - rightWhite, maxH, mPaint_bg);

				// 分界横线-上面
				canvas.drawLine(leftWhite, topWhite + kHeight, width - rightWhite, topWhite + kHeight, mPaint_bg);
				// 分界横线-下面
				canvas.drawLine(leftWhite, topWhite + kHeight + midHeight, width - rightWhite, topWhite + kHeight + midHeight, mPaint_bg);

				// K线 中值水平线
				mPaint.setPathEffect(effects2);
				mPaint.setColor(temp_write2);
				for (int i = 1; i < 4; i++) {
					canvas.drawLine(leftWhite, topWhite + kHeight * i / 4, width - rightWhite, topWhite + kHeight * i / 4, mPaint);
				}

				// 交易量中值横线
				canvas.drawLine(leftWhite, topWhite + kHeight + midHeight + tradeHeight / 2, width - rightWhite, topWhite + kHeight + midHeight + tradeHeight / 2, mPaint);

				String price01 = "0.00", price02 = "0.00", price03 = "0.00", price04 = "0.00", price05 = "0.00";

				// 计算文字高度
				FontMetrics fontMetrics = mPaint_text.getFontMetrics();
				float fontHeight = fontMetrics.bottom - fontMetrics.top;
				/** 字的半高 */
				float offY = fontHeight / 2 - fontMetrics.bottom;
				/** 左边字x起始点 */
				float x_a = leftWhite - textSizeWhite;

				if (initFlag && ls.size() > points + 1) {
					// if (false) {
					try {

						price01 = String.valueOf(df_base.format(maxPrice));
						price02 = String.valueOf(df_base.format(maxPrice - (maxPrice - minPrice) / 4));
						price03 = String.valueOf(df_base.format((maxPrice + minPrice) / 2));
						price04 = String.valueOf(df_base.format(minPrice + (maxPrice - minPrice) / 4));
						price05 = String.valueOf(df_base.format(minPrice));

						if (macdData != null) {
							macdDataDraw_all = new float[3][ls.size()];
						}
						if (volData != null) {
							volDataDraw_all = new float[4][ls.size()];
						}
						if (kdjData != null) {
							kdjDataDraw_all = new float[3][ls.size()];
						}
						if (rsiData != null) {
							rsiDataDraw_all = new float[3][ls.size()];
						}
						if (bollData != null) {
							bollDataDraw_all = new float[3][ls.size()];
						}
						if (obvData != null) {
							obvDataDraw_all = new float[2][ls.size()];
						}
						if (cciData != null) {
							cciDataDraw_all = new float[2][ls.size()];
						}
						if (psyData != null) {
							psyDataDraw_all = new float[2][ls.size()];
						}

						if (lsDraw_all != null) {
							lsDraw_all.clear();
						}
						if (maSet1listDraw_all != null) {
							maSet1listDraw_all.clear();
						}
						if (maSet2listDraw_all != null) {
							maSet2listDraw_all.clear();
						}
						if (maSet3listDraw_all != null) {
							maSet3listDraw_all.clear();
						}

						for (int i = 0; i < ls.size(); i++) {
							lsDraw_all.add(ls.get(i));
							maSet1listDraw_all.add(maSet1list.get(i));
							maSet2listDraw_all.add(maSet2list.get(i));
							maSet3listDraw_all.add(maSet3list.get(i));
						}

						// if (lsDraw_all != null && lsDraw_all.size() > 0) {
						// // 完整数据如果已经全部加载过 那么不需要再初始化这些数据了
						//
						// if (lsDraw_all.size() != ls.size()) {
						//
						// lsDraw_all.clear();
						// log.i("回补");
						// for (int i = 0; i < ls.size(); i++) {
						// lsDraw_all.add(ls.get(i));
						// maSet1listDraw_all.add(maSet1list.get(i));
						// maSet2listDraw_all.add(maSet2list.get(i));
						// maSet3listDraw_all.add(maSet3list.get(i));
						// }
						// }
						// } else {
						// log.i("完整数据初始化");
						// for (int i = 0; i < ls.size(); i++) {
						// lsDraw_all.add(ls.get(i));
						// maSet1listDraw_all.add(maSet1list.get(i));
						// maSet2listDraw_all.add(maSet2list.get(i));
						// maSet3listDraw_all.add(maSet3list.get(i));
						// }
						// }

						for (int i = 0; i < ls.size(); i++) {

							if (macdData != null) {
								// log.i("空指针->" + i);
								macdDataDraw_all[0][i] = macdData[0][i];
								macdDataDraw_all[1][i] = macdData[1][i];
								macdDataDraw_all[2][i] = macdData[2][i];
							}
							if (volData != null) {
								volDataDraw_all[0][i] = volData[0][i];
								volDataDraw_all[1][i] = volData[1][i];
								volDataDraw_all[2][i] = volData[2][i];
								volDataDraw_all[3][i] = volData[3][i];
							}
							if (kdjData != null) {
								kdjDataDraw_all[0][i] = kdjData[0][i];
								kdjDataDraw_all[1][i] = kdjData[1][i];
								kdjDataDraw_all[2][i] = kdjData[2][i];
							}
							if (rsiData != null) {
								rsiDataDraw_all[0][i] = rsiData[0][i];
								rsiDataDraw_all[1][i] = rsiData[1][i];
								rsiDataDraw_all[2][i] = rsiData[2][i];
							}
							if (bollData != null) {
								bollDataDraw_all[0][i] = bollData[0][i];
								bollDataDraw_all[1][i] = bollData[1][i];
								bollDataDraw_all[2][i] = bollData[2][i];
							}
							if (obvData != null) {
								obvDataDraw_all[0][i] = obvData[0][i];
								obvDataDraw_all[1][i] = obvData[1][i];
							}
							if (cciData != null) {
								cciDataDraw_all[0][i] = cciData[0][i];
								cciDataDraw_all[1][i] = cciData[1][i];
							}
							if (psyData != null) {
								psyDataDraw_all[0][i] = psyData[0][i];
								psyDataDraw_all[1][i] = psyData[1][i];
							}
						}
						// log.i("原始" + Arrays.toString(macdDataDraw_all[0]));

						List<KData> lsDraw = new ArrayList<KData>();
						List<Float> maSet1listDraw = new ArrayList<Float>();
						List<Float> maSet2listDraw = new ArrayList<Float>();
						List<Float> maSet3listDraw = new ArrayList<Float>();
						float macdDataDraw[][] = null;
						float volDataDraw[][] = null;
						float kdjDataDraw[][] = null;
						float rsiDataDraw[][] = null;
						float bollDataDraw[][] = null;
						float obvDataDraw[][] = null;
						float cciDataDraw[][] = null;
						float psyDataDraw[][] = null;

						// int point = ls.size() - points;
						// log.i("画线条数->" + draw_points);

						if (macdData != null) {
							macdDataDraw = new float[3][draw_points];
							// log.i("points长度是" + points);
							// log.i("数组长度 =" + (point));
						}
						if (volData != null) {
							volDataDraw = new float[4][draw_points];
						}
						if (kdjData != null) {
							kdjDataDraw = new float[3][draw_points];
						}
						if (rsiData != null) {
							rsiDataDraw = new float[3][draw_points];
						}
						if (bollData != null) {
							bollDataDraw = new float[3][draw_points];
						}
						if (obvData != null) {
							obvDataDraw = new float[2][draw_points];
						}
						if (cciData != null) {
							cciDataDraw = new float[2][draw_points];
						}
						if (psyData != null) {
							psyDataDraw = new float[2][draw_points];
						}
						// 从135点开始画的
						// log.i("从points=" + (points - offset) + "开始,offset=" +
						// offset);

						// 右移 offset大于零
						// 起始点 = 起始点 - 偏移量
						points = points - offset;
						// log.i("偏移后的points=" + points);

						// 向右滑不动了
						if (points > ls.size() - draw_points) {
							points = ls.size() - draw_points;
							offset = 0;
						}

						// 起始点小于 可画的点数 向左滑不动了
						if (points < draw_points) {
							points = draw_points;
							offset = 0;
						}

						// 存疑
						if (offset < 0) {
							offset = 0;
						}

						// lsDraw = lsDraw_all.subList(points, ls.size() -
						// offset);
						// maSet1listDraw = maSet1listDraw_all.subList(points,
						// ls.size() - offset);
						// maSet2listDraw = maSet2listDraw_all.subList(points,
						// ls.size() - offset);
						// maSet3listDraw = maSet3listDraw_all.subList(points,
						// ls.size() - offset);
						// log.i("lsDraw_all->" + lsDraw_all.size() +
						// ",points->" + points + ",draw_points->" +
						// draw_points);
						lsDraw = lsDraw_all.subList(points, points + draw_points);
						maSet1listDraw = maSet1listDraw_all.subList(points, points + draw_points);
						maSet2listDraw = maSet2listDraw_all.subList(points, points + draw_points);
						maSet3listDraw = maSet3listDraw_all.subList(points, points + draw_points);

						// for (int i = points; i < ls.size(); i++) {
						// //lsDraw.add(ls.get(i));
						// //maSet1listDraw.add(maSet1list.get(i));
						// //maSet2listDraw.add(maSet2list.get(i));
						// //maSet3listDraw.add(maSet3list.get(i));
						// if (macdData != null && macdData[0].length > i) {
						// macdDataDraw[0][i - points] = macdData[0][i];
						// macdDataDraw[1][i - points] = macdData[1][i];
						// macdDataDraw[2][i - points] = macdData[2][i];
						// }
						// if (volData != null) {
						// volDataDraw[0][i - points] = volData[0][i];
						// volDataDraw[1][i - points] = volData[1][i];
						// volDataDraw[2][i - points] = volData[2][i];
						// volDataDraw[3][i - points] = volData[3][i];
						// }
						// if (kdjData != null) {
						// kdjDataDraw[0][i - points] = kdjData[0][i];
						// kdjDataDraw[1][i - points] = kdjData[1][i];
						// kdjDataDraw[2][i - points] = kdjData[2][i];
						// }
						// if (rsiData != null) {
						// rsiDataDraw[0][i - points] = rsiData[0][i];
						// rsiDataDraw[1][i - points] = rsiData[1][i];
						// rsiDataDraw[2][i - points] = rsiData[2][i];
						// }
						// if (bollData != null) {
						// bollDataDraw[0][i - points] = bollData[0][i];
						// bollDataDraw[1][i - points] = bollData[1][i];
						// bollDataDraw[2][i - points] = bollData[2][i];
						// }
						// if (obvData != null) {
						// obvDataDraw[0][i - points] = obvData[0][i];
						// obvDataDraw[1][i - points] = obvData[1][i];
						// }
						// if (cciData != null) {
						// cciDataDraw[0][i - points] = cciData[0][i];
						// cciDataDraw[1][i - points] = cciData[1][i];
						// }
						// if (psyData != null) {
						// psyDataDraw[0][i - points] = psyData[0][i];
						// psyDataDraw[1][i - points] = psyData[1][i];
						// }
						// }

						if (macdData != null && ls.size() >= points + draw_points) {
							// log.i("macdDataDraw_all截取");
							// log.i("原始" +
							// Arrays.toString(macdDataDraw_all[0]));
							// macdDataDraw[0][i - points] = macdData[0];
							// log.i("points" + points);
							System.arraycopy(macdDataDraw_all[0], points, macdDataDraw[0], 0, draw_points);
							System.arraycopy(macdDataDraw_all[1], points, macdDataDraw[1], 0, draw_points);
							System.arraycopy(macdDataDraw_all[2], points, macdDataDraw[2], 0, draw_points);
							// log.i("macdDataDraw[2]->" +
							// Arrays.toString(macdDataDraw[2]));
						}

						if (volData != null) {
							System.arraycopy(volDataDraw_all[0], points, volDataDraw[0], 0, draw_points);
							System.arraycopy(volDataDraw_all[1], points, volDataDraw[1], 0, draw_points);
							System.arraycopy(volDataDraw_all[2], points, volDataDraw[2], 0, draw_points);
							System.arraycopy(volDataDraw_all[3], points, volDataDraw[3], 0, draw_points);

						}
						if (kdjData != null) {

							System.arraycopy(kdjDataDraw_all[0], points, kdjDataDraw[0], 0, draw_points);
							System.arraycopy(kdjDataDraw_all[1], points, kdjDataDraw[1], 0, draw_points);
							System.arraycopy(kdjDataDraw_all[2], points, kdjDataDraw[2], 0, draw_points);
						}

						if (rsiData != null) {

							System.arraycopy(rsiDataDraw_all[0], points, rsiDataDraw[0], 0, draw_points);
							System.arraycopy(rsiDataDraw_all[1], points, rsiDataDraw[1], 0, draw_points);
							System.arraycopy(rsiDataDraw_all[2], points, rsiDataDraw[2], 0, draw_points);
						}
						if (bollData != null) {

							System.arraycopy(bollDataDraw_all[0], points, bollDataDraw[0], 0, draw_points);
							System.arraycopy(bollDataDraw_all[1], points, bollDataDraw[1], 0, draw_points);
							System.arraycopy(bollDataDraw_all[2], points, bollDataDraw[2], 0, draw_points);
						}
						if (obvData != null) {

							System.arraycopy(obvDataDraw_all[0], points, obvDataDraw[0], 0, draw_points);
							System.arraycopy(obvDataDraw_all[1], points, obvDataDraw[1], 0, draw_points);
						}
						if (cciData != null) {

							System.arraycopy(cciDataDraw_all[0], points, cciDataDraw[0], 0, draw_points);
							System.arraycopy(cciDataDraw_all[1], points, cciDataDraw[1], 0, draw_points);
						}
						if (psyData != null) {

							System.arraycopy(psyDataDraw_all[0], points, psyDataDraw[0], 0, draw_points);
							System.arraycopy(psyDataDraw_all[1], points, psyDataDraw[1], 0, draw_points);
						}

						// 决定刻度线的值
						// float qujian = 0l;
						// if ((maxPrice - lastClose) > (lastClose - minPrice))
						// {
						// qujian = (float) ((maxPrice - lastClose) * 1.5);
						// } else {
						// qujian = (float) ((lastClose - minPrice) * 1.5);
						// }
						// qujian = Math.round(qujian * 100) / 100;
						// if (qujian < 0) {
						// qujian = 0 - qujian;
						// }
						double rul = kHeight / (maxPrice - minPrice);
						// 指标参考值
						float zbrul = 0f;

						// 画实线坐标
						mPaint.setColor(Color.RED);
						mPaint.setAntiAlias(true);
						mPaint.setStyle(Style.FILL);
						mPaint.setPathEffect(null);
						mPaint.setStrokeWidth(1.0f);
						mPaint.setTextSize(textSize);
						mPaint.setTextAlign(Paint.Align.RIGHT);
						// 标记K线 图左侧 数值刻度
						mPaint.setColor(Color.RED);

						//
						// canvas.drawText(, x_a, topWhite + offY * 2, mPaint);
						// canvas.drawText(String.valueOf(dfforview.format(maxPrice
						// - (maxPrice - minPrice) / 4)), x_a, topWhite
						// + offY + kHeight * 1 / 4, mPaint);
						// mPaint.setColor(Color.WHITE);
						// canvas.drawText(String.valueOf(dfforview.format((maxPrice
						// + minPrice) / 2)), x_a, topWhite + offY
						// + kHeight * 2 / 4, mPaint);
						// mPaint.setColor(Color.GREEN);
						// canvas.drawText(String.valueOf(dfforview.format(minPrice
						// + (maxPrice - minPrice) / 4)), x_a, topWhite
						// + offY + kHeight * 3 / 4, mPaint);
						// canvas.drawText(String.valueOf(dfforview.format(minPrice)),
						// x_a, topWhite - textSizeWhite + kHeight,
						// mPaint);

						// moveDate(canvas, lsDraw);
						// canvas.drawText(stockName, leftWhite, topWhite - 3,
						// mPaint);
						// canvas.drawText(beginDay, leftWhite+1,
						// maxH-tradeHeight-2,
						// mPaint);
						// canvas.drawText(endDay, width-rightWhite-64,
						// maxH-tradeHeight-2,
						// mPaint);
						if (indexMA) {
							// MA数值
							mPaint.setTextAlign(Paint.Align.LEFT);
							mPaint.setColor(Color.YELLOW);
							canvas.drawText("MA", leftWhite, topWhite - textSizeWhite_bottom, mPaint);
							mPaint.setColor(Color.WHITE);
							canvas.drawText("5=" + String.valueOf(df_base.format(maSet1list.get(maSet1list.size() - 1))), leftWhite + d2px_30, topWhite - textSizeWhite_bottom, mPaint);
							mPaint.setColor(Color.YELLOW);
							canvas.drawText("10=" + String.valueOf(df_base.format(maSet2list.get(maSet2list.size() - 1))), leftWhite + d2px_110, topWhite - textSizeWhite_bottom, mPaint);
							mPaint.setColor(Const.MA_ZISE);
							canvas.drawText("20=" + String.valueOf(df_base.format(maSet3list.get(maSet3list.size() - 1))), leftWhite + d2px_190, topWhite - textSizeWhite_bottom, mPaint);

						} else {
							// BOLL数值
							mPaint.setTextAlign(Paint.Align.LEFT);
							mPaint.setColor(Color.YELLOW);
							canvas.drawText("BOLL", leftWhite, topWhite - 3, mPaint);
							mPaint.setColor(Color.WHITE);
							canvas.drawText("MID=" + String.valueOf(util.formatFloat00((bollDataDraw_all[0][lsDraw.size() - 1]))), leftWhite + d2px_30, topWhite - textSizeWhite_bottom, mPaint);
							mPaint.setColor(Color.YELLOW);
							canvas.drawText("UPPER=" + String.valueOf(util.formatFloat00((bollDataDraw_all[1][lsDraw.size() - 1]))), leftWhite + d2px_110, topWhite - textSizeWhite_bottom, mPaint);
							mPaint.setColor(Const.MA_ZISE);
							canvas.drawText("LOWER=" + String.valueOf(util.formatFloat00((bollDataDraw_all[2][lsDraw.size() - 1]))), leftWhite + d2px_190, topWhite - textSizeWhite_bottom, mPaint);
						}
						int l = lsDraw.size();
						// log.i("l -> " + lsDraw.size());

						float lowMacd = 0;

						float tradeText_base = maxH - tradeHeight - textSizeWhite;

						// 画MACD指标
						if (Const.INDEX_MACD.equals(index)) {
							// log.i("maxZB->" + maxZB + ",minZB->" + minZB +
							// ",tradeHeight->" + tradeHeight);
							zbrul = tradeHeight / (maxZB - minZB);

							if (Math.abs(maxZB) > Math.abs(minZB)) {
								zbrul = tradeHeight / (maxZB * 2);
								if (maxZB > 0) {
									lowMacd = 0 - maxZB;
								} else {
									lowMacd = maxZB;
								}
							} else {
								zbrul = tradeHeight / (minZB * 2);
								if (minZB > 0) {
									lowMacd = 0 - minZB;
								} else {
									lowMacd = minZB;
								}
							}
							zbrul = Math.abs(zbrul);
							// log.i("tradeHeight->" + tradeHeight + ",zbrul->"
							// + zbrul);

							// macd 指标
							mPaint.setColor(Color.YELLOW);
							mPaint.setTextAlign(Paint.Align.RIGHT);
							// canvas.drawText(String.valueOf(util.formatFloat00(maxZB)),
							// leftWhite - textSizeWhite,
							// maxH
							// - tradeHeight + offY * 2, mPaint);
							// canvas.drawText(String.valueOf(util.formatFloat00(minZB)),
							// leftWhite - textSizeWhite,
							// maxH
							// - textSizeWhite, mPaint);
							canvas.drawText(df_base.format(maxZB), leftWhite - textSizeWhite, maxH - tradeHeight + offY * 2, mPaint);
							canvas.drawText(df_base.format(minZB), leftWhite - textSizeWhite, maxH - textSizeWhite, mPaint);

							mPaint.setTextAlign(Paint.Align.LEFT);
							canvas.drawText("MACD", leftWhite, tradeText_base, mPaint);
							mPaint.setColor(Color.YELLOW);

							canvas.drawText("DIF=" + String.valueOf(util.formatFloat00((macdDataDraw_all[0][lsDraw.size() - 1]))), leftWhite + d2px_50, tradeText_base, mPaint);
							mPaint.setColor(Const.MA_ZISE);
							canvas.drawText("DEA=" + String.valueOf(util.formatFloat00(macdDataDraw_all[1][lsDraw.size() - 1])), leftWhite + d2px_120, tradeText_base, mPaint);
							mPaint.setColor(Color.GREEN);
							canvas.drawText("MACD=" + String.valueOf(util.formatFloat00(macdDataDraw_all[2][lsDraw.size() - 1])), leftWhite + d2px_200, tradeText_base, mPaint);

						}
						// 画VOL指标
						if (Const.INDEX_VOL.equals(index)) {
							zbrul = tradeHeight / maxZB;
							// vol刻度
							mPaint.setColor(Color.YELLOW);
							mPaint.setTextAlign(Paint.Align.RIGHT);
							// 左边两个交易量标
							canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite - textSizeWhite, maxH - tradeHeight + offY * 2, mPaint);
							canvas.drawText("0", leftWhite - textSizeWhite, maxH - textSizeWhite, mPaint);
							mPaint.setTextAlign(Paint.Align.LEFT);
							// 中间标
							canvas.drawText("VOL", leftWhite, tradeText_base, mPaint);
							mPaint.setColor(Color.WHITE);
							canvas.drawText("MA1=" + String.valueOf(util.formatFloat00((volDataDraw_all[0][lsDraw.size() - 1]))), leftWhite + d2px_30, tradeText_base, mPaint);
							mPaint.setColor(Color.YELLOW);
							canvas.drawText("MA2=" + String.valueOf(util.formatFloat00(volDataDraw_all[1][lsDraw.size() - 1])), leftWhite + d2px_110, tradeText_base, mPaint);
							mPaint.setColor(Const.MA_ZISE);
							canvas.drawText("MA3=" + String.valueOf(util.formatFloat00(volDataDraw_all[2][lsDraw.size() - 1])), leftWhite + d2px_190, tradeText_base, mPaint);

						}
						// 画KDJ指标
						if (Const.INDEX_KDJ.equals(index)) {
							zbrul = tradeHeight / (maxZB - minZB);
							zbrul = Math.abs(zbrul);
							// DKJ刻度
							mPaint.setColor(Color.YELLOW);
							mPaint.setTextAlign(Paint.Align.RIGHT);
							canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite - textSizeWhite, maxH - tradeHeight + offY * 2, mPaint);
							canvas.drawText(String.valueOf(util.formatFloat00(minZB)), leftWhite - textSizeWhite, maxH - textSizeWhite, mPaint);

							mPaint.setTextAlign(Paint.Align.LEFT);
							canvas.drawText("KDJ", leftWhite, tradeText_base, mPaint);
							mPaint.setColor(Color.WHITE);
							canvas.drawText("K=" + String.valueOf(util.formatFloat00((kdjDataDraw_all[0][lsDraw.size() - 1]))), leftWhite + d2px_50, tradeText_base, mPaint);
							mPaint.setColor(Color.YELLOW);
							canvas.drawText("D=" + String.valueOf(util.formatFloat00(kdjDataDraw_all[1][lsDraw.size() - 1])), leftWhite + d2px_120, tradeText_base, mPaint);
							mPaint.setColor(Const.MA_ZISE);
							canvas.drawText("J=" + String.valueOf(util.formatFloat00(kdjDataDraw_all[2][lsDraw.size() - 1])), leftWhite + d2px_200, tradeText_base, mPaint);
						}

						// 画RSI指标
						if (Const.INDEX_RSI.equals(index)) {
							zbrul = tradeHeight / (maxZB - minZB);
							zbrul = Math.abs(zbrul);
							// RSI刻度
							mPaint.setColor(Color.YELLOW);
							mPaint.setTextAlign(Paint.Align.RIGHT);
							canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite - textSizeWhite, maxH - tradeHeight + offY * 2, mPaint);
							canvas.drawText(String.valueOf(util.formatFloat00(minZB)), leftWhite - textSizeWhite, maxH - textSizeWhite, mPaint);

							mPaint.setTextAlign(Paint.Align.LEFT);
							canvas.drawText("RSI", leftWhite, tradeText_base, mPaint);
							mPaint.setColor(Color.WHITE);
							canvas.drawText("RSI1=" + String.valueOf(util.formatFloat00((rsiDataDraw_all[0][lsDraw.size() - 1]))), leftWhite + d2px_50, tradeText_base, mPaint);
							mPaint.setColor(Color.YELLOW);
							canvas.drawText("RSI2=" + String.valueOf(util.formatFloat00(rsiDataDraw_all[1][lsDraw.size() - 1])), leftWhite + d2px_120, tradeText_base, mPaint);
							mPaint.setColor(Const.MA_ZISE);
							canvas.drawText("RSI3=" + String.valueOf(util.formatFloat00(rsiDataDraw_all[2][lsDraw.size() - 1])), leftWhite + d2px_200, tradeText_base, mPaint);
						}

						// 画OBV指标
						if (Const.INDEX_OBV.equals(index)) {
							zbrul = tradeHeight / (maxZB - minZB);
							zbrul = Math.abs(zbrul);
							// OBV刻度
							mPaint.setColor(Color.YELLOW);
							mPaint.setTextAlign(Paint.Align.RIGHT);
							canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite - textSizeWhite, maxH - tradeHeight + offY * 2, mPaint);
							canvas.drawText(String.valueOf(util.formatFloat00(minZB)), leftWhite - textSizeWhite, maxH - textSizeWhite, mPaint);
							mPaint.setTextAlign(Paint.Align.LEFT);
							canvas.drawText("OBV", leftWhite, tradeText_base, mPaint);
							canvas.drawText(String.valueOf(util.formatFloat00((obvDataDraw_all[0][lsDraw.size() - 1]))), leftWhite + d2px_50, tradeText_base, mPaint);
						}

						// 画CCI指标
						if (Const.INDEX_CCI.equals(index)) {
							zbrul = tradeHeight / (maxZB - minZB);
							zbrul = Math.abs(zbrul);
							// CCI刻度
							mPaint.setColor(Color.YELLOW);
							mPaint.setTextAlign(Paint.Align.RIGHT);
							canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite - textSizeWhite, maxH - tradeHeight + offY * 2, mPaint);
							canvas.drawText(String.valueOf(util.formatFloat00(minZB)), leftWhite - textSizeWhite, maxH - textSizeWhite, mPaint);
							mPaint.setTextAlign(Paint.Align.LEFT);
							canvas.drawText("CCI", leftWhite, tradeText_base, mPaint);
							canvas.drawText(String.valueOf(util.formatFloat00((cciDataDraw_all[0][lsDraw.size() - 1]))), leftWhite + d2px_50, tradeText_base, mPaint);
						}
						// 画PSY指标
						if (Const.INDEX_PSY.equals(index)) {
							zbrul = tradeHeight / (maxZB - minZB);
							zbrul = Math.abs(zbrul);
							// CCI刻度
							mPaint.setColor(Color.YELLOW);
							mPaint.setTextAlign(Paint.Align.RIGHT);
							canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite - textSizeWhite, maxH - tradeHeight + offY * 2, mPaint);
							canvas.drawText(String.valueOf(util.formatFloat00(minZB)), leftWhite - textSizeWhite, maxH - textSizeWhite, mPaint);
							mPaint.setTextAlign(Paint.Align.LEFT);
							canvas.drawText("PSY", leftWhite, maxH - tradeHeight - 5, mPaint);
							canvas.drawText(String.valueOf(util.formatFloat00((psyDataDraw_all[0][lsDraw.size() - 1]))), leftWhite + d2px_50, tradeText_base, mPaint);
						}

						// 画线
						for (int i = 0; i < l; i++) {
							KData s = lsDraw.get(i);
							float open = util.getFloat(s.getK_open());
							float close = util.getFloat(s.getK_close());
							if (open - close < 0) {
								mPaint.setColor(Color.RED);
								mPaint.setStrokeWidth(2);
							} else {
								// mPaint.setColor(Const.K_DOWN);
								mPaint.setColor(Color.GREEN);
								mPaint.setStrokeWidth(1);
							}

							canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), (float) (kHeight + topWhite - (util.getFloat(s.getK_high()) - minPrice) * rul), (float) (interval * i
									+ interval / 2 + leftWhite), (float) (kHeight + topWhite - (util.getFloat(s.getK_low()) - minPrice) * rul), mPaint);
							mPaint.setStrokeWidth(interval - 2);
							canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), (float) (kHeight + topWhite - (util.getFloat(s.getK_open()) - minPrice) * rul), (float) (interval * i
									+ interval / 2 + leftWhite), (float) (kHeight + topWhite - (util.getFloat(s.getK_close()) - minPrice) * rul), mPaint);

							// 画MACD指标
							if (Const.INDEX_MACD.equals(index) && i < draw_points) {
								// 画macd
								// log.i("macdDataDraw.length->" +
								// macdDataDraw[2].length);
								mPaint.setStrokeWidth(4);
								if (macdDataDraw[2][i] < 0) {
									mPaint.setColor(Color.GREEN);
								} else {
									mPaint.setColor(Color.RED);
								}

								if (macdDataDraw[2][i] < 0) { // 负半轴的
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), maxH - tradeHeight / 2.0f, (float) (interval * i + interval / 2 + leftWhite), maxH - tradeHeight
											/ 2.0f - tradeHeight / 2 * ((macdDataDraw[2][i]) / Math.abs(minZB)), mPaint);
								} else {// 正半轴的 maxZB
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), maxH - tradeHeight / 2.0f, (float) (interval * i + interval / 2 + leftWhite), maxH - tradeHeight
											/ 2.0f - tradeHeight / 2 * ((macdDataDraw[2][i]) / Math.abs(maxZB)), mPaint);
								}

								// log.i("macdDataDraw[2][i]->" +
								// macdDataDraw[2][i] + ",zbrul->" + zbrul);
								// canvas.drawLine((float) (interval * i +
								// interval / 2 + leftWhite), maxH - tradeHeight
								// / 2.0f,
								// (float) (interval * i + interval / 2 +
								// leftWhite), maxH - tradeHeight / 2.0f - 2
								// * (macdDataDraw[2][i]) * zbrul, mPaint);

								// 这个数据是正常的
								mPaint.setStrokeWidth(interval - 2);
								mPaint.setColor(Color.WHITE);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), (float) ((maxH - tradeHeight / 2) - 0.5), (float) (interval * i + interval / 2 + leftWhite),
										(float) ((maxH - tradeHeight / 2) + 0.5), mPaint);

								//
								mPaint.setStrokeWidth(1);
								if (i > 0) {
									mPaint.setColor(Color.YELLOW);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (macdDataDraw[0][i - 1] - lowMacd) * zbrul), (float) (interval * i
											+ interval / 2 + leftWhite), (float) (maxH - (macdDataDraw[0][i] - lowMacd) * zbrul), mPaint);
									mPaint.setColor(Const.MA_ZISE);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (macdDataDraw[1][i - 1] - lowMacd) * zbrul), (float) (interval * i
											+ interval / 2 + leftWhite), (float) (maxH - (macdDataDraw[1][i] - lowMacd) * zbrul), mPaint);

								}

							}
							// 画VOL指标
							if (Const.INDEX_VOL.equals(index) && i < draw_points) {
								// 画vol
								if (open - close < 0) {
									mPaint.setColor(Color.RED);
								} else {
									mPaint.setColor(Const.K_DOWN);
								}
								mPaint.setStrokeWidth(interval - 2);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), maxH, (float) (interval * i + interval / 2 + leftWhite), maxH - volDataDraw[3][i] * zbrul, mPaint);
								if (open - close < 0) {
									mPaint.setStrokeWidth(interval - 4);
									mPaint.setColor(Color.BLACK);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), (float) maxH - 1, (float) (interval * i + interval / 2 + leftWhite), (float) maxH
											- volDataDraw[3][i] * zbrul + 1, mPaint);
								}
								mPaint.setStrokeWidth(1);
								if (i > 0) {
									mPaint.setColor(Color.WHITE);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (volDataDraw[0][i - 1]) * zbrul), (float) (interval * i + interval
											/ 2 + leftWhite), (float) (maxH - (volDataDraw[0][i]) * zbrul), mPaint);
									mPaint.setColor(Color.YELLOW);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (volDataDraw[1][i - 1]) * zbrul), (float) (interval * i + interval
											/ 2 + leftWhite), (float) (maxH - (volDataDraw[1][i]) * zbrul), mPaint);
									mPaint.setColor(Const.MA_ZISE);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (volDataDraw[2][i - 1]) * zbrul), (float) (interval * i + interval
											/ 2 + leftWhite), (float) (maxH - (volDataDraw[2][i]) * zbrul), mPaint);
								}

							}
							// 画KDJ指标
							if (Const.INDEX_KDJ.equals(index) && i < draw_points) {
								mPaint.setStrokeWidth(1);
								if (i > 0) {
									mPaint.setColor(Color.WHITE);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (kdjDataDraw[0][i - 1] - minZB) * zbrul), (float) (interval * i
											+ interval / 2 + leftWhite), (float) (maxH - (kdjDataDraw[0][i] - minZB) * zbrul), mPaint);
									mPaint.setColor(Color.YELLOW);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (kdjDataDraw[1][i - 1] - minZB) * zbrul), (float) (interval * i
											+ interval / 2 + leftWhite), (float) (maxH - (kdjDataDraw[1][i] - minZB) * zbrul), mPaint);
									mPaint.setColor(Const.MA_ZISE);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (kdjDataDraw[2][i - 1] - minZB) * zbrul), (float) (interval * i
											+ interval / 2 + leftWhite), (float) (maxH - (kdjDataDraw[2][i] - minZB) * zbrul), mPaint);
								}
							}
							// 画RSI指标
							if (Const.INDEX_RSI.equals(index) && i < draw_points) {
								mPaint.setStrokeWidth(1);
								if (i > 0) {
									mPaint.setColor(Color.WHITE);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (rsiDataDraw[0][i - 1] - minZB) * zbrul), (float) (interval * i
											+ interval / 2 + leftWhite), (float) (maxH - (rsiDataDraw[0][i] - minZB) * zbrul), mPaint);
									mPaint.setColor(Color.YELLOW);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (rsiDataDraw[1][i - 1] - minZB) * zbrul), (float) (interval * i
											+ interval / 2 + leftWhite), (float) (maxH - (rsiDataDraw[1][i] - minZB) * zbrul), mPaint);
									mPaint.setColor(Const.MA_ZISE);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (rsiDataDraw[2][i - 1] - minZB) * zbrul), (float) (interval * i
											+ interval / 2 + leftWhite), (float) (maxH - (rsiDataDraw[2][i] - minZB) * zbrul), mPaint);
								}
							}
							if (Const.INDEX_OBV.equals(index) && i < draw_points) {
								mPaint.setStrokeWidth(1);
								if (i > 0) {
									mPaint.setColor(Color.YELLOW);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (obvDataDraw[0][i - 1] - minZB) * zbrul), (float) (interval * i
											+ interval / 2 + leftWhite), (float) (maxH - (obvDataDraw[0][i] - minZB) * zbrul), mPaint);

								}
							}
							if (Const.INDEX_CCI.equals(index) && i < draw_points) {
								mPaint.setStrokeWidth(1);
								if (i > 0) {
									mPaint.setColor(Color.YELLOW);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (cciDataDraw[0][i - 1] - minZB) * zbrul), (float) (interval * i
											+ interval / 2 + leftWhite), (float) (maxH - (cciDataDraw[0][i] - minZB) * zbrul), mPaint);

								}
							}
							if (Const.INDEX_PSY.equals(index) && i < draw_points) {
								mPaint.setStrokeWidth(1);
								if (i > 0) {
									mPaint.setColor(Color.YELLOW);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (maxH - (psyDataDraw[0][i - 1] - minZB) * zbrul), (float) (interval * i
											+ interval / 2 + leftWhite), (float) (maxH - (psyDataDraw[0][i] - minZB) * zbrul), mPaint);

								}
							}
							if (open - close < 0) {
								mPaint.setStrokeWidth(interval - 4);
								mPaint.setColor(Color.BLACK);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), (float) (kHeight + topWhite - (util.getFloat(s.getK_open()) - minPrice) * rul) - 1,
										(float) (interval * i + interval / 2 + leftWhite), (float) (kHeight + topWhite - (util.getFloat(s.getK_close()) - minPrice) * rul) + 1, mPaint);

								// 暂时不美化 // 交易量
								// canvas.drawLine(
								// (float) (interval * i + interval / 2 +
								// leftWhite),
								// maxH - 1,
								// (float) (interval * i + interval / 2 +
								// leftWhite),
								// maxH -
								// util.getFloat(lsDraw.get(i).getK_total())
								// * trul
								// + 1, mPaint);
							}

							if (i > 0) {
								if (indexMA) {
									if (maSet1list.size() > 20 && maSet2list.size() > 20 && maSet3list.size() > 20) {
										mPaint.setStrokeWidth(1);
										// ma5
										mPaint.setStyle(Paint.Style.FILL);
										mPaint.setColor(Color.WHITE);
										canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (kHeight + topWhite - (maSet1listDraw.get(i - 1) - minPrice) * rul),
												(float) (interval * i + interval / 2 + leftWhite), (float) (kHeight + topWhite - (maSet1listDraw.get(i) - minPrice) * rul), mPaint);
										mPaint.setColor(Color.YELLOW);
										canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (kHeight + topWhite - (maSet2listDraw.get(i - 1) - minPrice) * rul),
												(float) (interval * i + interval / 2 + leftWhite), (float) (kHeight + topWhite - (maSet2listDraw.get(i) - minPrice) * rul), mPaint);
										mPaint.setColor(Const.MA_ZISE);
										canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (kHeight + topWhite - (maSet3listDraw.get(i - 1) - minPrice) * rul),
												(float) (interval * i + interval / 2 + leftWhite), (float) (kHeight + topWhite - (maSet3listDraw.get(i) - minPrice) * rul), mPaint);
									}
								} else {

									mPaint.setStrokeWidth(1);
									// ma5
									mPaint.setStyle(Paint.Style.FILL);
									mPaint.setColor(Color.WHITE);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (kHeight + topWhite - (bollDataDraw[0][i - 1] - minPrice) * rul),
											(float) (interval * i + interval / 2 + leftWhite), (float) (kHeight + topWhite - (bollDataDraw[0][i] - minPrice) * rul), mPaint);
									mPaint.setColor(Color.YELLOW);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (kHeight + topWhite - (bollDataDraw[1][i - 1] - minPrice) * rul),
											(float) (interval * i + interval / 2 + leftWhite), (float) (kHeight + topWhite - (bollDataDraw[1][i] - minPrice) * rul), mPaint);
									mPaint.setColor(Const.MA_ZISE);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (kHeight + topWhite - (bollDataDraw[2][i - 1] - minPrice) * rul),
											(float) (interval * i + interval / 2 + leftWhite), (float) (kHeight + topWhite - (bollDataDraw[2][i] - minPrice) * rul), mPaint);

								}

							}

						}
						mPaint.setStyle(Paint.Style.FILL);
						// drawTitle(canvas);
						moveLine(canvas, lsDraw);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				mPaint_text.setColor(Color.RED);
				canvas.drawText(price01, x_a, topWhite + offY * 2, mPaint_text);
				canvas.drawText(price02, x_a, topWhite + offY + kHeight * 1 / 4, mPaint_text);
				mPaint_text.setColor(Color.WHITE);
				canvas.drawText(price03, x_a, topWhite + offY + kHeight * 2 / 4, mPaint_text);
				mPaint_text.setColor(Color.GREEN);
				canvas.drawText(price04, x_a, topWhite + offY + kHeight * 3 / 4, mPaint_text);
				canvas.drawText(price05, x_a, topWhite - textSizeWhite + kHeight, mPaint_text);

				// mPaint.setAntiAlias(false);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// canvas是根据surfaceHolder得到的, 最后一次surfaceView已经销毁, canvas当然也不存在了.
			if (canvas != null)
				// 3. 将画布解锁并显示在屏幕上
				surfaceHolder.unlockCanvasAndPost(canvas);
		}

		offset = 0;
	}

	int temp_write = Color.argb(255, 100, 100, 100);
	int temp_write2 = Color.argb(255, 60, 60, 60);

	public void run() {
		// 每隔100ms刷新屏幕
		while (isThreadRunning) {
			drawVieW2();
			try {
				Thread.sleep(thread_sleep_time);
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.postInvalidate();
		}

	}

	/*
	 * 这个是第二种方法解决退出是报错的问题. 当按下返回键时, 提前设置isThreadRunning为false, 让线程结束.
	 * 
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
	 * if(keyCode == KeyEvent.KEYCODE_BACK) { isThreadRunning = false; } return
	 * super.onKeyDown(keyCode, event); }
	 */
}
