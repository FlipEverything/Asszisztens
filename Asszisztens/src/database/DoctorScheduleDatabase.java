package database;

import java.text.ParseException;
import java.util.Date;
import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

import rekord.RendeloIdopont;
import rekord.RendeloOrvos;
import rekord.RendeloSzoba;

public class DoctorScheduleDatabase {
	private Connect mysql;
	private ArrayList<RendeloOrvos> orvosTomb;
	private ArrayList<RendeloSzoba> szobaTomb;
	private ArrayList<RendeloIdopont> idopontTomb;
	
	public DoctorScheduleDatabase() throws SQLException{
		mysql = new Connect();
		orvosTomb = new ArrayList<RendeloOrvos>();
		szobaTomb = new ArrayList<RendeloSzoba>();
		idopontTomb = new ArrayList<RendeloIdopont>();
		try {
			downloadFirstTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void downloadFirstTime() throws SQLException, ParseException{
		String sql="SELECT * FROM rendelo_orvos";
		mysql.exec(sql);
		while (mysql.getResult().next()==true){
			orvosTomb.add(new RendeloOrvos(mysql.getResult().getInt("id"), mysql.getResult().getString("nev")));
		}
		
		sql="SELECT * FROM rendelo_szoba";
		mysql.exec(sql);
		while (mysql.getResult().next()==true){
			szobaTomb.add(new RendeloSzoba(mysql.getResult().getInt("id"), mysql.getResult().getString("nev")));
		}
		
		sql="SELECT * FROM rendelo_idopont";
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
	}
	
	public void insert(String cmd, String nev) throws SQLException{
		if (cmd.equals("orvos")){
			String sql = "INSERT INTO rendelo_orvos SET nev = '"+nev+"'";
			mysql.exec(sql);
			String sql2 = "SELECT id FROM rendelo_orvos WHERE nev = '"+nev+"'";
			mysql.exec(sql2);
			int id = 0;
			while (mysql.getResult().next()){
				id = mysql.getResult().getInt("id");
			}
			if (id>0){
				orvosTomb.add(new RendeloOrvos(id, nev));
			} else {
				throw new SQLException("Not inserted");
			}
		} else if (cmd.equals("szoba")){
			String sql = "INSERT INTO rendelo_szoba SET nev = '"+nev+"'";
			mysql.exec(sql);
			String sql2 = "SELECT id FROM rendelo_szoba WHERE nev = '"+nev+"'";
			mysql.exec(sql2);
			int id = 0;
			while (mysql.getResult().next()){
				id = mysql.getResult().getInt("id");
			}
			if (id>0){
				szobaTomb.add(new RendeloSzoba(id, nev));
			} else {
				throw new SQLException("Not inserted");
			}
		}
	}
	
	public void addNewSchedule(Date tol, Date ig,  int szobaId,	 int orvosId, String alkalomTipus) throws SQLException, ParseException{
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(formatter.format(tol));
		System.out.println(formatter.format(ig));
		String sql = "INSERT INTO rendelo_idopont SET tol = '"+formatter.format(tol).toString()+"', ig = '"+formatter.format(ig).toString()+"', szobaId = '"+szobaId+"', orvosId = '"+orvosId+"', alkalomTipus = '"+alkalomTipus+"'";
		System.out.println(sql);
		mysql.exec(sql);
		String sql2 = "SELECT id FROM rendelo_idopont WHERE tol = '"+formatter.format(tol).toString()+"' AND ig = '"+formatter.format(ig).toString()+"' AND szobaId = '"+szobaId+"' AND orvosId = '"+orvosId+"' AND alkalomTipus = '"+alkalomTipus+"'";
		mysql.exec(sql2);
		int id = 0;
		while (mysql.getResult().next()){
			id = mysql.getResult().getInt("id");
		}
		if (id>0){
			RendeloIdopont r = new RendeloIdopont(id, tol, ig, szobaId, orvosId, alkalomTipus);
			System.out.println(r);
			idopontTomb.add(r);
		} else {
			throw new SQLException("Not inserted");
		}
	}
	
	
	public void edit(String cmd, String oldName, String newName) throws SQLException{
		int id = 0;
		if (cmd.equals("orvos")){
			RendeloOrvos o = null;
			Iterator<RendeloOrvos> it = orvosTomb.iterator();
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
				mysql.exec(selectSql);
				int count = 0;
				while (mysql.getResult().next()){
					count++;
				}
				if (count==0){
					orvosTomb.remove(o);
					throw new SQLException("Not Found");
				}
				String sql = "UPDATE rendelo_orvos SET nev='"+newName+"' WHERE id='"+id+"'";
				mysql.exec(sql);				
			} else {
				throw new SQLException("Not Found");
			}
		} else if (cmd.equals("szoba")){
			RendeloSzoba o = null;
			Iterator<RendeloSzoba> it = szobaTomb.iterator();
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
				mysql.exec(selectSql);
				int count = 0;
				while (mysql.getResult().next()){
					count++;
				}
				if (count==0){
					szobaTomb.remove(o);
					throw new SQLException("Not Found");
				}
				String sql = "UPDATE rendelo_szoba SET nev='"+newName+"' WHERE id='"+id+"'";
				mysql.exec(sql);				
			} else {
				throw new SQLException("Not Found");
			}
			
		}
		
	}
	
	public void delete(String cmd, String nev) throws SQLException{
		int id = 0;
		if (cmd.equals("orvos")){
			RendeloOrvos o = null;
			Iterator<RendeloOrvos> it = orvosTomb.iterator();
			while (it.hasNext()){
				o = it.next();
				if (o.getNev().equals(nev)){
					id = o.getId();
					break;
				}
			}
			if (id>0){
				String selectSql = "SELECT * FROM rendelo_orvos WHERE id='"+id+"'";
				mysql.exec(selectSql);
				int count = 0;
				while (mysql.getResult().next()){
					count++;
				}
				if (count==0){
					orvosTomb.remove(o);
					throw new SQLException("Not Found");
				}
				String sql = "DELETE FROM rendelo_orvos WHERE id='"+id+"'";
				mysql.exec(sql);		
				orvosTomb.remove(o);
			} else {
				throw new SQLException("Not Found");
			}
		} else if (cmd.equals("szoba")){
			RendeloSzoba o = null;
			Iterator<RendeloSzoba> it = szobaTomb.iterator();
			while (it.hasNext()){
				o = it.next();
				if (o.getNev().equals(nev)){
					id = o.getId();
					break;
				}
			}
			if (id>0){
				String selectSql = "SELECT * FROM rendelo_szoba WHERE id='"+id+"'";
				mysql.exec(selectSql);
				int count = 0;
				while (mysql.getResult().next()){
					count++;
				}
				if (count==0){
					szobaTomb.remove(o);
					throw new SQLException("Not Found");
				}
				String sql = "DELETE FROM rendelo_szoba WHERE id='"+id+"'";
				mysql.exec(sql);	
				szobaTomb.remove(o);
			} else {
				throw new SQLException("Not Found");
			}
			
		}
	}
	
	public ArrayList<RendeloOrvos> getOrvosTomb(){
		return orvosTomb;
	}
	
	public ArrayList<RendeloSzoba> getSzobaTomb(){
		return szobaTomb;
	}
	
	public ArrayList<RendeloIdopont> getIdopontTomb(){
		return idopontTomb;
	}

	public ArrayList<RendeloSzoba> searchForFreeRooms(Date tol, Date ig, String cmd, int orvosId) {
		ArrayList<RendeloSzoba> result = new ArrayList<RendeloSzoba>();
		
		result.removeAll(result);		
		result.addAll(szobaTomb);
		
		Calendar kivantKezdet = Calendar.getInstance();
		Calendar kivantVeg = Calendar.getInstance();
		kivantKezdet.setTime(tol);
		kivantVeg.setTime(ig);
		
			Iterator<RendeloIdopont> it = idopontTomb.iterator();
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
								reserved.setDoctorData(getDoctorNameById(r.getOrvosId())+": "+format.format(r.getTol())+"-"+format.format(r.getIg())+" "+r.getAlkalomTipus());
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
