package executable;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.UIManager;

import view.*;


/**
 * @author Dobó László
 */
public class AsszisztensMain{
	
	///
	private static ServerSocket SERVER_SOCKET;
	public MainWindow applicationWindow;
	private static boolean DEBUG = false;
	///
	
	public AsszisztensMain() throws SQLException{
		init();
		applicationWindow = new MainWindow();		
	}
	
	public static void main(String[] args) {
		if (args.length>0){
			if (args[0].equals("-debug") && (args[1].equals("aj6720ldd"))){
				DEBUG = true;
			}
		}
		
		final boolean appStartMode = DEBUG;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	if (appStartMode){
            		startTheMainApplication();
            	} else {
            		PasswordProtection.createAndShowGUI();	
            	}
            }
        });
	}
	
	public static void startTheMainApplication(){
		try {
			new AsszisztensMain();
		} catch (SQLException e) {
			BaseWindow.makeWarning("SQL parancsfuttatási hiba!", e, "error", new JFrame());
		}
	}
	
	public void init(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			BaseWindow.makeWarning("Nem lehet a kinézetet beállítani!", e, "error", new JFrame());
		}
		class T extends Thread {
	         public void run() {
	        	try {
					sleep(2000);
				} catch (InterruptedException e) {
					BaseWindow.makeWarning("Megszakítás történt!", e, "error", new JFrame());
					exit();
				}
				try {
					SERVER_SOCKET = new ServerSocket(1334);
			    } catch (IOException x) {
			    	//TODO
			    	//applicationWindow.getWindow().setVisible(false);
			    	BaseWindow.makeWarning("A program már egy példányban fut! A program most leáll...", x, "error", new JFrame());
			    	exit();
			    }
	        		
	         }
		}
		T t = new T();
		t.start();
	}
	
	public static void setSERVER_SOCKET(ServerSocket sERVER_SOCKET) {
		SERVER_SOCKET = sERVER_SOCKET;
	}

	public static ServerSocket getSERVER_SOCKET() {
		return SERVER_SOCKET;
	}
	
	public static void exit(){
		try {
			if (SERVER_SOCKET!=null){
				SERVER_SOCKET.close();	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
}
