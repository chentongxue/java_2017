package sacred.alliance.magic.app.carnival.logic;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sacred.alliance.magic.app.carnival.CarnivalRule;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.CarnivalRankInfo;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class RoleMountLogic extends CarnivalLogic {
	private static RoleMountLogic instance = new RoleMountLogic();
	
	public static RoleMountLogic getInstance(){
		return instance ;
	}
	@Override
	public List<CarnivalRankInfo> getCarnivalRank(CarnivalRule carnivalRule, int itemId) {
		return sortRole(GameContext.getOnlineCenter().getAllOnlineRole(), carnivalRule.getCarnivalType().getSubAttriType());
	}
	
	/**
	 * 获取排行
	 * @param roleList
	 * @param carnivalType
	 * @return
	 */
	private List<CarnivalRankInfo> sortRole(Collection<RoleInstance> roleList, AttributeType subAtt){
		return null ;
		/*
		List<CarnivalRankInfo> rankList = new ArrayList<CarnivalRankInfo>();
		try{
			List<RoleInstance> list = new ArrayList<RoleInstance>();
			list.addAll(roleList);
			sortList(list, subAtt);
			int rankSize = Util.getSubListSize(list.size(), RANK_SIZE);
			List<RoleInstance> onlineList = list.subList(0, rankSize);
			List<CarnivalRankInfo> onlineRankList = new ArrayList<CarnivalRankInfo>();
			CarnivalRankInfo onlineInfo = null;
			for(RoleInstance role : onlineList) {
				if(null == role){
					continue;
				}
				RoleMount roleMount = role.getRoleMount();
				if(null == roleMount) {
					continue;
				}
				onlineInfo = new CarnivalRankInfo();
				onlineInfo.setCampId(role.getCampId());
				onlineInfo.setCareer(role.getCareer());
				onlineInfo.setTargetId(role.getRoleId());
				onlineInfo.setName(role.getRoleName());
				onlineInfo.setTargetValue(roleMount.getBattleScore());
				onlineRankList.add(onlineInfo);
			}
			
			List<CarnivalRankInfo> dbRankList = GameContext.getCarnivalApp().getRoleMonutSort(0, RANK_SIZE);
			
			Map<String, CarnivalRankInfo> roleMap = new HashMap<String, CarnivalRankInfo>();
			if(!Util.isEmpty(onlineRankList)) {
				for(CarnivalRankInfo info : onlineRankList) {
					roleMap.put(info.getTargetId(), info);
				}
			}
			
			if(!Util.isEmpty(dbRankList)) {
				for(CarnivalRankInfo dbInfo : dbRankList) {
					if(roleMap.containsKey(dbInfo.getTargetId())) {
						continue;
					}
					roleMap.put(dbInfo.getTargetId(), dbInfo);
				}
			}
			
			List<CarnivalRankInfo> tempList = new ArrayList<CarnivalRankInfo>();
			tempList.addAll(roleMap.values());
			sortRankList(tempList);
			rankSize = Util.getSubListSize(tempList.size(), RANK_SIZE);
			List<CarnivalRankInfo> sortList = tempList.subList(0, rankSize);
			byte index = 1;
			for(CarnivalRankInfo info : sortList){
				info.setRank(index++);
				rankList.add(info);
			}
		}catch(Exception e){
			logger.error("RoleMountLogic.sortRole error: ", e);
		}
		return rankList;
	*/}
	
	private void sortRankList(List<CarnivalRankInfo> list){
		Collections.sort(list, new Comparator<CarnivalRankInfo>() {
			public int compare(CarnivalRankInfo info1, CarnivalRankInfo info2) {
				if(info1.getTargetValue() > info2.getTargetValue()) {
					return -1;
				}
				if(info1.getTargetValue() < info2.getTargetValue()) {
					return 1;
				}
				return 0;
			}
		});
	}
	
	/*private void sortList(List<RoleInstance> list, final AttributeType subAttriType){
		Collections.sort(list, new Comparator<RoleInstance>() {
			public int compare(RoleInstance role1, RoleInstance role2) {
				RoleMount roleMount1 = role1.getRoleMount();
				RoleMount roleMount2 = role2.getRoleMount();
				if(null == roleMount1 || null == roleMount2) {
					return 1;
				}
				int score1 = roleMount1.getBattleScore();
				int score2 = roleMount2.getBattleScore();
				if(score1 > score2) {
					return -1;
				}
				if(score1 == score2 && getAttri(role1, subAttriType) > getAttri(role2, subAttriType)) {
					return -1;
				}
				if(score2 < score2) {
					return 1;
				}
				return 0;
			}
		});
	}*/
	
	@Override
	public Collection<CarnivalRankInfo> getCarnivalReward(CarnivalRule carnivalRule, int itemId) {
		return null ;
		
		/*Map<String, CarnivalRankInfo> rewardMap = new HashMap<String, CarnivalRankInfo>();
		try{
			CarnivalRankInfo info = null;
			byte index = ALL_REWARD_RANK;
			for(RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()) {
				RoleMount roleMount = role.getRoleMount();
				if(null == roleMount){
					continue;
				}
				if(!isMeet(carnivalRule, roleMount.getBattleScore())) {
					continue;
				}
				info = new CarnivalRankInfo();
				info.setCareer(role.getCareer());
				info.setCampId(role.getCampId());
				info.setTargetId(role.getRoleId());
				info.setRank(index);
				rewardMap.put(role.getRoleId(), info);
				printRewardLog(carnivalRule.getId(), role.getRoleId(), role.getRoleName());
			}
			
			List<RoleInstance> dbRole = GameContext.getRoleDAO().getRoleMountByScore(carnivalRule.getMinValue());
			if(!Util.isEmpty(dbRole)) {
				for(RoleInstance role : dbRole) {
					if(rewardMap.containsKey(role.getRoleId())) {
						continue;
					}
					info = new CarnivalRankInfo();
					info.setCareer(role.getCareer());
					info.setTargetId(role.getRoleId());
					info.setCampId(role.getCampId());
					info.setRank(index);
					rewardMap.put(role.getRoleId(), info);
					printRewardLog(carnivalRule.getId(), role.getRoleId(), role.getRoleName());
				}
			}
		}catch(Exception e){
			logger.error("RoleMountLogic.getCarnivalReward error: ", e);
		}
		return rewardMap.values();
		*/
	}
}
