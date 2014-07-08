package com.hzwydyj.finace.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;

public class IMGtools {
	/**
	 * 保存图片
	 * 
	 * @param bitName
	 * @throws IOException
	 */
	public Bitmap saveMyBitmap(String bitName) throws IOException {
		Bitmap mBitmap = null;
		File f = new File("/data/data/com.fx678.finace/files/" + bitName + ".png");
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mBitmap;
	}
}
