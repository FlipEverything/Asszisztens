package database;

import java.sql.SQLException;
import java.util.Iterator;

import GUI.BaseWindow;

import com.mysql.jdbc.Statement;

import rekord.Csoport;


public class LabCash {
	
	public static void insertCategory(DAO dao, Csoport cs){
		try {
			int id = dao.getMysql().getS().executeUpdate("INSERT INTO csoport SET nev='"+cs.getNev()+"'", Statement.RETURN_GENERATED_KEYS);
			dao.getLaborCsoport().add(new Csoport(id, cs.getNev()));
		} catch (SQLException e) {
			BaseWindow.makeWarning("Nem tudtam a csoportot beszúrni!", e, "error");
		}
	}
	
	public static void editCategory(DAO dao, Csoport cs){
		try {
			dao.getMysql().exec("UPDATE csoport SET nev = '"+cs.getNev()+"' WHERE id ='"+cs.getId()+"'");
			Iterator<Csoport> it = dao.getLaborCsoport().iterator();
			while (it.hasNext()){
				Csoport a = it.next();
				if (a.getId()==cs.getId()){
					a.setNev(cs.getNev());
					break;
				}
			}
		} catch (SQLException e) {
			BaseWindow.makeWarning("Nem tudtam a csoportot szerkeszteni!", e, "error");
		}
	}
	
	public static void deleteCategory(DAO dao, Csoport cs){
		try {
			dao.getMysql().exec("DELETE FROM csoport WHERE id ='"+cs.getId()+"'");
			Iterator<Csoport> it = dao.getLaborCsoport().iterator();
			while (it.hasNext()){
				Csoport a = it.next();
				if (a.getId()==cs.getId()){
					dao.getLaborCsoport().remove(a);
					break;
				}
			}
		} catch (SQLException e) {
			BaseWindow.makeWarning("Nem tudtam a csoportot törölni!", e, "error");
		}
	}
	
	public static void insertItem(){
		
	}
	
	public static void editItem(){
		
	}

	
}
