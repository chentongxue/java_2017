package sacred.alliance.magic.app.map.xml.data;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.game.draco.message.item.MapGetDataPluginItem;

import sacred.alliance.magic.app.map.data.MapImageAssociate;
import sacred.alliance.magic.app.map.data.MapRoadPointVO;
import sacred.alliance.magic.app.map.data.MapRoadVO;
import sacred.alliance.magic.app.map.data.MapRoadVOConfig;
import sacred.alliance.magic.app.map.data.TilesetLayer;
import sacred.alliance.magic.util.FileUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XmlUtil;


public class MapImageAssociateReader {

	
	public static MapImageAssociate readMapImageAssociate(String mapImageAssociatePath, String roadblockPath, 
			String pluginPath, String roadblockXmlPath) throws Exception{
		try{
	        String xmlPath = mapImageAssociatePath.substring(0,mapImageAssociatePath.lastIndexOf(File.separatorChar) + 1);
	
	        String xmlFile = XmlUtil.makeUrl(mapImageAssociatePath);
			
			URL url = new URL(xmlFile);
	        InputStream is = url.openStream();
			
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        Document doc;
	        factory.setIgnoringComments(true);
	        factory.setIgnoringElementContentWhitespace(true);
	        factory.setExpandEntityReferences(false);
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        doc = builder.parse(is, xmlPath);
	        
	        MapImageAssociate mapImageAssociate = buildMapImageAssociate(doc, roadblockPath, pluginPath);
	        
	        is.close();
	        //载入maproadpoint
	        File mapRoadVOFile = new File(roadblockXmlPath);
	        if(mapRoadVOFile.exists()){
	        	MapRoadVOConfig mapRoadVOConfig = XmlUtil.loadFromXml(roadblockXmlPath, MapRoadVOConfig.class);
	    		List<MapRoadPointVO> pointNodeList = mapRoadVOConfig.getPoints();
	    		if(!Util.isEmpty(pointNodeList)){
	    			HashMap<Integer, MapRoadPointVO> map = new HashMap<Integer, MapRoadPointVO>();
		    		for(MapRoadPointVO pointNode : pointNodeList) {
		    			if(pointNode.getConnectList().size() == 0){
		    				continue;
		    			}
		    			map.put(pointNode.getId(), pointNode);
		    		}
		    		MapRoadVO mapRoadVO = new MapRoadVO();
		    		mapRoadVO.setPoints(map);
		    		mapImageAssociate.setMapRoadVO(mapRoadVO);
	    		}
	        }
	       /* else {
	        	Log4jManager.CHECK.error(" maproadpoint donot exsist , path=" + roadblockXmlPath);
				Log4jManager.checkFail();
	        }*/
	        return mapImageAssociate;
        
		}catch(Exception e){
			throw e;
		}
	}
	
	private static MapImageAssociate buildMapImageAssociate(Document doc, 
			String roadblockPath, String pluginPath) throws Exception {
		
		Node mapImageAssociateNode = doc.getDocumentElement();
		
		if (!"mapimageassociate".equals(mapImageAssociateNode.getNodeName())) {
            throw new Exception("Not a valid collidedata file.");
        }
		
		String mapId = XmlUtil.getAttributeString(mapImageAssociateNode, "mapid");
		int width = XmlUtil.getAttributeInt(mapImageAssociateNode, "width", 0);
		int height = XmlUtil.getAttributeInt(mapImageAssociateNode, "height", 0);
		int mapDataType = XmlUtil.getAttributeInt(mapImageAssociateNode, "mapdatatype", 0);
		String mapGroundRes = XmlUtil.getAttributeString(mapImageAssociateNode, "mapgroundres");
		
		byte[][] mapIndex = null;
		byte[][] reverseType = null;
		byte[][] buildReverseType = null;
		List<TilesetLayer> tilesetLayers = new ArrayList<TilesetLayer>();
		List<MapGetDataPluginItem> pluginItems = new ArrayList<MapGetDataPluginItem>() ;
		
		if(mapDataType == 0) {//读入老资源
			NodeList list = mapImageAssociateNode.getChildNodes();
			for(int i=0;i<list.getLength();i++){
				Node node = (Node)list.item(i);
				if(node.getNodeName().equals("mapindexdata")){
					mapIndex = buildByte(node,"mapindex");
				}
				if(node.getNodeName().equals("reversetypedata")){
					reverseType = buildByte(node,"reversetype");
				}
				if(node.getNodeName().equals("layer")){
					buildPlugin(node, pluginItems);//mapPlugin = buildPlugin(node, mapPlugin);
				}
			}
			width = mapIndex[0].length;
			height = mapIndex.length;
			tilesetLayers.add(new TilesetLayer(width, height, mapIndex, reverseType, 0, mapGroundRes));
		}else if(mapDataType == 1) {
			//读入是新地图资源
			buildMapDataNew(mapImageAssociateNode, mapIndex, reverseType, tilesetLayers, pluginItems);
		}else if(mapDataType == 2) {
			buildMapDataMixTure(mapImageAssociateNode, mapIndex, buildReverseType, tilesetLayers, pluginItems, pluginPath);
		}
		
		File roadblockFile = new File(roadblockPath);
		byte[] roadblock = null;
		if(roadblockFile.exists()){
			roadblock = FileUtil.readByteData(roadblockFile);
		}
		return new MapImageAssociate(mapId, pluginItems, mapGroundRes, mapDataType, tilesetLayers, roadblock);
	}
	
	
	private static List<MapGetDataPluginItem> buildPlugin(Node fatherNode, List<MapGetDataPluginItem> mapPlugin) throws Exception{
		NodeList list = fatherNode.getChildNodes();
		if(null == list) return mapPlugin;
		for(int i=0;i<list.getLength();i++){			
			Node node = (Node)list.item(i);
			if(!(node instanceof Element)){
				continue ;
			}
			MapGetDataPluginItem item = new MapGetDataPluginItem();
			int flags = XmlUtil.getAttributeInt(node, "flags", 0);
			int posx = XmlUtil.getAttributeInt(node, "posx", 0);
			int posy = XmlUtil.getAttributeInt(node, "posy", 0);
			int classid = XmlUtil.getAttributeInt(node, "classid", 0);
			int spriteAnimId = XmlUtil.getAttributeInt(node, "anim", 0);
			int pal = XmlUtil.getAttributeInt(node, "pal", 0);
			item.setAnim((short)spriteAnimId);
			item.setClassid((short)classid);
			item.setFlags(flags);
			item.setPosx((short)posx);
			item.setPosy((short)posy);
			item.setPal((byte)pal);
			mapPlugin.add(item);
		}
		return mapPlugin;
	}
	
	private static byte[][] buildByte(Node fatherNode,String nodeName) throws Exception{
		int x = XmlUtil.getAttributeInt(fatherNode, "x", 0);
		int y = XmlUtil.getAttributeInt(fatherNode, "y", 0);
		
		byte[][] byteData = new byte[x][y];
		
		NodeList list = fatherNode.getChildNodes();
		for(int i=0,j=0;i<list.getLength();i++){			
			Node node = (Node)list.item(i);
			if(!(node instanceof Element)){
				continue ;
			}
			if(node.getNodeName().equals(nodeName)){
				String content = node.getTextContent();
				String[] contents = content.split(",");
				if(contents!=null && contents.length>0){
					for(int k=0;k<contents.length;k++){
						byteData[j][k] = Byte.parseByte(contents[k]);
					}
					j++;
				}
			}
		}
		return byteData;		
	}
	
	private static void buildMapDataNew(Node mapImageAssociateNode
			, byte[][] mapIndex
			, byte[][] reverseType
			, List<TilesetLayer> tilesetLayers
			, List<MapGetDataPluginItem> mapPlugin) {
		try {
			NodeList list = mapImageAssociateNode.getChildNodes();
			for(int i=0;i<list.getLength();i++){
				Node node = (Node)list.item(i);
				if(node.getNodeName().equals("tilesetlayer")) {
					String mapGroundRes = XmlUtil.getAttributeString(node, "mapgroundres");
					int mapType = XmlUtil.getAttributeInt(node, "maptype", 0);
					int tilesetWidth = XmlUtil.getAttributeInt(node, "width", 0);
					int tilesetHeight = XmlUtil.getAttributeInt(node, "height", 0);
					
					NodeList tilesetNodeList = node.getChildNodes();
					for(int j = 0; j < tilesetNodeList.getLength(); j++) {
						Node tilesetNode = (Node)tilesetNodeList.item(j);
						if(tilesetNode.getNodeName().equals("mapindexdata")) {
							mapIndex = buildByte(tilesetNode,"mapindex");
						}
						if(tilesetNode.getNodeName().equals("reversetypedata")) {
							reverseType = buildByte(tilesetNode,"reversetype");
						}
					}
					tilesetLayers.add(new TilesetLayer(tilesetWidth, tilesetHeight, mapIndex, reverseType, mapType,mapGroundRes));
				}
				if(node.getNodeName().equals("layer")){
					buildPlugin(node, mapPlugin);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("load map new data error");
			e.printStackTrace();
		}
	}
	
	private static void buildMapDataMixTure(Node mapImageAssociateNode
			, byte[][] mapIndex
			, byte[][] reverseType
			, List<TilesetLayer> tilesetLayers
			, List<MapGetDataPluginItem> mapPlugin, String pluginFile) {
		try {
			//读入老数据
			Node node;
			NodeList list;
//			try {
//				String xmlPath = oldFileName.substring(0,oldFileName.lastIndexOf(File.separatorChar) + 1);
//				
//		        String xmlFile = XmlUtil.makeUrl(oldFileName);
//				
//				URL url = new URL(xmlFile);
//		        InputStream is = url.openStream();
//				
//		        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		        Document doc;
//		        factory.setIgnoringComments(true);
//		        factory.setIgnoringElementContentWhitespace(true);
//		        factory.setExpandEntityReferences(false);
//		        DocumentBuilder builder = factory.newDocumentBuilder();
//		        doc = builder.parse(is, xmlPath);
//				Node oldMapImageAssociateNode = doc.getDocumentElement();
//				int width;
//				int height;
//				String mapGroundRes = XmlUtil.getAttributeString(oldMapImageAssociateNode, "mapgroundres");
//				//读入是老地图资源
//				list = oldMapImageAssociateNode.getChildNodes();
//				for(int i=0;i<list.getLength();i++){
//					node = (Node)list.item(i);
//					if(node.getNodeName().equals("mapindexdata")){
//						mapIndex = buildByte(node,"mapindex");
//					}
//					if(node.getNodeName().equals("reversetypedata")){
//						reverseType = buildByte(node,"reversetype");
//					}
//				}
//				width = mapIndex[0].length;
//				height = mapIndex.length;
//				tilesetLayers.add(new TilesetLayer( width, height, mapIndex, reverseType, 0, mapGroundRes));
//				
//				is.close();
//			} catch (Exception e) {
//				System.out.println("mapDataType = =2, load old data error");
//				e.printStackTrace();
//			}
			
			
			//读新的地图
			list = mapImageAssociateNode.getChildNodes();
			for(int i=0;i<list.getLength();i++){
				node = (Node)list.item(i);
				if(node.getNodeName().equals("tilesetlayer")) {
					String mapNewGroundRes = XmlUtil.getAttributeString(node, "mapgroundres");
					int mapType = XmlUtil.getAttributeInt(node, "maptype", 0);
					int tilesetWidth = XmlUtil.getAttributeInt(node, "width", 0);
					int tilesetHeight = XmlUtil.getAttributeInt(node, "height", 0);
					
					NodeList tilesetNodeList = node.getChildNodes();
					for(int j = 0; j < tilesetNodeList.getLength(); j++) {
						Node tilesetNode = (Node)tilesetNodeList.item(j);
						if(tilesetNode.getNodeName().equals("mapindexdata")) {
							mapIndex = buildByte(tilesetNode,"mapindex");
						}
						if(tilesetNode.getNodeName().equals("reversetypedata")) {
							reverseType = buildByte(tilesetNode,"reversetype");
						}
					}
					tilesetLayers.add(new TilesetLayer(tilesetWidth, tilesetHeight,  mapIndex, reverseType, mapType, mapNewGroundRes));
				}
			}
			
			//读入插件
			String xmlPath = pluginFile.substring(0,pluginFile.lastIndexOf(File.separatorChar) + 1);
	        String xmlFile = XmlUtil.makeUrl(pluginFile);
			URL url = new URL(xmlFile);
	        InputStream is = url.openStream();
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        Document doc;
	        factory.setIgnoringComments(true);
	        factory.setIgnoringElementContentWhitespace(true);
	        factory.setExpandEntityReferences(false);
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        doc = builder.parse(is, xmlPath);
			Node pluginNode = doc.getDocumentElement();
			if(pluginNode.getNodeName().equals("layer")){
				buildPlugin(pluginNode, mapPlugin);//mapPlugin = buildPlugin(node, mapPlugin);
			}
			is.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("load map new data error");
			e.printStackTrace();
		}
	}
}
