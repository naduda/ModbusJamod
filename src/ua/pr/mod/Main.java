package ua.pr.mod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import ua.pr.common.MyFormatter;
import ua.pr.common.ToolsPrLib;
import ua.pr.mod.ui.MainFrame;
import ua.pr.mod.xml.EntityFromXML;
import ua.pr.mod.xml.objects.Base;
import ua.pr.mod.Main;

public class Main implements Serializable {
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(Main.class.getName());
	private static final String CONNECT_XML_PATH = "Settings.xml";
	
	public Main() {
		try {
            log.setUseParentHandlers(false);
           
            MyFormatter formatter = new MyFormatter();
            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(formatter);
            log.addHandler(handler);
            
            FileHandler fHandler = new FileHandler("log", 100000, 3);
            fHandler.setFormatter(formatter);
            log.addHandler(fHandler);
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e);
        }
//		-----------------------------------------------------------------
		String libDir = System.getProperty("user.dir");	
		String arch = System.getProperty("os.arch");
		try {
			arch = arch.toLowerCase().equals("x86") ? "32" : "64";
			ToolsPrLib.addLibraryPath(libDir + File.separator + arch);	
	    } catch (Exception e) {
	    	log.log(Level.SEVERE, "UIManager Exception ...   ", e);
	    }
//		-----------------------------------------------------------------
		EntityFromXML efx = new EntityFromXML();
		Base base = (Base)efx.getObject(CONNECT_XML_PATH, Base.class);
//		-----------------------------------------------------------------
		MainFrame mainFrame = null;
		String pathOfFormState = ToolsPrLib.getFullPath(base.getMainForm().getPathOfFormState());
		File frmState = new File(pathOfFormState);
		if (frmState.exists()) {
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(frmState));
				mainFrame = ((MainFrame) ois.readObject());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			mainFrame = new MainFrame(base);
			mainFrame.setLocationRelativeTo(null);
		}
//	--------------------------------------------------------------------
		ToolsPrLib.HideOnEsc(mainFrame, true);
		mainFrame.addMainPanel();
		mainFrame.setResizable(false);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new Main();	
	}
}
