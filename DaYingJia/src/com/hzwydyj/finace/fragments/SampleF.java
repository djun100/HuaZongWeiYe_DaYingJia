package com.hzwydyj.finace.fragments;

/**
 * 仅供测试使用
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzwydyj.finace.R;

public class SampleF extends Fragment {

	public void onResume() {
		super.onResume();
		// MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.samplefragments, container, false);
		// url = bundle.getString("link");
		return view;
	}

}
