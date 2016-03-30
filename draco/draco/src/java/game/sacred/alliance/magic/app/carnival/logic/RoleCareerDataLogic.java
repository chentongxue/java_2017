package sacred.alliance.magic.app.carnival.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.carnival.CarnivalRule;
import sacred.alliance.magic.domain.CarnivalDbInfo;
import sacred.alliance.magic.domain.CarnivalRankInfo;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;

public class RoleCareerDataLogic extends RoleDataLogic {
	private static RoleCareerDataLogic instance = new RoleCareerDataLogic();
	public static RoleCareerDataLogic getInstance(){
		return instance ;
	}
	@Override
	public List<CarnivalRankInfo> getCarnivalRank(CarnivalRule carnivalRule, int itemId) {
		Map<String, Map<Integer, CarnivalDbInfo>> allRoleDataMap = GameContext.getCarnivalApp().getAllRoleData();
		return sortRole(allRoleDataMap, itemId);
	}
	
	private List<CarnivalRankInfo> sortRole(Map<String, Map<Integer, CarnivalDbInfo>> allRoleDataMap, int itemId){
		List<CarnivalRankInfo> rankList = new ArrayList<CarnivalRankInfo>();
		try{
			Map<Byte, CarnivalDbInfo> onlineRankMap = new HashMap<Byte, CarnivalDbInfo>();
			if(!Util.isEmpty(allRoleDataMap)) {
				for(String roleId : allRoleDataMap.keySet()) {
					Map<Integer, CarnivalDbInfo> onlineMap = allRoleDataMap.get(roleId);
					if(Util.isEmpty(onlineMap)) {
						continue;
					}
					CarnivalDbInfo info = onlineMap.get(itemId);
					if(null == info) {
						continue;
					}
					
					CarnivalDbInfo roleInfo = onlineRankMap.get(info.getCareer());
					if(null == roleInfo) {
						onlineRankMap.put(info.getCareer(), info);
						continue;
					}
					if(info.getTargetValue() > roleInfo.getTargetValue()) {
						onlineRankMap.put(info.getCareer(), info);
						continue;
					}
					if(info.getTargetValue() == roleInfo.getTargetValue() &&
							info.getSubTargetValue() > roleInfo.getSubTargetValue()) {
						onlineRankMap.put(info.getCareer(), info);
						continue;
					}
				}
			}
			
			List<CarnivalDbInfo> dbRole = GameContext.getCarnivalApp().getCareerActiveDataByColumn(itemId);
			Map<Byte, CarnivalDbInfo> dbRankMap = new HashMap<Byte, CarnivalDbInfo>();
			if(!Util.isEmpty(dbRole)) {
				for(CarnivalDbInfo info : dbRole) {
					dbRankMap.put(info.getCareer(), info); 
				}
			}
			
			List<CarnivalDbInfo> sortList = sortList(onlineRankMap, dbRankMap);
			CarnivalRankInfo rankInfo = null;
			byte index = 1;
			for(CarnivalDbInfo info : sortList){
				rankInfo = new CarnivalRankInfo();
				rankInfo.setCampId(info.getCampId());
				rankInfo.setCareer(info.getCareer());
				rankInfo.setTargetId(info.getTargetId());
				rankInfo.setName(info.getName());
				rankInfo.setTargetValue(info.getTargetValue());
				rankInfo.setRank(index++);
				rankList.add(rankInfo);
			}
		}catch(Exception e){
			logger.error("RoleLevelLogic.sortRole error: ", e);
		}
		return rankList;
	}
	
	private List<CarnivalDbInfo> sortList(Map<Byte, CarnivalDbInfo> onlineMap, Map<Byte, CarnivalDbInfo> dbMap){
		List<CarnivalDbInfo> sortList = new ArrayList<CarnivalDbInfo>();
		/*for(CareerType careerType : CareerType.values()) {
			if(careerType.getType()<0){
				continue ;
			}
			CarnivalDbInfo onlineRole = onlineMap.get(careerType.getType());
			CarnivalDbInfo dbRole = dbMap.get(careerType.getType());
			if(null == onlineRole && null == dbRole) {
				continue;
			}
			if(null == onlineRole && null != dbRole) {
				sortList.add(dbRole);
				continue;
			}
			if(null != onlineRole && null == dbRole) {
				sortList.add(onlineRole);
				continue;
			}
			
			if(onlineRole.getTargetId().equals(dbRole.getTargetId())) {
				sortList.add(onlineRole);
				continue;
			}
			
			if(onlineRole.getTargetValue() > dbRole.getTargetValue()) {
				sortList.add(onlineRole);
				continue;
			}
			
			if(onlineRole.getTargetValue() == dbRole.getTargetValue() &&
					onlineRole.getSubTargetValue() > dbRole.getSubTargetValue()) {
				sortList.add(onlineRole);
				continue;
			}
			sortList.add(dbRole);
		}*/
		return sortList;
	}
}
