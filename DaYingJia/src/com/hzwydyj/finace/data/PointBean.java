package com.hzwydyj.finace.data;

/**
 * 名师讲堂
 * 
 * @author Administrator
 * 
 */
public class PointBean {

	/* 名师讲堂 首页 */
	private String vtid;// 标题id
	private String vtname;// 标题名字
	/* 每日解盘\技术讲解 */
	private String uid;// 老师id
	private String username;// 老师名字
	private String intro;// 老师介绍
	private String name;// 老师名字
	private String avatar;// 老师照片
	/* 视频列表 */
	private String id;// 视频id
	private String subject;// 标题
	private String appaddr;// 地址
	private String dateline;// 发布日期
	private String teacheruid;// 老师ID
	private String ttype;// 解盘/讲解
	private String passwd;// 视频密码

	public PointBean() {
		super();
	}

	public PointBean(String vtid, String vtname, String uid, String username, String intro, String name, String avatar, String id, String subject, String appaddr, String dateline, String teacheruid,
			String ttype, String passwd) {
		super();
		this.vtid = vtid;
		this.vtname = vtname;
		this.uid = uid;
		this.username = username;
		this.intro = intro;
		this.name = name;
		this.avatar = avatar;
		this.id = id;
		this.subject = subject;
		this.appaddr = appaddr;
		this.dateline = dateline;
		this.teacheruid = teacheruid;
		this.ttype = ttype;
		this.passwd = passwd;
	}

	public String getVtid() {
		return vtid;
	}

	public void setVtid(String vtid) {
		this.vtid = vtid;
	}

	public String getVtname() {
		return vtname;
	}

	public void setVtname(String vtname) {
		this.vtname = vtname;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAppaddr() {
		return appaddr;
	}

	public void setAppaddr(String appaddr) {
		this.appaddr = appaddr;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public String getTeacheruid() {
		return teacheruid;
	}

	public void setTeacheruid(String teacheruid) {
		this.teacheruid = teacheruid;
	}

	public String getTtype() {
		return ttype;
	}

	public void setTtype(String ttype) {
		this.ttype = ttype;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	@Override
	public String toString() {
		return "PointBean [vtid=" + vtid + ", vtname=" + vtname + ", uid=" + uid + ", username=" + username + ", intro=" + intro + ", name=" + name + ", avatar=" + avatar + ", id=" + id
				+ ", subject=" + subject + ", appaddr=" + appaddr + ", dateline=" + dateline + ", teacheruid=" + teacheruid + ", ttype=" + ttype + ", passwd=" + passwd + "]";
	}

}
