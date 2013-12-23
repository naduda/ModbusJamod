package ua.pr.mod.ui;

import java.io.Serializable;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TableDevice extends JTable implements Serializable {
	private static final long serialVersionUID = 1L;

	private DefaultTableModel dtm;
	
	public TableDevice() {
		dtm = new DefaultTableModel();
		dtm.setColumnIdentifiers(new String[] {"Тип", "Адреса", "Серійний номер", "Швидкість"});
		setModel(dtm);
	}
	
	public void addRow(Object[] row) {
		dtm.addRow(row);
	}
	
	public void removeAllRows() {
		dtm.setRowCount(0);
	}
}
