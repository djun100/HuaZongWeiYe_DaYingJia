package com.hzwydyj.finace.data;

public class Gstore {
	private String gs_name;
	private String gs_id;

	public String getGs_name() {
		return gs_name;
	}

	public void setGs_name(String gs_name) {
		this.gs_name = gs_name;
	}

	public String getGs_id() {
		return gs_id;
	}

	public void setGs_id(String gs_id) {
		this.gs_id = gs_id;
	}

	@Override
	public String toString() {
		return "Gstore [gs_name=" + gs_name + ", gs_id=" + gs_id + "]";
	}

}
