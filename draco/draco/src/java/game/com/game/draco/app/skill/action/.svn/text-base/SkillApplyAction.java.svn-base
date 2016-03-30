package com.game.draco.app.skill.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.skill.config.SkillAttackType;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.push.C0610_ActorDeathNotifyMessage;
import com.game.draco.message.request.C0300_SkillApplyReqMessage;
import com.game.draco.message.response.C0300_SkillApplyRespMessage;

public class SkillApplyAction extends BaseAction<C0300_SkillApplyReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C0300_SkillApplyReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C0300_SkillApplyRespMessage resp = new C0300_SkillApplyRespMessage();
		resp.setType((byte) RespTypeStatus.FAILURE);
		try {
			//判断玩家状态
			if(role.inState(StateType.charm) || role.inState(StateType.mum)
					|| role.inState(StateType.noUseSkill)) {
				resp.setType((byte)SkillApplyResult.BAD_STATE.getType());
				return resp;
			}
			// 角色使用 或者 ai调用
			short skillId = req.getSkillId();
			Skill skill = GameContext.getSkillApp().getSkill(skillId);
			if (null == skill) {
				resp.setType((byte) SkillApplyResult.HAS_NOT_SKILL.getType());
				return resp;
			}
			int delayTime = req.getDelayTime();
			//TODO:判断 delayTime
			if(role.getJumpMap().get()){
				return resp;
			}
			if(skill.getSkillAttackType() != SkillAttackType.NormalAttack){
				if(!role.getBehavior().canUseSkill()){
					resp.setType((byte) RespTypeStatus.FAILURE);
					return resp;
				}
			}else{
				if(!role.getBehavior().canUseCommonSkill()){
					resp.setType((byte) RespTypeStatus.FAILURE);
					return resp;
				}
			}
		
			MapInstance mapInstance = role.getMapInstance();
			if(null != mapInstance && 
					!mapInstance.canUseSkill(role, skillId)){
				//判断当前地图是否允许使用此技能
				resp.setType((byte) SkillApplyResult.CURRENT_MAP_CANOT_USE.getType());
				return resp;
			}
			
			int targetIntId = req.getTargetRoleId();
			String targetRoleId = String.valueOf(targetIntId);
			AbstractRole targetRole = null;
			if (!Util.isEmpty(targetRoleId) && null != mapInstance) {
				targetRole = mapInstance.getAbstractRole(targetRoleId);
				role.setTarget(targetRole);
			}
			try {
				if (null == targetRole || targetRole.isDeath()) {
					// 有可能客户端没有收到npc/角色死亡消息,出现一直打空血怪,怪物却不消失情况
					// 此情况下给此用户再次push npc/角色死亡消息
					C0610_ActorDeathNotifyMessage npcDeathMsg = new C0610_ActorDeathNotifyMessage();
					npcDeathMsg.setInstanceId(targetIntId);
					role.getBehavior().sendMessage(npcDeathMsg);
				}
			}catch(Exception ex){
				logger.error("",ex);
			}
			
			role.setMapX(req.getMapX());
			role.setMapY(req.getMapY());
			role.setDir(Direction.getDir(req.getMapDir()));
			SkillApplyResult value = GameContext.getUserSkillApp().useSkill(role, skillId,delayTime, true);
			if(value != SkillApplyResult.SUCCESS){
				resp.setSkillId(req.getSkillId());
				resp.setType((byte) value.getType());
				return resp;
			}
			//调用女神使用技能
			GameContext.getGoddessApp().roleGoddessUseSkill(role, targetRole);
			return null;
		} catch (Exception e) {
			logger.error("", e);
			resp.setType((byte) RespTypeStatus.FAILURE);
		}
		return resp;
	}

	
}
