package database;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import GUI.BaseWindow;

public class DBConnect {
	
	private Connection conn;
	private boolean connection;	
	private String activeServer;
	private String activeDatabase;
	private Statement s;
	private ResultSet result;
	private int workingConn;
	private DatabaseMirrors acc;
	
	private JLabel pLabel;
	private JProgressBar pBar;
	
	public DBConnect(){
		
		pBar = null;
		pLabel = null;
		conn = null;
		connection = false;
		activeServer = "";
		activeDatabase = "";
		s = null;
		result = null;
		setWorkingConn(-1);
		acc = new DatabaseMirrors();
		
	}
	
	public void startTheConnection(){	
		pLabel.setForeground(Color.BLACK);
		pLabel.setText("(OFFLINE) Kapcsolodas...");
		pBar.setIndeterminate(true);
		class ConnectToDatabaseThread extends Thread{
			public void run(){						
				if (acc.getCounter()==-1){
					pLabel.setForeground(Color.RED);
					pLabel.setText("(OFFLINE) Hiba: Adatbazis konfiguracios fajl hianyzik vagy ures. ");
					pBar.setIndeterminate(false);
					connection=false;
					
					System.out.println("[ERROR] Nincs konfig fájl.");
				} else {
					System.out.println("[SUCCESS] Konfig fájl beolvasva.");
					//van konfig fájl
					
					if (connection==false){
						for (int i=0; i<=acc.getCounter(); i++){
							pLabel.setForeground(Color.BLACK);
							pLabel.setText("(OFFLINE) Kapcsolodas ide..."+acc.getMirror(i).getUrl()+" ");
							try {
								open(acc, i);
								setWorkingConn(i);
								connection=true;
								pLabel.setText("(ONLINE) Kapcsolodva: "+acc.getMirror(i).getDatabase()+"@"+acc.getMirror(i).getUrl()+" ");
								pBar.setIndeterminate(false);
								pBar.setValue(pBar.getMaximum());
								System.out.println("[SUCCESS] Kapcsolodva: "+acc.getMirror(i).getDatabase()+"@"+acc.getMirror(i).getUrl()+" ");
								break;
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								connection = false;
								System.out.println("[ERROR] Nincs kapcsolat: "+acc.getMirror(i).getUrl());
							}
						}
						
						if (connection==false){
							System.out.println("[ERROR] Nincs semmilyen kapcsolat. Varakozas az ujrakapcsolodasra...");
							int sec = 10;
							pLabel.setForeground(Color.RED);
							pBar.setIndeterminate(false);
							pBar.setMaximum(sec);
							pBar.setValue(0);
							for (int i=sec; i>0; i--){
								pLabel.setText("(OFFLINE) Nem sikerult kapcsolodni... Ujrakapcsolodas "+i+" masodperc mulva.");
								pBar.setValue(pBar.getValue()+1);
								try {
									sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							startTheConnection();	
						} else {
							System.out.println("[SUCCESS] Van kapcsolat. Kapcsolat ellenorzese.");
							while (true){
								try {
									sleep(10000);
									exec("SELECT 1");
									//System.out.println("[INFO] Van kapcsolat");
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									//e.printStackTrace();
									System.out.println("[INFO] Nincs kapcsolat. Újrakapcsolódás indítása.");
									connection = false;
									startTheConnection();
									break;
								}
							}	
						}
						
					}
				}
				
			}
		}
		
		acc = new DatabaseMirrors();
		ConnectToDatabaseThread connection = new ConnectToDatabaseThread();
		connection.start();	
	}
	

	public void exec(String sql) throws SQLException{
		boolean execute = false;
		if (!isConnection()){
			BaseWindow.makeWarning("Megszakadt a kapcsolat az adatbázisszerverrel! Várjon amíg újrakapcsolódik!", new Exception("No Connection"), "error", new JFrame());
			while (true){
				if (isConnection()){
					execute = true;
					break;
				} else {
					BaseWindow.makeWarning("Megszakadt a kapcsolat az adatbázisszerverrel! Várjon amíg újrakapcsolódik!", new Exception("No Connection"), "error", new JFrame());
				}
			}
		} else {
			execute = true;
		}
		if (execute){
			setS(conn.createStatement());
			s.execute(sql);
			setResult(s.getResultSet());
		}			 
	}
	
	public void open(DatabaseMirrors acc, int i) throws SQLException{
			setConn(DriverManager.getConnection("jdbc:mysql://"+acc.getMirror(i).getUrl()+":"+acc.getMirror(i).getPort()+"/"+acc.getMirror(i).getDatabase()+"?" +
			        "user="+acc.getMirror(i).getUsername()+"&password="+acc.getMirror(i).getEncodedPassword()+"&characterEncoding=utf-8&" + 
			"useUnicode=true"));
			setActiveServer(acc.getMirror(i).getUrl());
			setActiveDatabase(acc.getMirror(i).getDatabase());
		
	}
	
	public void close(){
		try {
			if (conn!=null) conn.close();
		} catch (SQLException e) {
			BaseWindow.makeWarning("Az adatbáziskapcsolt lezárása meghiúsult!", e, "error", new JFrame());
		}
	}
	
	
	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public Connection getConn() {
		return conn;
	}

	public boolean isConnection() {
		return connection;
	}

	public void setConnection(boolean connection) {
		this.connection = connection;
	}

	public void setActiveServer(String activeServer) {
		this.activeServer = activeServer;
	}

	public String getActiveServer() {
		return activeServer;
	}

	public String getActiveDatabase() {
		return activeDatabase;
	}

	public void setActiveDatabase(String activeDatabase) {
		this.activeDatabase = activeDatabase;
	}

	public void setS(Statement s) {
		this.s = s;
	}

	public Statement getS() {
		return s;
	}

	public void setResult(ResultSet result) {
		this.result = result;
	}

	public ResultSet getResult() {
		return result;
	}
	

	public JProgressBar getpBar() {
		return pBar;
	}

	public void setpBar(JProgressBar pBar) {
		this.pBar = pBar;
	}

	public JLabel getpLabel() {
		return pLabel;
	}

	public void setpLabel(JLabel pLabel) {
		this.pLabel = pLabel;
	}

	public int getWorkingConn() {
		return workingConn;
	}

	public void setWorkingConn(int workingConn) {
		this.workingConn = workingConn;
	}
}
