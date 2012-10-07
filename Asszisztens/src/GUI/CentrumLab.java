package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import layout.SpringUtilities;


import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFOperator;

import GUI.BaseWindow;
import tools.PdfOpenFilter;

/**
 * @author Dobó László
 */
public class CentrumLab extends BaseWindow implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4202548799866747834L;
	static PDDocument doc = null;
	static String inputFile;
	static JFrame frame;
	static File file;
	private URL url;
	private String extension;
	private ArrayList<String> mitCsere;
	private ArrayList<String> mireCsere;
	
	JButton openFile;
	JButton editFile;
	JButton preview;
	JButton saveFile;
	JButton cancel;
	
	JTextArea previewText;
	
	JPanel panel;
	private String previewString = "Előnézethez nyomja meg az 'Előnézet' gombot Megnyitás után!";
	
	/**
	 * A foprogram
	 * @param args
	 */
	public CentrumLab(){
		super(670, 650, false, false, "CentrumLab lelet átalakítás", 0, 0, JFrame.DISPOSE_ON_CLOSE, false);
		
		mitCsere = new ArrayList<String>();
		mireCsere = new ArrayList<String>();
		
		mitCsere.add(new String("Centrum-Lab Kft Kiskõrösi Laboratóriuma"));
		mireCsere.add(new String("            Aranyklinika Kft"));
		
		mitCsere.add(new String("Szakmai vezetõ: Dr. Széles Ilona"));
		mireCsere.add(new String("6720 Szeged, Arany János utca 14."));
		
		mitCsere.add(new String("Vezetõ asszisztens: Gálik Sándorné"));
		mireCsere.add(new String("        Tel: 20/573-54-34"));
		
		mitCsere.add(new String("6200 Kiskõrös, Petõfi tér 12."));
		mireCsere.add(new String("           30/561-10-98"));
		
		mitCsere.add(new String("Tel: \\+36 78 414-754"));
		mireCsere.add(new String(""));
		
		mitCsere.add(new String("e-mail:info@centrumlab.hu"));
		mireCsere.add(new String("e-mail:info@aranyklinika.hu"));
		
		mitCsere.add(new String("www.centrumlab.hu"));
		mireCsere.add(new String("www.aranyklinika.hu"));
		
		mitCsere.add(new String("Validálta: Dr. Széles Ilona"));
		mireCsere.add(new String(""));
		
		init();
		
		panel.setLayout(new SpringLayout());
		
		panel.add(openFile);
		panel.add(editFile);
		panel.add(preview);
		panel.add(saveFile);
		panel.add(cancel);
		
		//Lay out the panel.
        makeTheGrid(1, 5, panel);
        panel.setOpaque(true);
        
        JPanel previewPanel = new JPanel();
        previewPanel.add(previewText);
		JScrollPane pane = new JScrollPane(previewPanel );
        pane.setPreferredSize(new Dimension(getSize().width, getSize().height-60));
        
        setLayout(new BorderLayout());
        add(panel,"Center");
        add(pane,"South");
		
	}
	
	public void makeTheGrid(int i, int j, JPanel panel){
		SpringUtilities.makeCompactGrid(panel,
                i, j, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
	}

	
	public void init(){
		openFile = new JButton("Megnyitás");
		openFile.addActionListener(this);
		openFile.setActionCommand("openFile");
		
		editFile = new JButton("Szerkesztés");
		editFile.addActionListener(this);
		editFile.setActionCommand("editFile");
		editFile.setEnabled(false);
		
		saveFile = new JButton("Mentés");
		saveFile.addActionListener(this);
		saveFile.setActionCommand("saveFile");
		saveFile.setEnabled(false);
		
		preview = new JButton("Előnézet");
		preview.addActionListener(this);
		preview.setActionCommand("preview");
		preview.setEnabled(false);
		
		cancel = new JButton("Mégsem");
		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");
		cancel.setEnabled(false);
		
		panel = new JPanel();
		previewText = new JTextArea(previewString );
		previewText.setFont(new Font("Courier New", Font.PLAIN, 12));
		previewText.setEditable(false);
		
	}
	
	@SuppressWarnings("rawtypes")
	public static void replace(String mit, String mire) throws IOException{
		List pages = doc.getDocumentCatalog().getAllPages();
        for( int i=0; i<pages.size(); i++ )
        {
            PDPage page = (PDPage)pages.get( i );
            PDStream contents = page.getContents();
            PDFStreamParser parser = new PDFStreamParser(contents.getStream() );
            parser.parse();
            List tokens = parser.getTokens();
            for( int j=0; j<tokens.size(); j++ )
            {
                Object next = tokens.get( j );
                if( next instanceof PDFOperator )
                {
                    PDFOperator op = (PDFOperator)next;
                    String strToFind = mit;
					String message = mire;
                    if( op.getOperation().equals( "Tj" ) )
                    {
                        COSString previous = (COSString)tokens.get( j-1 );
                        String string = previous.getString();
                        string = string.replaceFirst( strToFind, message );
                        previous.reset();
                        previous.append( string.getBytes("ISO-8859-1") );
                    }
                    else if( op.getOperation().equals( "TJ" ) )
                    {
                        COSArray previous = (COSArray)tokens.get( j-1 );
                        for( int k=0; k<previous.size(); k++ )
                        {
                            Object arrElement = previous.getObject( k );
                            if( arrElement instanceof COSString )
                            {
                                COSString cosString = (COSString)arrElement;
                                String string = cosString.getString();
                                string = string.replaceFirst( strToFind, message );
                                cosString.reset();
                                cosString.append( string.getBytes("ISO-8859-1") );
                            }
                        }
                    }
                }
            }
            PDStream updatedStream = new PDStream(doc);
            OutputStream out = updatedStream.createOutputStream();
            ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
            tokenWriter.writeTokens( tokens );
            page.setContents( updatedStream );
        }
	}
	
	public static String print() throws IOException{
		String output = "";
		List<?> pages = doc.getDocumentCatalog().getAllPages();
        for( int i=0; i<pages.size(); i++ )
        {
            PDPage page = (PDPage)pages.get( i );
            PDStream contents = page.getContents();
            PDFStreamParser parser = new PDFStreamParser(contents.getStream() );
            parser.parse();
            List<?> tokens = parser.getTokens();
            for( int j=0; j<tokens.size(); j++ )
            {
                Object next = tokens.get( j );
                if( next instanceof PDFOperator )
                {
                    PDFOperator op = (PDFOperator)next;
                    if( op.getOperation().equals( "Tj" ) )
                    {
                        COSString previous = (COSString)tokens.get( j-1 );
                        String string = previous.getString();
                        output+=string+"\n";
                        previous.reset();
                        previous.append( string.getBytes("ISO-8859-1") );
                    }
                    else if( op.getOperation().equals( "TJ" ) )
                    {
                        COSArray previous = (COSArray)tokens.get( j-1 );
                        for( int k=0; k<previous.size(); k++ )
                        {
                            Object arrElement = previous.getObject( k );
                            if( arrElement instanceof COSString )
                            {
                                COSString cosString = (COSString)arrElement;
                                String string = cosString.getString();
                                output+=string+"\n";
                                cosString.reset();
                                cosString.append( string.getBytes("ISO-8859-1") );
                            }
                        }
                    }
                }
            }
            PDStream updatedStream = new PDStream(doc);
            OutputStream out = updatedStream.createOutputStream();
            ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
            tokenWriter.writeTokens( tokens );
            page.setContents( updatedStream );
        }
        return output;
	}
	
	
	public static void save(URL url){
		String nev = url.toString().replace("file:/", "").replace(".pdf","_aranyklinika.pdf").replaceAll("%20"," ");
		try {
			doc.save( nev );
			BaseWindow.makeWarning("Elmentettem az átalakított fájlt!\r\n"+nev, new Exception(), "success", new JFrame());
		} catch (COSVisitorException e) {
			BaseWindow.makeWarning("Nem tudtam a fájlt menteni!", e, "error", new JFrame());
		} catch (IOException e) {
			BaseWindow.makeWarning("Nem tudtam a fájlt menteni!", e, "error", new JFrame());
		}
	}
	
	public void close(){
		if( doc != null )
        {
            try {
				doc.close();
				url = null;
			} catch (IOException e) {
				BaseWindow.makeWarning("Nem tudtam lezárni a fájlt!", e, "error", new JFrame());
			}
        }
	}
	
	public void finalize(){
		save(url);
		close();
	}
	
	 public static void openPdf(URL url) throws IOException{        
         doc = PDDocument.load( url );
         if( doc.isEncrypted() )
         {
             throw new IOException( "Hiba! A fájl jelszóvédett! " );
         }
         
	}

	public boolean fileWindow(){
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new PdfOpenFilter());
		// TODO
 		if (fc.showOpenDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            extension = file.toString().substring((int)file.toString().length()-3, (int)file.toString().length());
            extension = extension.toLowerCase();
            if (!(extension.equals("pdf"))){
            	BaseWindow.makeWarning("Hibás fájlformátum! Ezzel a programmal csak pdf-et lehet megnyitni!", new Exception(), "error", new JFrame());
            	return false;
            } else {
            	BaseWindow.makeWarning("Pdf fájl elfogadva!", new Exception(), "success", this);
            	return true;
            }
 		}
 		return false;
	}
	
	public void makeFileDetailsWindow(URL fileUrl){
        try {
			openPdf(url);
			if (mitCsere.size()!=mireCsere.size()){
				BaseWindow.makeWarning("Hiba a cserélő algoritmusban!", new Exception(), "error", new JFrame());
			}
			
			saveFile.setEnabled(true);
			editFile.setEnabled(true);
			preview.setEnabled(true);
			cancel.setEnabled(true);
			openFile.setEnabled(false);
			
			for (int i=0; i<mitCsere.size(); i++){
				replace(mitCsere.get(i),mireCsere.get(i));
			}
			
			deleteLine("Tér.kat.");
			
			//finalize();
		
		} catch (IOException e) {
			BaseWindow.makeWarning("Nem tudtam a fájlt megnyitni!", e, "success", new JFrame());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("openFile")){
			if (fileWindow()){
				try {
					makeFileDetailsWindow(url = file.toURI().toURL());
				} catch (MalformedURLException e2) {
					BaseWindow.makeWarning("Hibás a fájl URL!", e2, "error", new JFrame());
				}
			}		
		} else if (cmd.equals("cancel")){
			cancel();
		} else if (cmd.equals("preview")){
			try {
				previewText.setText(print());
				previewText.setCaretPosition(0);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (cmd.equals("saveFile")){
			finalize();
			cancel();
		}
	}
	
	public void deleteLine(String pattern) throws UnsupportedEncodingException, IOException{
		List<?> pages = doc.getDocumentCatalog().getAllPages();
        for( int i=0; i<pages.size(); i++ )
        {
            PDPage page = (PDPage)pages.get( i );
            PDStream contents = page.getContents();
            PDFStreamParser parser = new PDFStreamParser(contents.getStream() );
            parser.parse();
            List<?> tokens = parser.getTokens();
            for( int j=0; j<tokens.size(); j++ )
            {
                Object next = tokens.get( j );
                if( next instanceof PDFOperator )
                {
                    PDFOperator op = (PDFOperator)next;
                    if( op.getOperation().equals( "Tj" ) )
                    {
                        COSString previous = (COSString)tokens.get( j-1 );
                        String string = previous.getString();
                        previous.reset();
                        if (string.indexOf(pattern)==-1){
                            previous.append( string.getBytes("ISO-8859-1") );
                        }
                    }
                    else if( op.getOperation().equals( "TJ" ) )
                    {
                        COSArray previous = (COSArray)tokens.get( j-1 );
                        for( int k=0; k<previous.size(); k++ )
                        {
                            Object arrElement = previous.getObject( k );
                            if( arrElement instanceof COSString )
                            {
                                COSString cosString = (COSString)arrElement;
                                String string = cosString.getString();
                                cosString.reset();
                                if (string.indexOf(pattern)==-1){
                                	 cosString.append( string.getBytes("ISO-8859-1") );
                                }                          
                            }
                        }
                    }
                }
            }
            PDStream updatedStream = new PDStream(doc);
            OutputStream out = updatedStream.createOutputStream();
            ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
            tokenWriter.writeTokens( tokens );
            page.setContents( updatedStream );
        }
	}
	
	public void cancel(){
		close();
		saveFile.setEnabled(false);
		editFile.setEnabled(false);
		preview.setEnabled(false);
		cancel.setEnabled(false);
		openFile.setEnabled(true);
		doc = null;
		inputFile = null;
		previewText.setText(previewString);
		file = null;
		url = null;
		extension = null;
	}
}
