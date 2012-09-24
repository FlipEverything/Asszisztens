package rekord;


public class Labor {
	private int id;
	private String nev1;
	private String nev2;
	private String megj;
	private String ido;
	private int laborAr;
	private int partnerAr;
	private int aranyklinikaAr;
	private String alapdij;
	private int csoport;
	
	public Labor(int id, String nev1, String nev2, String megj, String ido,
			int laborAr, int partnerAr, int aranyklinikaAr, String alapdij, int csoport) {
		this.id = id;
		this.nev1 = nev1;
		this.nev2 = nev2;
		this.megj = megj;
		this.ido = ido;
		this.laborAr = laborAr;
		this.partnerAr = partnerAr;
		this.aranyklinikaAr = aranyklinikaAr;
		this.alapdij = alapdij;
		this.setCsoport(csoport);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNev1() {
		return nev1;
	}
	public void setNev1(String nev1) {
		this.nev1 = nev1;
	}
	public String getNev2() {
		return nev2;
	}
	public void setNev2(String nev2) {
		this.nev2 = nev2;
	}
	public String getMegj() {
		return megj;
	}
	public void setMegj(String megj) {
		this.megj = megj;
	}
	public String getIdo() {
		return ido;
	}
	public void setIdo(String ido) {
		this.ido = ido;
	}
	public int getLaborAr() {
		return laborAr;
	}
	public void setLaborAr(int laborAr) {
		this.laborAr = laborAr;
	}
	public int getPartnerAr() {
		return partnerAr;
	}
	public void setPartnerAr(int partnerAr) {
		this.partnerAr = partnerAr;
	}
	public int getAranyklinikaAr() {
		return aranyklinikaAr;
	}
	public void setAranyklinikaAr(int aranyklinikaAr) {
		this.aranyklinikaAr = aranyklinikaAr;
	}
	public String getAlapdij() {
		return alapdij;
	}
	public void setAlapdij(String alapdij) {
		this.alapdij = alapdij;
	}

	public void setCsoport(int csoport) {
		this.csoport = csoport;
	}

	public int getCsoport() {
		return csoport;
	}
}
