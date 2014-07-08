package com.hzwydyj.finace.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hzwydyj.finace.R;
import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.utils.MyLogger;

/**
 * 汇率换算弹出
 * 
 * @author Administrator
 * 
 */
public class CustomDialog extends Dialog {

	private MyLogger log = MyLogger.yLog();
	int layoutRes;// 布局文件
	String exchange;
	Context context;
	float rate;
	String rmb;

	public CustomDialog(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * 自定义布局的构造方法
	 * 
	 * @param context
	 * @param resLayout
	 */
	public CustomDialog(Context context, int resLayout) {
		super(context);
		this.context = context;
		this.layoutRes = resLayout;
	}

	/**
	 * 自定义主题及布局的构造方法
	 * 
	 * @param context
	 * @param theme
	 * @param resLayout
	 */
	public CustomDialog(Context context, int theme, int resLayout) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
	}

	/**
	 * 自定义主题及布局的构造方法
	 * 
	 * @param context
	 * @param theme
	 * @param resLayout
	 */
	public CustomDialog(Context context, int theme, int resLayout, String exchange) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
		this.exchange = exchange;

	}

	/**
	 * 自定义主题及布局的构造方法
	 * 
	 * @param context
	 * @param theme
	 * @param resLayout
	 */
	public CustomDialog(Context context, int theme, int resLayout, String exchange, float rate) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
		this.exchange = exchange;
		this.rate = rate;

	}

	/**
	 * 自定义主题及布局的构造方法
	 * 
	 * @param context
	 * @param theme
	 * @param resLayout
	 */
	public CustomDialog(Context context, int theme, int resLayout, String exchange, float rate, String rmb) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
		this.exchange = exchange;
		this.rate = rate;
		this.rmb = rmb;
	}

	private TextView exchange1;
	private TextView exchange2;
	private EditText exchange1_num;
	private TextView exchange2_num;
	private Button change;
	private Button cancel;
	private Button changeall;
	/** 交换的标志 */
	private boolean exchange_bol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(layoutRes);

		exchange1 = (TextView) findViewById(R.id.exchange1);
		exchange2 = (TextView) findViewById(R.id.exchange2);
		exchange1_num = (EditText) findViewById(R.id.exchange1_num);
		exchange2_num = (TextView) findViewById(R.id.exchange2_num);
		change = (Button) findViewById(R.id.change);
		cancel = (Button) findViewById(R.id.cancel);
		changeall = (Button) findViewById(R.id.changeall);

		exchange1.setText(exchange);
		exchange1_num.setText("100");
		exchange1_num.setFocusable(true);

		exchange2.setText("人民币");
		exchange2_num.setText(rmb);

		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CustomDialog.this.dismiss();
			}
		});

		change.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				change();
				exchange_bol = exchange_bol == true ? false : true;
			}
		});

		exchange1_num.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if ("".equals(s.toString())) {
					exchange2_num.setText("0");
				}
			}

			public void afterTextChanged(Editable s) {
				if (s != null && !"".equals(s.toString())) {
					Double temp = Double.parseDouble(rmb) / 100;
					if (!exchange_bol) {
						temp = (Double.parseDouble(s.toString())) * temp;
					} else {
						temp = (Double.parseDouble(s.toString())) / temp;
					}

					exchange2_num.setText(Const.df2.format(temp));
				} else {
					exchange2_num.setText("0");
				}

			}
		});

		changeall.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("exchange1_num", exchange1_num.getText().toString().trim());
				intent.putExtra("exchange2_num", exchange2_num.getText().toString().trim());
				intent.putExtra("exchange_bol", exchange_bol);

				intent.setAction("android.intent.action.rmbpricechange");
				//log.i("-----------广播 单位/rate -----------" + exchange1_num.getText().toString().trim());
				context.sendBroadcast(intent);
				CustomDialog.this.dismiss();
			}
		});

	}

	/**
	 * 调换位置
	 */
	public void change() {
		String temp = exchange1.getText().toString();
		exchange1.setText(exchange2.getText().toString());
		exchange2.setText(temp);

		String temp2 = exchange1_num.getText().toString();
		exchange1_num.setText(exchange2_num.getText().toString());
		exchange2_num.setText(temp2);
	}

}
