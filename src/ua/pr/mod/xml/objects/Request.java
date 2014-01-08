package ua.pr.mod.xml.objects;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Request implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name="name")
	private String name;
	@XmlAttribute(name="modbusId")
	private String modbusId;
	@XmlAttribute(name="offset")
	private String offset;
	@XmlAttribute(name="count")
	private int count;
	@XmlAttribute(name="vars")
	private String vars;
	@XmlAttribute(name="type")
	private String type_;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getModbusId() {
		return modbusId;
	}

	public void setModbusId(String modbusId) {
		this.modbusId = modbusId;
	}

	public String getOffset() {
		return offset;
	}
	
	public void setOffset(String offset) {
		this.offset = offset;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public String getVars() {
		return vars;
	}
	
	public void setVars(String vars) {
		this.vars = vars;
	}

	public String getType_() {
		return type_;
	}

	public void setType_(String type_) {
		this.type_ = type_;
	}
}
