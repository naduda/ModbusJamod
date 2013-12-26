package ua.pr.mod.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.wimpi.modbus.io.ModbusSerialTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.SerialParameters;
import ua.pr.common.ToolsPrLib;
import ua.pr.mod.modbus.ToolsModbus;
import ua.pr.mod.xml.objects.Base;
import ua.pr.mod.xml.objects.Device;
import ua.pr.mod.xml.objects.Speed;

public class Settings extends JDialog {
	private static final long serialVersionUID = 1L;

	private JLabel curAddr;
	private JSpinner spAddr;
	private JLabel curSpeed;
	private JComboBox<Object> cbSpeed;
	private SelectedDevice sd;
	private ToolsModbus tm;
	private ModbusSerialTransaction trans;
	private Base base;
	private TableDevice table;
	
	public Settings(Base base, SelectedDevice sd, ToolsModbus tm, TableDevice table) {
		setModal(true);
		
		this.table = table;
		this.base = base;
		this.sd = sd;
		this.tm = tm;
		trans = tm.getTransaction(true);
//		-----------------------------------------------------		
		JPanel pMain = new JPanel(new GridLayout(0, 2, 5, 1));
		Border border = BorderFactory.createTitledBorder("Параметри");
		pMain.setBorder(border);
		
		curAddr = new JLabel("Адресу " + sd.getAddress() + " змінити на ");
		SpinnerNumberModel modelAddrB = 
				new SpinnerNumberModel(sd.getAddress(), 1, 255, 1);
		spAddr = new JSpinner(modelAddrB);
		curSpeed = new JLabel("Швидкість " + sd.getSpeed() + " змінити на ");
		cbSpeed = new JComboBox<>();
		((JLabel)cbSpeed.getRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);
		int curSpeedId = 0;
		for (Speed speed : base.getSpeeds()) {
			if (speed.getValue() == sd.getSpeed()) {
				curSpeedId = cbSpeed.getItemCount();
			}
			cbSpeed.addItem(speed);
		}
		cbSpeed.setSelectedIndex(curSpeedId);
//		------------------------------------------------
		JPanel pButtons = new JPanel();
		JButton btnOK = new JButton("OK");
		btnOK.setPreferredSize(new Dimension(75, 23));
		btnOK.addActionListener(new MyActionListener("OK"));
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(75, 23));
		btnCancel.addActionListener(new MyActionListener("Exit"));
		pButtons.add(btnOK);
		pButtons.add(btnCancel);
//		-----------------------------------------------------
		pMain.add(curAddr);
		pMain.add(spAddr);
		pMain.add(curSpeed);
		pMain.add(cbSpeed);
		
		add(pMain, BorderLayout.CENTER);
		add(pButtons, BorderLayout.SOUTH);
//		-----------------------------------------------------
		pack();
		setResizable(false);
		setLocationRelativeTo(null);

		setVisible(true);
	}
//	------------------------------------------------------------	
	class MyActionListener implements ActionListener {
		private String name;
		
		public MyActionListener(String name) {
			this.name = name;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (name.toLowerCase().equals("ok")) {
				int newAddress = Integer.parseInt(spAddr.getValue().toString());
				Speed speed = (Speed)cbSpeed.getSelectedItem();
				if ((sd.getAddress() != newAddress) || (sd.getSpeed() != speed.getValue())) {
					changeSettings(newAddress, speed.getId() + 2, (sd.getSpeed() != speed.getValue()));
					
					
				}
			} 
			ToolsPrLib.getActiveForm((JComponent) e.getSource(), Settings.class).setVisible(false);
		}
	}
//	---------------------------------------------------------------------------------------------------------------------
	private void changeSettings(int newAddress, int newSpeed, boolean changSpeed) {
		
		try {			
			Device device = base.getDeviceByName(sd.getType());
			ModbusRequest req = null;
			
			if (sd.getType().toLowerCase().equals("nik 1f")) {
				req = new WriteMultipleRegistersRequest(
						Integer.parseInt(device.getModbusIdAddress(), 16), new Register[] {new SimpleRegister((byte)0, (byte)newAddress)});
			} else if (sd.getType().toLowerCase().equals("akon")) {
				Register reg1 = new SimpleRegister(1);
				Register reg2 = new SimpleRegister((byte)newSpeed, (byte)newAddress);
				req = new WriteMultipleRegistersRequest(
						Integer.parseInt(device.getModbusIdAddress(), 16), new Register[] {reg1, reg2});
			}
			
			trans.setRequest(req);
			req.setUnitID(sd.getAddress());
			req.setHeadless();

			try {
				trans.execute();
			} catch (Exception e) {
				
			}
		
			if (changSpeed) {
				if (sd.getType().toLowerCase().equals("nik 1f")) {
					req = new WriteMultipleRegistersRequest(
							Integer.parseInt("F201", 16), new Register[] {new SimpleRegister((byte)0, (byte)newSpeed)});
					trans.setRequest(req);
					req.setUnitID(newAddress);
					req.setHeadless();
					trans.setRetries(5);
					trans.execute();
				} 
				trans = newTransaction(Integer.parseInt(cbSpeed.getSelectedItem().toString()));
			}

			req = new ReadMultipleRegistersRequest(Integer.parseInt(device.getSerialNumberAddress(), 16), 
					   Integer.parseInt(device.getSerialNumberLength(), 16));
			
			trans.setRequest(req);
			req.setUnitID(newAddress);
			req.setHeadless();
			trans.execute();

			if (saveSettings(newAddress, device)) {
				table.setValueAt(newAddress, table.getSelectedRow(), 1);
				table.setValueAt(Integer.parseInt(cbSpeed.getSelectedItem().toString()), table.getSelectedRow(), 3);
				JOptionPane.showMessageDialog(this,"Налаштування змінено:\nАдреса - " + newAddress + "\nШвидкість - " + cbSpeed.getSelectedItem());
			} else {
				JOptionPane.showMessageDialog(this,"Не вдалося змінити налаштування");
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,"Не вдалося змінити налаштування");
		}
	}

	private boolean saveSettings(int addr, Device device) {
		try {
			Register reg1 = new SimpleRegister(0);
			Register reg2 = sd.getType().toLowerCase().equals("nik 1f") ? new SimpleRegister(3) : new SimpleRegister(0);
			ModbusRequest req = new WriteMultipleRegistersRequest(
					Integer.parseInt(device.getModbusSaveSettings(), 16), new Register[] {reg1, reg2});

			trans.setRequest(req);
			req.setUnitID(addr);
			trans.setRetries(50);
			req.setHeadless();

			trans.execute();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private ModbusSerialTransaction newTransaction(int speed) {
		tm.getSerialParameters();
		SerialParameters serialParameters = tm.getSerialParameters();
		serialParameters.setBaudRate(speed);
		tm.setTransaction(serialParameters);
		
		return tm.getTransaction(true);
	}
}
