package com.game.draco.app.quest.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.exception.OutOfGoodsBagException;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTaskprops;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestAcceptParam;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.base.QuestAcceptType;
import com.game.draco.app.quest.base.QuestOpCode;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0704_QuestAcceptReqMessage;

public class QuestAcceptAction extends BaseAction<C0704_QuestAcceptReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C0704_QuestAcceptReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		int type = reqMsg.getType();
		String acceptParam = reqMsg.getParam();
		int questId = reqMsg.getQuestId();
		try {
			Quest quest = GameContext.getQuestApp().getQuest(questId);
			//不是从NPC处接的任务，不能自己接取。七星、轮回任务会自动触发，随机任务会单独处理
			QuestAcceptType acceptType = quest.getQuestAcceptType();
			if(QuestAcceptType.Npc != acceptType){
				return null;
			}
			QuestAcceptParam qa = QuestHelper.questAcceptParamParser(acceptParam);
			if(qa.getAcceptModel() == QuestAcceptParam.Model.Goods){
				//判断参数
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(qa.getTiggerGoodsId());
				if(null == gb || !(gb instanceof GoodsTaskprops) || 
						((GoodsTaskprops)gb).getTaskId() != questId){
					return new C0003_TipNotifyMessage(Status.Sys_Error.getTips());
				}
			}
			QuestOpCode value = GameContext.getUserQuestApp().acceptQuest(role, questId);
			//接任务失败，发提示信息
			if(QuestOpCode.success != value){
				return new C0003_TipNotifyMessage(value.getInfo());
			}
			//接任务成功之后，将NPC身上可接的任务全部接取
			//GameContext.getUserQuestApp().acceptNpcAllQuest(role, quest.getAcceptNpcId());//TODO:只有主线任务链2014.4.30
			//接任务之后需要寻路
			if(type == 1){
				if(qa.getAcceptModel() == QuestAcceptParam.Model.Goods) {
					//删除触发的道具
					GameContext.getUserGoodsApp().deleteForBagByGoodsId(role, qa.getTiggerGoodsId(), OutputConsumeType.quest_accept_consume);
				}
			}
			//返回消息在接任务逻辑里构建发送
			return null;
		} catch(OutOfGoodsBagException ex){
			return new C0003_TipNotifyMessage(Status.Quest_Backpack_Full.getTips());
		}catch (ServiceException e) {
			logger.error(this.getClass().getName() + ".execute error: ", e);
			return new C0003_TipNotifyMessage(this.getText(TextId.SYSTEM_ERROR));
		}
	}
	
}
