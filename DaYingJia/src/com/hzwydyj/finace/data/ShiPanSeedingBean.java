package com.hzwydyj.finace.data;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

/**
 * 实盘直播
 * 
 * @author Administrator
 * 
 */
public class ShiPanSeedingBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2491693013597045039L;
	/* 评论 */
	private String cid;// 评论cid
	private String rid;// 直播室编号
	private String lid;// 直播室直播lid
	private String uid;// 用户uid
	private String showname;// 用户名
	private String ip;// 用户IP
	private String message;// 互动内容
	private String is_shield;// 是否屏蔽
	private String is_top;// 置顶
	private String is_verify;// 审核(0未通过，1通过)
	private String agree;// 被赞同
	private String notagree;// 不被赞同
	private String tips;// 举报数
	private String dateline;// 互动时间
	private String replycid;// 回复的cid
	private String replyuid;// 回复人uid
	private String c_showname;// 回复人昵称
	private String is_deleted;// 逻辑删除标题(1表示删除)
	/* 直播间 */
	private String recordid;// 直播记录ID
	private String is_wonderful;// 是否为精华（1为精华，0为非精华）
	private String is_call;// 是否为喊单（1为喊单，0为非喊单）
	private String level;// 权限（0：全部可见；1：会员可见；2：实盘客户可见；3：vip客户可见）
	private String is_system;// 是否为系统直播消息
	private String authority;// 直播数据查看权限
	private String avatar_middle;// 老师头像（大）
	private String avatar_small;// 老师头像（小）

	public ShiPanSeedingBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ShiPanSeedingBean(String cid, String rid, String lid, String uid, String showname, String ip, String message, String is_shield, String is_top, String is_verify, String agree,
			String notagree, String tips, String dateline, String replycid, String replyuid, String c_showname, String is_deleted, String recordid, String is_wonderful, String is_call, String level,
			String is_system, String authority, String avatar_middle, String avatar_small) {
		super();
		this.cid = cid;
		this.rid = rid;
		this.lid = lid;
		this.uid = uid;
		this.showname = showname;
		this.ip = ip;
		this.message = message;
		this.is_shield = is_shield;
		this.is_top = is_top;
		this.is_verify = is_verify;
		this.agree = agree;
		this.notagree = notagree;
		this.tips = tips;
		this.dateline = dateline;
		this.replycid = replycid;
		this.replyuid = replyuid;
		this.c_showname = c_showname;
		this.is_deleted = is_deleted;
		this.recordid = recordid;
		this.is_wonderful = is_wonderful;
		this.is_call = is_call;
		this.level = level;
		this.is_system = is_system;
		this.authority = authority;
		this.avatar_middle = avatar_middle;
		this.avatar_small = avatar_small;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getLid() {
		return lid;
	}

	public void setLid(String lid) {
		this.lid = lid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getShowname() {
		return showname;
	}

	public void setShowname(String showname) {
		this.showname = showname;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getIs_shield() {
		return is_shield;
	}

	public void setIs_shield(String is_shield) {
		this.is_shield = is_shield;
	}

	public String getIs_top() {
		return is_top;
	}

	public void setIs_top(String is_top) {
		this.is_top = is_top;
	}

	public String getIs_verify() {
		return is_verify;
	}

	public void setIs_verify(String is_verify) {
		this.is_verify = is_verify;
	}

	public String getAgree() {
		return agree;
	}

	public void setAgree(String agree) {
		this.agree = agree;
	}

	public String getNotagree() {
		return notagree;
	}

	public void setNotagree(String notagree) {
		this.notagree = notagree;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public String getReplycid() {
		return replycid;
	}

	public void setReplycid(String replycid) {
		this.replycid = replycid;
	}

	public String getReplyuid() {
		return replyuid;
	}

	public void setReplyuid(String replyuid) {
		this.replyuid = replyuid;
	}

	public String getC_showname() {
		return c_showname;
	}

	public void setC_showname(String c_showname) {
		this.c_showname = c_showname;
	}

	public String getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(String is_deleted) {
		this.is_deleted = is_deleted;
	}

	public String getRecordid() {
		return recordid;
	}

	public void setRecordid(String recordid) {
		this.recordid = recordid;
	}

	public String getIs_wonderful() {
		return is_wonderful;
	}

	public void setIs_wonderful(String is_wonderful) {
		this.is_wonderful = is_wonderful;
	}

	public String getIs_call() {
		return is_call;
	}

	public void setIs_call(String is_call) {
		this.is_call = is_call;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getIs_system() {
		return is_system;
	}

	public void setIs_system(String is_system) {
		this.is_system = is_system;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getAvatar_middle() {
		return avatar_middle;
	}

	public void setAvatar_middle(String avatar_middle) {
		this.avatar_middle = avatar_middle;
	}

	public String getAvatar_small() {
		return avatar_small;
	}

	public void setAvatar_small(String avatar_small) {
		this.avatar_small = avatar_small;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "ShiPanSeedingBean [cid=" + cid + ", rid=" + rid + ", lid=" + lid + ", uid=" + uid + ", showname=" + showname + ", ip=" + ip + ", message=" + message + ", is_shield=" + is_shield
				+ ", is_top=" + is_top + ", is_verify=" + is_verify + ", agree=" + agree + ", notagree=" + notagree + ", tips=" + tips + ", dateline=" + dateline + ", replycid=" + replycid
				+ ", replyuid=" + replyuid + ", c_showname=" + c_showname + ", is_deleted=" + is_deleted + ", recordid=" + recordid + ", is_wonderful=" + is_wonderful + ", is_call=" + is_call
				+ ", level=" + level + ", is_system=" + is_system + ", authority=" + authority + ", avatar_middle=" + avatar_middle + ", avatar_small=" + avatar_small + "]";
	}

}
