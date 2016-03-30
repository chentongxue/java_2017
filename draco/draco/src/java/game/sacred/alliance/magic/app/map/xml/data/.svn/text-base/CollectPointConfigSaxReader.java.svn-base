package sacred.alliance.magic.app.map.xml.data;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import sacred.alliance.magic.app.map.point.CollectPointConfig;
import sacred.alliance.magic.app.map.point.PointNode;
import sacred.alliance.magic.vo.Point;

public class CollectPointConfigSaxReader extends XmlSaxReader{

	CollectPointConfig config = new CollectPointConfig();
	private PointNode currNode = null ;
	private String preTag = null ;
	
	public void addPoint(Point p){
		if(null == p){
			return ;
		}
		this.currNode.getPoint().add(p);
	}
	
	private void addNode(PointNode node){
		if(null == node){
			return ;
		}
		config.getNodes().add(node);
	}
	
	public CollectPointConfig getCollectPointConfig(){
		return config ;
	}
	
	 public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		 if(qName.equals("nodes")){
			 currNode = new PointNode();
			 for(int i=0;i<attributes.getLength();i++){
				 String name = attributes.getQName(i);
				 String value = attributes.getValue(i);
				 if("id".equals(name)){
					 currNode.setId(value);
					 continue ;
				 }
			 }
		 }else if(qName.equals("point")){
			 Point p = new Point() ;
			 for(int i=0;i<attributes.getLength();i++){
				 String name = attributes.getQName(i);
				 String value = attributes.getValue(i);
				 if("x".equals(name)){
					 p.setX(this.getInt(value));
					 continue ;
				 }
				 if("y".equals(name)){
					 p.setY(this.getInt(value));
					 continue ;
				 }
			 }
			 this.addPoint(p);
		 }
		 //将正在解析的节点名称赋给preTag
		 preTag = qName;
	 }
	
	public void endElement(String uri, String localName, String qName)  
            throws SAXException {
		if("nodes".equals(qName)){  
           this.addNode(this.currNode);
           this.currNode = null ;
        }
        preTag = null;
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException { 
		
	}
	
}
