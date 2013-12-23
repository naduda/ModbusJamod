package ua.pr.mod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.UIManager;
import ua.pr.common.ToolsPrLib;
import ua.pr.mod.ui.MainFrame;
import ua.pr.mod.xml.EntityFromXML;
import ua.pr.mod.xml.objects.Base;
import ua.pr.mod.Main;

public class Main implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(Main.class.getName());
	private static final String CONNECT_XML_PATH = "Settings.xml";
	
	public Main() {
		try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
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
	    	log.log(Level.INFO, "UIManager Exception ...   ", e);
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
		}
//	--------------------------------------------------------------------
		mainFrame.addMainPanel();
		mainFrame.setResizable(false);
		mainFrame.setLocationRelativeTo(null);
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
