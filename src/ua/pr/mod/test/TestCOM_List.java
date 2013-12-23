package ua.pr.mod.test;

import java.util.Enumeration;

import gnu.io.CommPortIdentifier;

public class TestCOM_List {
	
	public static void main(String[] args) {
		Enumeration<?> pList = CommPortIdentifier.getPortIdentifiers();

	    // Process the list.
	    while (pList.hasMoreElements()) {
	      CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();
	      System.out.print("Port " + cpi.getName() + " ");
	      if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	        System.out.println("is a Serial Port: " + cpi);
	      } else if (cpi.getPortType() == CommPortIdentifier.PORT_PARALLEL) {
	        System.out.println("is a Parallel Port: " + cpi);
	      } else {
	        System.out.println("is an Unknown Port: " + cpi);
	      }
	    }
	}
}
