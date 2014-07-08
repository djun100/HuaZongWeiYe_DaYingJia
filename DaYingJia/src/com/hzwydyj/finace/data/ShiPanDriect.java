package com.hzwydyj.finace.data;

public class ShiPanDriect {

	private String haveimg;
	private String havemp3;
	private String havereplay;
	private String havelink;
	private String msgtext;
	private String replaytext;
	private String replaydate;
	private String imgurl;
	private String imgthumburl;
	private String mp3url;
	private String link;

	public ShiPanDriect() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ShiPanDriect(String haveimg, String havemp3, String havereplay, String havelink, String msgtext, String replaytext, String replaydate, String imgurl, String imgthumburl, String mp3url,
			String link) {
		super();
		this.haveimg = haveimg;
		this.havemp3 = havemp3;
		this.havereplay = havereplay;
		this.havelink = havelink;
		this.msgtext = msgtext;
		this.replaytext = replaytext;
		this.replaydate = replaydate;
		this.imgurl = imgurl;
		this.imgthumburl = imgthumburl;
		this.mp3url = mp3url;
		this.link = link;
	}

	public String getHaveimg() {
		return haveimg;
	}

	public void setHaveimg(String haveimg) {
		this.haveimg = haveimg;
	}

	public String getHavemp3() {
		return havemp3;
	}

	public void setHavemp3(String havemp3) {
		this.havemp3 = havemp3;
	}

	public String getHavereplay() {
		return havereplay;
	}

	public void setHavereplay(String havereplay) {
		this.havereplay = havereplay;
	}

	public String getHavelink() {
		return havelink;
	}

	public void setHavelink(String havelink) {
		this.havelink = havelink;
	}

	public String getMsgtext() {
		return msgtext;
	}

	public void setMsgtext(String msgtext) {
		this.msgtext = msgtext;
	}

	public String getReplaytext() {
		return replaytext;
	}

	public void setReplaytext(String replaytext) {
		this.replaytext = replaytext;
	}

	public String getReplaydate() {
		return replaydate;
	}

	public void setReplaydate(String replaydate) {
		this.replaydate = replaydate;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getImgthumburl() {
		return imgthumburl;
	}

	public void setImgthumburl(String imgthumburl) {
		this.imgthumburl = imgthumburl;
	}

	public String getMp3url() {
		return mp3url;
	}

	public void setMp3url(String mp3url) {
		this.mp3url = mp3url;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "ShiPanDriect [haveimg=" + haveimg + ", havemp3=" + havemp3 + ", havereplay=" + havereplay + ", havelink=" + havelink + ", msgtext=" + msgtext + ", replaytext=" + replaytext
				+ ", replaydate=" + replaydate + ", imgurl=" + imgurl + ", imgthumburl=" + imgthumburl + ", mp3url=" + mp3url + ", link=" + link + "]";
	}

}
