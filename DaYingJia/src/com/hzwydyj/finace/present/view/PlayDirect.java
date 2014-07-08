package com.hzwydyj.finace.present.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hzwydyj.finace.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 播放语音的气泡
 * 
 * @Description:
 * @author:zhuanggy
 * @see:
 * @since:
 * 
 * @Date:2013-5-29
 * 
 *                 欢迎加入QQ开发群：88130145
 * 
 */

public class PlayDirect extends LinearLayout {

	private Handler mHandler;
	private static final int PREPARE_VIEW = 110;
	private static final int START_VIEW = 111;
	private static final int STOP_VIEW = 112;
	private static final int ERROR_VIEW = 113;

	private static final String TAG = "PlayBubble";
	private Context mContext;
	private ImageView imgWave;// 播放时的波纹图片
	private ProgressBar progressBar;// 下载或prepare中的等待窗展示
	private ImageView imgError;// 下载失败，或无法播放
	private RelativeLayout layoutMain;// progressBar显示时此气泡是不可点击的
	private TextView textTime;// 语音时长
	private ViewGroup main;
	private AnimationDrawable mAnimationDrawable;// 播放时的波纹动画
	private DirectListener mBubbleListener;

	private MediaPlayer mMediaPlayer;
	private String mUrl;

	private boolean mIsprepared;// 是否准备好了，（文件已经下载下来，且MediaPlayer可以播放）
	private int mId;
	private boolean isDownloading;// 如果正在下载，则不再触发下载
	private boolean playAfterDownload;// 播放时本地缓存不存在，触发下载，下载完成后是否播放(可能下载过程中，触发了其它语音的播放，则此标志置为false)
	private static final String FILE_PATH = "/mnt/sdcard/driectdir/";// 语音缓存目录

	public PlayDirect(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initViews();
	}

	public PlayDirect(Context context) {
		super(context);
		this.mContext = context;
		initViews();
	}

	/**
	 * 初始化ui
	 */
	private void initViews() {

		LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
		main = (ViewGroup) inflater.inflate(R.layout.audio_bubble, null);
		imgWave = (ImageView) main.findViewById(R.id.img_bubble_wave);
		textTime = (TextView) main.findViewById(R.id.text_bubble_time);
		layoutMain = (RelativeLayout) main.findViewById(R.id.layout_bubble_main);
		progressBar = (ProgressBar) main.findViewById(R.id.progressbar_audio_prepareing);
		imgError = (ImageView) main.findViewById(R.id.img_audio_error);

		layoutMain.setEnabled(true);
		mHandler = new BubbleHandler();
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		layoutMain.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startStopPlay();
			}
		});

		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				Log.v(TAG, "onCompletion");
				if (mIsprepared) {
					mHandler.sendEmptyMessage(STOP_VIEW);
					if (mBubbleListener != null) {
						mBubbleListener.playCompletion(PlayDirect.this);
					}
				} else {
					mHandler.sendEmptyMessage(ERROR_VIEW);
					if (mBubbleListener != null) {
						mBubbleListener.playFail(PlayDirect.this);
					}
				}
			}
		});
		mMediaPlayer.setOnErrorListener(new OnErrorListener() {

			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				Log.e(TAG, "onError");
				mHandler.sendEmptyMessage(ERROR_VIEW);
				if (mBubbleListener != null) {
					mBubbleListener.playFail(PlayDirect.this);
				}
				return false;
			}
		});
		mMediaPlayer.setOnInfoListener(new OnInfoListener() {

			public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
				Log.v(TAG, "onInfo");
				return false;
			}
		});
		mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			public void onPrepared(MediaPlayer mp) {
				// prepare完成，随时可以播放
				Log.v(TAG, "onPrepared");
				textTime.setText(formatTimeMilles(mMediaPlayer.getDuration()));// 显示语音时长
				mIsprepared = true;
				if (playAfterDownload) {
					// 下载并prepare完成，若需要播放，则播放
					startStopPlay();
				} else {
					// 不需要播放
					mHandler.sendEmptyMessage(STOP_VIEW);
				}
			}
		});

		// mMediaPlayer.setOnBufferingUpdateListener(new
		// OnBufferingUpdateListener() {
		//
		// @Override
		// public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// Log.v(TAG, "onBufferingUpdate percent=" + percent);
		// }
		// });
		this.addView(main);
	}

	/**
	 * 停止播放(用于外部控制停止播放，可在通话状态、不允许多个气泡同时播放时调用停止某个或某些气泡的播放)
	 * 
	 * @Description:
	 * @see:
	 * @since:
	 * @author: zhuanggy
	 * @date:2013-5-29
	 */
	public void stopPlay() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			// 若正在播放，则触发停止
			doStop();
			Log.v(TAG, "now is playing stop");
		} else {
			playAfterDownload = false;
			Log.v(TAG, "now is not playing, set playAfterDownload = false");
		}
	}

	/**
	 * 判断是否正在播放
	 * 
	 * @Description:
	 * @return
	 * @see:
	 * @since:
	 * @author: zhuanggy
	 * @date:2013-5-29
	 */
	public boolean isPlaying() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获得id
	 * 
	 * @Description:
	 * @return
	 * @see:
	 * @since:
	 * @author: zhuanggy
	 * @date:2013-5-29
	 */
	public int getId() {
		return this.mId;
	}

	/**
	 * 获得URL
	 * 
	 * @Description:
	 * @return
	 * @see:
	 * @since:
	 * @author: zhuanggy
	 * @date:2013-5-29
	 */
	public String getUrl() {
		return this.mUrl;
	}

	/**
	 * 设置监听器
	 * 
	 * @Description:
	 * @param listener
	 * @see:
	 * @since:
	 * @author: zhuanggy
	 * @date:2013-5-29
	 */
	public void setBubbleListener(DirectListener listener) {
		this.mBubbleListener = listener;
	}

	/**
	 * 设置音频URl
	 * 
	 * @Description:
	 * @param url
	 * @param id
	 * @see:
	 * @since:
	 * @author: zhuanggy
	 * @date:2013-5-29
	 */
	public void setAudioUrl(String url) {
		this.mUrl = url;
		final File soundFile = new File(FILE_PATH + url.hashCode());
		// 如果此文件已经存在，则直接prepare，否则不操作，等待点击时:download-prepare-play
		new Thread(new Runnable() {

			public void run() {
				if (soundFile.exists()) {
					doPrepare(soundFile);
				}
			}
		}).start();
	}

	private class BubbleHandler extends Handler {

		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);

			switch (msg.what) {
			case START_VIEW:
				setPlayingView();
				break;
			case STOP_VIEW:
				setStopView();
				break;
			case PREPARE_VIEW:
				setLoadingView();
				break;
			case ERROR_VIEW:
				setErrorView();
				break;
			default:
				break;
			}
		}

	}

	private boolean doPrepare(File soundFile) {
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(soundFile.getAbsolutePath());
			mMediaPlayer.prepare();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(ERROR_VIEW);
			if (mBubbleListener != null) {
				mBubbleListener.playFail(PlayDirect.this);
			}
		}
		return false;
	}

	/**
	 * 开始或暂停播放
	 * 
	 * @Description:
	 * @see:
	 * @since:
	 * @author: zhuanggy
	 * @date:2013-5-29
	 */
	private void startStopPlay() {
		if (mMediaPlayer.isPlaying()) {
			// 若正在播放中，再次触发播放，则为暂停
			doStop();
			if (mBubbleListener != null) {
				mBubbleListener.playStoped(PlayDirect.this);
			}
		} else {
			if (mBubbleListener != null) {
				mBubbleListener.playStart(PlayDirect.this);
			}
			if (mIsprepared) {
				// 若下载完成，并prepare完成，可立即播放
				doPlay();
			} else {
				// 若为prepare，则尝试重新（下载）prepare
				if (mUrl != null && mUrl.length() > 0) {
					new Thread(new Runnable() {

						public void run() {
							playAfterDownload = true;
							downLoadPrepareFile(mUrl);
						}
					}).start();

				} else {
					mHandler.sendEmptyMessage(ERROR_VIEW);
					if (mBubbleListener != null) {
						mBubbleListener.playFail(PlayDirect.this);
					}
				}
			}
		}

	}

	private void doPlay() {
		mHandler.sendEmptyMessage(START_VIEW);
		try {
			mMediaPlayer.start();
			Log.v("", "start media player");
			// telephonyManager.listen(new MainPhoneListener(),
			// PhoneStateListener.LISTEN_CALL_STATE);
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(ERROR_VIEW);
			if (mBubbleListener != null) {
				mBubbleListener.playFail(PlayDirect.this);
			}
		}
	}

	private void doStop() {
		mHandler.sendEmptyMessage(STOP_VIEW);
		mMediaPlayer.pause();
		mMediaPlayer.seekTo(0);
		Log.v("", "stop media player");
		// telephonyManager.listen(new MainPhoneListener(),
		// PhoneStateListener.LISTEN_NONE);
	}

	private String formatTimeMilles(int timeMilles) {
		int second = timeMilles / 1000;
		return second + "'";
	}

	private void setPlayingView() {
		Log.v(TAG, "playing view");
		layoutMain.setBackgroundResource(R.drawable.bg_bubble_playing);
		imgWave.setImageResource(R.anim.bubble_anim);
		mAnimationDrawable = (AnimationDrawable) imgWave.getDrawable();
		mAnimationDrawable.start();
		progressBar.setVisibility(View.GONE);
		layoutMain.setEnabled(true);
	}

	private void setStopView() {
		Log.v(TAG, "stop view");
		if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
			mAnimationDrawable.stop();
		}
		imgWave.setImageResource(R.drawable.ic_bubble_normal);
		layoutMain.setBackgroundResource(R.drawable.bg_bubble);
		layoutMain.setEnabled(true);
		progressBar.setVisibility(View.GONE);
		imgError.setVisibility(View.GONE);
	}

	private void setErrorView() {
		Log.v(TAG, "error view");
		imgError.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
			mAnimationDrawable.stop();
		}
		imgWave.setImageResource(R.drawable.ic_bubble_normal);
		layoutMain.setBackgroundResource(R.drawable.bg_bubble);
		layoutMain.setEnabled(true);
	}

	private void setLoadingView() {
		Log.v(TAG, "loading view");
		imgError.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		layoutMain.setEnabled(false);
	}

	// telephonyManager = (TelephonyManager)
	// mContext.getSystemService(Context.TELEPHONY_SERVICE);
	// private final class MainPhoneListener extends
	// android.telephony.PhoneStateListener {
	//
	// @Override
	// public void onCallStateChanged(int state, String incomingNumber) {
	// switch (state) {
	// case TelephonyManager.CALL_STATE_RINGING:// 电话来了
	// stopPlay();
	// break;
	// case TelephonyManager.CALL_STATE_IDLE: // 通话结束
	// break;
	// }
	// }
	// }

	/**
	 * 下载方法1
	 * 
	 * @Description:
	 * @param url
	 * @return
	 * @see:
	 * @since:
	 * @author: zhuanggy
	 * @date:2013-5-29
	 */
	public void downLoadPrepareFile(String url) {

		// TODO 判断存储卡是否可用，若不可用，则存在程序cache目录里，即FILE_PATH为mContext.getCacheDir();

		File soundFile = new File(FILE_PATH + url.hashCode());
		if (!soundFile.exists()) {
			if (!isDownloading) {
				Log.v(TAG, "start download");
				// 若尚未下载，且，本地没有则下载
				mHandler.sendEmptyMessage(PREPARE_VIEW);
				try {
					isDownloading = true;
					if (!(new File(FILE_PATH)).exists()) {
						(new File(FILE_PATH)).mkdirs();
					}
					System.setProperty("http.keepAlive", "false");// 解决经常报此异常问题，at
																	// java.util.zip.GZIPInputStream.readFully(GZIPInputStream.java:214)
					URL Url = new URL(url);
					URLConnection conn = Url.openConnection();
					conn.connect();
					InputStream is = conn.getInputStream();
					// this.fileSize = conn.getContentLength();// 根据响应获取文件大小
					// if (this.fileSize <= 0) { // 获取内容长度为0
					// throw new RuntimeException("无法获知文件大小 ");
					// }
					if (is == null) { // 没有下载流
						mHandler.sendEmptyMessage(ERROR_VIEW);
						Log.e(TAG, "没有下载流");
					}
					FileOutputStream FOS = new FileOutputStream(soundFile); // 创建写入文件内存流，通过此流向目标写文件

					byte buf[] = new byte[1024];
					// downLoadFilePosition = 0;
					int numread;
					while ((numread = is.read(buf)) != -1) {
						FOS.write(buf, 0, numread);
						// downLoadFilePosition += numread
					}
					is.close();

					doPrepare(soundFile);
					isDownloading = false;
					Log.v(TAG, "download finish");
					FOS.flush();
					if (FOS != null) {
						FOS.close();
					}

				} catch (Exception e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(ERROR_VIEW);
					isDownloading = false;
				}
			}
		} else {
			// 本地已经存在
			doPrepare(soundFile);
		}
	}

	/**
	 * 下载方法二
	 * 
	 * @Description:
	 * @param url
	 * @return
	 * @see:
	 * @since:
	 * @author: zhuanggy
	 * @date:2013-5-29
	 */
	private void loadAudioFromUrl(String url) {
		File soundFile = new File(FILE_PATH + url.hashCode());
		if (!soundFile.exists()) {
			if (!isDownloading) {
				Log.v(TAG, "start download");
				// 若尚未下载，且，本地没有则下载
				mHandler.sendEmptyMessage(PREPARE_VIEW);
				try {
					isDownloading = true;
					if (!(new File(FILE_PATH)).exists()) {
						(new File(FILE_PATH)).mkdirs();
					}

					InputStream is = null;

					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(url);
					HttpResponse response;

					response = client.execute(get);

					HttpEntity entity = response.getEntity();
					// float length = entity.getContentLength();

					is = entity.getContent();
					// GoOutDebug.v(TAG, "loadImageFromUrl");
					FileOutputStream fos = null;
					if (is != null) {
						fos = new FileOutputStream(soundFile);
						byte[] buf = new byte[1024];
						int ch = -1;
						// float count = 0;
						// int newPercent = 0, oldPercent = 0;
						while ((ch = is.read(buf)) != -1) {
							fos.write(buf, 0, ch);

							// 更新进度与否
							// count += ch;
							// newPercent = (int) (count * 100 / length);
							// if (newPercent > oldPercent) {
							// percentHandler.sendEmptyMessage(newPercent);
							// }
							// oldPercent = newPercent;
						}
					} else {
						mHandler.sendEmptyMessage(ERROR_VIEW);
						Log.e(TAG, "没有下载流");
						if (soundFile.exists()) {
							soundFile.delete();
						}
					}
					fos.flush();
					if (fos != null) {
						fos.close();
					}

					doPrepare(soundFile);
					isDownloading = false;
					Log.v(TAG, "download finish");

				} catch (Exception e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(ERROR_VIEW);
					isDownloading = false;
					if (soundFile.exists()) {
						soundFile.delete();
					}
				}
			}
		} else {
			// 本地已经存在
			doPrepare(soundFile);
		}
	}
}
