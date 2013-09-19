package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import model.DAO;

import rekord.Csoport;
import rekord.Labor;
import tools.Const;


public class LabCashWindow extends BaseWindow implements  ActionListener, DocumentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1229457295479031303L;
	
	public static final int CHECHBOX = 0;
	public static final int CATEGORY = 2;
	public static final int LAB_OBJECT = 1;
	public static final int COMMENT = 3;
	public static final int PRICE = 4;
	
	public static final int COLUMN_COUNT = 5;
	
	public static final String[] columnNames = 	 {"",
										         "Név",
										         "Kategória",
										         "Megjegyzés",
										         "Ár"};
	
	public static final int ARANYKLINIKA_AR = 0;
	public static final int CENTRUMLAB_AR = 1;
	public static final int KLINIKA_AR = 2;
	
	//GUI Components
	private JScrollPane felsoScroll;
	private JPanel felso;
	private JPanel also;
	private JPanel gombok;
	private JPanel keresoPanel;
	private JLabel kasszaVegosszeg;
	private JButton deselectAll;
	private JTextField kereses;
	private JButton category;
	private JButton newItem;
	private JButton manageItem;
	
	//Window dimensions & options
	private static String title = "Labor fizetés összesítő";
	private static int height = BaseWindow.screenHeight-100;
	private static int width = 1200;
	private static boolean resizable = true;
	private static boolean visible = false;
	private static int defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE;
	private static boolean exit = false;
	private int alsoHeight = 100;
	private int buttonHeight = 30;
	private int searchHeight = 30;
	
	//Database connection
	private int fizetendo;
	private DAO dao;
	
	
	private JTable table;
	private int selectedCount = 0;
	private DefaultRowSorter<DefaultTableModel, Integer> sorter;
	private JButton report;
	private JButton sumFilter;

	private JComboBox priceFilter;

	public LabCashWindow(DAO dao){
		super(width, height, resizable, visible, title, 0, 0, defaultCloseOperation, exit);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Const.ICON_PATH+"icon_lab.png"));

		this.dao = dao;
		this.fizetendo = 0;	
		
		createWindowContent();
		
	}
	
	public void createWindowContent(){
		also = new JPanel();
		gombok = new JPanel();
		kereses = new JTextField();
		keresoPanel = new JPanel();
		kasszaVegosszeg = new JLabel();
		deselectAll = new JButton("Összes kijelölés megszüntetése", new ImageIcon(Const.ICON_PATH+"icon_cancel.png"));
		category = new JButton("Kategóriák kezelése", new ImageIcon(Const.ICON_PATH+"icon_category.png"));
		newItem = new JButton("Új laborvizsgálat", new ImageIcon(Const.ICON_PATH+"icon_new.png"));
		manageItem = new JButton("Laborvizsgálatok kezelése", new ImageIcon(Const.ICON_PATH+"icon_edit.png"));
		report = new JButton("Összesítő", new ImageIcon(Const.ICON_PATH+"icon_report.png"));
		sumFilter = new JButton("Csak a kiválasztottak mutatása", new ImageIcon(Const.ICON_PATH+"icon_report.png"));
		
		Object[] items = {"Aranyklinika ár", "CentrumLab ár", "Klinika ár"};
		priceFilter = new JComboBox(items);
		felso = new JPanel();
	
		DefaultTableModel tableModel = new DefaultTableModel() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 4455267473892614053L;


			@Override
		    public String getColumnName(int col) {
		        return columnNames[col];
		    }
			
			
			@Override
			public Object getValueAt(int row, int col) {
				if (col==CHECHBOX){
				   return ((Labor)this.getValueAt(row, LAB_OBJECT)).getSelected();  
				} else if (col==CATEGORY){
					Csoport eredmeny = null;
					Iterator<Csoport> it = dao.getLaborCsoport().iterator();
					while (it.hasNext()){
						Csoport cs = it.next();
						if (cs.getId()==((Labor)this.getValueAt(row, LAB_OBJECT)).getCsoport()){
							eredmeny = cs;
							break;
						}
					}
					return eredmeny;
				} else if (col==LAB_OBJECT){
					return dao.getLabor().get(row);
				} else if (col==COMMENT){
					String megj = ((Labor)this.getValueAt(row, LAB_OBJECT)).getMegj();
					int lineBreak = 65;
					if (megj.length()>lineBreak){
						for (int i=lineBreak; i<megj.length(); i++){
							if (megj.substring(i, i+1).equals(" ")){
								megj = megj.substring(0, i)+megj.substring(i, i+1).replace(" ", "<br/>")+megj.substring(i+1, megj.length());
								break;
							}
						}
					}
					return "<html>"+megj+"</html>";
				} else if (col==PRICE){
					if (priceFilter.getSelectedIndex()==ARANYKLINIKA_AR){
						return ((Labor)this.getValueAt(row, LAB_OBJECT)).getAranyklinikaAr();	
					} else if (priceFilter.getSelectedIndex()==CENTRUMLAB_AR){
						return ((Labor)this.getValueAt(row, LAB_OBJECT)).getPartnerAr();	
					} else if (priceFilter.getSelectedIndex()==KLINIKA_AR){
						return ((Labor)this.getValueAt(row, LAB_OBJECT)).getLaborAr();
					}
					
				}
				return "Error";
			}
			
			@Override
			public void setValueAt(Object aValue, int row, int column) {
				// TODO Auto-generated method stub
				if (column==0){
					((Labor)this.getValueAt(row, LAB_OBJECT)).setSelected(!((Labor)this.getValueAt(row, LAB_OBJECT)).getSelected());
					Labor l = ((Labor)this.getValueAt(row, LAB_OBJECT));
					if (l.getAlapdij().equals("nem")){
						int ar = 0;
						if (priceFilter.getSelectedIndex()==ARANYKLINIKA_AR){
							ar = l.getAranyklinikaAr();
						} else if (priceFilter.getSelectedIndex()==CENTRUMLAB_AR){
							ar = l.getPartnerAr();
						} else if (priceFilter.getSelectedIndex()==KLINIKA_AR){
							ar = l.getLaborAr();
						}
						if (l.getSelected()==false){
							setFizetendo(getFizetendo()-ar);
							selectedCount--;
						}else{
							setFizetendo(getFizetendo()+ar);
							selectedCount++;
						}
					}
					
					if (selectedCount>0){
						deselectAll.setEnabled(true);
					} else {
						deselectAll.setEnabled(false);
					}
					table.revalidate();
					table.repaint();
				} else {
					super.setValueAt(aValue, row, column);
				}
				
			}
			
			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return dao.getLabor().size();
			}
			
			@Override
			public int getColumnCount() {
				// TODO Auto-generated method stub
				return COLUMN_COUNT;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex==CHECHBOX)
					return Boolean.class;
				else
					return super.getColumnClass(columnIndex);
			}
			
			@Override
			public boolean isCellEditable(int row, int col) {
				// TODO Auto-generated method stub
				if (col==CHECHBOX)
					if (((Labor)this.getValueAt(row, LAB_OBJECT)).getAlapdij().equals("nem") && ((Labor)this.getValueAt(row, LAB_OBJECT)).getAllapot().equals("aktiv"))
						return true;
					else
						return false;
				else 
					return false;
			}
			
		};		
		
		sorter = new TableRowSorter<DefaultTableModel>(tableModel);
		
		table = new JTable(tableModel);
		felsoScroll = new JScrollPane(table);
		
		table.setRowSorter(sorter);
		table.setFocusable(false);
		table.setRowSelectionAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(30);
		table.getTableHeader().setEnabled(false);
		
		table.addMouseListener(new MouseAdapter() {
			   public void mouseClicked(MouseEvent e) {
			      if (e.getClickCount() == 2) {
			         JTable target = (JTable)e.getSource();
			         int row = target.getSelectedRow();
			         int column = target.getSelectedColumn();
				         if (column==LAB_OBJECT){
				        	 deselectAll();
				        	 int view = table.convertRowIndexToModel(row);
				      	   int actualRow = 0;
				      	  if (view != row){
				      		  actualRow = view;
				      	  } else {
				      		  actualRow = row;
				      	  }
				        	 new LabCashItem(dao, (Labor)table.getModel().getValueAt(actualRow, column)); 
				         }
			         }
			   }
			});
		
		int[] widthk = new int[COLUMN_COUNT];
		
		widthk[0] = (int)((width)*0.025);
		widthk[1] = (int)((width)*0.35);
		widthk[2] = (int)((width)*0.24);
		widthk[3] = (int)((width)*0.3);
		widthk[4] = (int)((width)*0.05);
		 
		for (int i=0;  i<COLUMN_COUNT; i++){
			TableColumn tm = table.getColumnModel().getColumn(i);
		    tm.setCellRenderer(new ColorColumnRenderer());
		    tm.setPreferredWidth(widthk[i]);
		}
		
		deselectAll.addActionListener(this);
		deselectAll.setFocusable(false);
		deselectAll.setEnabled(false);
		
		category.addActionListener(this);
		category.setFocusable(false);
		
		newItem.addActionListener(this);
		newItem.setFocusable(false);
		
		manageItem.addActionListener(this);
		manageItem.setFocusable(false);
		
		report.addActionListener(this);
		report.setFocusable(false);
		
		sumFilter.addActionListener(this);
		sumFilter.setFocusable(false);
		
		priceFilter.addActionListener(this);
		priceFilter.setFocusable(false);
		JPanel priceFilterPanel = new JPanel();
		
		priceFilterPanel.setPreferredSize(new Dimension(width/10, buttonHeight));
		priceFilterPanel.add(priceFilter);
		
		felso.setLayout(new BoxLayout(felso, BoxLayout.PAGE_AXIS));
		felso.setSize(new Dimension(width, height-alsoHeight));
		
		felsoScroll.setSize(width, height-alsoHeight);
		felsoScroll.setPreferredSize(new Dimension(width, height-alsoHeight));
		felsoScroll.getVerticalScrollBar().setUnitIncrement(30);
		
		also.setLayout(new BorderLayout());
				
		gombok.setPreferredSize(new Dimension(width, buttonHeight));
		gombok.setLayout(new BoxLayout(gombok, BoxLayout.X_AXIS));
		gombok.add(deselectAll);
		gombok.add(category);
		gombok.add(newItem);
		gombok.add(report);
		gombok.add(sumFilter);
		gombok.add(priceFilterPanel);
		//gombok.add(manageItem);
			
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
		add(keresoPanel,"North");
		add(also,"South");
			
		refresh();
	}
	
	
	public int getAlapdij(){
		int alapdij = 0;
		
	    Iterator<Labor> itAlapdij = dao.getLabor().iterator();
	    while ( itAlapdij.hasNext() ){
	    	Labor j = itAlapdij.next();
	    	if (j.getAlapdij().equals("igen")){
	    		int ar = 0;
	    		if (priceFilter.getSelectedIndex()==ARANYKLINIKA_AR){
					ar = j.getAranyklinikaAr();
				} else if (priceFilter.getSelectedIndex()==CENTRUMLAB_AR){
					ar = j.getPartnerAr();
				} else if (priceFilter.getSelectedIndex()==KLINIKA_AR){
					ar = j.getLaborAr();
				}
	    		alapdij += ar;
	    	}
	    }
		
		return alapdij;
	}
	
	public void init(){
		setFizetendo(getAlapdij());	
	}
	
	public void beallitKasszaVegosszeg(int osszeg){
		kasszaVegosszeg.setText("<html><div style='text-align: right;'><span style='font-size: 20px; font-weight: bold;'>Fizetendő:</span> <span style='font-size: 30px; font-weight: bold;'>"+osszeg+"&nbsp;HUF&nbsp;</span></div></html>");
	}
	
	public void refresh(){
		//repaint();
		SwingUtilities.updateComponentTreeUI(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==deselectAll){
			deselectAll();
		} else if (e.getSource()==category){
			new LabCashCategory(dao);
		/*} else if (e.getSource()==manageItem){
			new LabCashManage(dao);*/
		} else if (e.getSource()==report){
			report();
		} else if (e.getSource()==newItem){
			new LabCashItem(dao);
		} else if (e.getSource()==sumFilter){
			if (kereses.getText().equals("sum")){
				kereses.setText("");
				sorter.setRowFilter(null);
				table.repaint();
			} else {
				kereses.setText("sum");
				table.repaint();
			}
		} else if (e.getSource()==priceFilter){
			for (int i=0; i<table.getModel().getRowCount(); i++){
				if (((Labor)table.getModel().getValueAt(i, LAB_OBJECT)).getAlapdij().equals("nem")){
					((Labor)table.getModel().getValueAt(i, LAB_OBJECT)).setSelected(false);
				}
			}
			
			
			setFizetendo(getAlapdij());	
			refresh();
		}
	}
	
	public void report(){
		String s = "<html>";
		if (selectedCount==0){
			s += "Nincs kiválasztva vizsgálat!";
		} else {
			Iterator<Labor> it = dao.getLabor().iterator();
			while (it.hasNext()){
				Labor l = it.next();
				int ar = 0;
	    		if (priceFilter.getSelectedIndex()==ARANYKLINIKA_AR){
					ar = l.getAranyklinikaAr();
				} else if (priceFilter.getSelectedIndex()==CENTRUMLAB_AR){
					ar = l.getPartnerAr();
				} else if (priceFilter.getSelectedIndex()==KLINIKA_AR){
					ar = l.getLaborAr();
				}
				if (l.getSelected()!=false){
					s += "<b><span style='color: #701d25;'>"+ar+" HUF </span>: "+
							""+l+"</b>"+
							((!l.getIdo().equals(""))?"+"+l.getIdo()+"":"")+"<br/>"+
							((!l.getMegj().equals(""))?"(<i>Megj: "+l.getMegj()+"</i>)<br/>":"")+
							"<hr/>";
				}
				
			}
		}
		
		s+="</html>";
		
		BaseWindow.makeWarning(s, new Exception(), "success");
	}
	
	public void deselectAll(){
		Iterator<Labor> itAlapdij = dao.getLabor().iterator();
	    while ( itAlapdij.hasNext() ){
	    	Labor j = itAlapdij.next();
	    	if (j.getAlapdij().equals("nem"))
	    		j.setSelected(false);
	    }
	    setFizetendo(getAlapdij());
		refresh();
		selectedCount = 0;
		deselectAll.setEnabled(false);
	}

	/*
	 * Searchbar filter function
	 * Called by the DocumentListener
	 * */
	public void filter(){
		String k = kereses.getText();
		if (k.equals("")){
			sorter.setRowFilter(null);
		} else if (k.equals("sum")){
			List<RowFilter<Object,Object>> rfs = new ArrayList<RowFilter<Object,Object>>(COLUMN_COUNT);
			rfs.add(RowFilter.regexFilter("true", CHECHBOX));
		
			RowFilter<DefaultTableModel, Object> rf = RowFilter.orFilter(rfs);
		    sorter.setRowFilter(rf);
		} else {
			List<RowFilter<Object,Object>> rfs = new ArrayList<RowFilter<Object,Object>>(COLUMN_COUNT);
			rfs.add(RowFilter.regexFilter("(?i)"+kereses.getText(), CATEGORY));
			rfs.add(RowFilter.regexFilter("(?i)"+kereses.getText(), LAB_OBJECT));
		
			RowFilter<DefaultTableModel, Object> rf = RowFilter.orFilter(rfs);
		    sorter.setRowFilter(rf);
		}
		
	    
	    table.revalidate();
	    table.repaint();
	}
	
	class ColorColumnRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -5231478102825612264L;
		
		Color claret = new Color(112, 29, 37);
		Color gold = new Color(239, 205, 108);
		Color extraLightGray = new Color(233, 233, 233);
	
		float fontSize;
		int fontStyle;
	
		private JCheckBox box = new JCheckBox();
		private Color backColor;
		private Color foreColor;
	
		private Labor l;
	
		public ColorColumnRenderer() {
			super(); 
		}
	
		public Component getTableCellRendererComponent
		(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) 
		{
	
			int view = table.convertRowIndexToModel(row);
			int actualRow = 0;
			if (view != row)
				actualRow = view;
			else
				actualRow = row;
	
			boolean selected = (Boolean)(table.getModel().getValueAt(actualRow, CHECHBOX));
			l = ((Labor)(table.getModel().getValueAt(actualRow, LAB_OBJECT)));
	
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	
			if (selected){
				backColor = claret;
				foreColor = gold;
				fontSize = 13f;
				fontStyle = Font.BOLD;
			} else {
				if (row%2==0){
					backColor = Color.white;
					foreColor = Color.black;
				} else {
					backColor = extraLightGray;
					foreColor = Color.black;
				}
				fontSize = 12f;
				if (column==LAB_OBJECT){
					fontStyle = Font.BOLD;
				} else {
					fontStyle = Font.PLAIN;
				}
			}
	
			if (l.getAllapot().equals("passziv")){
				if (column==CHECHBOX){
					backColor = Color.LIGHT_GRAY;
					foreColor = Color.black;
					
				}
				fontStyle = Font.PLAIN;
	
			} else {
				
			}
				
	
			cell.setFont( cell.getFont().deriveFont(fontSize).deriveFont(fontStyle) );
			cell.setBackground( backColor );
			cell.setForeground( foreColor );
	
	
			if (column==CHECHBOX){
				if (l.getAlapdij().equals("igen")){
					backColor = Color.LIGHT_GRAY;
					foreColor = Color.black;
				}
				box.setBackground(backColor);
				box.setForeground(foreColor);
				box.setHorizontalAlignment(SwingConstants.CENTER); 
				box.setSelected((Boolean)(table.getModel().getValueAt(actualRow, CHECHBOX)));
				return box;
			} else if (column==PRICE){
				((JLabel) cell).setHorizontalAlignment(SwingConstants.CENTER);  
			}
			
			return cell;
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