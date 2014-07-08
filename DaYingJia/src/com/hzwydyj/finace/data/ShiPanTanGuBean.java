package com.hzwydyj.finace.data;

import java.io.Serializable;

/**
 * 直播室信息接口
 * 
 * @author Administrator
 * 
 */
public class ShiPanTanGuBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2390009360122552130L;
	
	private String rid;// 直播室编号
	private String name;// 直播室名称
	private String username;// 直播室主播
	private String subject;// 主题
	private String point;// 观点
	private String public_notice;// 公告
	private String status;// 直播室状态(open开放stop关闭)
	private String authority;// 用户访问权限

	public ShiPanTanGuBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ShiPanTanGuBean(String rid, String name, String username, String subject, String point, String public_notice, String status, String authority) {
		super();
		this.rid = rid;
		this.name = name;
		this.username = username;
		this.subject = subject;
		this.point = point;
		this.public_notice = public_notice;
		this.status = status;
		this.authority = authority;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getPublic_notice() {
		return public_notice;
	}

	public void setPublic_notice(String public_notice) {
		this.public_notice = public_notice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	@Override
	public String toString() {
		return "ShiPanTanGuBean [rid=" + rid + ", name=" + name + ", username=" + username + ", subject=" + subject + ", point=" + point + ", public_notice=" + public_notice + ", status=" + status
				+ ", authority=" + authority + "]";
	}

}
