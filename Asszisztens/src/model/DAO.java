package model;

import java.awt.Color;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JFrame;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;


import rekord.Csoport;
import rekord.Labor;
import rekord.RendeloIdopont;
import rekord.RendeloOrvos;
import rekord.RendeloSzoba;
import view.BaseWindow;

public class DAO {
	private ArrayList<Labor> labor;
	private ArrayList<Csoport> laborCsoport;
	//TODO rendelo
	private ArrayList<RendeloOrvos> orvosTomb;
	private ArrayList<RendeloSzoba> szobaTomb;
	private ArrayList<RendeloIdopont> idopontTomb;
	
	private boolean initialized = false;
	private DBConnect mysql;
	
	public DAO(){
		labor = new ArrayList<Labor>();
		laborCsoport = new ArrayList<Csoport>();
		idopontTomb = new ArrayList<RendeloIdopont>();
		orvosTomb = new ArrayList<RendeloOrvos>();
		szobaTomb = new ArrayList<RendeloSzoba>();
	}

	public void init(DBConnect mysql){
		
		this.setMysql(mysql);
		
		//downloadIdopont();
		downloadLabor();
		downloadLaborCsoport();
		//downloadOrvos();
		//downloadSzoba();
		
		
		setInitialized(true);
	}
	
	public void downloadLaborCsoport(){
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
	}
	
	public void downloadLabor(){
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
	}
	
	public void downloadOrvos(){
		String sql="SELECT * FROM rendelo_orvos";
		try {
			mysql.exec(sql);
			while (mysql.getResult().next()==true){
				orvosTomb.add(new RendeloOrvos(mysql.getResult().getInt("id"), mysql.getResult().getString("nev")));
			}
		} catch (SQLException e) {
			BaseWindow.makeWarning("Nem sikerült az orvosokat letölteni!", e, "error", new JFrame());
			setInitialized(false);
		}
		
	}
	
	public void downloadSzoba(){
		String sql="SELECT * FROM rendelo_szoba";			
		try {
			mysql.exec(sql);
			while (mysql.getResult().next()==true){
				szobaTomb.add(new RendeloSzoba(mysql.getResult().getInt("id"), mysql.getResult().getString("nev")));
			}
		} catch (SQLException e) {
			BaseWindow.makeWarning("Nem sikerült a rendelőket letölteni!", e, "error", new JFrame());
			setInitialized(false);
		}
		

	}
	
	public void downloadIdopont(){
		String sql="SELECT * FROM rendelo_idopont";
		try {			
			mysql.exec(sql);
			while (mysql.getResult().next()==true){
				 DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			
				 
				 Date tol = format.parse(mysql.getResult().getString("tol"));
				 Date ig = format.parse(mysql.getResult().getString("ig"));
				 
				 idopontTomb.add(
						new RendeloIdopont(
								mysql.getResult().getInt("id"),
								tol, 
								ig, 
								mysql.getResult().getInt("szobaId"),
								mysql.getResult().getInt("orvosId"), 
								mysql.getResult().getString("alkalomTipus")
						)		
				);
								
			}
		} catch (ParseException e) {
			BaseWindow.makeWarning("Rossz dátumformátum, nem sikerült az időpontokat letölteni!", e, "error", new JFrame());
			setInitialized(false);
		} catch (SQLException e) {
			BaseWindow.makeWarning("Nem sikerült az időpontokat letölteni!", e, "error", new JFrame());
			setInitialized(false);
		}
	}

	public ArrayList<RendeloOrvos> getOrvosTomb() {
		return orvosTomb;
	}

	public void setOrvosTomb(ArrayList<RendeloOrvos> orvosTomb) {
		this.orvosTomb = orvosTomb;
	}

	public ArrayList<RendeloSzoba> getSzobaTomb() {
		return szobaTomb;
	}

	public void setSzobaTomb(ArrayList<RendeloSzoba> szobaTomb) {
		this.szobaTomb = szobaTomb;
	}

	public ArrayList<RendeloIdopont> getIdopontTomb() {
		return idopontTomb;
	}

	public void setIdopontTomb(ArrayList<RendeloIdopont> idopontTomb) {
		this.idopontTomb = idopontTomb;
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
	
	public String getDoctorNameById(int id){
		String nev = null;
		Iterator<RendeloOrvos> it = orvosTomb.iterator();
		while (it.hasNext()){
			RendeloOrvos orvos = it.next();
			if (orvos.getId()==id){
				nev = orvos.getNev();
			}
		}
		return nev;
	}
	
	public String getRoomNameById(int id){
		String nev = null;
		Iterator<RendeloSzoba> it = szobaTomb.iterator();
		while (it.hasNext()){
			RendeloSzoba szoba = it.next();
			if (szoba.getId()==id){
				nev = szoba.getNev();
			}
		}
		return nev;
	}
	
	public Color getDoctorColorById(int id){
		Color szin = null;
		Iterator<RendeloOrvos> it = orvosTomb.iterator();
		while (it.hasNext()){
			RendeloOrvos orvos = it.next();
			if (orvos.getId()==id){
				szin = orvos.getSzin();
			}
		}
		return szin;
	}
	
	
}
