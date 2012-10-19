package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import GUI.BaseWindow;

import com.mysql.jdbc.Statement;

import rekord.Csoport;
import rekord.Labor;


public class LabCash {
	
	public static void insertCategory(DAO dao, Csoport cs){
		try {
			String sql = "INSERT INTO csoport SET nev='"+cs.getNev()+"'";
			dao.getMysql().getS().execute(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet r = dao.getMysql().getS().getGeneratedKeys();
			int id = 0;
			if (r.next()){
				System.out.println(id = r.getInt(1));
			}
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
	
	public static void insertItem(DAO dao, Labor lab) throws SQLException{
		String sql = "INSERT INTO labor VALUES (id, " +
				"'"+lab.getNev1()+"', " +
				"'"+lab.getNev2()+"', " +
				"'"+lab.getMegj()+"', " +
				"'"+lab.getIdo()+"', " +
				""+lab.getLaborAr()+", " +
				""+lab.getPartnerAr()+", " +
				""+lab.getAranyklinikaAr()+", " +
				"'"+lab.getAlapdij()+"', " +
				""+lab.getCsoport()+", " +
				"'"+lab.getAllapot()+"');";
		System.out.println(sql);
		dao.getMysql().getS().execute(sql, Statement.RETURN_GENERATED_KEYS);
		ResultSet r = dao.getMysql().getS().getGeneratedKeys();
		if (r.next()){
			lab.setId(r.getInt(1));
		}
		System.out.println(lab.getId());
		dao.getLabor().add(lab);
	}
	
	public static void editItem(DAO dao, Labor lab) throws SQLException{
		String sql = "UPDATE labor" +
				" SET nev1='"+lab.getNev1()+"', " +
				" nev2='"+lab.getNev2()+"', " +
				" megj='"+lab.getMegj()+"', " +
				" ido='"+lab.getIdo()+"', " +
				" labor_ar="+lab.getLaborAr()+", " +
				" partner_ar="+lab.getPartnerAr()+", " +
				" aranyklinika_ar="+lab.getAranyklinikaAr()+", " +
				" alapdij='"+lab.getAlapdij()+"', " +
				" csoport="+lab.getCsoport()+", " +
				" allapot='"+lab.getAllapot()+"' WHERE id ="+lab.getId()+";";
		System.out.println(sql);
		dao.getMysql().exec(sql);
	}
	
	public static void deleteItem(DAO dao, Labor lab) throws SQLException{
		String sql = "DELETE FROM labor" +
				" WHERE id="+lab.getId()+";";
		System.out.println(sql);
		dao.getMysql().exec(sql);
		dao.getLabor().remove(lab);
	}

	
}
