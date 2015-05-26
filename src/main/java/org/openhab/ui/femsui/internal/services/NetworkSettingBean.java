package org.openhab.ui.femsui.internal.services;

import java.net.Inet4Address;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="networkSetting")
public class NetworkSettingBean {
	public enum Mode {
		STATIC("STATIC"),
		DYNAMIC("DYNAMIC");
		
		private final String text;
		private Mode(String text) {
			this.text = text;
		}
		@Override
		public String toString() {
			return text;
		}
	}
	
	public Mode mode = null;
	public Inet4Address ip = null;
	public Inet4Address netmask = null;
	public Inet4Address gateway = null;
	
}
