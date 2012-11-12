package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import tools.Const;

import GUI.CentrumLab;
import database.DAO;
import database.DBConnect;
import executable.AsszisztensMain;

public class MainWindow implements ActionListener{
	/*MAIN WINDOW OPTIONS*/
	private boolean resizable = true;
	private boolean visible = true;
	private int defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE;
	private int height = 450;
	private int width = 750;
	private boolean fullScreen = false;
	public static final int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
	public static final int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	/*MAIN WINDOW OPTIONS*/
	
	private JFrame window;
	private JMenuBar menuBar;
	private JMenu menu;
	private JPanel mainCenterPanel;
	private JLabel statusLabel;
	
	static String inputFile;
	static JFrame frame;
	static File file;
	public static final String fileSepa = System.getProperty("file.separator");
	private String dest = System.getProperty("user.dir")+fileSepa;
	
	private String status;
	private String serverDetails;
	private DBConnect mysql;
	
	private LabCashWindow labCashWindow;
	private DoctorScheduleWindow doctorScheduleWindow;
	private CentrumLab c;
	private JPanel mainMessagePanel;
	private JProgressBar connectionProgress;
	private DAO dao;
	
	public MainWindow(){
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Asszisztens");
		} catch (Exception e) {
			BaseWindow.makeWarning("Nem lehet a kinézetet beállítani!", e, "error", new JFrame());
		}
		
		try {
			initComponents();
		} catch (SQLException e1) {
			BaseWindow.makeWarning("SQL parancsfuttatási hiba!", e1, "error", new JFrame());
		}
		
		window.setIconImage(Toolkit.getDefaultToolkit().getImage(Const.ICON_PATH+"icon.png"));
		
	    /** A menusor letrehozasa, beallitasa, hozzaadasa az ablakhoz */
		newJMenu("Fájl", KeyEvent.VK_F, "Fájl menü");
			newJMenuItem("update", "Frissítés keresése", "", true);	
			newJMenuItem("exit", "Kilépés", KeyEvent.VK_X, "", true);	
		newJMenu("Lelet átalakítás", KeyEvent.VK_F, "Lelet menü");
			newJMenuItem("centrumlab", "Centrumlab lelet feldolgozás", "", true, new ImageIcon(Const.ICON_PATH+"icon_centrumlab.png")); 
		newJMenu("Rendelő nyilvántartás", KeyEvent.VK_F, "Lelet menü");
			newJMenuItem("rendeloBeosztas", "Rendelő beosztás", "", true, new ImageIcon(Const.ICON_PATH+"icon_calendar.png"));
			newJMenuItem("penztar", "Betegelőjegyzés (időpontkiadás)", "", false);
			newJMenuItem("penztar", "Laborvizsgálat árszámító", "", true, new ImageIcon(Const.ICON_PATH+"icon_lab.png"));
			newJMenuItem("penztar", "WebLabor", "", false);
		/*newJMenu("WebLabor (IN PROGRESS)", KeyEvent.VK_F, "Lelet menü");
			newJMenuItem("ujOrvos", "Új orvos regisztrálása", "");
			newJMenuItem("szerkesztOrvos", "Orvos szerkesztése (Még nincs...)", "");
			newJMenuItem("arajanlatKeres", "Árajánlat kérések", "");*/
		newJMenu("Súgó", KeyEvent.VK_S, "");
			newJMenuItem("errorReport", "Hibabejelentés", "", false);
			newJMenuItem("about", "Névjegy", "", true);
			newJMenuItem("help", "Segítség", "", false);
		
			
		
		mainCenterPanel.setLayout(new BoxLayout(mainCenterPanel, BoxLayout.X_AXIS));
		TitledBorder title;
		title = BorderFactory.createTitledBorder("Gyorsmenü");
		mainCenterPanel.setBorder(title);
		
		
		JButton centrumLab = new JButton("CentrumLab lelet", new ImageIcon(Const.ICON_PATH+"icon_centrumlab.png"));
		centrumLab.addActionListener(this);
		centrumLab.setActionCommand("centrumlab");
		centrumLab.setFocusable(false);
		
		JButton calendar = new JButton("Rendelő beosztás", new ImageIcon(Const.ICON_PATH+"icon_calendar.png"));
		
		calendar.addActionListener(this);
		calendar.setActionCommand("rendeloBeosztas");
		calendar.setFocusable(false);
		calendar.setEnabled(true);
		
		JButton lab = new JButton("Laborvizsgálatok", new ImageIcon(Const.ICON_PATH+"icon_lab.png"));
		lab.addActionListener(this);
		lab.setActionCommand("penztar");
		lab.setFocusable(false);
		lab.setEnabled(true);
		
		mainCenterPanel.add(centrumLab);
		mainCenterPanel.add(calendar);
		mainCenterPanel.add(lab);
				
		window.setTitle(titleString());
		window.setSize( new Dimension( width, height ) );	
		window.setLocation((screenWidth-window.getWidth())/2, (screenHeight-window.getHeight())/2);
		window.setResizable( resizable );
		window.setVisible( visible );		
		window.setDefaultCloseOperation( defaultCloseOperation );
        window.setLayout(new BorderLayout());
		window.setJMenuBar(menuBar);
		
		window.addWindowListener(new WindowAdapter() {
	            public void windowClosing(WindowEvent e) {
	            	exit();
	            }
	    });
		
		
		JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,mainCenterPanel,mainMessagePanel);
		mainSplit.setEnabled(false);
		
		window.add(mainSplit,"Center");
		
		statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 3));
		statusLabel.setPreferredSize(new Dimension(window.getWidth()/3*2,15));
		statusLabel.setSize(new Dimension(window.getWidth()/3*2,15));
		
		JPanel status = new JPanel();
		status.setLayout(new BoxLayout(status, BoxLayout.LINE_AXIS));
		
		status.setPreferredSize(new Dimension(window.getWidth(),15));
		status.setSize(new Dimension(window.getWidth()/3,15));
		
		status.add(statusLabel);
		status.add(connectionProgress);
		
		window.add(status,"South");
	    if (fullScreen){
	    	window.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    }
	}
	
	
	public void initComponents() throws SQLException{
		connectionProgress = new JProgressBar();
		connectionProgress.setIndeterminate(true);
		statusLabel = new JLabel();
		window = new JFrame();
		mainCenterPanel = new JPanel();
		mainMessagePanel = new JPanel();
		
		menuBar = new JMenuBar();
		menu = new JMenu();
		
		mysql = new DBConnect();
		mysql.setpBar(connectionProgress);
		mysql.setpLabel(statusLabel);
		mysql.startTheConnection();
		
		dao = new DAO();
		
		labCashWindow = new LabCashWindow(dao);
		doctorScheduleWindow = new DoctorScheduleWindow(dao);
		c = new CentrumLab();
		
		downloadDatas();
	}
	
	public void downloadDatas(){
		
		class DownloadFirstTime extends Thread {			
	         public void run() {	        	 
	        	while (!dao.isInitialized()){	        		
	        		if (mysql.isConnection()){	        			
	        			dao.init(mysql);
	        			labCashWindow.init();
	        			doctorScheduleWindow.init();
		        	} 		
	        	}	        		
	        }	        		        	 
		}
		
		DownloadFirstTime t = new DownloadFirstTime();
		t.start();
	}
	
	public void newJMenu(String label, int key, String description){
		menu = new JMenu(label); 
		menu.setMnemonic(key);
		menu.getAccessibleContext().setAccessibleDescription(description);
		menuBar.add(menu);
	}
	
	public void newJMenu(String label, String description){
		menu = new JMenu(label); 
		menu.getAccessibleContext().setAccessibleDescription(description);
		menuBar.add(menu);
	}
	
	public void newJMenuItem(String itemName, String label, int key, String description, boolean enabled){
		JMenuItem item = new JMenuItem(label);
		item.setAccelerator(KeyStroke.getKeyStroke(key, ActionEvent.CTRL_MASK));
		item.setActionCommand(itemName);
		item.setEnabled(enabled);
		item.getAccessibleContext().setAccessibleDescription(description);
		item.addActionListener(this);
		menu.add(item);
	}
	
	public void newJMenuItem(String itemName, String label, String description, boolean enabled){
		JMenuItem item = new JMenuItem(label);
		item.setActionCommand(itemName);
		item.setEnabled(enabled);
		item.getAccessibleContext().setAccessibleDescription(description);
		item.addActionListener(this);
		menu.add(item);
	}
	
	public void newJMenuItem(String itemName, String label, String description, boolean enabled, ImageIcon icon){
		JMenuItem item = new JMenuItem(label, icon);
		item.setActionCommand(itemName);
		item.setEnabled(enabled);
		item.getAccessibleContext().setAccessibleDescription(description);
		item.addActionListener(this);
		menu.add(item);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String cmd = e.getActionCommand();
		boolean runAnotherCommand = false;
		
		//CHECK ONLINE
		if (
				cmd.equals("adminPasswordChange") || 
				cmd.equals("kategoriaUj") ||
				cmd.equals("kategoriaSzerk") ||
				cmd.equals("kategoriaTorol") ||
				cmd.equals("laborUj") ||
				cmd.equals("laborSzerk") ||
				cmd.equals("laborTorol") ||
				cmd.equals("ujOrvos") ||
				cmd.equals("szerkesztOrvos") ||
				cmd.equals("arajanlatKeres") || 		
				cmd.equals("adminLogin") || 
				cmd.equals("kereses") || 
				cmd.equals("penztar") ||
				cmd.equals("arfolyamFrissit") ||
				cmd.equals("arfolyamBeallit") ||
				cmd.equals("arfolyamLekerdezes") ||
				cmd.equals("rendeloBeosztas")
			){
				if (mysql.isConnection()==false){
					BaseWindow.makeWarning("Offline módban ez a funkció nem elérhető!", new Exception(), "success", new JFrame());
				} else {
					runAnotherCommand = true;
				}
		} else {
			runAnotherCommand = true;		
		}
		
		if (runAnotherCommand == true){
			if (cmd=="centrumlab"){
				c.setVisible(true);
			} else if (cmd=="exit"){
				exit();
			} else if (cmd=="update"){
				searchForUpdates();
			} else if (cmd=="about"){
				about();
			} else if (cmd=="help"){
				help();
			} else if (cmd=="penztar"){
				if (!dao.isInitialized()) 
					BaseWindow.makeWarning("Még folyik az adatletöltés", new Exception(), "success", new JFrame());
				else {
					labCashWindow.setVisible(true);
				}		
			} else if (cmd=="arfolyamFrissit"){
				
			} else if (cmd=="arfolyamBeallit"){
				
			} else if (cmd=="arfolyamLekerdezes"){
				
			} else if (cmd=="arajanlatKeres"){
				
			} else if (cmd=="ujOrvos"){
				
			} else if (cmd=="szerkesztOrvos"){
				
			} else if (cmd.equals("rendeloBeosztas")){
				if (!dao.isInitialized()) 
					BaseWindow.makeWarning("Még folyik az adatletöltés", new Exception(), "success", new JFrame());
				else
					doctorScheduleWindow.setVisible(true);
			}
		}
	}

	public JFrame getWindow() {
		return window;
	}

	public void setWindow(JFrame window) {
		this.window = window;
	}
	
	public double getVersion()
	{
		double version = 0.0;
		try{
			  FileInputStream fstream = new FileInputStream( dest + "version.txt");
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine = br.readLine();
			  if (strLine != null)   {
				  version = Double.parseDouble(strLine);
			  }
			  in.close();
		}catch (Exception e){
			BaseWindow.makeWarning("Nem tudom betölteni a verziószámot! A program leáll!", e, "error", new JFrame());
			exit();
		}
		return version;
	}
	
	public void searchForUpdates() {
		String[] run = {"java","-jar","AsszisztensUpdater.jar"};
        try { //win
            Runtime.getRuntime().exec(run);
        } catch (Exception ex) {
        	try{	//mac
				Runtime.getRuntime().exec("open AsszisztensUpdater.jar");
			} catch (IOException e2){
				BaseWindow.makeWarning("Főalkalmazás megnyitási hiba!", e2, "error", new JFrame());
			}
        }
        exit();
	}
	
	public void about(){
		//TODO
		BaseWindow.makeWarning(
				"Asszisztens program v.: " + getVersion() + "\n\r" +
				"Készült: 2012 I. negyedév\n\r" +
				"Készítette: Dobó László +36209803955 www.lddsystems.eu Szeged\n\r" +
				"Készült az Aranyklinika számára. www.aranyklinika.hu\n\r" +
				"\n\r" +
				"A program hálózati adatbáziskapcsolattal kéri le a számára szükséges adatokat. Többfelhasználós, adminisztrálási lehetőség.\n\r" +
				"Automatikusan frissíti a feltelepített példányokat hálózati kapcsolaton keresztül. Fejlett hibakezelés.", new Exception(), "success", new JFrame());
	}
	
	public void print(String s){
		System.out.println(s);
	}
	
	public void help(){
		//TODO
		BaseWindow.makeWarning("A funkció jelenleg nem elérhető!", new Exception(), "success", new JFrame());
	}

	
	
	public String titleString(){
		return " Asszisztens v." + getVersion() + " *** Dobó László ***";
	}
	
	public String getDatabaseStatus(){
		return " Adatbáziskapcsolat: "+status+serverDetails;
	}
	
	public void exit(){
		mysql.close();
		c.closeAll();
		c.cancel();
		AsszisztensMain.exit();
	}
	
}
