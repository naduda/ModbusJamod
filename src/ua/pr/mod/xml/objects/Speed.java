package ua.pr.mod.xml.objects;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Speed implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name="id")
	private int id;
	@XmlAttribute(name="value")
	private int value;
	@XmlAttribute(name="defaultBeg")
	private boolean defaultBeg;
	@XmlAttribute(name="defaultEnd")
	private boolean defaultEnd;
	
	@Override
	public String toString() {
		return "" + value;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

	public boolean isDefaultBeg() {
		return defaultBeg;
	}

	public void setDefaultBeg(boolean defaultBeg) {
		this.defaultBeg = defaultBeg;
	}

	public boolean isDefaultEnd() {
		return defaultEnd;
	}

	public void setDefaultEnd(boolean defaultEnd) {
		this.defaultEnd = defaultEnd;
	}
}
