package com.hzwydyj.finace.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.data.ShiPanLogin;
import com.hzwydyj.finace.service.SysApplication;
import com.hzwydyj.finace.utils.CommonUtil;
import com.hzwydyj.finace.utils.Util;

/**
 * 找回密码
 * @author LuoYi
 */
public class ShiPanFindPassword extends Activity implements OnClickListener {

	private EditText 				findDianHua;
	private EditText 				findVerification;
	private EditText 				findPassword;
	private EditText 				findMiMa;
	private Button 					findCode;
	private Button 					findConfirm;
	private Button 					findBack;
	private String 					phoneNumber;
	private String 					verification;
	private String 					password1;
	private String 					password;
	private MyCount 				mc;
	private Util 					findUtil = new Util();

	/* 创建页面*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		init();
		SysApplication.getInstance().addActivity(this);  
	}

	/* 初始化控件*/
	public void init() {
		setContentView(R.layout.shipan_find_password);
		
		hzwy_BaseActivity = new HZWY_BaseActivity(MyApplication.CONTEXT);
		
		findDianHua 			= (EditText) this.findViewById(R.id.edit_find_dian_hua);
		findVerification 		= (EditText) this.findViewById(R.id.edit_find_verification_code);
		findPassword 			= (EditText) this.findViewById(R.id.edit_find_pass_word);
		findMiMa 				= (EditText) this.findViewById(R.id.edit_find_mima);
		findCode 				= (Button) this.findViewById(R.id.button_find_verification_code);
		findConfirm 			= (Button) this.findViewById(R.id.button_find_confirm);
		findBack 				= (Button) this.findViewById(R.id.button_find_backbtn);
		findCode.setOnClickListener(this);
		findConfirm.setOnClickListener(this);
		findBack.setOnClickListener(this);
	}

	/* 控件监听事件*/
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_find_verification_code:/* 获取验证码*/
			phoneNumber = findDianHua.getText().toString();
			if (!"".equals(phoneNumber)) {
				if (phoneNumber.length() == 11) {
					if (CommonUtil.isMobileNum(phoneNumber)) {
						if (CommonUtil.isConnectingToInternet(ShiPanFindPassword.this)) {
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
			break;

		case R.id.button_find_confirm:/* 确认*/
			phoneNumber 		= findDianHua.getText().toString();
			verification 		= findVerification.getText().toString();
			password 			= findPassword.getText().toString();
			password1 			= findMiMa.getText().toString();
			if (!"".equals(phoneNumber)) {
				if (CommonUtil.isMobileNum(phoneNumber)) {
					if (verification.length() == 4) {
						if (password1.equals(password)) {
							if (CommonUtil.isConnectingToInternet(ShiPanFindPassword.this)) {
								userRegister();
								mc.onFinish();
							} else {
								hzwy_BaseActivity.HZWY_Toast(R.string.network_connection_failure);
							}
						} else {
							hzwy_BaseActivity.HZWY_Toast(R.string.judge_pass_word_input);
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
			break;

		case R.id.button_find_backbtn:/* 返回*/
			ShiPanFindPassword.this.finish();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 获取验证码
	 */
	public void verificationCode() {
		new Thread(new Runnable() {

			public void run() {
				String url = Const.HZWY_URL + "type=sendmobilecode&";
				try {
					Message msg 	= verificationHandler.obtainMessage();
					msg.obj 		= findUtil.getVerificationCodePost(url, phoneNumber);
					verificationHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/* 接口返回的数据*/
	Handler verificationHandler = new Handler() {
		public void handleMessage(Message msg) {
			ShiPanLogin panLogin 	= (ShiPanLogin) msg.obj;
			String code 			= panLogin.getCode();
			if ("succeed".equals(code)) {
				mc = new MyCount(60000, 1000);
				mc.start();
			} else if ("error".equals(code)) {
				hzwy_BaseActivity.HZWY_Toast1("发送失败");
			} else if ("mobile_error".equals(code)) {
				hzwy_BaseActivity.HZWY_Toast1("手机号码错误");
			}
		};
	};
	
	/**
	 * 找回密码
	 */
	private void userRegister() {
		new Thread(new Runnable() {

			public void run() {
				String url = Const.HZWY_URL + "type=lostpasswd&";
				try {
					Message msg 	= handler.obtainMessage();
					msg.obj 		= findUtil.getFindPassword_post(url, phoneNumber, verification, password1);
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/* 接口返回的数据*/
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ShiPanLogin shipanLogin 	= (ShiPanLogin) msg.obj;
			String code 				= shipanLogin.getFindPassword();
			String message 				= null;
			if ("succeed".equals(code)) {
				message = "修改成功";
				ShiPanFindPassword.this.finish();
			} else if ("update_error".equals(code)) {
				message = "修改失败";
				findDianHua.setText("");
				findVerification.setText("");
				findPassword.setText("");
				findMiMa.setText("");
			} else if ("nochange".equals(code)) {
				message = "没有任务修改";
				findDianHua.setText("");
				findVerification.setText("");
				findPassword.setText("");
				findMiMa.setText("");
			} else if ("mobilecode_error".equals(code)) {
				message = "手机验证码错误";
				findDianHua.setText("");
				findVerification.setText("");
				findPassword.setText("");
				findMiMa.setText("");
			} else if ("getpasswd_account_invalid".equals(code)) {
				message = "不可找回密码";
				findDianHua.setText("");
				findVerification.setText("");
				findPassword.setText("");
				findMiMa.setText("");
			} else if ("user_does_not_exist".equals(code)) {
				message = "用户不存在";
				findDianHua.setText("");
				findVerification.setText("");
				findPassword.setText("");
				findMiMa.setText("");
			} else if ("user_delete".equals(code)) {
				message = "用户已经已被删除";
				findDianHua.setText("");
				findVerification.setText("");
				findPassword.setText("");
				findMiMa.setText("");
			} else if ("system_error".equals(code)) {
				message = "系统错误";
				findDianHua.setText("");
				findVerification.setText("");
				findPassword.setText("");
				findMiMa.setText("");
			} else if ("newpasswd_error".equals(code)) {
				message = "新密码有误";
				findDianHua.setText("");
				findVerification.setText("");
				findPassword.setText("");
				findMiMa.setText("");
			} else if ("mobilecode_error".equals(code)) {
				message = "手机短信验证码有误";
				findDianHua.setText("");
				findVerification.setText("");
				findPassword.setText("");
				findMiMa.setText("");
			} else if ("username_error".equals(code)) {
				message = "用户名有误(即手机号码有误)";
				findDianHua.setText("");
				findVerification.setText("");
				findPassword.setText("");
				findMiMa.setText("");
			}
			hzwy_BaseActivity.HZWY_Toast1(message);
		};
	};
	private HZWY_BaseActivity hzwy_BaseActivity;

	/**
	 * 获取验证码倒计时
	 * @author Administrator
	 */
	class MyCount extends CountDownTimer {

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		/* 倒计时停止*/
		@Override
		public void onFinish() {
			findCode.setText(R.string.regiter_gain_verification_code);
			findCode.setEnabled(true);/* 启动button*/
			
			findDianHua.setCursorVisible(true);/* 显示光标*/
			findDianHua.setFocusableInTouchMode(true);/* 弹出软键盘*/
			
			findDianHua.setFilters(new InputFilter[] { new InputFilter() {/* editext可编辑*/
				              public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {     
				                       return null;  
				              }     
			} });    
		}

		/* 倒计时开始*/
		@Override
		public void onTick(long millisUntilFinished) {
			findCode.setText(millisUntilFinished / 1000 + "秒后重新获取");
			long a = millisUntilFinished / 1000;
			findCode.setEnabled(false);/* 禁止button*/
			findCode.setTextSize(12);
			
			findDianHua.setCursorVisible(false);/* 隐藏光标*/
			findDianHua.setFocusableInTouchMode(false);/* 禁止弹出软键盘*/
			findDianHua.setFilters(new InputFilter[] { new InputFilter() {/* editext不可编辑*/

				public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
					return source.length() < 1 ? dest.subSequence(dstart, dend) : "";
				}
			} });
		}
	}
}
