package  rekord;

import java.awt.Color;
import java.util.Random;

public class Csoport {
	private int id;
	private String nev;
	private Color szin;
	
	public Csoport(int id, String nev) {
		super();
		this.id = id;
		this.nev = nev;
		Random rand = new Random();
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		setSzin(new Color(r, g, b));
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
	public String toString() {
		return nev;
	}

	public Color getSzin() {
		return szin;
	}

	public void setSzin(Color szin) {
		this.szin = szin;
	}
}
