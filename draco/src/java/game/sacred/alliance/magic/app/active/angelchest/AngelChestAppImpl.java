package sacred.alliance.magic.app.active.angelchest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.active.common.ActiveCommonSupport;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

public class AngelChestAppImpl extends ActiveCommonSupport implements Service,AngelChestApp {
	
	/**
	 * 各分线刷新信息
	 */
	Map<String,MapRefreshStatus> refreshStatusMap = new ConcurrentHashMap<String, MapRefreshStatus>();
	/**
	 * 刷新小时点[0-23]
	 */
	private List<Integer> refreshHourPoints = new ArrayList<Integer>();

	/**
	 * 下次开启时间
	 */
	@Override
	public Date getNextTime(Date date){
		Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.set(Calendar.HOUR_OF_DAY, 0);  
        calendar.set(Calendar.MINUTE, 0);  
        calendar.set(Calendar.SECOND, 0);  
        calendar.set(Calendar.MILLISECOND, 0);  
		int hour = date.getHours() ;
		int max = refreshHourPoints.size()-1 ;
		for(int i=0;i <= max;i++){
			int h = refreshHourPoints.get(i);
			if( h > hour){
				calendar.set(Calendar.HOUR_OF_DAY, h);  
				return calendar.getTime() ;
			}
		}
		//后一天的最前
		calendar.set(Calendar.HOUR_OF_DAY, refreshHourPoints.get(0)); 
		return DateUtil.addDayToDate(calendar.getTime(), 1);
	}
	
	
	/**
	 * 当前开启时间
	 */
	@Override
	public Date getStartTime(Date date){
		Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.set(Calendar.HOUR_OF_DAY, 0);  
        calendar.set(Calendar.MINUTE, 0);  
        calendar.set(Calendar.SECOND, 0);  
        calendar.set(Calendar.MILLISECOND, 0);  
		int hour = date.getHours() ;
		int max = refreshHourPoints.size()-1 ;
		for(int i= max ;i>=0;i--){
			int h = refreshHourPoints.get(i);
			if(hour >= h ){
				calendar.set(Calendar.HOUR_OF_DAY, h);  
				return calendar.getTime() ;
			}
		}
		//前一天的最后
		calendar.set(Calendar.HOUR_OF_DAY, refreshHourPoints.get(max)); 
		return DateUtil.addDayToDate(calendar.getTime(), -1);
	}
	
	
	@Override
	 public MapRefreshStatus getRefreshStatus(String mapId,int lineId){
		 String key = mapId + Cat.underline + lineId ;
		 return this.refreshStatusMap.get(key);
	 }
	 
	@Override
	 public void putRefreshStatus(MapRefreshStatus status){
		 String key = status.getMapId() + Cat.underline + status.getLineId() ;
		 this.refreshStatusMap.put(key, status);
	 }
	
	private void loadRefreshHours(){
		String fileName = XlsSheetNameType.angel_chest_hours.getXlsName();
		String sheetName = XlsSheetNameType.angel_chest_hours.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<String> list = XlsPojoUtil.sheetToStringList(sourceFile, sheetName);
			refreshHourPoints.clear();
			for(String s : list){
				Integer v = Integer.parseInt(s) ;
				if(refreshHourPoints.contains(v)){
					continue ;
				}
				refreshHourPoints.add(v);
			}
			if(Util.isEmpty(refreshHourPoints)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("angel_chest_hours not config: sourceFile = " + fileName + "sheetName = " + sheetName);
				return ;
			}
			//从小到大排序
			Collections.sort(refreshHourPoints, new Comparator<Integer>(){
				@Override
				public int compare(Integer a, Integer b) {
					return a >= b ? 1 :-1 ;
				}
			});
			
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	private void loadMaps(){
		String fileName = XlsSheetNameType.angel_chest_map.getXlsName();
		String sheetName = XlsSheetNameType.angel_chest_map.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<String> list = XlsPojoUtil.sheetToStringList(sourceFile, sheetName);
			if(Util.isEmpty(list)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("angel_chest_map not config: sourceFile = " + fileName + "sheetName = " + sheetName);
				return ;
			}
			for(String mapId : list){
				//判断地图ID是否存在
				sacred.alliance.magic.app.map.Map mapInfo = GameContext.getMapApp().getMap(mapId) ;
				if (null == mapInfo) {
					Log4jManager.checkFail();
					Log4jManager.CHECK
							.error("angel_chest_map config error,mapId="
									+ mapId + " not exist" + " sourceFile="
									+ fileName + "sheetName=" + sheetName);
				} else {
					//修改地图的逻辑类型
					if(!mapInfo.getMapConfig().changeLogicType(MapLogicType.angelChest)){
						Log4jManager.checkFail();
						Log4jManager.CHECK
								.error("angel_chest_map config error change map logicType error,mapId=" + mapId );
					}
				}
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	@Override
	public void start() {
		this.loadRefreshHours();
		this.loadMaps() ;
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void setArgs(Object arg0) {
		
	}
	
}
