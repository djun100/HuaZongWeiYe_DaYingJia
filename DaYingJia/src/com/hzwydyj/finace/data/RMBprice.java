package com.hzwydyj.finace.data;

public class RMBprice {
	private String name;
	private String code;
	private String money;
	private String rmb;
	private float rate;

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getRmb() {
		return rmb;
	}

	public void setRmb(String rmb) {
		this.rmb = rmb;
	}

	@Override
	public String toString() {
		return "RMBprice [name=" + name + ", code=" + code + ", money=" + money + ", rmb=" + rmb + "]";
	}

}
