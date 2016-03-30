package sacred.alliance.magic.app.carnival.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.carnival.CarnivalRule;
import sacred.alliance.magic.app.carnival.CarnivalType;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.CarnivalRankInfo;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class RoleCareerAttrLogic extends RoleAttrLogic {
	private static RoleCareerAttrLogic instance = new RoleCareerAttrLogic();
	public static RoleCareerAttrLogic getInstance(){
		return instance ;
	}
	@Override
	public List<CarnivalRankInfo> getCarnivalRank(CarnivalRule carnivalRule, int itemId) {
		return sortRole(GameContext.getOnlineCenter().getAllOnlineRole(), carnivalRule.getCarnivalType());
	}
	
	private List<CarnivalRankInfo> sortRole(Collection<RoleInstance> roleList, CarnivalType carnivalType){
		List<CarnivalRankInfo> rankList = new ArrayList<CarnivalRankInfo>();
		/*try{
			Map<Byte, RoleInstance> onlineRankMap = sortOnlineRole(roleList, carnivalType.getAttriType(), carnivalType.getSubAttriType());
			List<RoleInstance> dbRole = GameContext.getRoleDAO().getCareerRoleSortByColumn(carnivalType.getColumnName(), carnivalType.getSubColumnName());
			Map<Byte, RoleInstance> dbRankMap = new HashMap<Byte, RoleInstance>();
			if(!Util.isEmpty(dbRole)) {
				for(RoleInstance role : dbRole) {
					dbRankMap.put(role.getCareer(), role); 
				}
			}
			List<RoleInstance> sortList = sortList(onlineRankMap, dbRankMap, carnivalType.getAttriType(), carnivalType.getSubAttriType());
			CarnivalRankInfo info = null;
			byte index = 1;
			for(RoleInstance role : sortList){
				info = new CarnivalRankInfo();
				info.setCampId(role.getCampId());
				info.setCareer(role.getCareer());
				info.setTargetId(role.getRoleId());
				info.setName(role.getRoleName());
				info.setTargetValue(getAttri(role, carnivalType.getAttriType()));
				info.setRank(index++);
				rankList.add(info);
			}
		}catch(Exception e){
			logger.error("RoleLevelLogic.sortRole error: ", e);
		}*/
		return rankList;
	}
	
	private Map<Byte, RoleInstance> sortOnlineRole(Collection<RoleInstance> roleList, AttributeType attriType, AttributeType subAttriType){
		Map<Byte, RoleInstance> map = new HashMap<Byte, RoleInstance>();
		if(Util.isEmpty(roleList)) {
			return map;
		}
		for(RoleInstance info : roleList) {
			RoleInstance role = map.get(info.getCareer());
			if(null == role) {
				map.put(info.getCareer(), info);
				continue;
			}
			int infoAttriValue = getAttri(info, attriType);
			int roleArrriValue = getAttri(role, attriType);
			
			if(infoAttriValue > roleArrriValue) {
				map.put(info.getCareer(), info);
				continue;
			}
			if(infoAttriValue == roleArrriValue && getAttri(info, subAttriType) > getAttri(role, subAttriType)) {
				map.put(info.getCareer(), info);
			}
		}
		return map;
	}
	
	private List<RoleInstance> sortList(Map<Byte, RoleInstance> onlineMap, Map<Byte, RoleInstance> dbMap, AttributeType attriType, AttributeType subAttriType){
		List<RoleInstance> sortList = new ArrayList<RoleInstance>();
		/*for(CareerType careerType : CareerType.values()) {
			if(careerType.getType()<0){
				continue ;
			}
			RoleInstance onlineRole = onlineMap.get(careerType.getType());
			RoleInstance dbRole = dbMap.get(careerType.getType());
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
			
			if(onlineRole.getRoleId().equals(dbRole.getRoleId())) {
				sortList.add(onlineRole);
				continue;
			}
			
			int onlineRoleAttrValue = getAttri(onlineRole, attriType);
			
			if(onlineRoleAttrValue > dbRole.get(attriType)) {
				sortList.add(onlineRole);
				continue;
			}
			if(onlineRoleAttrValue == dbRole.get(attriType) && getAttri(onlineRole, subAttriType) > dbRole.get(subAttriType)) {
				sortList.add(onlineRole);
				continue;
			}
			sortList.add(dbRole);
		}*/
		return sortList;
	}
}
