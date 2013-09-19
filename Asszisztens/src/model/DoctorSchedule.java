package model;

import java.util.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;


import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

import rekord.RendeloIdopont;
import rekord.RendeloOrvos;
import rekord.RendeloSzoba;
import view.BaseWindow;

public class DoctorSchedule {

	

	
	public static void insert(DAO dao, String cmd, String nev){
		try {
			if (cmd.equals("orvos")){
				String sql = "INSERT INTO rendelo_orvos SET nev = '"+nev+"'";
				dao.getMysql().exec(sql);
				String sql2 = "SELECT id FROM rendelo_orvos WHERE nev = '"+nev+"'";
				dao.getMysql().exec(sql2);
				int id = 0;
				while (dao.getMysql().getResult().next()){
					id = dao.getMysql().getResult().getInt("id");
				}
				if (id>0){
					dao.getOrvosTomb().add(new RendeloOrvos(id, nev));
				} else {
					throw new SQLException("Not inserted");
				}
			} else if (cmd.equals("szoba")){
				String sql = "INSERT INTO rendelo_szoba SET nev = '"+nev+"'";
				dao.getMysql().exec(sql);
				String sql2 = "SELECT id FROM rendelo_szoba WHERE nev = '"+nev+"'";
				dao.getMysql().exec(sql2);
				int id = 0;
				while (dao.getMysql().getResult().next()){
					id = dao.getMysql().getResult().getInt("id");
				}
				if (id>0){
					dao.getSzobaTomb().add(new RendeloSzoba(id, nev));
				} else {
					throw new SQLException("Not inserted");
				}
			}
		} catch (SQLException e){
			//TODO
			e.printStackTrace();
		}
		
	}
	
	public static void addNewSchedule(DAO dao, Date tol, Date ig,  int szobaId,	 int orvosId, String alkalomTipus){
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println(formatter.format(tol));
			System.out.println(formatter.format(ig));
			String sql = "INSERT INTO rendelo_idopont SET tol = '"+formatter.format(tol).toString()+"', ig = '"+formatter.format(ig).toString()+"', szobaId = '"+szobaId+"', orvosId = '"+orvosId+"', alkalomTipus = '"+alkalomTipus+"'";
			System.out.println(sql);
			dao.getMysql().exec(sql);
			String sql2 = "SELECT id FROM rendelo_idopont WHERE tol = '"+formatter.format(tol).toString()+"' AND ig = '"+formatter.format(ig).toString()+"' AND szobaId = '"+szobaId+"' AND orvosId = '"+orvosId+"' AND alkalomTipus = '"+alkalomTipus+"'";
			dao.getMysql().exec(sql2);
			int id = 0;
			while (dao.getMysql().getResult().next()){
				id = dao.getMysql().getResult().getInt("id");
			}
			if (id>0){
				RendeloIdopont r = new RendeloIdopont(id, tol, ig, szobaId, orvosId, alkalomTipus);
				System.out.println(r);
				dao.getIdopontTomb().add(r);
			} else {
				throw new SQLException("Not inserted");
			}
		} catch (SQLException e) {
			// TODO: handle exception
			BaseWindow.makeWarning("Hiba a beszúrás közben!", e, "error");
		}
		
	}
	
	
	public static void edit(DAO dao, String cmd, String oldName, String newName){
		try {
			int id = 0;
			if (cmd.equals("orvos")){
				RendeloOrvos o = null;
				Iterator<RendeloOrvos> it = dao.getOrvosTomb().iterator();
				while (it.hasNext()){
					o = it.next();
					if (o.getNev().equals(oldName)){
						id = o.getId();
						o.setNev(newName);
						break;
					}
				}
				if (id>0){
					String selectSql = "SELECT * FROM rendelo_orvos WHERE id='"+id+"'";
					dao.getMysql().exec(selectSql);
					int count = 0;
					while (dao.getMysql().getResult().next()){
						count++;
					}
					if (count==0){
						dao.getOrvosTomb().remove(o);
						throw new SQLException("Not Found");
					}
					String sql = "UPDATE rendelo_orvos SET nev='"+newName+"' WHERE id='"+id+"'";
					dao.getMysql().exec(sql);				
				} else {
					throw new SQLException("Not Found");
				}
			} else if (cmd.equals("szoba")){
				RendeloSzoba o = null;
				Iterator<RendeloSzoba> it = dao.getSzobaTomb().iterator();
				while (it.hasNext()){
					o = it.next();
					if (o.getNev().equals(oldName)){
						id = o.getId();
						o.setNev(newName);
						break;
					}
				}
				if (id>0){
					String selectSql = "SELECT * FROM rendelo_szoba WHERE id='"+id+"'";
					dao.getMysql().exec(selectSql);
					int count = 0;
					while (dao.getMysql().getResult().next()){
						count++;
					}
					if (count==0){
						dao.getSzobaTomb().remove(o);
						throw new SQLException("Not Found");
					}
					String sql = "UPDATE rendelo_szoba SET nev='"+newName+"' WHERE id='"+id+"'";
					dao.getMysql().exec(sql);				
				} else {
					throw new SQLException("Not Found");
				}
				
			}
			
		} catch (SQLException e) {
			// TODO: handle exception
		}
		
	}
	
	public static void delete(DAO dao, String cmd, String nev){
		try {
			int id = 0;
			if (cmd.equals("orvos")){
				RendeloOrvos o = null;
				Iterator<RendeloOrvos> it = dao.getOrvosTomb().iterator();
				while (it.hasNext()){
					o = it.next();
					if (o.getNev().equals(nev)){
						id = o.getId();
						break;
					}
				}
				if (id>0){
					String selectSql = "SELECT * FROM rendelo_orvos WHERE id='"+id+"'";
					dao.getMysql().exec(selectSql);
					int count = 0;
					while (dao.getMysql().getResult().next()){
						count++;
					}
					if (count==0){
						dao.getOrvosTomb().remove(o);
						throw new SQLException("Not Found");
					}
					String sql = "DELETE FROM rendelo_orvos WHERE id='"+id+"'";
					dao.getMysql().exec(sql);		
					dao.getOrvosTomb().remove(o);
				} else {
					throw new SQLException("Not Found");
				}
			} else if (cmd.equals("szoba")){
				RendeloSzoba o = null;
				Iterator<RendeloSzoba> it = dao.getSzobaTomb().iterator();
				while (it.hasNext()){
					o = it.next();
					if (o.getNev().equals(nev)){
						id = o.getId();
						break;
					}
				}
				if (id>0){
					String selectSql = "SELECT * FROM rendelo_szoba WHERE id='"+id+"'";
					dao.getMysql().exec(selectSql);
					int count = 0;
					while (dao.getMysql().getResult().next()){
						count++;
					}
					if (count==0){
						dao.getSzobaTomb().remove(o);
						throw new SQLException("Not Found");
					}
					String sql = "DELETE FROM rendelo_szoba WHERE id='"+id+"'";
					dao.getMysql().exec(sql);	
					dao.getSzobaTomb().remove(o);
				} else {
					throw new SQLException("Not Found");
				}
				
			}
		} catch (SQLException e) {
			// TODO: handle exception
			BaseWindow.makeWarning("Hiba! DoctorSchedule", e, "error");
		}
		
	}

	public static ArrayList<RendeloSzoba> searchForFreeRooms(DAO dao, Date tol, Date ig, String cmd, int orvosId) {
		ArrayList<RendeloSzoba> result = new ArrayList<RendeloSzoba>();
		
		result.removeAll(result);		
		result.addAll(dao.getSzobaTomb());
		
		Calendar kivantKezdet = Calendar.getInstance();
		Calendar kivantVeg = Calendar.getInstance();
		kivantKezdet.setTime(tol);
		kivantVeg.setTime(ig);
		
			Iterator<RendeloIdopont> it = dao.getIdopontTomb().iterator();
			while (it.hasNext()){
				RendeloIdopont r = it.next();
				
				System.out.println(" Tömb idő kezdet: "+r.getTol());
				System.out.println(" Tömb idő vég: "+r.getIg());
				
				Calendar tombKezdet = Calendar.getInstance();
				Calendar tombVeg = Calendar.getInstance();
				tombKezdet.setTime(r.getTol());
				tombVeg.setTime(r.getIg());
				
				boolean sameDay = false;
				boolean metszet = false;
				
				if (r.getAlkalomTipus().equals("ismetlodo")){
					//A kívánt időpont alkalmi, a rekord ismétlődő
					//meg kell nézni, hogy az ismétlődő azonos napon van e a kívánt alkalmival
					//ha nem, akkor biztos, hogy nincs metszet
					//ha igen, akkor megvizsgáljuk, hogy az időpont fedi e egymást
					//ha fedi, akkor vizsgálódunk, van e lemondás pont erre az időpontra, mert ha van, akkor jó lesz
					sameDay = kivantKezdet.get(Calendar.YEAR) == tombKezdet.get(Calendar.YEAR) &&
			                  kivantKezdet.get(Calendar.DAY_OF_WEEK) == tombKezdet.get(Calendar.DAY_OF_WEEK);
					if (cmd.equals("alkalmi")){
						//TODO ha a kivant alkalmi es a tomb ismetlodo
						
					} else if (cmd.equals("ismetlodo")){
						//TODO ha a kivant ismetlodo es a tomb is ismetlodo
						
					}
				} else if (r.getAlkalomTipus().equals("alkalmi")){
					//a rekord alkalmi 
					//legegyszerűbb eset, ha van metszet, akkor nem jó
					//először csak dátum, majd időpont vizsgálat
					//nics lemondás, mert alkalmi esetén törlünk, ide már nem kerül be
					
					if (cmd.equals("alkalmi")){
						//ha mindketto alkalmi akkor a ket datumnak meg kell egyeznie
						sameDay = kivantKezdet.get(Calendar.YEAR) == tombKezdet.get(Calendar.YEAR) &&
				                  kivantKezdet.get(Calendar.DAY_OF_YEAR) == tombKezdet.get(Calendar.DAY_OF_YEAR);	
					} else if (cmd.equals("ismetlodo")){
						//ha a kivant idopont ismetlodo, akkor eleg hogy ha egy napon vannak a hét napjai közül
						//az ismetlodo minden heten van viszont csak a kezdoidopontot tudjuk, igy a nap osszehasonlitasakor,
						//megtudhatjuk hogy utkozik e ezzel az egyszeri idoponttal amit felvittunk.
						//ha egy is kozben van, akkor nem jo!
						sameDay = kivantKezdet.get(Calendar.YEAR) == tombKezdet.get(Calendar.YEAR) &&
				                  kivantKezdet.get(Calendar.DAY_OF_WEEK) == tombKezdet.get(Calendar.DAY_OF_WEEK);
					}
					
				}
				
				if (sameDay){
					metszet = 
							(kivantKezdet.get(Calendar.MILLISECONDS_IN_DAY) < tombKezdet.get(Calendar.MILLISECONDS_IN_DAY)) && ((kivantVeg.get(Calendar.MILLISECONDS_IN_DAY)) > tombKezdet.get(Calendar.MILLISECONDS_IN_DAY)) ||
							(kivantKezdet.get(Calendar.MILLISECONDS_IN_DAY) > tombKezdet.get(Calendar.MILLISECONDS_IN_DAY)) && ((kivantVeg.get(Calendar.MILLISECONDS_IN_DAY)) < tombVeg.get(Calendar.MILLISECONDS_IN_DAY)) ||
							(kivantKezdet.get(Calendar.MILLISECONDS_IN_DAY) < tombVeg.get(Calendar.MILLISECONDS_IN_DAY)) && ((kivantVeg.get(Calendar.MILLISECONDS_IN_DAY)) > tombVeg.get(Calendar.MILLISECONDS_IN_DAY)) ||
							(kivantKezdet.get(Calendar.MILLISECONDS_IN_DAY) == tombKezdet.get(Calendar.MILLISECONDS_IN_DAY)) && ((kivantVeg.get(Calendar.MILLISECONDS_IN_DAY)) == tombVeg.get(Calendar.MILLISECONDS_IN_DAY));
					if (metszet){
						if (r.getOrvosId()==orvosId){
							return null;
						}
						Iterator<RendeloSzoba> szobaIt = result.iterator();
						while (szobaIt.hasNext()){
							RendeloSzoba szoba = szobaIt.next();
							System.out.println(szoba.getId()+" "+r.getSzobaId());
							if (szoba.getId()==r.getSzobaId()){
								result.remove(szoba);
								RendeloSzoba reserved = new RendeloSzoba(szoba.getId(), szoba.getNev());
								reserved.setReserved(true);
								DateFormat format = new SimpleDateFormat("HH:mm");
								reserved.setDoctorData(dao.getDoctorNameById(r.getOrvosId())+": "+format.format(r.getTol())+"-"+format.format(r.getIg())+" "+r.getAlkalomTipus());
								result.add(reserved);
								break;
							}
						}
					}
				}
			}
		//}
		
		/*Iterator<RendeloIdopont> it = idopontTomb.iterator();
		while (it.hasNext()){
			RendeloIdopont r = it.next();
			
		}*/
		
		return result;
	}
	

	
}
