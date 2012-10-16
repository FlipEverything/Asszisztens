package GUI;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import rekord.Csoport;
import rekord.Labor;
import tools.Const;

import layout.SpringUtilities;

import database.DAO;
import database.LabCash;

public class LabCashItem extends BaseWindow{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6534350561469847538L;
	private DAO dao;
	private Labor l;
	private JTextField nev;
	private JTextField nev2;
	private JTextField megj;
	private JSpinner laborAr;
	private JSpinner partnerAr;
	private JSpinner aranyAr;
	private JComboBox alapdij;
	private JComboBox csoport;
	private JComboBox allapot;
	private JTextField ido;

	public LabCashItem(DAO dao){
		super(400, 380, false, true, "Új laborvizsgálat felvitele", 0, 0, JFrame.HIDE_ON_CLOSE, false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Const.PROJECT_PATH+"icon_new.png"));
		this.dao = dao;
		
		l = null;
		add(makePanel(new Labor()));
	}
	
	public LabCashItem(DAO dao, Labor l){
		super(400, 380, false, true, "Laborvizsgálat szerkesztése", 0, 0, JFrame.HIDE_ON_CLOSE, false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Const.PROJECT_PATH+"icon_edit.png"));
		this.dao = dao;
		
		this.l=l;
		add(makePanel(l));
	}

	public JPanel makePanel(Labor l){
		JPanel editPanel = new JPanel();
    	editPanel.setLayout(new SpringLayout());
    	
        //Név1
    	editPanel.add(new JLabel("Elsődleges név:"));
    	nev = new JTextField(l.getNev1());
    	editPanel.add(nev);
    	
    	//Név2
    	editPanel.add(new JLabel("Másodlagos név:"));
    	nev2 = new JTextField(l.getNev2());
    	editPanel.add(nev2);
    	
    	//Megjegyzés
    	editPanel.add(new JLabel("Megjegyzés:"));
    	megj = new JTextField(l.getMegj());
    	editPanel.add(megj);	
    
    	//Elkészítési idő
    	editPanel.add(new JLabel("Vizsgálati idő:"));
    	ido = new JTextField(l.getIdo());
    	editPanel.add(ido);
    	
    	//Labor ár
    	editPanel.add(new JLabel("Klinika ár:"));
    	laborAr = new JSpinner();
    	laborAr.setValue(l.getLaborAr());
    	editPanel.add(laborAr);
    	
    	//Partner ár
    	editPanel.add(new JLabel("CentrumLab ár:"));
    	partnerAr = new JSpinner();
    	partnerAr.setValue(l.getPartnerAr());
    	editPanel.add(partnerAr);
    	
    	//Aranyklinika ár
    	editPanel.add(new JLabel("Aranyklinika ár:"));
    	aranyAr = new JSpinner();
    	aranyAr.setValue(l.getAranyklinikaAr());
    	editPanel.add(aranyAr);
    	
    	//Alapdíj-e
    	editPanel.add(new JLabel("Alapdíjként felszámoljuk-e:"));
    	alapdij = new JComboBox();
    	alapdij.addItem("nem");
    	alapdij.addItem("igen");
    	if (l.getAlapdij()!=null){
	    	if (l.getAlapdij().equals("igen")){
	    		alapdij.setSelectedIndex(1);    		
	    	} else {
	    		alapdij.setSelectedIndex(0);
	    	}
    	}
    	editPanel.add(alapdij);
    	
    	//Kategória
    	editPanel.add(new JLabel("Kategória:"));
    	csoport = new JComboBox();
        csoport.addItem("-- Válasszon! --");
        Iterator<Csoport> cs = dao.getLaborCsoport().iterator();
        while (cs.hasNext()){
        	csoport.addItem(cs.next());
        }	
        editPanel.add(csoport);
        if (l.getCsoport()!=0){
        	csoport.setSelectedIndex(l.getCsoport());		
    	}
    	
    	//Állapot
    	editPanel.add(new JLabel("Állapot (megjelenik-e):"));
        allapot = new JComboBox();
        allapot.addItem("aktiv");
        allapot.addItem("passziv");
        if (l.getAllapot()!=null){
        	if (l.getAllapot().equals("passziv")){
            	allapot.setSelectedIndex(1);    		
        	} else {
        		allapot.setSelectedIndex(0);
        	}
        }
          	
        editPanel.add(allapot);
        
        editPanel.add(new JLabel(""));
    	
    	JButton edit = new JButton("Rögzít");
    	edit.addActionListener(this);
    	if (l.getId()==0){
    		edit.setActionCommand("create");
    		System.out.println("Create");
    	} else {
    		edit.setActionCommand("edit");
    		System.out.println("Edit");
    	}
    	editPanel.add(edit);
    
        //Lay out the panel.
        makeTheGrid(11, 2, editPanel);
        //Set up the content pane.
        editPanel.setOpaque(true);  //content panes must be opaque
    	
		return editPanel;
	}
	
	public void makeTheGrid(int i, int j, JPanel panel){
		SpringUtilities.makeCompactGrid(panel,
                i, j, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand()=="create"){
			int cs1 = 0;
			Iterator<Csoport> it = dao.getLaborCsoport().iterator();
			while (it.hasNext()){
				Csoport cs = it.next();
				if (cs.getNev().equals((csoport.getSelectedItem()))){
					cs1 = cs.getId();
					break;
				}
			}
			
			Labor lab = new Labor(0, nev.getText(), nev2.getText(), megj.getText(), ido.getText(), (Integer)(laborAr.getValue()), (Integer)partnerAr.getValue(), (Integer)aranyAr.getValue(), (String)alapdij.getSelectedItem(), cs1, (String)allapot.getSelectedItem());
			try {
				LabCash.insertItem(dao, lab);
				setVisible(false);
			} catch (SQLException e1) {
				BaseWindow.makeWarning("Nem tudtam a laborvizsgálatot beszúrni!", e1, "error");
			}
			setVisible(false);
		} else if (e.getActionCommand()=="edit"){
			int cs1 = 0;
			Iterator<Csoport> it = dao.getLaborCsoport().iterator();
			while (it.hasNext()){
				Csoport cs = it.next();
				if (cs.getNev().equals((csoport.getSelectedItem()))){
					cs1 = cs.getId();
					break;
				}
			}
			
			l.setNev1(nev.getText());
			l.setNev2(nev2.getText());
			l.setMegj(megj.getText());
			l.setIdo(ido.getText());
			l.setLaborAr((Integer)laborAr.getValue());
			l.setPartnerAr((Integer)partnerAr.getValue());
			l.setAranyklinikaAr((Integer)aranyAr.getValue());
			l.setAlapdij((String)alapdij.getSelectedItem());
			l.setCsoport(cs1);
			l.setAllapot((String)allapot.getSelectedItem());
			try {
				LabCash.editItem(dao, l);
				setVisible(false);
			} catch (SQLException e1) {
				BaseWindow.makeWarning("Nem tudtam a laborvizsgálatot szerkeszteni!", e1, "error");
			}

		}
	}
}
