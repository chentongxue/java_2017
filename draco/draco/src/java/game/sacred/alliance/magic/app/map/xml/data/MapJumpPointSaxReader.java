package sacred.alliance.magic.app.map.xml.data;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import sacred.alliance.magic.app.map.point.JumpMapPoint;
import sacred.alliance.magic.app.map.point.JumpMapPointCollection;

public class MapJumpPointSaxReader extends XmlSaxReader{
	JumpMapPointCollection data = new JumpMapPointCollection();
	private JumpMapPoint currJumpMapPoint = null ;
	private String preTag = null ;
	
	private void addJumpMapPoint(JumpMapPoint point){
		if(null == point){
			return ;
		}
		data.getPoint().add(point);
	}
	public JumpMapPointCollection getJumpMapPointCollection(){
		return data ;
	}
	
	 public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException { 
		 if(qName.equals("point")){
			 currJumpMapPoint = new JumpMapPoint();
			 for(int i=0;i<attributes.getLength();i++){
				 String name = attributes.getQName(i);
				 String value = attributes.getValue(i);
				 if("tomapid".equals(name)){
					 currJumpMapPoint.setTomapid(value);
					 continue ;
				 }
				 if("level".equals(name)){
					 currJumpMapPoint.setLevel(this.getInt(value));
					 continue ;
				 }
				 if("desX".equals(name)){
					 currJumpMapPoint.setDesX(this.getInt(value));
					 continue ;
				 }
				 if("desY".equals(name)){
					 currJumpMapPoint.setDesY(this.getInt(value));
					 continue ;
				 }
				 if("questid".equals(name)){
					 currJumpMapPoint.setQuestid(this.getInt(value));
					 continue ;
				 }
				 if("goodsid".equals(name)){
					 currJumpMapPoint.setGoodsid(this.getInt(value));
					 continue ;
				 }
				 if("tocamp".equals(name)){
					 currJumpMapPoint.setTocamp(this.getInt(value));
					 continue ;
				 }
				 if("x".equals(name)){
					 currJumpMapPoint.setX(this.getInt(value));
					 continue ;
				 }
				 if("y".equals(name)){
					 currJumpMapPoint.setY(this.getInt(value));
					 continue ;
				 }
			 }
		 }
		 //将正在解析的节点名称赋给preTag
		 preTag = qName;
	 }
	
	public void endElement(String uri, String localName, String qName)  
            throws SAXException {
		if("point".equals(qName)){  
           this.addJumpMapPoint(this.currJumpMapPoint);
           this.currJumpMapPoint = null ;
        }
        preTag = null;
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException { 
		
	}
	
}
