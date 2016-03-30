package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.horse.domain.RoleHorseSkill;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.debug.message.item.RoleHorseItem;
import com.game.draco.debug.message.item.RoleHorseSkillItem;
import com.game.draco.debug.message.request.C10080_RoleHorseDetailReqMessage;
import com.game.draco.debug.message.response.C10080_RoleHorseDetailRespMessage;

public class RoleHorseDetailAction extends ActionSupport<C10080_RoleHorseDetailReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10080_RoleHorseDetailReqMessage req) {
		C10080_RoleHorseDetailRespMessage resp = new C10080_RoleHorseDetailRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		
		try {
			String roleId = req.getRoleId();
			if(roleId==null || roleId.trim().equals("")){
				String roleName = req.getRoleName();
				RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleName(roleName);
				roleId = role.getRoleId();
			}
			int roleIntId = Integer.parseInt(roleId);
			
			List<RoleHorse> horseList = getRoleHorseList(roleIntId);
			if(Util.isEmpty(horseList)){
				resp.setInfo(GameContext.getI18n().getText(TextId.GM_ROLE_HORSE_EMPTY));
				return resp;
			}
			resp.setHorseNum(horseList.size());
			List<RoleHorseItem> list = buildRoleHorseItemList(horseList);
			resp.setList(list);
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		} catch (Exception e) {
			this.logger.error("RoleHorseAction error: ", e);
			resp.setType((byte)RespTypeStatus.FAILURE);
			e.printStackTrace();
			return resp;
		}
	}

	private List<RoleHorseItem> buildRoleHorseItemList(List<RoleHorse> horseList) {
		List<RoleHorseItem> list = new ArrayList<RoleHorseItem>();
		for (RoleHorse roleHorse : horseList) {
			list.add(buildRoleHorseItem(roleHorse));
		}
		return list;
	}

	private RoleHorseItem buildRoleHorseItem(RoleHorse roleHorse) {
		//获取坐骑名
		HorseBase base = GameContext.getHorseApp().getHorseBaseById(roleHorse.getHorseId());
		
		RoleHorseItem item = new RoleHorseItem();
		item.setHorseId(roleHorse.getHorseId());
		item.setHorseName(base.getName());
		item.setQuality(roleHorse.getQuality());
		item.setStar(roleHorse.getStar());
		item.setBattleScore(roleHorse.getBattleScore());
		//new
		item.setStarNum(roleHorse.getStarNum());
		//11 06
		item.setOnBattle(roleHorse.getState());
		
		List<RoleHorseSkill> skillList = roleHorse.getSkillList();//需要修改
		List<RoleHorseSkillItem> roleHorseSkillList = buildRoleHorseSkillList(skillList);//非 null
		item .setRoleHorseSkillList(roleHorseSkillList);
		return item;
	}

	private List<RoleHorseSkillItem> buildRoleHorseSkillList(
			List<RoleHorseSkill> skillList) {
		List<RoleHorseSkillItem> list = new ArrayList<RoleHorseSkillItem>();
		for (RoleHorseSkill roleHorseSkill : skillList) {
			RoleHorseSkillItem item = buildRoleHorseSkillItem(roleHorseSkill);
			list.add(item);
		}
		return list;
	}
	private RoleHorseSkillItem buildRoleHorseSkillItem(
			RoleHorseSkill horseSkill) {
		Skill skill = GameContext.getSkillApp().getSkill(horseSkill.getSkillId());
		
		RoleHorseSkillItem item = new RoleHorseSkillItem();
		item.setSkillId(skill.getSkillId());
		item.setSkillName(skill.getName());
		item.setLevel(horseSkill.getLevel());
		item.setMaxLevel((short)skill.getMaxLevel());
		item.setFlag((byte)skill.getSkillApplyType().ordinal());
		//new 
		item.setLuck(horseSkill.getLuck());
		return item;
	}
	//如果玩家不在线，需要初始化玩家的坐骑技能
	private List<RoleHorse> getRoleHorseList(int roleIntId) {
		List<RoleHorse> horseList = null;
		Map<Integer,RoleHorse>  horseMap = GameContext.getRoleHorseApp().getAllRoleHorseByRoleId(roleIntId);
		if(!Util.isEmpty(horseMap)){
			horseList = new ArrayList<RoleHorse>(horseMap.values());
		}else{
			horseList = GameContext.getBaseDAO().selectList(
					RoleHorse.class, RoleHorse.ROLE_ID, roleIntId);
			for (RoleHorse roleHorse : horseList) {
				if(roleHorse == null){
					continue;
				}
				roleHorse.setSkillList(GameContext.getRoleHorseApp().getRoleHorseSkill(roleIntId, roleHorse.getHorseId()));
			}
		}
		return horseList;
	}
}
