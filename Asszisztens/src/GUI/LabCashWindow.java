package GUI;

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

import javax.swing.BoxLayout;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import database.DAO;
import rekord.Csoport;
import rekord.Labor;
import tools.Const;


public class LabCashWindow extends BaseWindow implements  ActionListener, DocumentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1229457295479031303L;
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
	private static int height = 800;
	private static int width = 1200;
	private static int locationX = 0;
	private static int locationY = 0;
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
	
	public static final int COLUMN_COUNT = 5;

	
	public LabCashWindow(DAO dao){
		super(width, height, resizable, visible, title, locationX, locationY, defaultCloseOperation, exit);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Const.PROJECT_PATH+"icon_lab.png"));

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
		deselectAll = new JButton("Összes kijelölés megszüntetése", new ImageIcon(Const.PROJECT_PATH+"icon_cancel.png"));
		category = new JButton("Kategóriák kezelése", new ImageIcon(Const.PROJECT_PATH+"icon_category.png"));
		newItem = new JButton("Új laborvizsgálat", new ImageIcon(Const.PROJECT_PATH+"icon_new.png"));
		manageItem = new JButton("Laborvizsgálatok kezelése", new ImageIcon(Const.PROJECT_PATH+"icon_edit.png"));
		report = new JButton("Összesítő", new ImageIcon(Const.PROJECT_PATH+"icon_report.png"));
		felso = new JPanel();
	
		DefaultTableModel tableModel = new DefaultTableModel() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 4455267473892614053L;
			String[] columnNames = 
				   {"",
	                "Csoport",
	                "Név",
	                "Megjegyzés",
	                "Ár"};

			@Override
		    public String getColumnName(int col) {
		        return columnNames[col];
		    }
			
			
			@Override
			public Object getValueAt(int row, int col) {
				if (col==0){
				   return ((Labor)this.getValueAt(row, col+2)).getSelected();  
				} else if (col==1){
					Csoport eredmeny = null;
					Iterator<Csoport> it = dao.getLaborCsoport().iterator();
					while (it.hasNext()){
						Csoport cs = it.next();
						if (cs.getId()==((Labor)this.getValueAt(row, col+1)).getCsoport()){
							eredmeny = cs;
							break;
						}
					}
					return eredmeny;
				} else if (col==2){
					return dao.getLabor().get(row);
				} else if (col==3){
					String megj = ((Labor)this.getValueAt(row, col-1)).getMegj();
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
				} else if (col==4){
					return ((Labor)this.getValueAt(row, col-2)).getAranyklinikaAr();
				}
				return "Error";
			}
			
			@Override
			public void setValueAt(Object aValue, int row, int column) {
				// TODO Auto-generated method stub
				if (column==0){
					((Labor)this.getValueAt(row, column+2)).setSelected(!((Labor)this.getValueAt(row, column+2)).getSelected());
					Labor l = ((Labor)this.getValueAt(row, column+2));
					if (l.getAlapdij().equals("nem")){
						if (l.getSelected()==false){
							setFizetendo(getFizetendo()-l.getAranyklinikaAr());
							selectedCount--;
						}else{
							setFizetendo(getFizetendo()+l.getAranyklinikaAr());
							selectedCount++;
						}
					}
					
					if (selectedCount>0){
						deselectAll.setEnabled(true);
					} else {
						deselectAll.setEnabled(false);
					}
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
				if (columnIndex==0)
					return Boolean.class;
				else
					return super.getColumnClass(columnIndex);
			}
			
			@Override
			public boolean isCellEditable(int row, int col) {
				// TODO Auto-generated method stub
				if (col==0)
					if (((Labor)this.getValueAt(row, col+2)).getAlapdij().equals("nem") && ((Labor)this.getValueAt(row, col+2)).getAllapot().equals("aktiv"))
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
				         if (column==2){
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
		widthk[1] = (int)((width)*0.25);
		widthk[2] = (int)((width)*0.34);
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
	    		alapdij += j.getAranyklinikaAr();
	    	}
	    }
		
		return alapdij;
	}
	
	public void init(){
		refreshGUI();
		
	}
	
	public void refreshGUI(){			
			setFizetendo(getAlapdij());
	}
	
	
	public void beallitKasszaVegosszeg(int osszeg){
		kasszaVegosszeg.setText("<html><div style='text-align: right;'><span style='font-size: 20px; font-weight: bold;'>Fizetendő:</span> <span style='font-size: 30px; font-weight: bold;'>"+osszeg+"&nbsp;HUF&nbsp;</span></div></html>");
	}
	
	public void refresh(){
		repaint();
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
				if (l.getSelected()!=false){
					s += "<b><span style='color: #701d25;'>"+l.getAranyklinikaAr()+" HUF </span>: "+
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
		List<RowFilter<Object,Object>> rfs = new ArrayList<RowFilter<Object,Object>>(2);
			rfs.add(RowFilter.regexFilter("(?i)"+kereses.getText(), 1));
			rfs.add(RowFilter.regexFilter("(?i)"+kereses.getText(), 2));
		
		RowFilter<DefaultTableModel, Object> rf = RowFilter.orFilter(rfs);
	    sorter.setRowFilter(rf);
	    
	    table.revalidate();
	    table.repaint();
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

class ColorColumnRenderer extends DefaultTableCellRenderer 
{

/**
	 * 
	 */
	private static final long serialVersionUID = -5231478102825612264L;
Color selectedColor = new Color(112, 29, 37);
   Color selectedForeColor = new Color(239, 205, 108);
   float selectedFontSize = 13f;
   float selectedNameSize = 14f;
   
   Color unSelectedForeColor;
   Color unSelectedColor;
   float unSelectedFontSize = 11f;
   float unSelectedNameSize = 12f;
   
   int fontStyle;
   
   private JCheckBox box = new JCheckBox();
 	
   public ColorColumnRenderer() {
      super(); 
   }
  	
   public Component getTableCellRendererComponent
	    (JTable table, Object value, boolean isSelected,
	     boolean hasFocus, int row, int column) 
   {
	  
	   int view = table.convertRowIndexToModel(row);
	   int actualRow = 0;
	  if (view != row){
		  actualRow = view;
	  } else {
		  actualRow = row;
	  }
	   
      Component cell = super.getTableCellRendererComponent
         (table, value, isSelected, hasFocus, row, column);
        
      //System.out.println(row+" "+view);
      
      if (row%2==0){
    	  unSelectedColor = Color.white;
    	  unSelectedForeColor = Color.black;
      } else {
    	  unSelectedColor = new Color(233, 233, 233);
    	  unSelectedForeColor = Color.black;
      }
      
      if (column==0){
    	  box.setHorizontalAlignment(SwingConstants.CENTER);  
    	  box.setBackground( Color.white);  
    	  box.setSelected((Boolean)(table.getModel().getValueAt(actualRow, 0)));
      	  return box;
      } else {
    	  if (column==4){
    		  ((JLabel) cell).setHorizontalAlignment(SwingConstants.CENTER);  
    	  }
    	  boolean firstCell = (Boolean)(table.getModel().getValueAt(actualRow, 0));

          if (firstCell){
        	  if (column==2){
        		  cell.setFont( cell.getFont().deriveFont(selectedNameSize).deriveFont(Font.BOLD) );
        	  } else {
        		  cell.setFont( cell.getFont().deriveFont(selectedFontSize).deriveFont(Font.BOLD) );
        	  }
        	  cell.setBackground( selectedColor );
              cell.setForeground( selectedForeColor );
              
          } else {
        	  if (column==2){
        		  if (((Labor)(table.getModel().getValueAt(actualRow, 2))).getAllapot().equals("aktiv"))
        			  cell.setFont( cell.getFont().deriveFont(unSelectedNameSize).deriveFont(Font.BOLD) );
        		  else
        			  cell.setFont( cell.getFont().deriveFont(unSelectedFontSize).deriveFont(Font.PLAIN) );
        	  } else {
        		  cell.setFont( cell.getFont().deriveFont(unSelectedFontSize).deriveFont(Font.PLAIN) );  
        	  }
        	  cell.setBackground( unSelectedColor );
        	  cell.setForeground( unSelectedForeColor );
        	  
          }        
      }
      table.revalidate();
      table.repaint();
      return cell;  
      
      
   }
}