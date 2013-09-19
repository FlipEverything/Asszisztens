package model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JFrame;
import tools.Base64Coder;
import view.BaseWindow;
import view.MainWindow;

/**
 * 
 */

/**
 * @author Laci
 *
 */
public class DatabaseMirrors {
	
	private String dest = System.getProperty("user.dir")+MainWindow.fileSepa;
	private String configFile = dest + "connection.conf";
	private ArrayList<Mirror> mirrors = new ArrayList<DatabaseMirrors.Mirror>();
	private int counter = -1;
	
	public class Mirror{
		private String url;
		private int port;
		private String database;
		private String username;
		private String password;
		private String encodedPassword;	
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		public String getDatabase() {
			return database;
		}
		public void setDatabase(String database) {
			this.database = database;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getEncodedPassword() {
			return encodedPassword;
		}
		public void setEncodedPassword(String encodedPassword) {
			this.encodedPassword = encodedPassword;
		}
	}
	
	public DatabaseMirrors(){
		//System.out.println(Base64Coder.encodeString("aj6720ldd").concat("R4q"));
		try{
			  FileInputStream fstream = new FileInputStream( configFile );
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  while ((strLine = br.readLine()) != null) {
				 if (strLine.length()>0){
					 if ((strLine.substring(0, 1).equals("[")) && (strLine.substring(strLine.length()-1, strLine.length()).equals("]"))){
						 mirrors.add(new Mirror());
						 counter++;
						 mirrors.get(counter).setUrl(readConfigLine(br));
						 mirrors.get(counter).setPort(Integer.parseInt(readConfigLine(br)));
						 mirrors.get(counter).setDatabase(readConfigLine(br));
						 mirrors.get(counter).setUsername(readConfigLine(br));
						 mirrors.get(counter).setPassword(readConfigLine(br));
						 mirrors.get(counter).setEncodedPassword(Base64Coder.decodeString(mirrors.get(counter).getPassword().substring(0,mirrors.get(counter).getPassword().length()-3)));
					 }
				 }
			  }
			  in.close();
		}catch (Exception e){
			BaseWindow.makeWarning("Nem tudom betölteni a szerver kapcsolat adatait! A program leáll!", e, "error", new JFrame());
			System.exit(0);
		}
	}
	
	public String readConfigLine(BufferedReader reader) throws IOException{
		String line = reader.readLine();
		String data = line.substring(line.indexOf("=")+1,line.length());
		return data;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public Mirror getMirror(int i){
		return mirrors.get(i);
	}
}
