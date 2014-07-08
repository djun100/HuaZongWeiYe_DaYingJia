package com.hzwydyj.finace.fragments;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.activitys.HZWY_BaseActivity;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.KData;
import com.hzwydyj.finace.data.PriceData;
import com.hzwydyj.finace.present.view.KLineDraw;
import com.hzwydyj.finace.utils.MSG_WHAT;
import com.hzwydyj.finace.utils.MyLogger;
import com.hzwydyj.finace.utils.UDPtools;
import com.hzwydyj.finace.utils.Util;
import com.hzwydyj.finace.view.KLineDraw_SurfaceView2;

public class Line_k_Fragment extends Fragment {

	OnUDPListener mCallback;

	/**
	 * 回调接口
	 * 
	 * @author yaoid
	 * 
	 */
	public interface OnUDPListener {
		public void onUDP_push(String timeNow);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mCallback = (OnUDPListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnUDPListener");
		}

	}

	KLineDraw_SurfaceView2 kLineDraw_SurfaceView2;

	private MyLogger log = MyLogger.yLog();

	private int klinesize = 45;
	// 本地配置保存对象
	private SharedPreferences preferences;
	private static final String TYPE1M = "1m";
	private static final String TYPE5M = "5m";
	private static final String TYPE15M = "15m";
	private static final String TYPE30M = "30m";
	private static final String TYPE60M = "60m";
	private static final String TYPE1D = "1d";
	private static final String TYPE1W = "1w";
	private static final String TYPE1MON = "1mon";

	private static final long TYPE1MTIME = 60;
	private static final long TYPE5MTIME = 300;
	private static final long TYPE15MTIME = 900;
	private static final long TYPE30MTIME = 1800;
	private static final long TYPE60MTIME = 3600;
	private static final long TYPETIME = 36000000;

	private String code;
	private String ex;
	private String name;
	private String decimal;
	private String type = TYPE1D;
	private long timeforcheck = TYPE1MTIME;
	private LinearLayout klineLayout;
	private PriceData timeNow;

	private List<KData> klineList;

	private float lastClose = 0;// 最后收盘价

	// private TextView nowvalue;
	// private TextView updownpecent;
	// private TextView updownvalue;
	// private TextView cjvalue;
	// private TextView openvalue;
	// private TextView closevalue;
	// private TextView highvalue;
	// private TextView lowvalue;
	// private TextView dayvalue;

	private ImageButton increase;
	private ImageButton narrow;

	private Button indexBtn;
	private Button timeSel;
	private Button typedayBtn;
	private Button typeweekBtn;
	private Button typemonBtn;
	// private Button type1mBtn;
	private Button type5mBtn;
	private Button type15mBtn;
	private Button type30mBtn;
	private Button type60mBtn;
	// 最后选择类别按钮
	private Button lastSelectBtn;

	// 数据取得等待对话框组件
	private ProgressDialog progressDialog;

	// 自动更新线程
	private AutoUpdateThread autoUpdateThread;

	// 自动发送active
	private SendActiveThread sendActiveThread;

	private KLineDraw kLineDraw;

	// 自动更新线程启动标志
	private boolean running = false;

	private Util util = new Util();

	private String errorMsg;

	public void onPause() {
		super.onPause();
		//log.i("onPause");
		closeUDP();
	}

	public void onResume() {
		super.onResume();
		//log.i("onResume");
		UDPtools.initUDP();
		startUDP();
	}

	private void startUDP() {
		running = true;
		new Thread("onResume") {
			public void run() {
				try {
					UDPtools.startUDP(code);
					startAutoUpdateT();
					startsendActiveT();
				} catch (Exception e) {
					//log.i("startUDP Exception");
				}
			}
		}.start();
	}

	private void closeUDP() {
		running = false;

		UDPtools.closeClient();

		if (autoUpdateThread != null) {
			autoUpdateThread.interrupt();
			autoUpdateThread = null;
		}

		if (sendActiveThread != null) {
			sendActiveThread.interrupt();
			sendActiveThread = null;
		}

	}

	private void startAutoUpdateT() {
		autoUpdateThread = new AutoUpdateThread();
		autoUpdateThread.start();
	}

	private void startsendActiveT() {
		sendActiveThread = new SendActiveThread();
		sendActiveThread.start();
	}

	private Button choiceline;
	private Button zoomin;
	private Button zoomout;

	private void initButton(View view) {

		zoomin = (Button) view.findViewById(R.id.zoomin);
		zoomin.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				klinesize -= 9;
				if (klinesize <= 27) {
					klinesize = 27;
					hzwy_BaseActivity.HZWY_Toast1("已放大至最大");
				}
				//log.i("zoomin->" + klineList.size());
				//log.i("klinesize->" + klinesize);

				kLineDraw_SurfaceView2.setInitFlag(true);
				kLineDraw_SurfaceView2.updateData(klineList, klinesize);

				// kLineDraw_SurfaceView2.setInitFlag(false);
				// getTCPDataTask(code);
			}
		});

		zoomout = (Button) view.findViewById(R.id.zoomout);
		zoomout.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				klinesize += 9;
				if (klinesize >= 153) {
					klinesize = 153;
				}

				// points = size- point
				if (klineList.size() - klinesize < klinesize) {
					klinesize -= 9;
					hzwy_BaseActivity.HZWY_Toast1("已缩小至最小");
				}

				//log.i("zoomout->" + klineList.size());
				//log.i("klinesize->" + klinesize);
				kLineDraw_SurfaceView2.setInitFlag(true);
				kLineDraw_SurfaceView2.updateData(klineList, klinesize);

				// kLineDraw_SurfaceView2.setInitFlag(false);
				// getTCPDataTask(code);
			}
		});

		// increase = (ImageButton) view.findViewById(R.id.increase);
		// increase.setOnClickListener(new OnClickListener() {
		// public void onClick(View arg0) {
		// klinesize -= 9;
		// if (klinesize <= 27) {
		// klinesize = 27;
		// }
		// }
		// });
		// narrow = (ImageButton) view.findViewById(R.id.narrow);
		// narrow.setOnClickListener(new OnClickListener() {
		// public void onClick(View arg0) {
		// klinesize += 9;
		// if (klinesize >= 153) {
		// klinesize = 153;
		// }
		// }
		// });

		choiceline = (Button) view.findViewById(R.id.choiceline);
		choiceline.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (kLineDraw_SurfaceView2.getChoiceline()) {// 如果为真 竖线开启
					choiceline.setSelected(false);
					choiceline.setBackgroundDrawable(getResources().getDrawable(R.drawable.choseline));
					hzwy_BaseActivity.HZWY_Toast1("横向拖动-已开启");
					// choiceline.setText("拖动");
				} else {
					choiceline.setSelected(true);
					choiceline.setBackgroundDrawable(getResources().getDrawable(R.drawable.choseline_press));
					hzwy_BaseActivity.HZWY_Toast1("横向拖动-已关闭");
					// choiceline.setText("单根");
				}

				kLineDraw_SurfaceView2.setChoiceline(kLineDraw_SurfaceView2.getChoiceline() == true ? false : true);
			}
		});

		// typedayBtn = (Button) view.findViewById(R.id.daytype);
		// lastSelectBtn = typedayBtn;
		// lastSelectBtn.setBackgroundResource(R.drawable.orangebut);
		// typedayBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// Button btn = (Button) v;
		// // type = TYPE1D;
		// timeforcheck = TYPETIME;
		// if (!btn.equals(lastSelectBtn)) {
		// running = false;
		// btn.setBackgroundResource(R.drawable.orangebut);
		// lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
		// lastSelectBtn = btn;
		// kLineDraw_SurfaceView2.setInitFlag(false);
		// getTCPDataTask(code);
		// }
		// }
		// });
		// typeweekBtn = (Button) view.findViewById(R.id.weektype);
		// typeweekBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// Button btn = (Button) v;
		// // type = TYPE1W;
		// timeforcheck = TYPETIME;
		// if (!btn.equals(lastSelectBtn)) {
		// running = false;
		// btn.setBackgroundResource(R.drawable.orangebut);
		// lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
		// lastSelectBtn = btn;
		// kLineDraw_SurfaceView2.setInitFlag(false);
		// getTCPDataTask(code);
		// // startUDP();
		// }
		// }
		// });

		// 月线
		// typemonBtn = (Button) view.findViewById(R.id.montype);
		// typemonBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// Button btn = (Button) v;
		// type = TYPE1MON;
		// timeforcheck = TYPETIME;
		// try {
		// if (!btn.equals(lastSelectBtn)) {
		// running = false;
		// btn.setBackgroundResource(R.drawable.orangebut);
		// lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
		// lastSelectBtn = btn;
		// getTCPDataTask(code);
		// // startUDP();
		// }
		// } catch (Exception e) {
//		HZWY_Toast1("暂无数据");
		// e.printStackTrace();
		// }
		// }
		// });

		// 1分
		// typemonBtn = (Button) view.findViewById(R.id.type1m);
		// typemonBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// Button btn = (Button) v;
		// // type = TYPE1M;
		// timeforcheck = TYPETIME;
		// try {
		// if (!btn.equals(lastSelectBtn)) {
		// running = false;
		// btn.setBackgroundResource(R.drawable.orangebut);
		// lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
		// lastSelectBtn = btn;
		// kLineDraw_SurfaceView2.setInitFlag(false);
		// getTCPDataTask(code);
		// // startUDP();
		// }
		// } catch (Exception e) {
//		HZWY_Toast1("暂无数据");
		// e.printStackTrace();
		// }
		// }
		// });
		// type5mBtn = (Button) view.findViewById(R.id.type5m);
		// type5mBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// Button btn = (Button) v;
		// // type = TYPE5M;
		// timeforcheck = TYPE5MTIME;
		// if (!btn.equals(lastSelectBtn)) {
		// running = false;
		// btn.setBackgroundResource(R.drawable.orangebut);
		// lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
		// lastSelectBtn = btn;
		// kLineDraw_SurfaceView2.setInitFlag(false);
		// getTCPDataTask(code);
		// // startUDP();
		// }
		// }
		// });
		// type15mBtn = (Button) view.findViewById(R.id.type15m);
		// type15mBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// Button btn = (Button) v;
		// // type = TYPE15M;
		// timeforcheck = TYPE15MTIME;
		// if (!btn.equals(lastSelectBtn)) {
		// running = false;
		// btn.setBackgroundResource(R.drawable.orangebut);
		// lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
		// lastSelectBtn = btn;
		// kLineDraw_SurfaceView2.setInitFlag(false);
		// getTCPDataTask(code);
		// // startUDP();
		// }
		// }
		// });
		// type30mBtn = (Button) view.findViewById(R.id.type30m);
		// type30mBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// Button btn = (Button) v;
		// // type = TYPE30M;
		// timeforcheck = TYPE30MTIME;
		// if (!btn.equals(lastSelectBtn)) {
		// running = false;
		// btn.setBackgroundResource(R.drawable.orangebut);
		// lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
		// lastSelectBtn = btn;
		// kLineDraw_SurfaceView2.setInitFlag(false);
		// getTCPDataTask(code);
		// // startUDP();
		// }
		// }
		// });
		// type60mBtn = (Button) view.findViewById(R.id.type60m);
		// type60mBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// Button btn = (Button) v;
		// // type = TYPE60M;
		// timeforcheck = TYPE60MTIME;
		// if (!btn.equals(lastSelectBtn)) {
		// running = false;
		// btn.setBackgroundResource(R.drawable.orangebut);
		// lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
		// lastSelectBtn = btn;
		// kLineDraw_SurfaceView2.setInitFlag(false);
		// getTCPDataTask(code);
		// // startUDP();
		// }
		// }
		// });

		timeSel = (Button) view.findViewById(R.id.timeSel);
		timeSel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final String[] items = getResources().getStringArray(R.array.chosetime);
				new AlertDialog.Builder(getActivity()).setTitle("选择周期").setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
						case 6:
							type = types[which];
							timeforcheck = timeforchecks[which];// 这句代码没什么作用
							kLineDraw_SurfaceView2.setInitFlag(false);
							getTCPDataTask(code);
							return;
						case 7:
							return;
						default:
							;

						}
					}
				}).show();
			}
		});

		indexBtn = (Button) view.findViewById(R.id.indexSel);
		indexBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final String[] items = getResources().getStringArray(R.array.typeitem);
				new AlertDialog.Builder(getActivity()).setTitle("选择指标").setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
						case 6:
						case 7:
							kLineDraw_SurfaceView2.setIndex(INDEX[which]);
							return;
						case 8:
							return;
						default:
							;

						}
					}
				}).show();
			}
		});
	}

	private static final String[] types = { TYPE1D, TYPE1W, TYPE1M, TYPE5M, TYPE15M, TYPE30M, TYPE60M };
	private static final long[] timeforchecks = { TYPETIME, TYPETIME, TYPETIME, TYPE5MTIME, TYPE15MTIME, TYPE30MTIME, TYPE60MTIME };
	private final static String[] INDEX = { Const.INDEX_MACD, Const.INDEX_VOL, Const.INDEX_RSI, Const.INDEX_BOLL,
			Const.INDEX_KDJ, Const.INDEX_OBV, Const.INDEX_CCI, Const.INDEX_PSY };

	public void showNew() {
		progressD_state(false);

		if (klineList != null && klineList.size() > 0) {
			kLineDraw_SurfaceView2.setInitFlag(true);
			kLineDraw_SurfaceView2.updateData(klineList, klinesize);
		}

	}

	private synchronized void updateData(String code) {
		//log.i("updateData");
		// lastUpdateData = null;
		String listDataURL = Const.URL_KDATA;
		listDataURL = listDataURL.replaceFirst(Const.URL_EX, ex);
		listDataURL = listDataURL.replaceFirst(Const.URL_CODE, code);
		listDataURL = listDataURL.replaceFirst(Const.URL_TYPE, type);
		listDataURL = listDataURL.replaceFirst(Const.URL_COUNT, "180");
		klineList = util.getKData(listDataURL);
		//log.i("url->" + listDataURL);
		//log.i("updateData->" + klineList.size());
		String timeNowURL = Const.URL_NOW;
		timeNowURL = timeNowURL.replaceFirst(Const.URL_EX, ex);
		timeNowURL = timeNowURL.replaceFirst(Const.URL_CODE, code);
		timeNowURL = timeNowURL.replaceFirst(Const.URL_DATE, "1333605270");
		timeNowURL = timeNowURL.replaceFirst(Const.URL_COUNT, "3");
		timeNow = util.getTimeNow(timeNowURL, ex);
		lastClose = util.getFloat(timeNow.getPrice_lastclose());
		if (lastClose == 0) {
			lastClose = util.getFloat(timeNow.getPrice_lastclose());
			kLineDraw_SurfaceView2.setLastClose(lastClose);
		}
		//log.i("KDraw setCode->" + code);
		kLineDraw_SurfaceView2.setCode(code);
		running = true;
	}

	private class SendActiveThread extends Thread {
		private long waitTime = 10000;

		public void run() {
			while (running) {
				try {
					SendActiveThread.sleep(waitTime);
					if (UDPtools.client != null && !UDPtools.client.isClosed()) {
						UDPtools.sendpacket.setData(UDPtools.sendBuf_active);
						UDPtools.client.send(UDPtools.sendpacket);
						//log.i("UDP active,active");
					}

				} catch (Exception e) {
					e.printStackTrace();
					SendActiveThread.currentThread().interrupt();
					if (running) {
						// sendMessagewhat(MSG_WHAT.THREAD_SENDACTIVE_FAIL);
					}
					return;
				}

			}
		}
	}

	private class AutoUpdateThread extends Thread {

		public void run() {
			while (running) {
				try {
					AutoUpdateThread.sleep(500);
					if (UDPtools.client != null && !UDPtools.client.isClosed()) {
						UDPtools.client.receive(UDPtools.recpacket);

						String str = new String(UDPtools.recpacket.getData(), 0, 1000);
						// PriceData temp = null;
						if (str.indexOf("{") >= 0 && str.indexOf("}") >= 0) {
							// temp = util.getTimeNowUDPString(str, null);
							timeNow_UDP = util.getTimeNowUDPString(str, null);
						}
						if (timeNow_UDP != null && running == true) {
							mCallback.onUDP_push(str);
							Message msg = handler.obtainMessage();
							msg.what = MSG_WHAT.REFRESH_K;
							msg.obj = timeNow_UDP;
							handler.sendMessage(msg);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					if (running) {
						sendMessagewhat(MSG_WHAT.THREAD_AUTOUPDATE_FAIL);
					}
				}
			}
		}
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_WHAT.WATCHLIST_ADD:
				hzwy_BaseActivity.HZWY_Toast1("已添加到我的自选中。");
				break;
			case MSG_WHAT.WATCHLIST_EXIST:
				hzwy_BaseActivity.HZWY_Toast1("我的自选中已有此项。");
				break;
			case MSG_WHAT.THREAD_SENDACTIVE_FAIL:
				hzwy_BaseActivity.HZWY_Toast(MSG_WHAT.THREAD_SENDACTIVE_FAIL);
				break;
			case MSG_WHAT.THREAD_AUTOUPDATE_FAIL:
				hzwy_BaseActivity.HZWY_Toast(MSG_WHAT.THREAD_AUTOUPDATE_FAIL);
				break;
			case MSG_WHAT.SHOW_LIST:
				showNew();
				break;
			case MSG_WHAT.REFRESH_K:
				if (TYPE1D.equals(type) || TYPE1W.equals(type) || TYPE1MON.equals(type)) {
					showNew_K_notadd();
				} else {
					showNew_K_maybeadd();
				}
				break;
			default:
			}

		}
	};

	protected void addDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setMessage("确认要添加到我的自选吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				new Thread() {
					public void run() {

						SharedPreferences.Editor editor = preferences.edit();
						String opTemp = preferences.getString(Const.PREF_OPTIONAL, "");
						String opNameTemp = preferences.getString(Const.PREF_OPTIONAL_NAME, "");
						String exTemp = preferences.getString(Const.PREF_EX, "");
						if ("".equals(opTemp)) {
							editor.putString(Const.PREF_OPTIONAL, code);
							editor.putString(Const.PREF_OPTIONAL_NAME, name);
							editor.putString(Const.PREF_EX, ex);
						} else {
							String[] tmp = opTemp.split(",");
							for (int i = 0; i < tmp.length; i++) {
								if (code.equals(tmp[i])) {
									sendMessagewhat(MSG_WHAT.WATCHLIST_EXIST);
									return;
								}
							}
							opTemp = opTemp + "," + code;
							exTemp = exTemp + "," + ex;
							opNameTemp = opNameTemp + "," + name;
							editor.putString(Const.PREF_OPTIONAL, opTemp);
							editor.putString(Const.PREF_EX, exTemp);
							editor.putString(Const.PREF_OPTIONAL_NAME, opNameTemp);
						}
						editor.commit();
						sendMessagewhat(MSG_WHAT.WATCHLIST_ADD);
					}
				}.start();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.regiter_caler, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void sendMessagewhat(int what) {
		handler.sendEmptyMessage(what);
	}

	public Line_k_Fragment() {
	}

	/** 缓存view */
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//log.i("onCreateView");
		if (view == null) {
			view = inflater.inflate(R.layout.f_line_k, container, false);
			code = getArguments().getString("code");
			ex = getArguments().getString("ex");
			name = getArguments().getString("name");
			decimal = getArguments().getString("decimal");
			//log.i("code - " + code + ",ex - " + ex + ",name - " + name + ",decimal - " + decimal);

			initProgressD();
			initButton(view);

			klineLayout = (LinearLayout) view.findViewById(R.id.klinelayout);
			kLineDraw_SurfaceView2 = new KLineDraw_SurfaceView2(getActivity(), decimal);
			klineLayout.addView(kLineDraw_SurfaceView2);

			getTCPDataTask(code);

		}

		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}

		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		return view;
	}

	private void getTCPDataTask(String code) {
		GetTCPDataTask mytask = new GetTCPDataTask();
		mytask.execute(code, null, null);
	}

	class GetTCPDataTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressD_state(true);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				//log.i("GetTCPDataTask -> " + params[0]);
				updateData(params[0]);
			} catch (Exception e) {
				progressD_state(false);
				e.printStackTrace();
				sendMessagewhat(MSG_WHAT.ASYNCTASK_FAIL);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressD_state(false);
			sendMessagewhat(MSG_WHAT.SHOW_LIST);
		}
	}

	private void getTCPDataTask2(String code) {
		GetTCPDataTask mytask = new GetTCPDataTask();
		mytask.execute(code, null, null);
	}

	class GetTCPDataTask2 extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressD_state(true);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				//log.i("GetTCPDataTask -> " + params[0]);
				updateData(params[0]);
			} catch (Exception e) {
				progressD_state(false);
				e.printStackTrace();
				sendMessagewhat(MSG_WHAT.ASYNCTASK_FAIL);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressD_state(false);
			sendMessagewhat(MSG_WHAT.REFRESH_K);
		}
	}

	private void initProgressD() {
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("取得数据...");
		progressDialog.setCancelable(true);
	}

	/**
	 * 进度条显示或隐藏
	 * 
	 * @param isshow
	 */
	private void progressD_state(boolean isshow) {
		if (progressDialog != null) {
			if (isshow) {
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}
			} else {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		}
	}

	// 当前数据
	private PriceData timeNow_UDP;

	/**
	 * 日线 周线 月线 更新
	 */
	public void showNew_K_notadd() {
		if (klineList != null && klineList.size() > 0) {

			// 拿出最后一根K线
			KData k_temp = klineList.get(klineList.size() - 1);
			// log.i("k_temp->" + k_temp.toString());
			// log.i("timeNow_UDP->" + timeNow_UDP.toString());
			KData lastPoint = new KData();

			// 更新为最新的数据
			lastPoint.setK_timeLong(String.valueOf(Long.valueOf(timeNow_UDP.getPrice_quotetime())));
			lastPoint.setK_open(timeNow_UDP.getPrice_open());

			// 不知道为什么 推送的时候 会遇到开盘价为零的情况 暂时这么弥补一下
			// log.i("getPrice_open->" + timeNow_UDP.getPrice_open());
			if (Double.parseDouble(timeNow_UDP.getPrice_open()) == 0) {
				lastPoint.setK_open(k_temp.getK_open());
			} else {
				lastPoint.setK_open(timeNow_UDP.getPrice_open());
			}
			lastPoint.setK_close(timeNow_UDP.getPrice_last());
			//
			lastPoint.setK_date(k_temp.getK_date());

			if (util.getFloat(timeNow_UDP.getPrice_last()) > util.getFloat(k_temp.getK_high())) {
				lastPoint.setK_high(timeNow_UDP.getPrice_last());
			} else {
				lastPoint.setK_high(k_temp.getK_high());
			}
			if (util.getFloat(timeNow_UDP.getPrice_last()) < util.getFloat(k_temp.getK_low())) {
				lastPoint.setK_low(timeNow_UDP.getPrice_last());
			} else {
				lastPoint.setK_low(k_temp.getK_low());
			}

			klineList.remove(klineList.size() - 1);
			klineList.add(lastPoint);

			kLineDraw_SurfaceView2.setInitFlag(true);
			if (!kLineDraw_SurfaceView2.getRight2edge()) {
				kLineDraw_SurfaceView2.updateData(klineList, klinesize);
			}
		}
	}

	private boolean flag_updateList;

	private HZWY_BaseActivity hzwy_BaseActivity;

	/**
	 * 分钟K线 更新
	 */
	private void showNew_K_maybeadd() {
		if (klineList != null && klineList.size() > 0 && timeNow_UDP != null) {

			// 拿出最后一根K线
			KData k_temp = klineList.get(klineList.size() - 1);
			// log.i("k_temp->" + k_temp.toString());
			// log.i("timeNow_UDP->" + timeNow_UDP.toString());
			// 判断具体参数类型
			int time_diff = 0;
			if (TYPE1M.equals(type)) {
				time_diff = 60;
			} else if (TYPE5M.equals(type)) {
				time_diff = 300;
			} else if (TYPE15M.equals(type)) {
				time_diff = 900;
			} else if (TYPE30M.equals(type)) {
				time_diff = 1800;
			} else if (TYPE60M.equals(type)) {
				time_diff = 3600;
			}

			// 取出最后一根K线的时间戳
			String time_listlast = k_temp.getK_timeLong();
			// 取出timeNow_UDP 的时间戳
			String time_timeNow_UDP = timeNow_UDP.getPrice_quotetime();

			// log.i(flag_updateList + "差值为->" + (Long.valueOf(time_timeNow_UDP) - (Long.valueOf(time_listlast))));
			// log.i("lastdate->" + k_temp.getK_date());

			if (time_diff > (Long.valueOf(time_timeNow_UDP) - (Long.valueOf(time_listlast)))
					&& (Long.valueOf(time_timeNow_UDP) - (Long.valueOf(time_listlast))) >= 0) {

				if (flag_updateList) {
					getTCPDataTask2(code);
					flag_updateList = false;
					return;
				}

				// log.i("新增一根" + flag_updateList);
				KData lastPoint = new KData();
				lastPoint.setK_open(k_temp.getK_close());
				// lastPoint.setK_open(timeNow_UDP.getPrice_open());

				// 按照不同的type塞入不同的时间间隔
				Long long_timeLong = Long.valueOf(k_temp.getK_timeLong());
				String long_timelongString = String.valueOf(long_timeLong + time_diff);
				lastPoint.setK_timeLong(long_timelongString);
				lastPoint.setK_date(util.formatTimeSec(long_timelongString));

				lastPoint.setK_close(timeNow_UDP.getPrice_last());

				if (util.getFloat(timeNow_UDP.getPrice_last()) > util.getFloat(k_temp.getK_high())) {
					// lastPoint.setK_high(timeNow_UDP.getPrice_last());
					lastPoint.setK_high(k_temp.getK_high());
				} else {
					lastPoint.setK_high(k_temp.getK_high());
					// lastPoint.setK_high(timeNow_UDP.getPrice_high());
				}
				if (util.getFloat(timeNow_UDP.getPrice_last()) < util.getFloat(k_temp.getK_low())) {
					lastPoint.setK_low(timeNow_UDP.getPrice_last());
					lastPoint.setK_low(k_temp.getK_low());

				} else {
					lastPoint.setK_low(k_temp.getK_low());
					// lastPoint.setK_low(timeNow_UDP.getPrice_low());
				}

				// klineList.remove(klineList.size() - 1);
				// log.i("lastPoint->" + lastPoint.toString());
				klineList.add(lastPoint);

				kLineDraw_SurfaceView2.setInitFlag(true);
				if (!kLineDraw_SurfaceView2.getRight2edge()) {
					kLineDraw_SurfaceView2.updateData(klineList, klinesize);
				}
				flag_updateList = true;

			} else if ((Long.valueOf(time_timeNow_UDP)) - (Long.valueOf(time_listlast)) > -time_diff) {
				// log.i("-----");
				KData lastPoint = new KData();
				lastPoint.setK_open(k_temp.getK_open());

				// 按照不同的type塞入不同的时间间隔
				lastPoint.setK_timeLong(String.valueOf(Long.valueOf(k_temp.getK_timeLong())));
				lastPoint.setK_date(k_temp.getK_date());

				lastPoint.setK_close(timeNow_UDP.getPrice_last());

				if (util.getFloat(timeNow_UDP.getPrice_last()) > util.getFloat(k_temp.getK_high())) {
					lastPoint.setK_high(timeNow_UDP.getPrice_last());
				} else {
					lastPoint.setK_high(k_temp.getK_high());
				}
				if (util.getFloat(timeNow_UDP.getPrice_last()) < util.getFloat(k_temp.getK_low())) {
					lastPoint.setK_low(timeNow_UDP.getPrice_last());
				} else {
					lastPoint.setK_low(k_temp.getK_low());
				}

				klineList.remove(klineList.size() - 1);
				klineList.add(lastPoint);
				// log.i("lastPoint-1>" + lastPoint.toString());

				kLineDraw_SurfaceView2.setInitFlag(true);
				if (!kLineDraw_SurfaceView2.getRight2edge()) {
					kLineDraw_SurfaceView2.updateData(klineList, klinesize);
				}
			}

		}
	}
}
