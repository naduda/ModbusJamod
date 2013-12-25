package ua.pr.mod.xml.objects;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

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
	@XmlElementWrapper
	@XmlElement(name="signal")
	private List<Signal> signals;
	@XmlAttribute(name="default")
	private boolean default_;
	@XmlAttribute(name="beginAddress")
	private int beginAddress;
	@XmlAttribute(name="endAddress")
	private int endAddress;
	
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

	public List<Signal> getSignals() {
		return signals;
	}

	public void setSignals(List<Signal> signals) {
		this.signals = signals;
	}

	public boolean isDefault_() {
		return default_;
	}

	public void setDefault_(boolean default_) {
		this.default_ = default_;
	}
	
	public int getBeginAddress() {
		return beginAddress;
	}

	public void setBeginAddress(int beginAddress) {
		this.beginAddress = beginAddress;
	}

	public int getEndAddress() {
		return endAddress;
	}

	public void setEndAddress(int endAddress) {
		this.endAddress = endAddress;
	}

	//	----------------------------------------------
	public Signal getSignalByName(String name) {
		for (Signal s : signals) {
			if (s.getType().toLowerCase().equals(name.toLowerCase())) {
				return s;
			}
		}
		return null;
	}
}
