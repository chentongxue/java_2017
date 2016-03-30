package com.game.draco.app.npc.action;

import java.util.Collection;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstanceEvent;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.copy.config.CopyConfig;
import com.game.draco.app.copy.vo.CopyType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.push.C0005_TipMultiNotifyMessage;
import com.game.draco.message.request.C3840_ChoiceRefRuleReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class NpcChoiceRefRuleAction extends BaseAction<C3840_ChoiceRefRuleReqMessage>{
		
	@Override
	public Message execute(ActionContext context, C3840_ChoiceRefRuleReqMessage req) {
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			// 获取副本的所有者
			AbstractRole leader = role;
			short copyId = GameContext.getCopyLogicApp().getMapConfig(role.getMapId()).getCopyId();
			CopyConfig copyConfig = GameContext.getCopyLogicApp().getCopyConfig(copyId);
			if (null == copyConfig || CopyType.team == copyConfig.getCopyType()) {
				leader = role.getTeam().getLeader();
			}
			if (leader != null && !role.getRoleId().equals(leader.getRoleId())) {
				// 没有权限
				C0002_ErrorRespMessage erm = new C0002_ErrorRespMessage();
				erm.setReqCmdId(req.getCommandId());
				erm.setInfo(this.getText(TextId.NPC_CHOICE_NOT_LEADER));
				return erm;
			}
	
			// 刷怪规则id,npcId,是否删除npc,难度文本
			String [] param = req.getParam().split(",");
			if(null == param || param.length < 4){
				return null ;
			}
			
			String ruleId = param[0];
			if(Util.isEmpty(ruleId)){
				return null;
			}
			String npcId = param[1];
			byte flag = Byte.parseByte(param[2]);
			String info = param[3];
			
			if (1 == flag) {
				Collection<NpcInstance> npcList = role.getMapInstance()
						.getNpcList();
				for (NpcInstance npc : npcList) {
					if (npc.getNpcid().equals(npcId)) {
						role.getMapInstance().removeAbstractRole(npc);
						role.getMapInstance().notifyNpcDeath(npc);
					}
				}
			}
			MapInstanceEvent event = new MapInstanceEvent(MapInstanceEvent.EventType.refReshRule,ruleId);
			role.getMapInstance().doEvent(role,event);
			
			if(Util.isEmpty(info)){
				return null;
			}
			C0005_TipMultiNotifyMessage respMsg = new C0005_TipMultiNotifyMessage();
			respMsg.setMsgContext(GameContext.getI18n().messageFormat(TextId.NPC_CHOICE_SUCCESS,info));
			role.getMapInstance().broadcastScreenMap(null, respMsg);
			return null;
		}catch(Exception e){
			logger.error("",e);
			C0002_ErrorRespMessage erm = new C0002_ErrorRespMessage();
			erm.setReqCmdId(req.getCommandId());
			erm.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return erm;
		}
	}

}
