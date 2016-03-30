package sacred.alliance.magic.app.fall;

import java.util.Map;

import sacred.alliance.magic.util.XlsPojoUtil;

public class LootLoader {

	private static final String GROUP_SHEET = "group" ;
	private static final String LIST_SHEET = "list" ;
	public static Map<String, LootList> loadLootList(String xlsName){
		Map<String, LootGroup> lootGroupMap = XlsPojoUtil.sheetToMap(xlsName,GROUP_SHEET, LootGroup.class);
		if(null != lootGroupMap && lootGroupMap.size() != 0){
			for(LootGroup lg: lootGroupMap.values()){
				lg.init();
			}
		}
		
		Map<String, LootList> lootListMap = XlsPojoUtil.sheetToMap(xlsName,LIST_SHEET, LootList.class);
		if(null != lootListMap && lootListMap.size() != 0){
			for(LootList ll: lootListMap.values()){
				ll.init(lootGroupMap);
			}
		}
		
		lootGroupMap.clear();
		lootGroupMap = null ;
		
		return lootListMap ;
	}
}
