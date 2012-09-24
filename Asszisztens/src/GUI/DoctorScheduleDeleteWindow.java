package GUI;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import rekord.RendeloIdopont;
import rekord.RendeloOrvos;

import database.DoctorScheduleDatabase;

public class DoctorScheduleDeleteWindow extends BaseWindow{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1122778302552565055L;
	
	private int topSize = 30;
	private JComboBox orvosLista;

	private DoctorScheduleDatabase dsObject;

	private JList list;
	private JPanel buttonBar;

	private JButton get;
	
	//private DoctorScheduleDatabase dsObject;
	
	public DoctorScheduleDeleteWindow(DoctorScheduleDatabase dsObject){
		super(500, 450, false, false, "Rendelő beosztás: Időpont törlése (lemondás)", 0, 0, JFrame.DISPOSE_ON_CLOSE, false);
		this.dsObject = dsObject;
		
		setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel(new FlowLayout());
		
		DefaultListModel model = new DefaultListModel();
		
		list = new JList(model);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		
		JScrollPane bottom = new JScrollPane(list);
		
		topPanel.setPreferredSize(new Dimension(getWidth(),topSize));
		bottom.setPreferredSize(new Dimension(getWidth(),getHeight()-topSize));
		
		orvosLista = new JComboBox();
		initComboBoxWithDoctors(orvosLista);
		
		JLabel l = new JLabel("Válasszon orvost: ", JLabel.TRAILING);
        topPanel.add(l);
        l.setLabelFor(orvosLista);
        topPanel.add(orvosLista);
        
        get = new JButton("Lekérdezés");
        get.addActionListener(this);
        get.setActionCommand("get");
        
        topPanel.add(get);
        
        buttonBar = new JPanel(new FlowLayout());
        buttonBar.setPreferredSize(new Dimension(getWidth(),topSize));
		buttonBar.setVisible(false);
		
        JButton delete = new JButton("Kiválasztott időpontok törlése");
        delete.addActionListener(this);
        delete.setActionCommand("delete");
        
        JButton back = new JButton("Vissza");
        back.addActionListener(this);
        back.setActionCommand("back");
        
        buttonBar.add(delete);
        buttonBar.add(back);
        
		add(topPanel,"North");
		add(bottom,"Center");
		add(buttonBar,"South");
	}
	
	private void initComboBoxWithDoctors(JComboBox c){
		int index = 0;
		c.insertItemAt("---Válasszon orvost!---", index++);
		c.setSelectedIndex(0);
		Iterator<RendeloOrvos> it = dsObject.getOrvosTomb().iterator();
		while (it.hasNext()){
			c.insertItemAt(it.next(), index++);
		}
	}
	
	public void addDoctorData(){
		RendeloOrvos orvos = (RendeloOrvos) orvosLista.getModel().getSelectedItem();
		Iterator<RendeloIdopont> it = dsObject.getIdopontTomb().iterator();
		
		DefaultListModel dlm = (DefaultListModel) list.getModel();
		int index=0;
		while (it.hasNext()){
			RendeloIdopont ido = it.next(); 
			if (ido.getOrvosId()==orvos.getId()){
				dlm.add(index++, ido);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String cmd = arg0.getActionCommand();
		if (cmd.equals("get")){
			
			if (orvosLista.getSelectedIndex()==0){
				buttonBar.setVisible(false);
				DefaultListModel dlm =  (DefaultListModel) list.getModel();

			    for (int i = 0; i< dlm.getSize(); i++) {
			    	dlm.removeElementAt(i);
			    } 
				BaseWindow.makeWarning("Nem választott orvost!", new Exception("Not found"), "error", (JFrame)this);
			} else {
				orvosLista.setEnabled(false);
				get.setEnabled(false);			    
				addDoctorData();
				buttonBar.setVisible(true);
			}
		} else if (cmd.equals("back")){
			orvosLista.setEnabled(true);
			get.setEnabled(true);	
			buttonBar.setVisible(false);
			DefaultListModel dlm =  (DefaultListModel) list.getModel();
		    dlm.clear();
		}
	}

}
