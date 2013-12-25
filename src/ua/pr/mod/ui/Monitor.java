package ua.pr.mod.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import ua.pr.mod.common.NumArray;
import ua.pr.mod.modbus.ToolsModbus;
import ua.pr.mod.xml.objects.Device;
import net.wimpi.modbus.io.ModbusSerialTransaction;

public class Monitor extends JDialog {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(Monitor.class.getName());
	
	private List<JLabel> listTS = new ArrayList<>();

	private JPanel pTopTS;
	private JPanel pTI;
	private JPanel pTU;
	private JTextField txtVolt;
	private JTextField txtCur;
	private JTextField txtFreq;
	private JButton btnTU1;
	private JButton btnTU2;
	private JLabel lbStatus;
	
	private List<Float> ts;
	private List<Float> ti;
	private boolean boolTU;
	private int idTU;

	private ToolsModbus tm;
	private int address;
	private Device device;
	private ModbusSerialTransaction trans;
	public Monitor(int address, Device device, ModbusSerialTransaction trans, ToolsModbus tm) {
		setModal(true);
		
		this.address = address;
		this.device = device;
		this.trans = trans;
		this.tm = tm;
//		-----------------------------------------------------
		pTopTS = new JPanel(new GridLayout(1, 0, 20, 1));
		Border border = BorderFactory.createTitledBorder("Телесигнали");
		pTopTS.setBorder(border);
		
		JLabel lTS1 = new JLabel("R");
		setBG(lTS1, Color.GREEN);
		JLabel lTS2 = new JLabel();
		setBG(lTS2, Color.GREEN);
		JLabel lTS3 = new JLabel();
		setBG(lTS3, Color.GREEN);
		JLabel lTS4 = new JLabel();
		setBG(lTS4, Color.GREEN);
		
		listTS.add(lTS1);
		listTS.add(lTS2);
		listTS.add(lTS3);
		listTS.add(lTS4);
		
		pTopTS.add(lTS1);
		pTopTS.add(lTS2);
		pTopTS.add(lTS3);
		pTopTS.add(lTS4);
//		-----------------------------------------------------
		pTI = new JPanel(new GridLayout(0, 2, 5, 1));
		Border borderTI = BorderFactory.createTitledBorder("Телевимірювання");
		pTI.setBorder(borderTI);
		
		pTI.add(new JLabel("Напруга"));
		txtVolt = new JTextField();
		txtVolt.setPreferredSize(new Dimension(60, (int) txtVolt.getPreferredSize().getHeight()));
		pTI.add(txtVolt);
		pTI.add(new JLabel("Струм"));
		txtCur = new JTextField();
		pTI.add(txtCur);
		pTI.add(new JLabel("Частота"));
		txtFreq = new JTextField();
		pTI.add(txtFreq);
//		-----------------------------------------------------
		pTU = new JPanel(new GridLayout(1, 0, 5, 1));
		Border borderTU = BorderFactory.createTitledBorder("Телевуправління");
		pTU.setBorder(borderTU);
		
		btnTU1 = new JButton("ТУ-1");
		btnTU2 = new JButton("ТУ-2");
		btnTU1.addActionListener(new MyActionListener(1, address, device, trans, tm));
		btnTU2.addActionListener(new MyActionListener(2, address, device, trans, tm));
		pTU.add(btnTU1);
		pTU.add(btnTU2);

		JPanel pBott = new JPanel();
		BoxLayout boxy = new BoxLayout(pBott, BoxLayout.Y_AXIS);
		pBott.setLayout(boxy);
		pBott.add(pTU);
		
		JPanel pStatus = new JPanel();
		Border borderStatus = BorderFactory.createEmptyBorder();
		pStatus.setBorder(borderStatus);
		
		lbStatus = new JLabel("");
		pStatus.add(lbStatus);
		pBott.add(pStatus);
//		-----------------------------------------------------
		add(pTopTS, BorderLayout.NORTH);
		add(pTI, BorderLayout.CENTER);
		add(pBott, BorderLayout.SOUTH);
//		-----------------------------------------------------
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
	
		addWindowListener(new MyWindowListener());
		setVisible(true);
	}
	
	@SuppressWarnings("unchecked")
	public void refreshForm() {
		int iter = 0;
		int iterFault = 0;
		int tries = 0;
		while(isVisible()) {
			try {
				lbStatus.setText("Cycles - " + iter + " (errors - " + iterFault + ")");
				NumArray resTS = tm.getSignals(address, device, trans, "TS", "int");
				
				tries = resTS.getCounter();				
				List<Float> ts = (List<Float>) resTS.getList();

				NumArray resTI = tm.getSignals(address, device, trans, "TI", "float");

				List<Float> ti = (List<Float>) resTI.getList();
				tries = tries + resTI.getCounter();
				if (boolTU) {
					if (device.getName().toLowerCase().equals("akon")) {
						tries = tries + tm.changeTUAkon(idTU, trans, device, address);
					} else if (device.getName().toLowerCase().equals("nik 1f")) {
						tries = tries + tm.changeTUnik1F(idTU, trans, device, address);
					}
					
					iterFault = iterFault + tries;
					boolTU = false;
				}					
				
				setTs(ts);
				setTi(ti);
				
				int indTS = 0;
				Color col = null;
				for (Float tsi : ts) {
					if(tsi == 1) {
						col = Color.RED;
					} else {
						col = Color.GREEN;
					}
					setBG(listTS.get(indTS), col);
					indTS++;
				}
				
				if (device.getName().toLowerCase().equals("nik 1f")) {
					txtCur.setText(String.valueOf(ti.get(0)/1000));
					txtVolt.setText(String.valueOf(ti.get(1)/1000));
					txtFreq.setText(ti.get(5).toString());
				} else if (device.getName().toLowerCase().equals("akon")) {
					txtCur.setText(String.valueOf(ti.get(0)/1000));
					txtVolt.setText(String.valueOf(ti.get(1)/1000));
//					txtFreq.setText(ti.get(5).toString());
				}
								
				iter++;
				iterFault = iterFault + tries;
				tries = 0;
			} catch (Exception e) {
				log.log(Level.INFO, "Exception: ", e);
				iterFault++;
			}
		}
		tm.getCon().close();
	}
	
	private void setBG(JComponent comp, Color col) {
		comp.setBackground(col);
		comp.setForeground(col);
		comp.setOpaque(true);	
	}
//	--------------------------------------------------------------	
	class MyWindowListener implements WindowListener {
		
		@Override
		public void windowOpened(WindowEvent e) {
			new Thread()
			{
			    public void run() {
			    	refreshForm();
			    }
			}.start();
		}
		
		@Override
		public void windowActivated(WindowEvent e) {

		}
	
		@Override
		public void windowClosed(WindowEvent e) {

		}
	
		@Override
		public void windowClosing(WindowEvent e) {

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
	}

	class MyActionListener implements ActionListener {
		private int value;
		
		public MyActionListener(int value, int address, Device device, ModbusSerialTransaction trans, ToolsModbus tm) {
			this.value = value;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			idTU = value;
			boolTU = true;	
		}
		
	}
	//	--------------------------------------------------------------
	public List<Float> getTs() {
		return ts;
	}

	public void setTs(List<Float> ts) {
		this.ts = ts;
	}

	public List<Float> getTi() {
		return ti;
	}

	public void setTi(List<Float> ti) {
		this.ti = ti;
	}
}
