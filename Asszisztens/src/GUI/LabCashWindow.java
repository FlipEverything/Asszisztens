package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import database.DAO;
import rekord.Csoport;
import rekord.Labor;
import tools.Const;


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
	private int searchHeight = 30;
	private int componentWidth; //offset
	
	//Database connection
	private int fizetendo;
	private DAO dao;
	private JButton category;
	private JButton newItem;
	private JButton manageItem;
	
	public LabCashWindow(DAO dao){
		super(preferredWidth, preferredHeight, resizable, visible, title, locationX, locationY, defaultCloseOperation, exit);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Const.PROJECT_PATH+"icon_lab.png"));
		height = getHeight();
		width = getWidth();
		
		this.dao = dao;
		this.fizetendo = 0;	
		
		createWindowContent();
		
	}
	
	public void createWindowContent(){
		//Create main objects
		initComponents();
		
		componentWidth = (int)(width * 0.25);	
		deselectAll.addActionListener(this);
		deselectAll.setFocusable(false);
		deselectAll.setEnabled(false);
		
		category.addActionListener(this);
		category.setFocusable(false);
		
		newItem.addActionListener(this);
		newItem.setFocusable(false);
		
		manageItem.addActionListener(this);
		manageItem.setFocusable(false);
		
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
		gombok.add(category);
		gombok.add(newItem);
		gombok.add(manageItem);
			
		kereses.setFont(new Font("SansSerif", Font.PLAIN, 20));
		kereses.setPreferredSize(new Dimension(width, searchHeight));
		kereses.getDocument().addDocumentListener(this);
		
		keresoPanel.setLayout(new BorderLayout());
		keresoPanel.add(gombok, "North");
		keresoPanel.add(kereses, "South");
				
		kasszaVegosszeg.setSize(width, height);
		also.add(kasszaVegosszeg,"East");
			
		setLayout(new BorderLayout());
		add(felsoScroll,"Center");
		add(lista,"East");
		add(keresoPanel,"North");
		add(also,"South");
			
		refresh();
	}
	
	public void initComponents(){
		deselectAll = new JButton("Összes kijelölés megszüntetése", new ImageIcon(Const.PROJECT_PATH+"icon_cancel.png"));
		category = new JButton("Kategóriák kezelése", new ImageIcon(Const.PROJECT_PATH+"icon_category.png"));
		newItem = new JButton("Új laborvizsgálat", new ImageIcon(Const.PROJECT_PATH+"icon_new.png"));
		manageItem = new JButton("Laborvizsgálatok kezelése", new ImageIcon(Const.PROJECT_PATH+"icon_edit.png"));
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
	}
	
	public int getAlapdij(){
		int alapdij = 0;
		
	    Iterator<Labor> itAlapdij = dao.getLabor().iterator();
	    while ( itAlapdij.hasNext() ){
	    	Labor j = itAlapdij.next();
	    	String nev = j.getNev1();
	    	if (nev.length()>35){
	    		nev = nev.substring(0,35)+"...";
	    	}
	    	if (j.getAlapdij().equals("igen")){
	    		lista.add(new JLabel("<html><b>"+nev+"</b> ("+j.getAranyklinikaAr()+" HUF)</html>"));
	    		alapdij += j.getAranyklinikaAr();
	    	}
	    }
		
		return alapdij;
	}
	
	public void init(){
		setFizetendo(getAlapdij());
		refreshGUI();
	}
	
	public void refreshGUI(){
			lista.removeAll();
			
			getAlapdij();
			
			Iterator<Labor> rekordIt = dao.getLabor().iterator();
			Iterator<Csoport> csoportIt = dao.getLaborCsoport().iterator();
			int elozo = 0;
			while ( rekordIt.hasNext() ){
		    	Labor j = rekordIt.next();
		    	if (elozo!=j.getCsoport()){
		    		Csoport cs = csoportIt.next();
		    		String labelText = "<html><table><tr style='background-color: #5AAD41; color: white;'><td style='width: "+(felsoScroll.getPreferredSize().width-componentWidth)+"px;'><b>"+cs.getNev()+"</b></td></tr></table></html>";
		    		felso.add(new JLabel(labelText));				    			
		    	}
		    	if (j.getAllapot().equals("aktiv")){
		    		rekordPaint(j);
		    	}
		    	elozo = j.getCsoport();
			}
		    
		    refresh();
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
	
	public void valasztottak(){
		int i = 0;
		 Iterator<Labor> it = valasztott.iterator(); 
		    while ( it.hasNext() ){
		    	Labor j = it.next();
		    	String nev = j.getNev1();
		    	if (nev.length()>35){
		    		nev = nev.substring(0,35)+"...";
		    	}
		    	lista.add(new JLabel("<html><b>"+nev+"</b> ("+j.getAranyklinikaAr()+" HUF)</html>"));
		    	i++;
		    }
		    
		    if (i>0) deselectAll.setEnabled(true); else deselectAll.setEnabled(false); 
	}
	
	public void beallitKasszaVegosszeg(int osszeg){
		kasszaVegosszeg.setText("<html><div style='text-align: right;'><span style='font-size: 20px; font-weight: bold;'>Fizetendő:</span> <span style='font-size: 30px; font-weight: bold;'>"+osszeg+"&nbsp;HUF&nbsp;</span></div></html>");
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		Iterator<JCheckBox> it = checkBoxLista.iterator();
		JCheckBox clickObject;
		int i = 0;
		while (it.hasNext()){
				clickObject = it.next();
				if (clickObject==arg0.getSource()){
					if (clickObject.getSelectedObjects()==null){
						setFizetendo(getFizetendo()-dao.getLabor().get(i).getAranyklinikaAr());
						valasztott.remove(dao.getLabor().get(i));
					}else{
						setFizetendo(getFizetendo()+dao.getLabor().get(i).getAranyklinikaAr());
						valasztott.add(dao.getLabor().get(i));
					}
				}
				i++;
		}
		lista.removeAll();
		getAlapdij();
		valasztottak();
		refresh();
	}
	
	public void refresh(){
		repaint();
		SwingUtilities.updateComponentTreeUI(lista);
		SwingUtilities.updateComponentTreeUI(this);
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
			refresh();
		} else if (e.getSource()==category){
			new LabCashCategory(dao);
		} else if (e.getSource()==manageItem){
			new LabCashManage(dao);
		} else if (e.getSource()==newItem){
			new LabCashItem(dao);
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


}
