package sacred.alliance.magic.app.map.xml.data;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import sacred.alliance.magic.app.map.data.MapConfig;

public class MapConfigSaxReader extends XmlSaxReader{
	MapConfig mapConfig = new MapConfig();
	
	public MapConfig getMapConfig(){
		return mapConfig ;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException { 
		 if(qName.equals("mapconfig")){
			 for(int i=0;i<attributes.getLength();i++){
				 String name = attributes.getQName(i);
				 String value = attributes.getValue(i);
				 if("maplogicid".equals(name)){
					 mapConfig.setMaplogicid(this.getInt(value));
					 continue ;
				 }
				 /*if("samecampcanpk".equals(name)){
					 mapConfig.setSamecampcanpk(Boolean.parseBoolean(value));
					 continue ;
				 }
				 if("diffcampcanpk".equals(name)){
					 mapConfig.setDiffcampcanpk(Boolean.parseBoolean(value));
					 continue ;
				 }*/
				 if("cantroop".equals(name)){
					 mapConfig.setCantroop(Boolean.parseBoolean(value));
					 continue ;
				 }
				 if("logictype".equals(name)){
					 mapConfig.setLogictype(Byte.parseByte(value));
					 continue ;
				 }
				 if("deathjumpmapid".equals(name)){
					 mapConfig.setDeathjumpmapid(value);
					 continue ;
				 }
				 if("reloginjumpmapid".equals(name)){
					mapConfig.setReloginjumpmapid(value);
					 continue ;
				 }
				 if("maporiginx".equals(name)){
					 mapConfig.setMaporiginx(this.getInt(value));
					 continue ;
				 }
				 if("maporiginy".equals(name)){
					 mapConfig.setMaporiginy(this.getInt(value));
					 continue ;
				 }
				 if("maptype".equals(name)){
					 mapConfig.setMaptype(Byte.parseByte(value));
					 continue ;
				 }
				 if("mapweather".equals(name)){
					 mapConfig.setMapweather(Byte.parseByte(value));
					 continue ;
				 }
				 if("maplevelname".equals(name)){
					 mapConfig.setMaplevelname(value);
					 continue ;
				 }
				/* if("mapmusicindex".equals(name)){
					 mapConfig.setMapmusicindex(this.getInt(value,-1));
					 continue ;
				 }*/
				 /*if("isEquestrian".equals(name)){
					 mapConfig.setIsEquestrian(Byte.parseByte(value));
					 continue ;
				 }*/
			 }
		 }
	 }
	
	
}
