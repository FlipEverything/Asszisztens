import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ServerSocket;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;


/**
 * @author Dobó László
 */
@SuppressWarnings("unused")
public class Updater implements ActionListener, PropertyChangeListener{
	public static final String fileSepa = System.getProperty("file.separator");
	private static JFrame frame;
	private int height = 100;
	private int width = 800;
	private boolean resizable = false;
	private boolean visible = false;
	private int locationX = 0;
	private int locationY = 0;
	private String title = "Updater";
	private int defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE;
	private URL url;
	private InputStream is = null;
	private DataInputStream dis;
	private String line;
	private String user="aranyklinika";
	private String password="L@$mPq{db*x6";
	private String link = "http://lddsystems.eu/projekt/asszisztens/data/";
	private String versionLink = link+"version.txt";
	private String updateLink = link+"update.zip";
	private String fileDest = System.getProperty("user.dir")+fileSepa+"update.zip";
	private String dest = System.getProperty("user.dir")+fileSepa;
	private String destLnk = dest + "Asszisztens.lnk";
	private String desktopPath1 = System.getProperty("user.home") + fileSepa + "Desktop" + fileSepa;
	private String desktopPath2 = System.getProperty("user.home") + fileSepa + "Asztal" + fileSepa;
	private double internetVersion;
	private double actualVersion;
	private Task task;
	private Window update;
	private JProgressBar pb;
	private JLabel label;
	private boolean noProgram = false;
	private static ServerSocket SERVER_SOCKET;
	
	class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            Random random = new Random();
            double progress = 0;
            setProgress(0);
            	 try
        	     {
        	        URL url = new URL( updateLink );
        	        url.openConnection();

        			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        			int contentLength = httpConn.getContentLength();
        			if (contentLength == -1) {
        				Window.makeWarning("Nem tudom megállapítani a fájl méretét!", new Exception(), "error", update.getFrame());
        			} 
        			InputStream reader = httpConn.getInputStream();
        			
        	        FileOutputStream writer = new FileOutputStream( fileDest );
        	        byte[] buffer = new byte[153600];
        	        int totalBytesRead = 0;
        	        int bytesRead = 0;
        	        
        	        while ((bytesRead = reader.read(buffer)) > 0)
        	        {  
        	           writer.write(buffer, 0, bytesRead);
        	           buffer = new byte[153600];
        	           totalBytesRead += bytesRead;
                       progress = (double)totalBytesRead / (double)contentLength * 100;
                       setProgress(Math.min((int)progress, 100));
        	        }
        	 
        	        writer.close();
        	        reader.close();
        	        httpConn.disconnect();
        	     }
        	     catch (MalformedURLException e)
        	     {
        	    	 Window.makeWarning("A megadott webcím érvénytelen!", e, "error", update.getFrame());
        	    	 start();
        	     }
        	     catch (IOException e)
        	     {
        	    	 Window.makeWarning("I/O hiba", e, "error", update.getFrame());
        	    	 start();
        	     }
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            
        }
	}
        
	@SuppressWarnings("deprecation")
	public Updater(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Window.makeWarning("Nem lehet a kinézetet beállítani!", e, "error", new JFrame());
		}
		try {
			SERVER_SOCKET = new ServerSocket(1334);
		} catch (IOException x) {
	    	Window.makeWarning("A program már egy példányban fut! A program most leáll...", x, "error", new JFrame());
	    	exit();
	    }

		update = new Window(width, height, resizable, visible, title, locationX, locationY, defaultCloseOperation);

		try {
			Authenticator.setDefault (new Authenticator() {
			    protected PasswordAuthentication getPasswordAuthentication() {
			        return new PasswordAuthentication (user, password.toCharArray());
			    }
			});

		    url = new URL( versionLink );
		    is =  url.openStream();
		    dis = new DataInputStream(new BufferedInputStream(is));
		    line = dis.readLine();
		    if (line != null) {
		        internetVersion = Double.parseDouble(line);
		        actualVersion = getVersion();
		        if ( internetVersion > actualVersion )
		        {
		        	Object[] objects = {"Igen", "Nem"};
		        	String askTitle="", askText="";
		        	if (noProgram == true)
		        	{
		        		askTitle = "Program telepítés"; 
		        		askText = "<html>Jelenlegi verzió: nem található<br/>" +
	        			"Elérhető verzió: "+ internetVersion+"<br/>" +
    					"Kívánja telepíteni?";
		        	} else {
		        		askTitle = "Frissítés elérhető"; 
		        		askText = "<html>Jelenlegi verzió: "+actualVersion+"<br/>" +
	        			"Elérhető verzió: "+ internetVersion+"<br/>" +
    					"Kívánja telepíteni?";
		        	}
		        	boolean ask = Window.ask(objects, askTitle, askText, update.getFrame());
		        	if (ask == true )
		        	{
		        		label = new JLabel(" Frissítés letöltése folyamatban... Ne szakítsa meg a folyamatot! ");
		        		update.getFrame().setVisible(true);
		        		update.getFrame().setLayout(new GridLayout(2, 1));
		        		update.getFrame().add( label );
		        		update.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		        	    boolean done = false;
		        	    task = new Task();
		        		task.addPropertyChangeListener(this);
		        	    task.execute();
		        		pb = new JProgressBar(0, 100);
		        		pb.setValue(0);
		        		pb.setEnabled(true);
		        		pb.setIndeterminate(false);
		        		pb.setStringPainted(false);
		        		update.getFrame().add( pb );
		        	} else {
		        		start();
		        	}
		        } else {
		        	start();
		        }
		    }
		} catch (MalformedURLException mue) {
			Window.makeWarning("Hibás a megadott webcím! Az új verzió keresésekor hiba lépett fel!", mue, "error", update.getFrame());
			start();
		} catch (IOException ioe) {
			Window.makeWarning("I/O hiba. Az új verzió keresésekor hiba lépett fel!", ioe, "error", update.getFrame());
			start();
		} finally {
		    try {
		        is.close();
		    } catch (IOException ioe) {
		    	Window.makeWarning("I/O hiba. Az új verzió keresésekor hiba lépett fel!", ioe, "error", update.getFrame());
		        start();
		    } catch (NullPointerException ne) {
		    	Window.makeWarning("Nem létező mutató! Az új verzió keresésekor hiba lépett fel!", ne, "error", update.getFrame());
		    	start();
		    }
		}
	}
	
	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new Updater();
            }
        });
	}
	
	public void start() {
		String[] run = {"java","-jar","Asszisztens.jar"};
        try { //win
            Runtime.getRuntime().exec(run);
        } catch (Exception ex) {
        	try{	//mac
				Runtime.getRuntime().exec("open Asszisztens.jar");
			} catch (IOException e2){
				Window.makeWarning("Főalkalmazás megnyitási hiba!", e2, "error", new JFrame());
			}
        }
        exit();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            pb.setValue(progress);
            if (progress==100)
            {
            	pb.setIndeterminate(true);
            	label.setText(" Telepítés...");
            	Toolkit.getDefaultToolkit().beep();
                update.getFrame().setCursor(null);
            	extract( fileDest );
            }
        } 
		
	}
	
	public double getVersion()
	{
		double version = 0.0;
		try{
			  FileInputStream fstream = new FileInputStream( dest + "version.txt");
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine = br.readLine();
			  if (strLine != null)   {
				  version = Double.parseDouble(strLine);
			  }
			  in.close();
		}catch (Exception e){
			noProgram = true;
		}
		return version;
	}
	
	public void setVersion()
	{
		try{
			  FileWriter fstream = new FileWriter( dest + "version.txt");
			  BufferedWriter out = new BufferedWriter(fstream);
			  String v = internetVersion+"";
			  out.write(v);
			  out.close();
			  fstream.close();
		}catch (Exception e){
			Window.makeWarning("A verziószámot nem tudtam menteni!", e, "error", update.getFrame());
			exit();
		}
	}
	public void extract(String filename)
    {
        try
        {
            byte[] buf = new byte[1024];
            ZipInputStream zipinputstream = null;
            ZipEntry zipentry;
            zipinputstream = new ZipInputStream(
                new FileInputStream(filename));

            zipentry = zipinputstream.getNextEntry();
            while (zipentry != null) 
            { 
                String entryName = zipentry.getName();
                
                int n;
                FileOutputStream fileoutputstream;
                File newFile = new File(entryName);
                String directory = newFile.getParent();
                
                if(directory == null)
                {
                    if(newFile.isDirectory())
                        break;
                }
                
                fileoutputstream = new FileOutputStream(
                   dest + entryName);             

                while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
                    fileoutputstream.write(buf, 0, n);

                fileoutputstream.close(); 
                zipinputstream.closeEntry();
                zipentry = zipinputstream.getNextEntry();

            }

            zipinputstream.close();
            File updateZip = new File( fileDest );
            boolean success = updateZip.delete();
            if (!success){
            	Window.makeWarning("Az update.zip állományt nem tudtam letörölni!", new Exception(), "error", update.getFrame());
            	exit();
            }else{
            	try{
             	   File afile = new File( destLnk );
             	   if(!afile.renameTo(new File( desktopPath1 + afile.getName()))){
             		  if(!afile.renameTo(new File( desktopPath2 + afile.getName()))){
             			 
             		  }
             	   }
             	}catch(Exception e){
             		Window.makeWarning("Ismeretlen hiba", e, "error", update.getFrame());
             	}
             	File icon = new File( destLnk );
                icon.delete();
                setVersion();
            	Window.makeWarning("Sikeresen telepítettem az állományokat!", new Exception(), "success", update.getFrame());
            	update.getFrame().setVisible(false);
            	start();
            }
        }
        catch (Exception e)
        {
        	Window.makeWarning("Ismeretlen hiba", e, "error", update.getFrame());
        }
    }
	
	public void exit(){
		try {
			SERVER_SOCKET.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	
}
