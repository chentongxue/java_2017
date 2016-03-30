package sacred.alliance.magic.data;

import java.util.HashMap;
import java.util.Map;

import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;

public class RoleLevelupLoader extends DefaultDataLoader<String,RoleLevelup> {
	
	@Override
	public Map<String,RoleLevelup> loadData(){
		String fileName = "";
		String sheetName = "";
		try {			
			fileName = XlsSheetNameType.role_levelup.getXlsName();
			sheetName = XlsSheetNameType.role_levelup.getSheetName();
			String path = this.getXlsPath();
			String sourceFile = path + fileName;
			Map<String, RoleLevelup> map = XlsPojoUtil.sheetToMap(sourceFile,sheetName, RoleLevelup.class);
			return map;
		} catch (Exception e) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
			return new HashMap<String,RoleLevelup>();
		}
	}

}
