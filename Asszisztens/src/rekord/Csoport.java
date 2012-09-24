package  rekord;

public class Csoport {
	private int id;
	private String nev;
	
	public Csoport(int id, String nev) {
		super();
		this.id = id;
		this.nev = nev;
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
}
