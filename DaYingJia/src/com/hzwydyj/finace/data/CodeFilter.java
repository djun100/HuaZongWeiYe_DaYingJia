package com.hzwydyj.finace.data;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 质保书验证返回值解码
 * @author Administrator
 *
 */
public class CodeFilter {


	public String sToUtf(String str) {
		try {
			str = URLDecoder.decode(str, "utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	
	public String utfTos(String str){
		
		try {
			str = URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
}
