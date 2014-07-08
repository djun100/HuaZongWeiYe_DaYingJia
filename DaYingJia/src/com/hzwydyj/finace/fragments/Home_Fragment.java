package com.hzwydyj.finace.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzwydyj.finace.R;

public class Home_Fragment extends Fragment {

	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

	public Home_Fragment() {

	}

//	private int[] layout_ids = { R.layout.home_fragment, R.layout.home_fragment2 };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		int position = getArguments().getInt("position");
		View view = null;
		view = inflater.inflate(R.layout.home_fragment, container, false);
//		switch (position) {
//		case 0:
//		case 1:
////			view = inflater.inflate(layout_ids[position], container, false);
//			
//			
//			break;
//
//		default:
//			break;
//		}

		return view;
	}

}
