package database;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import GUI.BaseWindow;


public class DatabaseModify implements ActionListener{
	DBConnect mysql;
	JTextField text;
	BaseWindow newCat;
	BaseWindow editRec;
	BaseWindow editSingleRecord;
	BaseWindow deleteRec;
	JComboBox editLista;
	JComboBox elemek;
	DefaultListModel listModel;
	ArrayList<Integer> id;
	ArrayList<Integer> deleteId;
	JTextField[] arr;
	String[] arrDesc = {"Név1", "Név2","Megjegyzés","Idő", "Labor ár", "Partner ár", "Aranyklinika ár"};
	String[] arrKey = {"nev1", "nev2","megj","ido", "labor_ar", "partner_ar", "aranyklinika_ar"};
	JComboBox csoport;
	JComboBox alapdij, allapot;
	int actEdit;
	private static final int height = BaseWindow.screenHeight-200;
	private static final int width = 1080;
	
	public DatabaseModify(DBConnect mysql){
		this.mysql = mysql;
	}
	
	public void newCategory(){
		newCat = new BaseWindow(400, 90, false, true, "Új kategória felvitele", 0, 0, JFrame.HIDE_ON_CLOSE, false){
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -5146903044568914330L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		
		text = new JTextField();
		text.addKeyListener(
				new KeyAdapter(){
					public void keyReleased( KeyEvent e ) {
						if( e.getKeyCode() == KeyEvent.VK_ENTER )
						{
							try {
								makeNewCat();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
			);
		JButton go = new JButton("Új kategória felvitele");
		JPanel panel = new JPanel();
		
		text.setPreferredSize(new Dimension(300,20));
		go.addActionListener(this);
		go.setActionCommand("newCategory");
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(text);
		panel.add(go);
		
		newCat.add(panel);
		newCat.pack();
	}
	
	public void editCategory(){
		//TODO
	}
	
	public void deleteCategory(){
		//TODO
	}
	
	public void newRecord(){
		openEditWindow(-1);
	}
	
	public void editRecord() throws SQLException{
		id = new ArrayList<Integer>();
		editRec = new BaseWindow(width, height, false, true, "Laborvizsgálatok listája", 0, 0, JFrame.HIDE_ON_CLOSE, false){
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -5146903044568914330L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		/** Uzenetlista letrehozasa */
		listModel = new DefaultListModel();
		downloadList();
		final JList list = new JList(listModel);
		MouseListener mouseListener = new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() == 2) {
		            int index = list.locationToIndex(e.getPoint());
		            if (index>0){
		            	openEditWindow(index);
		            }
		        }
		    }
		};
		
		
		list.addMouseListener(mouseListener);
		JScrollPane scroll = new JScrollPane(list);
		editRec.add(scroll);
	}
	

	public void downloadList() throws SQLException{
		listModel.clear();
		listModel.addElement(
				"<html>" +
					"<table><tr style='background-color: black; color: white;'>" +
						"<td style='width:300px;'><b>Név1</b></td>" +
						"<td style='width:150px;'><b>Név2</b></td>" +
						"<td style='width:50px;'><b>Idő</b></td>" +
						"<td style='width:80px;'><b>Laborár</b></td>" +
						"<td style='width:80px;'><b>PartnerÁr</b></td>" +
						"<td style='width:80px;'><b>AranyÁr</b></td>" +
						"<td style='width:60px;'><b>Állapot</b></td>" +
					"</tr></table>" +
				"</html>");
		mysql.exec("SELECT * FROM labor ORDER BY nev1;");
		int i = 0;
		id.removeAll(id);
		while (mysql.getResult().next() == true){
			String bgcolor;
			if (i%2==0){
				bgcolor = "";
			} else {
				bgcolor = "#CECECE";
			}
			String allapotS = mysql.getResult().getString("allapot");
			String felkover = "", felkoverEnd = "";
			if (allapotS.equals("aktiv")){
				felkover = "<b>";
				felkoverEnd = "</b>";
			}
			listModel.addElement(
					"<html>" +
						"<table><tr style='background-color: "+bgcolor+";'>" +
							"<td style='width:300px;'>"+felkover+mysql.getResult().getString("nev1")+felkoverEnd+"</td>" +
							"<td style='width:150px;'>"+mysql.getResult().getString("nev2")+"</td>" +
							"<td style='width:50px;'>"+mysql.getResult().getString("ido")+"</td>" +
							"<td style='width:80px;'>"+mysql.getResult().getString("labor_ar")+"</td>" +
							"<td style='width:80px;'>"+mysql.getResult().getString("partner_ar")+"</td>" +
							"<td style='width:80px;'>"+mysql.getResult().getString("aranyklinika_ar")+"</td>" +
							"<td style='width:60px;'>"+allapotS+"</td>" +
						"</tr></table>" +
					"</html>");
			id.add(Integer.parseInt(mysql.getResult().getString("id")));
			i++;
		}
	}
	
	public void openEditWindow(int index){
		try {
	    	int ind = 0, actCsop = 0, allapotind = 0;
	    	String str ="", actCommand="", title="";
	    	arr = new JTextField[7];
	    	
			if (index==(-1)){
				actEdit = -1;
				for (int i=0; i<arr.length; i++){
					arr[i] = new JTextField();
				}
				ind = 1;
				allapotind = 0;
				actCsop = 0;
				str = "Létrehoz";
				actCommand = "new";
				title = "Új laborvizsgálat felvitele";
			} else {
				mysql.exec("SELECT * FROM labor WHERE id='"+id.get(index-1)+"'");
				if (mysql.getResult().next() == true ){
					actEdit = id.get(index-1);
					arr[0] = new JTextField(mysql.getResult().getString("nev1"));
			       	arr[1] = new JTextField(mysql.getResult().getString("nev2"));
			       	arr[2] = new JTextField(mysql.getResult().getString("megj"));
			       	arr[3] = new JTextField(mysql.getResult().getString("ido"));
			       	arr[4] = new JTextField(mysql.getResult().getString("labor_ar"));
			       	arr[5] = new JTextField(mysql.getResult().getString("partner_ar"));
			       	arr[6] = new JTextField(mysql.getResult().getString("aranyklinika_ar"));
			       	if (mysql.getResult().getString("alapdij").equals("igen")){
			       		ind = 0;
			       	} else {
			       		ind = 1;
			       	}
			       	if (mysql.getResult().getString("allapot").equals("aktiv")){
			       		allapotind = 0;
			       	} else {
			       		allapotind = 1;
			       	}
			       	mysql.exec("SELECT csoport.nev as csoportNev, labor.csoport as actualCsop FROM csoport, labor WHERE labor.csoport = csoport.id AND labor.id = '"+id.get(index-1)+"';");
		        	while (mysql.getResult().next()==true){
		        		actCsop = Integer.parseInt(mysql.getResult().getString("actualCsop"));
		        	}
		        	str = "Szerkesztés";
		        	actCommand = "edit";
		        	title = "Laborvizsgálatok szerkesztése";
				} else {
					BaseWindow.makeWarning("Nincs ilyen elem!", new Exception(), "success", new JFrame());
				}
			}
			
			editSingleRecord = new BaseWindow(400, 300, false, true, title, 0, 0, JFrame.HIDE_ON_CLOSE, false){
				
				/**
				 * 
				 */
				private static final long serialVersionUID = -5146903044568914330L;

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
				}
			};
			JPanel singleEditPanel = new JPanel();
	    	singleEditPanel.setLayout(new GridLayout(11,2));
	    	String[] arrDesc = {"Név1", "Név2","Megjegyzés","Idő", "Labor ár", "Partner ár", "Aranyklinika ár"};
	    	for (int i=0; i<arr.length; i++){
	    		singleEditPanel.add(new JLabel(arrDesc[i]));
	    		singleEditPanel.add(arr[i]);
	    	}
	    	alapdij = new JComboBox();
	    	alapdij.addItem("igen");
	    	alapdij.addItem("nem");
	        alapdij.setSelectedIndex(ind);
	        singleEditPanel.add(new JLabel("Alapdíj-e?"));
	        singleEditPanel.add(alapdij);
	        allapot = new JComboBox();
	        allapot.addItem("aktiv");
	        allapot.addItem("passziv");
	        allapot.setSelectedIndex(allapotind);
	        singleEditPanel.add(new JLabel("Állapot"));
	        singleEditPanel.add(allapot);
	        csoport = new JComboBox();
	        mysql.exec("SELECT * FROM csoport;");
	        csoport.addItem("------NINCS!-----");
	        while (mysql.getResult().next()==true){
	        	csoport.addItem(mysql.getResult().getString("nev"));
	        }	
        	csoport.setSelectedIndex(actCsop);
        	singleEditPanel.add(new JLabel("Csoport"));
        	singleEditPanel.add(csoport);
        	singleEditPanel.add(new JLabel(""));
        	JButton edit = new JButton(str);
        	edit.addActionListener(this);
        	edit.setActionCommand(actCommand);
        	singleEditPanel.add(edit);
            editSingleRecord.add(singleEditPanel);
			
		} catch (SQLException e1) {
			BaseWindow.makeWarning("SQL parancsfuttatási hiba!", e1, "error", new JFrame());
		}
	}
	
	public void refresh(){
		
	}
	
	public void makeNewCat() throws SQLException{
		Object[] o = {"Igen", "Nem"};
		boolean ask = BaseWindow.ask(o, "Megerősítés", "Ezt a kategóriát akarja felvinni: "+text.getText()+"\r\nBiztos benne?", new JFrame());
		if (ask==true){
			String sql = "INSERT INTO csoport SET nev='"+text.getText()+"';";
			mysql.exec(sql);
			newCat.setVisible(false);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		
		if (cmd.equals("newCategory")){
			try {
				makeNewCat();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("edit")){
			String sql = "UPDATE labor SET ";
			for (int i=0; i<arr.length; i++){
				sql+=arrKey[i]+"='"+arr[i].getText()+"', ";
			}
			sql+="alapdij='"+alapdij.getSelectedItem()+"', allapot='"+allapot.getSelectedItem()+"', csoport='"+csoport.getSelectedIndex()+"' WHERE id='"+actEdit+"';";
			try {
				mysql.exec(sql);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			editSingleRecord.setVisible(false);
			try {
				downloadList();
			} catch (SQLException e) {
				BaseWindow.makeWarning("SQL parancsfuttatási hiba!", e, "error", new JFrame());
			}
			refresh();
		} else if (cmd.equals("new")){
			String sql = "INSERT INTO labor SET ";
			for (int i=0; i<arr.length; i++){
				sql+=arrKey[i]+"='"+arr[i].getText()+"', ";
			}
			sql+="alapdij='"+alapdij.getSelectedItem()+"', csoport='"+csoport.getSelectedIndex()+"';";
			try {
				mysql.exec(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			editSingleRecord.setVisible(false);
			refresh();
		}
		
	}
	
}
