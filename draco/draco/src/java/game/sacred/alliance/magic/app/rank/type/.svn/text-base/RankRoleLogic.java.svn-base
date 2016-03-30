package sacred.alliance.magic.app.rank.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.rank.RankInfo;
import sacred.alliance.magic.app.rank.RankLogCountDB;
import sacred.alliance.magic.domain.RankDbInfo;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class RankRoleLogic extends RankLogic<RoleInstance> {

	
	/**
	 * 返回角色基本信息
	 * @return roleId#roleName#gender#career#level#camp#factionId
	 */
	public String getRoleBaseInfo(RoleInstance role){
		StringBuffer sb = new StringBuffer();
		sb.append(role.getRoleId());
		sb.append(CAT);
		sb.append(role.getRoleName());
		sb.append(CAT);
		sb.append(role.getSex());
		sb.append(CAT);
		sb.append(role.getCareer());
		sb.append(CAT);
		sb.append(role.getLevel());
		sb.append(CAT);
		sb.append(role.getCampId());
		sb.append(CAT);
		sb.append(role.getUnionId());
		return sb.toString();
	}
	
	
	/**
	 * 默认用db数据初始化log
	 * 活动排行榜初始化读取RankDbInfo
	 */
	@Override
	public void initLogData(RankInfo rankInfo){
		List<RankDbInfo> rankDbInfoList = GameContext.getRankDAO().selectAllRankDbInfo("rankId", 
				String.valueOf(rankInfo.getId()));
		if(Util.isEmpty(rankDbInfoList)){
			return ;
		}
		
		Map<String, RoleInstance> roleMap = new HashMap<String, RoleInstance>();
		for(RankDbInfo rankDbInfo : rankDbInfoList){
			if(null == rankDbInfo){
				continue ;
			}
			String roleId = rankDbInfo.getRoleId();
			RoleInstance role = roleMap.get(roleId);
			if(null == role){
				role = GameContext.getRankDAO().selectRole("roleId", roleId);
				if(null == role){
					continue ;
				}
				roleMap.put(roleId, role);
			}
			role.getRankDbInfo().put(rankDbInfo.getRankId(), rankDbInfo);
		}
		
		for(Entry<String, RoleInstance> entry : roleMap.entrySet()){
			RoleInstance role = entry.getValue();
			if(null == role){
				continue ;
			}
			printLog(role, rankInfo);
		}
	}
	
	
	/**
	 * 统一处理数据在RoleCount中的log
	 */
	protected void initRoleCountLog(List<RoleCount> roleCountList,RankInfo rankInfo){
		if(Util.isEmpty(roleCountList)){
			return ;
		}
		for(RoleCount roleCount : roleCountList){
			if(null == roleCount){
				continue ;
			}
			RoleInstance role = GameContext.getRankDAO().selectRole("roleId", roleCount.getRoleId());
			if(null == role){
				continue ;
			}
			role.setRoleCount(roleCount);
			printLog(role, rankInfo);
		}
	}
	
	
	protected RoleCount getRoleCount(RankLogCountDB rankLogCountDB){
		RoleCount roleCount = new RoleCount();
		BeanUtils.copyProperties(rankLogCountDB,roleCount);
		return roleCount ;
		/*roleCount.setRoleId(rankLogCountDB.getRoleId());
		roleCount.setTowerMaxOrder(rankLogCountDB.getTowerMaxOrder());
		roleCount.setTowerMaxScore(rankLogCountDB.getTowerMaxScore());
		roleCount.setCompassTotal(rankLogCountDB.getCompassTotal());
		roleCount.setCompassSky(rankLogCountDB.getCompassSky());
		roleCount.setCompassLand(rankLogCountDB.getCompassLand());
		roleCount.setCompassGod(rankLogCountDB.getCompassGod());
		roleCount.setTreasureMapTotal(rankLogCountDB.getTreasureMapTotal());
		roleCount.setTreasureMapGreen(rankLogCountDB.getTreasureMapGreen());
		roleCount.setTreasureMapBlue(rankLogCountDB.getTreasureMapBlue());
		roleCount.setTreasureMapPurple(rankLogCountDB.getTreasureMapPurple());
		roleCount.setTreasureMapGolden(rankLogCountDB.getTreasureMapGolden());
		roleCount.setTreasureMapOrange(rankLogCountDB.getTreasureMapOrange());
		roleCount.setSamsaraTotal(rankLogCountDB.getSamsaraTotal());
		roleCount.setSamsaraEmperor(rankLogCountDB.getSamsaraEmperor());
		roleCount.setSamsaraMystic(rankLogCountDB.getSamsaraMystic());
		roleCount.setSamsaraWizard(rankLogCountDB.getSamsaraWizard());
		return roleCount;*/
	}
	
	@Override
	public void frozenRoleOffRankLog(RoleInstance t, RankInfo rankInfo) {
		rankInfo.getLogger().info(LOG_OFFRANK_FLAG + t.getRoleId());
	}
}
