package sacred.alliance.magic.app.carnival.logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.carnival.CarnivalRule;
import sacred.alliance.magic.app.carnival.CarnivalType;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.CarnivalRankInfo;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class RoleAttrLogic extends CarnivalLogic {
	@Override
	public Collection<CarnivalRankInfo> getCarnivalReward(CarnivalRule carnivalRule, int itemId) {
		Map<String, CarnivalRankInfo> rewardMap = new HashMap<String, CarnivalRankInfo>();
		try{
			CarnivalType carnivalType = carnivalRule.getCarnivalType();
			AttributeType attriType = carnivalType.getAttriType();
			CarnivalRankInfo info = null;
			byte index = ALL_REWARD_RANK;
			for(RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()) {
				if(!isMeet(carnivalRule, getAttri(role, attriType))) {
					continue;
				}
				info = new CarnivalRankInfo();
				info.setCareer(role.getCareer());
				info.setTargetId(role.getRoleId());
				info.setRank(index);
				rewardMap.put(role.getRoleId(), info);
				printRewardLog(carnivalRule.getId(), role.getRoleId(), role.getRoleName());
			}
			
			List<RoleInstance> dbRole = GameContext.getRoleDAO().getRoleByColumn(carnivalRule.getCarnivalType().getColumnName(), carnivalRule.getMinValue());
			if(!Util.isEmpty(dbRole)) {
				for(RoleInstance role : dbRole) {
					if(rewardMap.containsKey(role.getRoleId())) {
						continue;
					}
					info = new CarnivalRankInfo();
					info.setCareer(role.getCareer());
					info.setTargetId(role.getRoleId());
					info.setRank(index);
					rewardMap.put(role.getRoleId(), info);
					printRewardLog(carnivalRule.getId(), role.getRoleId(), role.getRoleName());
				}
			}
		}catch(Exception e){
			logger.error("RoleAttrLogic.getCarnivalReward error: ", e);
		}
		return rewardMap.values();
	}
	
}
