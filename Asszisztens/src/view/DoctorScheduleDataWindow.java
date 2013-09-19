package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import model.DAO;
import model.DoctorSchedule;


public class DoctorScheduleDataWindow extends BaseWindow implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4996310186663304655L;
	
	private DefaultListModel orvosModel;
	private DefaultListModel szobaModel;
	
	private JList orvosList;
	private JList szobaList;
	
	private JSplitPane torzsadatSplit;

	private DAO dao;
	
	//0: orvos
	//1: szoba
	
	public DoctorScheduleDataWindow(DAO dao){
		super(640, 350, false, false, "Rendelő beosztás: Törzsadatok szerkesztése", 0, 0, JFrame.DISPOSE_ON_CLOSE, false);
		
		this.dao = dao;
		
		orvosModel = new DefaultListModel();
		szobaModel = new DefaultListModel();
		
		orvosList = new JList(orvosModel);
		szobaList = new JList(szobaModel);
		
		
		initList(orvosModel,dao.getOrvosTomb());
		initList(szobaModel,dao.getSzobaTomb());
		
		torzsadatSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, torzsLekerdez(orvosList, "0", "Orvosok"), torzsLekerdez(szobaList, "1", "Rendelők"));
		torzsadatSplit.setOneTouchExpandable(false);
		torzsadatSplit.setDividerLocation(getWidth()/2);
		torzsadatSplit.setPreferredSize(new Dimension(getWidth(), getHeight()));
		
		setLayout(new BorderLayout());
		add(torzsadatSplit,"Center");
		
		pack();
	}
	
	public void initList(DefaultListModel model, ArrayList<?> a){
		int index = 0;
		Iterator<?> it = a.iterator();
		while (it.hasNext()){
			model.add(index++, it.next());
		}
	}
	
	public void removeList(DefaultListModel model){
		model.removeAllElements();
	}
	
	public void refreshList(JList list){
		list.validate();
		list.repaint();
	}
	
	/**
	 * Visszater egy Panellel
	 * @param list A panel egy komponense
	 * @param actionCommand Actionlistenerhez
	 * @param tableName Ez fog megjelenni feliratba
	 * @return 
	 */
	public JPanel torzsLekerdez(JList list, String actionCommand, String tableName){		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,3));
		
		JButton edit = new JButton("Szerkeszt");
		JButton delete = new JButton("Töröl");
		JButton create = new JButton("Új felvitele");
		
		edit.addActionListener(this);
		edit.setActionCommand(actionCommand+"_edit");
		
		delete.addActionListener(this);
		delete.setActionCommand(actionCommand+"_delete");
		create.addActionListener(this);
		create.setActionCommand(actionCommand+"_create");
		
		buttonPanel.add(edit);
		buttonPanel.add(delete);
		buttonPanel.add(create);

		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		
		JScrollPane listScroll = new JScrollPane(list);
		
		listPanel.add(new JLabel("Aktuális lista:"),"North");
		listPanel.add(listScroll,"Center");
		listPanel.add(buttonPanel,"South");
		
		
		TitledBorder title2;
		Border blackline = BorderFactory.createLineBorder(Color.gray);
		title2 = BorderFactory.createTitledBorder(blackline, tableName);
		listPanel.setBorder(title2);
		
		return listPanel;
	}
	
	/*public void reloadTorzsadat(int id){
		DefaultListModel listData = null;
		try {
			listData = dsObject.getTorzsadatLista("rendelo_"+cmd[id]);
		} catch (SQLException e) {
			makeWarning("Hiba az lista lekérdezése közben!", e, "error", this);
		}
		torzsList[id].setModel(listData);
		validate();
		repaint();
	}*/
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		JList list = null;
		String createCmd = null;
		String command = null;
		
		if (cmd.substring(0, 1).equals("0")){
			list=orvosList;
			createCmd  = "Adja meg az orvos nevét!";
			command = "orvos";
		} else if (cmd.substring(0, 1).equals("1")){
			list=szobaList;
			createCmd = "Adja meg a rendelő nevét!";
			command = "szoba";
		}
		
		cmd = cmd.substring(2);
		
		if (cmd.equals("edit")){
			if (list.getSelectedIndex()!=-1){
				String s = (String)JOptionPane.showInputDialog(
	                    (JFrame)this,
	                    createCmd,
	                    "Szerkesztés: "+list.getSelectedValue().toString(),
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    null,
	                    list.getSelectedValue().toString());
				if ((s != null) && (s.length() > 0)) {
					DoctorSchedule.edit(dao, command, list.getSelectedValue().toString(), s);
					refreshLists();
				}
			} else {
				makeWarning("Válasszon egy elemet a listából!", new Exception(), "success", this);
			}
		} else if (cmd.equals("delete")){
			if (list.getSelectedIndex()!=-1){
				Object[] o = {"Igen","Nem"};
				if (ask(o, "Biztosan törlöd?", list.getSelectedValue().toString(), (JFrame)this)==true){
					DoctorSchedule.delete(dao, command, list.getSelectedValue().toString());
					refreshLists();				
				}
			} else {
				makeWarning("Válasszon egy elemet a listából!", new Exception(), "success", this);
			}
		} else if (cmd.equals("create")){
			String s2 = (String)JOptionPane.showInputDialog(
                    (JFrame)this,
                    createCmd,
                    "Új felvitele",
                    JOptionPane.PLAIN_MESSAGE);
			if ((s2 != null) && (s2.length() > 0)) {
				DoctorSchedule.insert(dao, command, s2);
				refreshLists();	
			}
		}

		
	}
	
	public void refreshLists(){
		removeList(orvosModel);
		removeList(szobaModel);
		initList(orvosModel,dao.getOrvosTomb());
		initList(szobaModel,dao.getSzobaTomb());
		refreshList(orvosList);
		refreshList(szobaList);
	}
}
