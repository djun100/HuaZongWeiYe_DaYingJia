package com.hzwydyj.finace.data;

/**
 * 登录and注册
 * 
 * @author Administrator
 * 
 */
public class ShiPanLogin {

	/* 注册 */
	private String code;// 获取验证码
	private String zhuCeValue;// 注册返回值
	private String findPassword;// 找回密码
	/* 登录 */
	private String login;// 返回值
	private String groupid;// 用户组gid
	private String grouptitle;// 用户组名
	private String grade;// 权限名
	private String roomlevel;// 直播室权限值
	private String cookieid;// 验证id
	private String uid;// uid
	private String Error;// 登录失败
	private String Message;// 返回信息

	public ShiPanLogin() {
		// TODO Auto-generated constructor stub
	}

	public ShiPanLogin(String code, String zhuCeValue, String findPassword, String login, String groupid, String grouptitle, String grade, String roomlevel, String cookieid, String uid, String error,
			String Message) {
		super();
		this.code = code;
		this.zhuCeValue = zhuCeValue;
		this.findPassword = findPassword;
		this.login = login;
		this.groupid = groupid;
		this.grouptitle = grouptitle;
		this.grade = grade;
		this.roomlevel = roomlevel;
		this.cookieid = cookieid;
		this.uid = uid;
		this.Error = error;
		this.Message = Message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getZhuCeValue() {
		return zhuCeValue;
	}

	public void setZhuCeValue(String zhuCeValue) {
		this.zhuCeValue = zhuCeValue;
	}

	public String getFindPassword() {
		return findPassword;
	}

	public void setFindPassword(String findPassword) {
		this.findPassword = findPassword;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getGrouptitle() {
		return grouptitle;
	}

	public void setGrouptitle(String grouptitle) {
		this.grouptitle = grouptitle;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getRoomlevel() {
		return roomlevel;
	}

	public void setRoomlevel(String roomlevel) {
		this.roomlevel = roomlevel;
	}

	public String getCookieid() {
		return cookieid;
	}

	public void setCookieid(String cookieid) {
		this.cookieid = cookieid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getError() {
		return Error;
	}

	public void setError(String error) {
		this.Error = error;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String Message) {
		this.Message = Message;
	}

	@Override
	public String toString() {
		return "ShiPanLogin [code=" + code + ", zhuCeValue=" + zhuCeValue + ", findPassword=" + findPassword + ", login=" + login + ", groupid=" + groupid + ", grouptitle=" + grouptitle + ", grade="
				+ grade + ", roomlevel=" + roomlevel + ", cookieid=" + cookieid + ", uid=" + uid + ", error=" + Error + ", Message=" + Message + "]";
	}

}
