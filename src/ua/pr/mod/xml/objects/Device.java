package ua.pr.mod.xml.objects;

import java.io.Serializable;
import java.util.Hashtable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Device implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(name="name")
	private String name;
	@XmlElement
	private String serialNumberAddress;
	@XmlElement
	private String serialNumberLength;
	@XmlElement
	private String modbusIdAddress;
	@XmlElement(name="TS")
	private String tsu;
	@XmlElement(name="TI")
	private String tis;
	@XmlAttribute(name="default")
	private boolean default_;
	
	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSerialNumberAddress() {
		return serialNumberAddress;
	}
	
	public void setSerialNumberAddress(String serialNumberAddress) {
		this.serialNumberAddress = serialNumberAddress;
	}
	
	public String getSerialNumberLength() {
		return serialNumberLength;
	}
	
	public void setSerialNumberLength(String serialNumberLength) {
		this.serialNumberLength = serialNumberLength;
	}
	
	public String getModbusIdAddress() {
		return modbusIdAddress;
	}
	
	public void setModbusIdAddress(String modbusIdAddress) {
		this.modbusIdAddress = modbusIdAddress;
	}

	public String getTsu() {
		return tsu;
	}

	public void setTsu(String tsu) {
		this.tsu = tsu;
	}

	public Hashtable<Integer, String> getTS() {
		Hashtable<Integer, String> ret = new Hashtable<>();
		String sTs = getTsu();
		String[] arr = sTs.split(";");
		
		for (int i = 0; i < arr.length; i++) {
			ret.put(i + 1, arr[i]);
		}
		return ret;
	}

	public String getTis() {
		return tis;
	}
	
	public Hashtable<Integer, String> getTI() {
		Hashtable<Integer, String> ret = new Hashtable<>();
		String sTs = getTis();
		String[] arr = sTs.split(";");
		
		for (int i = 0; i < arr.length; i++) {
			ret.put(i + 1, arr[i]);
		}
		return ret;
	}

	public void setTis(String tis) {
		this.tis = tis;
	}

	public boolean isDefault_() {
		return default_;
	}

	public void setDefault_(boolean default_) {
		this.default_ = default_;
	}
}
