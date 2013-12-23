package ua.pr.mod.ui;

import javax.swing.JTable;

public class SelectedDevice {
	private String type;
	private int address;
	private int serialNumber;
	private int speed;
	
	public SelectedDevice() {
		
	}
	
	public SelectedDevice(JTable table) {
		if ((table != null) && (table.getSelectedRow() != -1)) {
			setType(table.getValueAt(table.getSelectedRow(), 0).toString());
			setAddress((int)table.getValueAt(table.getSelectedRow(), 1));
			setSerialNumber((int)table.getValueAt(table.getSelectedRow(), 2));
			setSpeed((int)table.getValueAt(table.getSelectedRow(), 3));
		}		
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getAddress() {
		return address;
	}
	
	public void setAddress(int address) {
		this.address = address;
	}
	
	public int getSerialNumber() {
		return serialNumber;
	}
	
	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
