package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

import rekord.RendeloIdopont;
import rekord.RendeloOrvos;
import rekord.RendeloSzoba;
import tools.ProjectContants;

import database.DoctorScheduleDatabase;

public class DoctorScheduleWindow extends BaseWindow{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6137634184562239340L;
	DoctorScheduleDatabase dsObject;
	
	private DoctorScheduleDataWindow torzsadat;
	private DoctorScheduleDeleteWindow torolIdopont;
	private DoctorScheduleNewWindow ujIdopont;
	private JComboBox szobaLista;
	private JComboBox hetLista;
	private JTable calendar;
	
	private int topSize = 30;
	private int topSizeScroll = 100;
	
	private JPanel colors;
	
	public DoctorScheduleWindow(){
		super(0, 0, true, false, "Rendelő beosztás - Orvosok", 0, 0, JFrame.DISPOSE_ON_CLOSE, false);
		
		newJMenu("Fájl", "");
			newJMenuItem("reLoad", "Adatok újratöltése", "", false);
		newJMenu("Szerkesztés", "");
			newJMenuItem("data", "Törzsadatok szerkesztése", "", true);
			newJMenuItem("add", "Új időpont felvétele", "", true);
			newJMenuItem("delete", "Rögzített időpontok lekérdezése és törlése", "", true);
			newJMenuItem("delete", "Szabadság bejegyzése (ismétlődő időpont)", "", false);
		setMenu();
		
		try {
			dsObject = new DoctorScheduleDatabase();
		} catch (SQLException e) {
			makeWarning("SQL Hiba!", e, "error", this);
		}
		
		
		init();
		
		setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel(new FlowLayout());
		//JPanel bottomPanel = new JPanel(new GridLayout());
		
		JScrollPane bottom = new JScrollPane(calendar);
		
		topPanel.setPreferredSize(new Dimension(getWidth(),topSize));
		bottom.setPreferredSize(new Dimension(getWidth(),getHeight()-topSizeScroll));
		//bottomPanel.setPreferredSize(new Dimension(getWidth()-20,getHeight()-topSize-20));
		
        
        colors = new JPanel();
        
        generateColorsPanel();
		
		szobaLista = new JComboBox();
		szobaLista.setPreferredSize(new Dimension(200, 20));
		initComboBoxWithRooms(szobaLista);
		szobaLista.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int id = szobaLista.getSelectedIndex();
        		if (id>0){
        			calendar.setVisible(true);
        		} else {
        			calendar.setVisible(false);
        		}
				calendar.validate();
				calendar.repaint();
				generateColorsPanel();
				colors.validate();
				colors.repaint();			
			}
		});
		
		hetLista = new JComboBox();
		hetLista.setPreferredSize(new Dimension(200, 20));
		hetLista.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
        		int id = szobaLista.getSelectedIndex();
        		if (id>0){
        			calendar.setVisible(true);
        		} else {
        			calendar.setVisible(false);
        		}
				calendar.validate();
				calendar.repaint();
				generateColorsPanel();
				colors.validate();
				colors.repaint();			
			}
		});
		
		calendar.setVisible(false);
		calendar.setFocusable(false);
		calendar.setRowSelectionAllowed(false);
		
		initComboBoxWithWeeks(hetLista);
		
		JLabel l = new JLabel("Válasszon megjeleníteni kívánt rendelőt: ", JLabel.TRAILING);
        topPanel.add(l);
        l.setLabelFor(szobaLista);
        topPanel.add(szobaLista);
        
        JLabel l2 = new JLabel("Válasszon megjeleníteni kívánt hetet: ", JLabel.TRAILING);
        topPanel.add(l2);
        l2.setLabelFor(hetLista);
        topPanel.add(hetLista);
        
		add(topPanel,"North");
		add(bottom,"Center");
		add(colors,"South");
	}
	
	public void generateColorsPanel(){
		int count = dsObject.getOrvosTomb().size();
		int oneLine = getWidth()/200;
		colors.removeAll();
		colors.setLayout(new GridLayout(count/oneLine, oneLine));
		Iterator<RendeloOrvos> orvosIt = dsObject.getOrvosTomb().iterator();
        while (orvosIt.hasNext()){
        	RendeloOrvos r = orvosIt.next();
        	colors.add(new JLabel("<html><table><tr><td style='width: 10px; height: 10px; background-color: "+Integer.toHexString( r.getSzin().getRGB() & 0x00ffffff )+"'>&nbsp;</td><td>"+r.getNev()+"</td></tr></table></html>"));
        }
	}
	
	public void init(){
		torzsadat = new DoctorScheduleDataWindow(dsObject);
		torolIdopont = new DoctorScheduleDeleteWindow(dsObject);
		ujIdopont = new DoctorScheduleNewWindow(dsObject);
		
		
		TableModel dataModel = new AbstractTableModel() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 7791167407474757194L;
			String[] columnNames = {"Időpont",
	                "Hétfő",
	                "Kedd",
	                "Szerda",
	                "Csütörtök",
	                "Péntek",
	                "Szombat",
	                "Vasárnap"};

	
			@Override
		    public int getColumnCount() {
		        return columnNames.length;
		    }
			
			@Override
		    public int getRowCount() {
		        return 60/ProjectContants.CALENDAR_TIME_BETWEEN_TIME*(ProjectContants.CALENDAR_END_HOUR-ProjectContants.CALENDAR_START_HOUR);
		    }
			
			@Override
		    public String getColumnName(int col) {
		        return columnNames[col];
		    }
			
			
			@Override
		    public Object getValueAt(int row, int col) {
		        if (col==0){
		        	int hour = row/(60/ProjectContants.CALENDAR_TIME_BETWEEN_TIME)+ProjectContants.CALENDAR_START_HOUR;
		        	int min = row%(60/ProjectContants.CALENDAR_TIME_BETWEEN_TIME)*ProjectContants.CALENDAR_TIME_BETWEEN_TIME;
		        	String minS = "";
		        	String hourS = "";
		        	String endMinS = "";
		        	String endHourS = "";
		        	if (min<10){
		        		minS="0"+min;
		        	} else {
		        		minS=min+"";
		        	}
		        	int endMin=min+ProjectContants.CALENDAR_TIME_BETWEEN_TIME;
		        	
		        	int endHour = hour;
		        	if (endMin==60){
		        		endMin=0;
		        		endHour++;
		        	}
		        	if (endMin<10){
		        		endMinS="0"+endMin;
		        	} else {
		        		endMinS=endMin+"";
		        	}
		        	if (hour<10){
		        		hourS="0"+hour;
		        	} else {
		        		hourS=""+hour;
		        	}
		        	
		        	if (endHour<10){
		        		endHourS="0"+endHour;
		        	} else {
		        		endHourS=""+endHour;
		        	}
		        	
		        	String from = hourS+":"+minS;
		        	String to = endHourS+":"+endMinS;
		        	return from+" - "+to;
		        } else {
		        	String column = (String) getValueAt(row, 0);
		        	try {
		        		RendeloSzoba sz = (RendeloSzoba) szobaLista.getModel().getSelectedItem();
		        		int id = sz.getId();
		        		Iterator<RendeloIdopont> it = dsObject.getIdopontTomb().iterator();
		        		DateFormat formatter = null;
						Date tol = null;
						Date ig = null;
						while (it.hasNext()){
		        			RendeloIdopont r = it.next();
		        			formatter = new SimpleDateFormat("HH:mm");
		        			
		        			tol = formatter.parse(column.substring(0, 5));
		        			ig = formatter.parse(column.substring(7, 13));
		        			
		        			Calendar tombKezdet = Calendar.getInstance();
		    				Calendar tombVeg = Calendar.getInstance();
		    				tombKezdet.setTime(r.getTol());
		    				tombVeg.setTime(r.getIg());
		    				
		    				Calendar cellaKezdet = Calendar.getInstance();
		    				Calendar cellaVeg = Calendar.getInstance();
		    				cellaKezdet.setTime(tol);
		    				cellaVeg.setTime(ig);
		    				
		     				boolean azonosNap = 
		    						tombKezdet.get(Calendar.DAY_OF_WEEK) == col+1;
		     				boolean azonosSzoba =
		     						id == r.getSzobaId();
		     				if (azonosNap && azonosSzoba){
		     					System.out.println(cellaKezdet.get(Calendar.MILLISECONDS_IN_DAY));
		     					System.out.println(tombKezdet.get(Calendar.MILLISECONDS_IN_DAY));
		     				}
		    				boolean azonosIdo = 
									(cellaKezdet.get(Calendar.MILLISECONDS_IN_DAY) >= tombKezdet.get(Calendar.MILLISECONDS_IN_DAY)) &&
									((cellaVeg.get(Calendar.MILLISECONDS_IN_DAY)) <= tombVeg.get(Calendar.MILLISECONDS_IN_DAY));
		    				if (azonosNap && azonosIdo && azonosSzoba){
		    					Color szin = dsObject.getDoctorColorById(r.getOrvosId());
		    					
		    					return "<html><div style='width: "+getWidth()/columnNames.length+"; height: 100%; color: "+getFontColorBasedOnBGBrightness(szin)+"; background-color: "+Integer.toHexString( szin.getRGB() & 0x00ffffff )+";'>"+dsObject.getDoctorNameById(r.getOrvosId())+"</div></html>";
		    				}
		        		}
						return null;
		        	} catch (ClassCastException e) {
						return null;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
				return null;
		    }

		};
		calendar = new JTable(dataModel);
	}

	private void initComboBoxWithRooms(JComboBox c){
		int index = 0;
		c.insertItemAt("---Válasszon rendelőt!---", index++);
		c.setSelectedIndex(0);
		Iterator<RendeloSzoba> it = dsObject.getSzobaTomb().iterator();
		while (it.hasNext()){
			c.insertItemAt(it.next(), index++);
		}
	}
	
	private void initComboBoxWithWeeks(JComboBox c){
		int index = 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		for (int i=cal.get(Calendar.WEEK_OF_YEAR); i<getNumWeeksForYear(cal.get(Calendar.YEAR)); i++){
			c.insertItemAt(i, index++);
		}
		c.setSelectedIndex(0);
	}
	
	private static int getBrightness(Color c) {
	    return (int) Math.sqrt(
	      c.getRed() * c.getRed() * .241 +
	      c.getGreen() * c.getGreen() * .691 +
	      c.getBlue() * c.getBlue() * .068);
	}

	public static String getFontColorBasedOnBGBrightness(Color color) {
	    if (getBrightness(color) < 130)
	        return "white";
	    else
	        return "black";
	}
	
	public static int getNumWeeksForYear(int year) {  
		  Calendar c = Calendar.getInstance();  
		   
		  c.set(year, 0, 1);  
		  return c.getMaximum(Calendar.WEEK_OF_YEAR);   
	}  
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("data")){
			//TODO
			//torzsadat.refreshLists();
			torzsadat.setVisible(true);
		} else if (cmd.equals("add")){
			ujIdopont.setVisible(true);
			ujIdopont.reloadData();
		} else if (cmd.equals("delete")){
			torolIdopont.setVisible(true);
		} else if (cmd.equals("select")){
			calendar.validate();
			calendar.repaint();
		}
	}
}
