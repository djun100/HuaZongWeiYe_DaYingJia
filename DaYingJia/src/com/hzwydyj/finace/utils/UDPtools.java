package com.hzwydyj.finace.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.hzwydyj.finace.data.Const;

import android.util.Log;


public class UDPtools {

	private static MyLogger log = MyLogger.yLog();
	private static InetAddress address;
	private static String sendStr;
	private static byte[] sendBuf;
	private static byte[] recBuf;
	private static String sendStr_active;

	public static byte[] sendBuf_active;
	public static DatagramSocket client;
	public static DatagramPacket recpacket;
	public static DatagramPacket sendpacket;

	public static void initClient() {
		//log.i("initClient");
		try {
			client = new DatagramSocket();
		} catch (Exception e) {
			e.printStackTrace();
			//log.i("initClient_exception");
		}
	}

	public static void closeClient() {
		//log.i("closeClient");
		if (client != null) {
			client.close();
			client = null;
		}
	}

	public static void initrecpacket() {
		//log.i("initrecpacket");
		recBuf = new byte[1000];
		recpacket = new DatagramPacket(recBuf, recBuf.length);
	}

	public static void initsendpacket(String selected_str) {
		//log.i("initsendpacket");
		try {
			address = InetAddress.getByName(Const.UDP_IP);
			sendStr = String.format(Const.UDP_PARA + selected_str);
			sendBuf = sendStr.getBytes();
			sendStr_active = String.format("active,active");
			sendBuf_active = sendStr_active.getBytes();
			sendpacket = new DatagramPacket(sendBuf, sendBuf.length, address, Const.UDP_PORT);
		} catch (Exception e) {
			//log.i("initsendpacket_exception");
		}
	}

	public static void sendP() {
		//log.i("sendP");
		if (client == null) {
			throw new ClassCastException(" must initUDP");
		}
		if (sendpacket == null) {
			throw new ClassCastException(" must initsendpacket");
		}
		try {

			client.send(sendpacket);
		} catch (Exception e) {
			e.printStackTrace();
			//log.i("sendP_exception");
		}
	}

	public static void startUDP(String selected) {
		//log.i("startUDP");
		UDPtools.initsendpacket(selected);
		UDPtools.sendP();
	}

	public static void initUDP() {
		//log.i("initUDP");
		UDPtools.initClient();
		UDPtools.initrecpacket();
	}

}
