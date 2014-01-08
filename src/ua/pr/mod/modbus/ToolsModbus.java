package ua.pr.mod.modbus;

import gnu.io.CommPortIdentifier;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import ua.pr.mod.Main;
import ua.pr.mod.common.NumArray;
import ua.pr.mod.xml.objects.ComPortSettings;
import ua.pr.mod.xml.objects.Device;
import ua.pr.mod.xml.objects.Signal;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusSerialTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.net.SerialConnection;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.ModbusUtil;
import net.wimpi.modbus.util.SerialParameters;

public class ToolsModbus implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String portName;
	private int baudRate;
	private int databits;
	private String parity;
	private int stopbits;
	private String encoding;
	private int receiveTimeout;
	private SerialConnection con;
	private SerialParameters serialParameters;
	private ModbusSerialTransaction trans;
	
	/**
	 * portType = CommPortIdentifier.PORT_SERIAL;
	 *
	 */
	public static List<String> getCOMPortList(int portType) {
		List<String> ret = new ArrayList<>();
		
		Enumeration<?> pList = CommPortIdentifier.getPortIdentifiers();
		while (pList.hasMoreElements()) {
			CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();

		    if (cpi.getPortType() == portType) {
		    	ret.add(cpi.getName());
		    }
		}
		if (ret.size() == 0) {
			ret.add("");
		}
		return ret;
	}
	
	public ModbusSerialTransaction getTransaction(boolean current) {
		if (!current) {
			trans = null;
			if (con != null) {
				con.close();
			}
			
			try {
				serialParameters = new SerialParameters();
				serialParameters.setPortName(portName);
				serialParameters.setBaudRate(baudRate);
				serialParameters.setDatabits(databits);
				serialParameters.setParity(parity);
			    serialParameters.setStopbits(stopbits);
			    serialParameters.setEncoding(encoding);
			    serialParameters.setReceiveTimeout(receiveTimeout);
			    serialParameters.setEcho(false);
			    
			    con = new SerialConnection(serialParameters);
			    con.open();
			    
			    trans = new ModbusSerialTransaction(con);
			} catch (Exception e) {
	
			}			
		}
		return trans;
	}
	
	public void setTransaction(SerialParameters serialParameters) {
		if (con != null) {
			con.close();
		}
		trans = null;
		
		try {
		    con = new SerialConnection(serialParameters);
		    con.open();
		    
		    trans = new ModbusSerialTransaction(con);
		} catch (Exception e) {

		}
	}
	
	public List<Object> getDevices(Device device, Properties props) {
		List<Object> ret = new ArrayList<Object>();
		int[] speeds = ((IntBuffer)props.get("Speed")).array();
		int begAddress = Integer.parseInt(props.getProperty("BegAddress"));
		int endAddress = Integer.parseInt(props.getProperty("EndAddress"));
		endAddress = endAddress > begAddress ? endAddress : begAddress + 3;
		boolean isOne =  Integer.parseInt(props.getProperty("FindOne")) == 1 ? true : false;

//		---------------------------------------------------------------------------
		ModbusSerialTransaction trans = null;
		ReadMultipleRegistersRequest req = 
				new ReadMultipleRegistersRequest(Integer.parseInt(device.getSerialNumberAddress(), 16), 
												 Integer.parseInt(device.getSerialNumberLength()));
		ReadMultipleRegistersResponse res = null;

		for(int speed : speeds) {
			Main.log.log(Level.INFO, "Speed = " + speed);
			setBaudRate(speed);
			trans = getTransaction(false);
		
			for (int i = begAddress; i < endAddress + 1; i++) {	
			    req.setUnitID(i);	
				req.setHeadless();

				trans.setRequest(req);
				try {
					Main.log.log(Level.INFO, "S -> " + req.getHexMessage());
					trans.execute();
					res = (ReadMultipleRegistersResponse) trans.getResponse();
					Main.log.log(Level.INFO, "R <- " + res.getHexMessage());
					
					if (res != null) {
						ByteBuffer bb = ByteBuffer.allocate(res.getWordCount() * 2);
						for (int n = 0; n < res.getWordCount(); n++) {
							bb.put(res.getRegister(n).toBytes());
				        }

						ret.add(new Object[] {device.getName(), req.getUnitID(), 
								ModbusUtil.registersToInt(bb.array()), speed});
						break;
					}
				} catch (ModbusIOException e) {
					System.err.println("ModbusIOException");
				} catch (ModbusSlaveException e) {
					System.err.println("ModbusSlaveException");
				} catch (ModbusException e) {
					System.err.println("ModbusException");
				}
			}
			if ((res != null) && (isOne)) {
				break;
			}
		}

		return ret;
	}

	public int changeTUAkon(int idTU, ModbusSerialTransaction trans, Device sd, int currentAddress) {
		float curVal = (Float) getSignals(currentAddress, sd, trans, "TU", "int").getList().get(idTU - 1);

		int tries = 0;	
		
		String sCode = idTU == 1 ? "314" : "414";
		ModbusRequest req = new ReadMultipleRegistersRequest(Integer.parseInt(sCode, 16), 2);
		trans.setRequest(req);
		req.setUnitID(currentAddress);
		req.setHeadless();

		try {
			tries = trans.execute();
			ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();

			ByteBuffer bb = ByteBuffer.allocate(res.getWordCount() * 2);
			for (int n = 0; n < res.getWordCount(); n++) {
				bb.put(res.getRegister(n).toBytes());
	        }
			
			int rr = ModbusUtil.registersToInt(bb.array());
			if (curVal == 0) {
				rr = rr | 1;
			}
			
			String sReg1 = Integer.toHexString(rr).substring(0,  4);
			String sReg2 = Integer.toHexString(rr).substring(4);
			Register reg1 = new SimpleRegister(Integer.parseInt(sReg1, 16));
			Register reg2 = new SimpleRegister(Integer.parseInt(sReg2, 16));
			
			String sTU = idTU == 1 ? "310" : "410";
			WriteMultipleRegistersRequest wReq = new WriteMultipleRegistersRequest(
					Integer.parseInt(sTU, 16), new Register[] {reg1, reg2});
			wReq.setUnitID(currentAddress);
			wReq.setHeadless();
			
			trans.setRequest(wReq);

			tries = tries + trans.execute();
		} catch (ModbusIOException e1) {
			e1.printStackTrace();
		} catch (ModbusSlaveException e1) {
			e1.printStackTrace();
		} catch (ModbusException e1) {
			e1.printStackTrace();
		}
		
		return tries;
	}
	
	public int changeTUnik1F(int idTU, ModbusSerialTransaction trans, Device device, int currentAddress) {
		String sTU = idTU == 1 ? "1209" : "120B";

		ModbusRequest req = null;		
		int tries = 0;		

		try {
			float curVal = (Float) getSignals(currentAddress, device, trans, "TU", "int").getList().get(idTU - 1);
			
			String sCode = "120D";
			Register regCode = new SimpleRegister(1);
			req = new WriteMultipleRegistersRequest(Integer.parseInt(sCode, 16), new Register[] {regCode});
			trans.setRequest(req);
			req.setUnitID(currentAddress);
			req.setHeadless();
			tries = trans.execute();
		
			regCode = new SimpleRegister(curVal == 1 ? 0 : 1);
			req = new WriteMultipleRegistersRequest(Integer.parseInt(sTU, 16), new Register[] {regCode});
			req.setUnitID(currentAddress);
			req.setHeadless();			
			trans.setRequest(req);
			
			tries = tries + trans.execute();
		} catch (ModbusIOException e1) {
			e1.printStackTrace();
		} catch (ModbusSlaveException e1) {
			e1.printStackTrace();
		} catch (ModbusException e1) {
			e1.printStackTrace();
		}
		return tries;
	}
	
	public NumArray getSignals(int address, Device device, ModbusSerialTransaction trans, 
			String signalName, String signalType) {
		
		if (device.getSignalByName(signalName).getOffset() != null) {
			return getSignalsOnePackage(address, device, trans, signalName, signalType);
		}
//		-------------------------------------------------------------------------------------
		int tries = 0;
		List<Float> res = new ArrayList<>();

		Signal sTI = device.getSignalByName(signalName);
		String[] lSignals = sTI.getAddress().split(";");
		
		ReadMultipleRegistersRequest req = null;
		ReadMultipleRegistersResponse resp = null;
		for (String str : lSignals) {
			req = new ReadMultipleRegistersRequest(Integer.parseInt(str, 16), 2);
			req.setUnitID(address);
			req.setHeadless();
			
			try {
				trans.setRequest(req);
				
				Main.log.log(Level.INFO, "S -> " + req.getHexMessage());
				trans.execute();
				Main.log.log(Level.INFO, "R <- " + trans.getResponse().getHexMessage());
				
				resp = (ReadMultipleRegistersResponse) trans.getResponse();

				for (int i = 0; i < resp.getWordCount()/2; i++) {
					ByteBuffer bb = ByteBuffer.allocate(4);
					for (int j = 0; j < 2; j++) {
						bb.put(resp.getRegister(2 * i + j).toBytes());
					}
					
					if (signalType.toLowerCase().equals("float")) {
						res.add(ModbusUtil.registersToFloat(bb.array()));
					} else if (signalType.toLowerCase().equals("int")) {
						res.add((float)ModbusUtil.registersToInt(bb.array()));
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		return new NumArray(tries, res);
	}
	
	public NumArray getSignalsOnePackage(int address, Device device, ModbusSerialTransaction trans, 
			String signalName, String signalType) {
		
		int tries = 0;
		List<Float> res = new ArrayList<>();

		Signal sTI = device.getSignalByName(signalName);
		ReadMultipleRegistersRequest req = 
				new ReadMultipleRegistersRequest(Integer.parseInt(sTI.getOffset(), 16), sTI.getCount());
		req.setUnitID(address);
		req.setHeadless();

		ReadMultipleRegistersResponse resp = null;
		try {
			trans.setRequest(req);
			Main.log.log(Level.INFO, "S -> " + req.getHexMessage());
			trans.execute();
			Main.log.log(Level.INFO, "R <- " + trans.getResponse().getHexMessage());
			
			resp = (ReadMultipleRegistersResponse) trans.getResponse();

			for (int i = 0; i < resp.getWordCount()/2; i++) {
				ByteBuffer bb = ByteBuffer.allocate(4);
				for (int j = 0; j < 2; j++) {
					bb.put(resp.getRegister(2 * i + j).toBytes());
				}
				
				if (signalType.toLowerCase().equals("float")) {
					res.add(ModbusUtil.registersToFloat(bb.array()));
				} else if (signalType.toLowerCase().equals("int")) {
					res.add((float)ModbusUtil.registersToInt(bb.array()));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new NumArray(tries, res);
	}
	
//	Getters and Setters *********************************************************
	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public int getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public int getDatabits() {
		return databits;
	}

	public void setDatabits(int databits) {
		this.databits = databits;
	}

	public String getParity() {
		return parity;
	}

	public void setParity(String parity) {
		this.parity = parity;
	}

	public int getStopbits() {
		return stopbits;
	}

	public void setStopbits(int stopbits) {
		this.stopbits = stopbits;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getReceiveTimeout() {
		return receiveTimeout;
	}

	public void setReceiveTimeout(int receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}

	public SerialConnection getCon() {
		return con;
	}

	public void setCon(SerialConnection con) {
		this.con = con;
	}

	public SerialParameters getSerialParameters() {
		return serialParameters;
	}

	public void setSerialParameters(SerialParameters serialParameters) {
		this.serialParameters = serialParameters;
	}
	
	public void setSerialParameters(String portName, int baudRate, int databits,
			   String parity, int stopbits, String encoding, int receiveTimeout) {
		this.portName = portName;
		this.baudRate = baudRate;
		this.databits = databits;
		this.parity = parity;
		this.stopbits = stopbits;
		this.encoding = encoding;
		this.receiveTimeout = receiveTimeout;
	}
	
	public void setSerialParameters(ComPortSettings comPortSettings) {
		this.portName = comPortSettings.getPortName();
		this.baudRate = comPortSettings.getBaudRate();
		this.databits = comPortSettings.getDatabits();
		this.parity = comPortSettings.getParity();
		this.stopbits = comPortSettings.getStopbits();
		this.encoding = comPortSettings.getEncoding();
		this.receiveTimeout = comPortSettings.getReceiveTimeout();
	}
}
