package ua.pr.mod.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.wimpi.modbus.io.ModbusSerialTransaction;
import net.wimpi.modbus.util.SerialParameters;
import ua.pr.common.ToolsPrLib;
import ua.pr.menu.FrameXMLMenuLoader;
import ua.pr.menu.XMLMenuLoader;
import ua.pr.mod.modbus.ToolsModbus;
import ua.pr.mod.ui.MainFrame;
import ua.pr.mod.xml.objects.Base;

public class MainFrame extends FrameXMLMenuLoader implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String MENU_XML_PATH = "Menu.xml";
	
	private XMLMenuLoader loader;
	private Base base;
	private TableDevice table;
	private JScrollPane spTable;
	private JPanel pMain;
	
	public MainFrame(Base base) {
		super(base.getMainForm().getTitle(), MENU_XML_PATH);
		this.base = base;

		loader = getLoader();
//		-------------------------------------------------------------
		ToolsModbus tm = new ToolsModbus();
		
		JButton btnExit = (JButton) loader.getMenuItem("btnExit");
		btnExit.addActionListener(new WindowListenerMainFRM("Exit", tm));
		
		JButton btnFind = (JButton) loader.getMenuItem("btnFind");
		btnFind.addActionListener(new WindowListenerMainFRM("Find", tm));
		
		JButton btnMonitor = (JButton) loader.getMenuItem("btnMonitor");
		btnMonitor.addActionListener(new WindowListenerMainFRM("Monitor", tm));
//		-------------------------------------------------------------
		JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel lbStatus = new JLabel("status");
		statusBar.add(lbStatus);
		add(statusBar, BorderLayout.SOUTH);
//		-------------------------------------------------------------
		addWindowListener(new WindowListenerMainFRM());
	}

	
//	********************************************************************
	class WindowListenerMainFRM implements WindowListener, ActionListener, Serializable {
		private static final long serialVersionUID = 1L;

		private String btnName = "";
		private ToolsModbus tm;
		
		public WindowListenerMainFRM() {
			
		}
		
		public WindowListenerMainFRM(String btnName, Object obj, ToolsModbus tm) {
			this.btnName = btnName; 
			this.tm = tm;
		}
		
		public WindowListenerMainFRM(String btnName, ToolsModbus tm) {
			this.btnName = btnName;
			this.tm = tm;
		}
		
		public void actionPerformed(ActionEvent e) {
			if (btnName.toLowerCase().equals("exit")) {
				if (tm.getCon() != null) {
					try {
						tm.getCon().close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
				System.exit(0);
			} else if(btnName.toLowerCase().equals("find")) {
				new FindSettingsFRM(base, table, tm);
			} else if(btnName.toLowerCase().equals("monitor")) {
				SelectedDevice sd = new SelectedDevice(table);
				SerialParameters sp = (SerialParameters) table.getClientProperty("SerialParameters");
				sp.setBaudRate(sd.getSpeed());
				
				ModbusSerialTransaction trans = tm.getTransaction(sp);
				
				if (sd.getType().toLowerCase().equals("nik 1f")) {
					new Monitor(sd.getAddress(), base.getDeviceByName(sd.getType()), trans, tm);
				} else if (sd.getType().toLowerCase().equals("akon")) {
					new Monitor(sd.getAddress(), base.getDeviceByName(sd.getType()), trans, tm);
				}
			}
		}	
		
		public void windowClosing(WindowEvent e) {
			remove(pMain);
			ObjectOutputStream oos = null;
			try {
				String pathOfFormState = ToolsPrLib.getFullPath(base.getMainForm().getPathOfFormState());
				oos = new ObjectOutputStream(new FileOutputStream(new File(pathOfFormState)));
				oos.writeObject(e.getSource());
				System.out.println("end");
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				try {
					oos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
	}

//	********************************************************************
	public void addMainPanel() {
		pMain = new JPanel();
		table = new TableDevice();
		spTable = new JScrollPane(table);
		spTable.setPreferredSize(new Dimension(500, 150));
		pMain.add(spTable);
		add(pMain, BorderLayout.CENTER);
	}
}
