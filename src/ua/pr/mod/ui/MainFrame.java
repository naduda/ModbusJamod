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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.wimpi.modbus.util.SerialParameters;
import ua.pr.common.ToolsPrLib;
import ua.pr.menu.FrameXMLMenuLoader;
import ua.pr.menu.XMLMenuLoader;
import ua.pr.mod.modbus.ToolsModbus;
import ua.pr.mod.ui.MainFrame;
import ua.pr.mod.xml.EntityFromXML;
import ua.pr.mod.xml.objects.Base;
import ua.pr.mod.xml.objects.ReadRequests;

public class MainFrame extends FrameXMLMenuLoader implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String MENU_XML_PATH = "Menu.xml";
	
	private XMLMenuLoader loader;
	private Base base;
	private TableDevice table;
	private JScrollPane spTable;
	private JPanel pMain;
	private ToolsModbus tm;
	
	public MainFrame(Base base) {
		super(base.getMainForm().getTitle(), MENU_XML_PATH);
		this.base = base;

		loader = getLoader();
//		-------------------------------------------------------------
		tm = new ToolsModbus();
		
		JButton btnExit = (JButton) loader.getMenuItem("btnExit");
		btnExit.addActionListener(new WindowListenerMainFRM("Exit"));
		
		JButton btnFind = (JButton) loader.getMenuItem("btnFind");
		btnFind.addActionListener(new WindowListenerMainFRM("Find"));
		
		JButton btnMonitor = (JButton) loader.getMenuItem("btnMonitor");
		btnMonitor.addActionListener(new WindowListenerMainFRM("Monitor"));
		
		JButton btnSettings = (JButton) loader.getMenuItem("btnSettings");
		btnSettings.addActionListener(new WindowListenerMainFRM("Settings"));
		
		@SuppressWarnings("unchecked")
		JComboBox<String> cbTemplates = (JComboBox<String>) loader.getMenuItem("cbTemplates");
		File folder = new File(System.getProperty("user.dir"));
		FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File directory, String fileName) {
	        	if ((fileName.toLowerCase().endsWith(".xml")) && 
	        	   (!fileName.toLowerCase().equals("menu.xml")) &&
	     	       (!fileName.toLowerCase().equals("settings.xml"))) {
					return true;
				} else {
					return false;
				}
	        }};
		File[] listOfFiles = folder.listFiles(filter);

	    for (int i = 0; i < listOfFiles.length; i++) {
	    	if (listOfFiles[i].isFile()) {
	    		cbTemplates.addItem(new String(listOfFiles[i].getName()));
	    	}
	    }
	    
	    JButton btnRead = (JButton) loader.getMenuItem("btnRead");
	    btnRead.addActionListener(new WindowListenerMainFRM("Read", cbTemplates));
	    
	    JButton btnWrite = (JButton) loader.getMenuItem("btnWrite");
	    btnWrite.addActionListener(new WindowListenerMainFRM("Write", cbTemplates));
//		-------------------------------------------------------------
		JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel lbStatus = new JLabel("status");
		statusBar.add(lbStatus);
		add(statusBar, BorderLayout.SOUTH);
//		-------------------------------------------------------------
		addWindowListener(new WindowListenerMainFRM());
	}

	private FindSettingsFRM fFind = null;
//	********************************************************************
	private void closeConnection() {
		if (tm.getCon() != null) {
			try {
				tm.getCon().close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		System.exit(0);
	}
	class WindowListenerMainFRM implements WindowListener, ActionListener, Serializable {
		private static final long serialVersionUID = 1L;

		private String btnName = "";
		private Object obj;
		
		public WindowListenerMainFRM() {
			
		}
		
		public WindowListenerMainFRM(String btnName, Object obj) {
			this.btnName = btnName; 
			this.obj = obj;
		}
		
		public WindowListenerMainFRM(String btnName) {
			this.btnName = btnName;
		}
		
		public void actionPerformed(ActionEvent e) {
			if (btnName.toLowerCase().equals("exit")) {
				closeConnection();				
				System.exit(0);
			} else if(btnName.toLowerCase().equals("find")) {
				if (fFind != null) {
					fFind.setVisible(true);
				} else {
					fFind = new FindSettingsFRM(base, table, tm);
				}
			} else if(btnName.toLowerCase().equals("monitor")) {
				SelectedDevice sd = new SelectedDevice(table);
				SerialParameters sp = (SerialParameters) table.getClientProperty("SerialParameters");
				sp.setBaudRate(sd.getSpeed());
				
				tm.setTransaction(sp);
				
				if (sd.getType().toLowerCase().equals("nik 1f")) {
					new Monitor(sd.getAddress(), base.getDeviceByName(sd.getType()), tm);
				} else if (sd.getType().toLowerCase().equals("akon")) {
					new Monitor(sd.getAddress(), base.getDeviceByName(sd.getType()), tm);
				}
			} else if(btnName.toLowerCase().equals("settings")) {
				SelectedDevice sd = new SelectedDevice(table);
				SerialParameters sp = (SerialParameters) table.getClientProperty("SerialParameters");
				sp.setBaudRate(sd.getSpeed());
				
				tm.setTransaction(sp);
				
				if (sd.getType().toLowerCase().equals("nik 1f")) {
					new Settings(base, sd, tm, table);
				} else if (sd.getType().toLowerCase().equals("akon")) {
					new Settings(base, sd, tm, table);
				}
			} else if(btnName.toLowerCase().equals("read")) {
				@SuppressWarnings("unchecked")
				File f = new File(System.getProperty("user.dir") + File.separator + 
						((JComboBox<String>)obj).getSelectedItem());
				EntityFromXML efx = new EntityFromXML();
				ReadRequests rRequest = (ReadRequests) efx.getObject(f.getAbsolutePath(), ReadRequests.class);

				new ReadFRM(rRequest, tm);
			} else if(btnName.toLowerCase().equals("write")) {
				@SuppressWarnings("unchecked")
				File f = new File(System.getProperty("user.dir") + File.separator + 
						((JComboBox<String>)obj).getSelectedItem());
				EntityFromXML efx = new EntityFromXML();
				ReadRequests rRequest = (ReadRequests) efx.getObject(f.getAbsolutePath(), ReadRequests.class);
				
				new WriteFRM(rRequest, tm);
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
				closeConnection();
				try {
					oos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		@Override
		public void windowActivated(WindowEvent e) {			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			closeConnection();
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			
		}

		@Override
		public void windowOpened(WindowEvent e) {
			
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
