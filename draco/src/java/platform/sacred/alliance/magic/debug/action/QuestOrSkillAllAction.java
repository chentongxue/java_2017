package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.debug.message.item.QuestOrSkillItem;
import com.game.draco.debug.message.request.C10003_QuestOrSkillAllReqMessage;
import com.game.draco.debug.message.response.C10003_QuestOrSkillAllRespMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

/*查询全部任务或技能*/
public class QuestOrSkillAllAction extends ActionSupport<C10003_QuestOrSkillAllReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10003_QuestOrSkillAllReqMessage reqMsg) {
		C10003_QuestOrSkillAllRespMessage resp = new C10003_QuestOrSkillAllRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			byte type = reqMsg.getType();
			if(type == 1) {//查询技能
				Collection<Skill> allSkill = GameContext.getSkillApp().getAllSkill();
				if(allSkill==null || allSkill.size()==0){
					resp.setInfo(GameContext.getI18n().getText(TextId.NO_SKILL));
					return resp;
				}else{
					List<QuestOrSkillItem> items = new ArrayList<QuestOrSkillItem>();
					for(Skill skill : allSkill){
						QuestOrSkillItem item = new QuestOrSkillItem();
						item.setParamId(""+skill.getSkillId());
						item.setParamName(skill.getName());
						item.setDesc("");
						items.add(item);
					}
					resp.setItems(items);
					resp.setType((byte)RespTypeStatus.SUCCESS);
					return resp;
				}
			}else{//查询任务
				Collection<Quest> allQuest = GameContext.getQuestApp().getAllQuest();
				if(allQuest==null || allQuest.size()==0){
					resp.setInfo(GameContext.getI18n().getText(TextId.NO_TASK));
					return resp;
				}else{
					List<QuestOrSkillItem> items = new ArrayList<QuestOrSkillItem>();
					for(Quest quest : allQuest){
						QuestOrSkillItem item = new QuestOrSkillItem();
						item.setParamId(""+quest.getQuestId());
						item.setParamName(quest.getQuestName());
//						item.setDesc(quest.getQuestDesc());
						items.add(item);
					}
					resp.setItems(items);
					resp.setType((byte)RespTypeStatus.SUCCESS);
					return resp;
				}
			}			
		}catch(Exception e){
			logger.error("QuestOrSkillAllAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	
	}
}
