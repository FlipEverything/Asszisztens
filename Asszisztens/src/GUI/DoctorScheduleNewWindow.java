package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import rekord.RendeloOrvos;
import rekord.RendeloSzoba;
import tools.Const;

import layout.SpringUtilities;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

import database.DoctorScheduleDatabase;

public class DoctorScheduleNewWindow extends BaseWindow implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7902097712650562907L;

	private ButtonGroup valasztottGomb;
	private ButtonGroup valasztottSzoba;
	
	private JPanel gombPanel;
	
	private String[] labelText = {"Választott orvos:","Kezdet:","Befejezés:","Választott nap:","Választott dátum:"};
	private JLabel[] labels;
	private JComboBox[] datas;
	private static int numPairs = 5;
	private static int resultHeight = 200;
	private static int height = numPairs*20+180+resultHeight;
	
	
	
	private MyRadioButton ismetlodo;
	private MyRadioButton alkalmi;
	private JFormattedTextField date;
	
	private JPanel szobaResult;

	private JButton resultAccept;
	private JButton resultCancel;
	private JButton send;
	
	private String command = "Töltse ki az összes kötelező mezőt, majd nyomja meg a 'Keresés' gombot!";
	private String command2 = "Válasszon rendelőt:";
	
	private DateFormat format;
	
	DoctorScheduleDatabase dsObject;

	private Date tol;

	private Date ig;
	
	public DoctorScheduleNewWindow(DoctorScheduleDatabase dsObject){
		super(420, height, false, false, "Rendelő beosztás: Új időpont felvitele", 0, 0, JFrame.DISPOSE_ON_CLOSE, false);
		
		this.dsObject = dsObject;
		
		setLayout(new BorderLayout());
		
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		MaskFormatter mf1 = null;
		try {
			mf1 = new MaskFormatter("####-##-##");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    mf1.setPlaceholderCharacter('_');
	    
		date = new JFormattedTextField(mf1);
		
		JPanel northPanel = new JPanel(new GridLayout(2,1));
		JPanel centerPanel = new JPanel(new SpringLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout());
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		
		szobaResult = new JPanel(new BorderLayout());
		TitledBorder title;
		Border blackline = BorderFactory.createLineBorder(Color.gray);
		title = BorderFactory.createTitledBorder(blackline, "Keresés eredménye:");
		szobaResult.setBorder(title);
		JPanel szobaResultButton = new JPanel(new FlowLayout());
		
		resultAccept = new JButton("Elfogad");
		resultCancel = new JButton("Mégsem");
		
		resultAccept.setVisible(false);
		resultCancel.setVisible(false);
		
		resultAccept.addActionListener(this);
		resultCancel.addActionListener(this);
		
		resultAccept.setActionCommand("resultAccept");
		resultCancel.setActionCommand("resultCancel");
		
		szobaResultButton.add(resultAccept);
		szobaResultButton.add(resultCancel);
		
		
		szobaResult.add(new JLabel(command),"North");
		szobaResult.add(szobaResultButton,"South");
		
		JScrollPane szobaPanel = new JScrollPane(szobaResult);
		szobaPanel.setPreferredSize(new Dimension(getWidth(), resultHeight));
		
		JPanel radioButtons = new JPanel(new FlowLayout());
		
		ismetlodo = new MyRadioButton("Új ismétlődő alkalom","ismetlodo",true);
		alkalmi = new MyRadioButton("Új egyszeri alkalom","alkalmi");
		
		valasztottSzoba = new ButtonGroup();
		
		valasztottGomb = new ButtonGroup();
		valasztottGomb.add(ismetlodo);
		valasztottGomb.add(alkalmi);
		
		radioButtons.add(ismetlodo);
		radioButtons.add(alkalmi);
		Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(1);
		northPanel.add(new JLabel("<html><body><div style='font-size: 13px; text-align: center; width: "+getWidth()+";'>Mai dátum: "+getDateTime()+" ("+Const.getDayOfTheWeek(cal.get(Calendar.DAY_OF_WEEK)).toLowerCase()+")</div></body></html>#\n\n"));
		northPanel.add(radioButtons);
		
				
		send = new JButton("Szabad rendelő keresése");
		send.addActionListener(this);
		send.setActionCommand("send");
		
		buttonPanel.add(send);
		      
        datas = new JComboBox[numPairs];
        labels = new JLabel[labelText.length];
        
        for (int i = 0; i < labels.length; i++) {
			labels[i] = new JLabel(labelText[i], JLabel.TRAILING);
        }
        
		for (int i = 0; i < numPairs-1; i++) {
            centerPanel.add(labels[i]);
            datas[i] = new JComboBox();
            labels[i].setLabelFor(datas[i]);
            centerPanel.add(datas[i]);
        }
		
		labels[labels.length-1].setLabelFor(date);
		date.setEnabled(false);
		centerPanel.add(labels[numPairs-1]);
		centerPanel.add(date);
		
        //Lay out the panel.
        makeTheGrid(numPairs, 2, centerPanel);
        //Set up the content pane.
        centerPanel.setOpaque(true);  //content panes must be opaque
        
        
        initComboBoxWithDoctors(datas[0]);
        initComboBoxWithDays(datas[3]);
        initComboBoxWithTime(datas[1]);
        initComboBoxWithTime(datas[2]);
        

        
		contentPanel.add(northPanel,"North");
		contentPanel.add(centerPanel,"Center");
		contentPanel.add(buttonPanel,"South");
		
		add(contentPanel,"North");
		add(szobaPanel,"South");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
			boolean process = true;
			String cmd = e.getActionCommand();
			
			
			String tolString;
			String igString;
			if (cmd=="resultCancel"){
				enableForm();				
			} else if (cmd=="resultAccept"){
				if (valasztottSzoba.getSelection()!=null){
					int szobaId = 0;
					String alkalom = null;
					Enumeration<AbstractButton> allRadioButton=valasztottSzoba.getElements();  
					   
					while(allRadioButton.hasMoreElements()) {  
					   JRadioButton temp=(JRadioButton)allRadioButton.nextElement();  
					   if(temp.isSelected()){  
						   szobaId = (Integer) temp.getClientProperty("id"); 
					   }
					}  
					RendeloOrvos o = (RendeloOrvos)datas[0].getSelectedItem();
					
					Enumeration<AbstractButton> allRadioButton2=valasztottGomb.getElements();  
					   
					while(allRadioButton2.hasMoreElements()) {  
					   MyRadioButton temp=(MyRadioButton)allRadioButton2.nextElement();  
					   if(temp.isSelected()){  
						   alkalom = temp.getCode(); 
					   }
					}  
					
					try {
						dsObject.addNewSchedule(tol, ig, szobaId, o.getId(), alkalom);
					} catch (NumberFormatException e1) {
						BaseWindow.makeWarning("Nem sikerült létrehozni az időpontot!", e1, "error", (JFrame)this);
					}
					resetForm();
					enableForm();
				} else {
					BaseWindow.makeWarning("Nem választott szobát!", new Exception(), "error", this);
				}
			} else {
				for (int i=0; i<numPairs-2; i++){
					if (datas[i].getSelectedIndex()==0){
						process = false;
						labels[i].setForeground(Color.RED);
					} else {
						labels[i].setForeground(Color.BLACK);
					}
				}
				
				if(alkalmi.getModel()==valasztottGomb.getSelection()){
					if (!isValidDateStr(date.getText())){
						date.setBackground(Color.RED);
						labels[numPairs-1].setForeground(Color.RED);
						process = false;
					} else {
						date.setBackground(Color.WHITE);
						labels[numPairs-1].setForeground(Color.BLACK);
					}
					labels[numPairs-2].setForeground(Color.BLACK);
				} else if (ismetlodo.getModel()==valasztottGomb.getSelection()){
					labels[numPairs-1].setForeground(Color.BLACK);
					date.setBackground(Color.WHITE);
					if (datas[numPairs-2].getSelectedIndex()==0){
						process = false;
						labels[numPairs-2].setForeground(Color.RED);
					} else {
						labels[numPairs-2].setForeground(Color.BLACK);
					}
				}
				

				if (process){
					DateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
					Date kezdetIdo = null, vegIdo = null;
					try {
						kezdetIdo = formatter2 .parse(datas[1].getSelectedItem().toString());
						vegIdo = formatter2.parse(datas[2].getSelectedItem().toString());
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					if (kezdetIdo.compareTo(vegIdo)>0){
						datas[2].setForeground(Color.RED);
						process = false;
					} else {
						datas[2].setForeground(Color.BLACK);
					}
				}
				
				if (process==false){
					JLabel l = (JLabel)szobaResult.getComponent(0);
					l.setText(command);
					szobaResult.validate();
					szobaResult.repaint();
					
				} else if (process==true){
					disableForm();
					
					ArrayList<RendeloSzoba> result = null;
					try {
									
						RendeloOrvos o = (RendeloOrvos)datas[0].getSelectedItem();
						if(alkalmi.getModel()==valasztottGomb.getSelection()){
							tolString = date.getText()+" "+datas[1].getSelectedItem().toString();
							igString = date.getText()+" "+datas[2].getSelectedItem().toString();
							tol = format.parse(tolString);
							ig = format.parse(igString);
							result = dsObject.searchForFreeRooms(tol, ig, "alkalmi", o.getId());
						} else if (ismetlodo.getModel()==valasztottGomb.getSelection()){
							Calendar c = Calendar.getInstance();
							c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
							c.add(Calendar.DATE, datas[3].getSelectedIndex()-1);
							
							tolString = df.format(c.getTime())+" "+datas[1].getSelectedItem().toString();
							igString = df.format(c.getTime())+" "+datas[2].getSelectedItem().toString();
							tol = format.parse(tolString);
							ig = format.parse(igString);
							result = dsObject.searchForFreeRooms(tol, ig, "ismetlodo", o.getId());
						}
					} catch (ParseException e2) {
						BaseWindow.makeWarning("Hiba!", e2, "error", this);
					}
					
					if (result==null){
						BaseWindow.makeWarning("A választott orvos már rendel ebben az időpontban!", new Exception("Already exists"), "error", (JFrame)this);
						resultAccept.setEnabled(false);
					} else {
						Iterator<RendeloSzoba> it = result.iterator();
						gombPanel = new JPanel();
						gombPanel.setLayout(new BoxLayout(gombPanel, BoxLayout.Y_AXIS));
						boolean selected = false;
						int i = 0;
						while (it.hasNext()){
							RendeloSzoba szoba = it.next();
							 					
							JRadioButton b = new JRadioButton(szoba.getNev());
							b.putClientProperty("id", szoba.getId());
							gombPanel.add(b);
							if (szoba.isReserved()){
								b.setEnabled(false);
								b.setText(b.getText()+" ("+szoba.getDoctorData()+")");
								if (selected){
									selected=false;
									b.setSelected(selected);
								}
							} else {
								i++;
							}

							if (!selected) {b.setSelected(true); selected=true;}
						
							valasztottSzoba.add(b);
						}
						szobaResult.add(gombPanel);
						if (i==0){
							resultAccept.setEnabled(false);
						} else {
							resultAccept.setEnabled(true);
						}
					}
					
				}
			}
			
	}
	
	public void makeTheGrid(int i, int j, JPanel panel){
		SpringUtilities.makeCompactGrid(panel,
                i, j, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
	}
	
	public void enableForm(){
		alkalmi.setEnabled(true);
		ismetlodo.setEnabled(true);
		date.setEnabled(true);
		send.setEnabled(true);
		
		for (int i=0; i<numPairs-1; i++){
			datas[i].setEnabled(true);
		}
		
		if(alkalmi.getModel()==valasztottGomb.getSelection()){
			datas[3].setEnabled(false);
    		date.setEnabled(true);
		} else if (ismetlodo.getModel()==valasztottGomb.getSelection()){
			datas[3].setEnabled(true);
    		date.setEnabled(false);
		}
		
		resultAccept.setVisible(false);
		resultCancel.setVisible(false);
		
		JLabel l = (JLabel)szobaResult.getComponent(0);
		l.setText(command);
		
		gombPanel.removeAll();
		valasztottSzoba.clearSelection();
		szobaResult.remove(gombPanel);
		
		szobaResult.validate();
		szobaResult.repaint();
		
		tol = null;
		ig = null;
	}
	
	public void disableForm(){
		JLabel l = (JLabel)szobaResult.getComponent(0);
		l.setText(command2);
		szobaResult.validate();
		szobaResult.repaint();
		date.setEnabled(false);
		for (int i=0; i<numPairs-1; i++){
			datas[i].setEnabled(false);
		}
		alkalmi.setEnabled(false);
		ismetlodo.setEnabled(false);
		send.setEnabled(false);
		
		resultAccept.setVisible(true);
		resultCancel.setVisible(true);
	}
	
	public void resetForm(){
		for (int i=0; i<numPairs-1; i++){
			datas[i].setSelectedIndex(0);
		}
		date.setValue("");
		tol = null;
		ig = null;
	}
	
	public static boolean isValidDateStr(String date) {
	    try {
	     DateFormat df =
	    		 new SimpleDateFormat("yyyy-MM-dd"); // YYYY-MM-DD
	     df.setLenient(false);   // this is important!
	     Date d =df.parse(date);	     
	     if (d.compareTo(new Date())<0){
	    	 return false;
	     }
	    }
	    catch (ParseException e) {
	     return false;
	    }
	    catch (IllegalArgumentException e) {
	     return false;
	    }
	    return true;
	  }
	
	private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
	

	
	private void initComboBoxWithTime(JComboBox c){
		int index = 0;
		c.insertItemAt("---Válasszon időpontot!---", index++);
		c.setSelectedIndex(0);
		for (int i=Const.CALENDAR_START_HOUR; i<Const.CALENDAR_END_HOUR; i++){
			for (int j=0; j<60; j+=Const.CALENDAR_TIME_BETWEEN_TIME){
				String jj = j+"";
				if (j<10){
					jj="0"+j;
				}
				String label = i+":"+jj+":00";
				c.insertItemAt(label, index);
				index++;
			}
		}
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
	
	private void initComboBoxWithDays(JComboBox c){
		String days[] = {"---Válasszon napot!---","Hétfő", "Kedd", "Szerda", "Csütörtök", "Péntek", "Szombat", "Vasárnap"};
		for (int i=0; i<days.length; i++){
			c.insertItemAt(days[i], i);
		}
		c.setSelectedIndex(0);
	}
	
	 private class MyRadioButton extends JRadioButton 
     implements ItemListener  {
	     /**
		 * 
		 */
		private static final long serialVersionUID = -7697105295070612056L;
	     private String code = null;
	     public MyRadioButton(String t, String c, boolean f) {
	        super(t,f);
	        setCode(c);
	        addItemListener(this);
	     }
	     public MyRadioButton(String t, String c){
	    	 this(t,c,false);
	     }
	     public void itemStateChanged(ItemEvent e) {
	        if (e.getStateChange()==1){
	        	if (getCode()=="ismetlodo"){
	        		datas[3].setEnabled(true);
	        		date.setEnabled(false);
	        	} else if (getCode()=="alkalmi"){
	        		datas[3].setEnabled(false);
	        		date.setEnabled(true);	
	        	}
	        }
	        
	     }
	     public String getCode() {
			return code;
	     }
	     public void setCode(String code) {
			this.code = code;
	     }
	 }
	 
	 public void reloadData(){
		 datas[0].removeAllItems();
		 initComboBoxWithDoctors(datas[0]);
	 }

}
