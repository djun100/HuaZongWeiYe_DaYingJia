package com.hzwydyj.finace.data;

import java.io.Serializable;

/**
 * 赢家内参
 * 
 * @author LuoYi
 * 
 */
public class WinnerInside implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5477996508745519495L;

	private String doc_url;

	public WinnerInside() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getDoc_url() {
		return doc_url;
	}

	public void setDoc_url(String doc_url) {
		this.doc_url = doc_url;
	}

}
