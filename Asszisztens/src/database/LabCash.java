package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import rekord.Labor;

public class LabCash {
	private Connect mysql;
	private ArrayList<Labor> laborLista;
	
	public LabCash(){
		mysql = new Connect();
		laborLista = new ArrayList<Labor>();
	}
	
	public int downloadAlapdij() throws SQLException{
		int fizetendo;
		mysql.exec("SELECT SUM(aranyklinika_ar) as alapdij FROM labor WHERE alapdij='igen';");
		mysql.getResult().next();
		try {
			fizetendo = Integer.parseInt(mysql.getResult().getString("alapdij"));
		} catch (NumberFormatException e){
			fizetendo = 0;
		}
		return fizetendo;
	}
	
	public ArrayList<Labor> downloadResult() throws SQLException{
		String sql = "SELECT * FROM labor WHERE allapot='aktiv' ORDER BY csoport;";
		mysql.exec(sql);
		/*ResultSetMetaData metaData = mysql.getResult().getMetaData();
        int rowCount = metaData.getColumnCount();
        for (int i = 0; i < rowCount; i++) {
            System.out.print(metaData.getColumnName(i + 1) + "  \t");
            System.out.println(metaData.getColumnTypeName(i + 1));
        }*/
		while (mysql.getResult().next()==true){
			laborLista.add(
					new Labor(
						Integer.parseInt(mysql.getResult().getString("id")),
						mysql.getResult().getString("nev1"), 
						mysql.getResult().getString("nev2"),
						mysql.getResult().getString("megj"),
						mysql.getResult().getString("ido"), 
						Integer.parseInt(mysql.getResult().getString("labor_ar")),
						Integer.parseInt(mysql.getResult().getString("partner_ar")),
						Integer.parseInt(mysql.getResult().getString("aranyklinika_ar")),
						mysql.getResult().getString("alapdij"),
						Integer.parseInt(mysql.getResult().getString("csoport"))
					)
			);
		}
		return laborLista;
	}
	
	public ArrayList<Labor> getLaborLista() {
		return laborLista;
	}

	public void setLaborLista(ArrayList<Labor> laborLista) {
		this.laborLista = laborLista;
	}

	public ResultSet getItems(int id) throws SQLException{
		mysql.exec("SELECT nev FROM csoport WHERE id='"+id+"'");
		return mysql.getResult();
	}
}
