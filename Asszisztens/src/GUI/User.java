package GUI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import database.DBConnect;

public class User implements ActionListener{
	private JPasswordField password;
	private JButton go;
	private BaseWindow login;
	private boolean loggedIn;
	private String encodedPassword;
	private DBConnect mysql;
	//private JLabel label;
	
	public User(DBConnect c){
		loggedIn = false;
		mysql = c;
		password = new JPasswordField();
		go = new JButton("Bejelentkezés");
		go.addActionListener(this);
	}

	public void makePanel(JLabel l){
		password.addKeyListener(
			new KeyAdapter(){
				public void keyReleased( KeyEvent e ) {
					if( e.getKeyCode() == KeyEvent.VK_ENTER )
					{
						try {
							login();
						} catch (SQLException e1) {
							BaseWindow.makeWarning("SQL parancsfuttatási hiba!", e1, "error", new JFrame());
						}
					}
				}
			}
		);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JLabel("<html><b><p style='padding: 5p; padding-left: 0px;'>Admin bejelentkezés: adja meg jelszavát!</p></b>"), "North");
		panel.add(password,"Center");
	    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new GridLayout(1,2));
		bottom.add(new JLabel());
		bottom.add(go);
		panel.add(bottom,"South");
		
		//label = l;
		login = new BaseWindow(250, 200, false, true, "Bejelentkezés", 0, 0, JFrame.HIDE_ON_CLOSE, false){
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -5146903044568914330L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};

		login.add(panel);
		login.pack();
		login.setCenter();
	}
	
	public void login() throws SQLException{
		char[] pass = password.getPassword();
		String stringPass = new String(pass);
		encodedPassword=hashPassword(stringPass);
		mysql.exec("SELECT * FROM admin;");
		while(mysql.getResult().next())
		{
			if (mysql.getResult().getString("password").equals(encodedPassword)){
				loggedIn = true;
				BaseWindow.makeWarning("Sikeres bejelentkezés", new Exception(), "success", new JFrame());
				login.setVisible(false);
				//label.setText("<html><div style='margin: 2px;'><b>Jogosultság:</b> Admin</div></html>");
			}
		}
		if (loggedIn==false){
			BaseWindow.makeWarning("Sikertelen bejelentkezés, próbálja újra!", new Exception(), "success", new JFrame());
		}
		mysql.getResult().close();
		password.setText(null);
	}
	
	public void logout(){
		Object[] o = {"Igen","Nem"};
		boolean biztos = BaseWindow.ask(o, "Megerősítés", "Biztosan kijelentkezik?", new JFrame());
		if (biztos==true){
			loggedIn=false;
			BaseWindow.makeWarning("Sikeres kijelentkezés", new Exception(), "success", new JFrame());
			//label.setText("<html><div style='margin: 2px;'><b>Jogosultság:</b> Vendég</div></html>");
		}
	}
	
	public void modify(){
		//TODO
	}
	
	public static String hashPassword(String password) {
		String hashword = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(password.getBytes());
			BigInteger hash = new BigInteger(1, md5.digest());
			hashword = hash.toString(16);
		} catch (NoSuchAlgorithmException nsae) {
		
		}
		return hashword;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==go){
			try {
				login();
			} catch (SQLException e1) {
				BaseWindow.makeWarning("SQL parancsfuttatási hiba!", e1, "error", new JFrame());
			}
		}
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

}
