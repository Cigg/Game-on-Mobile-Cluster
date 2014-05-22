package com.pussycat.minions;

public class Server {
	public final String name;
	public final int slotsTaken;
	public final int numberOfSlots;
	public final String ip;
	public final int port;
	
	public Server(final String name, final int slotsTaken, final int numberOfSlots, final String ip, final int port) {
		this.slotsTaken = slotsTaken;
		this.numberOfSlots = numberOfSlots;
		this.name = name;
		this.ip = cleanIp(ip);
		this.port = port;
	}
	
	public String cleanIp(final String ip) {
		char[] charIp = new char[ip.length()];
		int index = 0;
		for(int i=0; i<ip.length(); i++) {
			char c = ip.charAt(i);
			if(c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || 
			   c == '6' || c == '7' || c == '8' || c == '9' || c == '.') {
				charIp[index] = c;
				index++;
			}
		}
		char[] finalIp = new char[index];
		System.arraycopy(charIp, 0, finalIp, 0, index);
		return String.valueOf(finalIp);
	}
}