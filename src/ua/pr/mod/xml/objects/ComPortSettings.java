package ua.pr.mod.xml.objects;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class ComPortSettings implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private int databits;
	@XmlAttribute
	private String parity;
	@XmlAttribute
	private int stopbits;
	@XmlAttribute
	private String encoding;
	
	public int getDatabits() {
		return databits;
	}
	
	public void setDatabits(int databits) {
		this.databits = databits;
	}
	
	public String getParity() {
		return parity;
	}
	
	public void setParity(String parity) {
		this.parity = parity;
	}
	
	public int getStopbits() {
		return stopbits;
	}
	
	public void setStopbits(int stopbits) {
		this.stopbits = stopbits;
	}
	
	public String getEncoding() {
		return encoding;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
