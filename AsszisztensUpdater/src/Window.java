

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
/**
 * 
 */

/**
 * @author Laci
 *
 */
public class Window {
	private JFrame frame;
	
	private static int screenHeight = 0;
	private static int screenWidth = 0;
	
	public static int getScreenHeight() {
		return screenHeight;
	}

	public static void setScreenHeight(int screenHeight) {
		Window.screenHeight = screenHeight;
	}

	public static int getScreenWidth() {
		return screenWidth;
	}

	public static void setScreenWidth(int screenWidth) {
		Window.screenWidth = screenWidth;
	}

	public Window(int width, int height, boolean resizable, boolean visible, String title, int locationX, int locationY, int defaultCloseOperation){
		if ((getScreenHeight()==0) || (getScreenWidth()==0))
		{
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension screenSize = tk.getScreenSize();
			setScreenHeight(screenSize.height);
			setScreenWidth(screenSize.width);
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			makeWarning("Nem lehet a kinézetet beállítani!", e, "error", getFrame());
		}
		setFrame(new JFrame());						
		getFrame().setSize( new Dimension( width, height ) );		
		getFrame().setResizable( resizable );	
        getFrame().setVisible( visible );		
        getFrame().setTitle( title );
        if ( ( locationX == 0) && (locationY == 0) )
        {
        	getFrame().setLocation((screenWidth-getFrame().getWidth())/2, (screenHeight-getFrame().getHeight())/2);	
        } 
        else
        {
        	getFrame().setLocation( locationX , locationY );
        }
        getFrame().setDefaultCloseOperation( defaultCloseOperation );	
        getFrame().addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
				         System.exit(0);
              }
        });
	
	}


	/**
	 * Informaciok megjelenitese egy felugro ablakban
	 */
	public static void makeWarning(String errorCode, Exception e, String type, JFrame frame){
		if (type.equals("error")){
			JOptionPane.showMessageDialog(frame, "<html><b>"+errorCode+"</b>\r\n\r\n"+
					"Localized Message: "+e.getLocalizedMessage()+"\r\n"+
					"Message: "+e.getMessage()+"\r\n"+
					"Cause: "+e.getCause()+"\r\n"+
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

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public JFrame getFrame() {
		return frame;
	}
	
	
	
}
