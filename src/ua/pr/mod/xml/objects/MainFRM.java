package ua.pr.mod.xml.objects;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class MainFRM implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute
	private String title;
	@XmlAttribute
	private String icoPath;
	@XmlAttribute
	private String pathOfFormState;
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getIcoPath() {
		return icoPath;
	}
	
	public void setIcoPath(String icoPath) {
		this.icoPath = icoPath;
	}

	public String getPathOfFormState() {
		return pathOfFormState;
	}

	public void setPathOfFormState(String pathOfFormState) {
		this.pathOfFormState = pathOfFormState;
	}
}
