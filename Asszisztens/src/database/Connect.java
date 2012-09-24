package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;

import GUI.BaseWindow;

public class Connect {
	
	private Connection conn;
	private boolean connectionStatus;	
	private String activeServer = "";
	private String activeDatabase = "";
	private Statement s;
	private ResultSet result;
	private int workingConn = -1;
	private DatabaseMirrors acc;
	
	public Connect(){
		connectionStatus = connectToDatabase();
		if (connectionStatus == false){
			Object[] o = {"Igen","Nem"};
			boolean ask = BaseWindow.ask(o, "Sikertelen kapcsolatlétrehozás", "Nem sikerült egyik adatbázisszerverhez se kapcsolódni! Folytatja a munkát offline módban?", new JFrame());
			if (ask == false){
				System.exit(0);
			}
		} else {
			try {
				setS(conn.createStatement());
			} catch (SQLException e) {
				BaseWindow.makeWarning("Nem tudok kapcsolatot létrehozni!", e, "error", new JFrame());
			}
		}
	}
	
	public boolean connectToDatabase(){
		acc = new DatabaseMirrors();
		if (acc.getCounter()==-1){
			return false;
		} else {
			for (int i=0; i<=acc.getCounter(); i++){
				try {
				    open(acc, i);
				    workingConn=i;
				    return true;
				} catch (SQLException ex) {
					BaseWindow.makeWarning("Nem tudok kapcsolódni a szerverhez!", ex, "error", new JFrame());
					Object[] o = {"Igen", "Nem"};
					boolean b = BaseWindow.ask(o, "Hiba az adatbáziskapcsolatban!", "Nem sikerült az előző kapcsolatot felépíteni! Megpróbál kapcsolatot létesíteni a tükörszerverrel?", new JFrame());
					if (b == false){
						System.exit(0);
					}
				}
			}
		}
		return false;
	}

	public void exec(String sql) throws SQLException{
		open(acc,workingConn);
		setS(conn.createStatement());
		s.execute(sql);
		setResult(s.getResultSet());
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
			conn.close();
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

	public boolean isConnectionStatus() {
		return connectionStatus;
	}

	public void setConnectionStatus(boolean connectionStatus) {
		this.connectionStatus = connectionStatus;
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
}
