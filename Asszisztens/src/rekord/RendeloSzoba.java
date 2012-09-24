package rekord;

public class RendeloSzoba {
	private int id;
	private String nev;
	private boolean reserved;
	private String doctorData;
	
	public RendeloSzoba(int id, String nev) {
		super();
		this.id = id;
		this.nev = nev;
		this.reserved = false;
		this.doctorData = "";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNev() {
		return nev;
	}
	public void setNev(String nev) {
		this.nev = nev;
	}
	@Override
	public String toString(){
		return nev;	
	}
	public String getDoctorData() {
		return doctorData;
	}
	public void setDoctorData(String doctorName) {
		this.doctorData = doctorName;
	}
	public boolean isReserved() {
		return reserved;
	}
	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}
	
}
