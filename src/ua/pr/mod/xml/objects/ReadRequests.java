package ua.pr.mod.xml.objects;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Base")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReadRequests implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name="COM")
	private ComPortSettings comPortSettings;
	@XmlElementWrapper(name="ReadRequests")
	@XmlElement(name="Request")
	private List<Request> requests;

	public ComPortSettings getComPortSettings() {
		return comPortSettings;
	}

	public void setComPortSettings(ComPortSettings comPortSettings) {
		this.comPortSettings = comPortSettings;
	}

	public List<Request> getRequests() {
		return requests;
	}

	public void setRequests(List<Request> requests) {
		this.requests = requests;
	}
}
