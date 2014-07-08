package com.hzwydyj.finace.data;

import java.text.DecimalFormat;

import com.hzwydyj.finace.utils.MyLogger;

import android.graphics.Color;

public class Const {
	
	public static final String LISTVIEW_PREFERENCES = "listview_preferences"; 
	
	public static final String PREFERNCES_SHIPAN = "ShiPanLogin";

	public static final int TABHOME_VIEWPAGER_NUMBER = 1;
	public static final int TABHOME_VIEW_NUMBER = 2;

	public static final String RMB_NAME = "name";
	public static final String RMB_CODE = "code";
	public static final String RMB_MONEY = "money";
	public static final String RMB_RMB = "rmb";

	public static final DecimalFormat df4 = new DecimalFormat("0.0000");
	public static final DecimalFormat df3 = new DecimalFormat("0.000");
	public static final DecimalFormat df2 = new DecimalFormat("0.00");
	public static final DecimalFormat df1 = new DecimalFormat("0.0");
	public static final DecimalFormat df0 = new DecimalFormat("0");

	public static final String PREFERENCES_NAME = "optionaldata";
	public static final String PREF_OPTIONAL = "prefoptional";
	public static final String PREF_OPTIONAL_NAME = "prefoptionalname";
	public static final String PREF_EX = "prefex";
	public static final String PREF_SELECTED = "optional_selected";

	public static final String TIME_MIN = "MM/dd/yyyy HH:mm";
	public static final String TIME_SEC = "MM/dd/yyyy HH:mm:ss";
	public static final String TIME_HMS = "HH:mm:ss";
	public static final String TIME_YD = "MM/dd/yyyy";

	// 列表数据
	// public static final String URL_MARKET =
	// "http://m.fx678.com/quotelist.aspx?key=PM_key";
	// public static final String URL_MARKET = "http://mhth.fx678.com/quotelist.aspx?key=PM_key";
	// 单条数据
	// public static final String URL_NOW =
	// "http://mhth.fx678.com/SingleNewQuote.aspx?ex=PM_ex&code=PM_code&date=PM_date&count=PM_count";
	// 分时数据
	// public static final String URL_TIMEDATA = "http://mhth.fx678.com/TickChart.aspx?ex=PM_ex&code=PM_code";
	// K线数据
	// public static final String URL_KDATA =
	// "http://mhth.fx678.com/CandleChart.aspx?ex=PM_ex&code=PM_code&type=PM_type&count=PM_count";
	// public static final String URL_DIY = "http://mhth.fx678.com/diy.aspx?code=PM_code";

	public static final String URL_MARKET = "http://m.fx678.com/quotelist.aspx?key=PM_key";
	public static final String URL_NOW = "http://m.fx678.com/SingleNewQuote.aspx?ex=PM_ex&code=PM_code&date=PM_date&count=PM_count";
	public static final String URL_TIMEDATA = "http://m.fx678.com/TickChart.aspx?ex=PM_ex&code=PM_code";
	public static final String URL_KDATA = "http://m.fx678.com/CandleChart.aspx?ex=PM_ex&code=PM_code&type=PM_type&count=PM_count";
	public static final String URL_DIY = "http://m.fx678.com/diy.aspx?code=PM_code";

	public static final String URL_KEY = "PM_key";
	public static final String URL_EX = "PM_ex";
	public static final String URL_CODE = "PM_code";
	public static final String URL_DATE = "PM_date";
	public static final String URL_COUNT = "PM_count";
	public static final String URL_TYPE = "PM_type";

	public static final String UDP_IP = "m1.fx678.com";
	// public static final String UDP_IP = "mht.fx678.com";
	public static final int UDP_PORT = 21134;
	// public static final int UDP_PORT = 21135;
	public static final String UDP_PARA = "login,";
	public static final String UDP_DIY = "diy,";

	public static final int K_DOWN = Color.rgb(84, 252, 252);
	public static final int MA_ZISE = Color.rgb(255, 0, 255);

	public static final String YGY = "ygy";
	public static final String SHGOLD = "shgold";
	public static final String HJXH = "hjxh";
	public static final String STOCKINDEX = "stockindex";
	public static final String WH = "wh";
	public static final String NYMEX = "nymex";
	public static final String COMEX = "comex";
	public static final String IPE = "ipe";
	public static final String TTJ = "ttj";
	public static final String SHQH = "shqh";
	public static final String SHGOLD_NAME = "上海黄金";
	public static final String HJXH_NAME = "国际黄金";
	public static final String YGY_NAME = "粤贵银";
	public static final String STOCKINDEX_NAME = "全球股指";
	public static final String WH_NAME = "外汇";
	public static final String NYMEX_NAME = "NYME原油";
	public static final String COMEX_NAME = "COMEX期金";
	public static final String IPE_NAME = "IPE原油";
	public static final String TTJ_NAME = "天交所";
	public static final String SHQH_NAME = "上海期货";

	public static final String SHGOLD_PERIOD = "21:00-15:30-2:30-/9:00";
	public static final String HJXH_PERIOD = "06:00-06:00";
	public static final String STOCKINDEX_PERIOD = "09:00-15:30";
	public static final String WH_PERIOD = "06:00-06:00";
	public static final String NYMEX_PERIOD = "06:00-05:00";
	public static final String COMEX_PERIOD = "21:10-21:00";
	public static final String IPE_PERIOD = "18:00-06:00";
	public static final String TTJ_PERIOD = "06:00-04:00";
	public static final String YGY_PERIOD = "06:00-04:00";

	public static final String AOI_PERIOD = "07:00-14:00";
	public static final String FTSEDAXCAC40_PERIOD = "15:00-00:30";
	public static final String NASDAQDJIASP500CRB_PERIOD = "21:30-05:00";
	public static final String NIKKIKSE_PERIOD = "08:00-14:00";
	public static final String STIKLSE_PERIOD = "09:00-17:00";
	public static final String PCOMP_PERIOD = "09:30-15:00";
	public static final String TWI_PERIOD = "09:00-13:30";
	public static final String SET_PERIOD = "11:00-17:30";
	public static final String SENSEX_PERIOD = "10:30-17:00";
	public static final String CHINA_PERIOD = "09:30-15:00-11:30-13:00";
	public static final String SHQH_PERIOD = "09:00-15:00-11:30-13:30";

	public static final String SHGOLD1 = "1";
	public static final String HJXH2 = "2";
	public static final String STOCKINDEX3 = "3";
	public static final String WH4 = "4";
	public static final String NYMEX5 = "5";
	public static final String COMEX6 = "6";
	public static final String IPE7 = "7";
	public static final String TTJ8 = "8";
	public static final String SHQH9 = "9";
	public static final String YGY10 = "10";

	public static final String INDEX_MACD = "MACD";
	public static final String INDEX_VOL = "VOL";
	public static final String INDEX_RSI = "RSI";
	public static final String INDEX_BOLL = "BOLL";
	public static final String INDEX_KDJ = "KDJ";
	public static final String INDEX_OBV = "OBV";
	public static final String INDEX_CCI = "CCI";
	public static final String INDEX_PSY = "PSY";

	public static final String CAL_TIME = "Time";
	public static final String CAL_COUNTRY = "Country";
	public static final String CAL_ITEM = "Item";
	public static final String CAL_IMPORTANCE = "Importance";
	public static final String CAL_LASTVALUE = "LastValue";
	public static final String CAL_PREDICTION = "Prediction";
	public static final String CAL_ACTUAL = "Actual";

	public static final String K_TIME = "time";
	public static final String K_NEW = "new";
	public static final String K_DATE = "date";
	public static final String K_CLOSE = "close";
	public static final String K_OPEN = "open";
	public static final String K_HIGH = "high";
	public static final String K_LOW = "low";
	public static final String K_AVERAGE = "average";
	public static final String K_LASTCLOSE = "lastclose";
	public static final String K_MONEY = "money";
	public static final String K_VOLUME = "volume";
	public static final String K_TOTAL = "total";

	public static final String ABNAME = "Abname";
	public static final String AB = "Ab";
	public static final String ABLOT = "Ablot";

	public static final String FENBI_TIME = "time";
	public static final String FENBI_PRICE = "fprice";
	public static final String FENBI_VOLUME = "fvolume";

	public static final String PRICE_CODE = "Code";
	public static final String PRICE_NAME = "Name";
	public static final String PRICE_QUOTETIME = "QuoteTime";
	public static final String PRICE_LOW = "Low";
	public static final String PRICE_LAST = "Last";
	public static final String PRICE_CLOSE = "Close";
	public static final String PRICE_SETTLE = "Settle";
	public static final String PRICE_POSI = "Posi";
	public static final String PRICE_UPDOWN = "UpDown";
	public static final String PRICE_UPDOWNRATE = "UpDownRate";
	public static final String PRICE_AVERAGE = "Average";
	public static final String PRICE_SEQUENCENO = "SequenceNo";
	public static final String PRICE_HIGH = "High";
	public static final String PRICE_HIGHLIMIT = "HighLimit";
	public static final String PRICE_LASTCLOSE = "LastClose";
	public static final String PRICE_LASTSETTLE = "LastSettle";
	public static final String PRICE_LOWLIMIT = "LowLimit";
	public static final String PRICE_OPEN = "Open";
	public static final String PRICE_TURNOVER = "TurnOver";
	public static final String PRICE_VOLUME = "Volume";
	public static final String PRICE_TOTAL = "Total";
	public static final String PRICE_WEIGHT = "Weight";
	public static final String PRICE_ASK1 = "Ask1";
	public static final String PRICE_ASK2 = "Ask2";
	public static final String PRICE_ASK3 = "Ask3";
	public static final String PRICE_ASK4 = "Ask4";
	public static final String PRICE_ASK5 = "Ask5";
	public static final String PRICE_ASKLOT1 = "AskLot1";
	public static final String PRICE_ASKLOT2 = "AskLot2";
	public static final String PRICE_ASKLOT3 = "AskLot3";
	public static final String PRICE_ASKLOT4 = "AskLot4";
	public static final String PRICE_ASKLOT5 = "AskLot5";
	public static final String PRICE_BID1 = "Bid1";
	public static final String PRICE_BID2 = "Bid2";
	public static final String PRICE_BID3 = "Bid3";
	public static final String PRICE_BID4 = "Bid4";
	public static final String PRICE_BID5 = "Bid5";
	public static final String PRICE_BIDLOT1 = "BidLot1";
	public static final String PRICE_BIDLOT2 = "BidLot2";
	public static final String PRICE_BIDLOT3 = "BidLot3";
	public static final String PRICE_BIDLOT4 = "BidLot4";
	public static final String PRICE_BIDLOT5 = "BidLot5";
	public static final String PRICE_TTJBUY = "Buy";
	public static final String PRICE_TTJSELL = "Sell";
	public static final String PRICE_DECIMAL = "Decimal";
	public static final String PRICE_TTJAMLITUDE = "Amplitude";

	public static final String AUT_D = "AuT+D";
	public static final String AGT_D = "AgT+D";
	public static final String USD = "USD";
	public static final String USDJPY = "USDJPY";
	public static final String A0001 = "1A0001";
	public static final String A01 = "2A01";
	public static final String AOI = "AOI";
	public static final String FTSE = "FTSE";
	public static final String DAX = "DAX";
	public static final String CAC40 = "CAC40";
	public static final String NASDAQ = "NASDAQ";
	public static final String DJIA = "DJIA";
	public static final String SP500 = "SP500";
	public static final String CRB = "CRB";
	public static final String SET = "SET";
	public static final String SENSEX = "SENSEX";
	public static final String NIKKI = "NIKKI";
	public static final String KSE = "KSE";
	public static final String STI = "STI";
	public static final String TWI = "TWI";
	public static final String KLSE = "KLSE";
	public static final String PCOMP = "PCOMP";

	public final static String PREFERENCESNEWS_NAME = "newsTypePref0821";
	public final static String UPDATE = "updateTime";
	public final static String VERSION = "version";
	public final static String MARKET_UPDATE = "marketUpdateTime";
	public final static String SOFT_UPDATE = "softUpdateTime";
	public final static String UPDATE_URL = "updateUrl";
	public final static String SOFT_UPDATE_URL = "softUpdateUrl";
	public final static String NEWS_NUM = "news_";
	public final static String NEWS_COUNT = "newsCount";
	public final static String MARKET_NUM = "market_";
	public final static String MARKET_COUNT = "marketCount";
	public final static String ABOUT = "汇通财经 \n汇通财经 www.fx678.com\n版本号：V";
	public final static String VERSIONCODE = "v1.1.1";

	/** 滚动播报 */
	public final static String URL_NEWS_GDBB = "http://tool.fx678.com/mob/source/yaowensudi.xml";
	/** 财经要闻 */
	public final static String URL_NEWS_CJYW = "http://tool.fx678.com/mob/source/caijingyaowen.xml";
	/** 全球股市 */
	public final static String URL_NEWS_QQGS = "http://tool.fx678.com/mob/source/gushixinwen.xml";
	/** 媒体头条 */
	public final static String URL_NEWS_MTTT = "http://tool.fx678.com/mob/source/meititoutiao.xml";
	/** 各国央行 */
	public final static String URL_NEWS_GGYH = "http://tool.fx678.com/mob/source/geguoyanghang.xml";
	/** 经济指标 */
	public final static String URL_NEWS_JJZB = "http://tool.fx678.com/mob/source/jingjizhibiao.xml";
	/** 外汇市场 */
	public final static String URL_NEWS_WHSC = "http://tool.fx678.com/mob/source/huishipinglun.xml";
	/** 黄金市场 */
	public final static String URL_NEWS_HJSC = "http://tool.fx678.com/mob/source/huangjinpinglun.xml";
	/** 原油市场 */
	public final static String URL_NEWS_YYSC = "http://tool.fx678.com/mob/source/yuanyoupinglun.xml";
	/** 理财投资 */
	public final static String URL_NEWS_LCTZ = "http://tool.fx678.com/mob/source/touzilicai.xml";
	/** 深度报道 */
	public final static String URL_NEWS_SDBD = "http://tool.fx678.com/mob/source/shendubaodao.xml";
	/** 交易策略 */
	public final static String URL_NEWS_JJCL = "http://tool.fx678.com/mob/source/jiaoyi.xml";
	/** 银行评论 */
	public final static String URL_NEWS_YHPL = "http://tool.fx678.com/mob/source/yinhang.xml";
	/** 专家评论 */
	public final static String URL_NEWS_ZJPL = "http://tool.fx678.com/mob/source/zhuanjia.xml";
	/** 机构评论 */
	public final static String URL_NEWS_JGPL = "http://tool.fx678.com/mob/source/jigou.xml";

	/** */
	public final static String BC_INTENT_ACTION_UDPREFLESH = "android.intent.action.udp_refresh";

	/** webview参数 */
	public final static String WEBVIEW_URL = "web_url";
	/** webview参数 */
	public final static String WEBVIEW_TITLE = "web_title";

	/** 链接地址 金牌分析师 */
//	public final static String URL_JPFXS = "http://t.fx678.com/index/livetrade/mobile/index.html";
	public final static String URL_KHZN = "http://118.244.200.199:16909/SelfOpenAccount/first.jsp?memNo=001";
	/** 华宗伟业*/
	public final static String HZWY_URL = "http://www.91baiyin.com.cn/app/interface.php?";
	/** 链接地址 汇通论坛 */
	public final static String URL_HTLT = "http://my.fx678.com/";
	/** 链接地址-汇通专家团 */
	public final static String URL_HTZJT = "http://online.fx678.com:8080/Index.aspx";

	public final static String APP_PACKETNAME = "com.hzwydyj.finace";
	public final static String APP_NAME = "大赢家";
	public final static String APP_Version = "V1.0.1";
	public final static String APP_VersionCheckURL = "http://m.fx678.com/Upgrade.aspx?ver=BEISDAYINGJIA_ANDROID_" + APP_Version;

	/** */
	public final static String RMB_EXCHANGE = "http://unews.fx678.com/union/RMBprice/RMBprice.html";

	/** 民泰广告地址 */
	public final static String AD_MINTA = "http://m.fx678.com/mintai/ad_fx678.xml";
	/** 汇通广告地址 */
	public final static String AD_HT = "http://app.fx678.com/client/ht/setAppLaunch.html";
	/** 汇通广告地址2 */
	public final static String AD_HT2 = "http://app.fx678.com/client/ht/app_market_advert.html";

	/** */
	public final static String URL_CALENDAR = "http://mhth.fx678.com/Calendar.aspx?date=";

	/** ETF-黄金 */
	public final static String URL_ETF_AU = "http://unews.fx678.com/union/HT/ETFAU.xml";
	/** ETF-白银 */
	public final static String URL_ETF_AG = "http://unews.fx678.com/union/HT/ETFAG.xml";

	/** 金店报价 */
	public final static String URL_GoldPriceList = "http://tool.fx678.com/mob/source/jdbj.asp";
	/** 金店报价-详细 */
	public final static String URL_GoldPriceDetail = "http://tool.fx678.com/mob/source/goldrealprice.asp?id=";

	/** 新闻详细-前缀 */
	public final static String URL_NEWS_PREFIX = "http://tool.fx678.com/mob/source/news.asp?id=";

	/** 汇通广告 */
	public final static String HT_AD_VERSION = "version";
	public final static String HT_AD_VERSION_SP = "ht_ad_version";
	/** 汇通广告 */
	public final static String HT_AD_ANDROID_SHOW = "android_show";
	public final static String HT_AD_ANDROID_SHOW_SP = "ht_ad_android_show";
	/** 汇通广告 */
	public final static String HT_AD_ANDROID_URL = "android_url";
	public final static String HT_AD_ANDROID_URL_SP = "ht_ad_android_url";
	/** 汇通广告 */
	public final static String HT_AD_ANDROIDPAD_SHOW = "androidpad_show";
	public final static String HT_AD_ANDROIDPAD_SHOW_SP = "ht_ad_androidpad_show";
	/** 汇通广告 */
	public final static String HT_AD_ANDROIDPAD_URL = "androidpad_url";
	public final static String HT_AD_ANDROIDPAD_URL_SP = "ht_ad_androidpad_url";
	/** 汇通广告 */
	public final static String HT_AD_DESCRIBE_MODIFY_DATE = "describe_modify_date";
	public final static String HT_AD_DESCRIBE_MODIFY_DATE_SP = "ht_ad_describe_modify_date";
	/** 汇通广告 */
	public final static String HT_AD_DESCRIBE_EXPIRE_DATE = "describe_expire_date";
	public final static String HT_AD_DESCRIBE_EXPIRE_DATE_SP = "ht_ad_describe_expire_date";

	/** 汇通广告2 */
	public final static String HT_AD2_NUM = "ht_ad2_num";
	/** 汇通广告2 */
	public final static String HT_AD2_KEY = "key";
	public final static String HT_AD2_KEY_SP = "ht_ad2_key";
	/** 汇通广告2 */
	public final static String HT_AD2_URL = "url";
	public final static String HT_AD2_URL_SP = "ht_ad2_url";
}