package ua.pr.mod.test;

import java.nio.ByteBuffer;

import net.wimpi.modbus.util.ModbusUtil;

public class Test2 {
	public static void main(String[] args) {
		int x = 1084886528;
		int y = 8;
		int z = x | y;
		System.out.println(x | y);
		System.out.println("z = " + Integer.toHexString(z));
		
		ByteBuffer bb = ByteBuffer.allocate(4).put(ByteBuffer.allocate(4).putInt(z).array());

//		byte[] b2 = bb.get(bb.array(), 3, 2).array();
		
		System.out.println(Integer.toHexString(z).substring(0, 4));
		System.out.println(Integer.toHexString(z).substring(4));
		//System.out.println(ModbusUtil.toHex(bb.array()));
//		System.out.println(ModbusUtil.toHex(b2));
		System.out.println(ModbusUtil.toHex(bb.array()));
	}
}
