package com.hzwydyj.finace.data;

public class ETF {
	private String time;
	private String val;
	private String preVal;
	private String amplitude;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getPreVal() {
		return preVal;
	}

	public void setPreVal(String preVal) {
		this.preVal = preVal;
	}

	public String getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(String amplitude) {
		this.amplitude = amplitude;
	}

	@Override
	public String toString() {
		return "ETF [time=" + time + ", val=" + val + ", preVal=" + preVal + ", amplitude=" + amplitude + "]";
	}

}
