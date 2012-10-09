package rekord;

import java.util.Date;

import tools.Const;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

public class RendeloIdopont {
	private int id;
	private Date tol;
	private Date ig;
	private int szobaId;
	private int orvosId;
	private String alkalomTipus;
	
	public RendeloIdopont(int id, Date tol, Date ig,
			int szobaId, int orvosId, String alkalomTipus) {
		super();
		this.id = id;
		this.tol = tol;
		this.ig = ig;
		this.szobaId = szobaId;
		this.orvosId = orvosId;
		this.alkalomTipus = alkalomTipus;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getTol() {
		return tol;
	}
	public void setTol(Date tol) {
		this.tol = tol;
	}
	public Date getIg() {
		return ig;
	}
	public void setIg(Date ig) {
		this.ig = ig;
	}
	public int getSzobaId() {
		return szobaId;
	}
	public void setSzobaId(int szobaId) {
		this.szobaId = szobaId;
	}
	public int getOrvosId() {
		return orvosId;
	}
	public void setOrvosId(int orvosId) {
		this.orvosId = orvosId;
	}
	public String getAlkalomTipus() {
		return alkalomTipus;
	}
	public void setAlkalomTipus(String alkalomTipus) {
		this.alkalomTipus = alkalomTipus;
	}
	
	@Override
	public String toString() {
		String data = null;
		Calendar tolC = Calendar.getInstance();
		Calendar igC = Calendar.getInstance();
		
		tolC.setFirstDayOfWeek(1);
		igC.setFirstDayOfWeek(1);
		
		tolC.setTime(tol);
		igC.setTime(ig);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
		
		if (alkalomTipus.equals("ismetlodo")){
			data = "Ismétlődő, minden héten: "+Const.getDayOfTheWeek(tolC.get(Calendar.DAY_OF_WEEK))+", "+formatTime.format(tol)+" - "+formatTime.format(ig);
		} else if (alkalomTipus.equals("alkalmi")){
			data = "Alkalmi, ekkor: "+format.format(tol)+", "+formatTime.format(tol)+" - "+formatTime.format(ig);
		} else if (alkalomTipus.equals("lemondas")){
			data = "Lemondás, ekkor: "+format.format(tol)+", "+formatTime.format(tol)+" - "+formatTime.format(ig);
		}
		return data;
	}
}
