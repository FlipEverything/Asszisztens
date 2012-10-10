package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import database.DBConnect;
import database.LabCash;
import rekord.Labor;


public class LabCashWindow extends BaseWindow implements ItemListener, ActionListener, DocumentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1229457295479031303L;
	//GUI Components
	private JScrollPane felsoScroll;
	private JPanel felso;
	private JPanel also;
	private JPanel lista;
	private JPanel gombok;
	private JPanel keresoPanel;
	private JLabel kasszaVegosszeg;
	private JPanel loadingBar;
	private JProgressBar pb;
	private JButton deselectAll;
	private JTextField kereses;
	
	private ArrayList<JCheckBox> checkBoxLista;
	private ArrayList<Labor> valasztott;
	
	//Window dimensions & options
	private static String title = "Labor fizetés összesítő";
	private static int preferredHeight = 0;
	private static int preferredWidth = 0;
	private static int height = 0;
	private static int width = 0;
	private static int locationX = 0;
	private static int locationY = 0;
	private static boolean resizable = true;
	private static boolean visible = false;
	private static int defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE;
	private static boolean exit = false;
	private int listaWidth = 350;
	private int alsoHeight = 100;
	private int buttonHeight = 30;
	private int searchHeight = 20;
	private int componentWidth; //offset
	
	//Database connection
	private LabCash labCash;
	private int fizetendo;
	private boolean firstDownload = true;
	
	public LabCashWindow(DBConnect mysql){
		super(preferredWidth, preferredHeight, resizable, visible, title, locationX, locationY, defaultCloseOperation, exit);
		height = getHeight();
		width = getWidth();
		
		labCash = new LabCash(mysql);
		
		newJMenu("Fájl", "Alap menü");
			newJMenuItem("reLoad", "Adatok újraletöltése", "", true);
		setMenu();
		createWindowContent();
	}
	
	public void initComponents(){
		deselectAll = new JButton("Összes kijelölés megszüntetése");
		valasztott = new ArrayList<Labor>();
		checkBoxLista = new ArrayList<JCheckBox>();
		felso = new JPanel();
		felsoScroll = new JScrollPane(felso);
		also = new JPanel();
		lista = new JPanel();
		gombok = new JPanel();
		kereses = new JTextField();
		keresoPanel = new JPanel();
		kasszaVegosszeg = new JLabel();
		loadingBar = new JPanel();
		pb = new JProgressBar();
	}
	
	public void deleteComponents(){
		deselectAll = null;
		valasztott = null;
		checkBoxLista = null;
		felso = null;
		felsoScroll = null;
		also = null;
		lista = null;
		gombok = null;
		kereses = null;
		keresoPanel = null;
		kasszaVegosszeg = null;
		loadingBar = null;
		pb = null;
		labCash = null;
	}
	
	public void createWindowContent(){
		//Create main objects
		initComponents();
		
		componentWidth = (int)(width * 0.25);	
		deselectAll.addActionListener(this);		
		
		felso.setLayout(new BoxLayout(felso, BoxLayout.PAGE_AXIS));
		felso.setSize(new Dimension(width-listaWidth, height-alsoHeight));
		
		felsoScroll.setSize(width-listaWidth, height-alsoHeight);
		felsoScroll.setPreferredSize(new Dimension(width-listaWidth, height-alsoHeight));
		felsoScroll.getVerticalScrollBar().setUnitIncrement(30);
		
		also.setLayout(new BorderLayout());
		
		TitledBorder title1;
		Border blackline = BorderFactory.createLineBorder(Color.gray);
		title1 = BorderFactory.createTitledBorder(blackline, "Választott laborvizsgálatok");
		lista.setBorder(title1);
		lista.setLayout(new BoxLayout(lista, BoxLayout.PAGE_AXIS)); 
		lista.setPreferredSize(new Dimension(listaWidth, height-alsoHeight));
		lista.setSize(new Dimension(listaWidth, height-alsoHeight));
		lista.setAlignmentY(Component.TOP_ALIGNMENT);
				
		gombok.setPreferredSize(new Dimension(width, buttonHeight));
		gombok.setLayout(new BoxLayout(gombok, BoxLayout.X_AXIS));
		gombok.add(deselectAll);
			
		kereses.setPreferredSize(new Dimension(width, searchHeight));
		kereses.getDocument().addDocumentListener(this);
		
		keresoPanel.setLayout(new BorderLayout());
		keresoPanel.add(gombok, "North");
		keresoPanel.add(kereses, "South");
				
		kasszaVegosszeg.setSize(width, height);
		also.add(kasszaVegosszeg,"East");
			
		loadingBar.setLayout(new BorderLayout());
		loadingBar.setPreferredSize(new Dimension(width, height));
		loadingBar.add(new JLabel("<html><b><span style='font-size: 30px; text-align: center;'>Adatok letöltése folyamatban...</span></html>",SwingConstants.CENTER),"Center");
		
		add(loadingBar,"Center");
		add(pb,"South");
			
		/////////////////////////////////////UPDATE///////////////////////////////
		SwingUtilities.updateComponentTreeUI(this);
		///////////////////////////////////UPDATE/////////////////////////////////
	
		this.setFizetendo(0);
		
		try {
			this.setFizetendo(labCash.downloadAlapdij());
		} catch (SQLException e) {
			BaseWindow.makeWarning("SQL parancsfuttatási hiba!", e, "error", this);
		}
	}
	
	public void startTransaction(){
		class DownloadThread extends Thread{
			LabCash lc;
			
			public DownloadThread(LabCash lc){
				this.lc = lc;
			}
			
			public void run(){				
				try {				
					Iterator<Labor> rekordIt = lc.downloadResult().iterator();
					int elozo = 0;
					while ( rekordIt.hasNext() ){
				    	Labor j = rekordIt.next();
				    	if (elozo!=j.getCsoport()){
				    		ResultSet rs = labCash.getItems(j.getCsoport());
				    		if (rs.next() == true ){
				    			String labelText = "<html><table><tr style='background-color: #5AAD41; color: white;'><td style='width: "+(felsoScroll.getPreferredSize().width-componentWidth)+"px;'><b>"+rs.getString("nev")+"</b></td></tr></table></html>";
				    			felso.add(new JLabel(labelText));				    			
				    		}
				    	}
				    	rekordPaint(j);
				    	elozo = j.getCsoport();
				    	pb.setValue(pb.getValue()+1);
				    }
					
					remove(loadingBar);
					remove(pb);
					setLayout(new BorderLayout());
					add(felsoScroll,"Center");
					add(lista,"East");
					add(keresoPanel,"North");
					add(also,"South");
					refreshWithNewDatas();
				    refresh();
				    
				} catch (SQLException e) {
					BaseWindow.makeWarning("SQL parancsfuttatási hiba!", e, "error", new JFrame());
				}		
			}
		}
		
		DownloadThread d = new DownloadThread(labCash);
		d.start();	
	}
	
	public void rekordPaint(Labor j){
    	String masodik = "";
    	if (!j.getNev2().equals("")){
    		masodik = "("+j.getNev2()+")";
    	}
    	if (j.getAlapdij().equals("nem")){
    		String megj = "", ido="";
    		if (!j.getMegj().equals("")){
    			if (j.getMegj().length()>50){
    				megj = "<br/><table><tr><td style='width: "+(felsoScroll.getPreferredSize().width-componentWidth)+"px;'>Megj: "+j.getMegj()+"</td></tr></table>";
    			} else {
    				megj = " Megj: "+j.getMegj();
    			}
    		}
    		if (!j.getIdo().equals("")){
    			ido = " ("+j.getIdo()+")";
    		}
    		
    		JCheckBox ck = new JCheckBox("<html><b>"+j.getNev1()+"</b> "+masodik+ido+megj+"</html>");
    		ck.addItemListener(this);
    		checkBoxLista.add(ck);
    		felso.add(ck);
    	}
	}	
	
	public void beallitKasszaVegosszeg(int osszeg){
		kasszaVegosszeg.setText("<html><div style='text-align: right;'><span style='font-size: 20px; font-weight: bold;'>Fizetendő:</span> <span style='font-size: 30px; font-weight: bold;'>"+osszeg+"&nbsp;HUF&nbsp;</span></div></html>");
	}
	
	public void refreshWithNewDatas(){
		lista.removeAll();
	    Iterator<Labor> itAlapdij = labCash.getLaborLista().iterator();
	    while ( itAlapdij.hasNext() ){
	    	Labor j = itAlapdij.next();
	    	String nev = j.getNev1();
	    	if (nev.length()>35){
	    		nev = nev.substring(0,35)+"...";
	    	}
	    	if (j.getAlapdij().equals("igen")){
	    		lista.add(new JLabel("<html><b>"+nev+"</b> ("+j.getAranyklinikaAr()+" HUF)</html>"));
	    	}
	    }
	    Iterator<Labor> it = valasztott.iterator(); 
	    while ( it.hasNext() ){
	    	Labor j = it.next();
	    	String nev = j.getNev1();
	    	if (nev.length()>35){
	    		nev = nev.substring(0,35)+"...";
	    	}
	    	lista.add(new JLabel("<html><b>"+nev+"</b> ("+j.getAranyklinikaAr()+" HUF)</html>"));
	    }
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		Iterator<JCheckBox> it = checkBoxLista.iterator();
		JCheckBox clickObject;
		int i = 0;
		while (it.hasNext()){
			if (labCash.getLaborLista().get(i).getAlapdij().equals("igen")){
				i++;
			}
				clickObject = it.next();
				if (clickObject==arg0.getSource()){
					if (clickObject.getSelectedObjects()==null){
						setFizetendo(getFizetendo()-labCash.getLaborLista().get(i).getAranyklinikaAr());
						valasztott.remove(labCash.getLaborLista().get(i));
					}else{
						setFizetendo(getFizetendo()+labCash.getLaborLista().get(i).getAranyklinikaAr());
						valasztott.add(labCash.getLaborLista().get(i));
					}
				}
				i++;
		}
		refreshWithNewDatas();
		repaint();
		refresh();
	}
	
	public void refresh(){
		SwingUtilities.updateComponentTreeUI(lista);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==deselectAll){
			valasztott.removeAll(valasztott);
			Iterator<JCheckBox> it = checkBoxLista.iterator();
			JCheckBox clickObject;
			while (it.hasNext()){
				clickObject = it.next();
				clickObject.setSelected(false);
			}
			try {
				labCash.downloadAlapdij();
			} catch (SQLException e1) {
				BaseWindow.makeWarning("SQL parancsfuttatási hiba!", e1, "error", new JFrame());
			}
			refreshWithNewDatas();
			repaint();
			refresh();
		}
		String cmd = e.getActionCommand();
		if (cmd.equals("reLoad")){
			remove(felsoScroll);
			remove(lista);
			remove(keresoPanel);
			remove(also);
			refresh();
			deleteComponents();
			createWindowContent();
			refresh();
			startTransaction();
		}
	}

	/*
	 * Searchbar filter function
	 * Called by the DocumentListener
	 * */
	public void filter(){
		String szoveg = (kereses.getText()).toLowerCase();
		//System.out.println(szoveg);
		boolean bool = true;
			Iterator<JCheckBox> lab = checkBoxLista.iterator();
			while (lab.hasNext()){
				JCheckBox ch = lab.next();
				if ((ch.getText().toLowerCase().contains(szoveg)) || (ch.getText().toLowerCase().contains(szoveg))){
					bool = true;
				} else {
					bool = false;
				}
				if (szoveg.length()>1){
					ch.setVisible(bool);
				} else {
					ch.setVisible(true);
				}
			}
			/*repaint();
		    refresh();*/
	}

	
	@Override
	public void changedUpdate(DocumentEvent e) {
		filter();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		filter();
		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		filter();
		
	}
	
	public void setFizetendo(int fizetendo) {
		this.fizetendo = fizetendo;
		beallitKasszaVegosszeg(fizetendo);
	}


	public int getFizetendo() {
		return fizetendo;
	}

	public boolean isFirstDownload() {
		return firstDownload;
	}

	public void setFirstDownload(boolean firstDownload) {
		this.firstDownload = firstDownload;
	}

}
