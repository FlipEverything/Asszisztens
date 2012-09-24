package centrumlab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;


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
public class CentrumLab implements ActionListener{
	static PDDocument doc = null;
	static String inputFile;
	static JFrame frame;
	static File file;
	JButton centrumlab;
	private URL url;
	private String extension;
	private ArrayList<String> mitCsere;
	private ArrayList<String> mireCsere;
	
	/**
	 * A foprogram
	 * @param args
	 */
	public CentrumLab(){
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

		fileWindow();
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
                        System.out.println(string);
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
	
	
	public static void save(URL url){
		String nev = url.toString().replace("file:/", "").replace(".pdf","_aranyklinika.pdf").replaceAll("%20"," ");
		try {
			doc.save( nev );
			BaseWindow.makeWarning("Sikeresen lérehoztam az aranyklinikásított fájlt!\r\n"+nev, new Exception(), "success", new JFrame());
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

	public void fileWindow(){
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new PdfOpenFilter());
		// TODO
 		if (fc.showOpenDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            extension = file.toString().substring((int)file.toString().length()-3, (int)file.toString().length());
            extension = extension.toLowerCase();
            if (!(extension.equals("pdf"))){
            	BaseWindow.makeWarning("Hibás fájlformátum! Ezzel a programmal csak pdf-et lehet megnyitni!", new Exception(), "error", new JFrame());
            } else {
            	try {
					makeFileDetailsWindow(url = file.toURI().toURL());
				} catch (MalformedURLException e) {
					BaseWindow.makeWarning("Hibás a fájl URL!", e, "error", new JFrame());
				}
            }		
 		}
	}
	
	public void makeFileDetailsWindow(URL fileUrl){
        try {
			openPdf(url);
			if (mitCsere.size()!=mireCsere.size()){
				BaseWindow.makeWarning("Hiba a cserélő algoritmusban!", new Exception(), "error", new JFrame());
			}
			
			
			for (int i=0; i<mitCsere.size(); i++){
				replace(mitCsere.get(i),mireCsere.get(i));
			}
			
			finalize();
		
		} catch (IOException e) {
			BaseWindow.makeWarning("Nem tudtam a fájlt megnyitni!", e, "success", new JFrame());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
}
