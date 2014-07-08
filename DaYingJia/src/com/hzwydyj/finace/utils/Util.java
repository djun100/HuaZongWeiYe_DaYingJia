package com.hzwydyj.finace.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.util.Xml;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.hzwydyj.finace.data.CalData;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.ETF;
import com.hzwydyj.finace.data.ExceptionBean;
import com.hzwydyj.finace.data.GPrice;
import com.hzwydyj.finace.data.Gstore;
import com.hzwydyj.finace.data.HT_AD;
import com.hzwydyj.finace.data.HT_AD2;
import com.hzwydyj.finace.data.InforPojo;
import com.hzwydyj.finace.data.KData;
import com.hzwydyj.finace.data.PointBean;
import com.hzwydyj.finace.data.PriceData;
import com.hzwydyj.finace.data.RMBprice;
import com.hzwydyj.finace.data.ShiPanLogin;
import com.hzwydyj.finace.data.ShiPanSeedingBean;
import com.hzwydyj.finace.data.ShiPanTanGuBean;
import com.hzwydyj.finace.data.TypePojo;
import com.hzwydyj.finace.data.WinnerInside;

public class Util {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);
	private DecimalFormat df4 = new DecimalFormat("0.0000");
	private DecimalFormat df2 = new DecimalFormat("0.00");
	private DecimalFormat df = new DecimalFormat("");
	private HttpClient httpClient = new DefaultHttpClient();
	private HttpResponse httpResponse;

	public String formatTimeMin(String time) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Const.TIME_MIN);
			long timeTemp = Long.valueOf(time + "000");
			Date dt = new Date(timeTemp);
			res = sdf.format(dt);
		} catch (Exception e) {
		}

		return res;
	}

	public String formatFloat00(float orig) {
		String res = "";
		res = String.valueOf((float) Math.round(orig * 100) / 100);
		return res;
	}

	public String formatTimeHms(String time) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Const.TIME_HMS);
			long timeTemp = Long.valueOf(time + "000");
			Date dt = new Date(timeTemp);
			res = sdf.format(dt);
		} catch (Exception e) {
		}

		return res;
	}

	public String formatTimeYD(String time) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Const.TIME_HMS);
			long timeTemp = Long.valueOf(time + "000");
			Date dt = new Date(timeTemp);
			res = sdf.format(dt);
		} catch (Exception e) {
		}

		return res;
	}

	public String formatTimeSec(String time) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Const.TIME_SEC);
			long timeTemp = Long.valueOf(time + "000");
			Date dt = new Date(timeTemp);
			res = sdf.format(dt);
		} catch (Exception e) {
		}

		return res;
	}

	// 不用了
	public PriceData resTimeNow(String last) {
		PriceData priceData = null;
		if (last.indexOf(",") >= 0) {
			priceData = new PriceData();
			String[] tmp = last.split(",");
			for (int i = 0; i < tmp.length; i++) {

				String[] tmpvalue = tmp[i].split(":");
				if (Const.K_TIME.equals(tmpvalue[0])) {
					priceData.setPrice_quotetime(tmpvalue[1]);
				}
				if (Const.K_NEW.equals(tmpvalue[0])) {
					priceData.setPrice_last(tmpvalue[1]);
				}
				if (Const.K_OPEN.equals(tmpvalue[0])) {
					priceData.setPrice_open(tmpvalue[1]);
				}
				if (Const.K_HIGH.equals(tmpvalue[0])) {
					priceData.setPrice_high(tmpvalue[1]);
				}
				if (Const.K_LOW.equals(tmpvalue[0])) {
					priceData.setPrice_low(tmpvalue[1]);
				}
				if (Const.K_VOLUME.equals(tmpvalue[0])) {
					priceData.setPrice_volume(tmpvalue[1]);
				}
				if (Const.K_TOTAL.equals(tmpvalue[0])) {
					priceData.setPrice_total(tmpvalue[1]);
				}
				if (Const.K_LASTCLOSE.equals(tmpvalue[0])) {
					priceData.setPrice_lastclose(tmpvalue[1]);
				}
				if (Const.K_AVERAGE.equals(tmpvalue[0])) {
					priceData.setPrice_average(tmpvalue[1]);
				}
			}
			float updown = getFloat(priceData.getPrice_last()) - getFloat(priceData.getPrice_lastclose());
			float updownrate = updown / getFloat(priceData.getPrice_lastclose());
			updownrate = (float) Math.round(updownrate * 10000) / 100;
			priceData.setPrice_updown(String.valueOf(updown));
			priceData.setPrice_updownrate(String.valueOf(updownrate));

		}
		return priceData;
	}

	public float getFloat(String temp) {

		float res = 0;
		try {
			res = Float.valueOf(temp);
			// BigDecimal b = new BigDecimal(res);
			// res = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		} catch (Exception e) {
		}
		return res;
	}

	// UDP数据
	public PriceData getTimeNowUDPStringTTJ(String udp, PriceData timeNow) {
		PriceData res = new PriceData();
		try {
			JSONObject objectson = new JSONObject(udp);
			res.setPrice_quotetime(getJsonValue(objectson, Const.PRICE_QUOTETIME));
			res.setPrice_last(getJsonValue(objectson, Const.PRICE_LAST));
			res.setPrice_open(getJsonValue(objectson, Const.PRICE_OPEN));
			res.setPrice_high(getJsonValue(objectson, Const.PRICE_HIGH));
			res.setPrice_low(getJsonValue(objectson, Const.PRICE_LOW));
			res.setPrice_lastclose(getJsonValue(objectson, Const.PRICE_LASTCLOSE));
			res.setPrice_lastsettle(getJsonValue(objectson, Const.PRICE_LASTSETTLE));
			res.setPrice_code(getJsonValue(objectson, Const.PRICE_CODE));
			res.setPrice_name(getJsonValue(objectson, Const.PRICE_NAME));
			float lastClose = getFloat(res.getPrice_lastclose());
			float updown = getFloat(res.getPrice_last()) - lastClose;
			float updownrate = updown / lastClose * 100;
			res.setPrice_updown(String.valueOf(df2.format(updown)));
			res.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
			res.setPriceTTJbuy(getJsonValue(objectson, Const.PRICE_TTJBUY));
			res.setPriceTTJsell(getJsonValue(objectson, Const.PRICE_TTJSELL));
			// Log.i("temp"," ampl -> " + getJsonValue(objectson,
			// Const.PRICE_TTJAMLITUDE));
			res.setPriceTTJAmplitude(getJsonValue(objectson, Const.PRICE_TTJAMLITUDE));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return res;
		}
		return res;
	}

	// UDP数据
	public synchronized PriceData getTimeNowUDPString(String udp, PriceData timeNow) {
		PriceData res = new PriceData();
		try {
			JSONObject objectson = new JSONObject(udp);
			String code = getJsonValue(objectson, Const.PRICE_CODE);
			if (code == null || "".equals(code)) {
				return null;
			}
			res.setPrice_quotetime(getJsonValue(objectson, Const.PRICE_QUOTETIME));
			res.setPrice_last(getJsonValue(objectson, Const.PRICE_LAST));
			res.setPrice_turnover(getJsonValue(objectson, Const.PRICE_TURNOVER));
			res.setPrice_open(getJsonValue(objectson, Const.PRICE_OPEN));
			res.setPrice_high(getJsonValue(objectson, Const.PRICE_HIGH));
			res.setPrice_low(getJsonValue(objectson, Const.PRICE_LOW));
			res.setPrice_volume(getJsonValue(objectson, Const.PRICE_VOLUME));
			res.setPrice_total(getJsonValue(objectson, Const.PRICE_TOTAL));
			res.setPrice_lastclose(getJsonValue(objectson, Const.PRICE_LASTCLOSE));
			res.setPrice_lastsettle(getJsonValue(objectson, Const.PRICE_LASTSETTLE));
			res.setPrice_code(getJsonValue(objectson, Const.PRICE_CODE));
			res.setPrice_name(getJsonValue(objectson, Const.PRICE_NAME));
			res.setPrice_average(getJsonValue(objectson, Const.PRICE_AVERAGE));
			res.setPrice_bid1(getJsonValue(objectson, Const.PRICE_BID1));
			res.setPrice_bid2(getJsonValue(objectson, Const.PRICE_BID2));
			res.setPrice_bid3(getJsonValue(objectson, Const.PRICE_BID3));
			res.setPrice_bid4(getJsonValue(objectson, Const.PRICE_BID4));
			res.setPrice_bid5(getJsonValue(objectson, Const.PRICE_BID5));
			res.setPrice_bidlot1(getJsonValue(objectson, Const.PRICE_BIDLOT1));
			res.setPrice_bidlot2(getJsonValue(objectson, Const.PRICE_BIDLOT2));
			res.setPrice_bidlot3(getJsonValue(objectson, Const.PRICE_BIDLOT3));
			res.setPrice_bidlot4(getJsonValue(objectson, Const.PRICE_BIDLOT4));
			res.setPrice_bidlot5(getJsonValue(objectson, Const.PRICE_BIDLOT5));
			res.setPrice_ask1(getJsonValue(objectson, Const.PRICE_ASK1));
			res.setPrice_ask2(getJsonValue(objectson, Const.PRICE_ASK2));
			res.setPrice_ask3(getJsonValue(objectson, Const.PRICE_ASK3));
			res.setPrice_ask4(getJsonValue(objectson, Const.PRICE_ASK4));
			res.setPrice_ask5(getJsonValue(objectson, Const.PRICE_ASK5));
			res.setPrice_asklot1(getJsonValue(objectson, Const.PRICE_ASKLOT1));
			res.setPrice_asklot2(getJsonValue(objectson, Const.PRICE_ASKLOT2));
			res.setPrice_asklot3(getJsonValue(objectson, Const.PRICE_ASKLOT3));
			res.setPrice_asklot4(getJsonValue(objectson, Const.PRICE_ASKLOT4));
			res.setPrice_asklot5(getJsonValue(objectson, Const.PRICE_ASKLOT5));
			// ttj
			res.setPriceTTJbuy(getJsonValue(objectson, Const.PRICE_TTJBUY));
			res.setPriceTTJsell(getJsonValue(objectson, Const.PRICE_TTJSELL));

			if ("".equals(res.getPrice_lastclose()) && timeNow != null) {
				res.setPrice_lastclose(timeNow.getPrice_lastclose());
				res.setPrice_lastsettle(timeNow.getPrice_lastsettle());
				res.setPrice_average(timeNow.getPrice_average());
			}
			float lastClose = getFloat(res.getPrice_lastclose());
			if (Const.AUT_D.equals(res.getPrice_code()) || Const.AGT_D.equals(res.getPrice_code())) {
				if (res.getPrice_lastsettle() != null && !"".equals(res.getPrice_lastsettle())) {
					lastClose = getFloat(res.getPrice_lastsettle());
				}
			}
			float updown = getFloat(res.getPrice_last()) - lastClose;
			float updownrate = updown / lastClose * 100;
			res.setPrice_updown(String.valueOf(df4.format(updown)));
			res.setPrice_updownrate(String.valueOf(df2.format(updownrate)));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		}
		return res;
	}

	// 分笔
	public List<PriceData> getFenbi(String url, String ex) {
		List<PriceData> result = new ArrayList<PriceData>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					PriceData pData = new PriceData();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					pData.setPrice_code(getJsonValue(temp, Const.PRICE_CODE));
					pData.setPrice_quotetime(getJsonValue(temp, Const.PRICE_QUOTETIME));
					pData.setPrice_open(getJsonValue(temp, Const.PRICE_OPEN));
					pData.setPrice_high(getJsonValue(temp, Const.PRICE_HIGH));
					pData.setPrice_last(getJsonValue(temp, Const.PRICE_LAST));
					pData.setPrice_low(getJsonValue(temp, Const.PRICE_LOW));
					pData.setPrice_lastclose(getJsonValue(temp, Const.PRICE_LASTCLOSE));
					pData.setPrice_lastsettle(getJsonValue(temp, Const.PRICE_LASTSETTLE));
					pData.setPrice_volume(getJsonValue(temp, Const.PRICE_VOLUME));
					float lastClose = 0;
					if (Const.AUT_D.equals(pData.getPrice_code()) || Const.AGT_D.equals(pData.getPrice_code())) {
						if (pData.getPrice_lastsettle() != null && !"".equals(pData.getPrice_lastsettle())) {
							lastClose = getFloat(pData.getPrice_lastsettle());
						}
					}
					float updown = getFloat(pData.getPrice_last()) - lastClose;
					float updownrate = updown / lastClose * 100;
					if (Const.WH4.equals(ex) && !Const.USD.equals(pData.getPrice_code()) && !Const.USDJPY.equals(pData.getPrice_code())) {
						pData.setPrice_updown(String.valueOf(df4.format(updown)));
					} else {
						pData.setPrice_updown(String.valueOf(df2.format(updown)));
					}
					pData.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
					if (i < arrayJson.length() - 1) {
						float now = getFloat(getJsonValue(temp, Const.PRICE_VOLUME));
						float last = getFloat(getJsonValue((JSONObject) arrayJson.get(i + 1), Const.PRICE_VOLUME));
						float volume = now - last;
						pData.setPrice_volume(String.valueOf(df.format(volume)));

					}
					result.add(pData);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	// 画面启动初期使用
	public PriceData getTimeNowTTJ(String url, String ex) {
		List<PriceData> list = new ArrayList<PriceData>();
		PriceData priceData = new PriceData();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					PriceData priceDataTemp = new PriceData();
					JSONObject objectson = (JSONObject) arrayJson.get(i);
					priceDataTemp.setPrice_quotetime(getJsonValue(objectson, Const.PRICE_QUOTETIME));
					priceDataTemp.setPrice_last(getJsonValue(objectson, Const.PRICE_LAST));
					priceDataTemp.setPrice_open(getJsonValue(objectson, Const.PRICE_OPEN));
					priceDataTemp.setPrice_high(getJsonValue(objectson, Const.PRICE_HIGH));
					priceDataTemp.setPrice_low(getJsonValue(objectson, Const.PRICE_LOW));
					priceDataTemp.setPriceTTJsell(getJsonValue(objectson, Const.PRICE_TTJSELL));
					priceDataTemp.setPriceTTJbuy(getJsonValue(objectson, Const.PRICE_TTJBUY));
					priceDataTemp.setPriceTTJAmplitude(getJsonValue(objectson, Const.PRICE_TTJAMLITUDE));
					// priceDataTemp.setPrice_total(getJsonValue(objectson,
					// Const.PRICE_TOTAL));
					priceDataTemp.setPrice_lastclose(getJsonValue(objectson, Const.PRICE_LASTCLOSE));
					priceDataTemp.setPrice_code(getJsonValue(objectson, Const.PRICE_CODE));

					float lastClose = getFloat(priceDataTemp.getPrice_lastclose());
					float updown = getFloat(priceDataTemp.getPrice_last()) - lastClose;
					float updownrate = updown / lastClose * 100;
					if (Const.WH4.equals(ex) && !Const.USD.equals(priceDataTemp.getPrice_code()) && !Const.USDJPY.equals(priceDataTemp.getPrice_code())) {
						priceDataTemp.setPrice_updown(String.valueOf(df4.format(updown)));
					} else {
						priceDataTemp.setPrice_updown(String.valueOf(df2.format(updown)));
					}
					priceDataTemp.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
					if (i < arrayJson.length() - 1) {
						float now = getFloat(getJsonValue(objectson, Const.PRICE_VOLUME));
						float last = getFloat(getJsonValue((JSONObject) arrayJson.get(i + 1), Const.PRICE_VOLUME));
						String volume = String.valueOf(now - last);
						priceDataTemp.setPrice_volume(volume);
					}
					list.add(priceDataTemp);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0) {
			priceData = list.get(0);
		}
		return priceData;
	}

	// 画面启动初期使用
	public PriceData getTimeNow(String url, String ex) {
		List<PriceData> list = new ArrayList<PriceData>();
		PriceData priceData = new PriceData();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					PriceData priceDataTemp = new PriceData();
					JSONObject objectson = (JSONObject) arrayJson.get(i);
					priceDataTemp.setPrice_quotetime(getJsonValue(objectson, Const.PRICE_QUOTETIME));
					priceDataTemp.setPrice_last(getJsonValue(objectson, Const.PRICE_LAST));
					priceDataTemp.setPrice_turnover(getJsonValue(objectson, Const.PRICE_TURNOVER));
					priceDataTemp.setPrice_open(getJsonValue(objectson, Const.PRICE_OPEN));
					priceDataTemp.setPrice_high(getJsonValue(objectson, Const.PRICE_HIGH));
					priceDataTemp.setPrice_low(getJsonValue(objectson, Const.PRICE_LOW));
					priceDataTemp.setPrice_volume(getJsonValue(objectson, Const.PRICE_VOLUME));
					// priceDataTemp.setPrice_total(getJsonValue(objectson,
					// Const.PRICE_TOTAL));
					priceDataTemp.setPrice_lastclose(getJsonValue(objectson, Const.PRICE_LASTCLOSE));
					priceDataTemp.setPrice_lastsettle(getJsonValue(objectson, Const.PRICE_LASTSETTLE));
					priceDataTemp.setPrice_code(getJsonValue(objectson, Const.PRICE_CODE));
					priceDataTemp.setPrice_average(getJsonValue(objectson, Const.PRICE_AVERAGE));
					priceDataTemp.setPrice_bid1(getJsonValue(objectson, Const.PRICE_BID1));
					priceDataTemp.setPrice_bid2(getJsonValue(objectson, Const.PRICE_BID2));
					priceDataTemp.setPrice_bid3(getJsonValue(objectson, Const.PRICE_BID3));
					priceDataTemp.setPrice_bid4(getJsonValue(objectson, Const.PRICE_BID4));
					priceDataTemp.setPrice_bid5(getJsonValue(objectson, Const.PRICE_BID5));
					priceDataTemp.setPrice_bidlot1(getJsonValue(objectson, Const.PRICE_BIDLOT1));
					priceDataTemp.setPrice_bidlot2(getJsonValue(objectson, Const.PRICE_BIDLOT2));
					priceDataTemp.setPrice_bidlot3(getJsonValue(objectson, Const.PRICE_BIDLOT3));
					priceDataTemp.setPrice_bidlot4(getJsonValue(objectson, Const.PRICE_BIDLOT4));
					priceDataTemp.setPrice_bidlot5(getJsonValue(objectson, Const.PRICE_BIDLOT5));
					priceDataTemp.setPrice_ask1(getJsonValue(objectson, Const.PRICE_ASK1));
					priceDataTemp.setPrice_ask2(getJsonValue(objectson, Const.PRICE_ASK2));
					priceDataTemp.setPrice_ask3(getJsonValue(objectson, Const.PRICE_ASK3));
					priceDataTemp.setPrice_ask4(getJsonValue(objectson, Const.PRICE_ASK4));
					priceDataTemp.setPrice_ask5(getJsonValue(objectson, Const.PRICE_ASK5));
					priceDataTemp.setPrice_asklot1(getJsonValue(objectson, Const.PRICE_ASKLOT1));
					priceDataTemp.setPrice_asklot2(getJsonValue(objectson, Const.PRICE_ASKLOT2));
					priceDataTemp.setPrice_asklot3(getJsonValue(objectson, Const.PRICE_ASKLOT3));
					priceDataTemp.setPrice_asklot4(getJsonValue(objectson, Const.PRICE_ASKLOT4));
					priceDataTemp.setPrice_asklot5(getJsonValue(objectson, Const.PRICE_ASKLOT5));
					float lastClose = getFloat(priceDataTemp.getPrice_lastclose());
					if (Const.AUT_D.equals(priceDataTemp.getPrice_code()) || Const.AGT_D.equals(priceDataTemp.getPrice_code())) {
						lastClose = getFloat(priceDataTemp.getPrice_lastsettle());
					}
					priceDataTemp.setPriceTTJbuy(getJsonValue(objectson, Const.PRICE_TTJBUY));
					priceDataTemp.setPriceTTJsell(getJsonValue(objectson, Const.PRICE_TTJSELL));
					float updown = getFloat(priceDataTemp.getPrice_last()) - lastClose;
					float updownrate = 0;
					if (lastClose > 0) {
						updownrate = updown / lastClose * 100;
					}
					priceDataTemp.setPrice_updown(String.valueOf(df4.format(updown)));
					priceDataTemp.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
					if (i < arrayJson.length() - 1) {
						float now = getFloat(getJsonValue(objectson, Const.PRICE_VOLUME));
						float last = getFloat(getJsonValue((JSONObject) arrayJson.get(i + 1), Const.PRICE_VOLUME));
						String volume = String.valueOf(now - last);
						priceDataTemp.setPrice_volume(volume);
					}
					list.add(priceDataTemp);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0) {
			priceData = list.get(0);
		}
		return priceData;
	}

	public List<KData> getTimeData(String history) {
		List<KData> result = new ArrayList<KData>();
		HttpGet httpRequest = new HttpGet(history);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					KData kData = new KData();
					JSONObject temp = (JSONObject) arrayJson.get(i);

					kData.setK_timeLong(getJsonValue(temp, Const.K_DATE));
					kData.setK_date(formatTimeSec(kData.getK_timeLong()));
					kData.setK_close(getJsonValue(temp, Const.K_CLOSE));
					kData.setK_open(getJsonValue(temp, Const.K_OPEN));
					kData.setK_high(getJsonValue(temp, Const.K_HIGH));
					kData.setK_low(getJsonValue(temp, Const.K_LOW));
					// kData.setK_total(getJsonValue(temp, Const.K_TOTAL));
					kData.setK_volume(getJsonValue(temp, Const.K_VOLUME));
					result.add(kData);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public List<KData> getKData(String url) {
		List<KData> result = new ArrayList<KData>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					KData kData = new KData();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					kData.setK_timeLong(getJsonValue(temp, Const.K_DATE));
					kData.setK_date(formatTimeSec(kData.getK_timeLong()));
					kData.setK_close(getJsonValue(temp, Const.K_CLOSE));
					kData.setK_open(getJsonValue(temp, Const.K_OPEN));
					kData.setK_high(getJsonValue(temp, Const.K_HIGH));
					kData.setK_low(getJsonValue(temp, Const.K_LOW));
					kData.setK_volume(getJsonValue(temp, Const.K_VOLUME));
					result.add(kData);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public List<PriceData> getPriceData(String url, String type) {
		logger.debug("取数据" + new Date().toLocaleString());
		List<PriceData> result = new ArrayList<PriceData>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				logger.debug("取到数据" + new Date().toLocaleString());
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					PriceData pData = new PriceData();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					pData.setPrice_quotetime(getJsonValue(temp, Const.PRICE_QUOTETIME));
					pData.setPrice_open(getJsonValue(temp, Const.PRICE_OPEN));
					pData.setPrice_high(getJsonValue(temp, Const.PRICE_HIGH));
					pData.setPrice_last(getJsonValue(temp, Const.PRICE_LAST));
					pData.setPrice_Decimal(getJsonValue(temp, Const.PRICE_DECIMAL));
					if (Const.SHGOLD.equals(type)) {
						pData.setPrice_turnover(getJsonValue(temp, Const.PRICE_TURNOVER));
					}
					pData.setPrice_low(getJsonValue(temp, Const.PRICE_LOW));
					pData.setPrice_name(getJsonValue(temp, Const.PRICE_NAME));
					pData.setPrice_code(getJsonValue(temp, Const.PRICE_CODE));
					pData.setPrice_lastclose(getJsonValue(temp, Const.PRICE_LASTCLOSE));
					pData.setPrice_lastsettle(getJsonValue(temp, Const.PRICE_LASTSETTLE));
					// pData.setPrice_updown(getJsonValue(temp,
					// Const.PRICE_UPDOWN));
					// pData.setPrice_updownrate(getJsonValue(temp,
					// Const.PRICE_UPDOWNRATE));
					pData.setPrice_average(getJsonValue(temp, Const.PRICE_AVERAGE));
					pData.setPrice_volume(getJsonValue(temp, Const.PRICE_VOLUME));
					float lastClose = 0;
					lastClose = getFloat(pData.getPrice_lastclose());
					if (Const.AUT_D.equals(pData.getPrice_code()) || Const.AGT_D.equals(pData.getPrice_code())) {
						if (pData.getPrice_lastsettle() != null && !"".equals(pData.getPrice_lastsettle())) {
							lastClose = getFloat(pData.getPrice_lastsettle());
						}
					}
					float updown = getFloat(pData.getPrice_last()) - lastClose;
					float updownrate = updown / lastClose * 100;
					if (Const.WH.equals(type) && !Const.USD.equals(pData.getPrice_code()) && !Const.USDJPY.equals(pData.getPrice_code())) {
						pData.setPrice_updown(String.valueOf(df4.format(updown)));
					} else {
						pData.setPrice_updown(String.valueOf(df2.format(updown)));
					}
					pData.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
					result.add(pData);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("返回数据到前台" + new Date().toLocaleString());
		return result;

	}

	public List<PriceData> getPriceDataTTJ(String url, String type) {
		List<PriceData> result = new ArrayList<PriceData>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				JSONArray arrayJson = new JSONArray(builder.toString());
				PriceData pData;
				JSONObject temp;
				for (int i = 0; i < arrayJson.length(); i++) {
					pData = new PriceData();
					temp = (JSONObject) arrayJson.get(i);
					pData.setPrice_quotetime(getJsonValue(temp, Const.PRICE_QUOTETIME));
					pData.setPrice_Decimal(getJsonValue(temp, Const.PRICE_DECIMAL));
					pData.setPrice_open(getJsonValue(temp, Const.PRICE_OPEN));
					pData.setPrice_high(getJsonValue(temp, Const.PRICE_HIGH));
					pData.setPrice_last(getJsonValue(temp, Const.PRICE_LAST));
					pData.setPrice_low(getJsonValue(temp, Const.PRICE_LOW));
					pData.setPrice_name(getJsonValue(temp, Const.PRICE_NAME));
					pData.setPrice_code(getJsonValue(temp, Const.PRICE_CODE));
					pData.setPrice_lastclose(getJsonValue(temp, Const.PRICE_LASTCLOSE));
					pData.setPriceTTJbuy(getJsonValue(temp, Const.PRICE_TTJBUY));
					pData.setPriceTTJsell(getJsonValue(temp, Const.PRICE_TTJSELL));
					pData.setPriceTTJAmplitude(getJsonValue(temp, Const.PRICE_TTJAMLITUDE));
					pData.setPrice_Decimal(getJsonValue(temp, Const.PRICE_DECIMAL));

					// 涨跌幅 涨跌 在这里直接计算
					float lastClose = 0;
					float updownrate = 0;
					lastClose = getFloat(pData.getPrice_lastclose());
					float updown = getFloat(pData.getPrice_last()) - lastClose;
					if (lastClose > 0) {
						updownrate = updown / lastClose * 100;
					}
					pData.setPrice_updown(String.valueOf(df4.format(updown)));
					pData.setPrice_updownrate(String.valueOf(df2.format(updownrate)));

					result.add(pData);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;

	}

	public List<PriceData> getPriceDataOP(String url, String diycode, String diyex) {
		List<PriceData> result = new ArrayList<PriceData>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					PriceData pData = new PriceData();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					pData.setPrice_quotetime(getJsonValue(temp, Const.PRICE_QUOTETIME));
					pData.setPrice_open(getJsonValue(temp, Const.PRICE_OPEN));
					pData.setPrice_high(getJsonValue(temp, Const.PRICE_HIGH));
					pData.setPrice_last(getJsonValue(temp, Const.PRICE_LAST));
					pData.setPrice_turnover(getJsonValue(temp, Const.PRICE_TURNOVER));
					pData.setPrice_low(getJsonValue(temp, Const.PRICE_LOW));
					pData.setPrice_name(getJsonValue(temp, Const.PRICE_NAME));
					pData.setPrice_code(getJsonValue(temp, Const.PRICE_CODE));
					pData.setPrice_lastclose(getJsonValue(temp, Const.PRICE_LASTCLOSE));
					pData.setPrice_lastsettle(getJsonValue(temp, Const.PRICE_LASTSETTLE));
					pData.setPrice_Decimal(getJsonValue(temp, Const.PRICE_DECIMAL));
					pData.setPrice_volume(getJsonValue(temp, Const.PRICE_VOLUME));
					pData.setPrice_Decimal(getJsonValue(temp, Const.PRICE_DECIMAL));
					float lastClose = 0;
					lastClose = getFloat(pData.getPrice_lastclose());
					if (Const.AUT_D.equals(pData.getPrice_code()) || Const.AGT_D.equals(pData.getPrice_code())) {
						if (pData.getPrice_lastsettle() != null && !"".equals(pData.getPrice_lastsettle())) {
							lastClose = getFloat(pData.getPrice_lastsettle());
						}
					}
					float updown = getFloat(pData.getPrice_last()) - lastClose;
					float updownrate = 0;
					if (lastClose > 0) {
						updownrate = updown / lastClose * 100;
					}
					String codeTmp = pData.getPrice_code();
					int t = 0;
					String[] codearray = diycode.split(",");
					for (int j = 0; j < codearray.length; j++) {
						if (codeTmp.equals(codearray[j])) {
							t = j;
						}
					}
					String[] codeEXarray = diyex.split(",");
					pData.setPrice_updown(String.valueOf(df4.format(updown)));
					pData.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
					result.add(pData);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;

	}

	public synchronized List<CalData> getCalData(String url) {
		List<CalData> result = new ArrayList<CalData>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					CalData calData = new CalData();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					calData.setCalTime(getJsonValue(temp, Const.CAL_TIME));
					calData.setCalCountry(getJsonValue(temp, Const.CAL_COUNTRY));
					calData.setCalItem(getJsonValue(temp, Const.CAL_ITEM));
					calData.setCalImportance(getJsonValue(temp, Const.CAL_IMPORTANCE));
					calData.setCalLastValue(getJsonValue(temp, Const.CAL_LASTVALUE));
					calData.setCalPrediction(getJsonValue(temp, Const.CAL_PREDICTION));
					calData.setCalActual(getJsonValue(temp, Const.CAL_ACTUAL));
					result.add(calData);
				}
				httpRequest.abort();
				bufferedReader2.close();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private String getJsonValue(JSONObject temp, String key) {
		String res = "";
		try {
			res = temp.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return res;
	}

	private String getJsonValueUDP(JSONObject temp, String key, String UDP) {
		String res = "";
		if (UDP.indexOf(key) < 0) {
			return res;
		}
		try {
			res = temp.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return res;
	}

	// news
	public List<InforPojo> getInforByXML(String url) throws Exception {
		List<InforPojo> result = new ArrayList<InforPojo>();

		URL inforUrl = new URL(url);
		URLConnection ucon = inforUrl.openConnection();
		ucon.setRequestProperty("accept", "text/xml");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		InputStream input = ucon.getInputStream();
		// InputStreamReader streamReader = new InputStreamReader(input,
		// "UTF-8");
		// InputSource inputSource = new InputSource(streamReader);
		Document doc = builder.parse(input);
		NodeList items = doc.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			InforPojo inforPojo = new InforPojo();
			Element item = (Element) items.item(i);
			inforPojo.setTitle_(getData(item, "title"));
			// inforPojo.setContent_(getData(item, "description"));
			// 只取时间，不取日期
			// inforPojo.setTime_(formatTime(getData(item, "pubDate")));
			inforPojo.setTime_(getData(item, "pubDate"));
			// inforPojo.setTime_(formatTime2(getData(item, "pubDate")));
			inforPojo.setLink_(getData(item, "ID"));
			// inforPojo.setAuthor_(getData(item, "author"));
			// Log.d("+++++++++++inforPojo: ", inforPojo.toString());
			inforPojo.setImg(getData(item, "Img"));
			result.add(inforPojo);
		}

		return result;
	}

	// news
	public List<InforPojo> getInforByXML2(String url) throws Exception {
		List<InforPojo> result = new ArrayList<InforPojo>();

		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		InputStream inpst = null;

		try {
			HttpResponse response = client.execute(httpget);
			StatusLine sl = response.getStatusLine();
			int sc = sl.getStatusCode();
			// log.i("log_tag", "getStatusCode->" + sc);
			if (sc == 200) {
				HttpEntity ent = response.getEntity();
				inpst = ent.getContent();
			} else {
				Log.e("log_tag", "I didn't  get the response!");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		// InputStreamReader streamReader = new InputStreamReader(input,
		// "UTF-8");
		// InputSource inputSource = new InputSource(streamReader);
		Document doc = builder.parse(inpst);
		NodeList items = doc.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			InforPojo inforPojo = new InforPojo();
			Element item = (Element) items.item(i);
			inforPojo.setTitle_(getData(item, "title"));
			// inforPojo.setContent_(getData(item, "description"));
			// 只取时间，不取日期
			// inforPojo.setTime_(formatTime(getData(item, "pubDate")));
			inforPojo.setTime_(getData(item, "pubDate"));
			// inforPojo.setTime_(formatTime2(getData(item, "pubDate")));
			inforPojo.setLink_(getData(item, "ID"));
			// inforPojo.setAuthor_(getData(item, "author"));
			// Log.d("+++++++++++inforPojo: ", inforPojo.toString());
			inforPojo.setImg(getData(item, "Img"));
			result.add(inforPojo);
		}

		return result;
	}

	public String getData(Element item, String key) {

		String ret = "";
		if (item.getElementsByTagName(key) != null) {
			if (item.getElementsByTagName(key).item(0) != null) {
				if (item.getElementsByTagName(key).item(0).getFirstChild() != null) {
					ret = item.getElementsByTagName(key).item(0).getFirstChild().getNodeValue();
				}
			}
		}

		return ret;
	}

	public String getChildData(Element item, String tagName, String name, String key) {

		String ret = "";
		NodeList itemList = item.getElementsByTagName(tagName);
		for (int i = 0; i < itemList.getLength(); i++) {
			Element itemTemp = (Element) itemList.item(i);
			if (name.equals(itemTemp.getAttribute("name"))) {
				ret = getData(itemTemp, key);
			}
		}

		return ret;
	}

	public List<TypePojo> getTypeByXML(String url) throws Exception {
		List<TypePojo> result = new ArrayList<TypePojo>();

		URL inforUrl = new URL(url);
		URLConnection ucon = inforUrl.openConnection();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(ucon.getInputStream());
		NodeList items = doc.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			TypePojo typePojo = new TypePojo();
			Element item = (Element) items.item(i);
			typePojo.setTypeno_(getData(item, "typeno"));
			typePojo.setTypename_(getData(item, "typename"));
			typePojo.setTypeupdateurl_(getData(item, "typeupdateurl"));
			typePojo.setAutoupdate_(getData(item, "autoupdate"));
			result.add(typePojo);
		}

		return result;
	}

	private String formatTime(String time) {
		if (time != null) {
			if (time.length() > 0) {
				String[] timeValue = time.split(" ");
				time = timeValue[4];
			}
		}
		return time;
	}

	public String checkVersion(String url) {
		String res = "0";
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				res = builder.toString();

			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public List<Gstore> getJDInforByXML(String url) throws Exception {
		List<Gstore> result = new ArrayList<Gstore>();

		URL inforUrl = new URL(url);
		URLConnection ucon = inforUrl.openConnection();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		InputStream input = ucon.getInputStream();

		Document doc = builder.parse(input);
		NodeList brands = doc.getElementsByTagName("brand");
		for (int i = 0; i < brands.getLength(); i++) {

			Gstore gsinfo = new Gstore();
			Element item = (Element) brands.item(i);

			NodeList gsinfos = item.getChildNodes();
			for (int j = 0; j < gsinfos.getLength(); j++) {
				if ("id".equals(gsinfos.item(j).getNodeName())) {
					gsinfo.setGs_id(gsinfos.item(j).getFirstChild().getNodeValue());
				} else if ("name".equals(gsinfos.item(j).getNodeName())) {

					try {
						gsinfo.setGs_name(gsinfos.item(j).getFirstChild().getNodeValue());
					} catch (Exception e) {
					}

				}

			}
			result.add(gsinfo);
		}

		return result;
	}

	public List<GPrice> getJDDetialByXML(String url) throws Exception {
		List<GPrice> result = new ArrayList<GPrice>();

		URL inforUrl = new URL(url);
		URLConnection ucon = inforUrl.openConnection();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		InputStream input = ucon.getInputStream();

		Document doc = builder.parse(input);
		NodeList quotes = doc.getElementsByTagName("QUOTE");
		for (int i = 0; i < quotes.getLength(); i++) {

			GPrice gPrice = new GPrice();
			Element item = (Element) quotes.item(i);

			NodeList gPrices = item.getChildNodes();
			for (int j = 0; j < gPrices.getLength(); j++) {
				if ("ID".equals(gPrices.item(j).getNodeName())) {
					gPrice.setId(gPrices.item(j).getFirstChild().getNodeValue());
				} else if ("BRANDID".equals(gPrices.item(j).getNodeName())) {
					gPrice.setBrandid(gPrices.item(j).getFirstChild().getNodeValue());
				} else if ("PRODUCT".equals(gPrices.item(j).getNodeName())) {
					gPrice.setProduct(gPrices.item(j).getFirstChild().getNodeValue());
				} else if ("PRICE".equals(gPrices.item(j).getNodeName())) {
					gPrice.setPrice(gPrices.item(j).getFirstChild().getNodeValue());
				} else if ("DEGREE".equals(gPrices.item(j).getNodeName())) {
					gPrice.setDegree(gPrices.item(j).getFirstChild().getNodeValue());
				} else if ("UPDATETIME".equals(gPrices.item(j).getNodeName())) {
					gPrice.setUpdatetime(gPrices.item(j).getFirstChild().getNodeValue());
				} else if ("CREATETIME".equals(gPrices.item(j).getNodeName())) {
					gPrice.setCreatetime(gPrices.item(j).getFirstChild().getNodeValue());
				} else if ("CHANGE".equals(gPrices.item(j).getNodeName())) {
					gPrice.setChange(gPrices.item(j).getFirstChild().getNodeValue());
				}

			}
			result.add(gPrice);
		}

		return result;
	}

	public synchronized List<RMBprice> getRMBprice(String url) {
		List<RMBprice> result = new ArrayList<RMBprice>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}

				String builder_str = null;

				// json在2.3 解析时 100%是因为UTF-8的BOM头
				if (builder != null && builder.toString().startsWith("\ufeff")) {
					builder_str = builder.toString().substring(1);
				}

				JSONArray arrayJson = new JSONArray(builder_str.trim());

				RMBprice calData0 = new RMBprice();
				calData0.setCode("CNY");
				calData0.setMoney("100");
				calData0.setName("人民币");
				calData0.setRmb("100");
				calData0.setRate(1.00f);
				// result.add(calData0);

				for (int i = 0; i < arrayJson.length(); i++) {
					RMBprice calData = new RMBprice();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					calData.setCode(getJsonValue(temp, Const.RMB_CODE));
					calData.setMoney(getJsonValue(temp, Const.RMB_MONEY));
					calData.setName(getJsonValue(temp, Const.RMB_NAME));
					calData.setRmb(getJsonValue(temp, Const.RMB_RMB));
					calData.setRate(Float.parseFloat(df2.format(100.0f * Float.parseFloat(calData.getMoney()) / Float.parseFloat(calData.getRmb()))));
					result.add(calData);
				}
				httpRequest.abort();
				bufferedReader2.close();
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	// etf
	public List<ETF> getETF_ByXML(String url) throws Exception {

		List<ETF> result = new ArrayList<ETF>();

		URL inforUrl = new URL(url);
		URLConnection ucon = inforUrl.openConnection();
		ucon.setRequestProperty("accept", "text/xml");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		InputStream input = ucon.getInputStream();
		// InputStreamReader streamReader = new InputStreamReader(input,
		// "UTF-8");
		// InputSource inputSource = new InputSource(streamReader);
		Document doc = builder.parse(input);
		NodeList items = doc.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			ETF inforPojo = new ETF();
			Element item = (Element) items.item(i);
			inforPojo.setTime(getData(item, "time"));
			// Log.i("etf", getData(item, "time"));

			inforPojo.setVal(getData(item, "val"));
			// Log.i("etf", getData(item, "val"));

			inforPojo.setPreVal(getData(item, "preVal"));
			// Log.i("etf", getData(item, "preVal"));

			inforPojo.setAmplitude(getData(item, "amplitude"));
			// Log.i("etf", getData(item, "amplitude"));

			result.add(inforPojo);
		}

		return result;
	}

	public HT_AD getHT_AD(String url) {
		List<PriceData> result = new ArrayList<PriceData>();
		HttpGet httpRequest = new HttpGet(url);
		HT_AD ht_AD = new HT_AD();
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}

				// JSONArray arrayJson = new JSONArray(builder.toString());
				JSONTokener jsonTokener = new JSONTokener(builder.toString());
				JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

				// for (int i = 0; i < arrayJson.length(); i++) {
				JSONObject temp = jsonObject;
				ht_AD.setVersion(getJsonValue(temp, Const.HT_AD_VERSION));
				// Log.i("fx678v2-util", ht_AD.getVersion());

				ht_AD.setAndroid_show(getJsonValue(temp, Const.HT_AD_ANDROID_SHOW));
				// Log.i("fx678v2-util", ht_AD.getAndroid_show());

				ht_AD.setAndroid_url(getJsonValue(temp, Const.HT_AD_ANDROID_URL));
				// Log.i("fx678v2-util", ht_AD.getAndroid_url());

				ht_AD.setAndroidpad_show(getJsonValue(temp, Const.HT_AD_ANDROIDPAD_SHOW));
				// Log.i("fx678v2-util", ht_AD.getAndroidpad_show());

				ht_AD.setAndroidpad_url(getJsonValue(temp, Const.HT_AD_ANDROIDPAD_URL));
				// Log.i("fx678v2-util", ht_AD.getAndroidpad_url());

				ht_AD.setDescribe_modify_date(getJsonValue(temp, Const.HT_AD_DESCRIBE_MODIFY_DATE));
				// Log.i("fx678v2-util", ht_AD.getDescribe_modify_date());

				ht_AD.setDescribe_expire_date(getJsonValue(temp, Const.HT_AD_DESCRIBE_EXPIRE_DATE));
				// Log.i("fx678v2-util", ht_AD.getDescribe_expire_date());
				// }
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ht_AD;
	}

	/** 解析出来的title */
	private String title;
	/** 解析出来的imgurl_android */
	private String imgurl_android;
	/** 解析出来的linkurl */
	private String linkurl;
	/** 解析出来的version */
	private String version_mintai;

	/**
	 * 解析输入流,保存到Sharep
	 * 
	 * @param inputStream
	 */
	public void getADfromInputStream(InputStream inputStream, SharedPreferences sharedPreferences) {
		XmlPullParser parser = Xml.newPullParser();

		Editor editor = sharedPreferences.edit();
		try {
			parser.setInput(inputStream, "utf-8");

			// 获取事件类型
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					// 当前事件是文档开始事件
					// 文档开始时初始化list
					break;

				case XmlPullParser.START_TAG:
					// 当前事件是标签元素开始事件
					if (parser.getName().equals("title")) {

						title = parser.nextText();
						// log.i("title" + title);
						editor.putString("title", title);
						editor.commit();

					} else if (parser.getName().equals("imgurl_android")) {
						imgurl_android = parser.nextText();
						// log.i("imgurl_android" + imgurl_android);
						editor.putString("imgurl_android", imgurl_android);
						editor.commit();

					} else if (parser.getName().equals("linkurl")) {

						linkurl = parser.nextText();
						// log.i("linkurl" + linkurl);
						editor.putString("linkurl", linkurl);
						editor.commit();
					} else if (parser.getName().equals("version")) {
						version_mintai = parser.nextText();
						// log.i("version" + version_mintai);
						editor.putString("version", version_mintai);
						editor.commit();
					}
					break;
				case XmlPullParser.TEXT:

					break;
				case XmlPullParser.END_TAG:
					// 当前事件是标签元素结束事件
					if (parser.getName().equals("row")) {
						// person标签结束 添加到list并设空person对象
					}
					break;
				default:
					break;
				}
				// 进入下一个元素并触发相应事件
				event = parser.next();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized List<HT_AD2> getHT_AD2(String url) {
		List<HT_AD2> result = new ArrayList<HT_AD2>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String builder_str = null;

				// json在2.3 解析时 100%是因为UTF-8的BOM头
				// if (builder != null &&
				// builder.toString().startsWith("\ufeff")) {
				// builder_str = builder.toString().substring(1);
				// }
				// Log.i("temp", "进来解析3");

				// Log.i("temp", builder.toString().trim());
				JSONArray arrayJson = new JSONArray(builder.toString().trim());

				for (int i = 0; i < arrayJson.length(); i++) {
					HT_AD2 calData = new HT_AD2();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					calData.setKey(getJsonValue(temp, Const.HT_AD2_KEY));
					calData.setUrl(getJsonValue(temp, Const.HT_AD2_URL));
					result.add(calData);
				}
				httpRequest.abort();
				bufferedReader2.close();
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 名师讲堂分类
	 * 
	 * 解析Json数据
	 * 
	 * @param urlPath
	 * @return mlists
	 * @throws Exception
	 */
	public static List<PointBean> getListPerson(String urlPath) throws Exception {
		List<PointBean> mlists = new ArrayList<PointBean>();
		byte[] data = readParse(urlPath);

		JSONArray jsonArray = new JSONArray(new String(data));
		for (int i = 0; i < jsonArray.length(); i++) {
			PointBean pointBean 	= new PointBean();
			JSONObject obeject 		= jsonArray.getJSONObject(i);
			String id 				= obeject.getString("vtid");
			pointBean.setVtid(id);
			String name 			= obeject.getString("vtname");
			pointBean.setVtname(name);
			System.out.println("-----" + id + "," + name);
			mlists.add(pointBean);
		}
		return mlists;
	}

	/**
	 * 从指定的url中获取字节数组
	 * 
	 * @param urlPath
	 * @return 字节数组
	 * @throws Exception
	 */
	public static byte[] readParse(String urlPath) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		URL url = new URL(urlPath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		InputStream inStream = conn.getInputStream();
		while ((len = inStream.read(data)) != -1) {
			outStream.write(data, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * 老师列表
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public List<PointBean> getAccountApplyResult_post(String url) throws Exception {
		List<PointBean> mlists 	= new ArrayList<PointBean>();
		HttpPost httppost 		= new HttpPost(url);
		httppost.addHeader("Context-Type", "text/html");
		httppost.addHeader("charset", HTTP.UTF_8);
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr = builder.toString();
				JSONArray jsonArray = new JSONArray(reslutstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					PointBean pointBean 		= new PointBean();
					JSONObject jsonObject 		= jsonArray.getJSONObject(i);
					String uid 					= jsonObject.getString("uid");
					String username 			= jsonObject.getString("username");
					String intro 				= jsonObject.getString("intro");

					pointBean.setUid(uid);
					pointBean.setUsername(username);
					pointBean.setIntro(intro);
					Log.i("temp", "uid:" + uid + " username:" + username + " intro:" + intro);
					mlists.add(pointBean);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mlists;
	}
	
	/**
	 * 老师列表
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public List<PointBean> getTeacherResult_post(String url) throws Exception {
		List<PointBean> mlists 	= new ArrayList<PointBean>();
		HttpPost httppost 		= new HttpPost(url);
		httppost.addHeader("Context-Type", "text/html");
		httppost.addHeader("charset", HTTP.UTF_8);
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr = builder.toString();
				JSONArray jsonArray = new JSONArray(reslutstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					PointBean pointBean 		= new PointBean();
					JSONObject jsonObject 		= jsonArray.getJSONObject(i);
					String uid 					= jsonObject.getString("uid");
					String name 				= jsonObject.getString("name");
					String intro 				= jsonObject.getString("intro");
					String avatar 				= jsonObject.getString("avatar");

					pointBean.setUid(uid);
					pointBean.setName(name);
					pointBean.setIntro(intro);
					pointBean.setAvatar(avatar);
					Log.i("temp", "uid:" + uid + " name:" + name + " intro:" + intro + "avatar:" + avatar);
					mlists.add(pointBean);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mlists;
	}

	/**
	 * 视频列表
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public List<PointBean> getVideoResult_post(String url) throws Exception {
		List<PointBean> mlists = new ArrayList<PointBean>();
		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("Context-Type", "text/html");
		httppost.addHeader("charset", HTTP.UTF_8);
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr = builder.toString();
				JSONArray jsonArray = new JSONArray(reslutstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					PointBean pointBean 		= new PointBean();
					JSONObject jsonObject 		= jsonArray.getJSONObject(i);
					String id 					= jsonObject.getString("id");
					String subject 				= jsonObject.getString("subject");
					String appaddr 				= jsonObject.getString("appaddr");
					String dateline 			= jsonObject.getString("dateline");
					String teacheruid 			= jsonObject.getString("teacheruid");
					String ttype 				= jsonObject.getString("ttype");
					String passwd 				= jsonObject.getString("passwd");

					pointBean.setId(id);
					pointBean.setSubject(subject);
					pointBean.setAppaddr(appaddr);
					pointBean.setDateline(DateUtils.getDateToString(dateline));
					pointBean.setTeacheruid(teacheruid);
					pointBean.setTtype(ttype);
					pointBean.setPasswd(passwd);
					Log.i("temp", "id:" + id + " subject:" + subject + " appaddr:" + appaddr + " dateline:" + DateUtils.getDateToString(dateline) + " teacheruid:" + teacheruid + "ttype:" + ttype + "passwd:" + passwd);
					mlists.add(pointBean);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mlists;
	}

	/**
	 * 获取验证码
	 * 
	 * @param url
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public ShiPanLogin getVerificationCodePost(String url, String mobile) throws Exception {
		ShiPanLogin shiPanLogin = new ShiPanLogin();
		HttpPost httppost = new HttpPost(url);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
			httppost.addHeader("Context-Type", "text/html");
			httppost.addHeader("charset", HTTP.UTF_8);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
		} catch (Exception e) {
		}
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr = builder.toString();
				Log.i("temp", "返回验证码的值：" + reslutstr);
				shiPanLogin.setCode(reslutstr);
				JSONArray jsonArray = new JSONArray(reslutstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject 		= jsonArray.getJSONObject(i);
					String Error 				= jsonObject.getString("Error");
					String Message 				= jsonObject.getString("Message");
					shiPanLogin.setError(Error);
					shiPanLogin.setMessage(Message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shiPanLogin;
	}

	/**
	 * 用户注册
	 * 
	 * @param url
	 * @param nickname
	 * @param username
	 * @param mobilecode
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public ShiPanLogin getRegister_post(String url, String username, String mobilecode, String password) throws Exception {
		ShiPanLogin shiPanLogin = new ShiPanLogin();
		HttpPost httppost = new HttpPost(url);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("mobilecode", mobilecode));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			httppost.addHeader("Context-Type", "text/html");
			httppost.addHeader("charset", HTTP.UTF_8);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
		} catch (Exception e) {
		}
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr = builder.toString();
				Log.i("temp", reslutstr);
//				shiPanLogin.setZhuCeValue(reslutstr);
				JSONArray jsonArray = new JSONArray(reslutstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject 		= jsonArray.getJSONObject(i);
					String Error 				= jsonObject.getString("Error");
					String Message 				= jsonObject.getString("Message");
					shiPanLogin.setError(Error);
					shiPanLogin.setMessage(Message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shiPanLogin;
	}

	/**
	 * 找回密码
	 * 
	 * @param url
	 * @param username
	 * @param mobilecode
	 * @param newpasswd
	 * @return
	 * @throws Exception
	 */
	public ShiPanLogin getFindPassword_post(String url, String username, String mobilecode, String newpasswd) throws Exception {
		ShiPanLogin shiPanLogin = new ShiPanLogin();
		HttpPost httppost = new HttpPost(url);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("mobilecode", mobilecode));
			nameValuePairs.add(new BasicNameValuePair("newpasswd", newpasswd));
			httppost.addHeader("Context-Type", "text/html");
			httppost.addHeader("charset", HTTP.UTF_8);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
		} catch (Exception e) {
		}
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr = builder.toString();
				Log.i("temp", reslutstr);
//				shiPanLogin.setFindPassword(reslutstr);
				JSONArray jsonArray = new JSONArray(reslutstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject 		= jsonArray.getJSONObject(i);
					String Error 				= jsonObject.getString("Error");
					String Message 				= jsonObject.getString("Message");
					shiPanLogin.setError(Error);
					shiPanLogin.setMessage(Message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shiPanLogin;
	}

	/**
	 * 登录
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public ShiPanLogin getLogin_post(String url, String username, String password) throws Exception {
		ShiPanLogin shiPanLogin = new ShiPanLogin();
		HttpPost httppost = new HttpPost(url);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			httppost.addHeader("Context-Type", "text/html");
			httppost.addHeader("charset", HTTP.UTF_8);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
		} catch (Exception e) {
		}
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr = builder.toString();
				shiPanLogin.setLogin(reslutstr);
					JSONArray jsonArray1 = new JSONArray(reslutstr);
					for (int i = 0; i < jsonArray1.length(); i++) {
						JSONObject jsonObject 		= jsonArray1.getJSONObject(i);
						boolean Error 				= jsonObject.has("Error");
						Log.i("BOOLEAN", "有没有？" + Error);
						if (Error != true) {
							String groupid 				= jsonObject.getString("groupid");
							String grouptitle 			= jsonObject.getString("grouptitle");
							String grade 				= jsonObject.getString("grade");
							String roomlevel 			= jsonObject.getString("roomlevel");
							String cookieid 			= jsonObject.getString("cookieid");
							String uid 					= jsonObject.getString("uid");
							shiPanLogin.setGroupid(groupid);
							shiPanLogin.setGrouptitle(grouptitle);
							shiPanLogin.setGrade(grade);
							shiPanLogin.setRoomlevel(roomlevel);
							shiPanLogin.setCookieid(cookieid);
							shiPanLogin.setUid(uid);
						} else {
							String error 				= jsonObject.getString("Error");
							String Message 				= jsonObject.getString("Message");
							Log.i("temp", "error返回的数据：" + error + "Message返回的数据：" + Message);
							shiPanLogin.setError(error);
							shiPanLogin.setMessage(Message);
						}
						Log.i("temp", "登录返回的数据：" + shiPanLogin.toString());
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shiPanLogin;
	}

	/**
	 * 直播室信息接口
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public List<ShiPanTanGuBean> getTanGu_post(String url) throws Exception {
		List<ShiPanTanGuBean> tanGuBeanlists = new ArrayList<ShiPanTanGuBean>();
		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("Context-Type", "text/html");
		httppost.addHeader("charset", HTTP.UTF_8);
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder 			= new StringBuilder();
				BufferedReader bufferedReader2 	= new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr 				= builder.toString();
				Log.i("LOG", "这里的类型：" + reslutstr);
				JSONArray jsonArray 			= new JSONArray(reslutstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					ShiPanTanGuBean shiPanTanGuBean = new ShiPanTanGuBean();
					JSONObject jsonObject 		= jsonArray.getJSONObject(i);
					String rid 					= jsonObject.getString("rid");
					String name 				= jsonObject.getString("name");
					String authority 			= jsonObject.getString("authority");
					String username 			= jsonObject.getString("username");
					String subject 				= jsonObject.getString("subject");
					String point 				= jsonObject.getString("point");
					String public_notice 		= jsonObject.getString("public_notice");
					String status 				= jsonObject.getString("status");

					shiPanTanGuBean.setRid(rid);
					shiPanTanGuBean.setName(name);
					shiPanTanGuBean.setAuthority(authority);
					shiPanTanGuBean.setUsername(username);
					shiPanTanGuBean.setSubject(subject);
					shiPanTanGuBean.setPoint(point);
					shiPanTanGuBean.setPublic_notice(public_notice);
					shiPanTanGuBean.setStatus(status);
					Log.i("temp", "rid-->" + rid + "\n"
							+ "name-->" + name + "\n"
							+ "authority-->" + authority + "\n"
							+ "username-->" + username + "\n"
							+ "subject-->" + subject + "\n"
							+ "point-->" + point + "\n"
							+ "public_notice-->" + public_notice + "\n"
							+ "status-->" + status);
					tanGuBeanlists.add(shiPanTanGuBean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return tanGuBeanlists;
	}
	
	/**
	 * 直播室状态开关
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String getStatus_post(String url) throws Exception {
		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("Context-Type", "text/html");
		httppost.addHeader("charset", HTTP.UTF_8);
		String reslutstr = "";
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder 			= new StringBuilder();
				BufferedReader bufferedReader2 	= new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				reslutstr 						= builder.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return reslutstr;
	}
	
	/**
	 * 直播室用户评论
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public List<ShiPanSeedingBean> getSeeding_post(String url) throws Exception {
		List<ShiPanSeedingBean> seedingBeanlist = new ArrayList<ShiPanSeedingBean>();

		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("Context-Type", "text/html");
		httppost.addHeader("charset", HTTP.UTF_8);
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder 			= new StringBuilder();
				BufferedReader bufferedReader2 	= new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr 				= builder.toString();
				JSONArray jsonArray 			= new JSONArray(reslutstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					ShiPanSeedingBean shiPanSeedingBean = new ShiPanSeedingBean();
					JSONObject jsonObject 		= jsonArray.getJSONObject(i);
					String uid 					= jsonObject.getString("uid");
					String cid 					= jsonObject.getString("cid");
					String message 				= jsonObject.getString("message");
					String dateline 			= jsonObject.getString("dateline");
					String avatar_middle 		= jsonObject.getString("avatar_middle");
					String avatar_small 		= jsonObject.getString("avatar_small");
					String showname 			= jsonObject.getString("showname");
					String c_showname 			= jsonObject.getString("c_showname");

					shiPanSeedingBean.setUid(uid);
					shiPanSeedingBean.setCid(cid);
					shiPanSeedingBean.setMessage(message);
					shiPanSeedingBean.setDateline(dateline);
					shiPanSeedingBean.setAvatar_middle(avatar_middle);
					shiPanSeedingBean.setAvatar_small(avatar_small);
					shiPanSeedingBean.setShowname(showname);
					shiPanSeedingBean.setC_showname(c_showname);
					
					Log.i("temp", shiPanSeedingBean.toString());
					seedingBeanlist.add(shiPanSeedingBean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return seedingBeanlist;
	}
	
	/**
	 * 直播
	 * @param url
	 * @param roomid
	 * @return
	 * @throws Exception
	 */
	public List<ShiPanSeedingBean> getZhiBo_post(String url) throws Exception {
		List<ShiPanSeedingBean> seedingBeanlist = new ArrayList<ShiPanSeedingBean>();

		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("Context-Type", "text/html");
		httppost.addHeader("charset", HTTP.UTF_8);
		
		try {
			HttpResponse response 				= new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder 			= new StringBuilder();
				BufferedReader bufferedReader2 	= new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr 			= builder.toString();
				JSONArray jsonArray 		= new JSONArray(reslutstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					ShiPanSeedingBean shiPanSeedingBean = new ShiPanSeedingBean();
					JSONObject jsonObject 	= jsonArray.getJSONObject(i);
					String recordid 		= jsonObject.getString("recordid");
					String rid1 			= jsonObject.getString("rid");
					String lid 				= jsonObject.getString("lid");
					String uid 				= jsonObject.getString("uid");
					String message 			= jsonObject.getString("message");
					String is_shield 		= jsonObject.getString("is_shield");
					String is_wonderful 	= jsonObject.getString("is_wonderful");
					String is_call 			= jsonObject.getString("is_call");
					String level 			= jsonObject.getString("level");
					String is_system 		= jsonObject.getString("is_system");
					String dateline 		= jsonObject.getString("dateline");
					String authority 		= jsonObject.getString("authority");
					String avatar_middle 	= jsonObject.getString("avatar_middle");
					String avatar_small 	= jsonObject.getString("avatar_small");
					String showname 		= jsonObject.getString("showname");
					String c_showname 		= jsonObject.getString("c_showname");
					
					shiPanSeedingBean.setRecordid(recordid);
					shiPanSeedingBean.setRid(rid1);
					shiPanSeedingBean.setLid(lid);
					shiPanSeedingBean.setUid(uid);
					shiPanSeedingBean.setMessage(message);
					shiPanSeedingBean.setIs_shield(is_shield);
					shiPanSeedingBean.setIs_wonderful(is_wonderful);
					shiPanSeedingBean.setIs_call(is_call);
					shiPanSeedingBean.setLevel(level);
					shiPanSeedingBean.setIs_system(is_system);
					shiPanSeedingBean.setDateline(dateline);
					shiPanSeedingBean.setAuthority(authority);
					shiPanSeedingBean.setAvatar_middle(avatar_middle);
					shiPanSeedingBean.setAvatar_small(avatar_small);
					shiPanSeedingBean.setShowname(showname);
					shiPanSeedingBean.setC_showname(c_showname);
					
					seedingBeanlist.add(shiPanSeedingBean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return seedingBeanlist;
	}
	
	/**
	 * 用户提交评论
	 * @return
	 * @throws Exception
	 */
	public ShiPanLogin getLiveComment_post(String url) throws Exception {
		ShiPanLogin shiPanLogin = new ShiPanLogin();
		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("Context-Type", "text/html");
		httppost.addHeader("charset", HTTP.UTF_8);
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr = builder.toString();
				JSONArray jsonArray = new JSONArray(reslutstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject 		= jsonArray.getJSONObject(i);
					String error = jsonObject.getString("Error");
					String message = jsonObject.getString("Message");
					shiPanLogin.setError(error);
					shiPanLogin.setMessage(message);
					Log.i("temp", "登录返回的数据：" + shiPanLogin.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shiPanLogin;
	}
	
	/**
	 * 申请开户
	 * @param url
	 * @param mobile
	 * @param mobilecode
	 * @param name
	 * @param uid
	 * @param cookieid
	 * @return
	 * @throws Exception
	 */
	public ShiPanLogin getApplyAccount_post(String url, String mobile, String mobilecode, String name, String uid, String cookieid) throws Exception {
		ShiPanLogin shiPanLogin = new ShiPanLogin();
		HttpPost httppost = new HttpPost(url);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
			nameValuePairs.add(new BasicNameValuePair("mobilecode", mobilecode));
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			nameValuePairs.add(new BasicNameValuePair("cookieid", cookieid));
			httppost.addHeader("Context-Type", "text/html");
			httppost.addHeader("charset", HTTP.UTF_8);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
		} catch (Exception e) {
		}
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr = builder.toString();
				JSONArray jsonArray = new JSONArray(reslutstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject 		= jsonArray.getJSONObject(i);
					String error 				= jsonObject.getString("Error");
					String Message 				= jsonObject.getString("Message");
					shiPanLogin.setError(error);
					shiPanLogin.setMessage(Message);
					Log.i("temp", "登录返回的数据：" + shiPanLogin.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shiPanLogin;
	}
	
	/**
	 * 赢家内参
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public WinnerInside getWinnerInside_post(String url) throws Exception {
		WinnerInside winnerInside = new WinnerInside();
		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("Context-Type", "text/html");
		httppost.addHeader("charset", HTTP.UTF_8);
		try {
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder 			= new StringBuilder();
				BufferedReader bufferedReader2 	= new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				String reslutstr 				= builder.toString();
				JSONArray jsonArray 			= new JSONArray(reslutstr);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject 		= jsonArray.getJSONObject(i);
					String doc_url 				= jsonObject.getString("doc_url");
					winnerInside.setDoc_url(doc_url);
				}
			}
		} catch(SocketException e){
			ExceptionBean exceptionBean = new ExceptionBean();
			String exception = "加载失败，请检查您的网络连接是否正常！";
			exceptionBean.setException(exception);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return winnerInside;
	}
}
