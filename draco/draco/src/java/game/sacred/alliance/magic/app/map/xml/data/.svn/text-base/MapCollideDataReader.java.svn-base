package sacred.alliance.magic.app.map.xml.data;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sacred.alliance.magic.app.map.data.MapCollideData;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XmlUtil;


public class MapCollideDataReader {

	
	public static MapCollideData readMapCollideData(String filename) throws Exception{
		try{
			File file = new File(filename);
			if(!file.exists()) {
				//System.err.println(filename + "²»´æÔÚ");
				Log4jManager.CHECK.error(filename + " not exist");
				Log4jManager.checkFail() ;
				return null;
			}
	        String xmlPath = filename.substring(0,
	                filename.lastIndexOf(File.separatorChar) + 1);
	
	        String xmlFile = XmlUtil.makeUrl(filename);
			
			URL url = new URL(xmlFile);
	        InputStream is = url.openStream();
			
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        Document doc;
	        factory.setIgnoringComments(true);
	        factory.setIgnoringElementContentWhitespace(true);
	        factory.setExpandEntityReferences(false);
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        doc = builder.parse(is, xmlPath);
	        
	        MapCollideData mapCollideData = buildMapCollideData(doc);
	        
	        is.close();
	        
	        return mapCollideData;
        
		}catch(Exception e){
//			System.out.println("filename="+filename);
//			System.out.println("exception"+e.toString());
			throw e;
		}
        
	}
	
	private static MapCollideData buildMapCollideData(Document doc) throws Exception {
		
		Node mapCollideDataNode = doc.getDocumentElement();
		
		if (!"mapcollidedata".equals(mapCollideDataNode.getNodeName())) {
			if(!"tilesetlayer".equals(mapCollideDataNode.getNodeName())) {
				throw new Exception("Not a valid collidedata file.");
			}
        }
		
		if("tilesetlayer".equals(mapCollideDataNode.getNodeName())) {
			NodeList list = mapCollideDataNode.getChildNodes();
			for(int i=0;i<list.getLength();i++){			
				Node node = (Node)list.item(i);
				if("mapcollidedata".equals(node.getNodeName())) {
					mapCollideDataNode = node;
				}
			}
		}
		
		int x = XmlUtil.getAttributeInt(mapCollideDataNode, "x", 0);
		int y = XmlUtil.getAttributeInt(mapCollideDataNode, "y", 0);
		
		byte[][] collideData = new byte[x][y];
		
		NodeList list = mapCollideDataNode.getChildNodes();
		for(int i=0,j=0;i<list.getLength();i++){			
			Node node = (Node)list.item(i);
			if(node.getNodeName().equals("cell")){
				String cell = node.getTextContent();
				String[] cells = cell.split(",");
				if(cells!=null && cells.length>0){
					for(int k=0;k<cells.length;k++){
						collideData[j][k] = Byte.parseByte(cells[k]);
					}
					j++;
				}
			}
		}
		return new MapCollideData(collideData,y,x);
	}
}
