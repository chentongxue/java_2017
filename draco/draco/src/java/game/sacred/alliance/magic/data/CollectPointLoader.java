package sacred.alliance.magic.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.domain.CollectPoint;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;

public class CollectPointLoader extends DefaultDataLoader<String, CollectPoint> {

	@Override
	public Map<String, CollectPoint> loadData() {
		String fileName = "";
		String sheetName = "";
		try {
			fileName = XlsSheetNameType.collect_point.getXlsName();
			sheetName = XlsSheetNameType.collect_point.getSheetName();
			String sourceFile = this.getXlsPath() + fileName;
			List<CollectPoint> list = XlsPojoUtil.sheetToList(sourceFile,sheetName, CollectPoint.class);
			if (list == null || list.size() <= 0) {
				return new HashMap<String, CollectPoint>();
			}
			Map<String, CollectPoint> map = new HashMap<String, CollectPoint>();
			for (CollectPoint collects : list) {
				map.put(collects.getId(), collects);
			}
			return map;
		} catch (Exception e) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
			return new HashMap<String, CollectPoint>();
		}
	}
}
