package com.hzwydyj.finace.present.view;


/**
 * 回调
 * @author LuoYi
 *
 */
public interface DirectListener {

	public void playFail(PlayDirect playBubble);
	public void playStoped(PlayDirect playBubble);
	public void playStart(PlayDirect playBubble);
	public void playCompletion(PlayDirect playBubble);
}
