package ua.pr.mod.xml.objects;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name="Base")
@XmlAccessorType(XmlAccessType.FIELD)
public class Base implements Serializable{
	private static final long serialVersionUID = 1L;

	@XmlElement(name="mainForm")
	private MainFRM mainForm;
	@XmlElement(name="COM")
	private ComPortSettings comPortSettings;
	@XmlElementWrapper
	@XmlElement(name="speed")
	private List<Speed> speeds;
	@XmlElementWrapper
	@XmlElement(name="device")
	private List<Device> devices;
	@XmlElement(name="findAddress")
	private FindAddress findAddress;

	public Device getDeviceByName(String name) {
		for (Device d : devices) {
			if (d.getName().toLowerCase().equals(name.toLowerCase())) {
				return d;
			}
		}
		return null;
	}
	
	public MainFRM getMainForm() {
		return mainForm;
	}

	public void setMainForm(MainFRM mainForm) {
		this.mainForm = mainForm;
	}

	public ComPortSettings getComPortSettings() {
		return comPortSettings;
	}

	public void setComPortSettings(ComPortSettings comPortSettings) {
		this.comPortSettings = comPortSettings;
	}

	public List<Speed> getSpeeds() {
		return speeds;
	}

	public void setSpeeds(List<Speed> speeds) {
		this.speeds = speeds;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public FindAddress getFindAddress() {
		return findAddress;
	}

	public void setFindAddress(FindAddress findAddress) {
		this.findAddress = findAddress;
	}	
}
