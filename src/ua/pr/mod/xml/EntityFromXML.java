package ua.pr.mod.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class EntityFromXML {
	public Object getObject(String xmlFilePath, Class<?> xmlClass) {
		Object result = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(xmlClass);
			Unmarshaller u = jc.createUnmarshaller();
			result = u.unmarshal(new FileInputStream(xmlFilePath));
		} catch (JAXBException e) {
			System.err.println("Error in EntityFromXML.getObject(...). JAXBException: " + e);
		} catch (FileNotFoundException ex) {
			System.err.println("Error in EntityFromXML.getObject(...). FileNotFoundException: " + ex);
			System.out.println("------------------------------------------------------------");
			System.out.println("xmlFilePath = " + xmlFilePath);
			System.out.println("------------------------------------------------------------");
		}
		return result;
	}
	
	public void setObject(String xmlFilePath, Object object) {
		try {
			JAXBContext jc = JAXBContext.newInstance(object.getClass());
			Marshaller m = jc.createMarshaller();
			m.marshal(object, new File(xmlFilePath));
		} catch (JAXBException e) {
			System.err.println("Error in EntityFromXML.setObject(...). JAXBException: " + e);
		}
	}
}
