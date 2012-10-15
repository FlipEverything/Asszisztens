package GUI;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
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

public class LabCashItem extends BaseWindow{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6534350561469847538L;
	private DAO dao;

	public LabCashItem(DAO dao){
		super(400, 380, false, true, "Új laborvizsgálat felvitele", 0, 0, JFrame.HIDE_ON_CLOSE, false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Const.PROJECT_PATH+"icon_new.png"));
		this.dao = dao;
		
		add(makePanel(new Labor()));
	}
	
	public LabCashItem(DAO dao, Labor l){
		super(400, 380, false, true, "Laborvizsgálat szerkesztése", 0, 0, JFrame.HIDE_ON_CLOSE, false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Const.PROJECT_PATH+"icon_edit.png"));
		this.dao = dao;
		
		add(makePanel(l));
	}

	public JPanel makePanel(Labor l){
		JPanel editPanel = new JPanel();
    	editPanel.setLayout(new SpringLayout());
    	
        //Név1
    	editPanel.add(new JLabel("Elsődleges név:"));
    	editPanel.add(new JTextField(l.getNev1()));
    	
    	//Név2
    	editPanel.add(new JLabel("Másodlagos név:"));
    	editPanel.add(new JTextField(l.getNev2()));
    	
    	//Megjegyzés
    	editPanel.add(new JLabel("Megjegyzés:"));
    	editPanel.add(new JTextField(l.getMegj()));	
    
    	//Elkészítési idő
    	editPanel.add(new JLabel("Vizsgálati idő:"));
    	editPanel.add(new JTextField(l.getId()));
    	
    	//Labor ár
    	editPanel.add(new JLabel("Labor ár:"));
    	JSpinner laborAr = new JSpinner();
    	laborAr.setValue(l.getLaborAr());
    	editPanel.add(laborAr);
    	
    	//Partner ár
    	editPanel.add(new JLabel("Partner ár:"));
    	JSpinner partnerAr = new JSpinner();
    	partnerAr.setValue(l.getPartnerAr());
    	editPanel.add(partnerAr);
    	
    	//Aranyklinika ár
    	editPanel.add(new JLabel("Aranyklinika ár:"));
    	JSpinner aranyAr = new JSpinner();
    	aranyAr.setValue(l.getAranyklinikaAr());
    	editPanel.add(aranyAr);
    	
    	//Alapdíj-e
    	editPanel.add(new JLabel("Alapdíjként felszámoljuk-e:"));
    	JComboBox alapdij = new JComboBox();
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
    	JComboBox csoport = new JComboBox();
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
        JComboBox allapot = new JComboBox();
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
    	//edit.setActionCommand(actCommand);
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
		// TODO Auto-generated method stub
		
	}
}
