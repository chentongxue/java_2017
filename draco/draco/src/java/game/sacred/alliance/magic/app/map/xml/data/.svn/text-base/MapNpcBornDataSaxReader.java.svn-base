package sacred.alliance.magic.app.map.xml.data;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import sacred.alliance.magic.app.map.data.MapNpcBornData;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.util.XmlUtil;

import com.google.common.collect.Lists;

public class MapNpcBornDataSaxReader extends XmlSaxReader{
	MapNpcBornData mapNpcBornData = new MapNpcBornData();
	private NpcBorn currNpcBorn = null ;
	private String preTag = null ;
	
	public MapNpcBornData getMapNpcBornData(){
		return mapNpcBornData ;
	}
	
	private void addNpcBorn(NpcBorn npcBorn){
		List<NpcBorn> npcBornList = mapNpcBornData.getNpcborn();
		if(null == npcBornList){
			npcBornList = Lists.newArrayList() ;
			mapNpcBornData.setNpcborn(npcBornList);
		}
		npcBornList.add(npcBorn) ;
	}
	
	
	 public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException { 
		 if(qName.equals("npcborn")){
			 currNpcBorn = new NpcBorn();
			 for(int i=0;i<attributes.getLength();i++){
				 String name = attributes.getQName(i);
				 String value = attributes.getValue(i);
				 if("bornNpcDir".equals(name)){
					 currNpcBorn.setBornNpcDir(this.getInt(value));
					 continue ;
				 }
				 if("bornmapgxbegin".equals(name)){
					 currNpcBorn.setBornmapgxbegin(this.getInt(value));
					 continue ;
				 }
				 if("bornmapgybegin".equals(name)){
					 currNpcBorn.setBornmapgybegin(this.getInt(value));
					 continue ;
				 }
				 if("bornmapgxend".equals(name)){
					 currNpcBorn.setBornmapgxend(this.getInt(value));
					 continue ;
				 }
				 if("bornmapgyend".equals(name)){
					 currNpcBorn.setBornmapgyend(this.getInt(value));
					 continue ;
				 }
				 if("bornnpccount".equals(name)){
					 currNpcBorn.setBornnpccount(this.getInt(value));
					 continue ;
				 }
				 if("bornnpcid".equals(name)){
					 currNpcBorn.setBornnpcid(value);
					 continue ;
				 }
				 if("minrefreshsecond".equals(name)){
					 currNpcBorn.setMinrefreshsecond(this.getInt(value));
					 continue ;
				 }
				 if("maxrefreshsecond".equals(name)){
					 currNpcBorn.setMaxrefreshsecond(this.getInt(value));
					 continue ;
				 }
				 if("starttime".equals(name)){
					 currNpcBorn.setStarttime(value);
					 continue ;
				 }
				 if("endtime".equals(name)){
					 currNpcBorn.setEndtime(value);
					 continue ;
				 }
				 if("pathtype".equals(name)){
					 currNpcBorn.setPathtype(this.getInt(value));
					 continue ;
				 }
				 if("pathid".equals(name)){
					 currNpcBorn.setPathid(value);
					 continue ;
				 }
				 if("pal".equals(name)){
					 currNpcBorn.setPal(Byte.parseByte(value));
					 continue ;
				 }
				 if("mmp".equals(name)){
					 currNpcBorn.setMmp(Byte.parseByte(value));
					 continue ;
				 }
				 if("startdate".equals(name)){
					 currNpcBorn.setStartdate(this.getInt(value, currNpcBorn.getStartdate()));
					 continue ;
				 }
				 if("enddate".equals(name)){
					 currNpcBorn.setEnddate(this.getInt(value, currNpcBorn.getEnddate()));
					 continue ;
				 }
				 if("refreshcycle".equals(name)){
					 currNpcBorn.setRefreshcycle(this.getInt(value));
					 continue ;
				 }
				 if("isDisappear".equals(name)){
					 currNpcBorn.setIsDisappear(this.getInt(value));
					 continue ;
				 }
			 }
		 }
		 //将正在解析的节点名称赋给preTag
		 preTag = qName;
	 }
	
	public void endElement(String uri, String localName, String qName)  
            throws SAXException {
		if("npcborn".equals(qName)){  
           this.addNpcBorn(this.currNpcBorn);
           this.currNpcBorn = null ;
        }
        preTag = null;
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException { 
		
	}
	
}
