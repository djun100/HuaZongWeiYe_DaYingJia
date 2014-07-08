package com.hzwydyj.finace.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hzwydyj.finace.data.Const;
import com.hzwydyj.finace.fragments.Home_Fragment;

public class Tab_Home_FragmentPagerAdapter extends FragmentPagerAdapter {

	public Tab_Home_FragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment 		= new Home_Fragment();
		Bundle args 			= new Bundle();
		args.putInt("position", position);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return Const.TABHOME_VIEWPAGER_NUMBER;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return "标签1";
		case 1:
			return "标签2";
		case 2:
			return "标签3";
		}
		return null;
	}
}
