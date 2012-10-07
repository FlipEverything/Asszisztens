package GUI;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
/**
 * 
 */

/**
 * @author Laci
 *
 */
public abstract class BaseWindow extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4353750273090410404L;
	
	public static final int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
	public static final int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int taskBar = 50;
	public static final int offset = 50;
	
	private JMenuBar menuBar;
	private JMenu menu;
	
	public BaseWindow(int width, int height, boolean resizable, boolean visible, String title, int locationX, int locationY, int defaultCloseOperation, boolean exit){
		menuBar = new JMenuBar();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
		} catch (Exception e) {
			makeWarning("Nem lehet a kinézetet beállítani!", e, "error", this);
		}					
		
		setResizable( resizable );	
        setVisible( visible );		
        setTitle( title );
        if ((width == 0) && (height == 0)){
        	setSize( new Dimension( screenWidth-offset, screenHeight-taskBar-offset ) );
        	setPreferredSize( new Dimension( screenWidth-offset, screenHeight-taskBar-offset ) );
        } else {
        	setSize( new Dimension( width, height ) );
        	setPreferredSize( new Dimension( width, height ) );	
        }
        if ( ( locationX == 0) && (locationY == 0) ) {
        	setLocation((screenWidth-getWidth())/2, (screenHeight-getHeight()-taskBar)/2);	
        } 
        else
        {
        	setLocation( locationX , locationY );
        }
        setDefaultCloseOperation( defaultCloseOperation );
        if (exit==true){
	        addWindowListener(new WindowAdapter() {
	            public void windowClosing(WindowEvent e) {
					         System.exit(0);
	              }
	        });
        }
	
	}

	public void packTheFrame(){
		pack();
	}

	public void setCenter(){
		setLocation((screenWidth-this.getWidth())/2, (screenHeight-this.getHeight())/2);	
	}
	
	/**
	 * Informaciok megjelenitese egy felugro ablakban
	 */
	public static void makeWarning(String errorCode, Exception e, String type, JFrame frame){
		if (type.equals("error")){
			JOptionPane.showMessageDialog(frame, "<html><b>"+errorCode+"</b>\r\n\r\n"+
					"Message: "+e.getMessage()+"\r\n"+
					"Class: "+e.getClass()+"\r\n"+
					"StackTrace: "+e.getStackTrace(), "Hiba", JOptionPane.ERROR_MESSAGE);
		} else if (type.equals("success")){
			JOptionPane.showMessageDialog(frame, errorCode+"\r\n");
		} else {
			JOptionPane.showMessageDialog(frame, "Hiba történt a hibajelentő rendszerben!");
		}
	}
	
	public static boolean ask(Object[] options, String title, String message, JFrame frame){
		int n = JOptionPane.showOptionDialog(frame, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n == JOptionPane.YES_OPTION){
			return true;
		} else {
			return false;
		}
	}
	
	public void newJMenu(String label, int key, String description){
		menu = new JMenu(label); 
		menu.setMnemonic(key);
		menu.getAccessibleContext().setAccessibleDescription(description);
		menuBar.add(menu);
	}
	
	public void newJMenu(String label, String description){
		menu = new JMenu(label); 
		menu.getAccessibleContext().setAccessibleDescription(description);
		menuBar.add(menu);
	}
	
	public void newJMenuItem(String itemName, String label, int key, String description, boolean enabled){
		JMenuItem item = new JMenuItem(label);
		item.setAccelerator(KeyStroke.getKeyStroke(key, ActionEvent.CTRL_MASK));
		item.setActionCommand(itemName);
		item.setEnabled(enabled);
		item.getAccessibleContext().setAccessibleDescription(description);
		item.addActionListener(this);
		menu.add(item);
	}
	
	public void newJMenuItem(String itemName, String label, String description, boolean enabled){
		JMenuItem item = new JMenuItem(label);
		item.setActionCommand(itemName);
		item.setEnabled(enabled);
		item.getAccessibleContext().setAccessibleDescription(description);
		item.addActionListener(this);
		menu.add(item);
	}
	
	public void setMenu(){
		this.setJMenuBar(menuBar);
	}
}
