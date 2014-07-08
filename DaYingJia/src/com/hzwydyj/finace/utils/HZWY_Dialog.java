package com.hzwydyj.finace.utils;

import com.hzwydyj.finace.MyApplication;
import com.hzwydyj.finace.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HZWY_Dialog extends Dialog {

	private AlertDialog pointDialog;

	public HZWY_Dialog(Context context) {
		super(context);
	}

	public static Dialog createDialog(Context context, int dlgID, Bundle args, int title, int index) {
		LayoutInflater factory = LayoutInflater.from(MyApplication.CONTEXT);
		final View textEntryView = factory.inflate(R.layout.shipan_share_dialog, null);
		TextView loginTitle = (TextView) textEntryView.findViewById(R.id.share_title_text);
		ProgressBar loginProgess = (ProgressBar) textEntryView.findViewById(R.id.progress_bar);
		TextView loginContent = (TextView) textEntryView.findViewById(R.id.share_content_text);
		TextView loginMessage = (TextView) textEntryView.findViewById(R.id.share_message_text);
		LinearLayout loginLayout = (LinearLayout) textEntryView.findViewById(R.id.share_layout);
		loginTitle.setText(title);
		loginProgess.setVisibility(View.VISIBLE);
		loginContent.setVisibility(View.GONE);
		loginMessage.setText(index);
		loginMessage.setVisibility(View.VISIBLE);
		loginLayout.setVisibility(View.GONE);
		AlertDialog.Builder pointDialog = new AlertDialog.Builder(MyApplication.CONTEXT).setView(textEntryView);
		pointDialog.show();
		return pointDialog.create();

	}

	/********************************************* 获取名师讲堂列表弹窗********开始 ************************************************/
	public void cancelDialog() {
		pointDialog.cancel();
	}
	/********************************************** 获取名师讲堂表弹窗********结束 ***********************************************/
}
