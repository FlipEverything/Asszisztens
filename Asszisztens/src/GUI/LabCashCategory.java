package GUI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import rekord.Csoport;
import tools.Const;

import database.DAO;
import database.LabCash;

public class LabCashCategory extends BaseWindow{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4746456272420146223L;
	private DAO dao;
	private DefaultListModel model;
	private JList list;

	public LabCashCategory(DAO dao){
		super(400, 400, false, false, "Laborcsopotok", 0, 0, JFrame.DISPOSE_ON_CLOSE, false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Const.ICON_PATH+"icon_category.png"));
		
		this.dao = dao;
		
		model = new DefaultListModel();		
		list = new JList(model);
		
		JScrollPane pane = new JScrollPane(list);
			
		paintList();
		
		setLayout(new BorderLayout());
		add(pane,"Center");
		add(buttonPanel(),"South");
		
		pack();
		
		setVisible(true);
	}
	
	private JPanel buttonPanel(){
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,3));
		
		JButton edit = new JButton("Szerkeszt");
		JButton delete = new JButton("Töröl");
		JButton create = new JButton("Új felvitele");
		
		edit.addActionListener(this);
		edit.setActionCommand("edit");
		
		delete.addActionListener(this);
		delete.setActionCommand("delete");
		create.addActionListener(this);
		create.setActionCommand("create");
		
		buttonPanel.add(edit);
		buttonPanel.add(delete);
		buttonPanel.add(create);

		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		
		return buttonPanel;
	}
	
	
	private void paintList() {
		model.removeAllElements();
		int index = 0;
		Iterator<Csoport> it = dao.getLaborCsoport().iterator();
		while (it.hasNext()){
			model.add(index++, it.next());
		}
		list.validate();
		list.repaint();
		repaint();
		SwingUtilities.updateComponentTreeUI(list);
		SwingUtilities.updateComponentTreeUI(this);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		String createCmd  = "Adja meg a kategória nevét!";
		
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
					Csoport cs = (Csoport)list.getSelectedValue();
					cs.setNev(s);
					LabCash.editCategory(dao, cs);
					paintList();
				}
			} else {
				makeWarning("Válasszon egy elemet a listából!", new Exception(), "success", this);
			}
		} else if (cmd.equals("delete")){
			if (list.getSelectedIndex()!=-1){
				Object[] o = {"Igen","Nem"};
				if (ask(o, "Biztosan törlöd?", list.getSelectedValue().toString(), (JFrame)this)==true){
					LabCash.deleteCategory(dao, (Csoport)list.getSelectedValue());
					paintList();			
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
				Csoport cs = new Csoport(0, s2);
				LabCash.insertCategory(dao, cs);
				paintList();	
			}
		}

		
	}
}
