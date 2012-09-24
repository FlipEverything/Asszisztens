package tools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.StringTokenizer;

import database.Connect;


public class ReadCsv {
	private String dest = "C:\\Users\\Laci\\Desktop\\"; //System.getProperty("user.dir")+"\\";
	FileInputStream fstream;
	DataInputStream in;
	BufferedReader br;
	
	public ReadCsv(Connect c) throws SQLException{
		try {
			openCsv();
			readCsv(c);
			closeCsv();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void openCsv() throws FileNotFoundException, UnsupportedEncodingException{
		  fstream = new FileInputStream( dest + "lista.csv");
		  in = new DataInputStream(fstream);
		  br = new BufferedReader(new InputStreamReader(in, "UTF8"));
	}
	
	public void closeCsv() throws IOException{
		in.close();
	}
	
	public void readCsv(Connect sql) throws IOException, SQLException{
		String strLine;
		String token;
		String[] data = new String[8];
		String command;
		int i;
		while ((strLine = br.readLine()) != null)   {
			i = 0;
			command = "INSERT INTO labor (`id`, `nev1`, `nev2`, `megj`, `labor_ar`, `ido`, `partner_ar`, `aranyklinika_ar`, `alapdij`, `csoport`) VALUES( NULL ";
			StringTokenizer t = new StringTokenizer(strLine,";", true);
			//System.out.println("------------------LINE---------------------"+strLine);
			if ((t.hasMoreTokens())==true){
				token = t.nextToken();
				if (token.equals(";")) { token = ""; } else {if ((t.hasMoreTokens())==true){ t.nextToken();}}
				data[i] = token; i++;
				command+=",'"+token+"'";
				//System.out.println("Név1: "+token);
			}
			if ((t.hasMoreTokens())==true){
				token = t.nextToken();
				if (token.equals(";")) { token = ""; } else {if ((t.hasMoreTokens())==true){ t.nextToken(); }}
				data[i] = token; i++;
				command+=",'"+token+"'";
				//System.out.println("Név2: "+token);
			}
			if ((t.hasMoreTokens())==true){
				token = t.nextToken();
				if (token.equals(";")) { token = ""; } else {if ((t.hasMoreTokens())==true){ t.nextToken(); }}
				data[i] = token; i++;
				command+=",'"+token+"'";
				//System.out.println("Megjegyzés: "+token);
			}
			if ((t.hasMoreTokens())==true){
				token = t.nextToken();
				if (token.equals(";")) { token = "0"; } else {if ((t.hasMoreTokens())==true){ t.nextToken(); }}
				data[i] = token; i++;
				token = token.replace(" ", "");
				command+=","+Integer.parseInt(token)+"";
				//System.out.println("Partner ár: "+token);
			}
			if ((t.hasMoreTokens())==true){
				token = t.nextToken();
				if (token.equals(";")) { token = ""; } else {if ((t.hasMoreTokens())==true){ t.nextToken(); }}
				data[i] = token; i++;
				command+=",'"+token+"'";
				//System.out.println("Idő: "+token);
			}
			if ((t.hasMoreTokens())==true){
				token = t.nextToken();
				if (token.equals(";")) { token = "0"; } else {if ((t.hasMoreTokens())==true){ t.nextToken(); }}
				data[i] = token; i++;
				token = token.replace(" ", "");
				command+=","+Integer.parseInt(token)+"";
				//System.out.println("Labor ár: "+token);
			}
			if ((t.hasMoreTokens())==true){
				token = t.nextToken();
				if (token.equals(";")) { token = "0"; } else {if ((t.hasMoreTokens())==true){ t.nextToken(); }}
				data[i] = token; i++;
				token = token.replace(" ", "");
				command+=","+Integer.parseInt(token)+"";
				//System.out.println("Arany ár: "+token);
			}
			while (t.hasMoreTokens() == true){
				//System.out.println("PLUSZ ADAT: "+t.nextToken());
			}
			for (int j=i; j<7; j++){
				if (j!=7)
					command+=",";
				token="0";
				token = token.replace(" ", "");
				command+=""+Integer.parseInt(token)+"";
			}
			//System.out.println("------------------ENDLINE---------------------");
			command+=", 'nem', 0);";
			System.out.println(command);
			//Window.makeWarning(command, new Exception(), "success", new JFrame());
			sql.exec(command);
		}
	}
}
