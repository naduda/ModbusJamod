package ua.pr.mod.ui;

import gnu.io.CommPortIdentifier;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import ua.pr.common.ToolsPrLib;
import ua.pr.mod.Main;
import ua.pr.mod.modbus.ToolsModbus;
import ua.pr.mod.xml.objects.Base;
import ua.pr.mod.xml.objects.ComPortSettings;
import ua.pr.mod.xml.objects.Device;
import ua.pr.mod.xml.objects.Speed;

public class FindSettingsFRM extends JDialog implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Base base;
	private TableDevice table;
	
	private JComboBox<Object> cbType;
	private JComboBox<Object> cbPort;
	private JSpinner spTyme;
	private JCheckBox cbFindOne;
	private JSpinner spAddrB;
	private JSpinner spAddrE;
	private JComboBox<Object> cbSpeedB;
	private JComboBox<Object> cbSpeedE;
	
	public FindSettingsFRM(final Base base, TableDevice table, ToolsModbus tm) {
		super(new JFrame(), true);
		setTitle("��������� ������");
		this.base = base;
		this.table = table;
		
//		----------------------------------------------
		JPanel pMain = new JPanel(new GridLayout(0, 2, 5, 1));
		Border border = BorderFactory.createTitledBorder("���������");
		pMain.setBorder(border);

		GridLayout gr = new GridLayout(0,2,5,1);
		
		JPanel pMainR = new JPanel(gr);
		JPanel pMainL = new JPanel(gr);
		
		pMain.add(pMainR);
		pMain.add(pMainL);
		
		JLabel lbType = new JLabel("��� ��������");
		cbType = new JComboBox<>();
		((JLabel)cbType.getRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);
		int defId = 0;
		for (Device dev : base.getDevices()) {
			if (dev.isDefault_()) {
				defId = cbType.getItemCount();
			}
			cbType.addItem(dev);
		}
		cbType.setSelectedIndex(defId);
		
		JLabel lbPort = new JLabel("���-����");
		cbPort = new JComboBox<>();
		((JLabel)cbPort.getRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);
		for (String st : ToolsModbus.getCOMPortList(CommPortIdentifier.PORT_SERIAL)) {
			cbPort.addItem(st);
		}
		JLabel lbTyme = new JLabel("������� ����������");
		SpinnerNumberModel model = new SpinnerNumberModel(100, 100, 1000, 50);
		spTyme = new JSpinner(model);
		JLabel lbFindOne = new JLabel("������ ���� �������");
		cbFindOne = new JCheckBox();
		cbFindOne.setSelected(true);
		
		pMainR.add(lbType);
		pMainR.add(cbType);
		pMainR.add(lbPort);
		pMainR.add(cbPort);
		pMainR.add(lbTyme);
		pMainR.add(spTyme);
		pMainR.add(lbFindOne);
		pMainR.add(cbFindOne);		
		
		JLabel lbAddrB = new JLabel("��������� ������");
		final SpinnerNumberModel modelAddrB = 
				new SpinnerNumberModel(base.getDeviceByName(cbType.getSelectedItem().toString()).getBeginAddress(), 1, 255, 1);
		spAddrB = new JSpinner(modelAddrB);
		JLabel lbAddrE = new JLabel("ʳ����� ������");
		final SpinnerNumberModel modelAddrE = 
				new SpinnerNumberModel(base.getDeviceByName(cbType.getSelectedItem().toString()).getEndAddress(), 1, 255, 1);
		spAddrE = new JSpinner(modelAddrE);
		
		cbType.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				modelAddrB.setValue(base.getDeviceByName(cbType.getSelectedItem().toString()).getBeginAddress());
				modelAddrE.setValue(base.getDeviceByName(cbType.getSelectedItem().toString()).getEndAddress());;
			}
		});
		
		JLabel lbSpeedB = new JLabel("��������� ��������");
		cbSpeedB = new JComboBox<>();
		((JLabel)cbSpeedB.getRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lbSpeedE = new JLabel("ʳ����� ��������");
		cbSpeedE = new JComboBox<>();
		((JLabel)cbSpeedE.getRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);
		int indBeg = 0;
		int indEnd = 0;
		for (Speed speed : base.getSpeeds()) {
			if (speed.isDefaultBeg()) {
				indBeg = cbSpeedB.getItemCount();
			}
			if (speed.isDefaultEnd()) {
				indEnd = cbSpeedE.getItemCount();
			}	
			cbSpeedB.addItem(speed);
			cbSpeedE.addItem(speed);
		}
		cbSpeedB.setSelectedIndex(indBeg);
		cbSpeedE.setSelectedIndex(indEnd);
		cbSpeedB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cbSpeedB.getSelectedIndex() > cbSpeedE.getSelectedIndex()) {
					cbSpeedE.setSelectedIndex(cbSpeedB.getSelectedIndex());
				}				
			}
		});
		
		pMainL.add(lbAddrB);
		pMainL.add(spAddrB);
		pMainL.add(lbAddrE);
		pMainL.add(spAddrE);
		pMainL.add(lbSpeedB);
		pMainL.add(cbSpeedB);
		pMainL.add(lbSpeedE);
		pMainL.add(cbSpeedE);
//		------------------------------------------------
		JPanel pButtons = new JPanel();
		JButton btnOK = new JButton("OK");
		btnOK.setPreferredSize(new Dimension(75, 23));
		btnOK.addActionListener(new ActionListenerBTN("Find", tm));
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(75, 23));
		btnCancel.addActionListener(new ActionListenerBTN("Exit", tm));
		pButtons.add(btnOK);
		pButtons.add(btnCancel);
//		----------------------------------------------
		add(pMain, BorderLayout.CENTER);
		add(pButtons, BorderLayout.SOUTH);
//		----------------------------------------------
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
//	*******************************************************************
	private class ActionListenerBTN implements ActionListener {

		private String name;
		private ToolsModbus tm;
		
		public ActionListenerBTN(String name, ToolsModbus tm) {
			this.name = name;
			this.tm = tm;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (name.toLowerCase().equals("find")) {
				ToolsPrLib.getActiveForm((JComponent) e.getSource(), FindSettingsFRM.class).setVisible(false);
				
				if ("".equals(cbPort.getSelectedItem().toString())) {
					Main.log.log(Level.SEVERE, "Not found COM-Port");
					return;
				}
				
				Main.log.log(Level.INFO, "     Finding device(s)".toUpperCase());
				table.removeAllRows();
				
				Properties props = new Properties();
				props.put("Type", cbType.getSelectedItem().toString());
				props.put("Port", cbPort.getSelectedItem().toString());
				props.put("TimeOut", spTyme.getValue().toString());
				props.put("FindOne", cbFindOne.isSelected() ? "1" : "0");
				props.put("BegAddress", spAddrB.getValue().toString());
				props.put("EndAddress", spAddrE.getValue().toString());
				
				IntBuffer sbSpeed = 
						IntBuffer.allocate(cbSpeedE.getSelectedIndex() - cbSpeedB.getSelectedIndex() + 1);
				for (int i = cbSpeedB.getSelectedIndex(); i < cbSpeedE.getSelectedIndex() + 1; i++) {
					sbSpeed.put(Integer.parseInt(cbSpeedB.getItemAt(i).toString()));
				}
				props.put("Speed", sbSpeed);
				
				ComPortSettings cps = base.getComPortSettings();
				
				tm.setSerialParameters(cbPort.getSelectedItem().toString(), 
						Integer.parseInt(cbSpeedB.getSelectedItem().toString()), 
						cps.getDatabits(), cps.getParity(), cps.getStopbits(), cps.getEncoding(), 
						Integer.parseInt(spTyme.getValue().toString()));
				
				Device device = base.getDeviceByName(cbType.getSelectedItem().toString());

				List<Object> rets = tm.getDevices(device, props);
				table.putClientProperty("SerialParameters", tm.getSerialParameters());
				for (Object object : rets) {
					table.addRow((Object[]) object);
				}
			}			
		}		
	}
}
