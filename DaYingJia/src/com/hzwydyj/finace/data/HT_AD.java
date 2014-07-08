package com.hzwydyj.finace.data;

public class HT_AD {
	private String version;

	private String android_show;
	private String android_url;

	private String androidpad_show;
	private String androidpad_url;

	private String describe_modify_date;
	private String describe_expire_date;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAndroid_show() {
		return android_show;
	}

	public void setAndroid_show(String android_show) {
		this.android_show = android_show;
	}

	public String getAndroid_url() {
		return android_url;
	}

	public void setAndroid_url(String android_url) {
		this.android_url = android_url;
	}

	public String getAndroidpad_show() {
		return androidpad_show;
	}

	public void setAndroidpad_show(String androidpad_show) {
		this.androidpad_show = androidpad_show;
	}

	public String getAndroidpad_url() {
		return androidpad_url;
	}

	public void setAndroidpad_url(String androidpad_url) {
		this.androidpad_url = androidpad_url;
	}

	public String getDescribe_modify_date() {
		return describe_modify_date;
	}

	public void setDescribe_modify_date(String describe_modify_date) {
		this.describe_modify_date = describe_modify_date;
	}

	public String getDescribe_expire_date() {
		return describe_expire_date;
	}

	public void setDescribe_expire_date(String describe_expire_date) {
		this.describe_expire_date = describe_expire_date;
	}

	@Override
	public String toString() {
		return "HT_AD [version=" + version + ", android_show=" + android_show + ", android_url=" + android_url
				+ ", androidpad_show=" + androidpad_show + ", androidpad_url=" + androidpad_url + ", describe_modify_date="
				+ describe_modify_date + ", describe_expire_date=" + describe_expire_date + "]";
	}

}
