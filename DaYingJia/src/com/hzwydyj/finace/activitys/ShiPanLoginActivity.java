package com.hzwydyj.finace.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.ShiPanLogin;
import com.hzwydyj.finace.utils.CommonUtil;
import com.hzwydyj.finace.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * 实盘直播登录页面
 * 
 * @author LuoYi
 * 
 */
public class ShiPanLoginActivity extends Activity implements OnClickListener {

	private Util 								shipanUtil = new Util();
	private AlertDialog 						dialog;
	private AlertDialog 						loginDialog;
	private MyCount 							mc;
	private RelativeLayout 						shipanLayout;
	private Button 								shipanLogin;
	private Button 								shipanCode;
	private Button 								shipanConfirm;
	private Button 								shipanCancelImage;
	private Button 								shipanBack;
	private String 								accounts;
	private String 								password;
	private String 								phoneNumber;
	private String 								verification;
	private String 								shiPassword;
	private String 								password1;
	private String 								shipanPhoneId;// 本机号码
	private EditText 							shipanAccounts, shipanPassword;
	private EditText 							shipanPhoneNumber;
	private EditText 							shipanVerification;
	private EditText 							shipanMiMa;
	private EditText 							findVerification;
	private EditText 							findPassword;
	private EditText 							findMiMa;
	private ImageView 							shipanZhangHao;
	private ImageView 							shipanPassWrod;
	private TextView 							shipanRegister;
	private TextView 							shipanFind;
	private boolean 							falg;
	private TelephonyManager tm;

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

	/**
	 * 创建页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		init();
//		SysApplication.getInstance().addActivity(this);  
	}

	/**
	 * 控件初始化
	 */
	public void init() {
		setContentView(R.layout.shipanlogin);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		
		/* 创建电话管理 与手机建立连接*/
		tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		
		/* 初始化控件 */
		shipanAccounts 					= (EditText) findViewById(R.id.edit_accounts);
		shipanPassword 					= (EditText) findViewById(R.id.edit_password);
		shipanLogin 					= (Button) findViewById(R.id.button_login);
		shipanRegister 					= (TextView) findViewById(R.id.button_register);
		shipanBack 						= (Button) findViewById(R.id.button_backbtn);
		shipanFind 						= (TextView) findViewById(R.id.text_find);
		shipanZhangHao 					= (ImageView) findViewById(R.id.zhanghao_image);
		shipanPassWrod 					= (ImageView) findViewById(R.id.mima_image);
		shipanLayout 					= (RelativeLayout) findViewById(R.id.login_relative_layout);
		final CheckBox shipanRememberPassword = (CheckBox) findViewById(R.id.remember_password_box);
		falg = true;
		shipanRememberPassword.setButtonDrawable(R.drawable.remember_password);
		shipanRememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					falg = false;
					shipanRememberPassword.setButtonDrawable(R.drawable.remember_password_hover);
				} else {
					falg = true;
					shipanRememberPassword.setButtonDrawable(R.drawable.remember_password);
				}
			}
		});

		shipanLogin.setOnClickListener(this);
		shipanRegister.setOnClickListener(this);
		shipanBack.setOnClickListener(this);
		shipanFind.setOnClickListener(this);

		mc = new MyCount(60000, 1000);

//		AutoLogin();
	}

	/************************************************ 自动登录******开始 *************************************************/
	/**
	 * 自动登录
	 */
	public void AutoLogin() {
		SharedPreferences preferences 	= getSharedPreferences(Const.PREFERNCES_SHIPAN, Activity.MODE_PRIVATE);
		String shiAccounts 				= preferences.getString("accounts", "");
		String shiPassword 				= preferences.getString("password", "");
		if (!"".equals(shiAccounts) && !"".equals(shiPassword)) {
			LoginDialog();
			shipanAccounts.setVisibility(View.GONE);
			shipanPassword.setVisibility(View.GONE);
			shipanLogin.setVisibility(View.GONE);
			shipanRegister.setVisibility(View.GONE);
			shipanBack.setVisibility(View.GONE);
			shipanFind.setVisibility(View.GONE);
			shipanZhangHao.setVisibility(View.GONE);
			shipanPassWrod.setVisibility(View.GONE);
			shipanLayout.setVisibility(View.GONE);
			accounts = shiAccounts;
			password = shiPassword;
			new Thread() {
				public void run() {
					try {
						sleep(2000);
						login();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

	/************************************************* 自动登录****结束 *******************************************************/

	/************************************************* 按钮********开始 ***********************************************/
	/**
	 * 控件按钮
	 */
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_login:// 登录
			accounts = shipanAccounts.getText().toString().trim();
			password = shipanPassword.getText().toString().trim();
			if (accounts.length() != 0) {
				if (password.length() >= 6) {
					if (CommonUtil.isConnectingToInternet(ShiPanLoginActivity.this)) {
						LoginDialog();
						login();
					} else {
						hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
					}
				} else {
					hzwy_BaseActivity.HZWY_Toast(R.string.judge_user_password);
				}
			} else {
				hzwy_BaseActivity.HZWY_Toast(R.string.judge_user_name);
			}
			break;

		case R.id.button_register:// 注册
			registerDialog();
			break;

		case R.id.text_find:// 找回密码
			findPasswordDialog();
			break;

		case R.id.button_backbtn:// 返回
			ShiPanLoginActivity.this.finish();
			break;

		default:
			break;
		}
	}

	/************************************************* 按钮******结束 ************************************************/

	/************************************************** 登录*******开始 **********************************************/
	/* 登录 */
	public void login() {
		new Thread(new Runnable() {

			public void run() {
				String url = Const.HZWY_URL + "type=login&";
				try {
					Message msg = loginHandler.obtainMessage();
					msg.obj 	= shipanUtil.getLogin_post(url, accounts, password);
					loginHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			ShiPanLogin panLogin 	= (ShiPanLogin) msg.obj;
			String loginRoomlevel 		= panLogin.getRoomlevel();/* 直播室权限值*/
			String loginError 			= panLogin.getError();
			String loginMessage 		= panLogin.getMessage();
			String loginGroupid 		= panLogin.getGroupid();
			String loginGrouptitle 		= panLogin.getGrouptitle();
			String loginGrade 			= panLogin.getGrade();
			String loginCookieid 		= panLogin.getCookieid();
			String loginUid 			= panLogin.getUid();

			if (!"1".equals(loginError)) {
				if ("0".equals(loginRoomlevel)) {
					jurisdictionDialog();
					loginDialog.cancel();
				} else {
					loginDialog.cancel();
					Intent intent = new Intent(ShiPanLoginActivity.this, ShiPanSelectActivity.class);
					intent.putExtra("groupid", loginGroupid);/* 用户组gid*/
					intent.putExtra("groupTitle", loginGrouptitle);/* 用户组名*/
					intent.putExtra("grade", loginGrade);/* 权限名*/
					intent.putExtra("roomlevel", loginRoomlevel);
					intent.putExtra("cookieid", loginCookieid);/* 验证id*/
					intent.putExtra("uid", loginUid);/* 登录用户uid*/
					startActivity(intent);
					hzwy_BaseActivity.HZWY_Toast(R.string.login_succeed);
					if (falg) {
						/* 将登录数据保存在SharedPreferences中 */
						SharedPreferences preferences = getSharedPreferences(Const.PREFERNCES_SHIPAN, Activity.MODE_PRIVATE);
						Editor editor = preferences.edit();
//						editor.putString("accounts", accounts);/* 用户名*/
//						editor.putString("password", password);/* 密码*/
						editor.putString("groupid", loginGroupid);/* 用户组gid*/
						editor.putString("groupTitle", loginGrouptitle);/* 用户组名*/
						editor.putString("grade", loginGrade);/* 权限名*/
						editor.putString("roomlevel", loginRoomlevel);
						editor.putString("cookieid", loginCookieid);/* 验证id*/
						editor.putString("uid", loginUid);/* 登录用户uid*/
						editor.commit();
					}
					ShiPanLoginActivity.this.finish();// 结束本页
				}
			} else {
				loginDialog.cancel();
				hzwy_BaseActivity.HZWY_Toast1(loginMessage);
			}
		};
	};

	/* 权限提示 */
	public void jurisdictionDialog() {
		final String suppoerPhone = "4000-720-760";
		LayoutInflater factory 			= LayoutInflater.from(this);
		final View textEntryView 		= factory.inflate(R.layout.shipan_share_dialog, null);
		TextView outInTitle 			= (TextView) textEntryView.findViewById(R.id.share_title_text);
		TextView outInContent 			= (TextView) textEntryView.findViewById(R.id.share_content_text);
		Button outInQueRen 				= (Button) textEntryView.findViewById(R.id.share_queren_button);
		Button outInQuXiao 				= (Button) textEntryView.findViewById(R.id.share_quxiao_button);
		/* 温馨提示*/
		outInTitle.setText(R.string.tishi_warm_prompt);
		outInContent.setText("您访问受限。" + "\n" + "客服电话：" + suppoerPhone + "\n" + "是否拨打？");
		outInQueRen.setText(R.string.tishi_dial);
		outInQuXiao.setText(R.string.regiter_caler);
		outInQuXiao.setVisibility(View.VISIBLE);

		final AlertDialog dialog 		= new AlertDialog.Builder(this).setView(textEntryView).create();
		dialog.show();

		outInQueRen.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.parse("tel://" + suppoerPhone));
				startActivity(intent);
			}
		});

		outInQuXiao.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog.cancel();
			}
		});
	}
	/*********************************************** 登录*******结束 **************************************************/

	/************************************************ 注册*******开始 *************************************************/
	/* 注册 */
	public void registerDialog() {
		LayoutInflater factory 			= LayoutInflater.from(this);

		final View textEntryView 		= factory.inflate(R.layout.register_windows, null);
		shipanPhoneNumber 				= (EditText) textEntryView.findViewById(R.id.edit_dian_hua);
		shipanVerification 				= (EditText) textEntryView.findViewById(R.id.edit_verification_code);
		shipanMiMa 						= (EditText) textEntryView.findViewById(R.id.edit_pass_word);
		shipanConfirm 					= (Button) textEntryView.findViewById(R.id.button_confirm);
		shipanCode 						= (Button) textEntryView.findViewById(R.id.button_verification_code);
		shipanCancelImage 				= (Button) textEntryView.findViewById(R.id.cancel_image);

		/* 创建电话管理 与手机建立连接*/
		try {
			shipanPhoneId 					= tm.getLine1Number();
			shipanPhoneId 					= shipanPhoneId.replace("+86", "");
		} catch (Exception e) {
			hzwy_BaseActivity.HZWY_Toast(R.string.introduce_sim_card);
		}
		shipanPhoneNumber.setText(shipanPhoneId);

		dialog 							= new AlertDialog.Builder(this).setView(textEntryView).create();
		dialog.show();
		shipanConfirm.setOnClickListener(new OnClickListener() {// 确定
					public void onClick(View v) {
						phoneNumber 	= shipanPhoneNumber.getText().toString();
						verification 	= shipanVerification.getText().toString();
						shiPassword 	= shipanMiMa.getText().toString();
						if (!"".equals(phoneNumber)) {
							if (phoneNumber.length() == 11) {
								if (verification.length() == 4) {
									if (shiPassword.length() >= 6) {
										if (CommonUtil.isConnectingToInternet(ShiPanLoginActivity.this)) {
											userRegister();
											mc.onFinish();
										} else {
											hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
										}
									} else {
										hzwy_BaseActivity.HZWY_Toast(R.string.judge_user_password);
									}
								} else {
									hzwy_BaseActivity.HZWY_Toast(R.string.judge_code_input_number);
								}
							} else {
								hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_format);
							}
						} else {
							hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_null);
						}
					}
				});
		shipanCancelImage.setOnClickListener(new OnClickListener() {// 取消

					public void onClick(View v) {
						dialog.dismiss();
						mc.onFinish();
					}
				});
		shipanCode.setOnClickListener(new OnClickListener() {// 获取验证码
					public void onClick(View v) {
						phoneNumber = shipanPhoneNumber.getText().toString();
						Log.i("temp", "phoneNumber:" + phoneNumber);
						if (!"".equals(phoneNumber)) {
							if (phoneNumber.length() == 11) {
								if (CommonUtil.isMobileNum(phoneNumber)) {
									if (CommonUtil.isConnectingToInternet(ShiPanLoginActivity.this)) {
										new Thread() {
											public void run() {
												verificationCode();
											};
										}.start();
									} else {
										hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
									}
								} else {
									hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_format);
								}
							} else {
								hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_format);
							}
						} else {
							hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_null);
						}
					}
				});
	}

	/**
	 * 用户注册
	 */
	private void userRegister() {
		new Thread(new Runnable() {

			public void run() {
				String url = Const.HZWY_URL + "type=regist&";
				try {
					Message msg = handler.obtainMessage();
					msg.obj 	= shipanUtil.getRegister_post(url, phoneNumber, verification, shiPassword);
					Log.i("LOG", "注册返回：" + msg.obj);
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ShiPanLogin shipanLogin 	= (ShiPanLogin) msg.obj;
			String Error 				= shipanLogin.getError();
			String Message 				= shipanLogin.getMessage();
			if ("0".equals(Error)) {
				dialog.dismiss();
				succeed();
				shipanAccounts.setText(phoneNumber);
			} else {
				hzwy_BaseActivity.HZWY_Toast1(Message);
				shipanPhoneNumber.setText("");
				shipanVerification.setText("");
				shipanMiMa.setText("");
			}
		};
	};

	/* 注册成功 */
	public void succeed() {
		LayoutInflater factory 				= LayoutInflater.from(this);
		final View textEntryView 			= factory.inflate(R.layout.shipan_share_dialog, null);

		TextView loginTitle 				= (TextView) textEntryView.findViewById(R.id.share_title_text);
		TextView loginContent 				= (TextView) textEntryView.findViewById(R.id.share_content_text);
		Button loginQueRen 					= (Button) textEntryView.findViewById(R.id.share_queren_button);

		loginTitle.setText(R.string.tishi_registration_successfu);
		loginContent.setText(R.string.tishi_successfu_message);
		loginQueRen.setText(R.string.tishi_que_dings);

		loginDialog = new AlertDialog.Builder(this).setView(textEntryView).create();
		loginDialog.show();
		
		loginQueRen.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				loginDialog.cancel();
			}
		});
	}
	/********************************************** 注册******结束 ***************************************************/

	/*********************************************** 找回密码********开始 ***************************************************/
	public void findPasswordDialog() {
		LayoutInflater factory 			= LayoutInflater.from(this);

		final View textEntryView 		= factory.inflate(R.layout.shipan_find_password_dialog, null);
		shipanPhoneNumber 				= (EditText) textEntryView.findViewById(R.id.edit_find_dian_hua);
		findVerification 				= (EditText) textEntryView.findViewById(R.id.edit_find_verification_code);
		findPassword 					= (EditText) textEntryView.findViewById(R.id.edit_find_pass_word);
		findMiMa 						= (EditText) textEntryView.findViewById(R.id.edit_find_mima);
		shipanCode 						= (Button) textEntryView.findViewById(R.id.button_find_verification_code);
		Button findConfirm 				= (Button) textEntryView.findViewById(R.id.button_find_confirm);
		Button findBack 				= (Button) textEntryView.findViewById(R.id.cancel_image_button);

		/* 创建电话管理 与手机建立连接*/
		try {
			shipanPhoneId 					= tm.getLine1Number();
			shipanPhoneId 					= shipanPhoneId.replace("+86", "");
		} catch (Exception e) {
			hzwy_BaseActivity.HZWY_Toast(R.string.introduce_sim_card);
		}
		shipanPhoneNumber.setText(shipanPhoneId);
		
		dialog 							= new AlertDialog.Builder(this).setView(textEntryView).create();
		dialog.show();

		shipanCode.setOnClickListener(new OnClickListener() {// 获取验证码

					public void onClick(View v) {
						phoneNumber = shipanPhoneNumber.getText().toString();
						if (!"".equals(phoneNumber)) {
							if (phoneNumber.length() == 11) {
								if (CommonUtil.isMobileNum(phoneNumber)) {
									if (CommonUtil.isConnectingToInternet(ShiPanLoginActivity.this)) {
										new Thread() {
											public void run() {
												verificationCode();   
											};
										}.start();
									} else {
										hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
									}
								} else {
									hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_format);
								}
							} else {
								hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_format);
							}
						} else {
							hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_null);
						}
					}
				});

		findConfirm.setOnClickListener(new OnClickListener() {// 确定

					public void onClick(View v) {
						phoneNumber 		= shipanPhoneNumber.getText().toString();
						verification 		= findVerification.getText().toString();
						password 			= findPassword.getText().toString();
						password1 			= findMiMa.getText().toString();
						if (!"".equals(phoneNumber)) {
							if (CommonUtil.isMobileNum(phoneNumber)) {
								if (verification.length() == 4) {
									if (password.length() >= 6) {
										if (password1.equals(password)) {
											if (CommonUtil.isConnectingToInternet(ShiPanLoginActivity.this)) {
												findPassword();
												mc.onFinish();
											} else {
												hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
											}
										} else {
											hzwy_BaseActivity.HZWY_Toast(R.string.judge_pass_word_input);
										}
									} else {
										hzwy_BaseActivity.HZWY_Toast(R.string.judge_user_password);
									}
								} else {
									hzwy_BaseActivity.HZWY_Toast(R.string.judge_code_input_number);
								}
							} else {
								hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_format);
							}
						} else {
							hzwy_BaseActivity.HZWY_Toast(R.string.judge_phone_number_null);
						}
					}
				});

		findBack.setOnClickListener(new OnClickListener() {// 取消

			public void onClick(View v) {
				dialog.dismiss();
				mc.onFinish();
			}
		});
	}

	/**
	 * 找回密码
	 */
	private void findPassword() {
		new Thread(new Runnable() {

			public void run() {
				String url = Const.HZWY_URL + "type=lostpasswd&";
				try {
					Message msg = findhandler.obtainMessage();
					msg.obj = shipanUtil.getFindPassword_post(url, phoneNumber, verification, password1);
					findhandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/* 接口返回的数据 */
	@SuppressLint("HandlerLeak")
	Handler findhandler = new Handler() {
		public void handleMessage(Message msg) {
			ShiPanLogin shipanLogin = (ShiPanLogin) msg.obj;
			String Error 			= shipanLogin.getError();
			Log.i("LOG", "Error:" + Error);
			String message 			= null;
			if ("0".equals(Error)) {
				message = "修改成功";
				shipanAccounts.setText(phoneNumber);
				dialog.dismiss();
			} else {
				message = shipanLogin.getMessage();
				shipanPhoneNumber.setText("");
				findVerification.setText("");
				findPassword.setText("");
				findMiMa.setText("");
			}
			hzwy_BaseActivity.HZWY_Toast1(message);
		};
	};
	/************************************************** 找回密码******结束 **************************************************/

	// /////////////////////////////////////////////获取验证码//////开始///////////////////////////////////////////////////
	/**
	 * 获取验证码
	 */
	public void verificationCode() {
		new Thread(new Runnable() {

			public void run() {
				String url = Const.HZWY_URL + "type=sendmobilecode&";
				try {
					Message msg 	= verificationHandler.obtainMessage();
					msg.obj 		= shipanUtil.getVerificationCodePost(url, phoneNumber);
					verificationHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	Handler verificationHandler = new Handler() {
		public void handleMessage(Message msg) {
			ShiPanLogin panLogin 	= (ShiPanLogin) msg.obj;
			String Error 			= panLogin.getError();
			String Message 			= panLogin.getMessage();
			if ("0".equals(Error)) {
				mc.start();
			} else {
				hzwy_BaseActivity.HZWY_Toast1(Message);
			}
		};
	};
	private HZWY_BaseActivity hzwy_BaseActivity;

	/**
	 * 获取验证码倒计时
	 * 
	 * @author Administrator
	 */
	class MyCount extends CountDownTimer {

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		/* 倒计时停止 */
		@Override
		public void onFinish() {
			Log.i("temp", "onFinish");
			shipanCode.setText(R.string.regiter_gain_verification_code);
			shipanCode.setBackgroundResource(R.color.login_button);
			shipanCode.setEnabled(true);/* 启动button*/
			mc.cancel();
			shipanPhoneNumber.setCursorVisible(true);/* 显示光标*/
			shipanPhoneNumber.setFocusableInTouchMode(true);/* 弹出软键盘*/
			shipanPhoneNumber.setFilters(new InputFilter[] { new InputFilter() {/* editext可编辑*/
				public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
					return null;
				}
			} });
		}

		/* 倒计时开始 */
		@Override
		public void onTick(long millisUntilFinished) {

			Log.i("temp", "onTick");
			shipanCode.setText(millisUntilFinished / 1000 + "秒后重新获取");
			shipanCode.setBackgroundResource(R.drawable.hzwy_verification_code);
			long a = millisUntilFinished / 1000;
			shipanCode.setEnabled(false);/* 禁止button*/
			shipanCode.setTextSize(12);

			shipanPhoneNumber.setCursorVisible(false);/* 隐藏光标*/
			shipanPhoneNumber.setFocusableInTouchMode(false);/* 禁止弹出软键盘*/
			shipanPhoneNumber.setFilters(new InputFilter[] { new InputFilter() {/* editext不可编辑*/
				public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
					return source.length() < 1 ? dest.subSequence(dstart, dend) : "";
				}
			} });
		}
	}

	// ///////////////////////////////////////////////获取验证码/////////结束/////////////////////////////////////////////

	/********************************************* 登录弹窗********开始 ************************************************/
	public void LoginDialog() {
		LayoutInflater factory 				= LayoutInflater.from(this);
		final View textEntryView 			= factory.inflate(R.layout.shipan_share_dialog, null);

		TextView loginTitle 				= (TextView) textEntryView.findViewById(R.id.share_title_text);
		ProgressBar loginProgess 			= (ProgressBar) textEntryView.findViewById(R.id.progress_bar);
		TextView loginContent 				= (TextView) textEntryView.findViewById(R.id.share_content_text);
		TextView loginMessage 				= (TextView) textEntryView.findViewById(R.id.share_message_text);
		LinearLayout loginLayout 			= (LinearLayout) textEntryView.findViewById(R.id.share_layout);
		/* 提示*/
		loginTitle.setText(R.string.later_on_user);
		loginProgess.setVisibility(View.VISIBLE);
		loginContent.setVisibility(View.GONE);
		loginMessage.setText(R.string.login_centre);
		loginMessage.setVisibility(View.VISIBLE);
		loginLayout.setVisibility(View.GONE);

		loginDialog = new AlertDialog.Builder(this).setView(textEntryView).create();
		loginDialog.show();
	}
	/********************************************** 登录弹窗********结束 ***********************************************/

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc();
	}
}
