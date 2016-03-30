package sacred.alliance.magic.app.map.xml.data;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlSaxReader extends DefaultHandler{
	protected ThreadLocal<SAXParser> parser = new ThreadLocal<SAXParser>() {
		protected SAXParser initialValue() {
			SAXParser newParser = null;
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			try {
				newParser = factory.newSAXParser();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			return newParser;
		};
	};

	public void parse(String fileName) throws Exception {
		parser.get().parse(new File(fileName), this);
	}
	
	protected int getInt(String value,int defValue){
		if(null == value || 0 == value.trim().length()){
			return defValue ;
		}
		return Integer.parseInt(value);
	}
	
	protected int getInt(String value){
		return getInt(value,0);
	}
}
