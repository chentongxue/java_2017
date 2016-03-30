package sacred.alliance.magic.debug.action;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.base.QuestOpCode;
import com.game.draco.debug.message.request.C10004_QuestOrSkillOperReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

/*添加删除技能或任务*/
public class QuestOrSkillOperAction extends ActionSupport<C10004_QuestOrSkillOperReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10004_QuestOrSkillOperReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			byte type = reqMsg.getType();//1：技能 2：任务
			byte operType = reqMsg.getOperType();//1：增加 2：删除 3：清空
			String operId = reqMsg.getOperId().trim();
			String roleName = reqMsg.getRoleName();
			byte addTaskType = reqMsg.getAddTaskType();
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleName(roleName);
			if(null == role){
				resp.setInfo(GameContext.getI18n().getText(TextId.ROLE_OFFLINE_FAIL));
				return resp;
			}
			if(type == 1) {//技能
				if(operType == 1){//添加技能
					//TODO:
					//GameContext.getUserSkillApp().addSkill(role, Short.valueOf(operId), SkillLearnType.RoleStudy, reqMsg.getOperLevel());
				}else if(operType == 2){//删除技能
					//TODO:
					//GameContext.getUserSkillApp().deleteSkill(role, Short.valueOf(operId));
				}else if(operType == 3){//重置技能
					//GameContext.getUserSkillApplication().resetSkillTree(role);
				}
				resp.setType((byte)RespTypeStatus.SUCCESS);
				return resp;
			}
			//任务
			if(operType == 1){//添加任务
				if(addTaskType == 0){//添加任务
					QuestOpCode code = GameContext.getUserQuestApp().debugAcceptQuest(role, Integer.valueOf(operId));
					if(code.getCode() == 2 || code.getCode() == 3 || code.getCode() == 4 || code.getCode() == 5){
						resp.setInfo(code.getInfo());
						return resp;
					}
				}else if(addTaskType == 1){//添加并完成任务
					GameContext.getUserQuestApp().acceptAndCompleteQuest(role,Integer.valueOf(operId));
				}
			}else if(operType == 2){//删除任务
				GameContext.getUserQuestApp().giveUpQuest(role, Integer.valueOf(operId));
			}else if(operType == 3){//清楚角色所有任务（正在做的和曾经完成的）
				GameContext.getUserQuestApp().removeAllQuest(role);
			}
		
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		}catch(Exception e){
			logger.error("QuestOrSkillOperAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
		
	}
}
