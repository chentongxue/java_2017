package com.game.draco.app.quest.poker.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.app.quest.poker.config.QuestPokerAwardRatioConfig;
import com.game.draco.message.item.QuestPokerRatioItem;
import com.game.draco.message.request.C0716_QuestPokerDescReqMessage;
import com.game.draco.message.response.C0716_QuestPokerDescRespMessage;

public class QuestPokerDescAction extends BaseAction<C0716_QuestPokerDescReqMessage>{

	@Override
	public Message execute(ActionContext context, C0716_QuestPokerDescReqMessage reqMsg) {
		List<QuestPokerRatioItem> ratioList = new ArrayList<QuestPokerRatioItem>();
		for(QuestPokerAwardRatioConfig config :GameContext.getQuestPokerApp().getPokerAwardRatioList()){
			if(null == config){
				continue;
			}
			QuestPokerRatioItem item = new QuestPokerRatioItem();
			item.setType((byte) config.getType());
			item.setName(config.getName());
			item.setRatio(config.getRatio());
			/*item.setColor1(config.getColor1());
			item.setNumber1(config.getNumber1());
			item.setColor2(config.getColor2());
			item.setNumber2(config.getNumber2());
			item.setColor3(config.getColor3());
			item.setNumber3(config.getNumber3());*/
			ratioList.add(item);
		}
		//排序
		this.sortRatioList(ratioList);
		C0716_QuestPokerDescRespMessage resp = new C0716_QuestPokerDescRespMessage();
		resp.setDesc(GameContext.getQuestPokerApp().getDescribe());
		resp.setRatioList(ratioList);
		return resp;
	}
	
	private void sortRatioList(List<QuestPokerRatioItem> ratioList){
		Collections.sort(ratioList, new Comparator<QuestPokerRatioItem>(){
			@Override
			public int compare(QuestPokerRatioItem item0, QuestPokerRatioItem item1) {
				if(item0.getType() > item1.getType()){
					return -1;
				}
				if(item0.getType() < item1.getType()){
					return 1;
				}
				return 0;
			}
		});
	}
	
}
