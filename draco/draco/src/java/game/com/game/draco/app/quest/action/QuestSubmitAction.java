package com.game.draco.app.quest.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.exception.GoodsIsOnlyException;
import sacred.alliance.magic.app.goods.exception.OutOfGoodsBagException;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.base.QuestAcceptType;
import com.game.draco.app.quest.base.QuestOpCode;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0705_QuestSubmitReqMessage;

public class QuestSubmitAction extends BaseAction<C0705_QuestSubmitReqMessage>{

	@Override
	public Message execute(ActionContext context, C0705_QuestSubmitReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		int questId = reqMsg.getQuestId();
		try {
			Quest quest = GameContext.getQuestApp().getQuest(questId);
			//随机任务会单独处理
			if(QuestAcceptType.Poker == quest.getQuestAcceptType()){
				return null;
			}
			//正常交任务逻辑
			QuestOpCode value = GameContext.getUserQuestApp().submitQuest(role, questId);
			//交任务失败，弹出提示信息
			if(QuestOpCode.success != value){
				return new C0003_TipNotifyMessage(value.getInfo());
			}
			//交任务成功，弹出下一个可交或可接的任务
			GameContext.getUserQuestApp().pushQuestViewMessage(role, quest.getSubmitNpcId());
			return null;
		}catch (GoodsIsOnlyException e1) {
			return new C0003_TipNotifyMessage(Status.Quest_Own_Goods.getTips());
		} catch(OutOfGoodsBagException ex){
			return new C0003_TipNotifyMessage(Status.Quest_Backpack_Full.getTips());
		}catch (Exception ex) {
			logger.error(this.getClass().getName() + ".execute error: ", ex);
			return new C0003_TipNotifyMessage(this.getText(TextId.SYSTEM_ERROR));
		}
	}
}
