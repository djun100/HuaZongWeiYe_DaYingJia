package com.hzwydyj.finace.data;

public class GPrice {
	private String id;
	private String brandid;
	private String product;
	private String price;
	private String degree;
	private String updatetime;
	private String createtime;
	private String change;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBrandid() {
		return brandid;
	}
	public void setBrandid(String brandid) {
		this.brandid = brandid;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getChange() {
		return change;
	}
	public void setChange(String change) {
		this.change = change;
	}
	@Override
	public String toString() {
		return "GPrice [id=" + id + ", brandid=" + brandid + ", product=" + product + ", price="
				+ price + ", degree=" + degree + ", updatetime=" + updatetime + ", createtime="
				+ createtime + ", change=" + change + "]";
	}
	
	
}
