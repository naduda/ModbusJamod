package ua.pr.mod.modbus;

import gnu.io.CommPortIdentifier;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import ua.pr.mod.common.NumArray;
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
	
	public ModbusSerialTransaction getTransaction() {
		ModbusSerialTransaction trans = null;
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
		return trans;
	}
	
	public ModbusSerialTransaction getTransaction(SerialParameters serialParameters) {
		ModbusSerialTransaction trans = null;
		if (con != null) {
			con.close();
		}
		
		try {
		    con = new SerialConnection(serialParameters);
		    con.open();
		    
		    trans = new ModbusSerialTransaction(con);
		} catch (Exception e) {

		}
		return trans;
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
		
		boolean endFor = false;
		for(int speed : speeds) {
			if (endFor) {
				break;
			}
			setBaudRate(speed);
			trans = getTransaction();
		
			for (int i = begAddress; i < endAddress + 1; i++) {	
				ReadMultipleRegistersResponse res = null;
			    req.setUnitID(i);	
				req.setHeadless();
				
				trans.setRequest(req);		
				try {
					trans.execute();
					res = (ReadMultipleRegistersResponse) trans.getResponse();
					
					if (res != null) {
						ByteBuffer bb = ByteBuffer.allocate(res.getWordCount() * 2);
						for (int n = 0; n < res.getWordCount(); n++) {
							bb.put(res.getRegister(n).toBytes());
				        }

						ret.add(new Object[] {device.getName(), req.getUnitID(), 
								ModbusUtil.registersToInt(bb.array()), speed});
						if(isOne) {
							endFor = true;
							break;
						}
					}
				} catch (ModbusIOException e) {
					System.err.println("ModbusIOException");
				} catch (ModbusSlaveException e) {
					System.err.println("ModbusSlaveException");
				} catch (ModbusException e) {
					System.err.println("ModbusException");
				}
			}
		}

		return ret;
	}

	public void changeTUAkon(int idTU, SerialParameters sp, Device sd, int currentAddress) {
		trans = getTransaction(sp);
		int curVal = getTSAkon(idTU, null, sd, currentAddress);
		
		String sCode = idTU == 1 ? "314" : "414";
		ModbusRequest req = 
				new ReadMultipleRegistersRequest(Integer.parseInt(sCode, 16), 2);
		trans.setRequest(req);
		req.setUnitID(currentAddress);
		req.setHeadless();

		try {
			trans.execute();
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

			trans.execute();
		} catch (ModbusIOException e1) {
			e1.printStackTrace();
		} catch (ModbusSlaveException e1) {
			e1.printStackTrace();
		} catch (ModbusException e1) {
			e1.printStackTrace();
		}
	}
	
	public int changeTUnik1F(int idTU, ModbusSerialTransaction trans, Device device, int currentAddress) {
		String sTU = idTU == 1 ? "1209" : "120B";
		ModbusRequest req = new ReadMultipleRegistersRequest(Integer.parseInt(sTU, 16), 2);		
		int tries = 0;		

		try {
			trans.setRequest(req);
			req.setUnitID(currentAddress);
			req.setHeadless();
			tries = trans.execute();
			ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();	
			int curVal = res.getRegisterValue(0);
			
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
			
			trans.execute();
		} catch (ModbusIOException e1) {
			e1.printStackTrace();
		} catch (ModbusSlaveException e1) {
			e1.printStackTrace();
		} catch (ModbusException e1) {
			e1.printStackTrace();
		}
		return tries;
	}
	
	public int getTSAkon(int idTS, SerialParameters sp, Device sd, int currentAddress) {
		int res = 0;
		
		String tsAddr = "";
		switch (idTS) {
		case 1:
			tsAddr = "b10";
			break;
		case 2:
			tsAddr = "c10";
			break;
		case 3:
			tsAddr = "d10";
			break;
		case 4:
			tsAddr = "e10";
			break;
		}
		
		if (sp != null) {
			trans = getTransaction(sp);
		}
		ReadMultipleRegistersRequest req = 
				new ReadMultipleRegistersRequest(Integer.parseInt(tsAddr, 16), 2);
		
		req.setUnitID(currentAddress);
		req.setHeadless();
		trans.setRequest(req);
		try {
			trans.execute();
			ReadMultipleRegistersResponse resp = (ReadMultipleRegistersResponse) trans.getResponse();
			ByteBuffer bb = ByteBuffer.allocate(resp.getWordCount() * 2);
			for (int n = 0; n < resp.getWordCount(); n++) {
				bb.put(resp.getRegister(n).toBytes());
	        }
			res = ModbusUtil.registersToInt(bb.array()) == 1 ? 1 : 0;
		} catch (ModbusIOException e) {
			e.printStackTrace();
		} catch (ModbusSlaveException e) {
			e.printStackTrace();
		} catch (ModbusException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public NumArray getSignals(int address, Device device, ModbusSerialTransaction trans, 
			String signalName, String signalType) {
		
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
				tries = trans.execute();
				
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
				new ReadMultipleRegistersRequest(Integer.parseInt(sTI.getOffset(), 16), sTI.getCount() * 2);
		req.setUnitID(address);
		req.setHeadless();

		ReadMultipleRegistersResponse resp = null;
		try {
			trans.setRequest(req);
			tries = trans.execute();
			
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

	public ModbusSerialTransaction getTrans() {
		return trans;
	}
}
