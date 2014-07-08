package com.hzwydyj.finace.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;
import com.hzwydyj.finace.image.CacheImageAsyncTask;

public class HZWY_BaseActivity extends Activity {
	
	private AlertDialog							dialog;
	private Context context;
	
	public HZWY_BaseActivity(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public void HZWY_Toast(int index) {
		LayoutInflater inflater 				= LayoutInflater.from(MyApplication.CONTEXT);
		View layout 							= inflater.inflate(R.layout.hzwy_toast_layout, null);
		TextView text 							= (TextView) layout.findViewById(R.id.text_toast);
		text.setText(index);
		Toast toast 							= new Toast(MyApplication.CONTEXT);
		toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 30);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

	public void HZWY_Toast1(String content) {
		LayoutInflater inflater 				= LayoutInflater.from(MyApplication.CONTEXT);
		View layout 							= inflater.inflate(R.layout.hzwy_toast_layout, null);
		TextView text 							= (TextView) layout.findViewById(R.id.text_toast);
		text.setText(content);
		Toast toast 							= new Toast(MyApplication.CONTEXT);
		toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 30);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}
	
	/*********************************************获取列表弹窗********开始************************************************/
	public void SingleDialog(int hint){
		LayoutInflater factory 					= LayoutInflater.from(MyApplication.CONTEXT);
		final View textEntryView 				= factory.inflate(R.layout.shipan_share_dialog, null);
		TextView loginTitle 					= (TextView) textEntryView.findViewById(R.id.share_title_text);
		ProgressBar loginProgess 				= (ProgressBar) textEntryView.findViewById(R.id.progress_bar);
		TextView loginContent 					= (TextView) textEntryView.findViewById(R.id.share_content_text);
		TextView loginMessage 					= (TextView) textEntryView.findViewById(R.id.share_message_text);
		LinearLayout loginLayout 				= (LinearLayout) textEntryView.findViewById(R.id.share_layout);
		loginTitle.setText(R.string.later_on_user);
		loginProgess.setVisibility(View.VISIBLE);
		loginContent.setVisibility(View.GONE);
		loginMessage.setText(hint);
		loginMessage.setVisibility(View.VISIBLE);
		loginLayout.setVisibility(View.GONE);
		dialog 									= new AlertDialog.Builder(MyApplication.CONTEXT).setView(textEntryView).create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
		dialog.show();
	}
	
	public void dialogCancel(){
		dialog.dismiss();
	}
	/**********************************************获取列表弹窗********结束***********************************************/
	
	/**
	 * 初级会员权限
	 */
	public void pinglun(int hint, int comment) {
		LayoutInflater factory 				= LayoutInflater.from(MyApplication.CONTEXT);
		final View textEntryView 			= factory.inflate(R.layout.shipan_share_dialog, null);
		TextView loginTitle 				= (TextView) textEntryView.findViewById(R.id.share_title_text);
		TextView loginContent 				= (TextView) textEntryView.findViewById(R.id.share_content_text);
		Button loginQueRen 					= (Button) textEntryView.findViewById(R.id.share_queren_button);
		loginTitle.setText(hint);
		loginContent.setText(comment);
		dialog 								= new AlertDialog.Builder(MyApplication.CONTEXT).setView(textEntryView).create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
		dialog.show();
		loginQueRen.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				dialog.cancel();
			}
		});
	}
	
	
	public void permissionHint(int index){
		LayoutInflater factory 				= LayoutInflater.from(MyApplication.CONTEXT);
		final View textEntryView 			= factory.inflate(R.layout.shipan_share_dialog, null);
		TextView permissionTitle 			= (TextView) textEntryView.findViewById(R.id.share_title_text);
		TextView permissionContent 			= (TextView) textEntryView.findViewById(R.id.share_content_text);
		Button permissionQueRen 			= (Button) textEntryView.findViewById(R.id.share_queren_button);
		permissionTitle.setText(R.string.tishi_warm_prompt);
		permissionContent.setText(index);
		dialog 								= new AlertDialog.Builder(MyApplication.CONTEXT).setView(textEntryView).create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
		dialog.show();
		
		permissionQueRen.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				dialog.cancel();
			}
		});
	}
	
	public void showPicture(String path){
		LayoutInflater factory 			= LayoutInflater.from(MyApplication.CONTEXT);
		final View textEntryView 		= factory.inflate(R.layout.shipan_picture_amplification, null);
		ImageView lookImage 			= (ImageView) textEntryView.findViewById(R.id.look_picture_image);
		dialog 							= new AlertDialog.Builder(MyApplication.CONTEXT).setView(textEntryView).create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
		dialog.show();
		loadImag(path, lookImage);
		lookImage.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				dialog.cancel();
			}
		});
	}
	
	/**
	 * 设置图片路径
	 * @param path
	 * @param imag
	 * @return
	 */
	public Bitmap loadImag(String path, ImageView imag) {
		new CacheImageAsyncTask(imag, MyApplication.CONTEXT).execute(path);
		return null;
	}
}
