package GUI;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import rekord.Labor;
import tools.Const;

import database.DAO;

public class LabCashManage extends BaseWindow{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultListModel listModel;
	private DAO dao;
	
	private static final int height = BaseWindow.screenHeight-200;
	private static final int width = 1080;

	public LabCashManage(final DAO dao){
		super(width, height, false, true, "Laborvizsgálatok listája", 0, 0, JFrame.DISPOSE_ON_CLOSE, false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Const.PROJECT_PATH+"icon_edit.png"));
		
		this.dao = dao;
		
		
		/** Uzenetlista letrehozasa */
		listModel = new DefaultListModel();
		
		makeList();
		
		final JList list = new JList(listModel);
		MouseListener mouseListener = new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() == 2) {
		            int index = list.locationToIndex(e.getPoint());
		            if (index>0){
		            	new LabCashItem(dao, (Labor)list.getModel().getElementAt(index));
		            }
		        }
		    }
		};
		
		list.addMouseListener(mouseListener);
		JScrollPane scroll = new JScrollPane(list);
		add(scroll);

	}
	
	public void refreshGUI(){
		makeList();
	}
	
	public void makeList(){
		listModel.clear();
		listModel.addElement(
				"<html>" +
					"<table><tr style='background-color: black; color: white;'>" +
						"<td style='width:300px;'><b>Név1</b></td>" +
						"<td style='width:150px;'><b>Név2</b></td>" +
						"<td style='width:50px;'><b>Idő</b></td>" +
						"<td style='width:80px;'><b>KlinikaÁr</b></td>" +
						"<td style='width:80px;'><b>CentrumLabÁr</b></td>" +
						"<td style='width:80px;'><b>AranyÁr</b></td>" +
						"<td style='width:60px;'><b>Állapot</b></td>" +
					"</tr></table>" +
				"</html>");
		Iterator<Labor> it = dao.getLabor().iterator();
		
		while (it.hasNext()){
			Labor l = it.next();			
			listModel.addElement(l);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
	}
}
