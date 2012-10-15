package database;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFrame;

import GUI.BaseWindow;

import rekord.Csoport;
import rekord.Labor;
import rekord.RendeloIdopont;
import rekord.RendeloOrvos;
import rekord.RendeloSzoba;

public class DAO {
	private ArrayList<Labor> labor;
	private ArrayList<Csoport> laborCsoport;
	//TODO rendelo
	private ArrayList<RendeloIdopont> rendeloIdopont; 
	private ArrayList<RendeloOrvos> rendeloOrvos;
	private ArrayList<RendeloSzoba> rendeloSzoba;
	
	private boolean initialized = false;
	private DBConnect mysql;
	
	public DAO(){
		labor = new ArrayList<Labor>();
		laborCsoport = new ArrayList<Csoport>();
		rendeloIdopont = new ArrayList<RendeloIdopont>();
		rendeloOrvos = new ArrayList<RendeloOrvos>();
		rendeloSzoba = new ArrayList<RendeloSzoba>();
	}

	public void init(DBConnect mysql){
		
		this.setMysql(mysql);
		
		//////labor//////
		try {
			mysql.exec("SELECT * FROM labor ORDER BY csoport;");
			while (mysql.getResult().next()==true){
				Labor l = new Labor(
						Integer.parseInt(mysql.getResult().getString("id")),
						mysql.getResult().getString("nev1"), 
						mysql.getResult().getString("nev2"),
						mysql.getResult().getString("megj"),
						mysql.getResult().getString("ido"), 
						Integer.parseInt(mysql.getResult().getString("labor_ar")),
						Integer.parseInt(mysql.getResult().getString("partner_ar")),
						Integer.parseInt(mysql.getResult().getString("aranyklinika_ar")),
						mysql.getResult().getString("alapdij"),
						Integer.parseInt(mysql.getResult().getString("csoport")),
						mysql.getResult().getString("allapot")
					);
				labor.add(l);
				//System.out.println(l);
			}
		} catch (SQLException e) {
			BaseWindow.makeWarning("Nem sikerült a laborvizsgálatokat letölteni!", e, "error", new JFrame());
			setInitialized(false);
		}
		
		//////laborCsoport//////
		try {
			mysql.exec("SELECT * FROM csoport");
			while (mysql.getResult().next()==true){
				Csoport cs = new Csoport(
						Integer.parseInt(mysql.getResult().getString("id")),
						mysql.getResult().getString("nev") 
					);
				//System.out.println(cs);
				laborCsoport.add(cs);
			}
		} catch (SQLException e) {
			BaseWindow.makeWarning("Nem sikerült a laborvizsgálat csoportokat letölteni!", e, "error", new JFrame());
			setInitialized(false);
		}
		
		setInitialized(true);
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	public ArrayList<Labor> getLabor() {
		return labor;
	}

	public void setLabor(ArrayList<Labor> labor) {
		this.labor = labor;
	}

	public ArrayList<Csoport> getLaborCsoport() {
		return laborCsoport;
	}

	public void setLaborCsoport(ArrayList<Csoport> laborCsoport) {
		this.laborCsoport = laborCsoport;
	}

	public DBConnect getMysql() {
		return mysql;
	}

	public void setMysql(DBConnect mysql) {
		this.mysql = mysql;
	}
	
	
}
