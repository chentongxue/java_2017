package com.game.draco.app.rank.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankDbInfo;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.domain.RankLogCountDB;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class RankRoleLogic extends RankLogic<RoleInstance> {

	
	/**
	 * 返回角色基本信息
	 * @return roleId#roleName#gender#level#campId#uionId#unionName
	 * @throws Exception 
	 */
	public String getRoleBaseInfo(RoleInstance role){
		String unionName = getUnionName(role.getUnionId());
		
		StringBuffer sb = new StringBuffer();
		sb.append(role.getRoleId());
		sb.append(CAT);
		sb.append(role.getRoleName());
		sb.append(CAT);
		sb.append(role.getSex());
		sb.append(CAT);
		sb.append(role.getLevel());
		sb.append(CAT);
		//去掉阵营 12/10/2014
//		sb.append(role.getCampId());
//		sb.append(CAT);
		sb.append(role.getUnionId());
		sb.append(CAT);
		sb.append(unionName);
		return sb.toString();
	}
	/**
	 * 1403169937034#Thu Jun 19 17:25:37 CST 2014
	 * @param role
	 * @return
	 * @date 2014-6-19 下午05:30:00
	 */
	public String getRoleCreatTimeStr(RoleInstance role){
		StringBuffer sb = new StringBuffer();
		sb.append(role.getCreateTime().getTime());
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
			printLog4init(role, rankInfo);
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
			role.setRoleCount(roleCount);//
			printLog4init(role, rankInfo);
		}
	}
	
	
	protected RoleCount getRoleCount(RankLogCountDB rankLogCountDB){
		RoleCount roleCount = new RoleCount();
		BeanUtils.copyProperties(rankLogCountDB,roleCount);
		return roleCount ;
	}
	
}
