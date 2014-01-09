package ua.pr.mod.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.wimpi.modbus.io.ModbusSerialTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import ua.pr.common.ToolsPrLib;
import ua.pr.mod.Main;
import ua.pr.mod.modbus.ToolsModbus;
import ua.pr.mod.xml.objects.ReadRequests;

public class WriteFRM extends JDialog {
	private static final long serialVersionUID = 1L;
	
	public WriteFRM(final ReadRequests rr, final ToolsModbus tm) {
		super(new JFrame(), true);
		setTitle("Reading");
		
		Main.log.log(Level.INFO, "     Write".toUpperCase());
		ToolsPrLib.HideOnEsc(this, false);
//		-------------------------------------------------------------
		JPanel pMain = new JPanel(new GridLayout(3, 2, 20, 1));
		Border border = BorderFactory.createTitledBorder("Параметри");
		pMain.setBorder(border);
		
		pMain.add(new JLabel("Address = ", SwingConstants.RIGHT));
		final JTextField tAddress = new JTextField();
		pMain.add(tAddress);
		
		pMain.add(new JLabel("Offset = ", SwingConstants.RIGHT));
		final JTextField tOffset = new JTextField();
		pMain.add(tOffset);
		
		pMain.add(new JLabel("Value = ", SwingConstants.RIGHT));
		final JTextField tValue = new JTextField();
		pMain.add(tValue);
		
		JPanel pButton = new JPanel();
		JButton btnSend = new JButton("Send");
		pButton.add(btnSend);
		btnSend.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tm.setSerialParameters(rr.getComPortSettings());
					ModbusSerialTransaction trans = tm.getTransaction(false);
					ModbusRequest req = 
							new WriteMultipleRegistersRequest(Integer.parseInt(tOffset.getText(), 16), 
									new Register[] {new SimpleRegister(Integer.parseInt(tValue.getText(), 16))});
					
					req.setUnitID(Integer.parseInt(tAddress.getText()));
					req.setHeadless();
					try {
						trans.setRequest(req);
						Main.log.log(Level.INFO, "S -> " + req.getHexMessage());
						trans.execute();
						Main.log.log(Level.INFO, "R <- " + trans.getResponse().getHexMessage());
					} catch (Exception e2) {
						Main.log.log(Level.SEVERE, e.toString());
					}
				} catch (Exception e2) {
					Main.log.log(Level.SEVERE, e.toString());
				}
			}
		});
		
		add(pMain, BorderLayout.CENTER);
		add(pButton, BorderLayout.SOUTH);
//		-------------------------------------------------------------
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
