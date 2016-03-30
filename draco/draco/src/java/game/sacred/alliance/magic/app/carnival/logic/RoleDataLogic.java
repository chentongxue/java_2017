package sacred.alliance.magic.app.carnival.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.carnival.CarnivalRule;
import sacred.alliance.magic.domain.CarnivalDbInfo;
import sacred.alliance.magic.domain.CarnivalRankInfo;
import sacred.alliance.magic.util.Util;

public class RoleDataLogic extends CarnivalLogic {
	private static RoleDataLogic instance = new RoleDataLogic();
	public static RoleDataLogic getInstance(){
		return instance ;
	}
	@Override
	public List<CarnivalRankInfo> getCarnivalRank(CarnivalRule carnivalRule, int itemId) {
		Map<String, Map<Integer, CarnivalDbInfo>> allRoleDataMap = GameContext.getCarnivalApp().getAllRoleData();
		return sortRole(allRoleDataMap, itemId);
	}
	@Override
	public Collection<CarnivalRankInfo> getCarnivalReward(CarnivalRule carnivalRule, int itemId) {
		Map<String, CarnivalRankInfo> rewardMap = new HashMap<String, CarnivalRankInfo>();
		try{
			Map<String, Map<Integer, CarnivalDbInfo>> allRoleDataMap = GameContext.getCarnivalApp().getAllRoleData();
			CarnivalRankInfo rankInfo = null;
			byte index = ALL_REWARD_RANK;
			for(String roleId : allRoleDataMap.keySet()) {
				Map<Integer, CarnivalDbInfo> dbMap = allRoleDataMap.get(roleId);
				if(Util.isEmpty(dbMap)) {
					continue;
				}
				CarnivalDbInfo info = dbMap.get(itemId);
				if(null == info) {
					continue;
				}
				if(!isMeet(carnivalRule, info.getTargetValue())) {
					continue;
				}
				
				rankInfo = new CarnivalRankInfo();
				rankInfo.setCareer(info.getCareer());
				rankInfo.setTargetId(info.getTargetId());
				rankInfo.setRank(index);
				rewardMap.put(info.getTargetId(), rankInfo);
				printRewardLog(carnivalRule.getId(), info.getTargetId(), info.getName());
			}
			
			List<CarnivalDbInfo> dbRole = GameContext.getCarnivalApp().getActiveData(itemId, carnivalRule.getMinValue());
			if(!Util.isEmpty(dbRole)) {
				for(CarnivalDbInfo info : dbRole) {
					if(rewardMap.containsKey(info.getTargetId())) {
						continue;
					}
					rankInfo = new CarnivalRankInfo();
					rankInfo.setCareer(info.getCareer());
					rankInfo.setTargetId(info.getTargetId());
					rankInfo.setRank(index);
					rewardMap.put(info.getTargetId(), rankInfo);
					printRewardLog(carnivalRule.getId(), info.getTargetId(), info.getName());
				}
			}
		}catch(Exception e){
			logger.error("RoleLevelLogic.getCarnivalReward error: ", e);
		}
		return rewardMap.values();
	}
	
	private List<CarnivalRankInfo> sortRole(Map<String, Map<Integer, CarnivalDbInfo>> allRoleDataMap, int itemId){
		List<CarnivalRankInfo> rankList = new ArrayList<CarnivalRankInfo>();
		try{
			List<CarnivalDbInfo> list = new ArrayList<CarnivalDbInfo>();
			if(!Util.isEmpty(allRoleDataMap)) {
				for(String roleId : allRoleDataMap.keySet()) {
					Map<Integer, CarnivalDbInfo> dbMap = allRoleDataMap.get(roleId);
					if(Util.isEmpty(dbMap)) {
						continue;
					}
					CarnivalDbInfo info = dbMap.get(itemId);
					if(null == info) {
						continue;
					}
					list.add(info);
				}
				sortList(list);
			}
			
			int rankSize = Util.getSubListSize(list.size(), RANK_SIZE);
			List<CarnivalDbInfo> onlineList = list.subList(0, rankSize);
			List<CarnivalDbInfo> dbRole = GameContext.getCarnivalApp().getActiveDataBySize(itemId,0,RANK_SIZE);
			
			Map<String, CarnivalDbInfo> roleMap = new HashMap<String, CarnivalDbInfo>();
			if(!Util.isEmpty(onlineList)) {
				for(CarnivalDbInfo info : onlineList) {
					roleMap.put(info.getTargetId(), info);
				}
			}
			
			if(!Util.isEmpty(dbRole)) {
				for(CarnivalDbInfo info : dbRole) {
					if(roleMap.containsKey(info.getTargetId())) {
						continue;
					}
					roleMap.put(info.getTargetId(), info);
				}
			}
			
			List<CarnivalDbInfo> tempList = new ArrayList<CarnivalDbInfo>();
			tempList.addAll(roleMap.values());
			
			sortList(tempList);
			rankSize = Util.getSubListSize(tempList.size(), RANK_SIZE);
			List<CarnivalDbInfo> sortList = tempList.subList(0, rankSize);
			CarnivalRankInfo rankinfo = null;
			byte index = 1;
			for(CarnivalDbInfo info : sortList){
				rankinfo = new CarnivalRankInfo();
				rankinfo.setCampId(info.getCampId());
				rankinfo.setCareer(info.getCareer());
				rankinfo.setTargetId(info.getTargetId());
				rankinfo.setName(info.getName());
				rankinfo.setTargetValue(info.getTargetValue());
				rankinfo.setRank(index++);
				rankList.add(rankinfo);
			}
		}catch(Exception e){
			logger.error("RoleLevelLogic.sortRole error: ", e);
		}
		return rankList;
	}
	
	private void sortList(List<CarnivalDbInfo> list){
		Collections.sort(list, new Comparator<CarnivalDbInfo>() {
			public int compare(CarnivalDbInfo info1, CarnivalDbInfo info2) {
				int TargetValue1 = info1.getTargetValue();
				int TargetValue2 = info2.getTargetValue();
				int subTargetValue1 = info1.getSubTargetValue();
				int subTargetValue2 = info2.getSubTargetValue();
				if(TargetValue1 > TargetValue2) {
					return -1;
				}
				if(TargetValue1 == TargetValue2 && subTargetValue1 > subTargetValue2) {
					return -1;
				}
				if(info1.getTargetValue() < info2.getTargetValue()) {
					return 1;
				}
				return 0;
			}
		});
	}
}
