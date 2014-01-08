package ua.pr.mod.ui;

import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import net.wimpi.modbus.io.ModbusSerialTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.util.ModbusUtil;
import ua.pr.common.ToolsPrLib;
import ua.pr.mod.Main;
import ua.pr.mod.modbus.ToolsModbus;
import ua.pr.mod.xml.objects.ReadRequests;
import ua.pr.mod.xml.objects.Request;

public class ReadFRM extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private ToolsModbus tm;
	private ReadRequests rr;
	
	private List<JLabel> listLabels;
	
	public ReadFRM(ReadRequests rr, ToolsModbus tm) {
		super(new JFrame(), true);
		setTitle("Reading");
		
		this.tm = tm;
		this.rr = rr;
		
		Main.log.log(Level.INFO, "     Reading".toUpperCase());
		listLabels = new ArrayList<>();
//		----------------------------------------------
		JPanel pMain = new JPanel(new GridLayout(0, 1, 5, 1));
		for (Request r : rr.getRequests()) {
			JPanel pan = new JPanel(new GridLayout(0, 1, 5, 1));
			Border border = BorderFactory.createTitledBorder(r.getName());
			pan.setBorder(border);
			String[] vars = r.getVars().split(";");
			for (String vv : vars) {
				String[] it = vv.split(":");
				JLabel lb = new JLabel();
				
				lb.setText(it[0] + " =   0.00000   " + (it.length < 3 ? "" : it[2]));
				lb.putClientProperty("userProp", vv);
				listLabels.add(lb);
				pan.add(listLabels.get(listLabels.size() - 1));
			}
			pMain.add(pan);
		}
		add(pMain);
//		----------------------------------------------
		addWindowListener(new MyWindowListener());
		
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	ModbusSerialTransaction trans = null;
	public void refreshForm() {
		List<Float> res = null;
		while(isVisible()) {
			res = new ArrayList<Float>();
			try {
				if (trans == null) {
					tm.setSerialParameters(rr.getComPortSettings());
					trans = tm.getTransaction(false);
				}
				for (Request r : rr.getRequests()) {
					ReadMultipleRegistersRequest req = 
							new ReadMultipleRegistersRequest(Integer.parseInt(r.getOffset(), 16), r.getCount());
					req.setUnitID(Integer.parseInt(r.getModbusId()));
					req.setHeadless();

					ReadMultipleRegistersResponse resp = new ReadMultipleRegistersResponse();
					try {
						trans.setRequest(req);
						Main.log.log(Level.INFO, "S -> " + req.getHexMessage());
						trans.execute();
						Main.log.log(Level.INFO, "R <- " + trans.getResponse().getHexMessage());
						resp = (ReadMultipleRegistersResponse) trans.getResponse();
						if (resp != null) {
							for (int i = 0; i < resp.getWordCount()/2; i++) {
								ByteBuffer bb = ByteBuffer.allocate(4);
								for (int j = 0; j < 2; j++) {
									bb.put(resp.getRegister(2 * i + j).toBytes());
								}
								
								if (r.getType_().toLowerCase().equals("float")) {
									res.add(ModbusUtil.registersToFloat(bb.array()));
								} else if (r.getType_().toLowerCase().equals("int")) {
									res.add((float)ModbusUtil.registersToInt(bb.array()));
								}
							}
						}
					} catch (Exception e) {
						Main.log.log(Level.SEVERE, e.toString());
					}
				}
			} catch (Exception e) {
				Main.log.log(Level.SEVERE, e.toString());
			}
			//-------------------------------------
			for (int i = 0; i < res.size(); i++) {
				JLabel lb = listLabels.get(i);
				String[] it = lb.getClientProperty("userProp").toString().split(":");
				lb.setText(ToolsPrLib.fixedLenthString(it[0], 5) + " =   " + 
				ToolsPrLib.customFormat("0.000", res.get(i)) + "   " + (it.length < 3 ? "" : it[2]));
			}
		}
	}
//	--------------------------------------------------------------	
	class MyWindowListener implements WindowListener {
		@Override
		public void windowOpened(WindowEvent arg0) {
			new Thread()
			{
			    public void run() {
			    	refreshForm();
			    }
			}.start();
		}
		@Override
		public void windowActivated(WindowEvent arg0) {
			
		}
	
		@Override
		public void windowClosed(WindowEvent arg0) {
			
		}
	
		@Override
		public void windowClosing(WindowEvent arg0) {
			
		}
	
		@Override
		public void windowDeactivated(WindowEvent arg0) {
			
		}
	
		@Override
		public void windowDeiconified(WindowEvent arg0) {
			
		}
	
		@Override
		public void windowIconified(WindowEvent arg0) {
			
		}		
	}
}
